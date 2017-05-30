package com.example.denky.ageis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * Created by WINDOWS7 on 2017-05-31.
 */

public class Settings implements Serializable{
    public static boolean useJavaScript;
    public static boolean permissionStartNewWindow;
    public static boolean permissionFileDownload;
    public static boolean permissionAppCache;

    public static boolean useVulnerabilityFindAlgorithm;
    public static boolean useProxyServer;
    public static boolean permissionAutoRemoveHistory;
    public static boolean useAdBlock;

    private static ObjectInputStream inputSettings;
    private static ObjectOutputStream outputSettings;


    private static Settings imported=null;

    //This class is never instantiated at other class
    private Settings(){
        initiate();
    }

    private void initiate(){
        useJavaScript=true;
        permissionStartNewWindow=true;
        permissionFileDownload=false;
        permissionAppCache=true;

        useVulnerabilityFindAlgorithm=true;
        useProxyServer=false;
        permissionAutoRemoveHistory=true;
        useAdBlock=true;
    }

    public static void restoreSetting(){
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        sdPath+="/Ageis"+"/Settings.set";

        File f=new File(sdPath);
        f.delete();

        try {
            outputSettings.writeObject(new Settings());
        } catch (IOException e) {
            Log.i("SettingSaveError: ", "failed saving file");
        }
    }

    //must be getting by this method
    public static Settings getInstance(){

        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        sdPath+="/Ageis";

        File folder= new File(sdPath);
        folder.mkdirs();

        sdPath+="/Settings.set";

        File settingFile=new File(sdPath);

        //make Stream
        try{
            outputSettings=new ObjectOutputStream(new FileOutputStream(settingFile));
        }catch(Exception e){
            Log.i("SettingSaveError: ", "can't make outputStream");
        }
        if(!settingFile.exists()){
            try {
                outputSettings.writeObject(new Settings());
                Log.i("SettingSaveError: ", "success writing object");
            } catch (IOException e) {
                Log.i("SettingSaveError: ", "fail writing object");
            }
        }
        try{
            inputSettings=new ObjectInputStream(new FileInputStream(settingFile));
        }catch(Exception e){
            Log.i("SettingSaveError: ", "Not exist setting file");
        }

        if(inputSettings==null){
            Log.i("SS", "NULL");
        }

        try{
            imported=(Settings)inputSettings.readObject();
        }catch(Exception e){
            Log.i("SettingSaveError: ", "can't read setting file");
        }

        return imported;
    }

    public static void saveSettings(){
        try {
            if(imported!=null){

                String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                sdPath+="/Ageis"+"/Settings.set";

                File f=new File(sdPath);
                f.delete();

                outputSettings.writeObject(imported);
            }else{
                Log.i("SettingSaveError: ", "can't read setting file");
            }
        } catch (IOException e) {
            Log.i("SettingSaveError: ", "can't save settings");
        }
    }
}
