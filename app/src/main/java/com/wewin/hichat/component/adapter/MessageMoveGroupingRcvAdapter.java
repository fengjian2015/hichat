package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.component.base.BaseRcvAdapter;
import com.wewin.hichat.model.db.entity.Subgroup;

import java.util.List;

/**
 * Created by Darren on 2019/1/4.
 */
public class MessageMoveGroupingRcvAdapter extends BaseRcvAdapter {

    private Context context;
    private List<Subgroup> subgroupList;

    public MessageMoveGroupingRcvAdapter(Context context, List<Subgroup> subgroupList){
        this.context = context;
        this.subgroupList = subgroupList;
    }

    @Override
    protected Context getContext() {
        return context;
    }

    @Override
    protected List getObjectList() {
        return subgroupList;
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(int viewType) {
        return new ItemViewHolder(View.inflate(context, R.layout.item_message_chat_move_grouping, null));
    }

    @Override
    protected void bindViewData(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder && !subgroupList.isEmpty()){
            ItemViewHolder iHolder = (ItemViewHolder)holder;

            iHolder.groupingNameTv.setText(subgroupList.get(position).getGroupName());
            iHolder.checkIv.setSelected(subgroupList.get(position).isChecked());

            iHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null){
                        itemClickListener.itemClick(position);
                    }
                }
            });
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView checkIv;
        private TextView groupingNameTv;

        private ItemViewHolder(View itemView) {
            super(itemView);
            checkIv = itemView.findViewById(R.id.iv_item_contact_move_grouping_check);
            groupingNameTv = itemView.findViewById(R.id.tv_item_contact_move_grouping_name);
        }
    }

}
