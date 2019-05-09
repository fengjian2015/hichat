package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.Subgroup;

import java.util.List;

/**
 * Created by Darren on 2018/12/24.
 */
public class ContactFriendElvAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Subgroup> friendTypeList;
    private OnGroupItemClickListener groupItemClickListener;
    private OnGroupItemLongClickListener groupItemLongClickListener;

    public ContactFriendElvAdapter(Context context, List<Subgroup> friendTypeList) {
        this.context = context;
        this.friendTypeList = friendTypeList;
    }

    public interface OnGroupItemClickListener{
        void itemClick(int groupPosition);
    }

    public interface OnGroupItemLongClickListener{
        void longClick(int groupPosition);
    }

    public void setOnGroupItemClickListener(OnGroupItemClickListener groupItemClickListener){
        this.groupItemClickListener = groupItemClickListener;
    }

    public void setOnGroupItemLongClickListener(OnGroupItemLongClickListener groupItemLongClickListener){
        this.groupItemLongClickListener = groupItemLongClickListener;
    }

    @Override
    public int getGroupCount() {
        if (friendTypeList == null) {
            return 0;
        } else {
            return friendTypeList.size();
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (friendTypeList == null || friendTypeList.isEmpty()
                || friendTypeList.get(groupPosition).getList() == null) {
            return 0;
        } else {
            return friendTypeList.get(groupPosition).getList().size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return friendTypeList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return friendTypeList.get(groupPosition).getList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder gHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_contact_friend_group, null);
            gHolder = new GroupViewHolder();
            gHolder.nameTv = convertView.findViewById(R.id.tv_contact_friend_type_name);
            gHolder.sumTv = convertView.findViewById(R.id.tv_contact_friend_type_num);
            gHolder.indicatorIv = convertView.findViewById(R.id.iv_item_contact_friend_group_indicator);
            gHolder.containerLl = convertView.findViewById(R.id.ll_item_contact_friend_group_container);
            convertView.setTag(gHolder);
        } else {
            gHolder = (GroupViewHolder) convertView.getTag();
        }

        if (isExpanded) {
            gHolder.indicatorIv.setImageResource(R.drawable.con_drop_right_black);

        } else {
            gHolder.indicatorIv.setImageResource(R.drawable.arrow_right_black);
        }

        gHolder.nameTv.setText(friendTypeList.get(groupPosition).getGroupName());
        gHolder.sumTv.setText(friendTypeList.get(groupPosition).getList().size() + "");

        gHolder.containerLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (groupItemLongClickListener != null){
                    groupItemLongClickListener.longClick(groupPosition);
                }
                return false;
            }
        });

        gHolder.containerLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupItemClickListener != null){
                    groupItemClickListener.itemClick(groupPosition);
                }
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder cHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_contact_friend_list, null);
            cHolder = new ChildViewHolder();
            cHolder.avatarCiv = convertView.findViewById(R.id.civ_item_contact_friend_avatar);
            cHolder.nameTv = convertView.findViewById(R.id.tv_item_contact_friend_name);
            cHolder.signTv = convertView.findViewById(R.id.tv_item_contact_friend_sign);
            convertView.setTag(cHolder);
        } else {
            cHolder = (ChildViewHolder) convertView.getTag();
        }

        List<FriendInfo> friendList = friendTypeList.get(groupPosition).getList();
        ImgUtil.load(context, friendList.get(childPosition).getAvatar(), cHolder.avatarCiv);
        cHolder.signTv.setText(friendList.get(childPosition).getSign());
        String friendNote = friendList.get(childPosition).getFriendNote();
        if (friendNote == null || friendNote.isEmpty()) {
            cHolder.nameTv.setText(friendList.get(childPosition).getUsername());
        } else {
            cHolder.nameTv.setText(friendNote);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class GroupViewHolder {
        private TextView nameTv, sumTv;
        private ImageView indicatorIv;
        private LinearLayout containerLl;
    }

    private static class ChildViewHolder {
        private ImageView avatarCiv;
        private TextView nameTv, signTv;
    }

}
