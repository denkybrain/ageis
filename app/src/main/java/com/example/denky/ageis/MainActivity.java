package com.example.denky.ageis;

/**
 * Created by denky on 2017-05-14.
 */

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


// overloading
public class MainActivity extends AppCompatActivity {
    static public boolean setting_javascript = true;
    static public boolean setting_newWindow = true;
    static public boolean setting_fileAccess = false;
    static public boolean setting_cache = true;
    static public boolean setting_vulnerable = true;
    static public boolean setting_adblock = true;
    static public boolean setting_proxy = false;
    static public boolean setting_history = true;
    private Button go, right, left;
    private EditText uri; //요즘은 URL가 아니라 URI, uniform resource identifier라고 부름
    private WebView wv;
    private WebSettings wvSettings; //webview setting 객체임. 편리하게 쓰려고 만듬
    private View.OnClickListener cl; //여러 개의 클릭 리스너를 switch로 처리하려고 만듬
    private String weburi;
    public ProgressBar progressBar;
    private InputMethodManager imm; //엔터키 입력 매니지를 위한 객체
    private ImageView homeBtn , lockBtn, settingBtn;
    private String uriHint ="Search or Input URI";
    private String securityHint = "Security Mode";
    private String startURL = "http://denkybrain.cafe24.com/ageis/main.php";
    private String country[]
            =   {".com",".co.kr"};
    private String fileformat[]
            =   {".php",".html"};
    private LinearLayout gotoBar, universe;
    boolean securityMode = false;
    /*************** 변수 초기화 끝 ******************/

