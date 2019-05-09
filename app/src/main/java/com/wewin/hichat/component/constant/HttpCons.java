package com.wewin.hichat.component.constant;

/**
 * Created by Darren on 2018/12/17.
 */
public class HttpCons {


    //服务器地址
//    private static final String SERVER_HTTP = "http://10.5.90.116:8084/";//jack
//    private static final String SERVER_SOCKET = "ws://10.5.90.116:8085/";
//    private static final String SERVER_HTTP = "http://10.5.86.19:8084/";//测试环境
//    private static final String SERVER_SOCKET = "ws://10.5.86.19:8085/";
    private static final String SERVER_HTTP = "http://10.5.90.15:8084/";//william
    private static final String SERVER_SOCKET = "ws://10.5.90.15:8085/";
//    private static final String SERVER_HTTP = "http://chat.wwxiliao.com:8084/";//release
//    private static final String SERVER_SOCKET = "ws://chat.wwxiliao.com:8085/";


    //webSocket
    public static final String SOCKET_IM = SERVER_SOCKET + "im";
    //发送短信
    public static final String PATH_LOGIN_SMS = SERVER_HTTP + "mobile/sms";
    //验证码校验
    public static final String PATH_LOGIN_CHECK_SMS = SERVER_HTTP + "mobile/check_sms";
    //注册
    public static final String PATH_LOGIN_REGISTER = SERVER_HTTP + "mobile/account/register";
    //登录
    public static final String PATH_LOGIN = SERVER_HTTP + "mobile/account/login";
    //修改密码
    public static final String PATH_LOGIN_MODIFY_PWD = SERVER_HTTP + "mobile/account/modify_psd";
    //找回密码
    public static final String PATH_LOGIN_RETRIEVE_PWD = SERVER_HTTP + "mobile/account/retrieve_psd";
    //登出
    public static final String PATH_LOGOUT = SERVER_HTTP + "mobile/account/logout";

    /*设置*/
    //修改账号信息
    public static final String PATH_MORE_MODIFY_ACCOUNT = SERVER_HTTP + "mobile/account/modify_account";
    //上传个人头像
    public static final String PATH_MORE_PERSONAL_AVATAR_LOAD = SERVER_HTTP + "mobile/upload/mine_avatar";
    //获取服务器时间戳
    public static final String PATH_MORE_GET_SERVICE_TIMESTAMP = SERVER_HTTP + "mobile/get_server_time";
    //获取个人信息
    public static final String PATH_MORE_GET_PERSONAL_INFO = SERVER_HTTP + "mobile/account/myself_info";
    //获取登录记录
    public static final String PATH_MORE_GET_LOGIN_RECORD = SERVER_HTTP + "mobile/account/get_userlogin_log";
    //设置隐身
    public static final String PATH_MORE_SET_HIDE = SERVER_HTTP + "mobile/account/status_setting";

    /*好友*/
    //上传文件
    public static final String PATH_CONTACT_UPLOAD_FILE = SERVER_HTTP + "mobile/upload/file";
    //添加好友
    public static final String PATH_CONTACT_FRIEND_APPLY_ADD = SERVER_HTTP + "mobile/message/add_friend";
    //搜索好友
    public static final String PATH_CONTACT_FRIEND_SEARCH = SERVER_HTTP + "mobile/friend/search";
    //修改好友备注
    public static final String PATH_CONTACT_FRIEND_MODIFY_NOTE = SERVER_HTTP + "mobile/friend/modify_friend_note";
    //查询好友分组
    public static final String PATH_CONTACT_FRIEND_GET_SUBGROUP = SERVER_HTTP + "mobile/friend/query_friend_group";
    //删除好友
    public static final String PATH_CONTACT_FRIEND_DELETE = SERVER_HTTP + "mobile/friend/delete";
    //移动好友分组
    public static final String PATH_CONTACT_FRIEND_MOVE_SUBGROUP = SERVER_HTTP + "mobile/friend/move_friend_group";
    //删除好友分组
    public static final String PATH_CONTACT_FRIEND_DELETE_SUBGROUP = SERVER_HTTP + "mobile/friend/delete_friend_group";
    //获取好友列表
    public static final String PATH_CONTACT_FRIEND_LIST = SERVER_HTTP + "mobile/friend/get_friend_list";
    //获取好友信息
    public static final String PATH_CONTACT_FRIEND_INFO = SERVER_HTTP + "mobile/friend/information";
    //匹配手机通讯录
    public static final String PATH_CONTACT_FRIEND_MATCH_PHONE_CONTACT = SERVER_HTTP + "mobile/account/init_address_book";
    //好友消息置顶
    public static final String PATH_CONTACT_FRIEND_MAKE_TOP = SERVER_HTTP + "mobile/friend/topping";
    //拉黑好友
    public static final String PATH_CONTACT_FRIEND_PULL_BLACKLIST = SERVER_HTTP + "mobile/friend/blacklist";
    //屏蔽消息提醒
    public static final String PATH_CONTACT_FRIEND_SHIELD_MARK_COMMIT = SERVER_HTTP + "mobile/friend/shield_mark";
    //新建分组
    public static final String PATH_CONTACT_FRIEND_CREATE_SUBGROUP = SERVER_HTTP + "mobile/friend/add_friend_group";
    //重命名分组
    public static final String PATH_CONTACT_FRIEND_RENAME_SUBGROUP = SERVER_HTTP + "mobile/friend/rename_friend_group";

