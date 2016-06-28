package com.chrisplus.rootmanagersample;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity {

    private ToggleButton grantButton;

    private Button installButton;

    private Button uninstallButton;

    private Button screenshotButton;

    private Button screenrecordButton;

    private Button runButton;

    private Button remountButton;

    private Button restartButton;

    private static boolean isRW = true;

    private static EditText commandText;

    private static EditText pathText;

    private static TextView logView;

    private static EditText apkText;

    private static EditText pnText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void init() {
        /* edit text */
        commandText = (EditText) findViewById(R.id.editCommand);
        pathText = (EditText) findViewById(R.id.editPath);
        apkText = (EditText) findViewById(R.id.editApkPath);
        pnText = (EditText) findViewById(R.id.editPackageName);

        /* grant button */
        grantButton = (ToggleButton) findViewById(R.id.btnRooted);
        if (RootManager.getInstance().hasRooted()) {
            grantButton.setEnabled(true);
        }

        grantButton.setOnCheckedChangeListener(grantButtonListener);

        /* Log view */
        logView = (TextView) findViewById(R.id.log);
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());

        /* screen cap */
        screenshotButton = (Button) findViewById(R.id.btnScreenShot);
        screenshotButton.setOnClickListener(screenCapListener);

        /* screen record */
        screenrecordButton = (Button) findViewById(R.id.btnScreenRecord);
        screenrecordButton.setOnClickListener(screenRecordListener);

        /* run button */
        runButton = (Button) findViewById(R.id.btnRunCommand);
        runButton.setOnClickListener(runCommandListener);

        /* remount button */
        remountButton = (Button) findViewById(R.id.btnRemount);
        remountButton.setOnClickListener(remountListener);

        /* install button */
        installButton = (Button) findViewById(R.id.bntInstallPackage);
        installButton.setOnClickListener(installListener);

        /* uninstall button */
        uninstallButton = (Button) findViewById(R.id.btnUninstallPackage);
        uninstallButton.setOnClickListener(uninstallListener);

        /* Restart Device Button */
        restartButton = (Button) findViewById(R.id.btnRestart);
        restartButton.setOnClickListener(restartListener);

    }

    private static final CompoundButton.OnCheckedChangeListener grantButtonListener
            = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final ToggleButton button = (ToggleButton) buttonView;

            if (isChecked) {
                if (!RootManager.getInstance().obtainPermission()) {
                    button.setChecked(false);
                }
            }
            updateLog("Check Has Rooted " + button.isChecked());
        }

    };

    private static final View.OnClickListener screenCapListener = new View.OnClickListener() {
        final String path = "/sdcard/testcap.png";

        @Override
        public void onClick(View v) {
            runAsyncTask(new AsyncTask<Boolean, Boolean, Boolean>() {

                @Override
                protected Boolean doInBackground(Boolean... params) {
                    return RootManager.getInstance().screenCap(path);
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if (result) {
                        updateLog("Screen shot saved at " + path);
                    } else {
                        updateLog("Screen shot failed");
                    }

                    super.onPostExecute(result);
                }

            });
        }

    };

    private static final OnClickListener screenRecordListener = new OnClickListener() {
        final String path = "/sdcard/testrecord.mp4";

        @Override
        public void onClick(View v) {
            runAsyncTask(new AsyncTask<Boolean, Boolean, Boolean>() {

                @Override
                protected void onPreExecute() {
                    updateLog("Recording 30s...");
                    super.onPreExecute();
                }

                @Override
                protected Boolean doInBackground(Boolean... params) {
                    return RootManager.getInstance().screenRecord(path);
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if (result) {
                        updateLog("Screen record saved at " + path);
                    } else {
                        updateLog("Screen record failed");
                    }

                    super.onPostExecute(result);
                }

            });
        }
    };

    private static final OnClickListener runCommandListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            final String command = commandText.getText().toString();
            runAsyncTask(new AsyncTask<Void, Void, Result>() {

                @Override
                protected Result doInBackground(Void... params) {
                    return RootManager.getInstance().runCommand(command);
                }

                @Override
                protected void onPostExecute(Result result) {
                    super.onPostExecute(result);
                    updateLog("Command " + command + " execute " + result.getResult()
                            + " with message " + result.getMessage());
                }

            });
        }

    };

    private static final OnClickListener remountListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            final String path = pathText.getText().toString();
            final String type = isRW ? "rw" : "ro";
            runAsyncTask(new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {
                    return RootManager.getInstance().remount(path, type);
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    updateLog("Remount " + path + " as " + type + " executed " + result);
                    isRW = !isRW;
                    super.onPostExecute(result);
                }
            });

        }

    };

    private static final OnClickListener installListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            final String apkPath = apkText.getText().toString();
            runAsyncTask(new AsyncTask<Void, Void, Result>() {

                @Override
                protected void onPreExecute() {
                    updateLog("Installing package " + apkPath + " ...........");
                    super.onPreExecute();
                }

                @Override
                protected Result doInBackground(Void... params) {
                    return RootManager.getInstance().installPackage(apkPath);
                }

                @Override
                protected void onPostExecute(Result result) {
                    updateLog("Install " + apkPath + " " + result.getResult()
                            + " with the message " + result.getMessage());
                    super.onPostExecute(result);
                }

            });
        }

    };

    private static final OnClickListener uninstallListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            final String pkgName = pnText.getText().toString();
            runAsyncTask(new AsyncTask<Void, Void, Result>() {

                @Override
                protected void onPreExecute() {
                    updateLog("Uninstalling package " + pkgName + " ...........");
                    super.onPreExecute();
                }

                @Override
                protected Result doInBackground(Void... params) {
                    return RootManager.getInstance().uninstallPackage(pkgName);
                }

                @Override
                protected void onPostExecute(Result result) {
                    updateLog("Uninstall " + pkgName + " " + result.getResult()
                            + " with the message " + result.getMessage());
                    super.onPostExecute(result);
                }

            });
        }

    };

    private static final OnClickListener restartListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            RootManager.getInstance().restartDevice();
        }

    };

    private static final <T> void runAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        asyncTask.execute(params);
    }

    private static void updateLog(String log) {
        logView.append("\n--> " + log);
    }
}
