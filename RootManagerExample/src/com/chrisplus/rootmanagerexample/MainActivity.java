
package com.chrisplus.rootmanagerexample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.chrisplus.rootmanager.RootManager;

public class MainActivity extends Activity {

    private ToggleButton grantButton;
    private Button installButton;
    private Button uninstallButton;
    private Button screenshotButton;
    private Button runButton;
    private Button remountButton;

    private EditText commandText;
    private EditText pathText;
    private TextView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void init() {
        /* grant button */
        grantButton = (ToggleButton) findViewById(R.id.btnRooted);
        if (RootManager.getInstance().hasRooted()) {
            grantButton.setEnabled(true);
        }

        grantButton.setOnCheckedChangeListener(grantButtonListener);
    }

    private static final OnCheckedChangeListener grantButtonListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final ToggleButton button = (ToggleButton) buttonView;

            if (isChecked) {
                if (!RootManager.getInstance().grantPermission()) {
                    button.setChecked(false);
                }
            }
        }

    };
    

    private static final <T> void runAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        asyncTask.execute(params);
    }

}
