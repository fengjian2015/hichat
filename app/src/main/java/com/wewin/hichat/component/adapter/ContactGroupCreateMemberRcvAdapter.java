package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.model.db.entity.FriendInfo;

import java.util.List;

/**
 * Created by Darren on 2019/3/6
 */
public class ContactGroupCreateMemberRcvAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<FriendInfo> friendInfoList;
    private OnRcvItemClickListener itemClickListener;

    public ContactGroupCreateMemberRcvAdapter(Context context, List<FriendInfo> friendInfoList) {
        this.context = context;
        this.friendInfoList = friendInfoList;
    }

    public interface OnRcvItemClickListener{
        void itemClick(int position);
    }

    public void setOnRcvItemClickListener(OnRcvItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(View.inflate(context, R.layout.item_contact_add_group_create, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder iHolder = (ItemViewHolder) holder;
        if (position == 0) {
            iHolder.addIconIv.setVisibility(View.VISIBLE);
            ImgUtil.load(context, R.drawable.corner_gray_fa, iHolder.avatarIv);
            iHolder.nameTv.setText(context.getString(R.string.add));

        } else {
            int currentPosition = position - 1;
            iHolder.addIconIv.setVisibility(View.INVISIBLE);
            ImgUtil.load(context, friendInfoList.get(currentPosition).getAvatar(), iHolder.avatarIv);
            iHolder.nameTv.setText(friendInfoList.get(currentPosition).getUsername());
        }

        iHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null){
                    itemClickListener.itemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendInfoList.size() + 1;
    }


    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatarIv, addIconIv;
        private TextView nameTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.iv_item_contact_add_group_create_member_avatar);
            addIconIv = itemView.findViewById(R.id.iv_item_contact_add_group_create_add);
            nameTv = itemView.findViewById(R.id.tv_item_contact_add_group_create_member_name);
        }
    }

}
