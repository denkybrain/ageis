package com.example.denky.ageis;

/**
 * Created by denky on 2017-05-14.
 */

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.denky.ageis.FragmentNormalMode.STORAGE_READ_PERMISSON;
import static com.example.denky.ageis.FragmentNormalMode.STORAGE_WRITE_PERMISSON;
import static com.example.denky.ageis.ReferenceString.SECURITY_MODE_STATE;
import static com.example.denky.ageis.Settings.permissionDangerousSite;

// overloading
public class ActivityMain extends AppCompatActivity{

    final Activity THIS_ACTIVITY =  this;

    public static Fragment normalMode=new FragmentNormalMode();
    public static Fragment securityMode=new FragmentSecurityMode();

    private ProcessContext processContext;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast Img_toast;
            switch (msg.what) {
                case 0  :
                    Img_toast = Toast.makeText(getApplicationContext(), "이미지 다운로드 시작", Toast.LENGTH_SHORT);
                    Img_toast.show();
                    break;
                case 1 :
                    Img_toast = Toast.makeText(getApplicationContext(), "이미지 다운로드 완료", Toast.LENGTH_LONG);
                    Img_toast.show();
                    break;
                case 2 : //주소 공유
                    Intent intent_text = new Intent(Intent.ACTION_SEND);
                    //intent_text.putExtra(Intent.EXTRA_SUBJECT, "url");
                    intent_text.setType("text/plain");
                    intent_text.putExtra(Intent.EXTRA_TEXT, processContext.getUrl());
                    startActivity(Intent.createChooser(intent_text, "이 사진을 공유합니다"));
                    break;
                case 3 : //이미지 공유
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "제목");
                    // Log.d("widae", "공유할 파일 from "+processContext.getLastDownloadFile());
                    Uri uri = Uri.fromFile(new File(processContext.getLastDownloadFile()));
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.setType("image/*");
                    startActivity(Intent.createChooser(intent, "이 사진을 공유합니다"));
                    break;
                case 4 :
                    Img_toast = Toast.makeText(getApplicationContext(), "이미 파일이 존재합니다", Toast.LENGTH_LONG);
                    Img_toast.show();
                    break;
                case  5:
                    //Log.d("widae", "주소가 클립보드에 복사되었습니다.");
                    ClipboardManager clipBoard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    clipBoard.setPrimaryClip(ClipData.newPlainText("url",processContext.getUrl()));
                    Img_toast = Toast.makeText(getApplicationContext(), "주소가 복사되었습니다", Toast.LENGTH_LONG);
                    Img_toast.show();
                    break;
                case  6:
                    Img_toast = Toast.makeText(getApplicationContext(), "화면을 캡쳐하고있습니다", Toast.LENGTH_SHORT);
                    Img_toast.show();
                    break;
                case  7:
                    Img_toast = Toast.makeText(getApplicationContext(), "화면을 저장하였습니다", Toast.LENGTH_LONG);
                    Img_toast.show();
                    break;
                case 99 ://보안수준 확인
                    // Log.d("widae", "access warning : " +wv.resultOfsafety);
                    //checkAccess();
                    break;
                case 98 ://보안수준 Unaccessable
                    //  Log.d("widae", "access warning : " +wv.resultOfsafety);
                    //denyAccess();
                    break;
                case 97 ://보안수준 good
                    //  Log.d("widae", "access warning : " +wv.resultOfsafety);
                    //safeAccess();
                    break;
            }
        }
    };
    /* 수정할 것
    private void safeAccess(){
        if(wv.resultOfsafety.equals(wv.SHOW_SAFETY_GREAT)){
            wv.loadUrl(wv.getUrl());
        }
        if(wv.resultOfsafety.equals(wv.SHOW_SAFETY_NORMAL)){
            wv.loadUrl(wv.getUrl());
        }
        return ;
    }
    private void checkAccess(){
        // Log.d("widae" ,"dif : "+wv.resultOfsafety);
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
        dm.show(getSupportFragmentManager(), "tag");
        return ;
    }
    private void denyAccess(){
        DialogMaker dm = new DialogMaker();

        if(permissionDangerousSite == true && SECURITY_MODE_STATE == true){ //접근할 수 없도록 설정
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
        dm.show(getSupportFragmentManager(), "tag");
        return ;
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS); //프로그래스 바 기능 요청
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get needed permission
        getPermission();

        //load Settings
        if(ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            boolean isSuccessLoadSetting=Settings.loadSettings();
            if(isSuccessLoadSetting==false){
                Toast.makeText(getApplicationContext(), "설정값을 불러오는데 실패해였습니다.\n설정에서 초기화를 실행하세요.", Toast.LENGTH_LONG).show();
            }
        }
        //Set Fragment
        final FragmentManager manager=getSupportFragmentManager();
        final FragmentTransaction transaction=manager.beginTransaction();
        transaction.add(R.id.container, normalMode).commit();

        /*
        ImageView changeToSecurityBtn=(ImageView)findViewById(R.id.lockBtn_normal);
        changeToSecurityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction.replace(R.id.container, securityMode).commit();
            }
        });

        ImageView changeToNormalBtn=(ImageView)findViewById(R.id.lockBtn_security);
        changeToNormalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction.replace(R.id.container, normalMode).commit();
            }
        });
        */
    }
    public void getPermission(){
        final int permissonCheck_readStorage= ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        final int permissonCheck_writeStorage= ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        if(permissonCheck_readStorage==PackageManager.PERMISSION_DENIED || permissonCheck_writeStorage==PackageManager.PERMISSION_DENIED){
            DialogMaker maker=new DialogMaker();
            Callback shutdown=new Callback() {
                @Override
                public void callbackMethod() {
                    System.exit(0);
                }
            };

            Callback agree=new Callback(){
                @Override
                public void callbackMethod() {
                    //storage read permission
                    if(permissonCheck_readStorage == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(getApplicationContext(), "저장소 읽기 권한 있음", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "저장소 읽기 권한 없음", Toast.LENGTH_SHORT).show();
                        if(ActivityCompat.shouldShowRequestPermissionRationale(THIS_ACTIVITY, READ_EXTERNAL_STORAGE)){
                            Toast.makeText(getApplicationContext(), "앱 사용을 위해 저장소 읽기 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                            ActivityCompat.requestPermissions(THIS_ACTIVITY, new String[]{READ_EXTERNAL_STORAGE}, STORAGE_READ_PERMISSON);
                        }else{
                            ActivityCompat.requestPermissions(THIS_ACTIVITY, new String[]{READ_EXTERNAL_STORAGE}, STORAGE_READ_PERMISSON);
                        }
                    }

                    //storage write permission
                    if(permissonCheck_writeStorage == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(getApplicationContext(), "저장소 쓰기 권한 있음", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "저장소 쓰기 권한 없음", Toast.LENGTH_SHORT).show();
                        if(ActivityCompat.shouldShowRequestPermissionRationale(THIS_ACTIVITY, WRITE_EXTERNAL_STORAGE)){
                            Toast.makeText(getApplicationContext(), "앱 사용을 위해 저장소 쓰기 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                            ActivityCompat.requestPermissions(THIS_ACTIVITY, new String[]{WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_PERMISSON);
                        }else{
                            ActivityCompat.requestPermissions(THIS_ACTIVITY, new String[]{WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_PERMISSON);
                        }
                    }

                }
            };
            String neededPermission="Read/Write Storage Permission";

            maker.setValue("다음의 권한을 취득해야 합니다.\n\n-"+neededPermission+"\n\n모든 권한을 취득하지 않으면 앱 사용이 불가능합니다.\n\n","알겠습니다", "앱 종료", agree, shutdown);
            maker.show(getSupportFragmentManager(), "FRAG");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    /*
    public void onBackPressed() { //뒤로가기 버튼 누르면 뒤로감
        if (wv.getUrl().equals(MAIN_URL)) {//현재가 초기 페이지면 앱을 종료
            if(SECURITY_MODE_STATE == true) { //시큐리티 모드면 웹뷰의 기록을 파괴하고 어플 종료
                wv.clearHistory();
                wv.clearCache(true);
            }
            finish();
            super.onBackPressed();
        } else { //현재가 초기 페이지가 아니라 로딩 페이지면 앱을 종료하지않고 뒤로감
            //뒤로갈 url 구하기
            //WebBackForwardList webBackForwardList = wv.copyBackForwardList();
            //String backUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();//뒤로갈
            //
            wv.goBack();
        }
    }*/
}