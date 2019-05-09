package com.wewin.hichat.androidlib.utils;

import android.content.Context;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.umeng.UMMessage;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.LoginUser;
import com.wewin.hichat.model.socket.ChatSocket;

/**
 * Created by Darren on 2019/5/8
 **/
public class CommonUtil {

    /**
     * 退出登录清除数据
     */
    public static void clearDataByLogout(Context context){
        SpCons.setCuid(context, "");
        SpCons.setDomain(context, "");
        UMMessage.getInstance().deleteAlias();
        UserDao.user = new LoginUser();
        SpCons.setLoginState(context, false);
        ChatSocket.getInstance().stop();
    }



}