    class MyWeb extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            return super.shouldOverrideUrlLoading(view, url);
        }
        private void setter(){
            wvSettings.setJavaScriptEnabled(setting_javascript);
            wvSettings.setSupportMultipleWindows(setting_newWindow);
            wvSettings.setAppCacheEnabled(setting_cache);
            wvSettings.setAllowFileAccess(setting_fileAccess);
        }

        //웹 페이지 로딩 시작시 호출

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            setter();
            super.onPageStarted(view, url, favicon);
            String wvUri = wv.getUrl().toString();
            if(!wvUri.equals(startURL)){} //초기화면에는 프로그래스 바를 표현하지않음
            else    progressBar.setVisibility(View.VISIBLE);
            if(wvUri.equals(startURL)){ //초기 화면이면 uri창을 비움
                uri.setText("");
            }else{ //초기 화면이 아니면
                if(wvUri.startsWith("http://")){//http://로 시작하면
                    if(wvUri.endsWith("/"))
                        uri.setText(wvUri.substring(7, wv.getUrl().length()-1));
                    else
                        uri.setText(wvUri.substring(7, wv.getUrl().length()));
                }
                if(wvUri.startsWith("https://")){//암호화한 정보 전송규약 https일 경우
                    if(wvUri.endsWith("/"))
                        uri.setText(wvUri.substring(8, wv.getUrl().length()-1));
                    else
                        uri.setText(wvUri.substring(8, wv.getUrl().length()));
                }
            }
        }

        //웹페이지 로딩 종료시 호출
        @Override
        public void onPageFinished(WebView view, String url){
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS); //프로그래스 바 기능 요청
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //settingRead();
       // getSupportActionBar().setDisplayShowHomeEnabled(true); -> 하면 오류걸림. 아이콘 설정 코드였는데 현재 버전 API에서 제공 안 하는듯.
        // 아이콘 설정하려면 그냥 Manifest.xml만 건들면 댐
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        //right = (Button) findViewById(R.id.right);
        //left = (Button) findViewById(R.id.left);
        uri = (EditText) findViewById(R.id.uri);
        wv = (WebView) findViewById(R.id.wv);
        homeBtn = (ImageView)findViewById(R.id.homeBtn);
        lockBtn = (ImageView)findViewById(R.id.lockBtn);
        gotoBar = (LinearLayout)findViewById(R.id.gotoBar);
        universe = (LinearLayout)findViewById(R.id.universe);
        settingBtn = (ImageView)findViewById(R.id.settingBtn);
        MyWeb wvWeb = new MyWeb();
        wv.setWebViewClient(wvWeb);
        wv.setWebChromeClient(new WebChromeClient() { //Progress bar 체인지를 위한 ChromeClient
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }

        });
        wvSettings = wv.getSettings();
        wvWeb.setter();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //progressBar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN); //
        //progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);//Prgress bar color change
        progressBar.setVisibility(View.INVISIBLE);
        goToURL(startURL); //처음 화면 로딩
        uri.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {  //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    imm.hideSoftInputFromWindow(uri.getWindowToken(), 0);
                    goToURL();
                }
                return false;
            }
        });

        cl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.homeBtn : //홈버튼 이벤트 처리
                        goToURL(startURL);
                        uri.setText("");
                        break;
                    /*case R.id.left :
                        wv.goBack();
                        break;
                    case R.id.right :
                        wv.goForward();
                        break;
                        */
                    case R.id.lockBtn :
                        if(securityMode == false) {
                            uri.setHint(securityHint); //시큐리티 모드
                            uri.setBackgroundResource(R.color.supergrey);
                            uri.setTextColor(Color.WHITE);
                            lockBtn.setImageResource(R.drawable.returnbtn);
                            gotoBar.setBackgroundResource(R.color.supergrey);
                            homeBtn.setImageResource(R.drawable.home2);
                            settingBtn.setImageResource(R.drawable.setting2);
                            universe.setBackgroundResource(R.color.supergrey);
                            securityMode = true;
                        }
                        else{
                            //기본 모드
                            uri.setHint(uriHint);
                            uri.setBackgroundResource(R.color.white);
                            uri.setTextColor(Color.BLACK);
                            lockBtn.setImageResource(R.drawable.lock);
                            homeBtn.setImageResource(R.drawable.home);
                            settingBtn.setImageResource(R.drawable.setting);
                            gotoBar.setBackgroundResource(R.color.white);
                            universe.setBackgroundResource(R.color.white);
                            securityMode = false;
                        }
                        break;
                    case R.id.settingBtn :
                        Intent appSetting = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(appSetting);
                        break;
                }

            }
        };
        lockBtn.setOnClickListener(cl);
        homeBtn.setOnClickListener(cl);
        settingBtn.setOnClickListener(cl);
        //go.setOnClickListener(cl);
        //right.setOnClickListener(cl);
        //left.setOnClickListener(cl);
    }

    void goToURL(String link){ // go to link by calling
        wv.loadUrl(link);
    }

    void goToURL(){ //go to with the uri.getText
        weburi = uri.getText().toString();
        if (weburi.startsWith("http://")) {
            wv.loadUrl(weburi);
        } else {
            for(int i = 0 ; i < country.length; i++){
                if(weburi.endsWith(country[i])){
                    wv.loadUrl("http://"+weburi);
                    return ;
                }
            }
            for(int i = 0; i < fileformat.length; i++){
                if(weburi.endsWith(fileformat[i])){
                    wv.loadUrl("http://"+weburi);
                    return ;
                }
            }
            //둘다 아니면, 즉, country와 fileformat과 맞지않으면 검색으로 치부함
            wv.loadUrl("https://www.google.co.kr/search?q=" + weburi);
        }
    }

    @Override
    public void onBackPressed() { //뒤로가기 버튼 누르면 뒤로감
        if (wv.getUrl().equals(startURL)) {//현재가 초기 페이지면 앱을 종료
            if(securityMode == true) { //시큐리티 모드면 웹뷰의 기록을 파괴하고 어플 종료
                wv.clearHistory();
                wv.clearCache(true);
            }
            super.onBackPressed();
        } else { //현재가 초기 페이지가 아니라 로딩 페이지면 앱을 종료하지않고 뒤로감
            wv.goBack();
        }
        //뒤로 가기 버튼을 누른 후에
        uri.setText(""); //텍스트를 비운다
    }

    public void settingRead(){
        File file = new File("setting/file.txt") ;
        FileReader fr = null ;
        int data ;
        char ch ;
        String text = "";

        try {
            // open file.
            fr = new FileReader(file) ;

            // read file.
            while ((data = fr.read()) != -1) {
                // TODO : use data
                ch = (char) data ;
                text+=ch;

            }
            Log.d("file reader", text);
            fr.close() ;
        } catch (Exception e) {
            Log.d("file read", "failed");
        }


    }
}
