package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.component.base.BaseRcvAdapter;
import com.wewin.hichat.model.db.entity.Announcement;
import com.wewin.hichat.model.db.entity.LoginUser;

import java.util.List;

/**
 * Created by Darren on 2019/1/15.
 */
public class ContactGroupAnnouncementListRcvAdapter extends BaseRcvAdapter {

    private Context context;
    private List<Announcement> announcementList;
    private OnMoreClickListener moreClickListener;

    public ContactGroupAnnouncementListRcvAdapter(Context context, List<Announcement> announcementList) {
        this.context = context;
        this.announcementList = announcementList;
    }

    public interface OnMoreClickListener {
        void deleteClick(int position);

        void editClick(int position);
    }

    public void setOnMoreClickListener(OnMoreClickListener moreClickListener) {
        this.moreClickListener = moreClickListener;
    }

    @Override
    protected Context getContext() {
        return context;
    }

    @Override
    protected List getObjectList() {
        return announcementList;
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(int viewType) {
        return new ItemViewHolder(View.inflate(context, R.layout.item_contact_group_announcement_list, null));
    }

    @Override
    protected void bindViewData(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder && !announcementList.isEmpty()) {
            final ItemViewHolder iHolder = (ItemViewHolder) holder;
            LoginUser account = announcementList.get(position).getAccount();
            if (account != null) {
                ImgUtil.load(context, account.getAvatar(), iHolder.avatarIv);
                iHolder.nameTv.setText(account.getUsername());
                iHolder.timeTv.setText(account.getUsername() + "发表于" + TimeUtil.timestampToStr(
                        announcementList.get(position).getPostTime(), "yyyy年MM月dd日"));
            }
            iHolder.titleTv.setText(announcementList.get(position).getTitle());
            iHolder.contentTv.setText(announcementList.get(position).getContent());

            iHolder.moreIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMorePop(v, position);
                }
            });

            iHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.itemClick(position);
                    }
                }
            });
        }
    }

    private void showMorePop(View clickView, final int position) {
        View popView = View.inflate(context, R.layout.pop_contact_group_announcement_more, null);
        TextView deleteTv = popView.findViewById(R.id.tv_pop_contact_group_announcement_delete);
        TextView editTv = popView.findViewById(R.id.tv_pop_contact_group_announcement_edit);

        final PopupWindow morePop = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        morePop.setFocusable(true);
        LogUtil.i("width", popView.getWidth());
        morePop.showAsDropDown(clickView, -(int) (clickView.getWidth() * 2.8),
                -(int) (clickView.getHeight() / 1.2));

        deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morePop.dismiss();
                if (moreClickListener != null) {
                    moreClickListener.deleteClick(position);
                }
            }
        });

        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morePop.dismiss();
                if (moreClickListener != null) {
                    moreClickListener.editClick(position);
                }
            }
        });
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarIv, moreIv;
        private TextView nameTv, titleTv, contentTv, timeTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.civ_item_contact_group_announcement_avatar);
            moreIv = itemView.findViewById(R.id.iv_item_contact_group_announcement_more);
            nameTv = itemView.findViewById(R.id.tv_item_contact_group_announcement_name);
            titleTv = itemView.findViewById(R.id.tv_item_contact_group_announcement_title);
            contentTv = itemView.findViewById(R.id.tv_item_contact_group_announcement_content);
            timeTv = itemView.findViewById(R.id.tv_item_contact_group_announcement_time);
        }
    }

}