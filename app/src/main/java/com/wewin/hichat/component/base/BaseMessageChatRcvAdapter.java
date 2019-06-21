package com.wewin.hichat.component.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.NameUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.ChatPopupWindow;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FileInfo;
import com.wewin.hichat.model.db.entity.FriendInfo;

import java.util.List;

/**
 * Created by Darren on 2018/12/17.
 */
public abstract class BaseMessageChatRcvAdapter extends BaseRcvTopLoadAdapter {

    private Context context;
    private List<ChatMsg> msgList;
    private OnMsgClickListener msgClickListener;
    private OmMsgLongClickListener mOmMsgLongClickListener;
    private String friendAvatarUrl;
    private ChatPopupWindow chatPopupWindow;

    //右侧，己方
    private final int TYPE_RIGHT_VERSION_NOT = 10;//版本不支持
    private final int TYPE_RIGHT_TEXT_AT = 11;//文本+@
    private final int TYPE_RIGHT_VOICE_CALL = 12;//语音通话
    private final int TYPE_RIGHT_FILE_IMG = 13;//图片
    private final int TYPE_RIGHT_FILE_VIDEO = 14;//视频
    private final int TYPE_RIGHT_FILE_DOC = 15;//文档+音乐
    private final int TYPE_RIGHT_FILE_TAPE = 16;//录音
    private final int TYPE_RIGHT_REPLY = 17;//回复
    //左侧，对方
    private final int TYPE_LEFT_VERSION_NOT = 30;//版本不支持
    private final int TYPE_LEFT_TEXT_AT = 31;//文本+@
    private final int TYPE_LEFT_VOICE_CALL = 32;//语音通话
    private final int TYPE_LEFT_FILE_IMG = 33;//图片
    private final int TYPE_LEFT_FILE_VIDEO = 34;//视频
    private final int TYPE_LEFT_FILE_DOC = 35;//文档+音乐
    private final int TYPE_LEFT_FILE_TAPE = 36;//录音
    private final int TYPE_LEFT_REPLY = 37;//回复


    public BaseMessageChatRcvAdapter(Context context, List<ChatMsg> msgList) {
        this.context = context;
        this.msgList = msgList;
        chatPopupWindow = new ChatPopupWindow(context);
    }

    //右侧自己 文字+@
    protected abstract void setRightTextHolder(RightTextHolder rightTextHolder, int position);

    //右侧自己 语音通话
    protected abstract void setRightCallHolder(RightCallHolder rightCallHolder, int position);

    //右侧自己 图片
    protected abstract void setRightImgHolder(RightImgHolder rightImgHolder, int position);

    //右侧自己 视频
    protected abstract void setRightVideoHolder(RightVideoHolder rightVideoHolder, int position);

    //右侧自己 文档+音乐
    protected abstract void setRightDocHolder(RightDocHolder rightDocHolder, int position);

    //右侧自己 录音
    protected abstract void setRightReplyHolder(RightReplyHolder rightReplyHolder, int position);

    //右侧自己 回复
    protected abstract void setRightTapeHolder(RightTapeHolder rightTapeHolder, int position);

    //左侧对方 文字+@
    protected abstract void setLeftTextHolder(LeftTextHolder leftTextHolder, int position);

    //左侧对方 语音通话
    protected abstract void setLeftCallHolder(LeftCallHolder leftCallHolder, int position);

    //左侧对方 图片
    protected abstract void setLeftImgHolder(LeftImgHolder leftImgHolder, int position);

    //左侧对方 视频
    protected abstract void setLeftVideoHolder(LeftVideoHolder leftVideoHolder, int position);

    //左侧对方 文档+音乐
    protected abstract void setLeftDocHolder(LeftDocHolder leftDocHolder, int position);

    //左侧对方 录音
    protected abstract void setLeftTapeHolder(LeftTapeHolder leftTapeHolder, int position);

    //左侧对方 回复
    protected abstract void setLeftReplyHolder(LeftReplyHolder leftReplyHolder, int position);

    public interface OnMsgClickListener {
        void docClick(int position);//点击文档

        void videoClick(int position);//点击视频

        void imgClick(int position);//点击图片

        void tapeRecordClick(int position);//点击录音

        void voiceCallClick();//点击语音通话

        void replyClick(int position);//点击回复

        void avatarLeftClick(int position);//点击左侧头像
    }

    public void setOnMsgClickListener(OnMsgClickListener msgClickListener) {
        this.msgClickListener = msgClickListener;
    }

    public interface OmMsgLongClickListener {
        /**
         * 文本长按事件
         *
         * @param position
         * @param view
         */
        void textLongClick(int position, View view);

