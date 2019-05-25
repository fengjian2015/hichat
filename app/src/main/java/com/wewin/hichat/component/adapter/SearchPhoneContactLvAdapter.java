package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.model.db.entity.PhoneContact;
import com.wewin.hichat.model.db.entity.SortModel;

import java.util.List;

/**
 * Created by Darren on 2019/2/14
 */
public class SearchPhoneContactLvAdapter extends BaseAdapter implements SectionIndexer {

    private Context context;
    private List<SortModel> sortModelList;
    private OnInviteClickListener inviteClickListener;

    public SearchPhoneContactLvAdapter(Context context, List<SortModel> sortModelList) {
        this.context = context;
        this.sortModelList = sortModelList;
    }

    public interface OnInviteClickListener {
        void inviteClick(int position);
    }

    public void setOnInviteClickListener(OnInviteClickListener inviteClickListener) {
        this.inviteClickListener = inviteClickListener;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(List<SortModel> list) {
        this.sortModelList = list;
        notifyDataSetChanged();
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < sortModelList.size(); i++) {
            String sortStr = sortModelList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {
        return sortModelList.get(position).getSortLetters().charAt(0);
    }

    @Override
    public int getCount() {
        if (sortModelList == null) {
            return 0;
        } else {
            return sortModelList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return sortModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemViewHolder iHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_contact_friend_phone_contact, null);
            iHolder = new ItemViewHolder();
            iHolder.letterTv = convertView.findViewById(R.id.tv_contact_friend_phone_contact_item_letter);
            iHolder.avatarIv = convertView.findViewById(R.id.iv_contact_friend_phone_contact_item_avatar);
            iHolder.nameTv = convertView.findViewById(R.id.tv_contact_friend_phone_contact_item_name);
            iHolder.phoneNumTv = convertView.findViewById(R.id.tv_contact_friend_phone_contact_item_num);
            iHolder.stateTv = convertView.findViewById(R.id.tv_contact_friend_phone_contact_item_right);
            iHolder.avatarNameTv = convertView.findViewById(R.id.tv_contact_friend_phone_contact_item_avatar_name);
            convertView.setTag(iHolder);
        } else {
            iHolder = (ItemViewHolder) convertView.getTag();
        }

        final SortModel mContent = sortModelList.get(position);
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            iHolder.letterTv.setVisibility(View.VISIBLE);
            iHolder.letterTv.setText(mContent.getSortLetters());
        } else {
            iHolder.letterTv.setVisibility(View.GONE);
        }

        String name = sortModelList.get(position).getName();
        if (!TextUtils.isEmpty(name)) {
            iHolder.nameTv.setText(name);
            iHolder.avatarNameTv.setText(name.substring(0, 1));
        }
        iHolder.phoneNumTv.setText(sortModelList.get(position).getCode());

        switch (sortModelList.get(position).getAvatar()) {
            case "0":
                iHolder.avatarIv.setImageResource(R.drawable.round_green_avatar_40);
                break;

            case "1":
                iHolder.avatarIv.setImageResource(R.drawable.round_yellow_avatar_40);
                break;

            case "2":
                iHolder.avatarIv.setImageResource(R.drawable.round_blue_avatar_40);
                break;

            case "3":
                iHolder.avatarIv.setImageResource(R.drawable.round_purple_avatar_40);
                break;

            case "4":
                iHolder.avatarIv.setImageResource(R.drawable.round_red_avatar_40);
                break;

            default:
                break;
        }

        switch (sortModelList.get(position).getState()) {
            case PhoneContact.NOT_INVITE:
                iHolder.stateTv.setText(context.getString(R.string.invite));
                iHolder.stateTv.setEnabled(true);
                break;

            case PhoneContact.INVITED:
                iHolder.stateTv.setText(context.getString(R.string.invited));
                iHolder.stateTv.setEnabled(false);
                break;

            case PhoneContact.ADDED:
                iHolder.stateTv.setText(context.getString(R.string.added));
                iHolder.stateTv.setEnabled(false);
                break;

            default:
                break;
        }

        iHolder.stateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inviteClickListener != null) {
                    inviteClickListener.inviteClick(position);
                }
            }
        });

        return convertView;
    }



    private static class ItemViewHolder {
        private TextView letterTv, nameTv, phoneNumTv, stateTv, avatarNameTv;
        private ImageView avatarIv;
    }

}
