package com.example.denky.ageis;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import static com.example.denky.ageis.Settings.permissionDangerousSite;

/**
 * Created by denky on 2017-06-19.
 */

public class CustomizedHandler extends Handler {
    private CustomizedWebView wv;
    private FragmentActivity activity;
    private ProcessContext processContext;
    private ImageView lockBtn;
    private CustomizedWebViewManager customizedWebViewManager;
    private String lastDownloadFile = "";

    //사용할 객체만 받아옴

    public final int IMG_DOWNLOAD_START = 0 ;
    public final int IMG_DOWNLOAD_FINISHED = 1 ;
    public final int ANCHOR_SHARE = 2;
    public final int IMG_SHARE = 3;
    public final int IMG_ALREADY_EXIST = 4;
    public final int ANCHOR_COPY = 5;
    public final int SCREENSHOT_CAPTURE = 6;
    public final int SCREENSHOT_SAVED =  7;
    public final int MEDIA_SCAN =  8;
    public final int IMG_DOWNLOAD_FAILED = 9 ;
    public final int ACCESS_SAFE = 97;
    public final int ACCESS_DENY = 98;
    public final int ACCESS_CHECK = 99;

    public String getLastDownloadFile() {
        return lastDownloadFile;
    }

    public void setLastDownloadFile(String lastDownloadFile) {
        this.lastDownloadFile = lastDownloadFile;
    }

    CustomizedHandler(CustomizedWebView wv, FragmentActivity activity, ProcessContext processContext, ImageView lockBtn
    ,CustomizedWebViewManager customizedWebViewManager                      ){
        this.wv = wv;
        this.activity =activity;
        this.processContext = processContext;
        this.lockBtn = lockBtn;
        this.customizedWebViewManager =customizedWebViewManager;
    }
    public void sendMsgQuick(int data){
        Message msg = obtainMessage();
        msg = obtainMessage();
        msg.what = data; //저장 완료했다고 띄움
        sendMessage(msg);
    }

    public void setProcessContext(ProcessContext processContext) {
        this.processContext = processContext;
    }

