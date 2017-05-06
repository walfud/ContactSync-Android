package com.walfud.contactsync_android.main;

import java.util.List;

/**
 * Created by walfud on 2017/4/20.
 */

public interface MainView {
    void login();
    void show(boolean isLogin, List<ContactModel> dataList);
    void loading(boolean show);

    class ViewContactData {
        public static final int STATUS_DEFAULT = 0;
        public static final int STATUS_LOCAL = 0;
        public static final int STATUS_SYNC = 1;
        public static final int STATUS_CHANGED = 0;

        public ContactModel contactModel;
        public int status;
    }
}
