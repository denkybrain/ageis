package com.example.denky.ageis;

/**
 * Created by denky on 2017-05-19.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class ActivitySetting extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener{

    CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8;
    TextView googleAsyncText;
    private GoogleApiClient mGoogleApiClient;

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
        cb6 = (CheckBox)findViewById(R.id.proxyOn);
        cb7 = (CheckBox)findViewById(R.id.historyDelOn);
        cb8 = (CheckBox)findViewById(R.id.adBlockOn);

        cb1.setChecked(Settings.useJavaScript);
        cb2.setChecked(Settings.permissionStartNewWindow);
        cb3.setChecked(Settings.permissionFileDownload);
        cb4.setChecked(Settings.permissionAppCache);
        cb5.setChecked(Settings.useVulnerabilityFindAlgorithm);
        cb6.setChecked(Settings.useProxyServer);
        cb7.setChecked(Settings.permissionAutoRemoveHistory);
        cb8.setChecked(Settings.useAdBlock);

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
                Settings.useProxyServer=cb6.isChecked();
                Settings.permissionAutoRemoveHistory=cb7.isChecked();
                Settings.useAdBlock=cb8.isChecked();

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
<<<<<<< HEAD
            case R.id.googleBtn :
=======
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
            case R.id.proxyOn :
                useProxyServer = cb6.isChecked();
                break;
            case R.id.historyDelOn :
                permissionAutoRemoveHistory = cb7.isChecked();
                break;
            case R.id.adBlockOn :
               useAdBlock = cb8.isChecked();
                break;
<<<<<<< HEAD:app/src/main/java/com/example/denky/ageis/SettingActivity.java
           /* case R.id.googleBtn :
>>>>>>> ageis/master
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(Plus.API)
                        .addScope(Plus.SCOPE_PLUS_PROFILE)
                        .build();
                mGoogleApiClient.connect();
                Log.d("result " , "구글 계정 연동 버튼~");
                break;
            case R.id.googleDeleteBtn :
                break;
<<<<<<< HEAD
=======
*/
>>>>>>> ageis/master
=======
>>>>>>> ageis/master:app/src/main/java/com/example/denky/ageis/ActivitySetting.java
            case R.id.settingInit:
                Settings.restoreSetting();
                finish();
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleAsyncText.setText("현재 로그인 상태입니다.");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
<<<<<<< HEAD:app/src/main/java/com/example/denky/ageis/SettingActivity.java

=======
>>>>>>> ageis/master:app/src/main/java/com/example/denky/ageis/ActivitySetting.java
}
