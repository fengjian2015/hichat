package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.component.base.BaseSearchAdapter;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;

import java.util.List;

/**
 * Created by Darren on 2019/3/2
 */
public class ContactGroupMemberLvAdapter extends BaseSearchAdapter {

    private Context context;
    private List<FriendInfo> memberList;

    public ContactGroupMemberLvAdapter(Context context, List<FriendInfo> memberList) {
        this.context = context;
        this.memberList = memberList;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(List<FriendInfo> list) {
        this.memberList = list;
        notifyDataSetChanged();
    }

    @Override
    public List getList() {
        return memberList;
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder iHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_contact_group_member, null);
            iHolder = new ItemViewHolder();
            iHolder.letterTv = convertView.findViewById(R.id.tv_item_contact_group_member_letter);
            iHolder.avatarIv = convertView.findViewById(R.id.iv_item_contact_group_member_avatar);
            iHolder.nameTv = convertView.findViewById(R.id.tv_item_contact_group_member_name);
            iHolder.roleTv = convertView.findViewById(R.id.tv_item_contact_group_member_role);
            convertView.setTag(iHolder);
        } else {
            iHolder = (ItemViewHolder) convertView.getTag();
        }
        if (memberList.get(position).getGrade() == GroupInfo.TYPE_GRADE_MANAGER||memberList.get(position).getGrade() == GroupInfo.TYPE_GRADE_OWNER) {
            iHolder.letterTv.setVisibility(View.GONE);
        }else {
            setLetterVisible(iHolder.letterTv, position);
        }
        ImgUtil.load(context, memberList.get(position).getAvatar(), iHolder.avatarIv);
        iHolder.nameTv.setText(memberList.get(position).getUsername());
        if (memberList.get(position).getGrade() == 1) {
            iHolder.roleTv.setVisibility(View.VISIBLE);
            iHolder.roleTv.setText(R.string.manager);
        } else if (memberList.get(position).getGrade() == 2) {
            iHolder.roleTv.setVisibility(View.VISIBLE);
            iHolder.roleTv.setText(R.string.group_owner);
        } else {
            iHolder.roleTv.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ItemViewHolder {
        private ImageView avatarIv;
        private TextView nameTv, roleTv, letterTv;
    }
}
