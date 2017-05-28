package com.walfud.contactsync_android.service.contact

import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import com.walfud.walle.android.DbUtils
import java.util.*

/**
 * Created by walfud on 2017/5/8.
 */

object ContactService {
    fun getContactList(context: Context): List<ContactModel> {
        val contactList = ArrayList<ContactModel>()

        // Normal Contacts
        val contactResult = DbUtils.get(context.contentResolver, ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC")
        contactResult
                .forEach { contactRow ->
                    val contact = ContactModel()
                    contact.id = contactRow["_id"] as Long
                    contact.isDeleted = false
                    contact.modifyTime = contactRow[ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP] as Long

                    val lookupKey = contactRow[ContactsContract.Contacts.LOOKUP_KEY] as String
                    contact.displayName = contactRow[ContactsContract.Contacts.DISPLAY_NAME] as String?
                    contact.phoneList = getPhoneList(context, lookupKey)
                    contact.emailList = getEmailList(context, lookupKey)
                    contact.noteList = getNoteList(context, lookupKey)
                    contact.addressList = getAddressList(context, lookupKey)
                    contact.imList = getImList(context, lookupKey)
                    contact.organization = getOrganization(context, lookupKey)

                    contactList.add(contact)
                }
        // Deleted Contacts
        val deletedContactResult = DbUtils.get(context.contentResolver, Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "raw_contacts"), null, null, null, null)
        deletedContactResult
                .filter { contactRow -> contactRow[ContactsContract.RawContacts.DELETED]!! != 0 }
                .forEach { contactRow ->
                    val contact = ContactModel()
                    contact.id = contactRow[ContactsContract.Data._ID] as Long
                    contact.isDeleted = true
                    val deletedResult = DbUtils.get(context.contentResolver, ContactsContract.DeletedContacts.CONTENT_URI, null, ContactsContract.DeletedContacts.CONTACT_ID + "=?", arrayOf(contact.id.toString()), null)
                    contact.modifyTime = deletedResult[0][ContactsContract.DeletedContacts.CONTACT_DELETED_TIMESTAMP] as Long
                    contact.displayName = contactRow[ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY] as String?

                    contactList.add(contact)
                }

        return contactList
    }

