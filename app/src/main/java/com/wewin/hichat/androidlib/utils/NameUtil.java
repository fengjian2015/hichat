package com.wewin.hichat.androidlib.utils;

import android.text.TextUtils;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.entity.FriendInfo;

/**
 * @author:jason date:2019/6/1118:55
 */
public class NameUtil {
    /**
     * 返回名字
     */
    public static String getName(String userId){
        String name = null;
        FriendInfo friendInfo=FriendDao.getFriendInfo(userId);
        if (friendInfo==null){
            friendInfo=ContactUserDao.getContactUser(userId);
        }
        if (friendInfo!=null) {
            if (!TextUtils.isEmpty(friendInfo.getFriendNote())) {
                name = friendInfo.getFriendNote();
            } else {
                name = friendInfo.getUsername();
            }
        }
        if (TextUtils.isEmpty(name)){
            name=userId;
        }
        return name;
    }
}
