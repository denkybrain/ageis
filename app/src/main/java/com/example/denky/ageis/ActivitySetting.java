package com.example.denky.ageis;

/**
 * Created by denky on 2017-05-19.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import static com.example.denky.ageis.Settings.autoClearUrl;
import static com.example.denky.ageis.Settings.permissionAppCache;
import static com.example.denky.ageis.Settings.permissionAutoRemoveHistory;
import static com.example.denky.ageis.Settings.permissionFileDownload;
import static com.example.denky.ageis.Settings.permissionStartNewWindow;
import static com.example.denky.ageis.Settings.useJavaScript;
import static com.example.denky.ageis.Settings.permissionDangerousSite;
import static com.example.denky.ageis.Settings.useVulnerabilityFindAlgorithm;

public class ActivitySetting extends AppCompatActivity {

    CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_setting);
        cb1 = (CheckBox)findViewById(R.id.javascriptCheck);
        cb2 = (CheckBox)findViewById(R.id.newWindowOn);
        cb3 = (CheckBox)findViewById(R.id.fileDownloadOn);
        cb4 = (CheckBox)findViewById(R.id.cacheOn);
        cb5 = (CheckBox)findViewById(R.id.webvulnearableToolOn);
        cb6 = (CheckBox)findViewById(R.id.clickedDenyDangerousSite);
        cb7 = (CheckBox)findViewById(R.id.historyDelOn);
        cb8 = (CheckBox)findViewById(R.id.clearUrlOn);

        cb1.setChecked(useJavaScript);
        cb2.setChecked(permissionStartNewWindow);
        cb3.setChecked(permissionFileDownload);
        cb4.setChecked(permissionAppCache);
        cb5.setChecked(useVulnerabilityFindAlgorithm);
        cb6.setChecked(permissionDangerousSite);
        cb7.setChecked(permissionAutoRemoveHistory);
        cb8.setChecked(autoClearUrl);

    }

    @Override
    public void onBackPressed() { //뒤로가기 버튼 누르면
        DialogMaker maker=new DialogMaker();
        DialogMaker.Callback callback_positive=new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                useJavaScript=cb1.isChecked();
                permissionStartNewWindow=cb2.isChecked();
                permissionFileDownload=cb3.isChecked();
                permissionAppCache=cb4.isChecked();
                useVulnerabilityFindAlgorithm=cb5.isChecked();
                permissionDangerousSite =cb6.isChecked();
                permissionAutoRemoveHistory=cb7.isChecked();
                autoClearUrl = cb8.isChecked();
                Settings.saveSettings();
                finish();
            }
        };
        DialogMaker.Callback callback_negative=new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                finish();
            }
        };
        maker.setValue("설정을 저장하시겠습니까?", "예", "아니오", callback_positive, callback_negative);
        maker.show(getSupportFragmentManager(), "Dialog");
    }
    public void clickedPermission(View v){
        switch (v.getId()){
            case R.id.javascriptCheck :
                useJavaScript = cb1.isChecked();
                break;
            case R.id.newWindowOn :
                permissionStartNewWindow = cb2.isChecked();
                break;
            case R.id.fileDownloadOn :
                permissionFileDownload = cb3.isChecked();
                break;
            case R.id.cacheOn :
                permissionAppCache = cb4.isChecked();
                break;
            case R.id.changeHomeUri:
                Intent intent=new Intent(getApplicationContext(), HomepageChangeDialog.class);
                startActivity(intent);
                break;
            case R.id.manageFavoriteSite:
                //즐겨찾기 사이트 띄움
                break;
            case R.id.webvulnearableToolOn :
                useVulnerabilityFindAlgorithm = cb5.isChecked();
                if(useVulnerabilityFindAlgorithm == false) {
                    permissionDangerousSite = false;
                    cb6.setChecked(false);
                }
                break;
            case R.id.clickedDenyDangerousSite:
                permissionDangerousSite = cb6.isChecked();
                break;
            case R.id.historyDelOn :
                permissionAutoRemoveHistory = cb7.isChecked();
                break;
            case R.id.settingInit:
                Settings.restoreSetting();
                finish();
                break;
            case R.id.clearUrlOn :
                autoClearUrl = cb8.isChecked();
                break;

        }
    }
}
