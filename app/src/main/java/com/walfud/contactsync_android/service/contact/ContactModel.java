package com.walfud.contactsync_android.service.contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by walfud on 2017/5/7.
 */

public class ContactModel {
    public boolean isDeleted;
    public String displayName;
    public List<PhoneModel> phoneList = new ArrayList<>();
    public List<EmailModel> emailList = new ArrayList<>();
    public List<String> noteList = new ArrayList<>();
    public List<AddressModel> addressList = new ArrayList<>();
    public List<ImModel> imList = new ArrayList<>();
    public OrganizationModel organization;
}
