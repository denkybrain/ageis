package com.example.denky.ageis;

/**
 * Created by denky on 2017-05-14.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.denky.ageis.ReferenceString.STORAGE_READ_PERMISSON;
import static com.example.denky.ageis.ReferenceString.STORAGE_WRITE_PERMISSON;

// overloading
public class ActivityMain extends AppCompatActivity{

    final Activity THIS_ACTIVITY =  this;

    public Fragment normalMode   =  new FragmentNormalMode();
    public Fragment securityMode =   new FragmentSecurityMode();
    public CustomizedWebViewManager customizedWebViewManager = new CustomizedWebViewManager(normalMode,securityMode, THIS_ACTIVITY);

    public CustomizedWebViewManager getData(){
        return customizedWebViewManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS); //프로그래스 바 기능 요청
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission();  //get needed permission
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
        Intent intent = getIntent();
        String url = intent.getStringExtra("URL");
        if(url != null){
            Log.d("widae","게이야 새 창열었다!");
            customizedWebViewManager.goToUrlNormalMode(url);
        }
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
    public void onBackPressed() { //뒤로가기 버튼 누르면 뒤로감 cWVM가 노말모드, 시큐리티 모드를 포함함
        if(customizedWebViewManager.backPress() == 0){
            finish();
            super.onBackPressed();
        }
        else
            return ;
    }
}
