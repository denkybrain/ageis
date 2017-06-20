package com.example.denky.ageis;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * Created by denky on 2017-06-20.
 */

public class CustomizedWebChromeClient extends WebChromeClient {
    ProgressBar progressBar;
    CustomizedWebViewManager customizedWebViewManager;
    Activity activity;
    CustomizedWebChromeClient(ProgressBar progressBar, CustomizedWebViewManager customizedWebViewManager) {
        this.progressBar = progressBar;
        this.customizedWebViewManager = customizedWebViewManager;
        this.activity = customizedWebViewManager.getActivity();
    }
    @Override
    public boolean onCreateWindow(WebView wv, boolean isDialog, boolean isUserGesture, Message resultMsg){
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
