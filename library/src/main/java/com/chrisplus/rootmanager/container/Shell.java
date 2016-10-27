package com.chrisplus.rootmanager.container;

import com.chrisplus.rootmanager.exception.PermissionException;
import com.chrisplus.rootmanager.utils.RootUtils;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Shell {

    private final static String TAG = Shell.class.getSimpleName();
    private static final String token = UUID.randomUUID().toString();
    private static int shellTimeout = 10000;

    private static String error = "";
    private static Shell rootShell = null;
    private static Shell customShell = null;

    private final Process proc;
    private final BufferedReader inputStream;
    private final BufferedReader errorStream;
    private final OutputStreamWriter outputStream;

    private final List<Command> commands = new ArrayList<>();
    private boolean close = false;

    private int writeIndex;
    private int readIndex;

    private Runnable input = new Runnable() {
        public void run() {
            try {
                writeCommands();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                writeIndex = 0;
                closeWriter(outputStream);
            }
        }
    };

    private Runnable output = new Runnable() {
        public void run() {
            try {
                readOutput();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readIndex = 0;
                closeReader(inputStream);
                closeReader(errorStream);
                closeWriter(outputStream);
            }
        }
    };

    private Shell(String cmd) throws IOException, TimeoutException, PermissionException {

        RootUtils.Log(TAG, "Starting shell: " + cmd);

        proc = Runtime.getRuntime().exec(cmd);

        inputStream = new BufferedReader(new InputStreamReader(proc.getInputStream(), "UTF-8"));
        errorStream = new BufferedReader(new InputStreamReader(proc.getErrorStream(), "UTF-8"));
        outputStream = new OutputStreamWriter(this.proc.getOutputStream(), "UTF-8");

        Worker worker = new Worker(this);
        worker.start();

        try {
            worker.join(shellTimeout);
            if (worker.exit == -911) {

                proc.destroy();
                closeReader(inputStream);
                closeReader(inputStream);
                closeWriter(outputStream);
                throw new TimeoutException(error);

            }
            if (worker.exit == -42) {

                proc.destroy();
                closeReader(inputStream);
                closeReader(errorStream);
                closeWriter(outputStream);
                throw new PermissionException("Root Access Denied");

            } else {
                Thread shellInput = new Thread(input, "RootManager Input");
                shellInput.setPriority(Thread.NORM_PRIORITY);
                shellInput.start();

                Thread shellOutput = new Thread(output, "RootManager Output");
                shellOutput.setPriority(Thread.NORM_PRIORITY);
                shellOutput.start();
            }
        } catch (InterruptedException ex) {
            worker.interrupt();
            Thread.currentThread().interrupt();
            throw new TimeoutException();
        }
    }


    public static Shell startRootShell() throws IOException, TimeoutException, PermissionException {
        return Shell.startRootShell(shellTimeout);
    }

    public static Shell startRootShell(int timeout) throws IOException, TimeoutException,
            PermissionException {
        Shell.shellTimeout = timeout;

        if (rootShell == null) {
            RootUtils.Log(TAG, "Starting root shell!");
            String cmd = "su";

            int retries = 0;
            while (rootShell == null) {
                try {
                    rootShell = new Shell(cmd);
                } catch (IOException e) {
                    if (retries++ >= 5) {
                        RootUtils.Log(TAG, "Could not start shell");
                        throw e;
                    }
                } catch (PermissionException e) {
                    if (retries++ >= 5) {
                        RootUtils.Log(TAG, "Could not start shell, permission denied");
                        throw e;
                    }

                } catch (TimeoutException e) {
                    if (retries++ >= 5) {
                        RootUtils.Log(TAG, "Could not start shell, timeout");
                        throw e;
                    }
                }
            }
        } else {
            RootUtils.Log(TAG, "Using existing root shell!");
        }

        return rootShell;
    }

    public static Shell startCustomShell(String shellPath) throws IOException, TimeoutException,
            PermissionException {
        return Shell.startCustomShell(shellPath, shellTimeout);
    }

    public static Shell startCustomShell(String shellPath, int timeout) throws IOException,
            TimeoutException, PermissionException {
        Shell.shellTimeout = timeout;

        if (customShell == null) {
            RootUtils.Log(TAG, "Starting custom shell");
            customShell = new Shell(shellPath);
        } else {
            RootUtils.Log(TAG, "Using existing custom shell");
        }

        return customShell;
    }


    public static void closeCustomShell() throws IOException {
        if (customShell == null) {
            return;
        }
        customShell.close();
    }

    public static void closeRootShell() throws IOException {
        if (rootShell == null) {
            return;
        }
        rootShell.close();
    }

    public static void closeAll() throws IOException {
        closeRootShell();
        closeCustomShell();
    }

    public static boolean isCustomShellOpen() {
        if (customShell == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isRootShellOpen() {
        if (rootShell == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isAnyShellOpen() {
        if (rootShell != null) {
            return true;
        } else if (customShell != null) {
            return true;
        } else {
            return false;
        }
    }

    private void closeWriter(final Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeReader(final Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeCommands() throws IOException, InterruptedException {

        while (true) {
            synchronized (commands) {
                while (!close && writeIndex >= commands.size()) {
                    commands.wait();
                }
            }
            if (writeIndex < commands.size()) {
                Command next = commands.get(writeIndex);
                outputStream.write(next.getCommand());

                String line = "\necho " + token + " " + writeIndex + " $?\n";
                outputStream.write(line);
                outputStream.flush();
                writeIndex++;

            } else if (close) {

                outputStream.write("\nexit 0\n");
                outputStream.flush();
                outputStream.close();
                RootUtils.Log(TAG, "closing shell");
                return;

            }
        }

    }

    private void readOutput() throws IOException, InterruptedException {
        Command command = null;

        while (!close || inputStream.ready() || readIndex < commands.size()) {

            String line = inputStream.readLine();
            if (line == null) {
                break;
            }

            if (command == null) {
                if (readIndex >= commands.size()) {
                    if (close) {
                        break;
                    }
                    continue;
                }
                command = commands.get(readIndex);
            }

            int pos = line.indexOf(token);

            if (pos == -1) {
                command.onUpdate(command.getID(), line);
            } else if (pos > 0) {
                command.onUpdate(command.getID(), line.substring(0, pos));
            }

            if (pos >= 0) {
                line = line.substring(pos);
                String fields[] = line.split(" ");
                if (fields.length >= 2 && fields[1] != null) {
                    int id = 0;
                    try {
                        id = Integer.parseInt(fields[1]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    int exitCode = -1;

                    try {
                        exitCode = Integer.parseInt(fields[2]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (id == readIndex) {
                        readError(command);
                        command.setExitCode(exitCode);
                        readIndex++;
                        command = null;
                        continue;
                    }
                }
            }
        }

        RootUtils.Log(TAG, "Read all output");
        proc.waitFor();
        proc.destroy();
        RootUtils.Log(TAG, "Shell destroyed");

        while (readIndex < commands.size()) {
            if (command == null) {
                command = commands.get(readIndex);
            }
            command.terminate("Unexpected Termination.");
            command = null;
            readIndex++;
        }
    }

    private void readError(Command command) {
        try {
            while (errorStream.ready() && command != null) {
                String line = errorStream.readLine();
                if (line == null) {
                    break;
                }
                command.onUpdate(command.getID(), line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Command add(Command command) throws IOException {
        if (close) {
            throw new IllegalStateException("Unable to add commands to a closed shell");
        }
        synchronized (commands) {
            commands.add(command);
            commands.notifyAll();
        }

        return command;
    }

    public void close() throws IOException {
        if (this == rootShell) {
            rootShell = null;
        }
        if (this == customShell) {
            customShell = null;
        }
        synchronized (commands) {
            this.close = true;
            commands.notifyAll();
        }
    }

    private static class Worker extends Thread {

        private int exit = -911;
        private Shell shell;

        private Worker(Shell shell) {
            this.shell = shell;
        }

        public void run() {

            try {
                shell.outputStream.write("echo Started\n");
                shell.outputStream.flush();

                while (true) {
                    String line = shell.inputStream.readLine();
                    if (line == null) {
                        throw new EOFException();
                    } else if ("".equals(line)) {
                        continue;
                    } else if ("Started".equals(line)) {
                        this.exit = 1;
                        setShellOom();
                        break;
                    }
                    Shell.error = "unknown error";
                }
            } catch (IOException e) {
                exit = -42;
                if (e.getMessage() != null) {
                    Shell.error = e.getMessage();
                } else {
                    Shell.error = "RootAccess denied";
                }
            }

        }

        private void setShellOom() {
            try {
                Class<?> processClass = shell.proc.getClass();
                Field field;
                try {
                    field = processClass.getDeclaredField("pid");
                } catch (NoSuchFieldException e) {
                    field = processClass.getDeclaredField("id");
                }
                field.setAccessible(true);
                int pid = (Integer) field.get(shell.proc);
                shell.outputStream.write("(echo -17 > /proc/" + pid + "/oom_adj) &> /dev/null\n");
                shell.outputStream.write("(echo -17 > /proc/$$/oom_adj) &> /dev/null\n");
                shell.outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
