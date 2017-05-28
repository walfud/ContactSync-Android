package com.walfud.contactsync_android.main

import android.util.Log
import com.walfud.contactsync_android.ContactSyncApplication
import com.walfud.contactsync_android.model.ContactRealm
import com.walfud.contactsync_android.service.contact.ContactService
import com.walfud.contactsync_android.service.network.NetworkService
import com.walfud.contactsync_android.service.user.UserService
import com.walfud.contactsync_android.type.ContactInputType
import com.walfud.walle.lang.ObjectUtils
import io.realm.Realm

/**
 * Created by walfud on 2017/4/20.
 */

class MainPresenterImpl(private val mMainView: MainView, private val mUserService: UserService, private val mNetworkService: NetworkService) : MainPresenter {

    override fun onLogin(oid: String, accessToken: String, refreshToken: String) {
        mUserService.changeUser(oid)
        mUserService.token = accessToken
    }

    override fun onRefresh() {
        val local = ContactService.getContactList(ContactSyncApplication.appContext!!)
                .map { contactModel ->
                    val realm = Realm.getDefaultInstance()
                    val contactRealm = realm.where(ContactRealm::class.java)
                            .equalTo("localId", contactModel.id)
                            .findFirst()

                    val viewContactData = MainView.ViewContactData()
                    if (contactRealm == null) {
                        // New contact
                        viewContactData.name = contactModel.displayName
                        viewContactData.phoneList = contactModel.phoneList
                                ?.map { phoneModel -> phoneModel.number!! }
                        viewContactData.status = MainView.ViewContactData.STATUS_LOCAL_ONLY
                    } else {
                        // TODO: show difference
                        viewContactData.name = contactModel.displayName
                        viewContactData.phoneList = contactModel.phoneList
                                ?.map { phoneModel -> phoneModel.number!! }
                        viewContactData.status = MainView.ViewContactData.STATUS_CHANGED
                    }

                    viewContactData!!
                }
        mMainView.show(local)
    }

    override fun onSync() {
        val clientContactList = ContactService.getContactList(ContactSyncApplication.appContext!!)
        val data = mNetworkService.sync(mUserService.token, clientContactList
                .map { contactModel ->
                    val realm = Realm.getDefaultInstance()
                    val contactRealm = realm.where(ContactRealm::class.java)
                            .equalTo("localId", contactModel.id)
                            .findFirst()

                    val contactBuilder = ContactInputType.builder()
                    if (contactRealm != null) {
                        contactBuilder.id(contactRealm.id)
                    }
                    contactBuilder.name(contactModel.displayName)
                            .phones(contactModel.phoneList?.map { phoneModel -> phoneModel.number!! })
                            .modify_time(contactModel.modifyTime)
                            .is_deleted(contactModel.isDeleted)

                    contactBuilder.build()
                })!!

        val serverContactList = data.sync()!!.contacts()!!
        if (serverContactList.size < clientContactList.size) {
            throw RuntimeException("server response error")
        }

        //
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.deleteAll()
        for (i in serverContactList.indices) {
            val serverContact = serverContactList[i]
            if (i < clientContactList.size) {
                // Client vs. Server
                val clientContact = clientContactList[i]
                if (!ObjectUtils.isEqual(clientContact.displayName, serverContact.name())
                        || !ObjectUtils.isEqual(clientContact.phoneList, serverContact.phones()) { clientPhone, serverPhone -> if (ObjectUtils.isEqual(clientPhone.number, serverPhone)) 0 else -1 }
                        || clientContact.isDeleted != serverContact.is_deleted) {
                    if (!serverContact.is_deleted!!) {
                        ContactService.update(ContactSyncApplication.appContext!!, clientContact.id, serverContact.name()!!, serverContact.phones()!!)
                        Log.d(TAG, String.format("modify: %10s, %s", serverContact.name(), serverContact.phones().toString()))

                    } else {
                        ContactService.delete(ContactSyncApplication.appContext!!, clientContact.id)
                        Log.d(TAG, String.format("del: %10s, %d", serverContact.name(), clientContact.id))
                    }
                }
                val contactRealm = ContactRealm.valueOf(realm, serverContact)
                contactRealm.localId = clientContact.id
            } else {
                // Only Server
                val localId = ContactService.insert(ContactSyncApplication.appContext!!, serverContact.name()!!, serverContact.phones()!!)
                val contactRealm = ContactRealm.valueOf(realm, serverContact)
                contactRealm.localId = localId
                Log.d(TAG, String.format("add: %10s, %s", serverContact.name(), serverContact.phones().toString()))
            }
        }
        realm.commitTransaction()
    }

    companion object {

        val TAG = "MainPresenterImpl"
    }
}
