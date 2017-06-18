package com.example.denky.ageis;

/**
 * Created by denky on 2017-05-19.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
public class ActivitySetting extends AppCompatActivity {

    CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7;

    private boolean useJavaScript=Settings.useJavaScript;
    private boolean permissionStartNewWindow=Settings.permissionStartNewWindow;
    private boolean permissionFileDownload=Settings.permissionFileDownload;
    private boolean permissionAppCache=Settings.permissionAppCache;
    private boolean useVulnerabilityFindAlgorithm=Settings.useVulnerabilityFindAlgorithm;
    private boolean denyDangerousSite =Settings.denyDangerousSite;
    private boolean permissionAutoRemoveHistory=Settings.permissionAutoRemoveHistory;
    private boolean useAdBlock=Settings.useAdBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_setting);

       // googleAsyncText = (TextView)findViewById(R.id.googleBtn);
        cb1 = (CheckBox)findViewById(R.id.javascriptCheck);
        cb2 = (CheckBox)findViewById(R.id.newWindowOn);
        cb3 = (CheckBox)findViewById(R.id.fileDownloadOn);
        cb4 = (CheckBox)findViewById(R.id.cacheOn);
        cb5 = (CheckBox)findViewById(R.id.webvulnearableToolOn);
        cb6 = (CheckBox)findViewById(R.id.denyDangerousSiteOn);
        cb7 = (CheckBox)findViewById(R.id.historyDelOn);

        cb1.setChecked(Settings.useJavaScript);
        cb2.setChecked(Settings.permissionStartNewWindow);
        cb3.setChecked(Settings.permissionFileDownload);
        cb4.setChecked(Settings.permissionAppCache);
        cb5.setChecked(Settings.useVulnerabilityFindAlgorithm);
        cb6.setChecked(Settings.denyDangerousSite);
        cb7.setChecked(Settings.permissionAutoRemoveHistory);

    }

    @Override
    public void onBackPressed() { //뒤로가기 버튼 누르면
        DialogMaker maker=new DialogMaker();
        Callback callback_positive=new Callback() {
            @Override
            public void callbackMethod() {
                Settings.useJavaScript=cb1.isChecked();
                Settings.permissionStartNewWindow=cb2.isChecked();
                Settings.permissionFileDownload=cb3.isChecked();
                Settings.permissionAppCache=cb4.isChecked();
                Settings.useVulnerabilityFindAlgorithm=cb5.isChecked();
                Settings.denyDangerousSite =cb6.isChecked();
                Settings.permissionAutoRemoveHistory=cb7.isChecked();
                Settings.saveSettings();
                finish();
            }
        };
        Callback callback_negative=new Callback() {
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
            case R.id.webvulnearableToolOn :
                useVulnerabilityFindAlgorithm = cb5.isChecked();
                break;
            case R.id.denyDangerousSiteOn:
                denyDangerousSite = cb6.isChecked();
                break;
            case R.id.historyDelOn :
                permissionAutoRemoveHistory = cb7.isChecked();
                break;
            case R.id.settingInit:
                Settings.restoreSetting();
                finish();
                break;
        }
    }


}
