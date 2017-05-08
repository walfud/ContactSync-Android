package com.walfud.contactsync_android.service.contact;

import android.net.Uri;
import android.provider.ContactsContract;

import com.walfud.contactsync_android.ContactSyncApplication;
import com.walfud.walle.android.DbUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by walfud on 2017/5/8.
 */

public class ContactService {
    public static List<ContactModel> getContactList() {
        List<ContactModel> contactList = new ArrayList<>();

        // Normal Contacts
        List<Map<String, Object>> contactResult = DbUtils.get(ContactSyncApplication.getAppContext().getContentResolver(), ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        contactResult.stream()
                .forEach(contactRow -> {
                    ContactModel contact = new ContactModel();
                    contact.isDeleted = false;
                    String lookupKey = (String) contactRow.get(ContactsContract.Contacts.LOOKUP_KEY);
                    contact.displayName = (String) contactRow.get(ContactsContract.Contacts.DISPLAY_NAME);
                    contact.phoneList = getPhoneList(lookupKey);
                    contact.emailList = getEmailList(lookupKey);
                    contact.noteList = getNoteList(lookupKey);
                    contact.addressList = getAddressList(lookupKey);
                    contact.imList = getImList(lookupKey);
                    contact.organization = getOrganization(lookupKey);

                    contactList.add(contact);
                });
        // Deleted Contacts
        List<Map<String, Object>> deletedContactResult = DbUtils.get(ContactSyncApplication.getAppContext().getContentResolver(), Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "raw_contacts"), null, null, null, null);
        deletedContactResult.stream()
                .filter(contactRow -> (Long) contactRow.get(ContactsContract.RawContacts.DELETED) != 0)
                .forEach(contactRow -> {
                    ContactModel contact = new ContactModel();
                    contact.isDeleted = true;
                    contact.displayName = (String) contactRow.get(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY);

                    contactList.add(contact);
                });

        return contactList;
    }

    private static String getDisplayName(String lookupKey) {
        String displayName = "";
        List<Map<String, Object>> structuredNameResult = DbUtils.get(ContactSyncApplication.getAppContext().getContentResolver(), ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), new String[]{lookupKey, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}, null);
        if (!structuredNameResult.isEmpty()) {
            displayName = (String) structuredNameResult.get(0).get(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
        }
        return displayName;
    }

    private static List<PhoneModel> getPhoneList(String lookupKey) {
        List<PhoneModel> phoneList = new ArrayList<>();
        List<Map<String, Object>> phoneResult = DbUtils.get(ContactSyncApplication.getAppContext().getContentResolver(), ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + "=?", new String[]{lookupKey}, null);
        for (Map<String, Object> phoneRow : phoneResult) {
            PhoneModel phone = new PhoneModel();
            phone.type = (String) phoneRow.get(ContactsContract.CommonDataKinds.Phone.TYPE);
            phone.number = (String) phoneRow.get(ContactsContract.CommonDataKinds.Phone.NUMBER);

            phoneList.add(phone);
        }
        return phoneList;
    }

    private static List<EmailModel> getEmailList(String lookupKey) {
        List<EmailModel> emailList = new ArrayList<>();
        List<Map<String, Object>> emailResult = DbUtils.get(ContactSyncApplication.getAppContext().getContentResolver(), ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.LOOKUP_KEY + "=?", new String[]{lookupKey}, null);
        for (Map<String, Object> emailRow : emailResult) {
            EmailModel email = new EmailModel();
            email.type = (String) emailRow.get(ContactsContract.CommonDataKinds.Email.TYPE);
            email.address = (String) emailRow.get(ContactsContract.CommonDataKinds.Email.ADDRESS);

            emailList.add(email);
        }
        return emailList;
    }

    private static List<String> getNoteList(String lookupKey) {
        List<String> noteList = new ArrayList<>();
        List<Map<String, Object>> noteResult = DbUtils.get(ContactSyncApplication.getAppContext().getContentResolver(), ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), new String[]{lookupKey, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE}, null);
        for (Map<String, Object> noteRow : noteResult) {
            String note = (String) noteRow.get(ContactsContract.CommonDataKinds.StructuredPostal.TYPE);

            noteList.add(note);
        }
        return noteList;
    }

    private static List<AddressModel> getAddressList(String lookupKey) {
        List<AddressModel> addressList = new ArrayList<>();
        List<Map<String, Object>> addressResult = DbUtils.get(ContactSyncApplication.getAppContext().getContentResolver(), ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), new String[]{lookupKey, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}, null);
        for (Map<String, Object> addressRow : addressResult) {
            AddressModel address = new AddressModel();
            address.type = (String) addressRow.get(ContactsContract.CommonDataKinds.StructuredPostal.TYPE);
            address.poBox = (String) addressRow.get(ContactsContract.CommonDataKinds.StructuredPostal.POBOX);
            address.street = (String) addressRow.get(ContactsContract.CommonDataKinds.StructuredPostal.STREET);
            address.city = (String) addressRow.get(ContactsContract.CommonDataKinds.StructuredPostal.CITY);
            address.state = (String) addressRow.get(ContactsContract.CommonDataKinds.StructuredPostal.REGION);
            address.postalCode = (String) addressRow.get(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE);
            address.country = (String) addressRow.get(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY);

            addressList.add(address);
        }
        return addressList;
    }

    private static List<ImModel> getImList(String lookupKey) {
        List<ImModel> imList = new ArrayList<>();
        List<Map<String, Object>> imResult = DbUtils.get(ContactSyncApplication.getAppContext().getContentResolver(), ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), new String[]{lookupKey, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE}, null);
        for (Map<String, Object> imRow : imResult) {
            ImModel im = new ImModel();
            im.type = (String) imRow.get(ContactsContract.CommonDataKinds.Email.TYPE);
            im.name = (String) imRow.get(ContactsContract.CommonDataKinds.Im.DATA);

            imList.add(im);
        }
        return imList;
    }

    private static OrganizationModel getOrganization(String lookupKey) {
        OrganizationModel organization = new OrganizationModel();
        List<Map<String, Object>> organizationResult = DbUtils.get(ContactSyncApplication.getAppContext().getContentResolver(), ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), new String[]{lookupKey, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}, null);
        if (!organizationResult.isEmpty()) {
            organization.title = (String) organizationResult.get(0).get(ContactsContract.CommonDataKinds.Organization.TITLE);
            organization.organization = (String) organizationResult.get(0).get(ContactsContract.CommonDataKinds.Organization.DATA);
        }
        return organization;
    }
}
