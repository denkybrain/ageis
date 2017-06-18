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

    static final String TAG="Setting File";

    //Setting Value
    public static boolean useJavaScript=true;
    public static boolean permissionStartNewWindow=true;
    public static boolean permissionFileDownload=false;
    public static boolean permissionAppCache=true;

    public static boolean useVulnerabilityFindAlgorithm=true;
    public static boolean permissionDangerousSite =false;
    public static boolean permissionAutoRemoveHistory=true;

    //Stream for File I/O
    private static ObjectInputStream inputSettings=null;
    private static ObjectOutputStream outputSettings=null;

    private static String filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Ageis"+File.separator+".Settings.set";

    //This class is never instantiated at other class
    private Settings(){}

    public static void restoreSetting(){
        try {
            SetInfo newSetInfo=new SetInfo();
            outputSettings=new ObjectOutputStream(new FileOutputStream(filePath));
            outputSettings.writeObject(newSetInfo);
            outputSettings.close();
        } catch (IOException e) {
            Log.i(TAG, "Can't restore Setting File");
        }
    }

    public static boolean loadSettings(){
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        sdPath+= File.separator+"Ageis";

        File folder= new File(sdPath);
        if(folder.exists()==false){
            folder.mkdirs();
        }

        File settingFile=new File(filePath);
        try{
            if(settingFile.exists()==false){
                Log.i(TAG, "Setting File is not existing");
                //make Output Stream (when not existing setting file)
                outputSettings=new ObjectOutputStream(new FileOutputStream(settingFile));
                outputSettings.writeObject(new SetInfo());
                outputSettings.close();
                Log.i(TAG, "Success writing new object");
            }else{
                Log.i(TAG, "Setting File is already exist");
                //make Output Stream (when already existing setting file)
                //outputSettings=new ObjectOutputStream(new FileOutputStream(settingFile));
            }

            //make Input Stream
            inputSettings=new ObjectInputStream(new FileInputStream(settingFile));

            //Read Setting file
            SetInfo info=(SetInfo)inputSettings.readObject();
            Log.i(TAG, "Success Reading");

            if(info!=null){
                useJavaScript=info.useJavaScript;
                permissionStartNewWindow=info.permissionStartNewWindow;
                permissionFileDownload=info.permissionFileDownload;
                permissionAppCache=info.permissionAppCache;
                useVulnerabilityFindAlgorithm=info.useVulnerabilityFindAlgorithm;
                permissionDangerousSite =info.permissionDangerousSite;
                permissionAutoRemoveHistory=info.permissionAutoRemoveHistory;
            }else{
                Log.i(TAG, "Fail to read object in file");
            }

            inputSettings.close();
        }catch(Exception e) {
            Log.i(TAG, "Exception Occur");
            return false;
        }
    return true;
    }

    public static void saveSettings(){
        ////////////////////////////Saving////////////////////////////////
        SetInfo info=new SetInfo();

        info.useJavaScript=useJavaScript;
        info.permissionStartNewWindow=permissionStartNewWindow;
        info.permissionFileDownload=permissionFileDownload;
        info.permissionAppCache=permissionAppCache;
        info.useVulnerabilityFindAlgorithm=useVulnerabilityFindAlgorithm;
        info.permissionDangerousSite = permissionDangerousSite;
        info.permissionAutoRemoveHistory=permissionAutoRemoveHistory;
        try {
            outputSettings=new ObjectOutputStream(new FileOutputStream(filePath));
            outputSettings.writeObject(info);
            outputSettings.close();
        } catch (IOException e) {
            Log.i(TAG, "Can't save settings");
        }
        //////////////////////////////////////////////////////////////////
    }
}

//This Class is only using when saving setting value to file.
class SetInfo implements Serializable{

    public boolean useJavaScript=true;
    public boolean permissionStartNewWindow=true;
    public boolean permissionFileDownload=false;
    public boolean permissionAppCache=true;

    public boolean useVulnerabilityFindAlgorithm=true;
    public boolean permissionDangerousSite =false;
    public boolean permissionAutoRemoveHistory=true;

    public SetInfo(){
        initiate();
    }

    private void initiate(){
        useJavaScript=true;
        permissionStartNewWindow=true;
        permissionFileDownload=false;
        permissionAppCache=true;

        useVulnerabilityFindAlgorithm=true;
        permissionDangerousSite =false;
        permissionAutoRemoveHistory=true;
    }
}