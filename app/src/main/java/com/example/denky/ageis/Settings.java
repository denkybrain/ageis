package com.example.denky.ageis;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by WINDOWS7 on 2017-05-31.
 */

public class Settings{
    static final String TAG="SETTING";


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


    private static SetInfo info=null;

    //This class is never instantiated at other class
    private Settings(){
    }



    public static void restoreSetting(){
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        sdPath+=File.separator+"Ageis"+File.separator+"Settings.set";

        File f=new File(sdPath);
        f.delete();

        try {
            outputSettings.writeObject(new SetInfo());
        } catch (IOException e) {
            Log.i(TAG, "failed saving file");
        }
    }

    public static SetInfo updateSettings(){

        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        sdPath+= File.separator+"Ageis";

        File folder= new File(sdPath);
        folder.mkdirs();

        sdPath+=File.separator+"Settings.set";

        Log.i(TAG, sdPath);

        File settingFile=new File(sdPath);

        //make Stream
        try{
            outputSettings=new ObjectOutputStream(new FileOutputStream(settingFile));
        }catch(Exception e){
            Log.i(TAG, "can't make outputStream");
        }

        if(settingFile.exists()==false){
            try {
                outputSettings.writeObject(new SetInfo());
                Log.i(TAG, "success writing object");
            } catch (IOException e) {
                Log.i(TAG, "fail writing object");
            }
        }else{
            Log.i(TAG, "File is already exist");
        }

        try{
            InputStream inputStream=new FileInputStream(settingFile);
            inputSettings=new ObjectInputStream(inputStream);
        }catch(Exception e){
            Log.i(TAG, "Can't make Input stream");
        }

        try{
            info=(SetInfo)inputSettings.readObject();
        }catch(Exception e){
            Log.i(TAG, e.toString());
            Log.i(TAG, "can't read setting file");
        }

        if(info!=null){
            useJavaScript=info.useJavaScript;
            permissionStartNewWindow=info.permissionStartNewWindow;
            permissionFileDownload=info.permissionFileDownload;
            permissionAppCache=info.permissionAppCache;

            useVulnerabilityFindAlgorithm=info.useVulnerabilityFindAlgorithm;
            useProxyServer=info.useProxyServer;
            permissionAutoRemoveHistory=info.permissionAutoRemoveHistory;
            useAdBlock=info.useAdBlock;
        }

        return info;
    }

    public static void saveSettings(){
        try {
            if(info!=null){

                String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                sdPath+="/Ageis"+"/Settings.set";

                File f=new File(sdPath);
                f.delete();

                outputSettings.writeObject(info);
            }else{
                Log.i(TAG, "can't read setting file");
            }
        } catch (IOException e) {
            Log.i(TAG, "can't save settings");
        }
    }
}
class SetInfo implements Serializable{

    public boolean useJavaScript;
    public boolean permissionStartNewWindow;
    public boolean permissionFileDownload;
    public boolean permissionAppCache;

    public boolean useVulnerabilityFindAlgorithm;
    public boolean useProxyServer;
    public boolean permissionAutoRemoveHistory;
    public boolean useAdBlock;

    public SetInfo(){
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

}
