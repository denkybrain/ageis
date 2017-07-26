package com.example.denky.ageis;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import static com.example.denky.ageis.Settings.permissionStartNewWindow;

/**
 * Created by denky on 2017-06-20.
 */

public class CustomizedWebChromeClient extends WebChromeClient {
    //Progress bar 설정, 새 창 띄우기 클래스
    ProgressBar progressBar;
    CustomizedWebViewManager customizedWebViewManager;
    Activity activity;
    CustomizedHandler handler;
    CustomizedWebChromeClient(ProgressBar progressBar, CustomizedWebViewManager customizedWebViewManager, CustomizedHandler handler) {
        this.progressBar = progressBar;
        this.customizedWebViewManager = customizedWebViewManager;
        this.activity = customizedWebViewManager.getActivity();
        this.handler = handler;
    }

    @Override
    public boolean onCreateWindow(WebView wv, boolean isDialog, boolean isUserGesture, Message resultMsg){
        if(!permissionStartNewWindow) //새창띄우기가 false면 함수 종료
        {
            handler.sendMsgQuick(handler.PERMISSION_DENY_NEW_WINDOW);
            return false;
        }
        wv.requestFocusNodeHref(resultMsg);
        String url = resultMsg.getData().getString("url");
        Intent intent = new Intent(activity.getApplicationContext(), ActivityMain.class);
        intent.putExtra("URL", url);
        activity.startActivity(intent);
        return super.onCreateWindow(wv,isDialog,isUserGesture,resultMsg);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        progressBar.setProgress(newProgress);
    }
}
