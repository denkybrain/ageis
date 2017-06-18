package com.example.denky.ageis;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by WINDOWS7 on 2017-05-31.
 */

public class Settings{
    static final String TAG="SETTING";
    //Setting Value
    public static boolean useJavaScript=true;
    public static boolean permissionStartNewWindow=true;
    public static boolean permissionFileDownload=true;
    public static boolean permissionAppCache=true;

    public static boolean useVulnerabilityFindAlgorithm=true;
    public static boolean denyDangerousSite =true;
    public static boolean permissionAutoRemoveHistory=true;
    public static boolean useAdBlock=true;

    //Stream for File I/O
    private static ObjectInputStream inputSettings;
    private static ObjectOutputStream outputSettings;

    private static String filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Ageis"+File.separator+"Settings.set";

    //This class is never instantiated at other class
    private Settings(){}

    public static void restoreSetting(){

        File f=new File(filePath);
        f.delete();
        try {
            outputSettings=new ObjectOutputStream(new FileOutputStream(f));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            SetInfo newSetInfo=new SetInfo();
            outputSettings.writeObject(newSetInfo);
        } catch (IOException e) {
            Log.i(TAG, "failed saving file");
        }
    }

    public static void loadSettings(){

        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        sdPath+= File.separator+"Ageis";

        File folder= new File(sdPath);
        folder.mkdirs();

        Log.i(TAG, filePath);

        File settingFile=new File(filePath);

        if(settingFile.exists()==false){
            try {

                //make Output Stream
                try{
                    outputSettings=new ObjectOutputStream(new FileOutputStream(settingFile));
                }catch(Exception e){
                    Log.i(TAG, "can't make outputStream");
                }

                SetInfo newSetInfo=new SetInfo();
                outputSettings.writeObject(newSetInfo);
                Log.i(TAG, "success writing object");
            } catch (IOException e) {
                Log.i(TAG, "fail writing object");
            }
        }else{

            //make Output Stream
            try{
                outputSettings=new ObjectOutputStream(new FileOutputStream(settingFile));
            }catch(Exception e){
                Log.i(TAG, "can't make outputStream");
            }

            Log.i(TAG, "File is already exist");
        }

        //make Input Stream
        try{
            inputSettings=new ObjectInputStream(new FileInputStream(settingFile));
        }catch(Exception e){
            Log.i(TAG, "Can't make InputStream");
        }

        //Read Setting file
        SetInfo info=null;
        if(inputSettings!=null){
            try{
                info=(SetInfo)inputSettings.readObject();
            }catch(Exception e){
                Log.i(TAG, "Exception: "+e.toString());
                Log.i(TAG, "can't read setting file");
            }
        }else{
            Log.i(TAG, "InputStream is null");
        }


        if(info!=null){
            useJavaScript=info.useJavaScript;
            permissionStartNewWindow=info.permissionStartNewWindow;
            permissionFileDownload=info.permissionFileDownload;
            permissionAppCache=info.permissionAppCache;

            useVulnerabilityFindAlgorithm=info.useVulnerabilityFindAlgorithm;
            denyDangerousSite =info.useProxyServer;
            permissionAutoRemoveHistory=info.permissionAutoRemoveHistory;
            useAdBlock=info.useAdBlock;
        }else{
            Log.i(TAG, "Fail to read object in file");
        }

    }

    public static void saveSettings(){
        try {
            //after deleting setting file, Save New Setting file.
            File f=new File(filePath);
            if(f.delete()){
                Log.i(TAG, "Success delete");
            }else {
                Log.i(TAG, "Fail delete");
            }
            outputSettings=new ObjectOutputStream(new FileOutputStream(f));

            ////////////////////////////Saving////////////////////////////////
            SetInfo info=new SetInfo();

            info.useJavaScript=useJavaScript;
            info.permissionStartNewWindow=permissionStartNewWindow;
            info.permissionFileDownload=permissionFileDownload;
            info.permissionAppCache=permissionAppCache;
            info.useVulnerabilityFindAlgorithm=useVulnerabilityFindAlgorithm;
            info.useProxyServer= denyDangerousSite;
            info.permissionAutoRemoveHistory=permissionAutoRemoveHistory;
            info.useAdBlock=useAdBlock;

            outputSettings.writeObject(info);
            //////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            Log.i(TAG, "can't save settings");
        }
    }
}

class SetInfo implements Serializable{

    public boolean useJavaScript=true;
    public boolean permissionStartNewWindow=true;
    public boolean permissionFileDownload=false;
    public boolean permissionAppCache=true;

    public boolean useVulnerabilityFindAlgorithm=true;
    public boolean useProxyServer=false;
    public boolean permissionAutoRemoveHistory=true;
    public boolean useAdBlock=true;

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
