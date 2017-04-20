package com.walfud.contactsync_android.main;

import com.walfud.contactsync_android.service.user.UserService;

import java.lang.reflect.Field;

/**
 * Created by walfud on 2017/4/20.
 */

public class MainPresenterImpl implements MainPresenter {

    private MainView mMainView;
    private UserService mUserService;

    public MainPresenterImpl(MainView mainView, UserService userService) {
        mMainView = mainView;
        mUserService = userService;
        try {
            Field field = MainActivity.class.getField("mSyncBtn");
            field.setAccessible(true);
            field.set(mMainView, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public void onSync() {
        if (mUserService.isLogin()) {

        }
    }
}
