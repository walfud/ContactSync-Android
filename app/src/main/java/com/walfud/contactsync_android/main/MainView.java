package com.walfud.contactsync_android.main;

import java.util.List;

/**
 * Created by walfud on 2017/4/20.
 */

public interface MainView {
    void show(List<ViewContactData> dataList);

    void loading(boolean show);

    void error(String err);

    class ViewContactData {
        public static final int STATUS_DEFAULT = 0;
        public static final int STATUS_LOCAL_ONLY = 1;
        public static final int STATUS_REMOTE_ONLY = 2;
        public static final int STATUS_CHANGED = 3;

        public String name;
        public List<String> phoneList;
        public int status;
    }
}