    private fun getDisplayName(context: Context, lookupKey: String): String? {
        var displayName: String? = null
        val structuredNameResult = DbUtils.get(context.contentResolver, ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), arrayOf(lookupKey, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE), null)
        if (!structuredNameResult.isEmpty()) {
            displayName = structuredNameResult[0][ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME] as String?
        }
        return displayName
    }

    private fun getPhoneList(context: Context, lookupKey: String): List<PhoneModel> {
        val phoneList = ArrayList<PhoneModel>()
        val phoneResult = DbUtils.get(context.contentResolver, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + "=?", arrayOf(lookupKey), null)
        for (phoneRow in phoneResult) {
            val phone = PhoneModel()
            phone.type = phoneRow[ContactsContract.CommonDataKinds.Phone.TYPE] as String?
            phone.number = phoneRow[ContactsContract.CommonDataKinds.Phone.NUMBER] as String?

            phoneList.add(phone)
        }
        return phoneList
    }

    private fun getEmailList(context: Context, lookupKey: String): List<EmailModel> {
        val emailList = ArrayList<EmailModel>()
        val emailResult = DbUtils.get(context.contentResolver, ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.LOOKUP_KEY + "=?", arrayOf(lookupKey), null)
        for (emailRow in emailResult) {
            val email = EmailModel()
            email.type = emailRow[ContactsContract.CommonDataKinds.Email.TYPE] as String?
            email.address = emailRow[ContactsContract.CommonDataKinds.Email.ADDRESS] as String?

            emailList.add(email)
        }
        return emailList
    }

    private fun getNoteList(context: Context, lookupKey: String): List<String>? {
        val noteResult = DbUtils.get(context.contentResolver, ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), arrayOf(lookupKey, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE), null)
        return noteResult?.map { it[ContactsContract.CommonDataKinds.StructuredPostal.TYPE] as String }
    }

    private fun getAddressList(context: Context, lookupKey: String): List<AddressModel> {
        val addressList = ArrayList<AddressModel>()
        val addressResult = DbUtils.get(context.contentResolver, ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), arrayOf(lookupKey, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE), null)
        for (addressRow in addressResult) {
            val address = AddressModel()
            address.type = addressRow[ContactsContract.CommonDataKinds.StructuredPostal.TYPE] as String?
            address.poBox = addressRow[ContactsContract.CommonDataKinds.StructuredPostal.POBOX] as String?
            address.street = addressRow[ContactsContract.CommonDataKinds.StructuredPostal.STREET] as String?
            address.city = addressRow[ContactsContract.CommonDataKinds.StructuredPostal.CITY] as String?
            address.state = addressRow[ContactsContract.CommonDataKinds.StructuredPostal.REGION] as String?
            address.postalCode = addressRow[ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE] as String?
            address.country = addressRow[ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY] as String?

            addressList.add(address)
        }
        return addressList
    }

    private fun getImList(context: Context, lookupKey: String): List<ImModel> {
        val imList = ArrayList<ImModel>()
        val imResult = DbUtils.get(context.contentResolver, ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), arrayOf(lookupKey, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE), null)
        for (imRow in imResult) {
            val im = ImModel()
            im.type = imRow[ContactsContract.CommonDataKinds.Email.TYPE] as String?
            im.name = imRow[ContactsContract.CommonDataKinds.Im.DATA] as String?

            imList.add(im)
        }
        return imList
    }

    private fun getOrganization(context: Context, lookupKey: String): OrganizationModel {
        val organization = OrganizationModel()
        val organizationResult = DbUtils.get(context.contentResolver, ContactsContract.Data.CONTENT_URI, null, String.format("%s=? AND %s=?", ContactsContract.Data.LOOKUP_KEY, ContactsContract.Data.MIMETYPE), arrayOf(lookupKey, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE), null)
        if (!organizationResult.isEmpty()) {
            organization.title = organizationResult[0][ContactsContract.CommonDataKinds.Organization.TITLE] as String?
            organization.organization = organizationResult[0][ContactsContract.CommonDataKinds.Organization.DATA] as String?
        }
        return organization
    }

    /**

     * @param context
     * *
     * @param name
     * *
     * @param phoneList
     * *
     * @return the row id if success, fail is -1
     */
    fun insert(context: Context, name: String, phoneList: List<String>): Long {
        val allOp = ArrayList<ContentProviderOperation>()

        // raw_contacts: display_name
        allOp.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.Contacts.DISPLAY_NAME, name)
                .build())
        // data: name
        allOp.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.Data.DATA1, name)
                .build())
        // data: [phones]
        allOp.addAll(phoneList.map { phone ->
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.Data.DATA1, phone)
                    .build()
        })
        try {
            val results = context.contentResolver.applyBatch(ContactsContract.AUTHORITY, allOp)
            return java.lang.Long.valueOf(results[0].uri.lastPathSegment)!!
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }

    }

    fun delete(context: Context, localId: Long): Boolean {
        val contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, localId.toString())
        return context.contentResolver.delete(contactUri, null, null) > 0
    }

    fun update(context: Context, localId: Long, newName: String, newPhoneList: List<String>): Boolean {
        context.contentResolver.delete(ContactsContract.Data.CONTENT_URI, String.format("%s=?", ContactsContract.Data.RAW_CONTACT_ID), arrayOf(localId.toString()))

        // name
        run {
            val contentValues = ContentValues()
            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, localId)
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName)
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, newName)
            context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues)
        }
        // phones
        for (phone in newPhoneList) {
            val contentValues = ContentValues()
            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, localId)
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
            contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
            context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues)
        }

        return true
    }
}
