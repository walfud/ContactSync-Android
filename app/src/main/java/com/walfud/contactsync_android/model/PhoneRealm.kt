package com.walfud.contactsync_android.model

import com.walfud.contactsync_android.service.contact.PhoneModel
import io.realm.Realm
import io.realm.RealmModel
import io.realm.annotations.RealmClass

/**
 * Created by walfud on 14/05/2017.
 */
@RealmClass
open class PhoneRealm(
        open var num: String = null!!
) : RealmModel {
    companion object {

        fun valueOf(realm: Realm, phoneModel: PhoneModel): PhoneRealm {
            val phoneRealm = realm.createObject(PhoneRealm::class.java)
            phoneRealm.num = phoneModel.number!!

            return phoneRealm
        }

        fun valueOf(realm: Realm, phone: String): PhoneRealm {
            val phoneRealm = realm.createObject(PhoneRealm::class.java)
            phoneRealm.num = phone

            return phoneRealm
        }
    }
}
