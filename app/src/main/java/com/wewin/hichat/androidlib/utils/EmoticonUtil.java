package com.wewin.hichat.androidlib.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Xml;

import com.wewin.hichat.model.db.entity.Emoticon;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Darren on 2019/1/22
 */
public class EmoticonUtil {

    private static Map<String, Emoticon> emoticonMap = new HashMap<>();
    private static List<Emoticon> emoticonList=new ArrayList<>();

    public static void init(Context context){
        parseEmoticonXml(context);
    }

    //解析表情包xml文件
    public static List<Emoticon> parseEmoticonXml(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("emoticon.xml");
            //创建xmlPull解析器
            XmlPullParser parser = Xml.newPullParser();
            ///初始化xmlPull解析器
            parser.setInput(inputStream, "utf-8");
            //读取文件的类型
            int type = parser.getEventType();
            //无限判断文件类型进行读取
            Emoticon emoticon;
            while (type != XmlPullParser.END_DOCUMENT) {
                if (type == XmlPullParser.START_TAG) {
                    if ("Emoticon".equals(parser.getName())) {
                        //获取sex属性
                        emoticon = new Emoticon();
                        String name = parser.getAttributeValue(null, "name");
                        String id = parser.getAttributeValue(null, "id");
                        String tag = parser.nextText();
                        String filePath = "emoticon/" + name;
                        emoticon.setName(name);
                        emoticon.setId(id);
                        emoticon.setTag(tag);
                        emoticon.setFilePath(filePath);
                        emoticonList.add(emoticon);
                        emoticonMap.put(tag, emoticon);
                    }
                }
                //继续往下读取标签类型
                type = parser.next();
            }
            return emoticonList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return emoticonList;
    }

    //SpannableString图文混排
    public static SpannableString getExpressionString(Context context, String text) {
        String patternStr = "face\\[[^\\]]+\\]";
        Pattern emoPattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
        return processExpression(context, text, emoPattern, 0);
    }

    private static SpannableString processExpression(Context context, String text, Pattern pattern, int start) {
        SpannableString spannableString = new SpannableString(text);
        Matcher matcher = pattern.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
            String value = emoticonMap.get(key).getId();
            if (TextUtils.isEmpty(value)) {
                continue;
            }
            int resId = context.getResources().getIdentifier(value, "drawable",
                    context.getPackageName());
            // 通过上面匹配得到的字符串来生成图片资源id
            if (resId != 0) {
                Drawable drawable = context.getResources().getDrawable(resId);
                drawable.setBounds(0, 0, SystemUtil.dp2px(context, 26), SystemUtil.dp2px(context, 26));
                // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
                ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                // 计算该图片名字的长度，也就是要替换的字符串的长度
                int end = matcher.start() + key.length();
                // 将该图片替换字符串中规定的位置中
                spannableString.setSpan(imageSpan, matcher.start(), end,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    public static List<Emoticon> getEmoticonList() {
        return emoticonList;
    }
}
