package com.wewin.hichat.component.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.SystemUtil;

import java.util.List;

/**
 * Created by Darren on 2017/9/26.
 */

public abstract class BaseRcvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_EMPTY = -1;
    private final int TYPE_TOP = -2;
    private boolean isTopHolderGet = false;
    private boolean isDataListGet = false;
    private boolean isEmptyViewVisible = true;
    private boolean isFirst = true;
    private int imgResourceId;
    private int emptyViewMarginTop;
    private List objectList;
    private String promptStr;
    private String btnText;
    private RecyclerView.ViewHolder baseTopHolder;
    private OnEmptyRefreshListener refreshListener;
    public OnItemClickListener itemClickListener;
    private OnEmptyBtnClickListener btnClickListener;

    protected abstract Context getContext();

    protected abstract List getObjectList();

    protected RecyclerView.ViewHolder getTopViewHolder(){
        return null;
    }

    protected abstract RecyclerView.ViewHolder getItemViewHolder(int viewType);

    protected abstract void bindViewData(RecyclerView.ViewHolder holder, int position);

    //无数据页面按钮点击事件
    public interface OnEmptyBtnClickListener {
        void btnClick();
    }

    //无数据页面刷新
    public interface OnEmptyRefreshListener {
        void refresh();
    }

    //条目点击
    public interface OnItemClickListener {
        void itemClick(int position);
    }

    public void setOnEmptyBtnClickListener(OnEmptyBtnClickListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    public void setOnEmptyRefreshListener(OnEmptyRefreshListener listener) {
        this.refreshListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener itemListener) {
        this.itemClickListener = itemListener;
    }


    public void setEmptyData(int imgResourceId, String promptStr, String btnText) {
        this.promptStr = promptStr;
        this.imgResourceId = imgResourceId;
        this.btnText = btnText;
    }


    public void setEmptyViewVisible(boolean state) {
        this.isEmptyViewVisible = state;
    }

    public void setEmptyViewMarginTop(int marginTop){
        this.emptyViewMarginTop = marginTop;
    }

    public void remove(int position) {
        objectList.remove(position);
        notifyItemRemoved(position);
        if (position != objectList.size()) {
            notifyItemRangeChanged(position, objectList.size() - position);
        }
    }

    public void remove(Object obj) {
        objectList.remove(obj);
        notifyItemRemoved(objectList.indexOf(obj));
        if (objectList.indexOf(obj) != objectList.size()) {
            notifyItemRangeChanged(objectList.indexOf(obj), objectList.size() - objectList.indexOf(obj));
        }
    }

    public void removeList(int positionStart, List objList) {
        for (int i = 0; i < objList.size(); i++) {
            if (objectList.size() > positionStart + i) {
                objectList.remove(i);
            }
        }
        notifyItemRangeRemoved(positionStart, objList.size());
    }

    public void add(int position, Object obj) {
        objectList.add(position, obj);
        notifyItemInserted(position);
    }

    public void add(Object obj) {
        objectList.add(0, obj);
        notifyItemInserted(0);
    }

    public void addList(int positionStart, List objList) {
        objectList.addAll(objList);
        notifyItemRangeInserted(positionStart, objList.size());
    }

    public void change(int position, Object obj) {
        objectList.remove(position);
        objectList.add(position, obj);
        notifyItemChanged(position);
    }

    public void change(Object obj) {
        int position = objectList.indexOf(obj);
        objectList.remove(obj);
        objectList.add(position, obj);
        notifyItemChanged(position);
    }

    public void changeList(int positionStart, List objList) {
        for (int i = 0; i < objList.size(); i++) {
            if (objectList.size() > positionStart + i) {
                objectList.remove(i);
            }
        }
        objectList.addAll(objList);
        notifyItemRangeChanged(positionStart, objList.size());
    }

    public void refreshList(List objList) {
        objectList.clear();
        objectList.addAll(objList);
        notifyItemRangeInserted(0, objList.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            return new EmptyViewHolder(View.inflate(getContext(), R.layout.item_rcv_empty, null));
        } else if (viewType == TYPE_TOP) {
            return getTopViewHolder();
        } else {
            return getItemViewHolder(viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmptyViewHolder) {
            EmptyViewHolder eHolder = (EmptyViewHolder) holder;
            if (isFirst || !isEmptyViewVisible) {
                eHolder.emptyLl.setVisibility(View.GONE);
                isFirst = false;
            } else {
                eHolder.emptyLl.setVisibility(View.VISIBLE);
                if (emptyViewMarginTop == 0){
                    eHolder.emptyLl.setPadding(0, SystemUtil.dp2px(getContext(), 125), 0, 0);
                }else {
                    eHolder.emptyLl.setPadding(0, SystemUtil.dp2px(getContext(), emptyViewMarginTop), 0, 0);
                }
            }

            if (imgResourceId != 0) {
                eHolder.emptyImg.setImageResource(imgResourceId);
            }

            if (!TextUtils.isEmpty(promptStr)) {
                eHolder.promptTv.setText(promptStr);
            }

        } else if (baseTopHolder == null) {
            bindViewData(holder, position);

        } else {
            bindViewData(holder, position - 1);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (objectList == null || objectList.size() == 0) {
            if (baseTopHolder == null) {
                return TYPE_EMPTY;
            } else if (position == 0) {
                return TYPE_TOP;
            } else if (position == 1) {
                return TYPE_EMPTY;
            }
        } else if (baseTopHolder != null && position == 0) {
            return TYPE_TOP;
        } else {
            return super.getItemViewType(position);
        }
        return -3;
    }

    @Override
    public int getItemCount() {
        if (!isDataListGet && objectList == null) {
            objectList = getObjectList();
            isDataListGet = true;
        }
        if (!isTopHolderGet && baseTopHolder == null) {
            baseTopHolder = getTopViewHolder();
            isTopHolderGet = true;
        }
        if (objectList == null || objectList.size() == 0) {
            if (baseTopHolder == null) {
                return 1;
            } else {
                return 2;
            }
        } else if (baseTopHolder != null) {
            return objectList.size() + 1;
        } else {
            return objectList.size();
        }
    }

    private static class EmptyViewHolder extends RecyclerView.ViewHolder {
        ImageView emptyImg;
        TextView promptTv;
        LinearLayout emptyLl;

        private EmptyViewHolder(View itemView) {
            super(itemView);
            emptyImg = itemView.findViewById(R.id.iv_empty);
            promptTv = itemView.findViewById(R.id.tv_empty_prompt);
            emptyLl = itemView.findViewById(R.id.ll_empty_container);
        }

    }


}
