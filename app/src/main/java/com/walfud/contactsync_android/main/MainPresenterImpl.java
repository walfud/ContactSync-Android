package com.walfud.contactsync_android.main;

import android.util.Log;

import com.walfud.contactsync_android.ContactSyncApplication;
import com.walfud.contactsync_android.SyncMutation;
import com.walfud.contactsync_android.model.ContactRealm;
import com.walfud.contactsync_android.service.contact.ContactModel;
import com.walfud.contactsync_android.service.contact.ContactService;
import com.walfud.contactsync_android.service.network.NetworkService;
import com.walfud.contactsync_android.service.user.UserService;
import com.walfud.contactsync_android.type.ContactInputType;
import com.walfud.walle.lang.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;

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
    public void onLogin(String oid, String accessToken, String refreshToken) {
        mUserService.changeUser(oid);
        mUserService.setToken(accessToken);
    }

    @Override
    public void onRefresh() {
        List<MainView.ViewContactData> local = ContactService.getContactList(ContactSyncApplication.getAppContext()).stream()
                .map(contactModel -> {
                    Realm realm = Realm.getDefaultInstance();
                    ContactRealm contactRealm = realm.where(ContactRealm.class)
                            .equalTo("localId", contactModel.id)
                            .findFirst();

                    MainView.ViewContactData viewContactData = new MainView.ViewContactData();
                    if (contactRealm == null) {
                        // New contact
                        viewContactData.name = contactModel.displayName;
                        viewContactData.phoneList = contactModel.phoneList.stream()
                                .map(phoneModel -> phoneModel.number)
                                .collect(Collectors.toList());
                        viewContactData.status = MainView.ViewContactData.STATUS_LOCAL_ONLY;
                    } else {
                        // TODO: show difference
                        viewContactData.name = contactModel.displayName;
                        viewContactData.phoneList = contactModel.phoneList.stream()
                                .map(phoneModel -> phoneModel.number)
                                .collect(Collectors.toList());
                        viewContactData.status = MainView.ViewContactData.STATUS_CHANGED;
                    }

                    return viewContactData;
                })
                .collect(Collectors.toList());
        mMainView.show(local);
    }

    @Override
    public void onSync() {
        List<ContactModel> clientContactList = ContactService.getContactList(ContactSyncApplication.getAppContext());
        mNetworkService.sync(mUserService.getToken(), clientContactList.stream()
                .map(contactModel -> {
                    Realm realm = Realm.getDefaultInstance();
                    ContactRealm contactRealm = realm.where(ContactRealm.class)
                            .equalTo("localId", contactModel.id)
                            .findFirst();

                    ContactInputType.Builder contactBuilder = ContactInputType.builder();
                    if (contactRealm != null) {
                        contactBuilder.id(contactRealm.id);
                    }
                    contactBuilder.name(contactModel.displayName)
                            .phones(contactModel.phoneList.stream()
                                    .map(phoneModel -> phoneModel.number)
                                    .collect(Collectors.toList()))
                            .modify_time(contactModel.modifyTime)
                            .is_deleted(contactModel.isDeleted);

                    return contactBuilder.build();
                })
                .collect(Collectors.toList()))
                .subscribe(new SingleObserver<SyncMutation.Data>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(SyncMutation.Data data) {
                        List<SyncMutation.Data.Contact> serverContactList = data.sync().contacts();
                        if (serverContactList.size() < clientContactList.size()) {
                            throw new RuntimeException("server response error");
                        }

                        //
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.deleteAll();
                        for (int i = 0; i < serverContactList.size(); i++) {
                            SyncMutation.Data.Contact serverContact = serverContactList.get(i);
                            if (i < clientContactList.size()) {
                                // Client vs. Server
                                ContactModel clientContact = clientContactList.get(i);
                                if (!ObjectUtils.isEqual(clientContact.displayName, serverContact.name())
                                        || !ObjectUtils.isEqual(clientContact.phoneList, serverContact.phones(), (clientPhone, serverPhone) -> ObjectUtils.isEqual(clientPhone.number, serverPhone) ? 0 : -1)
                                        || clientContact.isDeleted != serverContact.is_deleted()) {
                                    if (!serverContact.is_deleted()) {
                                        ContactService.update(ContactSyncApplication.getAppContext(), clientContact.id, serverContact.name(), serverContact.phones());
                                        Log.d(TAG, String.format("modify: %10s, %s", serverContact.name(), serverContact.phones().toString()));

                                    } else {
                                        ContactService.delete(ContactSyncApplication.getAppContext(), clientContact.id);
                                        Log.d(TAG, String.format("del: %10s, %d", serverContact.name(), clientContact.id));
                                    }
                                }
                                ContactRealm contactRealm = ContactRealm.valueOf(realm, serverContact);
                                contactRealm.localId = clientContact.id;
                            } else {
                                // Only Server
                                long localId = ContactService.insert(ContactSyncApplication.getAppContext(), serverContact.name(), serverContact.phones());
                                ContactRealm contactRealm = ContactRealm.valueOf(realm, serverContact);
                                contactRealm.localId = localId;
                                Log.d(TAG, String.format("add: %10s, %s", serverContact.name(), serverContact.phones().toString()));
                            }
                        }
                        realm.commitTransaction();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMainView.error(e.getMessage());
                    }
                });
    }
}
