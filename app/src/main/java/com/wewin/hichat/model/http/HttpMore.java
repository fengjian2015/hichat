package com.wewin.hichat.model.http;

import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.HttpUtil;
import com.wewin.hichat.component.constant.HttpCons;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Darren on 2018/12/28.
 */
public class HttpMore {

    //修改账号信息
    public static void modifyAccountInfo(int audioCues, int gender, String id, String sign,
                                         String username, int vibratesCues, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put("audioCues", String.valueOf(audioCues));
        map.put("gender", String.valueOf(gender));
        map.put("id", id);
        map.put("sign", sign);
        map.put("username", username);
        map.put("vibratesCues", String.valueOf(vibratesCues));
        HttpUtil.post(HttpCons.PATH_MORE_MODIFY_ACCOUNT, map, httpCallBack);
    }

    //上传个人头像
    public static void uploadPersonalAvatar(File File, String id, String pass, String suffix, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("pass", pass);
        map.put("suffix", suffix);
        HttpUtil.postFileFormData(HttpCons.PATH_MORE_PERSONAL_AVATAR_LOAD, "file", File, map, httpCallBack);
    }

    //获取服务器时间戳
    public static void getServiceTimestamp(HttpCallBack httpCallBack) {
        HttpUtil.get(HttpCons.PATH_MORE_GET_SERVICE_TIMESTAMP, httpCallBack);
    }

    //获取个人信息
    public static void getPersonalInfo(HttpCallBack httpCallBack) {
        HttpUtil.get(HttpCons.PATH_MORE_GET_PERSONAL_INFO, httpCallBack);
    }

    //获取登录记录
    public static void getLoginRecord(HttpCallBack httpCallBack){
        HttpUtil.get(HttpCons.PATH_MORE_GET_LOGIN_RECORD, httpCallBack);
    }

    //设置隐身//0在线；1隐身
    public static void setHide(int type, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>();
        map.put("type", String.valueOf(type));
        HttpUtil.post(HttpCons.PATH_MORE_SET_HIDE, map, httpCallBack);
    }

}
