/*
 * Copyright © Zhenjie Yan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wewin.hichat.androidlib.permission;

import android.os.Build;

import java.util.HashMap;

/**
 *  作者：created by jason on 2019/4/5 13
 */
public class Permission {
    public static HashMap permissionMap=new HashMap();

    public static String[] permissions=new String[]{"android.permission.READ_CALENDAR","android.permission.WRITE_CALENDAR","android.permission.CAMERA","android.permission.READ_CONTACTS"
            ,"android.permission.WRITE_CONTACTS","android.permission.GET_ACCOUNTS","android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION"
            ,"android.permission.RECORD_AUDIO","android.permission.READ_PHONE_STATE","android.permission.CALL_PHONE","android.permission.READ_CALL_LOG","android.permission.WRITE_CALL_LOG"
            ,"com.android.voicemail.permission.ADD_VOICEMAIL","android.permission.USE_SIP","android.permission.PROCESS_OUTGOING_CALLS","android.permission.READ_PHONE_NUMBERS"
            ,"android.permission.BODY_SENSORS","android.permission.SEND_SMS","android.permission.RECEIVE_SMS","android.permission.READ_SMS","android.permission.RECEIVE_WAP_PUSH"
            ,"android.permission.RECEIVE_MMS","android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.REQUEST_INSTALL_PACKAGES"
            ,"android.permission.SYSTEM_ALERT_WINDOW"};

    public static String[] permissionsName=new String[]{"应用程序读取用户日历数据","应用程序写入用户日程","应用程序使用照相机","应用程序读取联系人信息"
            ,"应用程序写入用户联系人","应用程序访问GMail账户列表","应用程序通过GPS获取精确的位置信息","应用程序通过WiFi或移动基站获取粗略的位置信息"
            ,"应用程序录制音频","应用程序读取电话状态","应用程序拨打电话","应用程序读取通话记录","应用程序写入通话记录"
            ,"应用程序使用语音邮件","应用程序使用SIP视频服务","应用程序监视、修改外拨电话","应用读取手机号码"
            ,"应用身体传感器","应用程序发送短信","应用程序接收短信","应用程序读取短信内容","应用程序接收WAP PUSH信息"
            ,"应用程序接收彩信","应用程序读取扩展存储器","应用程序写入外部存储","允许安装未知来源应用"
            ,"应用程序打开系统窗口"};


    public static final String READ_CALENDAR = "android.permission.READ_CALENDAR" ;
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR" ;

    public static final String CAMERA = "android.permission.CAMERA";

    public static final String READ_CONTACTS ="android.permission.READ_CONTACTS";
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";

    public static final String ACCESS_FINE_LOCATION ="android.permission.ACCESS_FINE_LOCATION";
    public static final String ACCESS_COARSE_LOCATION ="android.permission.ACCESS_COARSE_LOCATION";

    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";

    public static final String READ_PHONE_STATE ="android.permission.READ_PHONE_STATE";
    public static final String CALL_PHONE ="android.permission.CALL_PHONE";
    public static final String READ_CALL_LOG ="android.permission.READ_CALL_LOG";
    public static final String WRITE_CALL_LOG ="android.permission.WRITE_CALL_LOG";
    public static final String ADD_VOICEMAIL ="com.android.voicemail.permission.ADD_VOICEMAIL";
    public static final String USE_SIP ="android.permission.USE_SIP";
    public static final String PROCESS_OUTGOING_CALLS ="android.permission.PROCESS_OUTGOING_CALLS";
    public static final String READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS";

    public static final String BODY_SENSORS ="android.permission.BODY_SENSORS";

    public static final String SEND_SMS ="android.permission.SEND_SMS";
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";
    public static final String READ_SMS ="android.permission.READ_SMS";
    public static final String RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH";
    public static final String RECEIVE_MMS = "android.permission.RECEIVE_MMS";

    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE ="android.permission.WRITE_EXTERNAL_STORAGE";
    public static final String REQUEST_INSTALL_PACKAGES ="android.permission.REQUEST_INSTALL_PACKAGES";
    public static final String SYSTEM_ALERT_WINDOW ="android.permission.SYSTEM_ALERT_WINDOW";

    public static final class Group {

        public static final String[] CALENDAR = new String[] {Permission.READ_CALENDAR, Permission.WRITE_CALENDAR};

        public static final String[] CAMERA =  new String[] {Permission.CAMERA};

        public static final String[] CONTACTS = new String[] {Permission.READ_CONTACTS, Permission.WRITE_CONTACTS, Permission.GET_ACCOUNTS};

        public static final String[] LOCATION = new String[] {Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION};

        public static final String[] MICROPHONE = new String[] {Permission.RECORD_AUDIO};

        public static final String[] PHONE;

        static {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PHONE = new String[] {Permission.READ_PHONE_STATE, Permission.CALL_PHONE, Permission.READ_CALL_LOG,
                    Permission.WRITE_CALL_LOG, Permission.ADD_VOICEMAIL, Permission.USE_SIP,
                    Permission.PROCESS_OUTGOING_CALLS, Permission.READ_PHONE_NUMBERS};
            } else {
                PHONE = new String[] {Permission.READ_PHONE_STATE, Permission.CALL_PHONE, Permission.READ_CALL_LOG,
                    Permission.WRITE_CALL_LOG, Permission.ADD_VOICEMAIL, Permission.USE_SIP,
                    Permission.PROCESS_OUTGOING_CALLS};
            }
        }

        public static final String[] SENSORS = new String[] {Permission.BODY_SENSORS};

        public static final String[] SMS = new String[] {Permission.SEND_SMS, Permission.RECEIVE_SMS,
            Permission.READ_SMS, Permission.RECEIVE_WAP_PUSH, Permission.RECEIVE_MMS};

        public static final String[] STORAGE = new String[] {Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE};
    }

}