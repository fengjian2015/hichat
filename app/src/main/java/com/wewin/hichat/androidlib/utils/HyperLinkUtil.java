package com.wewin.hichat.androidlib.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.Patterns;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.view.contact.friend.FriendInfoActivity;
import com.wewin.hichat.view.conversation.DocViewActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xux on 2016/7/6.
 */
public class HyperLinkUtil {

    public SpannableStringBuilder getHyperClickableSpan(Context context, SpannableStringBuilder spanStr, Map mapString, int color) {
        if (spanStr == null || spanStr.length() == 0) {
            return spanStr;
        }
        //网页链接正则匹配
        Matcher m = searchUrl().matcher(spanStr);
        while (m.find()) {
            spanStr = showHyperLinkString(context, spanStr, m.group(), m.start(), color);
        }
        Matcher mPhone = searchPhoneNumber().matcher(spanStr);
        while (mPhone.find()) {
            spanStr = showPhoneNumberString(context, spanStr, mPhone.group(), mPhone.start(), color);
        }
        //@功能变色
        spanStr = getATContent(context, spanStr, mapString, color);
        return spanStr;
    }

    /**
     * 匹配的网页数据进行更改颜色和跳转
     *
     * @param context
     * @param spanStr
     * @param url
     * @param start
     * @param color
     * @return
     */
    private static SpannableStringBuilder showHyperLinkString(final Context context, SpannableStringBuilder spanStr, final String url, int start, final int color) {

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(color));
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DocViewActivity.class);
                intent.putExtra(ContactCons.EXTRA_MESSAGE_CHAT_WEB_PATH, url);
                context.startActivity(intent);
            }
        };
        spanStr.setSpan(clickableSpan, start, start + url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    /**
     * 网页正则匹配
     *
     * @return
     */
    public static Pattern searchUrl() {
        String port = "(\\:((6553[0-5])|655[0-2]{2}\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|([1-9]\\d{3}|[1-9]\\d{2}|[1-9]\\d|[0-9])))?";
        String domainName = "(\\.(com.cn|com|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk|cn|co|int|biz|CC|TV|pro|coop|aero|hk|tw|top))";
        String pattern1 = "((www|WWW)\\.){1}([a-zA-Z0-9\\u4e00-\\u9fa5\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]+)" + domainName + port; //有www.开头的
        String pattern2 = "((((ht|f|Ht|F)tp(s?))|rtsp|Rtsp)\\://)(www\\.)?(.+)"; //有http等协议号开头
        String pattern3 = pattern2 + "([a-zA-Z0-9\\u4e00-\\u9fa5]+)([a-zA-Z0-9\\u4e00-\\u9fa5\\.]+)" + domainName + port + "(/(.*)*)"; //有.com等后缀的，并有参数
        String pattern4 = pattern2 + "([a-zA-Z0-9\\u4e00-\\u9fa5]+)([a-zA-Z0-9\\u4e00-\\u9fa5\\.]+)" + domainName + port; //有.com等后缀结尾的，无参数
        String pattern5 = "((((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[1-9])\\.)((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])\\.){2}((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])))" + port + "(\\/(.+))?)"; //ip地址的
        String pattern6 = pattern2 + "([a-zA-Z0-9\\.\\-\\~\\@\\#\\%\\^\\&\\+\\?\\:\\_\\/\\=\\<\\>]+)" + domainName + port; //有.com等后缀结尾的，无参数
        String pattern = pattern1 + "|" + pattern2 + "|" + pattern3 + "|" + pattern4 + "|" + pattern5 + "|" + pattern6;
        Pattern p = Pattern.compile(pattern);
        return p;
    }

    /**
     * 匹配的手机号数据进行更改颜色和跳转
     *
     * @param context
     * @param spanStr
     * @param url
     * @param start
     * @param color
     * @return
     */
    private static SpannableStringBuilder showPhoneNumberString(final Context context, SpannableStringBuilder spanStr, final String url, int start, final int color) {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(color));
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + url);
                intent.setData(data);
                context.startActivity(intent);
            }
        };
        spanStr.setSpan(clickableSpan, start, start + url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }


    /**
     * 手机号正则匹配
     *
     * @return
     */
    public static Pattern searchPhoneNumber() {
        // "[1]"代表下一位为数字可以是几，"[0-9]"代表可以为0-9中的一个
        // ，"[5,7,9]"表示可以是5,7,9中的任意一位,[^4]表示除4以外的任何一个,\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "(((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8})";
        String regex = "((0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?))";
        String regex1 = "((\\+\\d{2}-)?0\\d{2,3}-\\d{7,8})";
        String pattern = telRegex + "|" + regex + "|" + regex1;
        Pattern p = Pattern.compile(pattern);
        return p;
    }


    /**
     * 获取at变色后的文本
     *
     * @param context
     * @param spanStr
     * @param mapContent
     * @param color
     * @return
     */
    public SpannableStringBuilder getATContent(Context context, SpannableStringBuilder spanStr, Map<String, String> mapContent, int color) {
        if (mapContent == null || "".equals(mapContent)) return spanStr;
        for (Map.Entry<String, String> entry : mapContent.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getValue());
            Matcher matcher = pattern.matcher(spanStr);
            while (matcher.find()) {
                spanStr = matcherSearchText(context, spanStr, entry.getKey(), matcher.start() - 1, matcher.end(), color);
            }
        }
        return spanStr;
    }

    /**
     * AT消息文本变色
     *
     * @param context
     * @param spanStr
     * @param id
     * @param start
     * @param end
     * @param color
     * @return
     */
    public static SpannableStringBuilder matcherSearchText(final Context context, SpannableStringBuilder spanStr, final String id, int start, int end, final int color) {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(color));
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View v) {
                if("0".equals(id)||UserDao.user.getId().equals(id))return;
                Intent intent = new Intent(context, FriendInfoActivity.class);
                ChatRoom chatRoom = ChatRoomManager.getChatRoom(id, ChatRoom.TYPE_SINGLE);
                intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, chatRoom);
                context.startActivity(intent);
            }
        };
        spanStr.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }
}
