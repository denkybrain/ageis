package com.example.denky.ageis;

/**
 * Created by denky on 2017-05-19.
 */

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.example.denky.ageis.MainActivity.setting_adblock;
import static com.example.denky.ageis.MainActivity.setting_cache;
import static com.example.denky.ageis.MainActivity.setting_fileAccess;
import static com.example.denky.ageis.MainActivity.setting_history;
import static com.example.denky.ageis.MainActivity.setting_javascript;
import static com.example.denky.ageis.MainActivity.setting_newWindow;
import static com.example.denky.ageis.MainActivity.setting_proxy;
import static com.example.denky.ageis.MainActivity.setting_vulnerable;
import static com.example.denky.ageis.R.id.newWindowOn;

public class SettingActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8;
    TextView googleAsynText;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_setting);
        googleAsynText = (TextView)findViewById(R.id.googleBtn);
        cb1 = (CheckBox)findViewById(R.id.javascriptCheck);
        cb2 = (CheckBox)findViewById(R.id.newWindowOn);
        cb3 = (CheckBox)findViewById(R.id.fileDownloadOn);
        cb4 = (CheckBox)findViewById(R.id.cacheOn);
        cb5 = (CheckBox)findViewById(R.id.webvulnearableToolOn);
        cb6 = (CheckBox)findViewById(R.id.proxyOn);
        cb7 = (CheckBox)findViewById(R.id.historyDelOn);
        cb8 = (CheckBox)findViewById(R.id.adBlockOn);
        cb1.setChecked(setting_javascript);
        cb2.setChecked(setting_newWindow);
        cb3.setChecked(setting_fileAccess);
        cb4.setChecked(setting_cache);
        cb5.setChecked(setting_vulnerable);
        cb6.setChecked(setting_proxy);
        cb7.setChecked(setting_history);
        cb8.setChecked(setting_adblock);
    }

    @Override
    public void onBackPressed() { //뒤로가기 버튼 누르면
        //settingSave();
        finish();//액티비티 다운
    }
    public void clickedPermission(View v){
        switch (v.getId()){
            case R.id.javascriptCheck :
                setting_javascript = cb1.isChecked();
                break;
            case R.id.newWindowOn :
                setting_newWindow = cb2.isChecked();
                break;
            case R.id.fileDownloadOn :
                setting_fileAccess = cb3.isChecked();
                break;
            case R.id.cacheOn :
                setting_cache = cb4.isChecked();
                break;
            case R.id.webvulnearableToolOn :
                setting_vulnerable = cb5.isChecked();
                break;
            case R.id.proxyOn :
                setting_proxy = cb6.isChecked();
                break;
            case R.id.historyDelOn :
                setting_history = cb7.isChecked();
                break;
            case R.id.adBlockOn :
                setting_adblock = cb8.isChecked();
                break;
            case R.id.googleBtn :
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

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleAsynText.setText("현재 로그인 상태입니다.");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    protected void settingSave()  {

        File folder ;
        folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Ageis/");

        try {
            folder.mkdirs();
            Log.d("result", "됨1");

        } catch(Exception e ){
            Log.d("result", "안 됨1");

        }
        File file = new File(folder,"/Ageis/setting.txt");

            try {
                file.createNewFile();

        Log.d("result","됨ㅅㄱ");
        } catch (IOException e) {
            Log.d("result", "안됪ㄱ");
        }
        FileWriter fw = null ;
        String text = "";
        text+="[JS][NewWindow][FileDown][Cache][Tool][Proxy][HistoryDel][AdBlock]";
        text += "|"+setting_javascript
                +"|"+setting_newWindow
                +"|"+setting_fileAccess
                +"|"+setting_cache
                +"|"+setting_vulnerable
                +"|"+setting_proxy
                +"|"+setting_history
                +"|"+setting_adblock;

        try {
            // open file.
            fw = new FileWriter(file);
            // write file.
            fw.write(text) ;

        } catch (Exception e) {
            e.printStackTrace() ;
        }

        // close file.
        if (fw != null) {
            // catch Exception here or throw.
            try {
                fw.close() ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
