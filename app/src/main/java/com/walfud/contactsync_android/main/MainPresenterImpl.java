package com.walfud.contactsync_android.main;

import com.walfud.contactsync_android.service.user.UserService;

/**
 * Created by walfud on 2017/4/20.
 */

public class MainPresenterImpl implements MainPresenter {

    private MainView mMainView;
    private UserService mUserService;

    public MainPresenterImpl(MainView mainView, UserService userService) {
        mMainView = mainView;
        mUserService = userService;
    }

    @Override
    public void onSync() {
        if (!mUserService.isLogin()) {
            mMainView.login();
        }

        mMainView.loading();
    }

    //

}
