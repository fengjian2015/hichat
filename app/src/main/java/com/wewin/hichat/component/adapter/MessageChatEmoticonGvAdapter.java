package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.model.db.entity.Emoticon;

import java.util.List;

/**
 * Created by Darren on 2019/1/21
 */
public class MessageChatEmoticonGvAdapter extends BaseAdapter {

    private Context context;
    private List<Emoticon> emoticonList;

    public MessageChatEmoticonGvAdapter(Context context, List<Emoticon> emoticonList){
        this.context = context;
        this.emoticonList = emoticonList;
    }

    @Override
    public int getCount() {
        if (emoticonList == null){
            return 0;
        }else {
            return emoticonList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return emoticonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder iHolder;
        if (convertView == null){
            convertView = View.inflate(context, R.layout.item_message_chat_expression_list, null);
            iHolder = new ItemViewHolder();
            iHolder.emoticonIv = convertView.findViewById(R.id.iv_message_chat_expression);
            convertView.setTag(iHolder);
        }else {
            iHolder = (ItemViewHolder)convertView.getTag();
        }

        int resId = context.getResources().getIdentifier(emoticonList.get(position).getId(),
                "drawable", context.getPackageName());
        ImgUtil.load(context, resId, iHolder.emoticonIv);

        return convertView;
    }

    private static class ItemViewHolder{
        private ImageView emoticonIv;
    }

}
