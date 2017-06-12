package com.example.denky.ageis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Windows10 on 2017-06-12.
 */

public class SecurityMode extends Fragment implements View.OnLongClickListener{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup)inflater.inflate(R.layout.security_webview_fragment, container, false);
        return rootView;
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
