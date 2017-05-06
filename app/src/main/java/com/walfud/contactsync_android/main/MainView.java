package com.walfud.contactsync_android.main;

import android.text.TextUtils;

import com.walfud.contactsync_android.ContactsQuery;

import java.util.List;

/**
 * Created by walfud on 2017/4/20.
 */

public interface MainView {
    void login();
    void show(List<ViewContactData> dataList);
    void loading(boolean show);

    class ViewContactData {
        public static final int STATUS_DEFAULT = 0;
        public static final int STATUS_LOCAL = 0;
        public static final int STATUS_SYNC = 1;
        public static final int STATUS_CHANGED = 0;

        public String name;
        public String phone;
        public int status;

        public static ViewContactData valueOf(ContactsQuery.Data.Contact contact) {
            ViewContactData contactModel = new ViewContactData();
            contactModel.name = contact.name();
            contactModel.phone = contact.phone();
            contactModel.status = TextUtils.isEmpty(contact.id()) ? STATUS_LOCAL : STATUS_SYNC;

            return contactModel;
        }
    }
}
