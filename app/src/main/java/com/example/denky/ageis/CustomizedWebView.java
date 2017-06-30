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
import static com.example.denky.ageis.ReferenceString.URL_HASHMAP;


/**
 * Created by denky on 2017-06-08.
 */
public class CustomizedWebView extends WebView {
    private final String SAVE_FOLDER = File.separator+"Ageis"+File.separator+"Ageis_screenshot";
    private WebView wv = this;
    private String weburi;
    private EditText uri;
    private String country[]
            =   {".com",".co.kr", "go.kr"};
    private String fileformat[]
            =   {".php",".html", ".jsp"};
    public Handler handler;

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
        wv.loadUrl(link);
    }
    public void renew(){
        goToURL();
    }

    private boolean checkURL(String myURL, String staticURL){
        if(myURL.equals(staticURL))
            return true;
        return false;
    }
    private boolean CHECK_STATIC_URL(String staticURL){
        if(URL_HASHMAP.containsKey(staticURL)){
            goToURL(URL_HASHMAP.get(staticURL).toString());
            return true;
        }
        return false;
    }
    public void goToURL(){ //go to with the uri.getText
        weburi = getUriTextString();
        if(CHECK_STATIC_URL(weburi)){
            return ; //static에 등록된 URL이면 이동하고 함수를 종료함.
        }
        if (weburi.startsWith("http://")) {
            wv.loadUrl(weburi);
        } else {
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
            //둘다 아니면, 즉, country와 fileformat과 맞지않으면 검색으로 치부함
            wv.loadUrl("https://www.google.co.kr/search?q=" + weburi);
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