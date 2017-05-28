package com.walfud.contactsync_android.model

import com.walfud.contactsync_android.SyncMutation
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

/**
 * Created by walfud on 14/05/2017.
 */
@RealmClass
open class ContactRealm(
        @PrimaryKey open var id: String? = null,
        @Required open var localId: Long? = null,
        open var isDeleted: Boolean = false,
        open var modifyTime: Long = 0,
        open var name: String? = null,
        open var phoneRealmList: RealmList<PhoneRealm>? = null
) : RealmModel {
    companion object {

        fun valueOf(realm: Realm, contact: SyncMutation.Data.Contact): ContactRealm {
            val contactRealm = realm.createObject(ContactRealm::class.java, contact.id())
            contactRealm.isDeleted = contact.is_deleted!! or false
            contactRealm.modifyTime = (contact.modify_time() as Number).toLong()
            contactRealm.name = contact.name()
//            contactRealm.phoneRealmList!!.addAll(contact.phones()!!.map({ phone -> PhoneRealm.valueOf(realm, phone) }))

            return contactRealm
        }
    }
}
