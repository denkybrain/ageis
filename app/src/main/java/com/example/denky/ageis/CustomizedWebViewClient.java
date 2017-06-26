package com.example.denky.ageis;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

import static com.example.denky.ageis.ReferenceString.MAIN_URL;
import static com.example.denky.ageis.Settings.autoClearUrl;

/**
 * Created by denky on 2017-06-08.
 */

class CustomizedWebViewClient extends WebViewClient {
    CustomizedWebView wv;
    WebSettings wvSettings;
    ProgressBar progressBar;
    CustomizedWebViewManager customizedWebViewManager;

    CustomizedWebViewClient(CustomizedWebView wv, WebSettings ws, ProgressBar pb,CustomizedWebViewManager customizedWebViewManager    ){
        this.wv = wv;
        this.wvSettings = ws;
        this.progressBar = pb;
        this.customizedWebViewManager = customizedWebViewManager;

    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //새로 페이지를 읽는 중이면 스크롤바 이벤트에서 제외시킴
        if(customizedWebViewManager.SECURITY_MODE_STATE )//시큐리티 모드면
           customizedWebViewManager.setSTATE_LOADING_SECURITY(true);
        else
            customizedWebViewManager.setSTATE_LOADING_NORMAL(true); //로딩중이면

        parseUri(wv.getUrl());
        progressBar.setVisibility(View.VISIBLE);
        return super.shouldOverrideUrlLoading(view, url);
    }

    public void setWebView(){
        wvSettings.setJavaScriptEnabled(Settings.useJavaScript);
        wvSettings.setJavaScriptCanOpenWindowsAutomatically(Settings.useJavaScript);
        wvSettings.setSupportMultipleWindows(true);
        wvSettings.setJavaScriptCanOpenWindowsAutomatically (Settings.permissionStartNewWindow);
        wvSettings.setAppCacheEnabled(Settings.permissionAppCache);
        //   wvSettings.setBuiltInZoomControls(true);
        wvSettings.setSupportZoom(true);
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
        parseUri(wv.getUrl());
        if(!url.equals(MAIN_URL))
            progressBar.setVisibility(View.VISIBLE);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    private void parseUri(String wvUri){
        if(customizedWebViewManager.SECURITY_MODE_STATE == false) {
            if(wvUri.equals(MAIN_URL)){ //초기 화면이면 uri창을 비움
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
                    wv.setUri(wvUri.substring(8, wvUri.length()));
            }
        }
        else if(customizedWebViewManager.SECURITY_MODE_STATE == true && autoClearUrl == true)        {
            wv.setUri("");
        }
    }
    //웹페이지 로딩 종료시 호출
    @Override
    public void onPageFinished(WebView view, String url){
        super.onPageFinished(view, url);
        if(customizedWebViewManager.SECURITY_MODE_STATE) {
            customizedWebViewManager.SECURITY_MODE_LAST_VIEW = url;
            customizedWebViewManager.setSTATE_LOADING_SECURITY(false);
        } else {
            customizedWebViewManager.NORMAL_MODE_LAST_VIEW = url;
            customizedWebViewManager.setSTATE_LOADING_NORMAL(false);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

}///////////////////////Inner Web view Chrome class /////////////////////////