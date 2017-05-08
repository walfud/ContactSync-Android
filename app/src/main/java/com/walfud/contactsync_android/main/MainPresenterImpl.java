package com.walfud.contactsync_android.main;

import com.walfud.contactsync_android.ContactsQuery;
import com.walfud.contactsync_android.service.contact.ContactModel;
import com.walfud.contactsync_android.service.contact.ContactService;
import com.walfud.contactsync_android.service.network.NetworkService;
import com.walfud.contactsync_android.service.user.UserService;
import com.walfud.walle.collection.CollectionUtils;
import com.walfud.walle.lang.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * Created by walfud on 2017/4/20.
 */

public class MainPresenterImpl implements MainPresenter {

    public static final String TAG = "MainPresenterImpl";

    private MainView mMainView;
    private UserService mUserService;
    private NetworkService mNetworkService;

    public MainPresenterImpl(MainView mainView, UserService userService, NetworkService networkService) {
        mMainView = mainView;
        mUserService = userService;
        mNetworkService = networkService;
    }

    @Override
    public void onRefresh() {
        List<MainView.ViewContactData> local = ContactService.getContactList().stream()
                .filter(contactModel -> !contactModel.isDeleted)
                .map(contactModel -> {
                    MainView.ViewContactData viewContactData = new MainView.ViewContactData();
                    viewContactData.name = contactModel.displayName;
                    viewContactData.phoneList = contactModel.phoneList.stream()
                            .map(phoneModel -> phoneModel.number)
                            .collect(Collectors.toList());

                    return viewContactData;
                })
                .collect(Collectors.toList());
        mMainView.show(local);
    }

    @Override
    public void onSync() {
        mNetworkService.getContacts(mUserService.getToken())
                .map(remoteData -> {
                    List<MainView.ViewContactData> viewContactDataList = new ArrayList<>();

                    List<ContactModel> contactList = ContactService.getContactList();
                    List<ContactModel> localOnly = contactList.stream()
                            .filter(contact -> searchRemote(contact, remoteData.contacts()) == null)
                            .collect(Collectors.toList());

                    return viewContactDataList;
                })
//                .map(data -> data.contacts().stream().map(MainView.ViewContactData::valueOf).collect(Collectors.toList()))
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
    private ContactsQuery.Data.Contact searchRemote(ContactModel contact, List<ContactsQuery.Data.Contact> remoteContactList) {
        return CollectionUtils.find(remoteContactList, (Predicate<ContactsQuery.Data.Contact>) remoteContact -> {
            if (!ObjectUtils.isEqual(remoteContact.name(), contact.displayName)) {
                return false;
            }
            if (remoteContact.phones().size() != contact.phoneList.size()) {
                return false;
            } else {
                for (int i = 0; i < remoteContact.phones().size(); i++) {
                    String remotePhone = remoteContact.phones().get(i);
                    String phone = contact.phoneList.get(i).number;
                    if (!ObjectUtils.isEqual(remotePhone, phone)) {
                        return false;
                    }
                }
            }

            return true;
        });
    }

}
