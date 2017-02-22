package com.chrisplus.rootmanagersample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;
import com.chrisplus.rootmanager.utils.RootUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends ActionBarActivity {
    @BindView(R.id.btnRooted)
    public ToggleButton grantButton;

    @BindView(R.id.bntInstallPackage)
    public Button installButton;

    @BindView(R.id.btnUninstallPackage)
    public Button uninstallButton;

    @BindView(R.id.btnScreenShot)
    public Button screenshotButton;

    @BindView(R.id.btnScreenRecord)
    public Button screenrecordButton;

    @BindView(R.id.btnRunCommand)
    public Button runButton;

    @BindView(R.id.btnRemount)
    public Button remountButton;

    @BindView(R.id.btnRestart)
    public Button restartButton;

    private static boolean isRW = true;

    @BindView(R.id.editCommand)
    public EditText commandText;

    @BindView(R.id.editPath)
    public EditText pathText;

    @BindView(R.id.log)
    public TextView logView;

    @BindView(R.id.editApkPath)
    public EditText apkText;

    @BindView(R.id.editPackageName)
    public EditText pnText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (RootManager.getInstance().hasRooted()) {
            grantButton.setEnabled(true);
        } else {
            grantButton.setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


//    private void init() {
//        /* edit text */
//        commandText = (EditText) findViewById(R.id.editCommand);
//        pathText = (EditText) findViewById(R.id.editPath);
//        apkText = (EditText) findViewById(R.id.editApkPath);
//        pnText = (EditText) findViewById(R.id.editPackageName);
//
//        /* grant button */
//        grantButton = (ToggleButton) findViewById(R.id.btnRooted);
//        if (RootManager.getInstance().hasRooted()) {
//            grantButton.setEnabled(true);
//        }
//
//        grantButton.setOnCheckedChangeListener(grantButtonListener);
//
//        /* Log view */
//        logView = (TextView) findViewById(R.id.log);
//        logView.setMovementMethod(ScrollingMovementMethod.getInstance());
//
//        /* screen cap */
//        screenshotButton = (Button) findViewById(R.id.btnScreenShot);
//        screenshotButton.setOnClickListener(screenCapListener);
//
//        /* screen record */
//        screenrecordButton = (Button) findViewById(R.id.btnScreenRecord);
//        screenrecordButton.setOnClickListener(screenRecordListener);
//
//        /* run button */
//        runButton = (Button) findViewById(R.id.btnRunCommand);
//        runButton.setOnClickListener(runCommandListener);
//
//        /* remount button */
//        remountButton = (Button) findViewById(R.id.btnRemount);
//        remountButton.setOnClickListener(remountListener);
//
//        /* install button */
//        installButton = (Button) findViewById(R.id.bntInstallPackage);
//        installButton.setOnClickListener(installListener);
//
//        /* uninstall button */
//        uninstallButton = (Button) findViewById(R.id.btnUninstallPackage);
//        uninstallButton.setOnClickListener(uninstallListener);
//
//        /* Restart Device Button */
//        restartButton = (Button) findViewById(R.id.btnRestart);
//        restartButton.setOnClickListener(restartListener);
//
//    }

    @OnCheckedChanged(R.id.btnRooted)
    public void onGrandBtnCheckedChange(CompoundButton buttonView, boolean isChecked) {
        final ToggleButton button = (ToggleButton) buttonView;

        if (isChecked) {
            if (!RootManager.getInstance().obtainPermission()) {
                button.setChecked(false);
            }
        }
        updateLog("Check Has Rooted " + button.isChecked());
    }

//    private static final CompoundButton.OnCheckedChangeListener grantButtonListener
//            = new CompoundButton.OnCheckedChangeListener() {
//
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            final ToggleButton button = (ToggleButton) buttonView;
//
//            if (isChecked) {
//                if (!RootManager.getInstance().obtainPermission()) {
//                    button.setChecked(false);
//                }
//            }
//            updateLog("Check Has Rooted " + button.isChecked());
//        }
//
//    };

    @OnClick(R.id.btnScreenShot)
    public void onClickScreenShot(View view) {
        final String path = "/sdcard/test_screencap.png";
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

//    private static final View.OnClickListener screenCapListener = new View.OnClickListener() {
//        final String path = "/sdcard/testcap.png";
//
//        @Override
//        public void onClick(View v) {
//            runAsyncTask(new AsyncTask<Boolean, Boolean, Boolean>() {
//
//                @Override
//                protected Boolean doInBackground(Boolean... params) {
//                    return RootManager.getInstance().screenCap(path);
//                }
//
//                @Override
//                protected void onPostExecute(Boolean result) {
//                    if (result) {
//                        updateLog("Screen shot saved at " + path);
//                    } else {
//                        updateLog("Screen shot failed");
//                    }
//
//                    super.onPostExecute(result);
//                }
//
//            });
//        }
//
//    };

    @OnClick(R.id.btnScreenRecord)
    public void onClickscreenRecord(View view) {
        final String path = "/sdcard/test_screenrecord.mp4";
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
//    private static final OnClickListener screenRecordListener = new OnClickListener() {
//        final String path = "/sdcard/testrecord.mp4";
//
//        @Override
//        public void onClick(View v) {
//            runAsyncTask(new AsyncTask<Boolean, Boolean, Boolean>() {
//
//                @Override
//                protected void onPreExecute() {
//                    updateLog("Recording 30s...");
//                    super.onPreExecute();
//                }
//
//                @Override
//                protected Boolean doInBackground(Boolean... params) {
//                    return RootManager.getInstance().screenRecord(path);
//                }
//
//                @Override
//                protected void onPostExecute(Boolean result) {
//                    if (result) {
//                        updateLog("Screen record saved at " + path);
//                    } else {
//                        updateLog("Screen record failed");
//                    }
//
//                    super.onPostExecute(result);
//                }
//
//            });
//        }
//    };

    @OnClick(R.id.btnRunCommand)
    public void onClickRunCommand(View view) {
        final String command = commandText.getText().toString();
        runAsyncTask(new AsyncTask<Void, Void, Result>() {

            @Override
            protected Result doInBackground(Void... params) {
                RootUtils.getArchName();
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
//    private static final OnClickListener runCommandListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            final String command = commandText.getText().toString();
//            runAsyncTask(new AsyncTask<Void, Void, Result>() {
//
//                @Override
//                protected Result doInBackground(Void... params) {
//                    RootUtils.getArchName();
//                    return RootManager.getInstance().runCommand(command);
//                }
//
//                @Override
//                protected void onPostExecute(Result result) {
//                    super.onPostExecute(result);
//                    updateLog("Command " + command + " execute " + result.getResult()
//                            + " with message " + result.getMessage());
//                }
//
//            });
//        }
//
//    };

    @OnClick(R.id.btnRemount)
    public void onClickRemount(View view) {
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

//    private static final OnClickListener remountListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            final String path = pathText.getText().toString();
//            final String type = isRW ? "rw" : "ro";
//            runAsyncTask(new AsyncTask<Void, Void, Boolean>() {
//
//                @Override
//                protected Boolean doInBackground(Void... params) {
//                    return RootManager.getInstance().remount(path, type);
//                }
//
//                @Override
//                protected void onPostExecute(Boolean result) {
//                    updateLog("Remount " + path + " as " + type + " executed " + result);
//                    isRW = !isRW;
//                    super.onPostExecute(result);
//                }
//            });
//
//        }
//
//    };

    @OnClick(R.id.bntInstallPackage)
    public void onClickInstallPackage(View view) {
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

//    private static final OnClickListener installListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            final String apkPath = apkText.getText().toString();
//            runAsyncTask(new AsyncTask<Void, Void, Result>() {
//
//                @Override
//                protected void onPreExecute() {
//                    updateLog("Installing package " + apkPath + " ...........");
//                    super.onPreExecute();
//                }
//
//                @Override
//                protected Result doInBackground(Void... params) {
//                    return RootManager.getInstance().installPackage(apkPath);
//                }
//
//                @Override
//                protected void onPostExecute(Result result) {
//                    updateLog("Install " + apkPath + " " + result.getResult()
//                            + " with the message " + result.getMessage());
//                    super.onPostExecute(result);
//                }
//
//            });
//
//            mSubscription = RootManager.getInstance().observeInstallPackage(apkPath)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Action1<Result>() {
//                        @Override
//                        public void call(Result result) {
//                            updateLog("Install " + apkPath + " " + result.getResult()
//                                    + " with the message " + result.getMessage());
//                        }
//                    }, new Action1<Throwable>() {
//                        @Override
//                        public void call(Throwable throwable) {
//                            updateLog("Install exception " + throwable.getClass().getSimpleName
// ());
//                        }
//                    });
//        }
//
//    };

    @OnClick(R.id.btnUninstallPackage)
    public void onClickUninstallPackage(View view) {
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

//    private static final OnClickListener uninstallListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            final String pkgName = pnText.getText().toString();
//            runAsyncTask(new AsyncTask<Void, Void, Result>() {
//
//                @Override
//                protected void onPreExecute() {
//                    updateLog("Uninstalling package " + pkgName + " ...........");
//                    super.onPreExecute();
//                }
//
//                @Override
//                protected Result doInBackground(Void... params) {
//                    return RootManager.getInstance().uninstallPackage(pkgName);
//                }
//
//                @Override
//                protected void onPostExecute(Result result) {
//                    updateLog("Uninstall " + pkgName + " " + result.getResult()
//                            + " with the message " + result.getMessage());
//                    super.onPostExecute(result);
//                }
//
//            });
//            mSubscription = RootManager.getInstance().observeUninstallPackage(pkgName)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Action1<Result>() {
//                        @Override
//                        public void call(Result result) {
//                            updateLog("Uninstall " + pkgName + " " + result.getResult()
//                                    + " with the message " + result.getMessage());
//                        }
//                    }, new Action1<Throwable>() {
//                        @Override
//                        public void call(Throwable throwable) {
//                            updateLog("Install exception " + throwable.getClass().getSimpleName
// ());
//                        }
//                    });
//        }
//
//    };

    @OnClick(R.id.btnRestart)
    public void onClickRestart(View view) {
        RootManager.getInstance().restartDevice();
    }

//    private static final OnClickListener restartListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            RootManager.getInstance().restartDevice();
//        }
//
//    };

    private final <T> void runAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        asyncTask.execute(params);
    }

    private void updateLog(String log) {
        logView.append("\n--> " + log);
    }
}
