package com.walfud.contactsync_android.main

import android.widget.Toast
import com.walfud.contactsync_android.ContactQuery
import com.walfud.contactsync_android.ContactSyncApplication
import com.walfud.contactsync_android.R
import com.walfud.contactsync_android.appContext
import com.walfud.contactsync_android.main.MainContract.MainPresenter
import com.walfud.contactsync_android.main.MainContract.MainView
import com.walfud.contactsync_android.main.MainContract.MainView.ViewContactData
import com.walfud.contactsync_android.service.network.NetworkService
import com.walfud.contactsync_android.service.user.UserService
import com.walfud.contactsync_android.type.ContactInputType
import com.walfud.walle.android.contact.ContactUtils
import com.walfud.walle.lang.ObjectUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by walfud on 2017/4/20.
 */

class MainPresenterImpl(val mMainView: MainView) : MainPresenter {

    var contacts: List<ViewContactData> = listOf()

    override fun onLogin(oid: String, accessToken: String, refreshToken: String) {
        UserService.changeUser(oid)
        UserService.token = accessToken
    }

    override fun onRefresh() {
        mMainView.show(contacts)
    }

    override fun onSync() {
    }

    override fun onDownload() {
        Observable.just(0)
                .observeOn(Schedulers.io())
                .map {
                    // Local contacts
                    ContactUtils.getContactList(ContactSyncApplication.appContext)
                            .filter { !it.isDeleted }
                            .map { contactModel ->
                                ViewContactData(contactModel.displayName!!, contactModel.phoneList?.map { phoneModel -> phoneModel.number!! } ?: arrayListOf(), ViewContactData.STATUS_LOCAL_ONLY)
                            }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map { localContacts ->
                    contacts = localContacts
                    onRefresh()
                    contacts
                }
                .concatMap { localContacts ->
                    // Remote
                    Observable.create<ContactQuery.Data.Contact>({ e ->
                        NetworkService.download(UserService.token).contacts()!!.forEach(e::onNext)
                        e.onComplete()
                    })
                            .subscribeOn(Schedulers.io())
                            .filter({ serverContact ->
                                localContacts.find { ObjectUtils.isEqual(it.name + it.phoneList.toString(), serverContact.name() + serverContact.phones().toString()) } == null
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .map { contact ->
                                ViewContactData(contact.name()!!, contact.phones()!!, ViewContactData.STATUS_REMOTE_ONLY)
                            }
                }
                .observeOn(Schedulers.io())
                .map { contact ->
                    ContactUtils.insert(appContext, contact.name, contact.phoneList)
                    contact
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ViewContactData> {
                    override fun onSubscribe(d: Disposable?) {
                        mMainView.loading(true)
                    }

                    override fun onNext(contact: ViewContactData) {
                        contacts = contacts.plus(contact)
                        onRefresh()
                    }

                    override fun onComplete() {
                        mMainView.loading(false)
                        Toast.makeText(appContext, R.string.main_finish, Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                        mMainView.loading(false)
                        Toast.makeText(appContext, e.message ?: "", Toast.LENGTH_SHORT).show()
                    }
                })
    }

    override fun onUpload() {
        Single.just(0)
                .observeOn(Schedulers.io())
                .map {
                    NetworkService.upload(UserService.token, contacts.map { ContactInputType.builder().id("").name(it.name).phones(it.phoneList).build() }).upload()!!
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Boolean> {
                    override fun onSubscribe(d: Disposable) {
                        mMainView.loading(true)
                    }

                    override fun onSuccess(t: Boolean) {
                        mMainView.loading(false)
                        Toast.makeText(appContext, "Success", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                        mMainView.loading(false)
                        Toast.makeText(appContext, e.message, Toast.LENGTH_SHORT).show()
                    }
                })
    }

}
