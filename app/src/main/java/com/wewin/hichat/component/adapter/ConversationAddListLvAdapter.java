package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.model.db.entity.FriendInfo;

import java.util.List;


/**
 * Created by Darren on 2019/2/26
 */
public class ConversationAddListLvAdapter extends BaseAdapter implements SectionIndexer {

    private Context context;
    private List<FriendInfo> friendInfoList;

    public ConversationAddListLvAdapter(Context context, List<FriendInfo> friendInfoList){
        this.context = context;
        this.friendInfoList = friendInfoList;
    }

    public void updateListView(List<FriendInfo> friendInfoList) {
        this.friendInfoList = friendInfoList;
        notifyDataSetChanged();
    }


    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < friendInfoList.size(); i++) {
            String sortStr = friendInfoList.get(i).getSortLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return friendInfoList.get(position).getSortLetter().charAt(0);
    }

    @Override
    public int getCount() {
        if (friendInfoList == null){
            return 0;
        }else {
            return friendInfoList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return friendInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder iHolder;
        if (convertView == null){
            convertView = View.inflate(context, R.layout.item_conversation_add_list, null);
            iHolder = new ItemViewHolder();
            iHolder.letterTv = convertView.findViewById(R.id.tv_conversation_add_item_letter);
            iHolder.nameTv = convertView.findViewById(R.id.tv_conversation_add_item_name);
            iHolder.signTv = convertView.findViewById(R.id.tv_conversation_add_item_sign);
            iHolder.avatarIv = convertView.findViewById(R.id.civ_conversation_add_item_avatar);
            convertView.setTag(iHolder);
        }else {
            iHolder = (ItemViewHolder)convertView.getTag();
        }

        ImgUtil.load(context, friendInfoList.get(position).getAvatar(), iHolder.avatarIv);
        iHolder.nameTv.setText(friendInfoList.get(position).getUsername());
        iHolder.signTv.setText(friendInfoList.get(position).getSign());

        final FriendInfo friendInfo = friendInfoList.get(position);
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            iHolder.letterTv.setVisibility(View.VISIBLE);
            iHolder.letterTv.setText(friendInfo.getSortLetter());
        } else {
            iHolder.letterTv.setVisibility(View.GONE);
        }
        return convertView;
    }

    private static class ItemViewHolder{
        private TextView letterTv, nameTv, signTv;
        private ImageView avatarIv;
    }
}
