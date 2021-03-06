package com.wewin.hichat.androidlib.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.rxjava.OnRxJavaProcessListener;
import com.wewin.hichat.androidlib.rxjava.RxJavaObserver;
import com.wewin.hichat.androidlib.rxjava.RxJavaScheduler;
import com.wewin.hichat.androidlib.threadpool.SingleThreadPool;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.GroupDao;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.view.contact.friend.FriendInfoActivity;
import com.wewin.hichat.view.conversation.ChatRoomActivity;
import com.wewin.hichat.view.conversation.DocViewActivity;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.ObservableEmitter;

/**
 * @author Jason
 * Created by Jason on 2016/7/6.
 */
public class HyperLinkUtil {

    private static Pattern linkPattern = getUrlPattern();
    private static Pattern phonePattern = getPhonePattern();

    /**
     * 这里处理有问题，界面抖动和定位底部出现明显区别，暂时隐藏子线程加载，ChatRoomActivity.setManFlingVelocity()添加此方法缓解快速滑动造成卡顿问题
     */
    public static void setSpanStr(final Context context, final TextView textView, final ChatMsg chatMsg, final String  content, final boolean atMark,boolean replyMark) {
//        textView.setText(content);
        final SpannableString[] spanStr = {new SpannableString(content)};
//        RxJavaScheduler.execute(new OnRxJavaProcessListener() {
//            @Override
//            public void process(ObservableEmitter<Object> emitter) {
                if (textView != null && chatMsg != null && chatMsg.getContent() != null) {

                    if (atMark
                            &&chatMsg.getAtFriendMap() != null
                            && !chatMsg.getAtFriendMap().isEmpty()
                            && ChatRoom.TYPE_GROUP.equals(chatMsg.getRoomType())) {
                        spanStr[0] = HyperLinkUtil.getATSpanStr(context, spanStr[0], chatMsg.getAtFriendMap(),
                                R.color.blue_main, chatMsg.getRoomId());
                    }
                    if (replyMark) {
                        spanStr[0] = EmoticonUtil.getEmoSpanStr(context, spanStr[0]);
                        spanStr[0] = HyperLinkUtil.getPhoneSpanStr(context, spanStr[0], R.color.blue_main);
                        spanStr[0] = HyperLinkUtil.getLinkSpanStr(context, spanStr[0], R.color.blue_main);
                    }else {
                        if (chatMsg.getEmoMark()==1){
                            spanStr[0] = EmoticonUtil.getEmoSpanStr(context, spanStr[0]);
                        }
                        if (chatMsg.getPhoneMark()==1){
                            spanStr[0] = HyperLinkUtil.getPhoneSpanStr(context, spanStr[0], R.color.blue_main);
                        }
                        if (chatMsg.getUrlMark()==1){
                            spanStr[0] = HyperLinkUtil.getLinkSpanStr(context, spanStr[0], R.color.blue_main);
                        }
                    }
                }
//            }
//        }, new RxJavaObserver<Object>() {
//            @Override
//            public void onComplete() {
                textView.setText(spanStr[0]);
//            }
//        });

    }

    /**
     * 匹配网址更改颜色和跳转
     */
    public static SpannableString getLinkSpanStr(final Context context, SpannableString spanStr,
                                                 final int color) {
        final Matcher matcher = linkPattern.matcher(spanStr);
        while (matcher.find()) {
            final String webUrl = matcher.group();
            if (matcher.start() < 0) {
                continue;
            }
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(context.getResources().getColor(color));
                    ds.setUnderlineText(true);
                }

                @Override
                public void onClick(@NonNull View v) {
                    Intent intent = new Intent(context, DocViewActivity.class);
                    intent.putExtra(ContactCons.EXTRA_MESSAGE_CHAT_WEB_PATH, webUrl);
                    context.startActivity(intent);
                }
            };
            spanStr.setSpan(clickableSpan, matcher.start(), matcher.start() + webUrl.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanStr;
    }

