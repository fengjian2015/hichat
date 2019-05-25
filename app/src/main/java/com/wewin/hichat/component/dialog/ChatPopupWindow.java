package com.wewin.hichat.component.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.model.db.dao.MessageDao;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.FileInfo;
import com.wewin.hichat.view.conversation.SelectSendActivity;

/**
 * @author:jason date:2019/5/2318:24
 */
public class ChatPopupWindow extends PopupWindow {
    private Context mContext;
    private View contentView;
    private int mRawX, mRawY;
    private ChatMsg mChatMsg;
    private int mPosition;

    private FrameLayout flCopy;
    private FrameLayout flForward;
    private FrameLayout flReply;
    private FrameLayout flDelete;

    public ChatPopupWindow(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        contentView = getPopupWindowContentView();
        setContentView(contentView);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
    }

    public void showPopupWindow(ChatMsg chatMsg, View anchorView,int position) {
        mPosition=position;
        mChatMsg = chatMsg;
        // 设置好参数之后再show
        setViewType();
        int[] windowPos = calculatePopWindowPos(anchorView, contentView);
        showAtLocation(anchorView, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }

    private void setViewType() {
        if (mChatMsg == null) {
            return;
        }
        switch (mChatMsg.getContentType()) {
            case ChatMsg.TYPE_CONTENT_TEXT:
            case ChatMsg.TYPE_CONTENT_AT:
                //文本
                flCopy.setVisibility(View.VISIBLE);
                flForward.setVisibility(View.VISIBLE);
                flReply.setVisibility(View.VISIBLE);
                flDelete.setVisibility(View.VISIBLE);
                break;
            case ChatMsg.TYPE_CONTENT_VOICE_CALL:
                //语音通话
                flCopy.setVisibility(View.GONE);
                flForward.setVisibility(View.GONE);
                flReply.setVisibility(View.GONE);
                flDelete.setVisibility(View.VISIBLE);
                break;
            case ChatMsg.TYPE_CONTENT_FILE:
                //文件
                if(mChatMsg.getFileInfo().getFileType()==FileInfo.TYPE_TAPE_RECORD){
                    flCopy.setVisibility(View.GONE);
                    flForward.setVisibility(View.GONE);
                    flReply.setVisibility(View.GONE);
                    flDelete.setVisibility(View.VISIBLE);
                }else {
                    flCopy.setVisibility(View.GONE);
                    flForward.setVisibility(View.VISIBLE);
                    flReply.setVisibility(View.VISIBLE);
                    flDelete.setVisibility(View.VISIBLE);
                }
                if(mChatMsg.getSendState()==ChatMsg.TYPE_SEND_FAIL
                        ||mChatMsg.getSendState()==ChatMsg.TYPE_SENDING){
                    //发送中和发送失败的不允许转发和回复
                    flForward.setVisibility(View.GONE);
                    flReply.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置控件触摸点
     *
     * @param rawX 锚点距离屏幕左边的距离
     * @param rawY 锚点距离屏幕上方的距离
     */
    public void setXY(int rawX, int rawY) {
        mRawX = rawX;
        mRawY = rawY;
    }

    private View getPopupWindowContentView() {
        // 一个自定义的布局，作为显示的内容
        int layoutId = R.layout.pop_item_chat;
        View contentView = LayoutInflater.from(mContext).inflate(layoutId, null);
        flCopy = contentView.findViewById(R.id.fl_pop_chat_copy);
        flForward = contentView.findViewById(R.id.fl_pop_chat_forward);
        flReply = contentView.findViewById(R.id.fl_pop_chat_reply);
        flDelete = contentView.findViewById(R.id.fl_pop_chat_delete);
        flCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyText(mContext,mChatMsg.getContent());
                ToastUtil.showShort(mContext,mContext.getString(R.string.copy_success));
                dismiss();
            }
        });

        flForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SelectSendActivity.class);
                intent.putExtra(SelectSendActivity.DATA,mChatMsg);
                mContext.startActivity(intent);
                dismiss();
            }
        });
        flReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        flDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDao.deleteSingle(mChatMsg.getMsgId());
                EventTrans.post(EventMsg.CONVERSATION_DELETE_MSG,mChatMsg,mPosition);
                dismiss();
            }
        });
        return contentView;
    }


    /**
     * 计算popwindow在长按view 的什么位置显示
     *
     * @param anchorView 长按锚点的view
     * @param popView    弹出框
     * @return popwindow在长按view中的xy轴的偏移量
     */
    private int[] calculatePopWindowPos(final View anchorView, final View popView) {
        final int[] windowPos = new int[2];
        final int[] anchorLoc = new int[2];
//        // 获取触点在屏幕上相对左上角坐标位置
//        anchorLoc[0] = mRawX;
//        anchorLoc[1] = mRawY;
        anchorView.getLocationOnScreen(anchorLoc);
        //当前item的高度
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        // 测量popView 弹出框
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算弹出框的高宽
        final int popHeight = popView.getMeasuredHeight();
        final int popWidth = popView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        // 屏幕高度-触点距离左上角的高度 < popwindow的高度
        // 如果小于弹出框的高度那么说明下方空间不够显示 popwindow，需要放在触点的上方显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] < popHeight);
        // 判断需要向右边弹出还是向左边弹出显示
        //判断触点右边的剩余空间是否够显示popwindow 大于就说明够显示
        final boolean isNeedShowRight = (screenWidth - anchorLoc[0] > popWidth);
        if (isNeedShowUp) {
            //如果在上方显示 则用 触点的距离上方的距离 - 弹框的高度
            windowPos[1] = anchorLoc[1] - popHeight;
        } else {
            //如果在下方显示 则用 触点的距离上方的距离
            windowPos[1] = anchorLoc[1];
        }
        if (isNeedShowRight) {
            windowPos[0] = anchorLoc[0];
        } else {
            //显示在左边的话 那么弹出框的位置在触点左边出现，则是触点距离左边距离 - 弹出框的宽度
            windowPos[0] = anchorLoc[0] - popWidth;
        }
        return windowPos;
    }

    /**
     * 获取屏幕高度(px)
     */
    private int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    private int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    private void copyText(Context context,String content){
        if (content==null){
            return;
        }
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        if (cm!=null) {
            cm.setPrimaryClip(mClipData);
        }
    }
}
