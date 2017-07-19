package com.example.denky.ageis;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;

/**
 * Created by WINDOWS7 on 2017-05-31.
 */

public class DialogMaker extends DialogFragment {
    public interface Callback{
        void callbackMethod();
    }

    private String message="";
    private String positiveMsg="";
    private String negativeMsg="";
    private Callback callback_positive=null;
    private Callback callback_negative=null;

    private ArrayAdapter<String> arrayAdapter=null;
    private DialogInterface.OnClickListener listListener=null;

    private View childView=null;

    //Before using this class as instance, must call this method.
    public void setValue(String message, String positiveMsg, String negativeMsg, Callback callback_positive, Callback callback_negative){
        this.message=message;
        this.positiveMsg=positiveMsg;
        this.negativeMsg=negativeMsg;
        this.callback_positive=callback_positive;
        this.callback_negative=callback_negative;
    }

    public void setValue(String message, String positiveMsg, String negativeMsg, Callback callback_positive, Callback callback_negative, View childView){
        this.setValue(message, positiveMsg, negativeMsg, callback_positive, callback_negative);
        this.childView=childView;
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

        if(arrayAdapter!=null && listListener!=null){
            builder.setAdapter(arrayAdapter, listListener);
        }

        if(childView!=null){
            builder.setView(childView);
        }

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
