package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.SelectSubgroup;
import com.wewin.hichat.model.db.entity.Subgroup;
import com.wewin.hichat.view.conversation.SelectSendActivity;

import java.util.List;

/**
 * Created by Darren on 2018/12/24.
 */
public class ConversationSelectSendElvAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<SelectSubgroup> mSelectSubgroupList;
    private OnGroupItemClickListener groupItemClickListener;
    private List<SelectSubgroup.DataBean> mSelectList;

    public ConversationSelectSendElvAdapter(Context context, List<SelectSubgroup> selectSubgroup, List<SelectSubgroup.DataBean> selectList) {
        this.context = context;
        this.mSelectSubgroupList = selectSubgroup;
        mSelectList=selectList;
    }

    public interface OnGroupItemClickListener {
        void itemClick(int groupPosition);
    }


    public void setOnGroupItemClickListener(OnGroupItemClickListener groupItemClickListener) {
        this.groupItemClickListener = groupItemClickListener;
    }

    @Override
    public int getGroupCount() {
        if (mSelectSubgroupList == null) {
            return 0;
        } else {
            return mSelectSubgroupList.size();
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mSelectSubgroupList == null || mSelectSubgroupList.isEmpty()
                || mSelectSubgroupList.get(groupPosition).getData() == null) {
            return 0;
        } else {
            return mSelectSubgroupList.get(groupPosition).getData().size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mSelectSubgroupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mSelectSubgroupList.get(groupPosition).getData().get(childPosition);
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
            convertView = View.inflate(context, R.layout.item_conversation_select_send_elv, null);
            gHolder = new GroupViewHolder();
            gHolder.nameTv = convertView.findViewById(R.id.tv_contact_friend_type_name);
            gHolder.indicatorIv = convertView.findViewById(R.id.iv_item_contact_friend_group_indicator);
            convertView.setTag(gHolder);
        } else {
            gHolder = (GroupViewHolder) convertView.getTag();
        }

        if (isExpanded) {
            gHolder.indicatorIv.setImageResource(R.drawable.con_drop_right_black);

        } else {
            gHolder.indicatorIv.setImageResource(R.drawable.arrow_right_black);
        }

        gHolder.nameTv.setText(mSelectSubgroupList.get(groupPosition).getGroupName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupItemClickListener != null) {
                    groupItemClickListener.itemClick(groupPosition);
                }
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, final boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder cHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_conversation_select_child_list, null);
            cHolder = new ChildViewHolder();
            cHolder.avatarIv = convertView.findViewById(R.id.iv_contact_friend_search_list_avatar);
            cHolder.nameTv = convertView.findViewById(R.id.tv_contact_friend_search_list_name);
            cHolder.checkIv = convertView.findViewById(R.id.iv_item_search_friend_check);
            convertView.setTag(cHolder);
        } else {
            cHolder = (ChildViewHolder) convertView.getTag();
        }
        final SelectSubgroup.DataBean dataBean = mSelectSubgroupList.get(groupPosition).getData().get(childPosition);
        ImgUtil.load(context, dataBean.getAvatar(), cHolder.avatarIv);
        cHolder.nameTv.setText(dataBean.getUsername());
        ImgUtil.load(context, R.drawable.unselected_hollow_big, cHolder.checkIv);
        boolean isSelect=false;
        for(SelectSubgroup.DataBean dataBean1:mSelectList){
            if (dataBean1.getRoomId().equals(dataBean.getRoomId())&&dataBean1.getRoomType().equals(dataBean.getRoomType())){
                ImgUtil.load(context, R.drawable.selected_blue_big, cHolder.checkIv);
                isSelect=true;
                break;
            }
        }
        final boolean finalIsSelect = isSelect;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataBean.getSendMark()==0) {
                    if (!finalIsSelect) {
                        if (context instanceof SelectSendActivity
                                && ((SelectSendActivity) context).updateSelect()) {
                            mSelectList.add(dataBean);
                            notifyDataSetChanged();
                        }
                    } else {
                        for (SelectSubgroup.DataBean dataBean1 : mSelectList) {
                            if (dataBean1.getRoomId().equals(dataBean.getRoomId()) && dataBean1.getRoomType().equals(dataBean.getRoomType())) {
                                mSelectList.remove(dataBean1);
                                notifyDataSetChanged();
                                if (context instanceof SelectSendActivity) {
                                    ((SelectSendActivity) context).updateSelect();
                                }
                                break;
                            }
                        }
                    }
                }else {
                    ToastUtil.showShort(context,context.getString(R.string.been_banned_forwarding));
                }
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class GroupViewHolder {
        private TextView nameTv;
        private ImageView indicatorIv;
    }

    private static class ChildViewHolder {
        private TextView nameTv;
        private ImageView avatarIv, checkIv;
    }

}
