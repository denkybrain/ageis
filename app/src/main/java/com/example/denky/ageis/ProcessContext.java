package com.example.denky.ageis;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by denky on 2017-06-11.
 */

public class ProcessContext {
    private String contextTitle = "";
    private String contextfirstMenu = "";
    private String contextsecondMenu = "";
    private String contextthirdMenu = "";
    private CustomizedWebView wv;
    public Handler handler;
    public String getUrl() {       return url;    }
    public void setUrl(String url) {        this.url = url;    }

    private String url="";
    private ContextMenu contextMenu;
    private final String hyperlink ="하이퍼링크";
    private final String saveImage = "이미지 다운로드(secure)";
    private final String share = "공유하기";
    private final String titleImage = "이미지";
    private final String hyperlinkImage = "하이퍼링크 이미지";
    private final String goURL = "주소로 이동하기";
    private final String viewImage = "이미지 확대";
    private int menuBtnVolume = 3;
    private int typeOfLongClickedItem = 0;//롱클릭된 아이템의 타입. 0 기본값 1은 anchor type, 2는 image, 3은 anchor-image

    ProcessContext(CustomizedWebView wv, Handler handler){
        this.wv = wv;
        this.handler = handler;
    } //이 함수에서 웹뷰와 메인액티비티 핸들러에 접근해야함

    public int getMenuBtnVolume() {
        return menuBtnVolume;
    }

    public void setMenuBtnVolume(int menuBtnVolume) {
        this.menuBtnVolume = menuBtnVolume;
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    public void setContextMenu(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

    public String getContextTitle() {
        return contextTitle;
    }

    public void setContextTitle(String contextTitle) {
        this.contextTitle = contextTitle;
    }

    public String getContextfirstMenu() {
        return contextfirstMenu;
    }

    public void setContextfirstMenu(String contextfirstMenu) {
        this.contextfirstMenu = contextfirstMenu;
    }

    public String getContextsecondMenu() {
        return contextsecondMenu;
    }

    public void setContextsecondMenu(String contextsecondMenu) {
        this.contextsecondMenu = contextsecondMenu;
    }

    public String getContextthirdMenu() {
        return contextthirdMenu;
    }

    public void setContextthirdMenu(String contextthirdMenu) {
        this.contextthirdMenu = contextthirdMenu;
    }

    public void setContextMenuContents(ContextMenu cm){
        cm.setHeaderTitle(contextTitle);
        cm.add(0,1,100,contextfirstMenu);
        cm.add(0,2,100,contextsecondMenu);
        cm.add(0,3,100,contextthirdMenu);
    }// reference : https://developer.android.com/reference/android/view/ContextMenu.html

    public void longClickEvent(WebView.HitTestResult hitTestResult, String url) {
        this.url = url;
        switch(hitTestResult.getType()) {
            case  WebView.HitTestResult.SRC_ANCHOR_TYPE: //앙코르 타입이면
                menuBtnVolume = 3;
                typeOfLongClickedItem = 1;
                setContextTitle(hyperlink);
                setContextfirstMenu(goURL);
                setContextthirdMenu(share);
               // Log.d("widae", "Anchor Link:"+url);
                break;
            case WebView.HitTestResult.IMAGE_TYPE: //이미지 타입이면
                menuBtnVolume = 2;
                typeOfLongClickedItem = 2;
                setContextTitle(titleImage);
                setContextfirstMenu(saveImage);
                setContextsecondMenu(share);
               // Log.d("widae", "Image Link:"+url);
                break;
            case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                menuBtnVolume = 2;
                typeOfLongClickedItem = 3;
                setContextTitle(hyperlinkImage);
                setContextfirstMenu(viewImage);
                setContextthirdMenu(share);
               // Log.d("widae", "Image Anchor Link:"+url);
                break;
            default:
                this.url="";
                menuBtnVolume = 0;
                break;
        }
    }
    public void onContextItemSelected(int itemId) {

        switch(typeOfLongClickedItem) {
            case 1 :
                return ;
            case 2 :// img tag
                switch (itemId){
                    case 1 : //save img
                        ImageDownload downloader = new ImageDownload();
                        downloader.handler = this.handler;
                        downloader.execute(this.url);
                        break;
                    case 2 : //share
                        break;
                }
                break;
            case 3 :// anchor img tag
                url = url.substring(0, this.url.indexOf('?'));
                Log.d("widae", "item id : "+ itemId);
                switch(itemId){
                    case 1 : //goto
                        wv.goToURL(url);
                        break;
                    case 2 : //save img
                       // Log.d("widae", "long click from : "+url);
                        ImageDownload downloader = new ImageDownload();
                        downloader.handler = this.handler;
                        downloader.execute(this.url);
                        break;
                    case 3 : //share
                        break;
                }
                break;
            default: //anchor, img, anchor-img 중 아무것도 아니면 함수 종료
                    return;
        }

    }
}
