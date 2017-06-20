package com.example.denky.ageis;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.webkit.WebBackForwardList;

import static com.example.denky.ageis.ReferenceString.MAIN_URL;

/**
 * Created by denky on 2017-06-19.
 */

public class CustomizedWebViewManager {
    private CustomizedWebView normalWebView;
    private CustomizedWebView securityWebView;
    private static Fragment normalMode;
    private static Fragment securityMode;
    public String NORMAL_MODE_LAST_VIEW = MAIN_URL;
    public String SECURITY_MODE_LAST_VIEW = MAIN_URL;
    public boolean SECURITY_MODE_STATE = false;
    public boolean focusOnUrlBar = true;
    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    CustomizedWebViewManager(Fragment normalMode, Fragment securityMode, Activity activity) {
        this.normalMode = normalMode;
        this.securityMode = securityMode;
        this.activity = activity;
    }

    public void setSecurityWebView(CustomizedWebView securityWebView) {
        this.securityWebView = securityWebView;
    }

    public void setNormalWebView(CustomizedWebView normalWebView) {
        this.normalWebView = normalWebView;
    }
    public void goToUrlNormalMode(String url){
        NORMAL_MODE_LAST_VIEW = url;
        focusOnUrlBar = false;
    }

    public static Fragment getNormalMode() {
        return normalMode;
    }

    public static Fragment getSecurityMode() {
        return securityMode;
    }

    public int backPress(){
        try {
            CustomizedWebView wv;
            if (SECURITY_MODE_STATE)
                wv = securityWebView;
            else
                wv = normalWebView;
            WebBackForwardList webBackForwardList = wv.copyBackForwardList();
            int backUrl = webBackForwardList.getCurrentIndex();
            if (wv.getUrl().equals(MAIN_URL) || backUrl == 0) {//현재가 초기 페이지면 앱을 종료
                if (SECURITY_MODE_STATE == true) { //시큐리티 모드면 웹뷰의 기록을 파괴하고 어플 종료
                    wv.clearHistory();
                    wv.clearCache(true);
                }
                return 0; //앱을 종료한다
            } else { //현재가 초기 페이지가 아니라 로딩 페이지면 앱을 종료하지않고 뒤로감
                //뒤로갈 url 구하기
                //WebBackForwardList webBackForwardList = wv.copyBackForwardList();
                //String backUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();//뒤로갈
                wv.goBack();
            }
            return 1;
        }
        catch (Exception e){
            return 0;
        }
    }
}
