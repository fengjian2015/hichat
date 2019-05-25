package com.wewin.hichat.component.constant;

import com.wewin.hichat.MainApplication;

/**
 * @author darren
 * Created by Darren on 2018/12/17.
 */
public class HttpCons {


    //服务器地址
//    private static final String SERVER_HTTP = "http://10.5.90.116:8084/";//jack
//    private static final String SERVER_SOCKET = "ws://10.5.90.116:8085/";
//    private static final String SERVER_HTTP = "http://10.5.90.15:8084/";//william
//    private static final String SERVER_SOCKET = "ws://10.5.90.15:8085/";
    private static final String SERVER_HTTP = "https://gateway.wewein18.com/";//测试环境
    private static final String SERVER_SOCKET = "wss://ws.wewein18.com/";
    private static final String SERVER_HTML = "https://web.wewein18.com/";
//    private static final String SERVER_HTTP = "https://gateway.sousoutalk.com/";//release
//    private static final String SERVER_SOCKET = "wss://ws.sousoutalk.com/";
//    private static final String SERVER_HTML = "https://web.sousoutalk.com/";

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
    //关于我们
    public static final String PATH_MORE_ABOUT = SERVER_HTML + "about";
    //校验客户端版本更新
    public static final String PATH_MORE_GET_CHECK_VERSION = SERVER_HTTP + "mobile/check_version";

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
    public static final String PATH_NOTIFY_LIST = SERVER_HTTP + "mobile/message/get_notification_message";
    //同意好友添加
    public static final String PATH_NOTIFY_AGREE_ADD = SERVER_HTTP + "mobile/message/agree";
    //拒绝好友添加
    public static final String PATH_NOTIFY_REFUSE_ADD = SERVER_HTTP + "mobile/message/refuse";
    //获取未读通知数量
    public static final String PATH_NOTIFY_GET_UNREAD_COUNT = SERVER_HTTP + "mobile/get_unread_notice_count";
    //设置通知已读
    public static final String PATH_NOTIFY_SET_READ = SERVER_HTTP + "mobile/message/read";

    /*消息*/
    //设置聊天消息已读
    public static final String PATH_MESSAGE_SET_READ = SERVER_HTTP + "mobile/message/set_message_read";
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


