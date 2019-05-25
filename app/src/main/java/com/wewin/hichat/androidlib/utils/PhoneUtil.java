package com.wewin.hichat.androidlib.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.model.db.dao.PhoneContactDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.PhoneContact;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PhoneUtil {


    /**
     * 根据区号判断是否是正确的电话号码
     *
     * @param phoneNum    :不带国家码的电话号码
     * @param countryCode :默认国家码
     */
    public static boolean isPhoneNumValid(String countryCode, String phoneNum) {
        if (TextUtils.isEmpty(countryCode) || TextUtils.isEmpty(phoneNum)) {
            return false;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(countryCode + phoneNum, countryCode);
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            System.err.println("isPhoneNumberValid NumberParseException was thrown: " + e.toString());
        }
        return false;
    }

    /**
     * 获取手机通讯录
     */
    public static List<PhoneContact> getPhoneContactList(Context context) {
        List<PhoneContact> cacheContactList = new ArrayList<>();
        try {
            ContentResolver resolver = context.getContentResolver();
            //联系人的Uri
            Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null);
            if (cursor == null) {
                return cacheContactList;
            }
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract
                        .CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumStr = cursor.getString(cursor.getColumnIndex(ContactsContract
                        .CommonDataKinds.Phone.NUMBER));
                String phoneNum = phoneNumStr.replace("_", "").replace(" ", "")
                        .replace("-", "").replace("+", "");
                if (phoneNum.length() >= 7 && !phoneNum.equals(SpCons.getUser(context).getPhone())
                        && !phoneNum.equals(SpCons.getUser(context).getCountryCode() +
                        SpCons.getUser(context).getPhone())) {
                    PhoneContact phoneContact = new PhoneContact();
                    phoneContact.setName(name);
                    phoneContact.setPhone(phoneNum);
                    if (!cacheContactList.contains(phoneContact)) {
                        cacheContactList.add(phoneContact);
                    }
                }
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cacheContactList;
    }


}
