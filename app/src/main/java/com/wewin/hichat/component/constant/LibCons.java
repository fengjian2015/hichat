package com.wewin.hichat.component.constant;

import com.wewin.hichat.MainApplication;

/**
 * @author Jason
 * date:2019/5/410:32
 */
public class LibCons {

    //友盟相关配置
    public static final String UM_CHANNEL = MainApplication.IS_DEBUG ? "测试" : "生产";//友盟渠道
    public static final String UM_APP_KEY = "5ccfd3604ca357bcdc000743";
    public static final String UMENG_MESSAGE_SECRET = "fd330943ab631508e8d35cef98f60b8a";

    //语音通话sdk appId
    public static final String AGORA_APP_ID = "b5c008617e784d4185633b95b14e91db";

}
