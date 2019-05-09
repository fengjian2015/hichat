package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.model.db.entity.ImgUrl;

import java.util.List;

/**
 * Created by Darren on 2019/3/22
 */
public class ConversationChatRightMoreGvAdapter extends BaseAdapter {

    private Context context;
    private List<ImgUrl> imgList;

    public ConversationChatRightMoreGvAdapter(Context context, List<ImgUrl> imgList) {
        this.context = context;
        this.imgList = imgList;
    }

    @Override
    public int getCount() {
        if (imgList == null) {
            return 0;
        } else {
            return imgList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return imgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder iHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_conversation_chat_right_more, null);
            iHolder = new ItemViewHolder();
            iHolder.iconIv = convertView.findViewById(R.id.iv_item_conversation_chat_right_more_icon);
            iHolder.textTv = convertView.findViewById(R.id.tv_item_conversation_chat_right_more_text);
            convertView.setTag(iHolder);
        } else {
            iHolder = (ItemViewHolder) convertView.getTag();
        }

        ImgUtil.load(context, imgList.get(position).getResourceId(), iHolder.iconIv);
        iHolder.textTv.setText(imgList.get(position).getText());

        return convertView;
    }

    private static class ItemViewHolder {
        private ImageView iconIv;
        private TextView textTv;
    }


}
