package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.component.base.BaseRcvAdapter;
import com.wewin.hichat.component.constant.SpCons;
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
    private final int TYPE_NORMAL = 11;
    private final int TYPE_SYSTEM = 12;

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
        if (viewType == TYPE_NORMAL) {
            return new ItemViewHolder(View.inflate(context, R.layout.item_more_notify_normal, null));
        } else {
            return new SystemHolder(View.inflate(context, R.layout.item_more_notify_system, null));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!notifyList.isEmpty() && notifyList.get(position).getSystemMark() == 0) {
            return TYPE_NORMAL;
        } else if (!notifyList.isEmpty() && notifyList.get(position).getSystemMark() == 1) {
            return TYPE_SYSTEM;
        }
        return super.getItemViewType(position);
    }

    @Override
    protected void bindViewData(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder iHolder = (ItemViewHolder) holder;

            ImgUtil.load(context, notifyList.get(position).getAvatar(), iHolder.avatarIv);
            iHolder.nameTv.setText(notifyList.get(position).getName());

            if (TextUtils.isEmpty(notifyList.get(position).getSystemNote())) {
                iHolder.noteTv.setVisibility(View.GONE);
            } else {
                iHolder.noteTv.setVisibility(View.VISIBLE);
                iHolder.noteTv.setText(notifyList.get(position).getSystemNote());
            }

            if (TextUtils.isEmpty(notifyList.get(position).getRemark())) {
                iHolder.remarkTv.setVisibility(View.GONE);
            } else {
                iHolder.remarkTv.setVisibility(View.VISIBLE);
                iHolder.remarkTv.setText(notifyList.get(position).getRemark());
            }

            switch (notifyList.get(position).getStatus()) {
                case Notify.STATUS_REFUSE:
                    iHolder.agreeBtn.setVisibility(View.GONE);
                    iHolder.refuseBtn.setVisibility(View.GONE);
                    iHolder.stateTv.setVisibility(View.VISIBLE);
                    iHolder.stateTv.setText(context.getString(R.string.refused));
                    break;

                case Notify.STATUS_WAIT:
                    if (SpCons.getUser(context).getId().equals(notifyList.get(position).getSenderId())) {
                        iHolder.agreeBtn.setVisibility(View.GONE);
                        iHolder.refuseBtn.setVisibility(View.GONE);
                        iHolder.stateTv.setVisibility(View.VISIBLE);
                        iHolder.stateTv.setText(context.getString(R.string.wait_for_verify));
                    } else {
                        iHolder.agreeBtn.setVisibility(View.VISIBLE);
                        iHolder.refuseBtn.setVisibility(View.VISIBLE);
                        iHolder.stateTv.setVisibility(View.GONE);
                    }
                    break;

                case Notify.STATUS_AGREE:
                    iHolder.agreeBtn.setVisibility(View.GONE);
                    iHolder.refuseBtn.setVisibility(View.GONE);
                    iHolder.stateTv.setVisibility(View.VISIBLE);
                    iHolder.stateTv.setText(context.getString(R.string.agreed));
                    break;

                default:
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

        } else if (holder instanceof SystemHolder) {
            SystemHolder sHolder = (SystemHolder) holder;
            sHolder.systemMsgTv.setText(notifyList.get(position).getSystemMessage());
            sHolder.timeTv.setText(TimeUtil.timestampToStr(notifyList.get(position).getCreateTime(),
                    "yyyy年MM月dd日 HH:mm:ss"));
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarIv;
        private TextView nameTv, noteTv, stateTv, remarkTv;
        private Button agreeBtn, refuseBtn;

        private ItemViewHolder(View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.civ_item_more_notify_avatar);
            nameTv = itemView.findViewById(R.id.tv_more_notify_name);
            noteTv = itemView.findViewById(R.id.tv_more_notify_note);
            stateTv = itemView.findViewById(R.id.tv_item_more_notify_state);
            remarkTv = itemView.findViewById(R.id.tv_more_notify_remark);
            agreeBtn = itemView.findViewById(R.id.btn_item_more_notify_agree);
            refuseBtn = itemView.findViewById(R.id.btn_item_more_notify_refuse);
        }
    }

    private static class SystemHolder extends RecyclerView.ViewHolder {
        private TextView systemMsgTv;
        private TextView timeTv;

        private SystemHolder(View itemView) {
            super(itemView);
            systemMsgTv = itemView.findViewById(R.id.tv_more_notify_system_message);
            timeTv = itemView.findViewById(R.id.tv_more_notify_time);
        }
    }

}
