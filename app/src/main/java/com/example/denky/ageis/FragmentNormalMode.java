package com.example.denky.ageis;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Set;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.example.denky.ageis.ReferenceString.DEVICE_HEIGHT;
import static com.example.denky.ageis.ReferenceString.MAIN_URL;
import static com.example.denky.ageis.ReferenceString.URL_NORMAL_MODE_HINT;

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
    private ImageView homeBtn , lockBtn, settingBtn,  screenshotBtn, extendBtn;
    private ProcessContext processContext;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    private CustomizedHandler handler;
    private ViewGroup rootView;
    private CustomizedWebViewClient wvWeb;
    private CustomizedWebViewManager customizedWebViewManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    public LinearLayout bar;
    final private int TIME_OF_ANIMATION = 500;
    private boolean ANIMATION_DONE = true;

    private ImageView favoriteSiteIcon;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof ActivityMain){
            this.customizedWebViewManager = ((ActivityMain)getActivity()).getData();
        }
    }

    private void initializeValues(){

        THIS_ACTIVITY=getActivity();
        lockBtn = (ImageView)rootView.findViewById(R.id.lockBtn_normal);
        ReferenceString.initializeHashMap(); //URL맵을 초기화함(put해서 넣음)
        imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
        uri = (EditText) rootView.findViewById(R.id.uri_normal);
        wv = (CustomizedWebView) rootView.findViewById(R.id.wv_normal);
        handler = new CustomizedHandler(wv,getActivity(),processContext,lockBtn , customizedWebViewManager);
        wv.constructor(weburi, uri, handler,customizedWebViewManager);    //public void constructor(String weburi, EditText editText) 맘대로 만든 생성자
        homeBtn = (ImageView)rootView.findViewById(R.id.homeBtn_normal);
        settingBtn = (ImageView)rootView.findViewById(R.id.settingBtn_normal);
        extendBtn = (ImageView)rootView.findViewById(R.id.extendWindowBtn);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar_normal);
        screenshotBtn = (ImageView)rootView.findViewById(R.id.screenBtn_normal);
        swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayoutNormal);

        favoriteSiteIcon=(ImageView)rootView.findViewById(R.id.favoriteSite_normal);

        favoriteSiteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                favoriteSiteIcon.requestFocus();
                Toast.makeText(getContext(), "Click", Toast.LENGTH_SHORT).show();

                String linkList[]=new String[Settings.favoriteSiteList.size()];
                Iterator<String> key=Settings.favoriteSiteList.keySet().iterator();

                for(int i=0; i<Settings.favoriteSiteList.size(); i++){
                    linkList[i]=new String(Settings.favoriteSiteList.get(key.next()));
                }
                */
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //새로고침 작업 실행...
                wv.renew();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
        wvSettings = wv.getSettings();
        //device 가로 세로 구하기
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;// 가로
        int height = displayMetrics.heightPixels;// 세로
        DEVICE_HEIGHT = height;
    }

    private void initializedWv(){
        uri.setHint(URL_NORMAL_MODE_HINT);
        CustomizedWebChromeClient customizedWebChromeClient = new CustomizedWebChromeClient(progressBar, customizedWebViewManager);
        wvWeb = new CustomizedWebViewClient(wv, wvSettings, progressBar, customizedWebViewManager);
        wv.setWebViewClient(wvWeb);
        wv.setWebChromeClient(customizedWebChromeClient);
        wv.setLongClickable(true);
        wv.setOnLongClickListener(this);
        processContext = new ProcessContext(wv, handler);
        handler.setProcessContext(processContext);
        customizedWebViewManager.setNormalWebView(wv);
        registerForContextMenu(wv);
        wvWeb.setWebView();
        progressBar.setVisibility(View.INVISIBLE);
        wv.setLayerType(View.LAYER_TYPE_HARDWARE, null); //웹뷰 성능향상
    }

    public boolean isVisibleBar() {
        return isVisibleBar;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.normal_webview_fragment, container, false);

        initializeValues(); //변수들 초기화
        initializedWv(); //웹뷰 초기화
        if(customizedWebViewManager.focusOnUrlBar == false) {
            Log.d("widae","클리어 포커스!");
            uri.clearFocus();
        }
        wv.goToURL(customizedWebViewManager.NORMAL_MODE_LAST_VIEW); //처음 화면 로딩
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
                    DialogMaker.Callback shutdown=new DialogMaker.Callback() {
                        @Override
                        public void callbackMethod() {
                            getActivity().finish();
                        }
                    };
                    maker.setValue("모든 권한을 취득하지 않았기 때문에 기능을 사용할 수 없습니다. \n\n앱을 재시작하고 권한에 동의해주세요.", "", "앱 종료",shutdown, shutdown);
                    maker.show(getActivity().getSupportFragmentManager(), "TAG");
                }else{
                    switch (v.getId()){
                        case R.id.homeBtn_normal : //홈버튼 이벤트 처리
                            wv.goToURL(Settings.homeUri);
                            wv.setUri("");
                            visibleUniverseBar();
                            break;
                        case R.id.lockBtn_normal :
                            customizedWebViewManager.SECURITY_MODE_STATE = true;
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, customizedWebViewManager.getSecurityMode()).commit();
                            break;
                        case R.id.settingBtn_normal :
                            Intent appSetting = new Intent(getActivity(), ActivitySetting.class);
                            startActivity(appSetting);
                            break;
                        case R.id.screenBtn_normal :
                            wv.onSavePageAllScreenShot();
                            break;
                        case R.id.extendWindowBtn :
                            invisibleUniverseBar();
                            break;
                        case R.id.favoriteSite_normal:

                            final ArrayAdapter<String> siteListAdapter=new ArrayAdapter<String>(getContext(), R.layout.favorite_site_list);
                            siteListAdapter.addAll(Settings.favoriteSiteList.keySet());
                            Toast.makeText(getContext(), String.valueOf(siteListAdapter.getCount()), Toast.LENGTH_SHORT).show();

                            final DialogMaker favoriteSiteListDialog=new DialogMaker();

                            DialogMaker.Callback closeDialog=new DialogMaker.Callback() {
                                @Override
                                public void callbackMethod() {
                                    favoriteSiteListDialog.dismiss();
                                }
                            };
                            final DialogMaker.Callback addThisSite=new DialogMaker.Callback() {
                                @Override
                                public void callbackMethod() {
                                    //현재 사이트를 즐겨찾기에 추가
                                    //다이얼로그 하나 더 띄워서 처리
                                }
                            };

                            //If clicking element of list, go to seleted URI.
                            DialogInterface.OnClickListener listListener=new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /*
                                    Set<String> keySet=Settings.favoriteSiteList.keySet();

                                    String key[]=(String[])keySet.toArray();

                                    final String value[]=new String[Settings.favoriteSiteList.size()];
                                    for(int i=0; i<key.length; i++){
                                        value[i]=Settings.favoriteSiteList.get(key[i]);
                                    }

                                    String selectedSiteLink=value[which];
                                    wv.goToURL(selectedSiteLink);
                                    */
                                    String key=siteListAdapter.getItem(which);
                                    wv.goToURL(Settings.favoriteSiteList.get(key));
                                }
                            };

                            //Adapter가 제대로 반영이 안되고 있음.
                            //Why?
                            favoriteSiteListDialog.setValue("즐겨찾기 목록", "이 사이트 저장", "닫기", addThisSite, closeDialog, siteListAdapter, listListener);
                            favoriteSiteListDialog.show(getActivity().getSupportFragmentManager(), "Favorite Site List Dialog");
                            break;
                    }
                }

            }
        };
        lockBtn.setOnClickListener(cl);
        homeBtn.setOnClickListener(cl);
        settingBtn.setOnClickListener(cl);
        screenshotBtn.setOnClickListener(cl);
        extendBtn.setOnClickListener(cl);
        favoriteSiteIcon.setOnClickListener(cl);

        bar=(LinearLayout)rootView.findViewById(R.id.universe_normal);
        return rootView;
    }

    //bar animation over.///////////////////////////////////////////////////////////////////////////////////////////
    public void visibleUniverseBar(){
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) bar.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            bar.setLayoutParams(params);
            wv.scrollTo(0,0);
    }

    public void invisibleUniverseBar(){
        if(ANIMATION_DONE == true && !wv.getUrl().equals(MAIN_URL) ) {
            TranslateAnimation ani = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, //fromY
                    Animation.RELATIVE_TO_SELF, -1.0f);//toY

            ani.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                    ANIMATION_DONE = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) bar.getLayoutParams();
                    params.height = 0;
                    bar.setLayoutParams(params);
                    ANIMATION_DONE = true;

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            ani.setDuration(TIME_OF_ANIMATION);
            bar.startAnimation(ani);
        }
    }
    //bar animation over.///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onLongClick(View v) {
        if (v == wv) { //웹 뷰에서의 롱 터치일 때만 실행
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

}
