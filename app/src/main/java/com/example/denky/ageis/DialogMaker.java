package com.example.denky.ageis;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by WINDOWS7 on 2017-05-31.
 */

interface Callback{
    void callbackMethod();
}

public class DialogMaker extends DialogFragment {
    private String message="";
    private String positiveMsg="";
    private String negativeMsg="";
    private Callback callback_positive=null;
    private Callback callback_negative=null;

    //Before using this class as instance, must call this method.
    public void setValue(String message, String positiveMsg, String negativeMsg, Callback callback_positive, Callback callback_negative){
        this.message=message;
        this.positiveMsg=positiveMsg;
        this.negativeMsg=negativeMsg;
        this.callback_positive=callback_positive;
        this.callback_negative=callback_negative;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(positiveMsg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(callback_positive!=null){
                            callback_positive.callbackMethod();
                        }
                    }
                })
                .setNegativeButton(negativeMsg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(callback_negative!=null){
                            callback_negative.callbackMethod();
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}