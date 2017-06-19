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
import android.webkit.WebView;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.denky.ageis.ReferenceString.DEVICE_HEIGHT;
import static com.example.denky.ageis.ReferenceString.MAIN_URL;
import static com.example.denky.ageis.ReferenceString.SECURITY_MODE_STATE;
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
    public Handler handler;
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

    public void constructor(String weburi, EditText editText, Handler handler){
        this.weburi = weburi;
        this.uri = editText;
        this.handler = handler;
    }

    public CustomizedWebView(Context context) {
        super(context);
    }
    public CustomizedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        Log.d("scrolled : ", l + ":" + t + ":" + oldl+":"+oldt);
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
        Log.d("widae", weburi);
        if(CHECK_STATIC_URL(weburi)){ return ;  } //static에 등록된 URL이면 이동하고 함수를 종료함.
        if (weburi.startsWith("http://")) {
            checkVirus(weburi);
        } else {
            if(weburi.indexOf('.') > 0 ){
                checkVirus("http://"+weburi);
                return;
            }
            //둘다 아니면, 즉, country와 fileformat과 맞지않으면 검색으로 치부함
            checkVirus("https://www.google.co.kr/search?q=" + weburi);
        }
    }

    public void renew(){
        goToURL();
    }

    private boolean CHECK_STATIC_URL(String staticURL){
      //  Log.d("widae","찾느중이다 게이야 ㄱㄷ리라");
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
        //  Log.d("widae", "Check URL : "+loadUrl);
        if(url==MAIN_URL){
            wv.loadUrl(MAIN_URL);
            return ;
        }
        if(useVulnerabilityFindAlgorithm == true && SECURITY_MODE_STATE == true){
            try {
                Log.d("widae", "접근 시도");
                Message msg;
                JspAccessTask task = new JspAccessTask();
                task.getUrl = loadUrl;
                int safety = Integer.parseInt(task.execute().get());
                //  Log.d("widae", "url and safety : "+loadUrl +":"+safety);
                switch (safety){
                    case 0 : //when Race condition occurs or other exception occurs, goURL
                        Log.d("widae", "0번 오류 : race condition or access failed");
                        goToURL(loadUrl);
                        resultOfsafety = SHOW_SAFETY_GREAT;
                        msg = handler.obtainMessage();
                        msg.what = 97; //안전한 사이트 이동
                        handler.sendMessage(msg);
                        return ;
                    case SAFETY_GREAT  :
                        goToURL(loadUrl);
                        resultOfsafety = SHOW_SAFETY_GREAT;
                        msg = handler.obtainMessage();
                        msg.what = 97; //안전한 사이트 이동
                        handler.sendMessage(msg);
                        break;
                    case SAFETY_NORMAL  :
                        goToURL(loadUrl);
                        resultOfsafety = SHOW_SAFETY_GREAT;
                        msg = handler.obtainMessage();
                        msg.what = 97; //안전한 사이트 이동
                        handler.sendMessage(msg);
                        break;
                    case SAFETY_EXPOSED  :
                        //   Log.d("widae", "개좆같은 exposed다");
                        resultOfsafety = SHOW_SAFETY_EXPOSED;
                        msg = handler.obtainMessage();
                        msg.what = 99; //저장 완료했다고 띄움
                        handler.sendMessage(msg);
                        //super.onPageStarted(view, url, favicon);
                        break;
                    case SAFETY_WARNING :
                        resultOfsafety = SHOW_SAFETY_WARNING;
                        msg = handler.obtainMessage();
                        msg.what = 99; //저장 완료했다고 띄움
                        handler.sendMessage(msg);
                        break;
                    case SAFETY_UNACCESSABLE :
                        resultOfsafety =SHOW_SAFETY_UNACCESSABLE;
                        msg = handler.obtainMessage();
                        msg.what = 98; //저장 완료했다고 띄움
                        handler.sendMessage(msg);
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
        if (weburi.startsWith("http://")) {
            checkVirus(weburi);
        } else { //프로토콜이 안 붙음
            /*
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
            */
            if(weburi.indexOf('.') > 0 ){
                checkVirus("http://"+weburi);
                return;
            }
            //둘다 아니면, 즉, country와 fileformat과 맞지않으면 검색으로 치부함
            checkVirus("https://www.google.co.kr/search?q=" + weburi);
        }
    }
    public void onSavePageAllScreenShot() {
        Message msg = handler.obtainMessage();
        msg.what = 6; //캡쳐한다고 띄움
        handler.sendMessage(msg);
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
        File hFile = new File( "" + dirPath + "/SC_" + strDate +".jpg" );
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(hFile);
            saveAllScreenShot.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch ( Exception e){
            Log.i("error", e.getMessage());
        }

        wv.setDrawingCacheEnabled(false);
        msg = handler.obtainMessage();
        msg.what = 7; //저장 완료했다고 띄움
        handler.sendMessage(msg);
    }



    static public void MakeCache(View v, String filename){ //오픈 소스 가져옴 아직 안 씀
        //src : http://blog.jusun.org/archives/6

        String StoragePath =
                Environment.getExternalStorageDirectory().getAbsolutePath();
        String savePath = StoragePath;
        File f = new File(savePath);
        if (!f.isDirectory())f.mkdirs();

        v.buildDrawingCache();
        Bitmap bitmap = v.getDrawingCache();
        FileOutputStream fos;
        try{
            /*
            fos = new FileOutputStream(savePath+&quot;/&quot;+filename);
            bitmap.compress(Bitmap.CompressFormat <a href="http://biturlz.com/DFMr7yC">cherche viagra a vendre</a>.JPEG,80,fos);
            */
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}