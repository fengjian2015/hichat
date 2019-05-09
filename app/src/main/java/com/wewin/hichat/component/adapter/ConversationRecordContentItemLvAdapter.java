package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.model.db.entity.ChatRecordSearch;

import java.util.List;

/**
 * Created by Darren on 2019/3/14
 */
public class ConversationRecordContentItemLvAdapter extends BaseAdapter {

    private Context context;
    private List<ChatRecordSearch.ContentItem> contentItemList;
    private boolean lvExpand = false;
    private String searchStr = "";

    ConversationRecordContentItemLvAdapter(Context context, List<ChatRecordSearch.ContentItem> contentItemList) {
        this.context = context;
        this.contentItemList = contentItemList;
    }

    void setLvExpand(boolean state) {
        this.lvExpand = state;
    }

    void setSearchStr(String searchStr){
        this.searchStr = searchStr;
    }

    @Override
    public int getCount() {
        if (contentItemList == null) {
            return 0;
        } else if (contentItemList.size() <= 3) {
            return contentItemList.size();
        } else if (!lvExpand) {
            return 3;
        } else {
            return contentItemList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return contentItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder iHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_conversation_record_content_item, null);
            iHolder = new ItemViewHolder();
            iHolder.contentTv = convertView.findViewById(R.id.tv_item_conversation_record_item_content);
            iHolder.timeTv = convertView.findViewById(R.id.tv_item_conversation_record_item_time);
            convertView.setTag(iHolder);
        } else {
            iHolder = (ItemViewHolder) convertView.getTag();
        }

        if ((lvExpand && position < contentItemList.size()) || (position < contentItemList.size() && position < 3)) {
            String content = contentItemList.get(position).getContent();
            if (!TextUtils.isEmpty(searchStr) && !TextUtils.isEmpty(content)) {
                int contentStartIndex = content.toLowerCase().indexOf(searchStr.toLowerCase());
                String subContent;
                if (contentStartIndex > 12) {
                    subContent = "..." + content.substring(contentStartIndex - 3, content.length());
                } else {
                    subContent = content;
                }
                int startIndex = subContent.toLowerCase().indexOf(searchStr.toLowerCase());
                int endIndex = startIndex + searchStr.length();
                Spannable spannable = new SpannableString(subContent);
                if (startIndex >= 0 && endIndex <= subContent.length()){
                    spannable.setSpan(new ForegroundColorSpan(0xff4285f4), startIndex, endIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                iHolder.contentTv.setText(spannable);
            }
            iHolder.timeTv.setText(TimeUtil.timestampToStr(contentItemList.get(position).getCreateTimestamp(), "HH:mm"));
        }

        return convertView;
    }

    private static class ItemViewHolder {
        private TextView contentTv, timeTv;
    }

}