        /**
         * 语音通话长按事件
         *
         * @param position
         * @param view
         */
        void voiceCallLongClick(int position, View view);

        /**
         * 图片长按事件
         *
         * @param position
         * @param view
         */
        void imgLongClick(int position, View view);

        /**
         * 视频长按事件
         *
         * @param position
         * @param view
         */
        void videoLongClick(int position, View view);

        /**
         * 文档长按事件
         *
         * @param position
         * @param view
         */
        void docLongClick(int position, View view);

        /**
         * 录音长按事件
         *
         * @param position
         * @param view
         */
        void tapeRecordLongClick(int position, View view);

        /**
         * 回复长按事件
         *
         * @param position
         * @param view
         */
        void avatarLeftLongClick(int position, View view);

        /**
         * 左侧头像长按事件
         *
         * @param position
         * @param view
         */
        void replyLongClick(int position, View view);

    }

    public void setOmMsgLongClickListener(OmMsgLongClickListener omMsgLongClickListener) {
        this.mOmMsgLongClickListener = omMsgLongClickListener;
    }

    @Override
    protected Context getContext() {
        return context;
    }

    @Override
    protected List getObjectList() {
        return msgList;
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(int viewType) {
        switch (viewType) {
            //右边
            case TYPE_RIGHT_TEXT_AT:
            case TYPE_RIGHT_VERSION_NOT:
                return new RightTextHolder(View.inflate(context, R.layout.layout_item_message_text_right, null));

            case TYPE_RIGHT_VOICE_CALL:
                return new RightCallHolder(View.inflate(context, R.layout.layout_item_message_call_right, null));

            case TYPE_RIGHT_FILE_IMG:
                return new RightImgHolder(View.inflate(context, R.layout.layout_item_message_img_right, null));

            case TYPE_RIGHT_FILE_VIDEO:
                return new RightVideoHolder(View.inflate(context, R.layout.layout_item_message_video_right, null));

            case TYPE_RIGHT_FILE_DOC:
                return new RightDocHolder(View.inflate(context, R.layout.layout_item_message_doc_right, null));

            case TYPE_RIGHT_FILE_TAPE:
                return new RightTapeHolder(View.inflate(context, R.layout.layout_item_message_tape_right, null));
            case TYPE_RIGHT_REPLY:
                return new RightReplyHolder(View.inflate(context, R.layout.layout_item_message_reply_right, null));

            //左边
            case TYPE_LEFT_TEXT_AT:
            case TYPE_LEFT_VERSION_NOT:
                return new LeftTextHolder(View.inflate(context, R.layout.layout_item_message_text_left, null));

            case TYPE_LEFT_VOICE_CALL:
                return new LeftCallHolder(View.inflate(context, R.layout.layout_item_message_call_left, null));

            case TYPE_LEFT_FILE_IMG:
                return new LeftImgHolder(View.inflate(context, R.layout.layout_item_message_img_left, null));

            case TYPE_LEFT_FILE_VIDEO:
                return new LeftVideoHolder(View.inflate(context, R.layout.layout_item_message_video_left, null));

            case TYPE_LEFT_FILE_DOC:
                return new LeftDocHolder(View.inflate(context, R.layout.layout_item_message_doc_left, null));

            case TYPE_LEFT_FILE_TAPE:
                return new LeftTapeHolder(View.inflate(context, R.layout.layout_item_message_tape_left, null));

            case TYPE_LEFT_REPLY:
                return new LeftReplyHolder(View.inflate(context, R.layout.layout_item_message_reply_left, null));

            default:
                return new LeftTextHolder(View.inflate(context, R.layout.layout_item_message_tape_left, null));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!getTopPullViewVisible()) {
            return getViewType(position);
        } else if (getTopPullViewVisible() && position != 0) {
            return getViewType(position - 1);
        }
        return super.getItemViewType(position);
    }

    @Override
    protected void bindViewData(final RecyclerView.ViewHolder holder, final int position) {
//        long startTime = System.currentTimeMillis();
        if (holder instanceof RightViewHolder) {
            //右侧
            if (msgList.get(position) == null) {
                return;
            }
            RightViewHolder rightHolder = (RightViewHolder) holder;
            //右侧 己方 消息发送时间
            if (rightHolder.timeTv != null) {
                rightHolder.timeTv.setText(TimeUtil.timestampToStr(msgList
                        .get(position).getCreateTimestamp(), "HH:mm"));
            }
            //右侧 日期
            long createTimestamp = msgList.get(position).getCreateTimestamp();
            if (position == 0) {
                if (!getTopPullViewVisible()) {
                    rightHolder.dateTv.setVisibility(View.VISIBLE);
                    String dateStr = TimeUtil.getFormatDate(msgList.get(position).getCreateTimestamp());
                    rightHolder.dateTv.setText(dateStr);
                } else {
                    rightHolder.dateTv.setVisibility(View.GONE);
                }
            } else {
                long lastTimestamp = msgList.get(position - 1).getCreateTimestamp();
                if (TimeUtil.isSameDay(createTimestamp, lastTimestamp)) {
                    rightHolder.dateTv.setVisibility(View.GONE);
                } else {
                    rightHolder.dateTv.setVisibility(View.VISIBLE);
                    String dateStr = TimeUtil.getFormatDate(msgList.get(position).getCreateTimestamp());
                    rightHolder.dateTv.setText(dateStr);
                }
            }

            //右侧 消息发送状态
            if (msgList.get(position).getSendState() == ChatMsg.TYPE_SEND_FAIL) {
                rightHolder.failTopIv.setVisibility(View.INVISIBLE);
                rightHolder.failCenterIv.setVisibility(View.VISIBLE);
                rightHolder.sendingIv.setVisibility(View.INVISIBLE);

            } else if (msgList.get(position).getSendState() == ChatMsg.TYPE_SENDING) {
                rightHolder.failTopIv.setVisibility(View.INVISIBLE);
                rightHolder.failCenterIv.setVisibility(View.INVISIBLE);
                rightHolder.sendingIv.setVisibility(View.VISIBLE);
                rightHolder.sendingIv.startAnimation(AnimationUtils
                        .loadAnimation(context, R.anim.rotate360_anim));

            } else if (msgList.get(position).getSendState() == ChatMsg.TYPE_SEND_SUCCESS) {
                rightHolder.failTopIv.setVisibility(View.INVISIBLE);
                rightHolder.failCenterIv.setVisibility(View.INVISIBLE);
                rightHolder.sendingIv.setVisibility(View.INVISIBLE);
            }

            if (holder instanceof RightTextHolder) {
                //右侧 己方 text + @
                final RightTextHolder textHolder = (RightTextHolder) holder;
                setRightTextHolder(textHolder, position);

                //右侧 发送失败
                if (msgList.get(position).getSendState() == ChatMsg.TYPE_SEND_FAIL) {
                    textHolder.failTopIv.setVisibility(View.VISIBLE);
                    textHolder.failCenterIv.setVisibility(View.INVISIBLE);
                }
                textHolder.contentTv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.textLongClick(position, textHolder.contentTv);
                        }
                        return true;
                    }
                });
            } else if (holder instanceof RightCallHolder) {
                //右侧 语音通话
                if (msgList.get(position).getVoiceCall() == null) {
                    return;
                }
                setRightCallHolder((RightCallHolder) holder, position);
                ((RightCallHolder) holder).callLl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.voiceCallLongClick(position, ((RightCallHolder) holder).callLl);
                        }
                        return true;
                    }
                });
                ((RightCallHolder) holder).callLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.voiceCallClick();
                        }
                    }
                });

            } else if (holder instanceof RightImgHolder) {
                //右侧 图片
                if (msgList.get(position).getFileInfo() == null) {
                    return;
                }
                setRightImgHolder((RightImgHolder) holder, position);
                ((RightImgHolder) holder).imgIv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.imgLongClick(position, ((RightImgHolder) holder).imgIv);
                        }
                        return true;
                    }
                });
                ((RightImgHolder) holder).imgIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.imgClick(position);
                        }
                    }
                });

            } else if (holder instanceof RightVideoHolder) {
                //右侧 视频
                if (msgList.get(position).getFileInfo() == null) {
                    return;
                }
                setRightVideoHolder((RightVideoHolder) holder, position);
                ((RightVideoHolder) holder).videoIv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.videoLongClick(position, ((RightVideoHolder) holder).videoIv);
                        }
                        return true;
                    }
                });
                ((RightVideoHolder) holder).videoIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.videoClick(position);
                        }
                    }
                });

            } else if (holder instanceof RightDocHolder) {
                //右侧 文档+音乐
                if (msgList.get(position).getFileInfo() == null) {
                    return;
                }
                setRightDocHolder((RightDocHolder) holder, position);
                ((RightDocHolder) holder).docRl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.docLongClick(position, ((RightDocHolder) holder).docRl);
                        }
                        return true;
                    }
                });
                ((RightDocHolder) holder).docRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.docClick(position);
                        }
                    }
                });

            } else if (holder instanceof RightTapeHolder) {
                //右侧 录音
                if (msgList.get(position).getFileInfo() == null) {
                    return;
                }
                final RightTapeHolder tapeHolder = (RightTapeHolder) holder;
                setRightTapeHolder(tapeHolder, position);
                tapeHolder.tapeLl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.tapeRecordLongClick(position, tapeHolder.tapeLl);
                        }
                        return true;
                    }
                });
                tapeHolder.tapeLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.tapeRecordClick(position);
                        }
                    }
                });
                if (msgList.get(position).getSendState() == ChatMsg.TYPE_SEND_FAIL) {
                    tapeHolder.failTopIv.setVisibility(View.VISIBLE);
                    tapeHolder.failCenterIv.setVisibility(View.INVISIBLE);
                }
            } else if (holder instanceof RightReplyHolder) {
                if (msgList.get(position).getReplyMsgInfo()==null){
                    return;
                }
                //右侧回复消息
                final RightReplyHolder replyHolder = (RightReplyHolder) holder;
                setRightReplyHolder(replyHolder, position);
                replyHolder.replyRl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.replyLongClick(position, replyHolder.replyRl);
                        }
                        return true;
                    }
                });
                replyHolder.replyContentTv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.replyLongClick(position, replyHolder.replyRl);
                        }
                        return true;
                    }
                });
                replyHolder.replyTypeTv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.replyLongClick(position, replyHolder.replyRl);
                        }
                        return true;
                    }
                });
                replyHolder.replyRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.replyClick(position);
                        }
                    }
                });
            }

        } else if (holder instanceof LeftViewHolder) {
            //左侧
            if (msgList.get(position) == null) {
                return;
            }
            final LeftViewHolder leftHolder = (LeftViewHolder) holder;
            leftHolder.avatarIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (msgClickListener != null) {
                        msgClickListener.avatarLeftClick(position);
                    }
                }
            });
            leftHolder.avatarIv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOmMsgLongClickListener!=null){
                        mOmMsgLongClickListener.avatarLeftLongClick(position,leftHolder.avatarIv);
                    }
                    return true;
                }
            });
            //左侧 日期
            long currentTimestamp = msgList.get(position).getCreateTimestamp();
            if (position == 0) {
                if (!getTopPullViewVisible()) {
                    leftHolder.dateTv.setVisibility(View.VISIBLE);
                    String dateStr = TimeUtil.getFormatDate(msgList.get(position).getCreateTimestamp());
                    leftHolder.dateTv.setText(dateStr);
                } else {
                    leftHolder.dateTv.setVisibility(View.GONE);
                }
            } else {
                long lastTimestamp = msgList.get(position - 1).getCreateTimestamp();
                if (TimeUtil.isSameDay(currentTimestamp, lastTimestamp)) {
                    leftHolder.dateTv.setVisibility(View.GONE);
                } else {
                    leftHolder.dateTv.setVisibility(View.VISIBLE);
                    String dateStr = TimeUtil.getFormatDate(msgList.get(position).getCreateTimestamp());
                    leftHolder.dateTv.setText(dateStr);
                }
            }
            //左侧名字
            if (ChatRoom.TYPE_GROUP.equals(msgList.get(position).getRoomType())) {
                leftHolder.nameTv.setVisibility(View.VISIBLE);
                leftHolder.nameTv.setText(NameUtil.getName(msgList.get(position).getSenderId()));
            } else {
                leftHolder.nameTv.setVisibility(View.GONE);
            }
            //左侧 头像
            if (TextUtils.isEmpty(friendAvatarUrl)
                    && ChatRoom.TYPE_SINGLE.equals(msgList.get(position).getRoomType())) {
                FriendInfo contactInfo = ContactUserDao.getContactUser(msgList.get(position).getRoomId());
                if (contactInfo != null) {
                    friendAvatarUrl = contactInfo.getAvatar();
                }

            } else if (ChatRoom.TYPE_GROUP.equals(msgList.get(position).getRoomType())) {
                FriendInfo contactInfo = ContactUserDao.getContactUser(msgList.get(position).getSenderId());
                if (contactInfo != null) {
                    friendAvatarUrl = contactInfo.getAvatar();
                }
            }
            ImgUtil.loadCircle(context, friendAvatarUrl, leftHolder.avatarIv);

            //左侧 消息发送时间
            if (leftHolder.timeTv != null) {
                leftHolder.timeTv.setText(TimeUtil.timestampToStr(msgList.get(position)
                        .getCreateTimestamp(), "HH:mm"));
            }

            if (holder instanceof LeftTextHolder) {
                //左侧 文字+@
                setLeftTextHolder((LeftTextHolder) holder, position);
                ((LeftTextHolder) holder).contentTv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.textLongClick(position, ((LeftTextHolder) holder).contentTv);
                        }
                        return true;
                    }
                });

            } else if (holder instanceof LeftCallHolder) {
                //左侧 语音通话
                if (msgList.get(position).getVoiceCall() == null) {
                    return;
                }
                setLeftCallHolder((LeftCallHolder) holder, position);
                ((LeftCallHolder) holder).callLl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.voiceCallLongClick(position, ((LeftCallHolder) holder).callLl);
                        }
                        return true;
                    }
                });
                ((LeftCallHolder) holder).callLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.voiceCallClick();
                        }
                    }
                });

            } else if (holder instanceof LeftImgHolder) {
                //左侧 图片
                if (msgList.get(position).getFileInfo() == null) {
                    return;
                }
                setLeftImgHolder((LeftImgHolder) holder, position);
                ((LeftImgHolder) holder).imgIv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.imgLongClick(position, ((LeftImgHolder) holder).imgIv);
                        }
                        return true;
                    }
                });
                ((LeftImgHolder) holder).imgIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.imgClick(position);
                        }
                    }
                });

            } else if (holder instanceof LeftVideoHolder) {
                //左侧 视频
                if (msgList.get(position).getFileInfo() == null) {
                    return;
                }
                setLeftVideoHolder((LeftVideoHolder) holder, position);
                ((LeftVideoHolder) holder).videoIv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.videoLongClick(position, ((LeftVideoHolder) holder).videoIv);
                        }
                        return true;
                    }
                });
                ((LeftVideoHolder) holder).videoIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.videoClick(position);
                        }
                    }
                });

            } else if (holder instanceof LeftDocHolder) {
                //左侧 文档+音乐
                if (msgList.get(position).getFileInfo() == null) {
                    return;
                }
                final LeftDocHolder docHolder = (LeftDocHolder) holder;
                setLeftDocHolder(docHolder, position);
                docHolder.docRl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.docLongClick(position, docHolder.docRl);
                        }
                        return true;
                    }
                });
                docHolder.docRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.docClick(position);
                        }
                    }
                });
                //左侧 文档下载状态
                switch (msgList.get(position).getFileInfo().getDownloadState()) {
                    case FileInfo.TYPE_DOWNLOAD_NOT:
                        docHolder.downloadStateIv.setVisibility(View.VISIBLE);
                        docHolder.downloadStateIv.setImageResource(R.drawable.file_download_black);
                        docHolder.loadingIv.setVisibility(View.GONE);
                        docHolder.loadingIv.clearAnimation();
                        break;

                    case FileInfo.TYPE_DOWNLOADING:
                        docHolder.downloadStateIv.setVisibility(View.VISIBLE);
                        docHolder.downloadStateIv.setImageResource(R.drawable.file_download_off_black);
                        docHolder.loadingIv.setVisibility(View.VISIBLE);
                        docHolder.loadingIv.startAnimation(AnimationUtils.loadAnimation(context,
                                R.anim.rotate360_anim));
                        break;

                    case FileInfo.TYPE_DOWNLOAD_SUCCESS:
                        docHolder.downloadStateIv.setVisibility(View.GONE);
                        docHolder.loadingIv.setVisibility(View.GONE);
                        docHolder.loadingIv.clearAnimation();
                        break;

                    case FileInfo.TYPE_DOWNLOAD_FAIL:
                        docHolder.downloadStateIv.setVisibility(View.VISIBLE);
                        docHolder.downloadStateIv.setImageResource(R.drawable.file_download_again_black);
                        docHolder.loadingIv.setVisibility(View.GONE);
                        docHolder.loadingIv.clearAnimation();
                        break;

                    default:

                        break;
                }

            } else if (holder instanceof LeftTapeHolder) {
                //左侧 录音
                if (msgList.get(position).getFileInfo() == null) {
                    return;
                }
                final LeftTapeHolder tapeHolder = (LeftTapeHolder) holder;
                setLeftTapeHolder(tapeHolder, position);
                tapeHolder.tapeLl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.tapeRecordLongClick(position, tapeHolder.tapeLl);
                        }
                        return true;
                    }
                });
                tapeHolder.tapeLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.tapeRecordClick(position);
                        }
                    }
                });
                //左侧 录音下载状态
                switch (msgList.get(position).getFileInfo().getDownloadState()) {
                    case FileInfo.TYPE_DOWNLOADING:
                        tapeHolder.loadingIv.setVisibility(View.VISIBLE);
                        tapeHolder.loadingIv.setAnimation(AnimationUtils.loadAnimation(context,
                                R.anim.rotate360_anim));
                        break;

                    case FileInfo.TYPE_DOWNLOAD_FAIL:
                    case FileInfo.TYPE_DOWNLOAD_SUCCESS:
                        tapeHolder.loadingIv.setVisibility(View.GONE);
                        tapeHolder.loadingIv.clearAnimation();
                        break;

                    default:

                        break;
                }
                //左侧 录音未读状态
                if (msgList.get(position).getFileInfo().getTapeUnreadMark() == 1) {
                    tapeHolder.redPointIv.setVisibility(View.VISIBLE);
                } else {
                    tapeHolder.redPointIv.setVisibility(View.GONE);
                }
            }else if(holder instanceof LeftReplyHolder){
                //左侧 回复
                if (msgList.get(position).getReplyMsgInfo() == null) {
                    return;
                }

                final LeftReplyHolder replyHolder = (LeftReplyHolder) holder;
                setLeftReplyHolder(replyHolder, position);
                replyHolder.replyRl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.replyLongClick(position, replyHolder.replyRl);
                        }
                        return true;
                    }
                });
                replyHolder.replyRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.replyClick(position);
                        }
                    }
                });
                replyHolder.replyContentTv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.replyLongClick(position, replyHolder.replyRl);
                        }
                        return true;
                    }
                });
                replyHolder.replyTypeTv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.replyLongClick(position, replyHolder.replyRl);
                        }
                        return true;
                    }
                });
            }
        }
