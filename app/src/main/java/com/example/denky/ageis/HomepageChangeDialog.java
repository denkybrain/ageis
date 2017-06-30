package com.example.denky.ageis;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by WINDOWS7 on 2017-07-01.
 */

public class HomepageChangeDialog extends Activity{
    private EditText homeUriInfo;
    private TextView changeUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_setting_dialog);

        homeUriInfo=(EditText)findViewById(R.id.homeUriInfo);
        homeUriInfo.setText(Settings.homeUri);
        homeUriInfo.requestFocus();
        homeUriInfo.extendSelection(0);

        //Show soft keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        changeUri=(TextView)findViewById(R.id.chagngeHomeUriCommit);
        changeUri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.homeUri=homeUriInfo.getText().toString();
                if(Settings.homeUri.substring(0, 3).equals("http")==false){
                    Settings.homeUri="http://"+Settings.homeUri;
                }
                Settings.saveSettings();
                finish();
            }
        });
    }
}
