package com.chrisplus.rootmanager.container;

import com.chrisplus.rootmanager.Constants;
import com.chrisplus.rootmanager.utils.RootUtils;

import java.io.IOException;

public abstract class Command {

    private static final String TAG = Command.class.getSimpleName();

    private String[] commands;

    private boolean isFinished;

    private int exitCode;

    private long timeout;

    private int id;

    /* Abstract function should be implemented by caller */

    public Command(String... commands) {
        this(Constants.COMMAND_TIMEOUT, commands);
    }

    public Command(int timeout, String... commands) {
        this.id = RootUtils.generateCommandID();
        this.timeout = timeout;
        this.commands = commands;
    }

    public abstract void onUpdate(int id, String message);

    public abstract void onFinished(int id);

    public int getID() {
        return id;
    }

    public void setExitCode(int code) {
        synchronized (this) {
            exitCode = code;
            isFinished = true;
            onFinished(id);
            this.notifyAll();
        }
    }

    public void terminate() {
        RootUtils.Log(TAG, "Terminate the command upon users' requests");
        setExitCode(-358);
    }

    public void terminate(String reason) {
        try {
            RootUtils.Log(TAG, "Terminate the shell with reason " + reason);
            //TODO only terminate command, not close the shells
            Shell.closeRootShell();
            setExitCode(-1);
        } catch (IOException e) {
            e.printStackTrace();
            RootUtils.Log(TAG, "Terminate the shell and io exception happens");
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public int waitForFinish(long timeout) throws InterruptedException {
        synchronized (this) {
            while (!isFinished) {
                this.wait(timeout);
                if (!isFinished) {
                    isFinished = true;
                    RootUtils.Log(TAG, "Timeout Exception has occurred.");
                    terminate("Timeout Exception");
                }
            }
        }
        return exitCode;
    }

    public int keepAlive() throws InterruptedException {
        synchronized (this) {
            while (!isFinished) {
                /* test function */
                this.wait();
            }
        }

        return exitCode;
    }

    public int waitForFinish() throws InterruptedException {
        synchronized (this) {
            waitForFinish(timeout);
        }
        return exitCode;
    }

    public String getCommand() {
        if (commands == null || commands.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < commands.length; i++) {
            sb.append(commands[i]);
            sb.append('\n');
        }
        String command = sb.toString();
        RootUtils.Log(TAG, "Sending command(s): " + command);
        return command;
    }

}
