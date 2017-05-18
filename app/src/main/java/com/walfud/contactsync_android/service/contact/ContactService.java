package com.walfud.contactsync_android.service.contact;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;

import com.walfud.walle.android.DbUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by walfud on 2017/5/8.
 */

public class ContactService {
    public static List<ContactModel> getContactList(Context context) {
        List<ContactModel> contactList = new ArrayList<>();

        // Normal Contacts
        List<Map<String, Object>> contactResult = DbUtils.get(context.getContentResolver(), ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        contactResult.stream()
                .forEach(contactRow -> {
                    ContactModel contact = new ContactModel();
                    contact.id = (Long) contactRow.get("_id");
                    String lookupKey = (String) contactRow.get(ContactsContract.Contacts.LOOKUP_KEY);
                    contact.displayName = (String) contactRow.get(ContactsContract.Contacts.DISPLAY_NAME);
                    contact.isDeleted = false;
                    contact.modifyTime = (Long) contactRow.get(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP);
                    contact.phoneList = getPhoneList(context, lookupKey);
                    contact.emailList = getEmailList(context, lookupKey);
                    contact.noteList = getNoteList(context, lookupKey);
                    contact.addressList = getAddressList(context, lookupKey);
                    contact.imList = getImList(context, lookupKey);
                    contact.organization = getOrganization(context, lookupKey);

                    contactList.add(contact);
                });
        // Deleted Contacts
        List<Map<String, Object>> deletedContactResult = DbUtils.get(context.getContentResolver(), Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "raw_contacts"), null, null, null, null);
        deletedContactResult.stream()
                .filter(contactRow -> (Long) contactRow.get(ContactsContract.RawContacts.DELETED) != 0)
                .forEach(contactRow -> {
                    ContactModel contact = new ContactModel();
                    contact.id = (Long) contactRow.get(ContactsContract.Data._ID);
                    contact.displayName = (String) contactRow.get(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY);
                    contact.isDeleted = true;
                    List<Map<String, Object>> deletedResult = DbUtils.get(context.getContentResolver(), ContactsContract.DeletedContacts.CONTENT_URI, null, ContactsContract.DeletedContacts.CONTACT_ID + "=?", new String[]{String.valueOf(contact.id)}, null);
                    contact.modifyTime = (Long) deletedResult.get(0).get(ContactsContract.DeletedContacts.CONTACT_DELETED_TIMESTAMP);

                    contactList.add(contact);
                });

        return contactList;
    }

    private static String getDisplayName(Context context, String lookupKey) {
        String displayName = "";
        List<Map<String, Object>> structuredNameResult = DbUtils.get(context.getContentResolver(), ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), new String[]{lookupKey, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}, null);
        if (!structuredNameResult.isEmpty()) {
            displayName = (String) structuredNameResult.get(0).get(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
        }
        return displayName;
    }

    private static List<PhoneModel> getPhoneList(Context context, String lookupKey) {
        List<PhoneModel> phoneList = new ArrayList<>();
        List<Map<String, Object>> phoneResult = DbUtils.get(context.getContentResolver(), ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + "=?", new String[]{lookupKey}, null);
        for (Map<String, Object> phoneRow : phoneResult) {
            PhoneModel phone = new PhoneModel();
            phone.type = (String) phoneRow.get(ContactsContract.CommonDataKinds.Phone.TYPE);
            phone.number = (String) phoneRow.get(ContactsContract.CommonDataKinds.Phone.NUMBER);

            phoneList.add(phone);
        }
        return phoneList;
    }

    private static List<EmailModel> getEmailList(Context context, String lookupKey) {
        List<EmailModel> emailList = new ArrayList<>();
        List<Map<String, Object>> emailResult = DbUtils.get(context.getContentResolver(), ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.LOOKUP_KEY + "=?", new String[]{lookupKey}, null);
        for (Map<String, Object> emailRow : emailResult) {
            EmailModel email = new EmailModel();
            email.type = (String) emailRow.get(ContactsContract.CommonDataKinds.Email.TYPE);
            email.address = (String) emailRow.get(ContactsContract.CommonDataKinds.Email.ADDRESS);

            emailList.add(email);
        }
        return emailList;
    }

    private static List<String> getNoteList(Context context, String lookupKey) {
        List<String> noteList = new ArrayList<>();
        List<Map<String, Object>> noteResult = DbUtils.get(context.getContentResolver(), ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), new String[]{lookupKey, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE}, null);
        for (Map<String, Object> noteRow : noteResult) {
            String note = (String) noteRow.get(ContactsContract.CommonDataKinds.StructuredPostal.TYPE);

            noteList.add(note);
        }
        return noteList;
    }

    private static List<AddressModel> getAddressList(Context context, String lookupKey) {
        List<AddressModel> addressList = new ArrayList<>();
        List<Map<String, Object>> addressResult = DbUtils.get(context.getContentResolver(), ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), new String[]{lookupKey, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}, null);
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

    private static List<ImModel> getImList(Context context, String lookupKey) {
        List<ImModel> imList = new ArrayList<>();
        List<Map<String, Object>> imResult = DbUtils.get(context.getContentResolver(), ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), new String[]{lookupKey, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE}, null);
        for (Map<String, Object> imRow : imResult) {
            ImModel im = new ImModel();
            im.type = (String) imRow.get(ContactsContract.CommonDataKinds.Email.TYPE);
            im.name = (String) imRow.get(ContactsContract.CommonDataKinds.Im.DATA);

            imList.add(im);
        }
        return imList;
    }

    private static OrganizationModel getOrganization(Context context, String lookupKey) {
        OrganizationModel organization = new OrganizationModel();
        List<Map<String, Object>> organizationResult = DbUtils.get(context.getContentResolver(), ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), new String[]{lookupKey, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}, null);
        if (!organizationResult.isEmpty()) {
            organization.title = (String) organizationResult.get(0).get(ContactsContract.CommonDataKinds.Organization.TITLE);
            organization.organization = (String) organizationResult.get(0).get(ContactsContract.CommonDataKinds.Organization.DATA);
        }
        return organization;
    }

    /**
     *
     * @param context
     * @param name
     * @param phoneList
     * @return the row id if success, fail is -1
     */
    public static long insert(Context context, String name, List<String> phoneList) {
        ArrayList<ContentProviderOperation> allOp = new ArrayList<>();

        // raw_contacts: display_name
        allOp.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.Contacts.DISPLAY_NAME, name)
                .build());
        // data: name
        allOp.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.Data.DATA1, name)
                .build());
        // data: [phones]
        allOp.addAll(phoneList.stream()
                .map(phone -> ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.Data.DATA1, phone)
                        .build())
                .collect(Collectors.toList()));

        try {
            ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, allOp);
            return Long.valueOf(results[0].uri.getLastPathSegment());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean delete(Context context, long localId) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(localId));
        return context.getContentResolver().delete(contactUri, null, null) > 0;
    }

    public static boolean update(Context context, long localId, String newName, List<String> newPhoneList) {
        context.getContentResolver().delete(ContactsContract.Data.CONTENT_URI, String.format("%s=?", ContactsContract.Data.RAW_CONTACT_ID), new String[]{String.valueOf(localId)});

        // name
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, localId);
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName);
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, newName);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues);
        }
        // phones
        for (String phone : newPhoneList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, localId);
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues);
        }

        return true;
    }
}