    /**
     * 匹配手机号更改颜色和跳转
     */
    public static SpannableString getPhoneSpanStr(final Context context, SpannableString spanStr,
                                                  final int color) {
        final Matcher matcher = phonePattern.matcher(spanStr);
        while (matcher.find()) {
            final String phone = matcher.group();
            if (matcher.start() < 0) {
                continue;
            }
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(context.getResources().getColor(color));
                    ds.setUnderlineText(true);
                }

                @Override
                public void onClick(@NonNull View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + phone);
                    intent.setData(data);
                    context.startActivity(intent);
                }
            };
            spanStr.setSpan(clickableSpan, matcher.start(), matcher.start() + phone.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanStr;
    }

    /**
     * 匹配at更改颜色和跳转
     * 匹配必须放最前面，因为需要更改文本
     */
    public static SpannableString getATSpanStr(Context context, SpannableString spanStr,
                                               Map<String, String> mapContent, int color, String roomId) {
        if (mapContent == null || mapContent.isEmpty()) {
            return spanStr;
        }
        spanStr = getATSpanChangeName(spanStr, mapContent);
        for (Map.Entry<String, String> entry : mapContent.entrySet()) {
            String name;
            if ("0".equals(entry.getKey())) {
                name = entry.getValue();
            } else {
                name = NameUtil.getName(entry.getKey());
            }
            int index = spanStr.toString().indexOf(name);
            if (index >= 1) {
                while (index < spanStr.length() && index >= 1) {
                    spanStr = matcherSearchText(context, spanStr, entry.getKey(), index - 1,
                            index + name.length(), color, roomId);
                    index = spanStr.toString().indexOf(name, index + name.length());
                }
            }
        }
        return spanStr;
    }

    public static SpannableString getATSpanChangeName(SpannableString spanStr,
                                                      Map<String, String> mapContent) {
        if (mapContent == null || mapContent.isEmpty()) {
            return spanStr;
        }
        String str = spanStr.toString();
        for (Map.Entry<String, String> entry : mapContent.entrySet()) {
            if ("0".equals(entry.getKey())) {
                continue;
            }
            String name = NameUtil.getName(entry.getKey());
            str = str.replace(entry.getValue(), name);
        }
        spanStr = new SpannableString(str);
        return spanStr;
    }

    /**
     * AT消息文本变色
     */
    private static SpannableString matcherSearchText(final Context context, SpannableString spanStr,
                                                     final String id, int start, int end, final int color, final String roomId) {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(color));
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(@NonNull View v) {
                if ("0".equals(id) || SpCons.getUser(context).getId().equals(id)) {
                    return;
                }
                if (roomId != null && GroupDao.getAddMark(roomId) == 0 && FriendDao.findFriendshipMark(id) != 1) {
                    ToastUtil.showShort(context, context.getString(R.string.allow_add_prompt));
                    return;
                }
                Intent intent = new Intent(context, FriendInfoActivity.class);
                ChatRoom chatRoom = ChatRoomManager.getChatRoom(id, ChatRoom.TYPE_SINGLE);
                intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, chatRoom);
                context.startActivity(intent);
            }
        };
        spanStr.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    /**
     * 内容是否包含手机号
     */
    public static boolean isContainPhone(String content) {
        Matcher matcher = phonePattern.matcher(content);
        return matcher.find();
    }

    /**
     * 内容是否包含网址
     */
    public static boolean isContainUrl(String content) {
        Matcher matcher = linkPattern.matcher(content);
        return matcher.find();
    }

    /**
     * 网页正则匹配
     */
    private static Pattern getUrlPattern() {
        String port = "(\\:((6553[0-5])|655[0-2]{2}\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|([1-9]\\d{3}|[1-9]\\d{2}|[1-9]\\d|[0-9])))?";
        String domainName = "(\\.(com.cn|com|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk|cn|co|int|biz|CC|TV|pro|coop|aero|hk|tw|top))";
        //有www.开头的
        String pattern1 = "((www|WWW)\\.){1}([a-zA-Z0-9\\u4e00-\\u9fa5\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]+)" + domainName + port;
        //有http等协议号开头
        String pattern2 = "((((ht|f|Ht|F)tp(s?))|rtsp|Rtsp)\\://)(www\\.)?(.+)";
        //有.com等后缀的，并有参数
        String pattern3 = pattern2 + "([a-zA-Z0-9\\u4e00-\\u9fa5]+)([a-zA-Z0-9\\u4e00-\\u9fa5\\.]+)" + domainName + port + "(/(.*)*)";
        //有.com等后缀结尾的，无参数
        String pattern4 = pattern2 + "([a-zA-Z0-9\\u4e00-\\u9fa5]+)([a-zA-Z0-9\\u4e00-\\u9fa5\\.]+)" + domainName + port;
        //ip地址的
        String pattern5 = "((((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[1-9])\\.)((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])\\.){2}((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])))" + port + "(\\/(.+))?)";
        //有.com等后缀结尾的，无参数
        String pattern6 = pattern2 + "([a-zA-Z0-9\\.\\-\\~\\@\\#\\%\\^\\&\\+\\?\\:\\_\\/\\=\\<\\>]+)" + domainName + port;
        String pattern = pattern1.concat("|").concat(pattern2).concat("|").concat(pattern3).concat("|").concat(pattern4).concat("|").concat(pattern5).concat("|").concat(pattern6);
        return Pattern.compile(pattern);
    }

    /**
     * 手机号正则匹配
     * "[1]"代表下一位为数字可以是几，"[0-9]"代表可以为0-9中的一个,
     * "[5,7,9]"表示可以是5,7,9中的任意一位,[^4]表示除4以外的任何一个,\\d{9}"代表后面是可以是0～9的数字，有9位。
     */
    private static Pattern getPhonePattern() {
        String telRegex = "(((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8})";
        String regex = "((0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?))";
        String regex1 = "((\\+\\d{2}-)?0\\d{2,3}-\\d{7,8})";
        String pattern = telRegex.concat("|").concat(regex).concat("|").concat(regex1);
        return Pattern.compile(pattern);
    }

}
