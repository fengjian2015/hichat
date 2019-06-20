package com.wewin.hichat.component.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.component.base.BaseSearchAdapter;
import com.wewin.hichat.model.db.entity.FriendInfo;


public class SearchFriendLvAdapter extends BaseSearchAdapter {

    private List<FriendInfo> friendInfoList;
    private Context context;
    private boolean checkVisible = true;

    public SearchFriendLvAdapter(Context mContext, List<FriendInfo> friendInfoList) {
        this.context = mContext;
        this.friendInfoList = friendInfoList;
    }

    public void setCheckVisible(boolean state) {
        this.checkVisible = state;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(List<FriendInfo> list) {
        this.friendInfoList = list;
        notifyDataSetChanged();
    }


    @Override
    public List<FriendInfo> getList() {
        return friendInfoList;
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder iHolder;
        if (convertView == null) {
            iHolder = new ItemViewHolder();
            convertView = View.inflate(context, R.layout.item_contact_friend_search_list, null);
            iHolder.letterTv = convertView.findViewById(R.id.tv_contact_friend_search_list_letter);
            iHolder.avatarIv = convertView.findViewById(R.id.iv_contact_friend_search_list_avatar);
            iHolder.nameTv = convertView.findViewById(R.id.tv_contact_friend_search_list_name);
            iHolder.checkIv = convertView.findViewById(R.id.iv_item_search_friend_check);
            convertView.setTag(iHolder);
        } else {
            iHolder = (ItemViewHolder) convertView.getTag();
        }

        setLetterVisible(iHolder.letterTv, position);

        ImgUtil.load(context, friendInfoList.get(position).getAvatar(), iHolder.avatarIv);

        String friendNote = friendInfoList.get(position).getFriendNote();
        if (TextUtils.isEmpty(friendNote)){
            iHolder.nameTv.setText(friendInfoList.get(position).getUsername());
        }else {
            iHolder.nameTv.setText(friendInfoList.get(position).getFriendNote()+"("+friendInfoList.get(position).getUsername()+")");
        }

        if (checkVisible) {
            iHolder.checkIv.setVisibility(View.VISIBLE);
            if (friendInfoList.get(position).isInvited()) {
                ImgUtil.load(context, R.drawable.selected_gray_big, iHolder.checkIv);
            } else if (friendInfoList.get(position).isChecked()) {
                ImgUtil.load(context, R.drawable.selected_blue_big, iHolder.checkIv);
            } else {
                ImgUtil.load(context, R.drawable.unselected_hollow_big, iHolder.checkIv);
            }
        } else {
            iHolder.checkIv.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ItemViewHolder {
        private TextView letterTv, nameTv;
        private ImageView avatarIv, checkIv;
    }

}