    @Override
    public void handleMessage(Message msg) {
        Toast Img_toast;
       // Log.d("widae", msg.what+"");
        switch (msg.what) {
            case IMG_DOWNLOAD_START  :
                Img_toast = Toast.makeText(activity.getApplicationContext(), "이미지 다운로드 시작", Toast.LENGTH_SHORT);
                Img_toast.show();
                break;
            case IMG_DOWNLOAD_FINISHED :
                Img_toast = Toast.makeText(activity.getApplicationContext(), "이미지 다운로드 완료", Toast.LENGTH_LONG);
                Img_toast.show();
                break;
            case ANCHOR_SHARE : //주소 공유
                Intent intent_text = new Intent(Intent.ACTION_SEND);
                //intent_text.putExtra(Intent.EXTRA_SUBJECT, "url");
                intent_text.setType("text/plain");
                intent_text.putExtra(Intent.EXTRA_TEXT, processContext.getUrl());
                activity.startActivity(Intent.createChooser(intent_text, "이 주소를 공유합니다"));
                break;
            case IMG_SHARE : //이미지 공유
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, "제목");
                // Log.d("widae", "공유할 파일 from "+processContext.getLastDownloadFile());
                Uri uri = Uri.fromFile(new File(getLastDownloadFile()));
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("image/*");
                activity.startActivity(Intent.createChooser(intent, "이 사진을 공유합니다"));
                break;
            case IMG_ALREADY_EXIST :
                Img_toast = Toast.makeText(activity.getApplicationContext(), "이미 파일이 존재합니다", Toast.LENGTH_LONG);
                Img_toast.show();
                break;
            case ANCHOR_COPY : //주소 복사
                //Log.d("widae", "주소가 클립보드에 복사되었습니다.");
                ClipboardManager clipBoard = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
                clipBoard.setPrimaryClip(ClipData.newPlainText("url",processContext.getUrl()));
                Img_toast = Toast.makeText(activity.getApplicationContext(), "주소가 복사되었습니다", Toast.LENGTH_LONG);
                Img_toast.show();
                break;
            case  SCREENSHOT_CAPTURE : //스크린샷
                Img_toast = Toast.makeText(activity.getApplicationContext(), "화면을 캡쳐하고있습니다", Toast.LENGTH_SHORT);
                Img_toast.show();
                break;
            case SCREENSHOT_SAVED: //화면 저장
                Img_toast = Toast.makeText(activity.getApplicationContext(), "화면을 저장하였습니다", Toast.LENGTH_LONG);
                Img_toast.show();
                break;
            case MEDIA_SCAN :
                mediaScan();
                break;
            case IMG_DOWNLOAD_FAILED :
                Img_toast = Toast.makeText(activity.getApplicationContext(), "이미지 다운로드 실패", Toast.LENGTH_LONG);
                Img_toast.show();
                break;
            case ACCESS_CHECK ://접근 허가요청
                // Log.d("widae", "access warning : " +wv.resultOfsafety);
                checkAccess();
                break;
            case ACCESS_DENY ://접근 불가
                //  Log.d("widae", "access warning : " +wv.resultOfsafety);
                denyAccess();
                break;
            case ACCESS_SAFE ://안전접근
                //Log.d("widae", "access warning : " +wv.resultOfsafety);
                safeAccess();
                break;

        }
    }
    private void mediaScan(){
        String filename = "file://" + getLastDownloadFile();
        Log.d("widae", "스캐닝 파일 : "+filename);
        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(filename)));
    }
    private void safeAccess(){
        if(wv.resultOfsafety.equals(wv.SHOW_SAFETY_GREAT))
            wv.loadUrl(wv.weburi);
        if(wv.resultOfsafety.equals(wv.SHOW_SAFETY_NORMAL)){
            wv.loadUrl(wv.weburi);
        }
        lockBtn.setImageResource(R.drawable.lockwhite);
        return ;
    }

    private void checkAccess(){
        DialogMaker dm = new DialogMaker();
        com.example.denky.ageis.Callback okay = new com.example.denky.ageis.Callback() {
            @Override
            public void callbackMethod() {
                if(wv.resultOfsafety.equals(wv.SHOW_SAFETY_EXPOSED)){
                    //     Log.d("widae", "handler : exposed");
                    wv.loadUrl(wv.weburi);
                    lockBtn.setImageResource(R.drawable.lockexposed);
                }
                if(wv.resultOfsafety.equals(wv.SHOW_SAFETY_WARNING)){
                    wv.loadUrl(wv.weburi);
                    lockBtn.setImageResource(R.drawable.lockwarning);
                }
                return ;
            }
        };
        com.example.denky.ageis.Callback cancel = new com.example.denky.ageis.Callback() {
            @Override
            public void callbackMethod() {
            }
        };
        dm.setValue("사이트의 보안 수준이 "+wv.resultOfsafety+"입니다. 접근하시겠습니까?", "취소", "접근",cancel, okay);
        dm.show(activity.getSupportFragmentManager(), "tag");
        return ;
    }
    private void denyAccess(){
        DialogMaker dm = new DialogMaker();

        if(permissionDangerousSite == true && customizedWebViewManager.SECURITY_MODE_STATE == true){ //접근할 수 없도록 설정
            com.example.denky.ageis.Callback okay = new com.example.denky.ageis.Callback() {
                @Override
                public void callbackMethod() {
                }
            };
            com.example.denky.ageis.Callback cancel = new com.example.denky.ageis.Callback() {
                @Override
                public void callbackMethod() {
                }
            };
            dm.setValue("사이트의 보안 수준이 "+wv.resultOfsafety+"입니다. 접근할 수 없습니다.", "확인", "",cancel, null);

        }else{ //접근할 수 있도록 설정
            com.example.denky.ageis.Callback okay = new com.example.denky.ageis.Callback() {
                @Override
                public void callbackMethod() {
                    wv.loadUrl(wv.weburi);
                    lockBtn.setImageResource(R.drawable.lockwarning);
                }
            };
            com.example.denky.ageis.Callback cancel = new com.example.denky.ageis.Callback() {
                @Override
                public void callbackMethod() {
                }
            };
            dm.setValue("사이트의 보안 수준이 "+wv.resultOfsafety+"입니다. 접근하시겠습니까?","취소", "접근",cancel, okay);
        }
        dm.show(activity.getSupportFragmentManager(), "tag");
        return ;
    }
}