    private static final String HTTPS_CER_DEBUG = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFVDCCBDygAwIBAgISA6i/RbJGn4ddMse61hRwk+JSMA0GCSqGSIb3DQEBCwUA\n" +
            "MEoxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1MZXQncyBFbmNyeXB0MSMwIQYDVQQD\n" +
            "ExpMZXQncyBFbmNyeXB0IEF1dGhvcml0eSBYMzAeFw0xOTA1MTcwNjU2MDZaFw0x\n" +
            "OTA4MTUwNjU2MDZaMBkxFzAVBgNVBAMMDioud2V3ZWluMTguY29tMIIBIjANBgkq\n" +
            "hkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA02CjkjeEGlJjzRUfYbbGq/wWmqVq46cl\n" +
            "p+qkfAa4H1yp0O734x+dUAAQfEr0UcX6bsjHCDlTGg5aWtrypHVWXU2Vm2q1aVpA\n" +
            "Vo2Yz4MYYSSPDQQBHQoxWQ5KfNxVOTqS1O4T75mIq7WjbWMciqVLSAxmcJSKcglh\n" +
            "EByHtUMDdyMdQJbvF34pDrTdY5ovGp3hZSfrfZucVLzmNB974X3MvQ6/6+DQbLlR\n" +
            "L9K5TOgCHpRIja6XnWn5R8SHjVCSPTOgHXaf9E4tc2B4ZojN30gLOwOf7uMxBVcN\n" +
            "SfKU1ZaEbCzz3ReE5qgAcV00yTOPIxlcYMp4u2KaqgHwowhL6m2R/QIDAQABo4IC\n" +
            "YzCCAl8wDgYDVR0PAQH/BAQDAgWgMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEF\n" +
            "BQcDAjAMBgNVHRMBAf8EAjAAMB0GA1UdDgQWBBTUaLLRSFVPFMQ3hRleQeNcU8tr\n" +
            "BTAfBgNVHSMEGDAWgBSoSmpjBH3duubRObemRWXv86jsoTBvBggrBgEFBQcBAQRj\n" +
            "MGEwLgYIKwYBBQUHMAGGImh0dHA6Ly9vY3NwLmludC14My5sZXRzZW5jcnlwdC5v\n" +
            "cmcwLwYIKwYBBQUHMAKGI2h0dHA6Ly9jZXJ0LmludC14My5sZXRzZW5jcnlwdC5v\n" +
            "cmcvMBkGA1UdEQQSMBCCDioud2V3ZWluMTguY29tMEwGA1UdIARFMEMwCAYGZ4EM\n" +
            "AQIBMDcGCysGAQQBgt8TAQEBMCgwJgYIKwYBBQUHAgEWGmh0dHA6Ly9jcHMubGV0\n" +
            "c2VuY3J5cHQub3JnMIIBBAYKKwYBBAHWeQIEAgSB9QSB8gDwAHYA4mlLribo6UAJ\n" +
            "6IYbtjuD1D7n/nSI+6SPKJMBnd3x2/4AAAFqxMp+5wAABAMARzBFAiAJ9jm1oQMO\n" +
            "FsYA8sO4cHSAfyMw1eeyDqnQsNUHZVOrlQIhAJG8nCd1LyaXUMpIi3asUlHErjVz\n" +
            "uMfCRUWLzfSO6Ad5AHYAKTxRllTIOWW6qlD8WAfUt2+/WHopctykwwz05UVH9HgA\n" +
            "AAFqxMp+2AAABAMARzBFAiEAnf+2FmE5RkZSIrEb3TmAFBYSocUk0GU4RXBuZMlc\n" +
            "OTUCIA8kGWphbANoGyWfjtBjhvrQO4tR0Z1gFQvuKFHTQfj7MA0GCSqGSIb3DQEB\n" +
            "CwUAA4IBAQBBCnMmfP+c4Se30ZNQTSIDrGnWlbEPYdglTyUOVdwq6LtxqtVB1ytS\n" +
            "X6/IhDUMiIzw2rWPxI7EE9qBOyWOlQU2J82F4kHg6oFJKawyjlZ+MFMHzPeGOfS/\n" +
            "kD7YSCixpR0p69qQihUCE77KJCjgIaDWLAsCbgD+9C5su/kf7CSc+FBNUvrWkgQE\n" +
            "bsWIiqfFExwg9S0AZw+rD2210zupHiVAPsIE1InZbf9iFJ8W/KQC8u4rXniFIXcw\n" +
            "v1ZgGsQO7K4xaTD7Vk07iMlrOLRvACoMfxEFw+ABLtzWi6gG82xi8zxvlZivdUND\n" +
            "i8CE55ch0qrdkwobOkIXpPo0N2lvZfEq\n" +
            "-----END CERTIFICATE-----";