//        long endTime = System.currentTimeMillis();
//        LogUtil.i("bindViewData diff", endTime - startTime);
    }

    private int getViewType(int position) {
        //消息是否自己发送
        boolean selfState = false;
        if (msgList.get(position).getContentType() != ChatMsg.TYPE_CONTENT_VOICE_CALL
                && SpCons.getUser(context).getId().equals(msgList.get(position).getSenderId())) {
            selfState = true;

        } else if (msgList.get(position).getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL
                && msgList.get(position).getVoiceCall() != null
                && msgList.get(position).getVoiceCall().getInviteUserId().equals(SpCons.getUser(context).getId())) {
            selfState = true;
        }
        switch (msgList.get(position).getContentType()) {
            case ChatMsg.TYPE_CONTENT_TEXT:
            case ChatMsg.TYPE_CONTENT_AT:
                if (selfState) {
                    return TYPE_RIGHT_TEXT_AT;
                } else {
                    return TYPE_LEFT_TEXT_AT;
                }

            case ChatMsg.TYPE_CONTENT_VOICE_CALL:
                if (selfState) {
                    return TYPE_RIGHT_VOICE_CALL;
                } else {
                    return TYPE_LEFT_VOICE_CALL;
                }

            case ChatMsg.TYPE_CONTENT_FILE:
                if (msgList.get(position) != null
                        && msgList.get(position).getFileInfo() != null) {
                    switch (msgList.get(position).getFileInfo().getFileType()) {
                        case FileInfo.TYPE_IMG:
                            if (selfState) {
                                return TYPE_RIGHT_FILE_IMG;
                            } else {
                                return TYPE_LEFT_FILE_IMG;
                            }

                        case FileInfo.TYPE_VIDEO:
                            if (selfState) {
                                return TYPE_RIGHT_FILE_VIDEO;
                            } else {
                                return TYPE_LEFT_FILE_VIDEO;
                            }

                        case FileInfo.TYPE_DOC:
                        case FileInfo.TYPE_MUSIC:
                            if (selfState) {
                                return TYPE_RIGHT_FILE_DOC;
                            } else {
                                return TYPE_LEFT_FILE_DOC;
                            }

                        case FileInfo.TYPE_TAPE_RECORD:
                            if (selfState) {
                                return TYPE_RIGHT_FILE_TAPE;
                            } else {
                                return TYPE_LEFT_FILE_TAPE;
                            }

                        default:
                            if (selfState) {
                                return TYPE_RIGHT_VERSION_NOT;
                            } else {
                                return TYPE_LEFT_VERSION_NOT;
                            }
                    }
                }
            case ChatMsg.TYPE_CONTENT_REPLY:
            case ChatMsg.TYPE_CONTENT_REPLY_AT:
                if (selfState) {
                    return TYPE_RIGHT_REPLY;
                } else {
                    return TYPE_LEFT_REPLY;
                }
            default:
                if (selfState) {
                    return TYPE_RIGHT_VERSION_NOT;
                } else {
                    return TYPE_LEFT_VERSION_NOT;
                }
        }
    }


    protected static class RightTextHolder extends RightViewHolder {
        public TextView contentTv;

        private RightTextHolder(View itemView) {
            super(itemView);
            contentTv = itemView.findViewById(R.id.tv_message_chat_right_content);
        }
    }

    protected static class RightCallHolder extends RightViewHolder {
        public TextView callStateTv;
        private LinearLayout callLl;

        private RightCallHolder(View itemView) {
            super(itemView);
            callStateTv = itemView.findViewById(R.id.tv_message_chat_voice_call_state_right);
            callLl = itemView.findViewById(R.id.ll_message_chat_voice_call_right);
        }
    }

    protected static class RightDocHolder extends RightViewHolder {
        public ImageView iconIv;
        public TextView nameTv, typeTv, sizeTv;
        private RelativeLayout docRl;

        private RightDocHolder(View itemView) {
            super(itemView);
            iconIv = itemView.findViewById(R.id.iv_message_chat_file_icon_right);
            nameTv = itemView.findViewById(R.id.tv_message_chat_file_name_right);
            typeTv = itemView.findViewById(R.id.tv_message_chat_file_type_right);
            sizeTv = itemView.findViewById(R.id.tv_message_chat_file_size_right);
            docRl = itemView.findViewById(R.id.rl_message_chat_file_container_right);
        }
    }

    protected static class RightImgHolder extends RightViewHolder {
        public ImageView imgIv;

        private RightImgHolder(View itemView) {
            super(itemView);
            imgIv = itemView.findViewById(R.id.iv_message_chat_right_img);
        }
    }

    protected static class RightTapeHolder extends RightViewHolder {
        public ImageView animIv, stateIv;
        public TextView durationTv;
        private LinearLayout tapeLl;

        private RightTapeHolder(View itemView) {
            super(itemView);
            animIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_anim_right);
            stateIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_play_right);
            durationTv = itemView.findViewById(R.id.tv_message_chat_voice_record_time_right);
            tapeLl = itemView.findViewById(R.id.ll_message_chat_voice_record_container_right);
        }
    }

    protected static class RightVideoHolder extends RightViewHolder {
        public ImageView videoIv;

        private RightVideoHolder(View itemView) {
            super(itemView);
            videoIv = itemView.findViewById(R.id.iv_message_chat_video_img_right);
        }
    }

    protected static class RightReplyHolder extends RightViewHolder {
        public ImageView replyIv, animIv, recordLoadingIv, recordPlayIv,replyVideoIv,replyStateIv
                ,loadingIv;
        public TextView replyNameTv, recordTimeTv, replyTypeTv, replyContentTv;
        public LinearLayout recordLl;
        public RelativeLayout replyRl;

        private RightReplyHolder(View itemView) {
            super(itemView);
            replyIv = itemView.findViewById(R.id.iv_message_chat_reply_img);
            replyNameTv = itemView.findViewById(R.id.tv_message_chat_reply_name);
            recordLl = itemView.findViewById(R.id.ll_message_chat_voice_record_container);
            animIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_anim);
            recordTimeTv = itemView.findViewById(R.id.tv_message_chat_voice_record_time);
            recordLoadingIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_loading);
            recordPlayIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_play);
            replyTypeTv = itemView.findViewById(R.id.tv_message_chat_reply_type);
            replyContentTv = itemView.findViewById(R.id.tv_message_chat_reply_content);
            replyRl=itemView.findViewById(R.id.ll_message_chat_reply_container);
            replyVideoIv=itemView.findViewById(R.id.iv_message_chat_reply_video);
            replyStateIv=itemView.findViewById(R.id.iv_chat_file_download_state);
            loadingIv = itemView.findViewById(R.id.iv_chat_text_msg_loading_left);
        }
    }

    protected static class LeftTextHolder extends LeftViewHolder {
        public TextView contentTv;

        private LeftTextHolder(View itemView) {
            super(itemView);
            contentTv = itemView.findViewById(R.id.tv_conversation_chat_left_content);
        }
    }

    protected static class LeftCallHolder extends LeftViewHolder {
        public TextView callStateTv;
        private LinearLayout callLl;

        private LeftCallHolder(View itemView) {
            super(itemView);
            callStateTv = itemView.findViewById(R.id.tv_message_chat_voice_call_state_left);
            callLl = itemView.findViewById(R.id.ll_message_chat_call_state_container_left);
        }
    }

    protected static class LeftDocHolder extends LeftViewHolder {
        ImageView downloadStateIv;
        public ImageView iconIv;
        public TextView nameTv, typeTv, sizeTv;
        private RelativeLayout docRl;

        private LeftDocHolder(View itemView) {
            super(itemView);
            iconIv = itemView.findViewById(R.id.iv_message_chat_file_icon_left);
            downloadStateIv = itemView.findViewById(R.id.iv_chat_file_download_state);
            nameTv = itemView.findViewById(R.id.tv_message_chat_file_name_left);
            typeTv = itemView.findViewById(R.id.tv_message_chat_file_type_left);
            sizeTv = itemView.findViewById(R.id.tv_message_chat_file_size_left);
            docRl = itemView.findViewById(R.id.rl_message_chat_file_container_left);
        }
    }

    protected static class LeftImgHolder extends LeftViewHolder {
        public ImageView imgIv;

        private LeftImgHolder(View itemView) {
            super(itemView);
            imgIv = itemView.findViewById(R.id.iv_message_chat_left_img);
        }
    }

    protected static class LeftTapeHolder extends LeftViewHolder {
        public ImageView animIv, stateIv;
        public TextView durationTv;
        private LinearLayout tapeLl;

        private LeftTapeHolder(View itemView) {
            super(itemView);
            animIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_anim_left);
            stateIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_play_left);
            durationTv = itemView.findViewById(R.id.tv_message_chat_voice_record_time_left);
            tapeLl = itemView.findViewById(R.id.ll_message_chat_voice_record_container_left);
        }
    }

    protected static class LeftVideoHolder extends LeftViewHolder {
        public ImageView videoIv;

        private LeftVideoHolder(View itemView) {
            super(itemView);
            videoIv = itemView.findViewById(R.id.iv_message_chat_video_img_left);
        }
    }

    protected static class LeftReplyHolder extends LeftViewHolder {
        public ImageView replyIv, animIv, recordLoadingIv, recordPlayIv,replyVideoIv,replyStateIv
                ,loadingIv;
        public TextView replyNameTv, recordTimeTv, replyTypeTv, replyContentTv;
        public LinearLayout recordLl;
        public RelativeLayout replyRl;

        private LeftReplyHolder(View itemView) {
            super(itemView);
            replyIv = itemView.findViewById(R.id.iv_message_chat_reply_img);
            replyNameTv = itemView.findViewById(R.id.tv_message_chat_reply_name);
            recordLl = itemView.findViewById(R.id.ll_message_chat_voice_record_container);
            animIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_anim);
            recordTimeTv = itemView.findViewById(R.id.tv_message_chat_voice_record_time);
            recordLoadingIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_loading);
            recordPlayIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_play);
            replyTypeTv = itemView.findViewById(R.id.tv_message_chat_reply_type);
            replyContentTv = itemView.findViewById(R.id.tv_message_chat_reply_content);
            replyRl=itemView.findViewById(R.id.ll_message_chat_reply_container);
            replyVideoIv=itemView.findViewById(R.id.iv_message_chat_reply_video);
            replyStateIv=itemView.findViewById(R.id.iv_chat_file_download_state);
            loadingIv = itemView.findViewById(R.id.iv_chat_text_msg_loading_left);
        }
    }

    private static class RightViewHolder extends RecyclerView.ViewHolder {
        private TextView timeTv, dateTv;
        private ImageView sendingIv;
        ImageView failTopIv, failCenterIv;

        private RightViewHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.tv_conversation_chat_time_right);
            failTopIv = itemView.findViewById(R.id.iv_chat_send_fail_top_right);
            failCenterIv = itemView.findViewById(R.id.iv_chat_send_fail_center_right);
            sendingIv = itemView.findViewById(R.id.iv_chat_text_msg_sending_right);
            dateTv = itemView.findViewById(R.id.tv_message_chat_date);
        }
    }

    private static class LeftViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarIv, redPointIv, loadingIv;
        private TextView timeTv, dateTv, nameTv;

        private LeftViewHolder(View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.civ_message_chat_left_avatar);
            redPointIv = itemView.findViewById(R.id.iv_conversation_unread_red_point_left);
            loadingIv = itemView.findViewById(R.id.iv_chat_text_msg_loading_left);
            timeTv = itemView.findViewById(R.id.tv_conversation_chat_time_left);
            dateTv = itemView.findViewById(R.id.tv_message_chat_date);
            nameTv = itemView.findViewById(R.id.tv_conversation_chat_left_name);
        }
    }

}
