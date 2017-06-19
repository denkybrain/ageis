package com.example.denky.ageis;

import static com.example.denky.ageis.ReferenceString.MAIN_URL;
import static com.example.denky.ageis.ReferenceString.SECURITY_MODE_STATE;

/**
 * Created by denky on 2017-06-19.
 */

public class CustomizedWebViewManager {
    private CustomizedWebView normalWebView;
    private CustomizedWebView securityWebView;

    public void setSecurityWebView(CustomizedWebView securityWebView) {
        this.securityWebView = securityWebView;
    }

    public void setNormalWebView(CustomizedWebView normalWebView) {
        this.normalWebView = normalWebView;
    }
    public int backPress(){
        CustomizedWebView wv;
        if(SECURITY_MODE_STATE)
            wv = securityWebView;
        else
            wv = normalWebView;
        if (wv.getUrl().equals(MAIN_URL)) {//현재가 초기 페이지면 앱을 종료
            if(SECURITY_MODE_STATE == true) { //시큐리티 모드면 웹뷰의 기록을 파괴하고 어플 종료
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
        return 1 ;
    }
}
