package com.example.denky.ageis;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    Button go, right, left;
    EditText uri;
    WebView wv;
    WebSettings wvSettings;
    View.OnClickListener cl;
    String weburi;
    InputMethodManager imm;
    ImageView homeBtn , lockBtn;
    String urlHint ="Search or Input URL";
    String startURL = "http://denkybrain.cafe24.com/ageis/main.php";
    String country[]
            ={".com",".co.kr"};
    String fileformat[]
            =
            {".php",".html"};
    LinearLayout gotoBar, universe;
    boolean securityMode = false;
    ///변수 초기화 끝


    class MyWeb extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
    // overloading

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        //go = (Button) findViewById(R.id.go);
        //right = (Button) findViewById(R.id.right);
        //left = (Button) findViewById(R.id.left);
        uri = (EditText) findViewById(R.id.uri);
        wv = (WebView) findViewById(R.id.wv);
        homeBtn = (ImageView)findViewById(R.id.homeBtn);
        lockBtn = (ImageView)findViewById(R.id.lockBtn);
        gotoBar = (LinearLayout)findViewById(R.id.gotoBar);
        universe = (LinearLayout)findViewById(R.id.universe);
        wv.setWebViewClient(new MyWeb());
        wvSettings = wv.getSettings();
        wvSettings.setJavaScriptEnabled(true);

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
        homeBtn.setOnClickListener(new View.OnClickListener() {//home button click
            @Override
            public void onClick(View v) {
                goToURL(startURL);
            }
        });
        lockBtn.setOnClickListener(new View.OnClickListener() {//home button click
            @Override
            public void onClick(View v) {
                if(securityMode == false) {
                    uri.setHint("Security Mode"); //시큐리티 모드
                    uri.setBackgroundResource(R.color.supergrey);
                    uri.setTextColor(Color.WHITE);
                    lockBtn.setImageResource(R.drawable.returnbtn);
                    gotoBar.setBackgroundResource(R.color.supergrey);
                    homeBtn.setImageResource(R.drawable.home2);
                    universe.setBackgroundResource(R.color.supergrey);
                    securityMode = true;
                }
                else{
                    //기본 모드
                    uri.setHint(urlHint);
                    uri.setBackgroundResource(R.color.white);
                    uri.setTextColor(Color.BLACK);
                    lockBtn.setImageResource(R.drawable.lock);
                    homeBtn.setImageResource(R.drawable.home);
                    gotoBar.setBackgroundResource(R.color.white);
                    universe.setBackgroundResource(R.color.white);
                    securityMode = false;
                }
            }
        });

        cl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*switch (v.getId()){
                    case R.id.go :
                        weburi = uri.getText().toString();
                        if(weburi.startsWith("http://")) {
                            wv.loadUrl(weburi);
                        } else {
                            wv.loadUrl("http://" + weburi); }
                        break;
                    case R.id.left :
                        wv.goBack();
                        break;

                    case R.id.right :
                        wv.goForward();
                        break;

                }
                */
            }
        };
        //go.setOnClickListener(cl);
        //right.setOnClickListener(cl);
        //left.setOnClickListener(cl);
    }
}
