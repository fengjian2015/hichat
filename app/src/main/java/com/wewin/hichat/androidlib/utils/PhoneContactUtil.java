package com.wewin.hichat.androidlib.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.wewin.hichat.model.db.dao.PhoneContactDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.PhoneContact;
import java.util.ArrayList;
import java.util.List;

public class PhoneContactUtil {

    /**
     * 获取手机通讯录
     */
    public static List<PhoneContact> getPhoneContactList(Context context) {
        List<PhoneContact> cacheContactList = new ArrayList<>();
        try {
            ContentResolver resolver = context.getContentResolver();
            //联系人的Uri
            Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumStr = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String phoneNum = phoneNumStr.replace("_", "").replace(" ", "");
                    if (phoneNum.length() >= 7 && !phoneNum.equals(UserDao.user.getPhone())
                            && !phoneNum.equals(UserDao.user.getCountryCode() + UserDao.user.getPhone())) {
                        PhoneContact phoneContact = new PhoneContact();
                        phoneContact.setName(name);
                        phoneContact.setPhone(phoneNum.replace(" ", ""));
                        if (!cacheContactList.contains(phoneContact)) {
                            cacheContactList.add(phoneContact);
                        }
                    }
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cacheContactList;
    }


    // 获取Sim卡联系人
    private List<PhoneContact> getSimPhoneContactList(Context context) {
        List<PhoneContact> cacheContactList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, null, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                String contactName = phoneCursor.getString(phoneCursor.getColumnIndex("name"));
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex("number"));
                if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 7) {
                    continue;
                }
                PhoneContact phoneContact = new PhoneContact();
                phoneContact.setName(contactName);
                phoneContact.setPhone(phoneNumber);
                cacheContactList.add(phoneContact);
            }
            phoneCursor.close();
        }
        return cacheContactList;
    }

    public List<PhoneContact> getContactsList(Context context) {
        LogUtil.i("SMSHelper", "-----------SMSHelper#getContactsList()----------");
        LogUtil.i("SMSHelper", "开始查询 Contacts 表");
        // 访问地址
        Uri raw_contacts = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri data = Uri.parse("content://com.android.contacts/data");

        List<PhoneContact> cacheList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(raw_contacts, new String[]{"contact_id"}, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) {
            return cacheList;
        }
        LogUtil.i("SMSHelper", "cursor.getCount():" + cursor.getCount());

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("contact_id"));
            Cursor itemCursor = resolver.query(data, new String[]{"mimetype", "data1"},
                    "raw_contact_id = ?", new String[]{id}, null);
            if (itemCursor == null || itemCursor.getCount() <= 0){
                return cacheList;
            }
            String name = "";
            String phone = "";
            while (itemCursor.moveToNext()) {
                String mimetype = itemCursor.getString(itemCursor.getColumnIndex("mimetype"));
                String data1 = itemCursor.getString(itemCursor.getColumnIndex("data1"));
                if ("vnd.android.cursor.item/name".equals(mimetype)) {
                    name = data1;
                } else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                    // 有的手机号中间会带有空格
                    phone = data1.replace(" ","");
                }
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)){
                    PhoneContact phoneContact = new PhoneContact(phone, name);
                    cacheList.add(phoneContact);
                }
            }
            itemCursor.close();
        }
        cursor.close();
        LogUtil.i("cacheList.size", cacheList.size());
        return cacheList;
    }

    /**
     * 根据联系人姓名查询电话
     * 并写入
     */
    private List<PhoneContact> getPhoneNumByName(ContentResolver contentResolver, String name, String id) {
        LogUtil.i("SMSHelper", "开始查询 Data 表 : 查询联系人：" + name);
        List<PhoneContact> cacheList = new ArrayList<>();
        String selectStr = ContactsContract.RawContactsEntity.RAW_CONTACT_ID;
//        String selectStr = ContactsContract.Contacts.DISPLAY_NAME;
        Cursor dataCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                selectStr + " =? and mimetype_id = 5",
                new String[]{id}, null);
        if (dataCursor == null || dataCursor.getCount() <= 0) {
            LogUtil.i("SMSHelper", "dataCursor == null ");
            return cacheList;
        }
        LogUtil.i("SMSHelper", " 电话信息 -- size: " + dataCursor.getCount());
        while (dataCursor.moveToNext()) {
            String dataId = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data._ID));
            String rawContactId = dataCursor.getString(dataCursor
                    .getColumnIndex(ContactsContract.RawContactsEntity.RAW_CONTACT_ID));
            String numberStr = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DATA1));
            if (TextUtils.isEmpty(numberStr) || numberStr.length() < 7) {
                LogUtil.i("SMSHelper", " 电话信息(异常) -- number: " + numberStr);
                continue;
            }
            String phoneNumber = numberStr.replace(" ", "");
            PhoneContact phoneContact = new PhoneContact();
            phoneContact.setName(name);
            phoneContact.setPhone(phoneNumber);
            cacheList.add(phoneContact);
            LogUtil.i("id", id);
            LogUtil.i("dataId", dataId);
            LogUtil.i("rawContactId", rawContactId);

            LogUtil.i("SMSHelper", " 电话信息 -- number: " + phoneNumber);
        }
        dataCursor.close();
        return cacheList;
    }

}
