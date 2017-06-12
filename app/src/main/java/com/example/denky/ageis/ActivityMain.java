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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.denky.ageis.ReferenceString.DEVICE_HEIGHT;
import static com.example.denky.ageis.ReferenceString.URL_SECURITY_MODE_HINT;
import static com.example.denky.ageis.ReferenceString.SECURITY_MODE_STATE;
import static com.example.denky.ageis.ReferenceString.MAIN_URL;
import static com.example.denky.ageis.ReferenceString.URL_NORMAL_MODE_HINT;

// overloading
public class ActivityMain extends AppCompatActivity implements View.OnLongClickListener {

    private EditText uri; //요즘은 URL가 아니라 URI, uniform resource identifier라고 부름
    private CustomizedWebView  wv;
    private WebSettings wvSettings; //webview setting 객체임. 편리하게 쓰려고 만듬
    private View.OnClickListener cl; //여러 개의 클릭 리스너를 switch로 처리하려고 만듬
    private String weburi ="";
    public ProgressBar progressBar;
    private InputMethodManager imm; //엔터키 입력 매니지를 위한 객체
    private ImageView homeBtn , lockBtn, settingBtn, renewBtn, screenshotBtn;
    private LinearLayout  universe;
    private RelativeLayout gotoBar;
    private ProcessContext processContext;
    CustomizedWebViewClient wvWeb;
    static final int STORAGE_READ_PERMISSON=100;
    static final int STORAGE_WRITE_PERMISSON=101;
    final Activity THIS_ACTIVITY =  this;
    DisplayMetrics displayMetrics = new DisplayMetrics();
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
                    checkAccess();
                    break;
                case 98 ://보안수준 Unaccessable
                    //  Log.d("widae", "access warning : " +wv.resultOfsafety);
                    denyAccess();
                    break;
                case 97 ://보안수준 good
                    //  Log.d("widae", "access warning : " +wv.resultOfsafety);
                    safeAccess();
                    break;
            }
        }
    };
    private void safeAccess(){
        if(wv.resultOfsafety.equals(wv.SHOW_SAFETY_GREAT)){
            wv.loadUrl(wv.getUrl());
            lockBtn.setImageResource(R.drawable.lockwhite);
        }
        if(wv.resultOfsafety.equals(wv.SHOW_SAFETY_NORMAL)){
            wv.loadUrl(wv.getUrl());
            lockBtn.setImageResource(R.drawable.locknormal);
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
        dm.show(getSupportFragmentManager(), "tag");
        return ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS); //프로그래스 바 기능 요청
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get needed permission
        getPermission();
        //load Settings
        if(ContextCompat.checkSelfPermission(THIS_ACTIVITY, READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(THIS_ACTIVITY, WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            Settings.loadSettings();
        }
        ReferenceString.initializeHashMap(); //URL맵을 초기화함(put해서 넣음)
        //settingRead();
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        uri = (EditText) findViewById(R.id.uri);
        wv = (CustomizedWebView) findViewById(R.id.wv);
        wv.constructor(weburi, uri, handler);    //public void constructor(String weburi, EditText editText) 맘대로 만든 생성자
        homeBtn = (ImageView)findViewById(R.id.homeBtn);
        lockBtn = (ImageView)findViewById(R.id.lockBtn);
        gotoBar = (RelativeLayout)findViewById(R.id.gotoBar);
        universe = (LinearLayout)findViewById(R.id.universe);
        settingBtn = (ImageView)findViewById(R.id.settingBtn);
        renewBtn = (ImageView)findViewById(R.id.renewBtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        screenshotBtn = (ImageView)findViewById(R.id.screenBtn);
        wvSettings = wv.getSettings();

        /* display의 가로 세로 구하기 */
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;// 가로
        int height = displayMetrics.heightPixels;// 세로
        DEVICE_HEIGHT = height;
        /* */

        wvWeb = new CustomizedWebViewClient(wv, wvSettings, progressBar, uri, weburi , handler);
        wv.setWebViewClient(wvWeb);
        wv.setWebChromeClient(new WebChromeClient() { //Progress bar 체인지를 위한 ChromeClient
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });
        wv.setLongClickable(true);
        wv.setOnLongClickListener(this);
        processContext = new ProcessContext(wv, handler);
        registerForContextMenu(wv);
        wvWeb.setWebView();
        //progressBar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN); //
        //progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);//Prgress bar color change
        progressBar.setVisibility(View.INVISIBLE);
        wv.goToURL(MAIN_URL); //처음 화면 로딩
        uri.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {  //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    imm.hideSoftInputFromWindow(uri.getWindowToken(), 0);
                    wv.goToURL();
                }
                return false;
            }
        });

        cl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(THIS_ACTIVITY, READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(THIS_ACTIVITY, WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                    DialogMaker maker=new DialogMaker();
                    Callback shutdown=new Callback() {
                        @Override
                        public void callbackMethod() {
                            System.exit(0);
                        }
                    };
                    maker.setValue("모든 권한을 취득하지 않았기 때문에 기능을 사용할 수 없습니다. \n\n앱을 재시작하고 권한에 동의해주세요.", "", "앱 종료",shutdown, shutdown);
                    maker.show(getSupportFragmentManager(), "TAG");
                }else{
                    switch (v.getId()){
                        case R.id.homeBtn : //홈버튼 이벤트 처리
                            wv.goToURL(MAIN_URL);
                            wv.setUri("");
                            break;
                        case R.id.lockBtn :
                            if(SECURITY_MODE_STATE == false) {
                                wv.renew();
                                uri.setHint(URL_SECURITY_MODE_HINT); //시큐리티 모드로 만들기
                                uri.setBackgroundResource(R.color.supergrey);
                                uri.setTextColor(Color.WHITE);
                                wv.setUri("");
                                lockBtn.setImageResource(R.drawable.lockwhite);
                                renewBtn.setImageResource(R.drawable.returnbtn);
                                gotoBar.setBackgroundResource(R.color.supergrey);
                                homeBtn.setImageResource(R.drawable.home2);
                                screenshotBtn.setImageResource(R.drawable.screenwhite);
                                settingBtn.setImageResource(R.drawable.setting2);
                                universe.setBackgroundResource(R.color.supergrey);
                                SECURITY_MODE_STATE = true;
                            }
                            else{
                                //기본 모드
                                wv.goToURL(MAIN_URL);
                                uri.setHint(URL_NORMAL_MODE_HINT);
                                uri.setBackgroundResource(R.color.white);
                                uri.setTextColor(Color.BLACK);
                                lockBtn.setImageResource(R.drawable.lock);
                                homeBtn.setImageResource(R.drawable.home);
                                settingBtn.setImageResource(R.drawable.setting);
                                gotoBar.setBackgroundResource(R.color.white);
                                universe.setBackgroundResource(R.color.white);
                                renewBtn.setImageResource(R.drawable.returnbtnblack);
                                screenshotBtn.setImageResource(R.drawable.screenbtn);
                                SECURITY_MODE_STATE = false;
                            }
                            break;
                        case R.id.settingBtn :
                            Intent appSetting = new Intent(ActivityMain.this, ActivitySetting.class);
                            startActivity(appSetting);
                            break;
                        case R.id.renewBtn :
                            wv.renew();
                            break;
                        case R.id.screenBtn :
                            wv.onSavePageAllScreenShot();
                            break;
                    }
                }

            }
        };
        lockBtn.setOnClickListener(cl);
        homeBtn.setOnClickListener(cl);
        settingBtn.setOnClickListener(cl);
        renewBtn.setOnClickListener(cl);
        screenshotBtn.setOnClickListener(cl);
    }
    /* context Menu */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // API버전 올라가서 컨텍스트 메뉴 열 때마다 실행되는 콜벡 함수
        switch (processContext.getMenuBtnVolume()){
            case 0 : //이미지나 하이퍼링크가 아니면 컨텍스트 메뉴를 실행하지않음
                break;
            case 2 : //image
                menu.setHeaderTitle(processContext.getContextTitle());
                menu.add(0,1,100,processContext.getContextfirstMenu());
                menu.add(0,2,100,processContext.getContextsecondMenu());
                break;
            case 3 : //anchor image or anchor tag
                menu.setHeaderTitle(processContext.getContextTitle());
                menu.add(0,1,100,processContext.getContextfirstMenu());
                menu.add(0,2,100,processContext.getContextsecondMenu());
                menu.add(0,3,100,processContext.getContextthirdMenu());
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 롱클릭했을 때 나오는 context Menu 의 항목을 선택(클릭) 했을 때 호출
        processContext.onContextItemSelected(item.getItemId());
        return super.onContextItemSelected(item);
    }
    /* context Menu */
    @Override
    public boolean onLongClick(View v) {
        if (v == wv) { //웹 뷰에서의 롱 터치일 때만 실행

         // Log.d("widae", "long click listener activated");
            WebView.HitTestResult hitTestResult = wv.getHitTestResult();
            processContext.longClickEvent(hitTestResult, hitTestResult.getExtra());
        }
        return false;
    }

    private long time=0;

    @Override
    public void onBackPressed() { //뒤로가기 버튼 누르면 뒤로감
        if (wv.getUrl().equals(MAIN_URL)) {//현재가 초기 페이지면 앱을 종료
            if(SECURITY_MODE_STATE == true) { //시큐리티 모드면 웹뷰의 기록을 파괴하고 어플 종료
                wv.clearHistory();
                wv.clearCache(true);
            }
            //뒤로가기 버튼을 두 번 눌러야 종료되는 함수
            /*
            if(System.currentTimeMillis()-time>=1500){
                time=System.currentTimeMillis();
                Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
            }else if(System.currentTimeMillis()-time<1500){
                finish();
                super.onBackPressed();
            }
            */
            finish();
            super.onBackPressed();
        } else { //현재가 초기 페이지가 아니라 로딩 페이지면 앱을 종료하지않고 뒤로감
            /*뒤로갈 url 구하기
            WebBackForwardList webBackForwardList = wv.copyBackForwardList();
            String backUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();//뒤로갈
            */
            wv.goBack();
        }
    }
    private void LogPrintWebBackList(int index){
        WebBackForwardList webBackForwardList = wv.copyBackForwardList();
        String backUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - index).getUrl();
        Log.d("widae", "-1"+index+":"+backUrl);
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

}