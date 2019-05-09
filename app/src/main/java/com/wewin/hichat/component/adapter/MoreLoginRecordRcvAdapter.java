package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.component.base.BaseRcvAdapter;
import com.wewin.hichat.model.db.entity.LoginRecord;

import java.util.List;

/**
 * Created by Darren on 2019/3/1
 */
public class MoreLoginRecordRcvAdapter extends BaseRcvAdapter {

    private Context context;
    private List<LoginRecord> recordList;

    public MoreLoginRecordRcvAdapter(Context context, List<LoginRecord> recordList){
        this.context = context;
        this.recordList = recordList;
    }

    @Override
    protected Context getContext() {
        return context;
    }

    @Override
    protected List getObjectList() {
        return recordList;
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(int viewType) {
        return new ItemViewHolder(View.inflate(context, R.layout.item_more_account_login_record, null));
    }

    @Override
    protected void bindViewData(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder && !recordList.isEmpty()){
            ItemViewHolder iHolder = (ItemViewHolder)holder;
            iHolder.deviceTypeTv.setText(recordList.get(position).getTerminal());
            iHolder.ipTv.setText(recordList.get(position).getIp());
            iHolder.timeTv.setText(TimeUtil.timestampToStr(recordList.get(position).getLoginDate(),
                    "yyyy-MM-dd HH:mm"));

        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView deviceTypeTv, ipTv, timeTv, abnormalTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            deviceTypeTv = itemView.findViewById(R.id.tv_more_login_record_device_type);
            ipTv = itemView.findViewById(R.id.tv_more_login_record_device_ip);
            timeTv = itemView.findViewById(R.id.tv_more_login_record_time);
            abnormalTv = itemView.findViewById(R.id.tv_more_login_record_abnormal_prompt);
        }
    }

}
