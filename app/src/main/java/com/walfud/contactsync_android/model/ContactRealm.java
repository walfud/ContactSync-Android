package com.walfud.contactsync_android.model;

import com.walfud.contactsync_android.SyncMutation;

import java.util.stream.Collectors;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by walfud on 14/05/2017.
 */

public class ContactRealm extends RealmObject {
    @PrimaryKey
    public String id;
    @Required
    public Long localId;
    public boolean isDeleted;
    public long modifyTime;
    public String name;
    public RealmList<PhoneRealm> phoneRealmList;

    public static ContactRealm valueOf(Realm realm, SyncMutation.Data.Contact contact) {
        ContactRealm contactRealm = realm.createObject(ContactRealm.class, contact.id());
        contactRealm.isDeleted = contact.is_deleted();
        contactRealm.modifyTime = ((Number) contact.modify_time()).longValue();
        contactRealm.name = contact.name();
        contactRealm.phoneRealmList.addAll(contact.phones().stream().map(phone -> PhoneRealm.valueOf(realm, phone)).collect(Collectors.toList()));

        return contactRealm;
    }
}
