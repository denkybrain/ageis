package com.example.denky.ageis;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.denky.ageis.ReferenceString.MAIN_URL;

/**
 * Created by denky on 2017-06-19.
 */

public class CustomizedWebViewManager {
    private CustomizedWebView normalWebView;
    private CustomizedWebView securityWebView;
    private static Fragment normalMode;
    private static Fragment securityMode;
    private FragmentNormalMode fragmentNormalMode;
    private FragmentSecurityMode fragmentSecurityMode; //스크롤 이벤트를 위해 만든 변수 둘
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
        this.fragmentNormalMode = (FragmentNormalMode)normalMode;
        this.fragmentSecurityMode = (FragmentSecurityMode)securityMode;
        this.activity = activity;
    }

    public void controlHidebar(int t, int oldt){
      if(t == 0 && oldt > 0){ // 보이게 함
           if(!SECURITY_MODE_STATE)
               fragmentNormalMode.visibleUniverseBar();
          else
               fragmentSecurityMode.visibleUniverseBar();
       }
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
                if(backUrl!= 0){
                    String backUrlReal = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();
                    if(backUrlReal.equals(MAIN_URL)){ //뒤로가려는 페이지가 홈 페이지면
                        if(SECURITY_MODE_STATE)
                            fragmentSecurityMode.visibleUniverseBar(); //바를 보이자
                        else
                            fragmentNormalMode.visibleUniverseBar();
                    }
                }
                //뒤로갈 url 구하기
                //WebBackForwardList webBackForwardList = wv.copyBackForwardList();
                //String backUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();//뒤로갈
                wv.goBack();
            }
            return 1;//앱종료 아님
        }
        catch (Exception e){
            return 0;
        }
    }
}
