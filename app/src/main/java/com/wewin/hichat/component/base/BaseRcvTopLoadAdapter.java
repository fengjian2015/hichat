package com.wewin.hichat.component.base;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by darren on 2017/9/26.
 */

public abstract class BaseRcvTopLoadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_PULL_TOP = -2;
    //顶部下拉加载是否显示
    private boolean baseTpHolderVisible = false;
    private boolean hasData = true;
    private List objectList;
    private TopPullViewHolder baseTpHolder;
    public OnItemClickListener itemClickListener;
    private OnLoadDataListener loadDataListener;
    private LoadHandler loadHandler = new LoadHandler(new WeakReference<>(this));

    protected abstract Context getContext();

    protected abstract List getObjectList();

    protected abstract RecyclerView.ViewHolder getItemViewHolder(int viewType);

    protected abstract void bindViewData(RecyclerView.ViewHolder holder, int position);

    public interface OnItemClickListener {
        void itemClick(int position);
    }

    public interface OnLoadDataListener{
        void loadData();
    }

    //条目点击
    public void setOnItemClickListener(OnItemClickListener itemListener) {
        this.itemClickListener = itemListener;
    }

    //数据加载
    public void setOnLoadDataListener(OnLoadDataListener loadDataListener){
        if (hasData){
            this.loadDataListener = loadDataListener;
        }
    }

    public void setTopPullViewVisible(boolean state){
        baseTpHolderVisible = state;
    }

    public boolean getTopPullViewVisible(){
        return baseTpHolderVisible;
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_PULL_TOP) {
            return new TopPullViewHolder(View.inflate(getContext(), R.layout.item_rcv_bot, null));
        } else {
            return getItemViewHolder(viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0 && holder instanceof TopPullViewHolder) {
            baseTpHolder = (TopPullViewHolder) holder;
            if (hasData) {
                baseTpHolder.loadingIv.setVisibility(View.VISIBLE);
                baseTpHolder.botTv.setVisibility(View.VISIBLE);
                baseTpHolder.botTv.setText("加载中...");

                RotateAnimation rotateAnim360 = new RotateAnimation(0f, 359f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnim360.setDuration(600);
                rotateAnim360.setRepeatCount(-1);
                rotateAnim360.setInterpolator(new LinearInterpolator());
                baseTpHolder.loadingIv.startAnimation(rotateAnim360);

                loadHandler.sendEmptyMessageDelayed(LoadHandler.HANDLER_MSG_TOP_PULL, 500);

            } else {
                baseTpHolder.loadingIv.clearAnimation();
                baseTpHolder.loadingIv.setVisibility(View.GONE);
                baseTpHolder.botTv.setText("没有更多数据了");
                baseTpHolder.botTv.setVisibility(View.VISIBLE);
            }

        } else if (objectList != null && objectList.size() > 0) {
            if (baseTpHolderVisible){
                bindViewData(holder, position - 1);
            }else {
                bindViewData(holder, position);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (baseTpHolderVisible && position == 0) {
            return TYPE_PULL_TOP;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (objectList == null) {
            objectList = getObjectList();
        }
        if (objectList == null) {
            return 0;
        } else if (baseTpHolderVisible){
            return objectList.size() + 1;
        }else {
            return objectList.size();
        }
    }

    private static class TopPullViewHolder extends RecyclerView.ViewHolder {
        ImageView loadingIv;
        TextView botTv;

        private TopPullViewHolder(View itemView) {
            super(itemView);
            loadingIv = itemView.findViewById(R.id.iv_loading);
            botTv = itemView.findViewById(R.id.tv_bot_text);
        }
    }

    private static class LoadHandler extends Handler{

        private static final int HANDLER_MSG_TOP_PULL = 100;
        private WeakReference<BaseRcvTopLoadAdapter> reference;

        private LoadHandler(WeakReference<BaseRcvTopLoadAdapter> reference) {
            super();
            this.reference = reference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (reference == null){
                return;
            }
            if (msg.what == HANDLER_MSG_TOP_PULL) {
                reference.get().baseTpHolder.loadingIv.clearAnimation();
                reference.get().baseTpHolder.loadingIv.setVisibility(View.GONE);
                if (!reference.get().hasData){
                    reference.get().baseTpHolder.botTv.setText("没有更多数据了");
                    reference.get().baseTpHolder.botTv.setVisibility(View.VISIBLE);
                }else {
                    reference.get().baseTpHolder.botTv.setText("加载中...");
                    reference.get().baseTpHolder.botTv.setVisibility(View.INVISIBLE);
                    if (reference.get().loadDataListener != null){
                        reference.get().loadDataListener.loadData();
                    }
                }
            }
        }
    }

}
