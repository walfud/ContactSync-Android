package com.walfud.contactsync_android.service.contact

/**
 * Created by walfud on 2017/5/7.
 */

class ContactModel {
    var id: Long = 0
    var isDeleted: Boolean = false
    var modifyTime: Long = 0

    var displayName: String? = null
    var phoneList: List<PhoneModel>? = null
    var emailList: List<EmailModel>? = null
    var noteList: List<String>? = null
    var addressList: List<AddressModel>? = null
    var imList: List<ImModel>? = null
    var organization: OrganizationModel? = null
}
