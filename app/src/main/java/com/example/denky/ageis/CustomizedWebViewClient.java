package com.example.denky.ageis;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

import static com.example.denky.ageis.ReferenceString.securityMode;
import static com.example.denky.ageis.ReferenceString.startURL;

/**
 * Created by denky on 2017-06-08.
 */

class CustomizedWebViewClient extends WebViewClient {
    CustomizedWebView wv;
    WebSettings wvSettings;
    ProgressBar progressBar;
    private EditText uri;
    private String weburi;


    CustomizedWebViewClient(CustomizedWebView wv, WebSettings ws, ProgressBar pb, EditText editText, String weburi){
        this.wv = wv;
        this.wvSettings = ws;
        this.progressBar = pb;
        this.uri = editText;
        this.weburi = weburi;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // TODO Auto-generated method stub
        return super.shouldOverrideUrlLoading(view, url);
    }
    public void setWebView(){
        wvSettings.setJavaScriptEnabled(Settings.useJavaScript);
        wvSettings.setSupportMultipleWindows(Settings.permissionStartNewWindow);
        wvSettings.setJavaScriptCanOpenWindowsAutomatically (Settings.permissionStartNewWindow);
        wvSettings.setAppCacheEnabled(Settings.permissionAppCache);
        if(Settings.permissionAppCache == false)
            wvSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        else
            wvSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        wvSettings.setAllowFileAccess(Settings.permissionFileDownload);
        wvSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon){
        setWebView(); //페이지가 시작될때마다 웹뷰 설정 리로드
        super.onPageStarted(view, url, favicon);
        progressBar.setVisibility(View.VISIBLE);
    }
    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        //방문한 히스토리가 업데이트 될 때마다 작동하는 함수
       // Log.d("test", url);
        parseUri(wv.getUrl());
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    private void parseUri(String wvUri){
        if(securityMode == false) {
            if(wvUri.equals(startURL)){ //초기 화면이면 uri창을 비움
                wv.setUri("");
                return ;//초기화면이면 비우고 함수 종료
            }
            if (wvUri.startsWith("http://")) {//http://로 시작하면

                if (wvUri.endsWith("/"))
                   wv.setUri(wvUri.substring(7, wvUri.length()-1));
                else
                    wv.setUri(wvUri.substring(7, wvUri.length()));
            }
            if (wvUri.startsWith("https://")) {//암호화한 정보 전송규약 https일 경우
                if (wvUri.endsWith("/"))
                    wv.setUri(wvUri.substring(8, wvUri.length()-1));
                else
                    wv.setUri(wvUri.substring(7, wvUri.length()));
            }
        }
        else if(securityMode == true)
        {
            wv.setUri("");
        }
    }
    //웹페이지 로딩 종료시 호출
    @Override
    public void onPageFinished(WebView view, String url){
        super.onPageFinished(view, url);
        progressBar.setVisibility(View.INVISIBLE);
    }

}///////////////////////Inner Web view Chrome class /////////////////////////