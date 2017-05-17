package com.walfud.contactsync_android.model;

import com.walfud.contactsync_android.service.contact.PhoneModel;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by walfud on 14/05/2017.
 */

public class PhoneRealm extends RealmObject {
    public String num;

    public static PhoneRealm valueOf(Realm realm, PhoneModel phoneModel) {
        PhoneRealm phoneRealm = realm.createObject(PhoneRealm.class);
        phoneRealm.num = phoneModel.number;

        return phoneRealm;
    }
    public static PhoneRealm valueOf(Realm realm, String phone) {
        PhoneRealm phoneRealm = realm.createObject(PhoneRealm.class);
        phoneRealm.num = phone;

        return phoneRealm;
    }
}
