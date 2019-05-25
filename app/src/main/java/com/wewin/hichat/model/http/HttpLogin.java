package com.wewin.hichat.model.http;

import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.HttpUtil;
import com.wewin.hichat.component.constant.HttpCons;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Darren on 2018/12/18.
 */
public class HttpLogin {

    //获取短信 type: retrieve:找回密码 ; register 注册
    public static void getSms(String areaCode, String phoneNum, String type, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(3);
        map.put("areaCode", areaCode);
        map.put("phone", phoneNum);
        map.put("type", type);
        HttpUtil.post(HttpCons.PATH_LOGIN_SMS, map, httpCallBack);
    }

    //校验验证码
    public static void checkVerifyCode(String areaCode, String code, String phone, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(3);
        map.put("areaCode", areaCode);
        map.put("code", code);
        map.put("phone", phone);
        HttpUtil.post(HttpCons.PATH_LOGIN_CHECK_SMS, map, httpCallBack);
    }

    //注册
    public static void register(String areaCode, int gender, String nickName, String password,
                                String phone, String sign, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(6);
        map.put("areaCode", areaCode);
        map.put("gender", String.valueOf(gender));
        map.put("nickName", nickName);
        map.put("password", password);
        map.put("phone", phone);
        map.put("sign", sign);
        HttpUtil.post(HttpCons.PATH_LOGIN_REGISTER, map, httpCallBack);
    }

    //登录
    public static void login(String phone, String password, String type, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(4);
        map.put("phone", phone);
        map.put("password", password);
        map.put("terminal", "ANDROID");
        map.put("type", type);
        HttpUtil.post(HttpCons.PATH_LOGIN, map, httpCallBack);
    }

    //修改密码
    public static void modifyPassword(String newPassword, String oldPassword, String phone, int type,
                                      HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(3);
        map.put("newPassword", newPassword);
        map.put("oldPassword", oldPassword);
        map.put("phone", phone);
        HttpUtil.post(HttpCons.PATH_LOGIN_MODIFY_PWD, map, httpCallBack);
    }

    //找回密码
    public static void retrievePassword(String newPassword, String phone, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(2);
        map.put("newPassword", newPassword);
        map.put("phone", phone);
        HttpUtil.post(HttpCons.PATH_LOGIN_RETRIEVE_PWD, map, httpCallBack);
    }

    //退出登录
    public static void logout(HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(1);
        map.put("terminal", "ANDROID");
        HttpUtil.post(HttpCons.PATH_LOGOUT, map, httpCallBack);
    }

}
