package com.example.denky.ageis;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by denky on 2017-06-21.
 */

public class CustomizedHandlerAnimation extends Handler {

    private FragmentNormalMode activity;
    public final int UNIVERSE_VISIBLE_START = 0 ;
    public final int UNIVERSE_VISIBLE_DONE = 1 ;
    public final int UNIVERSE_INVISIBLE_START = 2 ;
    public final int UNIVERSE_INVISIBLE_DONE = 3 ;
    CustomizedHandlerAnimation(FragmentNormalMode activity){
        this.activity = activity;
    }

    public void sendMsgQuick(int data){
        Message msg = obtainMessage();
        msg = obtainMessage();
        msg.what = data; //저장 완료했다고 띄움
        sendMessage(msg);
    }

    @Override
    public void handleMessage(Message msg) {
        Log.d("widae", "핸들러 받음 : "+msg.what);
        switch (msg.what) {
            case UNIVERSE_VISIBLE_START  :
                ViewGroup.LayoutParams params=(ViewGroup.LayoutParams)activity.bar.getLayoutParams();
                Log.d("widae", "params.height : "+params.height);
                params.height += 0.01 ;
                activity.bar.setLayoutParams(params);
                break;
            case 1 :

                break;
            case 2 : //주소 공유

                break;

        }
    }
}
