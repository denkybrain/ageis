package com.example.denky.ageis;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.example.denky.ageis.ReferenceString.DEVICE_HEIGHT;
import static com.example.denky.ageis.ReferenceString.MAIN_URL;
import static com.example.denky.ageis.ReferenceString.TIME_OF_ANIMATION;
import static com.example.denky.ageis.ReferenceString.URL_NORMAL_MODE_HINT;

public class FragmentNormalMode extends Fragment implements View.OnLongClickListener{
    private Activity THIS_ACTIVITY ;
    private EditText uri; //요즘은 URL가 아니라 URI, uniform resource identifier라고 부름
    private CustomizedWebView  wv;
    private WebSettings wvSettings; //webview setting 객체임. 편리하게 쓰려고 만듬
    private View.OnClickListener cl; //여러 개의 클릭 리스너를 switch로 처리하려고 만듬
    private String weburi ="";
    public ProgressBar progressBar;
    private InputMethodManager imm; //엔터키 입력 매니지를 위한 객체
    private ImageView homeBtn , lockBtn, settingBtn, favortieSiteBtn;
    private ProcessContext processContext;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    private CustomizedHandler handler;
    private ViewGroup rootView;
    private CustomizedWebViewClient wvWeb;
    private CustomizedWebViewManager customizedWebViewManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    public LinearLayout bar;
    private boolean ANIMATION_DONE = true;
    private PopupMenu.OnMenuItemClickListener menuItemClickListener;

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
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar_normal);
        swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayoutNormal);
        favortieSiteBtn=(ImageView)rootView.findViewById(R.id.favoriteSite_normal);

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
        CustomizedWebChromeClient customizedWebChromeClient = new CustomizedWebChromeClient(progressBar, customizedWebViewManager, handler);
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
    private void initializeSettingPopup(){
        menuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.menu_capture :
                        wv.onSavePageAllScreenShot();
                        break;
                    case R.id.menu_extend :
                        invisibleUniverseBar();
                        break;
                    case R.id.menu_detailSetting :
                        Intent appSetting = new Intent(getActivity(), ActivitySetting.class);
                        startActivity(appSetting);
                        break;
                    case R.id.appShutDown:
                        getActivity().finish();
                        break;
                }
                return false;
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.normal_webview_fragment, container, false);

        initializeValues(); //변수들 초기화
        initializedWv(); //웹뷰 초기화
        initializeSettingPopup();

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
                    maker.setValue("모든 권한을 취득하지 않았기 때문에 기능을 사용할 수 없습니다. \n\n앱을 재시작하고 권한에 동의해주세요.", "", "앱 종료", shutdown, shutdown);
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
                            PopupMenu popupMenu = new PopupMenu(getActivity(),v);
                            getActivity().getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
                            popupMenu.setOnMenuItemClickListener(menuItemClickListener);
                            popupMenu.show();
                            break;
                        case R.id.favoriteSite_normal:
                            //Hiding Keyboard
                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService( Context.INPUT_METHOD_SERVICE);
                            if(imm.isAcceptingText()){
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            }
                            uri.clearFocus();

                            Settings.loadSettings();
                            final DialogMaker favoriteSiteListDialog=new DialogMaker();
                            final DialogMaker.Callback closeDialog=new DialogMaker.Callback() {
                                @Override
                                public void callbackMethod() {
                                    favoriteSiteListDialog.dismiss();
                                }
                            };
                            final DialogMaker.Callback addThisSite=new DialogMaker.Callback() {
                                @Override
                                public void callbackMethod() {
                                    final LayoutInflater inflater=getActivity().getLayoutInflater();
                                    final View inflatedView=inflater.inflate(R.layout.add_to_favorite_site_dialog, null);
                                    final EditText _uriInfo=(EditText)inflatedView.findViewById(R.id.addedUri);
                                    _uriInfo.setText(wv.getUriTextString());

                                    final DialogMaker addThisSiteDialog=new DialogMaker();
                                    DialogMaker.Callback add=new DialogMaker.Callback() {
                                        @Override
                                        public void callbackMethod() {
                                            final DialogMaker resultMessage=new DialogMaker();
                                            DialogMaker.Callback closeDialog=new DialogMaker.Callback() {
                                                @Override
                                                public void callbackMethod() {
                                                    resultMessage.dismiss();
                                                }
                                            };

                                            EditText _siteName=(EditText)inflatedView.findViewById(R.id.siteName);
                                            String siteName=_siteName.getText().toString();
                                            String uriInfo=_uriInfo.getText().toString();

                                            //Error Check Start
                                            if(siteName.equals("") || uriInfo.equals("")){
                                                //SiteName or URI is blank
                                                resultMessage.setCancelable(false);
                                                resultMessage.setValue("사이트 이름과 주소를 확인해주세요.", "확인", null, closeDialog, null);
                                                resultMessage.show(getActivity().getSupportFragmentManager(), "Save Error");
                                                return;
                                            }
                                            if(Settings.favoriteSiteList.get(siteName)!=null){
                                                //If site name is duplicated.
                                                resultMessage.setCancelable(false);
                                                resultMessage.setValue("사이트 이름이 중복됩니다.","확인", null, closeDialog, null);
                                                resultMessage.show(getActivity().getSupportFragmentManager(), "Site Name is Duplicated!");
                                                return;
                                            }

                                            //Successfully Save
                                            Settings.favoriteSiteList.put(siteName, uriInfo);
                                            Settings.saveSettings();

                                            resultMessage.setCancelable(false);
                                            resultMessage.setValue("저장되었습니다.", "확인", null, closeDialog, null);
                                            resultMessage.show(getActivity().getSupportFragmentManager(), "Successfully Save!");

                                            //Hiding Keyboard
                                            if(isOpenKeyboard()){
                                                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService( Context.INPUT_METHOD_SERVICE);
                                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                            }

                                            addThisSiteDialog.dismiss();
                                            favoriteSiteListDialog.dismiss();
                                        }
                                    };
                                    DialogMaker.Callback cancel=new DialogMaker.Callback() {
                                        @Override
                                        public void callbackMethod() {
                                            //Hiding Keyboard
                                            if(isOpenKeyboard()){
                                                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService( Context.INPUT_METHOD_SERVICE);
                                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                            }

                                            addThisSiteDialog.dismiss();
                                        }
                                    };
                                    addThisSiteDialog.setValue("즐겨찾기에 추가", "추가", "취소", add, cancel, inflatedView);
                                    addThisSiteDialog.show(getActivity().getSupportFragmentManager(), "ADD TO FAVORITE SITE LIST");

                                    //Open keyboard
                                    if(!isOpenKeyboard()){
                                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                    }
                                }
                            };

                            final ArrayAdapter<String> siteListAdapter=new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
                            siteListAdapter.addAll(Settings.favoriteSiteList.keySet());

                            final View inflatedFavoriteSiteListView=getActivity().getLayoutInflater().inflate(R.layout.favorite_site_list, null);
                            final ListView favoriteSiteList=(ListView)inflatedFavoriteSiteListView.findViewById(R.id.favoriteSiteList);
                            favoriteSiteList.setAdapter(siteListAdapter);
                            favoriteSiteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String key=siteListAdapter.getItem(position);
                                    wv.goToURL(Settings.favoriteSiteList.get(key));
                                    favoriteSiteListDialog.dismiss();
                                }
                            });
                            favoriteSiteList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    final int POSITION=position;
                                    final DialogMaker deleteThisSite=new DialogMaker();
                                    final DialogMaker.Callback delete=new DialogMaker.Callback() {
                                        @Override
                                        public void callbackMethod() {
                                            ViewGroup parent=(ViewGroup)(inflatedFavoriteSiteListView.getParent());
                                            parent.removeView(inflatedFavoriteSiteListView);

                                            Settings.favoriteSiteList.remove(siteListAdapter.getItem(POSITION));
                                            Settings.saveSettings();

                                            final DialogMaker completeDelete=new DialogMaker();
                                            completeDelete.setValue("삭제되었습니다.", "확인", "", new DialogMaker.Callback() {
                                                @Override
                                                public void callbackMethod() {
                                                    completeDelete.dismiss();
                                                }
                                            }, null).show(getActivity().getSupportFragmentManager(), "");
                                            deleteThisSite.dismiss();
                                            favoriteSiteListDialog.dismiss();
                                        }
                                    };
                                    DialogMaker.Callback cancel=new DialogMaker.Callback() {
                                        @Override
                                        public void callbackMethod() {
                                            deleteThisSite.dismiss();
                                        }
                                    };
                                    deleteThisSite.setValue("이 사이트를 즐겨찾기 목록에서 삭제하시겠습니까?", "삭제", "취소", delete, cancel);
                                    deleteThisSite.setCancelable(false);
                                    deleteThisSite.show(getActivity().getSupportFragmentManager(), "");
                                    return true;
                                }
                            });
                            favoriteSiteListDialog.setValue("즐겨찾기 목록", "이 사이트 저장", "닫기", addThisSite, closeDialog, inflatedFavoriteSiteListView);
                            favoriteSiteListDialog.show(getActivity().getSupportFragmentManager(), "Favorite Site List Dialog");
                            break;
                    }
                }
            }
        };
        lockBtn.setOnClickListener(cl);
        homeBtn.setOnClickListener(cl);
        settingBtn.setOnClickListener(cl);
        favortieSiteBtn.setOnClickListener(cl);

        View.OnLongClickListener longClickListener=new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                switch (v.getId()) {
                    case R.id.homeBtn_normal :
                        Toast.makeText(getContext(), "설정한 홈 페이지로 이동합니다", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.lockBtn_normal :
                        Toast.makeText(getContext(), "시큐리티 모드로 전환합니다", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settingBtn_normal :
                        Toast.makeText(getContext(), "세부 설정을 할 수 있습니다", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.favoriteSite_normal:
                        Toast.makeText(getContext(), "즐겨찾기 목록을 표시합니다", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        };
        lockBtn.setOnLongClickListener(longClickListener);
        homeBtn.setOnLongClickListener(longClickListener);
        settingBtn.setOnLongClickListener(longClickListener);
        favortieSiteBtn.setOnLongClickListener(longClickListener);


        bar=(LinearLayout)rootView.findViewById(R.id.universe_normal);

        return rootView;
    }


    private void openPopup(){

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

    private boolean isOpenKeyboard(){
        FrameLayout rootContainer=(FrameLayout)getActivity().findViewById(R.id.container);
        RelativeLayout fragmentContainer=(RelativeLayout) getActivity().findViewById(R.id.normalWebView);

        if(rootContainer.getHeight()-fragmentContainer.getHeight()> 100){
            return true;
        }else{
            return false;
        }
    }

}
