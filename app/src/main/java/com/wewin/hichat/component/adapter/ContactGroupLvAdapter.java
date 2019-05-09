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
import com.wewin.hichat.model.db.entity.GroupInfo;

import java.util.List;

/**
 * Created by Darren on 2018/12/28.
 */
public class ContactGroupLvAdapter extends BaseAdapter implements SectionIndexer {

    private Context context;
    private List<GroupInfo> groupList;

    public ContactGroupLvAdapter(Context context, List<GroupInfo> groupList){
        this.context = context;
        this.groupList = groupList;
    }

    public void updateListView(List<GroupInfo> groupList) {
        this.groupList = groupList;
        notifyDataSetChanged();
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < groupList.size(); i++) {
            String sortStr = groupList.get(i).getSortLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return groupList.get(position).getSortLetter().charAt(0);
    }

    @Override
    public int getCount() {
        if (groupList == null){
            return 0;
        }else {
            return groupList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return groupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder iHolder;
        if (convertView == null){
            convertView = View.inflate(context, R.layout.item_contact_group_list, null);
            iHolder = new ItemViewHolder();
            iHolder.letterTv = convertView.findViewById(R.id.tv_contact_group_letter);
            iHolder.avatarIv = convertView.findViewById(R.id.civ_contact_group_avatar);
            iHolder.groupNameTv = convertView.findViewById(R.id.tv_contact_group_name);
            iHolder.introductionTv = convertView.findViewById(R.id.tv_contact_group_introduce);
            convertView.setTag(iHolder);
        }else {
            iHolder = (ItemViewHolder)convertView.getTag();
        }

        ImgUtil.load(context, groupList.get(position).getGroupAvatar(), iHolder.avatarIv);
        iHolder.groupNameTv.setText(groupList.get(position).getGroupName());
        iHolder.introductionTv.setText(groupList.get(position).getDescription());

        final GroupInfo groupInfo = groupList.get(position);
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            iHolder.letterTv.setVisibility(View.VISIBLE);
            iHolder.letterTv.setText(groupInfo.getSortLetter());
        } else {
            iHolder.letterTv.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ItemViewHolder {
        private ImageView avatarIv;
        private TextView groupNameTv, introductionTv, letterTv;
    }

}
