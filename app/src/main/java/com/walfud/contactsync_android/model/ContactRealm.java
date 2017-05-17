package com.walfud.contactsync_android.model;

import com.walfud.contactsync_android.SyncMutation;
import com.walfud.contactsync_android.service.contact.ContactModel;

import java.util.stream.Collectors;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by walfud on 14/05/2017.
 */

public class ContactRealm extends RealmObject {
    public String id;
    public long localId;
    public boolean isDeleted;
    public long modifyTime;
    public String name;
    public RealmList<PhoneRealm> phoneRealmList;

    public static ContactRealm valueOf(Realm realm, ContactModel contactModel) {
        ContactRealm contactRealm = realm.createObject(ContactRealm.class);
        contactRealm.localId = contactModel.id;
        contactRealm.isDeleted = contactModel.isDeleted;
        contactRealm.modifyTime = contactModel.modifyTime;
        contactRealm.name = contactModel.displayName;
        contactRealm.phoneRealmList.addAll(contactModel.phoneList.stream().map(phoneModel -> PhoneRealm.valueOf(realm, phoneModel)).collect(Collectors.toList()));

        return contactRealm;
    }

    public static ContactRealm valueOf(Realm realm, SyncMutation.Data.Contact contact) {
        ContactRealm contactRealm = realm.createObject(ContactRealm.class);
        contactRealm.id = contact.id();
        contactRealm.isDeleted = contact.is_deleted();
        contactRealm.modifyTime = ((Number) contact.modify_time()).longValue();
        contactRealm.name = contact.name();
        contactRealm.phoneRealmList.addAll(contact.phones().stream().map(phone -> PhoneRealm.valueOf(realm, phone)).collect(Collectors.toList()));

        return contactRealm;
    }
}
