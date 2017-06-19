package com.example.denky.ageis;

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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.example.denky.ageis.ActivityMain.customizedWebViewManager;
import static com.example.denky.ageis.ReferenceString.DEVICE_HEIGHT;
import static com.example.denky.ageis.ReferenceString.MAIN_URL;
import static com.example.denky.ageis.ReferenceString.NORMAL_MODE_LAST_VIEW;
import static com.example.denky.ageis.ReferenceString.SECURITY_MODE_STATE;

public class FragmentNormalMode extends Fragment implements View.OnLongClickListener{
    private boolean isVisibleBar=true;
    private Activity THIS_ACTIVITY ;
    private EditText uri; //요즘은 URL가 아니라 URI, uniform resource identifier라고 부름
    private CustomizedWebView  wv;
    private WebSettings wvSettings; //webview setting 객체임. 편리하게 쓰려고 만듬
    private View.OnClickListener cl; //여러 개의 클릭 리스너를 switch로 처리하려고 만듬
    private String weburi ="";
    public ProgressBar progressBar;
    private InputMethodManager imm; //엔터키 입력 매니지를 위한 객체
    private ImageView homeBtn , lockBtn, settingBtn, renewBtn, screenshotBtn;
    private LinearLayout universe;
    private RelativeLayout gotoBar;
    private ProcessContext processContext;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    private CustomizedHandler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup)inflater.inflate(R.layout.normal_webview_fragment, container, false);
        THIS_ACTIVITY=getActivity();

        lockBtn = (ImageView)rootView.findViewById(R.id.lockBtn_normal);
        ReferenceString.initializeHashMap(); //URL맵을 초기화함(put해서 넣음)
        imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
        uri = (EditText) rootView.findViewById(R.id.uri_normal);
        wv = (CustomizedWebView) rootView.findViewById(R.id.wv_normal);
        handler = new CustomizedHandler(wv,getActivity(),processContext,lockBtn);
        wv.constructor(weburi, uri, handler);    //public void constructor(String weburi, EditText editText) 맘대로 만든 생성자
        homeBtn = (ImageView)rootView.findViewById(R.id.homeBtn_normal);
        gotoBar = (RelativeLayout)rootView.findViewById(R.id.gotoBar_normal);
        universe = (LinearLayout)rootView.findViewById(R.id.universe_normal);
        settingBtn = (ImageView)rootView.findViewById(R.id.settingBtn_normal);
        renewBtn = (ImageView)rootView.findViewById(R.id.renewBtn_normal);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar_normal);
        screenshotBtn = (ImageView)rootView.findViewById(R.id.screenBtn_normal);
        wvSettings = wv.getSettings();

        /* display의 가로 세로 구하기 */
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;// 가로
        int height = displayMetrics.heightPixels;// 세로
        DEVICE_HEIGHT = height;
        /* */

        CustomizedWebViewClient wvWeb = new CustomizedWebViewClient(wv, wvSettings, progressBar, uri, weburi);
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
        handler.setProcessContext(processContext);
        customizedWebViewManager.setNormalWebView(wv);
        registerForContextMenu(wv);
        wvWeb.setWebView();
        //progressBar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN); //
        //progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);//Prgress bar color change
        progressBar.setVisibility(View.INVISIBLE);
        wv.goToURL(NORMAL_MODE_LAST_VIEW); //처음 화면 로딩
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
                if(ContextCompat.checkSelfPermission(THIS_ACTIVITY, READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(THIS_ACTIVITY, WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                    DialogMaker maker=new DialogMaker();
                    Callback shutdown=new Callback() {
                        @Override
                        public void callbackMethod() {
                            System.exit(0);
                        }
                    };
                    maker.setValue("모든 권한을 취득하지 않았기 때문에 기능을 사용할 수 없습니다. \n\n앱을 재시작하고 권한에 동의해주세요.", "", "앱 종료",shutdown, shutdown);
                    maker.show(getActivity().getSupportFragmentManager(), "TAG");
                }else{
                    switch (v.getId()){
                        case R.id.homeBtn_normal : //홈버튼 이벤트 처리
                            wv.goToURL(MAIN_URL);
                            wv.setUri("");
                            break;
                        case R.id.lockBtn_normal :
                            if(SECURITY_MODE_STATE == false) {
                                //Security Mode
                                SECURITY_MODE_STATE = true;
                            }
                            else{
                                //Normal Mode
                                SECURITY_MODE_STATE = false;
                            }
                            break;

                        case R.id.settingBtn_normal :
                            Intent appSetting = new Intent(getActivity(), ActivitySetting.class);
                            startActivity(appSetting);
                            break;
                        case R.id.renewBtn_normal :
                            wv.renew();
                            break;
                        case R.id.screenBtn_normal :
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

        /////////////////////////////////////////Hide Navigation Function////////////////////////////////////////////
        final LinearLayout bar=(LinearLayout)rootView.findViewById(R.id.universe_normal);
        final FrameLayout fragmentContainer=(FrameLayout)rootView.findViewById(R.id.container);
        final CustomizedWebView wv=(CustomizedWebView)rootView.findViewById(R.id.wv_normal);
        final RelativeLayout relativeLayout=(RelativeLayout)rootView.findViewById(R.id.normalWebView);
        ImageView hideBarBt=(ImageView)rootView.findViewById(R.id.hideVarBtn_normal);
        hideBarBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisibleBar == true) {
                    isVisibleBar = false;
                    ViewGroup.LayoutParams params=(ViewGroup.LayoutParams)bar.getLayoutParams();
                    params.height=0;
                    bar.setLayoutParams(params);
                    //fragmentContainer.getLayoutParams().height= FrameLayout.LayoutParams.MATCH_PARENT;
                    //Toast.makeText(getActivity().getApplicationContext(), "Hide Bar", Toast.LENGTH_LONG).show();
                } else {
                    isVisibleBar = true;
                    ViewGroup.LayoutParams params=(ViewGroup.LayoutParams)bar.getLayoutParams();
                    params.height=LinearLayout.LayoutParams.WRAP_CONTENT;
                    bar.setLayoutParams(params);
                    //fragmentContainer.getLayoutParams().height= FrameLayout.LayoutParams.MATCH_PARENT;
                    //Toast.makeText(getActivity().getApplicationContext(), "Appear Bar", Toast.LENGTH_LONG).show();
                }
            }
        });
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////Change to security Mode////////////////////////////////////////////
        ImageView changeToSecurityBtn=(ImageView)rootView.findViewById(R.id.lockBtn_normal);
        changeToSecurityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SECURITY_MODE_STATE = true;
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, ActivityMain.securityMode).commit();
            }
        });
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return rootView;
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == wv) { //웹 뷰에서의 롱 터치일 때만 실행

            // Log.d("widae", "long click listener activated");
            WebView.HitTestResult hitTestResult = wv.getHitTestResult();
            processContext.longClickEvent(hitTestResult, hitTestResult.getExtra());
        }
        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 롱클릭했을 때 나오는 context Menu 의 항목을 선택(클릭) 했을 때 호출
        processContext.onContextItemSelected(item.getItemId());
        return super.onContextItemSelected(item);
    }

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
    private void LogPrintWebBackList(int index){
        WebBackForwardList webBackForwardList = wv.copyBackForwardList();
        String backUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - index).getUrl();
        Log.d("widae", "-1"+index+":"+backUrl);
    }
}
