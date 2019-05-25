package com.wewin.hichat.model.http;

import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.HttpUtil;
import com.wewin.hichat.component.constant.HttpCons;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Darren on 2018/12/19.
 */
public class HttpContact {

    //获取群列表
    public static void getGroupList(HttpCallBack httpCallBack) {
        HttpUtil.get(HttpCons.PATH_CONTACT_GROUP_LIST, httpCallBack);
    }

    //获取好友列表
    public static void getSubgroupFriendList(HttpCallBack httpCallBack){
        HttpUtil.get(HttpCons.PATH_CONTACT_FRIEND_LIST, httpCallBack);
    }

    //添加好友
    public static void applyAddFriendGroup(String friendId, String groupId, String remark, int type,
                                           String groupingId, String uid, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>(6);
        map.put("friendId", friendId);
        map.put("groupId", groupId);
        map.put("remark", remark);
        map.put("type", String.valueOf(type));
        map.put("groupingId", groupingId);
        map.put("uid", uid);
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_APPLY_ADD, map, httpCallBack);
    }

    //申请添加好友
    public static void applyAddFriend(String receiveUserId, String verifyInfo, String subgroupId,
                                      String userId, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(4);
        map.put("receiveUserId", receiveUserId);
        map.put("verifyInfo", verifyInfo);
        map.put("groupingId", subgroupId);
        map.put("userId", userId);
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_APPLY_ADD, map, httpCallBack);
    }

    //申请加入群组
    public static void applyJoinGroup(String groupNum, String verifyInfo, String userId, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(3);
        map.put("groupNum", groupNum);
        map.put("verifyInfo", verifyInfo);
        map.put("userId", userId);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_APPLY_JOIN, map, httpCallBack);
    }

    //找人
    public static void searchFriend(int page, String phoneNum,
                                    HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>(3);
        map.put("limit", String.valueOf(10));
        map.put("page", String.valueOf(page));
        map.put("value", phoneNum);
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_SEARCH, map, httpCallBack);
    }

    //创建群
    public static void createGroup(String description, int groupMax, String groupName, String accountId,
                                   int groupValid, String friendIdStr, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>(6);
        map.put("groupName", groupName);
        map.put("description", description);
        map.put("friendIdStr", friendIdStr);
        map.put("groupMax", String.valueOf(groupMax));
        map.put("groupValid", String.valueOf(groupValid));
        map.put("accountId", accountId);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_CREATE, map, httpCallBack);
    }

    //获取群信息
    public static void getGroupInfo(String groupId, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>(1);
        map.put("groupId", groupId);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_INFO, map, httpCallBack);
    }

    //搜索群
    public static void searchGroup(int page, String value, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>(3);
        map.put("limit", String.valueOf(10));
        map.put("page", String.valueOf(page));
        map.put("value", value);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_SEARCH, map, httpCallBack);
    }

    //修改好友备注
    public static void modifyFriendNote(String accountId, String friendId, String friendNote,int friendShipMark,
                                        HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>(4);
        map.put("accountId", accountId);
        map.put("friendId", friendId);
        map.put("friendNote", friendNote);
        map.put("friendShipMark",String.valueOf(friendShipMark));
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_MODIFY_NOTE, map, httpCallBack);
    }

    //查询好友分组
    public static void getFriendSubgroup(String id, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(1);
        map.put("id", id);
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_GET_SUBGROUP, map, httpCallBack);
    }

    //删除好友
    public static void deleteFriend(String accountId, String friendId, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(2);
        map.put("accountId", accountId);
        map.put("friendId", friendId);
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_DELETE, map, httpCallBack);
    }

    /**
     * 上传文件
     * @param flag 文件类型: 1 图片 2 视频 3文件 4音频
     * @param toId 接受方ID（可以是群ID或者好友ID）
     * @param type 传输类型: friend 好友 group 好友
     */
    public static void uploadFile(String filePath, int flag, String toId, String type, long tapeDuration,
                                  HttpCallBack httpCallBack){
        uploadFile(new File(filePath), flag, toId, type, tapeDuration, httpCallBack);
    }

    private static void uploadFile(File file, int flag, String toId, String type, long tapeDuration,
                                  HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(5);
        map.put("duration", String.valueOf(tapeDuration));
        map.put("flag", String.valueOf(flag));
        map.put("toId", toId);
        map.put("type", type);
        map.put("singleMark", "1");
        HttpUtil.postFileFormData(HttpCons.PATH_CONTACT_UPLOAD_FILE,
                "file", file, map, httpCallBack);
    }

    public static void uploadFileList(List<File> fileList, int flag, String toId,
                                  String type, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(3);
        map.put("flag", String.valueOf(flag));
        map.put("toId", toId);
        map.put("type", type);
        HttpUtil.postFileFormData(HttpCons.PATH_CONTACT_UPLOAD_FILE,
                "file", fileList, map, httpCallBack);
    }

    //移动好友分组
    public static void moveFriendSubgroup(String friendId, String newGroupId,
                                          HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(2);
        map.put("friendId", friendId);
        map.put("newGroupId", newGroupId);
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_MOVE_SUBGROUP, map, httpCallBack);
    }

    //删除好友分组
    public static void deleteFriendSubgroup(String groupId, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(1);
        map.put("groupId", groupId);
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_DELETE_SUBGROUP, map, httpCallBack);
    }

    //获取群成员列表 status 0:普通会员 1:管理员 -1:所有会员
    public static void getGroupMemberList(String groupId, int status, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(2);
        map.put("groupId", groupId);
        map.put("status", String.valueOf(status));
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_MEMBER_LIST, map, httpCallBack);
    }

    //获取好友信息
    public static void getFriendInfo(String friendId, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(1);
        map.put("friendId", friendId);
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_INFO, map, httpCallBack);
    }

    //获取群公告列表
    public static void getAnnouncementList(String groupId, int limit, int page, String value,
                                           HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(3);
        map.put("groupId", groupId);
        map.put("limit", String.valueOf(limit));
        map.put("page", String.valueOf(page));
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_ANNOUNCEMENT_LIST, map, httpCallBack);
    }

    //创建群公告
    public static void createAnnouncement(String accountId, String content, String groupId,
                                          String title, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(4);
        map.put("accountId", accountId);
        map.put("content", content);
        map.put("groupId", groupId);
        map.put("title", title);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_ANNOUNCEMENT_CREATE, map, httpCallBack);
    }

    //修改群公告
    public static void modifyAnnouncement(String accountId, String content, String id,
                                               String title, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(4);
        map.put("accountId", accountId);
        map.put("content", content);
        map.put("id", id);
        map.put("title", title);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_ANNOUNCEMENT_MODIFY, map, httpCallBack);
    }

    //删除群公告
    public static void deleteAnnouncement(String id, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(1);
        map.put("id", id);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_ANNOUNCEMENT_DELETE, map, httpCallBack);
    }

    //修改群信息
    public static void modifyGroupInfo(String description, String groupId, String groupName,
                                       int valid, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(4);
        map.put("description", description);
        map.put("groupId", groupId);
        map.put("groupName", groupName);
        map.put("valid", String.valueOf(valid));
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_INFO_MODIFY, map, httpCallBack);
    }

    //设置群管理员
    public static void setGroupManager(String accountId, String groupId, int type,
                                       HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(3);
        map.put("accountId", accountId);
        map.put("groupId", groupId);
        map.put("type", String.valueOf(type));
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_MANAGER_SET, map, httpCallBack);
    }

    //退群、解散群
    public static void disbandGroup(String groupId, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(1);
        map.put("groupId", groupId);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_DISBAND, map, httpCallBack);
    }

    //踢出群组
    public static void moveOutGroupMember(int grade, String groupId, String memberId, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(3);
        map.put("grade", String.valueOf(grade));
        map.put("groupId", groupId);
        map.put("memberId", memberId);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_REMOVE_MEMBER, map, httpCallBack);
    }

    //上传群头像
    public static void uploadGroupAvatar(File file, String groupId, String suffix, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(2);
        map.put("id", groupId);
        map.put("suffix", suffix);
        HttpUtil.postFileFormData(HttpCons.PATH_CONTACT_GROUP_AVATAR_UPLOAD, "file", file, map,
                httpCallBack);
    }

    //匹配手机通讯录
    public static void matchPhoneContact(String phoneListStr, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(1);
        map.put("phoneStr", phoneListStr);
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_MATCH_PHONE_CONTACT, map, httpCallBack);
    }

    //好友消息置顶; topMark:0 取消置顶 1设置置顶
    public static void makeTopFriend(String id, int topMark,int friendShipMark, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(3);
        map.put("id", id);
        map.put("topMark", String.valueOf(topMark));
        map.put("friendShipMark",String.valueOf(friendShipMark));
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_MAKE_TOP, map, httpCallBack);
    }

    //拉黑好友；0取消拉黑；1拉黑  friendShipMark:0非好友关系 1是好友关系
    public static void pullBlackFriend(String friendId, int type,int friendShipMark, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(3);
        map.put("friendId", friendId);
        map.put("type", String.valueOf(type));
        map.put("friendShipMark",String.valueOf(friendShipMark));
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_PULL_BLACKLIST, map, httpCallBack);
    }

    //屏蔽好友；0取消屏蔽；1屏蔽
    public static void shieldFriendConversation(String friendId, int type,int friendShipMark, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(3);
        map.put("friendId", friendId);
        map.put("type", String.valueOf(type));
        map.put("friendShipMark",String.valueOf(friendShipMark));
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_SHIELD_MARK_COMMIT, map, httpCallBack);
    }

    //群消息置顶
    public static void makeTopGroup(String groupId, int topMark, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(2);
        map.put("groupId", groupId);
        map.put("topMark", String.valueOf(topMark));
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_MAKE_TOP, map, httpCallBack);
    }

    //编辑群权限
    public static void editGroupPermission(String groupId, int allowFlag, int groupSpeak, int groupValid,
                                           int inviteFlag, int searchFlag, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(6);
        map.put("groupId", groupId);
        map.put("allowFlag", String.valueOf(allowFlag));
        map.put("groupSpeak", String.valueOf(groupSpeak));
        map.put("groupValid", String.valueOf(groupValid));
        map.put("inviteFlag", String.valueOf(inviteFlag));
        map.put("searchFlag", String.valueOf(searchFlag));
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_EDIT_PERMISSION, map, httpCallBack);
    }

    //获取群权限
    public static void getGroupPermission(String groupId, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(1);
        map.put("groupId", groupId);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_GET_PERMISSION, map, httpCallBack);
    }

    //邀请入群
    public static void inviteGroupMember(String friendIdStr, String groupNum, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(2);
        map.put("friendIdStr", friendIdStr);
        map.put("groupNum", groupNum);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_INVITE_MEMBER, map, httpCallBack);
    }

    //新建分组
    public static void createSubgroup(String groupingName, String userId, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(2);
        map.put("subgroupName", groupingName);
        map.put("userId", userId);
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_CREATE_SUBGROUP, map, httpCallBack);
    }

    //重命名分组
    public static void renameSubgroup(String groupingId, String groupingName, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(2);
        map.put("subgroupId", groupingId);
        map.put("subgroupName", groupingName);
        HttpUtil.post(HttpCons.PATH_CONTACT_FRIEND_RENAME_SUBGROUP, map, httpCallBack);
    }

    //屏蔽群会话
    public static void shieldGroupConversation(String groupId, int shieldMark, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(2);
        map.put("groupId", groupId);
        map.put("shieldMark", String.valueOf(shieldMark));
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_SHIELD_CONVERSATION, map, httpCallBack);
    }

}
