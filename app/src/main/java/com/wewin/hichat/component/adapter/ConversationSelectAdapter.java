package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.model.db.entity.SelectSubgroup;
import com.wewin.hichat.view.conversation.SelectSendActivity;

import java.util.List;

/**
 * @author jason
 */
public class ConversationSelectAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private  List<SelectSubgroup.DataBean> mSelectList;

    public ConversationSelectAdapter(Context context, List<SelectSubgroup.DataBean> selectList) {
        mContext = context;
        mSelectList = selectList;
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_conversation_select_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mSelectList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mSelectList.size() != 0) {
            return mSelectList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        int position;
        ImageView ivAvatar;
        ViewHolder(View view) {
            super(view);
            ivAvatar=view.findViewById(R.id.iv_avatar);
        }

        public void setData(SelectSubgroup.DataBean dataBean, final int position) {
            this.position = position;
            ImgUtil.load(mContext, dataBean.getAvatar(), ivAvatar);
            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectList.remove(position);
                    notifyDataSetChanged();
                    if(mContext instanceof SelectSendActivity){
                        ((SelectSendActivity)mContext).notifyData();
                    }
                }
            });
        }
    }
}
