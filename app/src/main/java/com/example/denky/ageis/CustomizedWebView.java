package com.example.denky.ageis;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.denky.ageis.ReferenceString.MAIN_URL;


/**
 * Created by denky on 2017-06-08.
 */
public class CustomizedWebView extends WebView {
    private WebView wv = this;
    private String weburi;
    private EditText uri;
    private String country[]
            =   {".com",".co.kr", "go.kr"};
    private String fileformat[]
            =   {".php",".html", ".jsp"};

    public void constructor(String weburi, EditText editText){
        this.weburi = weburi;
        this.uri = editText;
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
    public void goToURL(){ //go to with the uri.getText
        weburi = getUriTextString();
        if(weburi.equals("")){
            loadUrl(MAIN_URL);
            return ;
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