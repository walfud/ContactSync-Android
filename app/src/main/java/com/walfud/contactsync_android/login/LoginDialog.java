package com.walfud.contactsync_android.login;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.walfud.contactsync_android.R;

/**
 * Created by walfud on 2017/4/20.
 */

public class LoginDialog extends DialogFragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.dialog_login, container, false);
        return view;
    }
}
