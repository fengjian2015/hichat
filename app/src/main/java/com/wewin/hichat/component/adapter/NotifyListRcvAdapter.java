package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.component.base.BaseRcvAdapter;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.LoginUser;
import com.wewin.hichat.model.db.entity.Notify;

import java.util.List;

/**
 * Created by Darren on 2018/12/26.
 */
public class NotifyListRcvAdapter extends BaseRcvAdapter {

    private Context context;
    private List<Notify> notifyList;
    private OnBtnClickListener btnClickListener;

    public NotifyListRcvAdapter(Context context, List<Notify> notifyList) {
        this.context = context;
        this.notifyList = notifyList;
    }

    public interface OnBtnClickListener {
        void agreeClick(int position);

        void refuseClick(int position);
    }

    public void setOnBtnClickListener(OnBtnClickListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    @Override
    protected Context getContext() {
        return context;
    }

    @Override
    protected List getObjectList() {
        return notifyList;
    }

    @Override
    protected RecyclerView.ViewHolder getTopViewHolder() {
        return null;
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(int viewType) {
        return new ItemViewHolder(View.inflate(context, R.layout.item_more_notify_friend, null));
    }

    @Override
    protected void bindViewData(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder && !notifyList.isEmpty()) {
            ItemViewHolder iHolder = (ItemViewHolder) holder;

            boolean isMine = UserDao.user.getId().equals(notifyList.get(position).getUid());
            LoginUser sendUser = notifyList.get(position).getUser();
            LoginUser receiveUser = notifyList.get(position).getFromUser();
            Notify.GroupFrom group = notifyList.get(position).getGroupFrom();
            int type = notifyList.get(position).getType();

            String avatarUrl = null;
            String nickname = null;
            String applyInfo = null;
            if (isMine && type == Notify.TYPE_FRIEND && receiveUser != null) {
                avatarUrl = receiveUser.getAvatar();
                nickname = receiveUser.getUsername();
                applyInfo = context.getString(R.string.you_send_add_friend_notify);

            } else if (!isMine && type == Notify.TYPE_FRIEND && sendUser != null) {
                avatarUrl = sendUser.getAvatar();
                nickname = sendUser.getUsername();
                applyInfo = context.getString(R.string.other_side_send_add_friend_notify);

            } else if (isMine && type == Notify.TYPE_GROUP && group != null) {
                avatarUrl = group.getAvatar();
                nickname = group.getGroupName();
                applyInfo = context.getString(R.string.send_join_group_apply);

            } else if (!isMine && type == Notify.TYPE_GROUP && group != null && sendUser != null) {
                avatarUrl = group.getAvatar();
                nickname = group.getGroupName();
                applyInfo = sendUser.getUsername() + context.getString(R.string.apply_join_group);
            }
            ImgUtil.load(context, avatarUrl, iHolder.avatarIv);
            iHolder.nameTv.setText(nickname);
            iHolder.infoTv.setText(applyInfo);
            iHolder.dateTv.setText(TimeUtil.timestampToStr(notifyList.get(position).getTime(), "yyyy-MM-dd"));
            iHolder.extraTv.setText(notifyList.get(position).getRemark());

            int status = notifyList.get(position).getStatus();
            switch (status) {
                case Notify.STATUS_REFUSE:
                    iHolder.agreeBtn.setVisibility(View.GONE);
                    iHolder.refuseBtn.setVisibility(View.GONE);
                    iHolder.statusTv.setVisibility(View.VISIBLE);
                    iHolder.statusTv.setText(R.string.refused);
                    break;

                case Notify.STATUS_WAIT:
                    if (isMine) {
                        iHolder.statusTv.setText(R.string.wait_for_verify);
                        iHolder.agreeBtn.setVisibility(View.GONE);
                        iHolder.refuseBtn.setVisibility(View.GONE);
                        iHolder.statusTv.setVisibility(View.VISIBLE);
                    } else {
                        iHolder.agreeBtn.setVisibility(View.VISIBLE);
                        iHolder.refuseBtn.setVisibility(View.VISIBLE);
                        iHolder.statusTv.setVisibility(View.GONE);
                    }
                    break;

                case Notify.STATUS_AGREE:
                    iHolder.agreeBtn.setVisibility(View.GONE);
                    iHolder.refuseBtn.setVisibility(View.GONE);
                    iHolder.statusTv.setVisibility(View.VISIBLE);
                    iHolder.statusTv.setText(R.string.agreed);
                    break;
            }

            iHolder.agreeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnClickListener != null) {
                        btnClickListener.agreeClick(position);
                    }
                }
            });

            iHolder.refuseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnClickListener != null) {
                        btnClickListener.refuseClick(position);
                    }
                }
            });
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarIv;
        private TextView nameTv, infoTv, dateTv, statusTv, extraTv;
        private Button agreeBtn, refuseBtn;

        private ItemViewHolder(View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.civ_item_more_notify_avatar);
            nameTv = itemView.findViewById(R.id.tv_more_notify_name);
            infoTv = itemView.findViewById(R.id.tv_more_notify_info);
            dateTv = itemView.findViewById(R.id.tv_more_notify_date);
            statusTv = itemView.findViewById(R.id.tv_item_more_notify_state);
            extraTv = itemView.findViewById(R.id.tv_more_notify_extra_msg);
            agreeBtn = itemView.findViewById(R.id.btn_item_more_notify_agree);
            refuseBtn = itemView.findViewById(R.id.btn_item_more_notify_refuse);
        }
    }
}
