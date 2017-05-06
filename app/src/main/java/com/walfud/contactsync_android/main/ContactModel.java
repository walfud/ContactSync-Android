package com.walfud.contactsync_android.main;

import com.walfud.contactsync_android.ContactsQuery;

/**
 * Created by walfud on 2017/4/20.
 */

public class ContactModel {
    public String id;
    public String name;
    public String phone;

    public static ContactModel valueOf(ContactsQuery.Data.Contact contact) {
        ContactModel contactModel = new ContactModel();
        contactModel.id = contact.id();
        contactModel.name = contact.name();
        contactModel.phone = contact.phone();

        return contactModel;
    }
}
