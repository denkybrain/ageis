package com.example.denky.ageis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.denky.ageis.ReferenceString.DEVICE_HEIGHT;
import static com.example.denky.ageis.ReferenceString.GOOGLE_SEARCH_URL;
import static com.example.denky.ageis.ReferenceString.MAIN_URL;
import static com.example.denky.ageis.ReferenceString.URL_HASHMAP;
import static com.example.denky.ageis.Settings.useVulnerabilityFindAlgorithm;

/**
 * Created by denky on 2017-06-08.
 */
public class CustomizedWebView extends WebView {
    private final String SAVE_FOLDER = "/ageis_screenshot";
    private WebView wv = this;
    public String weburi;
    private EditText uri;
    public CustomizedHandler handler;
    private final int SAFETY_GREAT = 1;
    private final int SAFETY_NORMAL = 2;
    private final int SAFETY_EXPOSED = 3;
    private final int SAFETY_WARNING = 4;
    private final int SAFETY_UNACCESSABLE = 5;
    public final String SHOW_SAFETY_GREAT = "Great";
    public final String SHOW_SAFETY_NORMAL = "Normal";
    public final String SHOW_SAFETY_EXPOSED = "Exposed";
    public final String SHOW_SAFETY_WARNING = "Warning!";
    public final String SHOW_SAFETY_UNACCESSABLE = "Unaccessable";
    public String resultOfsafety;
    private CustomizedWebViewManager customizedWebViewManager;


    public void constructor(String weburi, EditText editText, CustomizedHandler handler
            ,CustomizedWebViewManager cwvm){
        this.weburi = weburi;
        this.uri = editText;
        this.handler = handler;
        this.customizedWebViewManager = cwvm;

    }

    public CustomizedWebView(Context context) {
        super(context);
    }
    public CustomizedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) { //스크롤 이벤트
        if(customizedWebViewManager.SECURITY_MODE_STATE) {// 시큐리티 모드면
            if (customizedWebViewManager.isSTATE_LOADING_SECURITY())
                return; //로딩중이면 함수 종료시킴.
        } else {//노말 모드일 때
            if (customizedWebViewManager.isSTATE_LOADING_NORMAL())
                return ;
        }
        customizedWebViewManager.controlHidebar(t, oldt);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setUri(String str){
        uri.setText(str);
    }
    public String getUriTextString(){
        return  uri.getText().toString();
    }
    public void goToURL(String link){ // go to link by calling
        weburi = link;
        if(CHECK_STATIC_URL(weburi)){ return ;  } //static에 등록된 URL이면 이동하고 함수를 종료함.
        if (weburi.startsWith("http://") || weburi.startsWith("https://")) {
            checkVirus(weburi);
        } else {
            if(weburi.indexOf('.') > 0 ){
                checkVirus("http://"+weburi);
                return;
            }            //둘다 아니면, 즉, country와 fileformat과 맞지않으면 구글 검색으로 처리
            checkVirus("https://www.google.co.kr/search?q=" + weburi);
        }
    }

    public void renew(){
        goToURL(getUrl());
    }

    private boolean CHECK_STATIC_URL(String staticURL){
        if(staticURL.equals(MAIN_URL)){
            loadUrl(MAIN_URL);
            return true;
        }
        if(URL_HASHMAP.containsKey(staticURL)){
            loadUrl(URL_HASHMAP.get(staticURL).toString());
            return true;
        }
        return false;
    }
    private void checkVirus(String url){
        String loadUrl = url;
        weburi = url;
        if(url==MAIN_URL){
            wv.loadUrl(MAIN_URL);
            return ;
        }
        if(useVulnerabilityFindAlgorithm == true && customizedWebViewManager.SECURITY_MODE_STATE == true){
            try {
                Log.d("widae", "접근 시도");
                JspAccessTask task = new JspAccessTask();
                task.getUrl = loadUrl;
                int safety = Integer.parseInt(task.execute().get());
                switch (safety){
                    case 0 : //when Race condition occurs or other exception occurs, just goURL
                        Log.d("widae", "0번 오류 : race condition or access failed");
                        resultOfsafety = SHOW_SAFETY_GREAT;
                        handler.sendMsgQuick(handler.ACCESS_SAFE);
                        return ;
                    case SAFETY_GREAT  :
                        resultOfsafety = SHOW_SAFETY_GREAT;
                        handler.sendMsgQuick(handler.ACCESS_SAFE);
                        break;
                    case SAFETY_NORMAL  :
                        resultOfsafety = SHOW_SAFETY_GREAT;
                        handler.sendMsgQuick(handler.ACCESS_SAFE);
                        break;
                    case SAFETY_EXPOSED  :
                        resultOfsafety = SHOW_SAFETY_EXPOSED;
                        handler.sendMsgQuick(handler.ACCESS_CHECK);
                        //super.onPageStarted(view, url, favicon);
                        break;
                    case SAFETY_WARNING :
                        resultOfsafety = SHOW_SAFETY_WARNING;
                        handler.sendMsgQuick(handler.ACCESS_CHECK);
                        break;
                    case SAFETY_UNACCESSABLE :
                        resultOfsafety =SHOW_SAFETY_UNACCESSABLE;
                        handler.sendMsgQuick(handler.ACCESS_DENY);
                        break;
                }
            } catch (Exception e){
                Log.d("widae", "jsp connection faile(error 101) from : "+e.getMessage());
            }
        }
        else{
            loadUrl(loadUrl);
        }
    }
    public void goToURL(){ //go to with the uri.getText
        weburi = getUriTextString();
        if(CHECK_STATIC_URL(weburi)){ return ;  } //static에 등록된 URL이면 이동하고 함수를 종료함.
        if (weburi.startsWith("http://") || weburi.startsWith("https://")) {
            checkVirus(weburi);
        } else { //프로토콜이 안 붙음
            if(weburi.indexOf('.') > 0 ){
                checkVirus("http://"+weburi);
                return;
            }
            //둘다 아니면, 즉, country와 fileformat과 맞지않으면 검색으로 치부함
            checkVirus( GOOGLE_SEARCH_URL + weburi);
        }
    }
    public void onSavePageAllScreenShot() {
        handler.sendMsgQuick(handler.SCREENSHOT_CAPTURE);
        wv.setDrawingCacheEnabled(true);
        Picture picture = wv.capturePicture();
        int captureHeight = DEVICE_HEIGHT -255;
        Bitmap saveAllScreenShot = Bitmap.createBitmap(picture.getWidth(), captureHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas( saveAllScreenShot );
        picture.draw( c );
        SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd_hhmmss");
        Date today = new Date();
        String strDate = formatter.format(today);
        String dirPath = Environment.getExternalStorageDirectory().toString() + SAVE_FOLDER;
        File file = new File(dirPath);
        if( !file.exists() )  // 원하는 경로에 폴더가 있는지 확인
            file.mkdirs();
        String localFile =  "" + dirPath + "/SC_" + strDate +".jpg";
        File hFile = new File( localFile );
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(hFile);
            saveAllScreenShot.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            handler.setLastDownloadFile(localFile);
        } catch ( Exception e){
            Log.i("error", e.getMessage());
        }


        wv.setDrawingCacheEnabled(false);
        handler.sendMsgQuick(handler.SCREENSHOT_SAVED);
        handler.sendMsgQuick(handler.IMG_SHARE);
    }

}