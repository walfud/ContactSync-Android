package com.walfud.contactsync_android.main;

import com.walfud.contactsync_android.service.network.NetworkService;
import com.walfud.contactsync_android.service.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * Created by walfud on 2017/4/20.
 */

public class MainPresenterImpl implements MainPresenter {

    private MainView mMainView;
    private UserService mUserService;
    private NetworkService mNetworkService;

    public MainPresenterImpl(MainView mainView, UserService userService, NetworkService networkService) {
        mMainView = mainView;
        mUserService = userService;
        mNetworkService = networkService;
    }

    @Override
    public void onSync() {
        mNetworkService.getContacts(mUserService.getToken())
                .map(data -> data.contacts().stream().map(MainView.ViewContactData::valueOf).collect(Collectors.toList()))
                .subscribe(new SingleObserver<List<MainView.ViewContactData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mMainView.loading(true);
                    }

                    @Override
                    public void onSuccess(List<MainView.ViewContactData> contactDataList) {
                        mMainView.loading(false);
                        mMainView.show(contactDataList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMainView.loading(false);
                    }
                });
    }

    //

}