    private static final String HTTPS_CER_RELEASE = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFWDCCBECgAwIBAgISAymtL1eHi9xWz2fzB1VoGiGMMA0GCSqGSIb3DQEBCwUA\n" +
            "MEoxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1MZXQncyBFbmNyeXB0MSMwIQYDVQQD\n" +
            "ExpMZXQncyBFbmNyeXB0IEF1dGhvcml0eSBYMzAeFw0xOTA1MjIwNjE0MzdaFw0x\n" +
            "OTA4MjAwNjE0MzdaMBsxGTAXBgNVBAMMECouc291c291dGFsay5jb20wggEiMA0G\n" +
            "CSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDAXPG30joPf1LefbBWbVAxArdWOMXH\n" +
            "tvCMwRi0rDNQc78FVAypwoaADRj1PCC1txakUFVmKjbQmIJx6U0jywCSLGRJ9LYq\n" +
            "DKlGouFcWsunkbp4/zKIuKbbsVfjK2V5LL1yc2DDcvV2T7AXdwne+CYEml+QlYBH\n" +
            "zc5B4XdvVpQNbDflJH+ZyFnghoNuntNHM/3P0E7Hj3a8Zwst1MRApc7d2g39/koS\n" +
            "w8RKpOEM2XnbWGe1vTAUyTiY72tvOC4nJY1b4i2sRd+I1FUYSbc/kTJ6Refq9Pjv\n" +
            "yaipT0drFPvwcNMJnZoXXXALSDWx2ooGvBevbXF/kk/6YA576E2C09XnAgMBAAGj\n" +
            "ggJlMIICYTAOBgNVHQ8BAf8EBAMCBaAwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsG\n" +
            "AQUFBwMCMAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFOjaOInWsXVGZ+6LEX5V6l1j\n" +
            "nxydMB8GA1UdIwQYMBaAFKhKamMEfd265tE5t6ZFZe/zqOyhMG8GCCsGAQUFBwEB\n" +
            "BGMwYTAuBggrBgEFBQcwAYYiaHR0cDovL29jc3AuaW50LXgzLmxldHNlbmNyeXB0\n" +
            "Lm9yZzAvBggrBgEFBQcwAoYjaHR0cDovL2NlcnQuaW50LXgzLmxldHNlbmNyeXB0\n" +
            "Lm9yZy8wGwYDVR0RBBQwEoIQKi5zb3Vzb3V0YWxrLmNvbTBMBgNVHSAERTBDMAgG\n" +
            "BmeBDAECATA3BgsrBgEEAYLfEwEBATAoMCYGCCsGAQUFBwIBFhpodHRwOi8vY3Bz\n" +
            "LmxldHNlbmNyeXB0Lm9yZzCCAQQGCisGAQQB1nkCBAIEgfUEgfIA8AB1AHR+2oMx\n" +
            "rTMQkSGcziVPQnDCv/1eQiAIxjc1eeYQe8xWAAABat5kU0oAAAQDAEYwRAIgAj/P\n" +
            "gosVTId0QRtI6vnVqDVKstw59BFBRUG5szr/YmECID9SusXIV6rDWuyZDGIKpfYN\n" +
            "cjXviIsBD3oF9xBecq3DAHcAY/Lbzeg7zCzPC3KEJ1drM6SNYXePvXWmOLHHaFRL\n" +
            "2I0AAAFq3mRTOAAABAMASDBGAiEA58mMoj8Px/yJhAtHYiUEw/7H+WRAjJd04iGH\n" +
            "qc/EgqMCIQDmCiz6Wvmvqh042M0h2yLiNm/yhvls1XYS0KL64ZfurzANBgkqhkiG\n" +
            "9w0BAQsFAAOCAQEAFxubEfdo+0TFtlYYtXbLVTzIVKDq8aHdg/NUpCJxu6K5PL9l\n" +
            "oP5ww5QeNEL/wfI7+GycT/b4XCJ+p8UYhGP4/kHYgv2dOW9P729eOipbhMJ/3aqD\n" +
            "VTVDIogPDW2/EjdM3TE0C52ubquFDhPArVmuU6qsm8v2plwD5Ns3TGCWr0sospLy\n" +
            "YsyZc3iWuflqs5XObWd0Mad9qqjKuuPkmW6BNDHSsJU6fxGeTo1ooxk3wittOr24\n" +
            "1zaUf/Qdzrg8TD6pKWgtJ7sH7MbCLJtGgZRBX5VM2MgjmDh1+fzyGqbpUcld7n8Z\n" +
            "QKtvdxmHt9ucSx5Tnrpy/Fx5ZlTnDkqT1oJCOQ==\n" +
            "-----END CERTIFICATE-----";

    public static String HTTPS_CER = MainApplication.IS_DEBUG ? HTTPS_CER_DEBUG : HTTPS_CER_RELEASE;

}