    /*群*/
    //创建群
    public static final String PATH_CONTACT_GROUP_CREATE = SERVER_HTTP + "mobile/group/create_group";
    //获取群列表
    public static final String PATH_CONTACT_GROUP_LIST = SERVER_HTTP + "mobile/group/query_belong_groups";
    //找群
    public static final String PATH_CONTACT_GROUP_SEARCH = SERVER_HTTP + "mobile/group/search";
    //申请入群
    public static final String PATH_CONTACT_GROUP_APPLY_JOIN = SERVER_HTTP + "mobile/message/add_group";
    //同意入群
    public static final String PATH_CONTACT_GROUP_AGREE_JOIN = SERVER_HTTP + "mobile/message/agreeGroup";
    //拒绝入群
    public static final String PATH_CONTACT_GROUP_REFUSE_JOIN = SERVER_HTTP + "mobile/message/refuseGroup";
    //获取群信息
    public static final String PATH_CONTACT_GROUP_INFO = SERVER_HTTP + "mobile/group/information";
    //获取群成员列表
    public static final String PATH_CONTACT_GROUP_MEMBER_LIST = SERVER_HTTP + "mobile/group/query_manager_list";
    //获取群公告列表
    public static final String PATH_CONTACT_GROUP_ANNOUNCEMENT_LIST = SERVER_HTTP + "mobile/group/post_lookup";
    //创建群公告
    public static final String PATH_CONTACT_GROUP_ANNOUNCEMENT_CREATE = SERVER_HTTP + "mobile/group/add_group_post";
    //编辑群公告
    public static final String PATH_CONTACT_GROUP_ANNOUNCEMENT_MODIFY = SERVER_HTTP + "mobile/group/modify_group_post";
    //删除群公告
    public static final String PATH_CONTACT_GROUP_ANNOUNCEMENT_DELETE = SERVER_HTTP + "mobile/group/del_group_post";
    //修改群信息
    public static final String PATH_CONTACT_GROUP_INFO_MODIFY = SERVER_HTTP + "mobile/group/modify_group";
    //设置群管理员
    public static final String PATH_CONTACT_GROUP_MANAGER_SET = SERVER_HTTP + "mobile/group/group_admin_setting";
    //退群、解散群
    public static final String PATH_CONTACT_GROUP_DISBAND = SERVER_HTTP + "mobile/group/quit_group";
    //踢出群组
    public static final String PATH_CONTACT_GROUP_REMOVE_MEMBER = SERVER_HTTP + "mobile/group/expel_member";
    //上传群头像
    public static final String PATH_CONTACT_GROUP_AVATAR_UPLOAD = SERVER_HTTP + "mobile/upload/group_avatar";
    //群消息置顶
    public static final String PATH_CONTACT_GROUP_MAKE_TOP = SERVER_HTTP + "mobile/group/topping";
    //编辑群权限
    public static final String PATH_CONTACT_GROUP_EDIT_PERMISSION = SERVER_HTTP + "mobile/group/modify_group_permission";
    //获取群权限
    public static final String PATH_CONTACT_GROUP_GET_PERMISSION = SERVER_HTTP + "mobile/group/get_group_permission";
    //邀请入群
    public static final String PATH_CONTACT_GROUP_INVITE_MEMBER = SERVER_HTTP + "mobile/message/invite_friend_join";
    //屏蔽群会话
    public static final String PATH_CONTACT_GROUP_SHIELD_CONVERSATION = SERVER_HTTP + "mobile/group/shield_mark";

    /*通知*/
    //通知列表
    public static final String PATH_NOTIFY_LIST = SERVER_HTTP + "mobile/message/get_request_message";
    //同意好友添加
    public static final String PATH_NOTIFY_AGREE_ADD = SERVER_HTTP + "mobile/message/agree";
    //拒绝好友添加
    public static final String PATH_NOTIFY_REFUSE_ADD = SERVER_HTTP + "mobile/message/refuse";

    /*消息*/
    //设置聊天消息已读
    public static final String PATH_MESSAGE_SET_READ = SERVER_HTTP + "mobile/message/set_message_read";
    //获取好友漫游消息记录
    public static final String PATH_MESSAGE_GET_FRIEND_SERVER_MESSAGE = SERVER_HTTP + "mobile/message/message_friend_search";
    //获取群组漫游消息记录
    public static final String PATH_MESSAGE_GET_GROUP_SERVER_MESSAGE = SERVER_HTTP + "mobile/message/message_group_search";
    //清空会话列表
    public static final String PATH_CONVERSATION_CLEAR_ALL = SERVER_HTTP + "mobile/message/clear_list";
    //删除单个会话
    public static final String PATH_CONVERSATION_DELETE_SINGLE = SERVER_HTTP + "mobile/message/remove_session";
    //批量删除会话
    public static final String PATH_CONVERSATION_DELETE_LIST = SERVER_HTTP + "mobile/message/batch_remove_session";
    //增量获取会话列表
    public static final String PATH_CONVERSATION_GET_LIST = SERVER_HTTP + "mobile/increment_session_list";
    //查询会话消息记录列表
    public static final String PATH_MESSAGE_GET_LIST = SERVER_HTTP + "mobile/message/message_conversation_search";

}
