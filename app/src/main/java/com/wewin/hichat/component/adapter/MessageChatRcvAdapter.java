package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.HyperLinkUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.androidlib.utils.EmoticonUtil;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.StrUtil;
import com.wewin.hichat.component.base.BaseRcvTopLoadAdapter;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.FileInfo;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.VoiceCall;

import java.util.List;

/**
 * Created by Darren on 2018/12/17.
 */
public class MessageChatRcvAdapter extends BaseRcvTopLoadAdapter {

    private Context context;
    private List<ChatMsg> msgList;
    private OnMsgClickListener msgClickListener;

    public MessageChatRcvAdapter(Context context, List<ChatMsg> msgList) {
        this.context = context;
        this.msgList = msgList;
    }

    public interface OnMsgClickListener {
        void docClick(int position);//点击文档

        void videoClick(int position);//点击视频

        void imgClick(int position);//点击图片

        void tapeRecordClick(int position);//点击录音

        void voiceCallClick();//点击语音通话

        void avatarLeftClick(int position);//点击左侧头像

        void avatarRightClick(int position);//点击右侧头像
    }

    public void setOnMsgClickListener(OnMsgClickListener msgClickListener) {
        this.msgClickListener = msgClickListener;
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
        return new ItemViewHolder(View.inflate(context, R.layout.item_message_chat, null));
    }

    @Override
    protected void bindViewData(RecyclerView.ViewHolder holder, final int position) {
        if (!(holder instanceof ItemViewHolder)) {
            return;
        }
        ItemViewHolder iHolder = (ItemViewHolder) holder;

        long currentTimestamp = msgList.get(position).getCreateTimestamp();

        if (position == 0) {
            iHolder.dateTv.setVisibility(View.VISIBLE);
            String dateStr = TimeUtil.getFormatDate(msgList.get(position).getCreateTimestamp());
            iHolder.dateTv.setText(dateStr);

        } else {
            long lastTimestamp = msgList.get(position - 1).getCreateTimestamp();
            if (TimeUtil.isSameDay(currentTimestamp, lastTimestamp)) {
                iHolder.dateTv.setVisibility(View.GONE);

            } else {
                iHolder.dateTv.setVisibility(View.VISIBLE);
                String dateStr = TimeUtil.getFormatDate(msgList.get(position).getCreateTimestamp());
                iHolder.dateTv.setText(dateStr);
            }
        }

        //消息是否自己发送
        boolean selfState = false;
        if (msgList.get(position).getContentType() != ChatMsg.TYPE_CONTENT_VOICE_CALL
                && UserDao.user.getId().equals(msgList.get(position).getSenderId())) {
            selfState = true;

        } else if (msgList.get(position).getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL
                && msgList.get(position).getVoiceCall() != null
                && msgList.get(position).getVoiceCall().getInviteUserId().equals(UserDao.user.getId())) {
            selfState = true;
        }

        /*左侧对方消息*/
        if (!selfState) {
            iHolder.leftContainerRl.setVisibility(View.VISIBLE);
            iHolder.leftExtraStateRl.setVisibility(View.VISIBLE);
            iHolder.rightContainerRl.setVisibility(View.GONE);
            iHolder.rightExtraStateRl.setVisibility(View.GONE);

            FriendInfo contactInfo = ContactUserDao.getContactUser(msgList.get(position).getSenderId());
            if (contactInfo != null) {
                ImgUtil.load(context, contactInfo.getAvatar(), iHolder.leftAvatarIv);
            }
            iHolder.leftTimeTv.setText(TimeUtil.timestampToStr(msgList.get(position)
                    .getCreateTimestamp(), "HH:mm"));

            //消息接收状态
            iHolder.leftUnreadRedIv.setVisibility(View.INVISIBLE);
            iHolder.leftDownloadingIv.setVisibility(View.INVISIBLE);
            iHolder.leftDownloadingIv.clearAnimation();

            switch (msgList.get(position).getContentType()) {
                //左侧text消息
                case ChatMsg.TYPE_CONTENT_TEXT:
                case ChatMsg.TYPE_CONTENT_AT:
                    iHolder.leftImgIv.setVisibility(View.GONE);
                    iHolder.leftVideoRl.setVisibility(View.GONE);
                    iHolder.leftDocRl.setVisibility(View.GONE);
                    iHolder.leftContentTv.setVisibility(View.VISIBLE);
                    iHolder.leftTapeRecordLl.setVisibility(View.GONE);
                    iHolder.leftVoiceCallLl.setVisibility(View.GONE);

                    iHolder.leftContentTv.setMovementMethod(LinkMovementMethod.getInstance());
                    SpannableString expressionString = EmoticonUtil.getExpressionString(context,
                            msgList.get(position).getContent());
                    iHolder.leftContentTv.setText(new HyperLinkUtil().getHyperClickableSpan(context,
                            new SpannableStringBuilder(expressionString), msgList.get(position)
                                    .getAtFriendMap(), R.color.blue_main));
                    break;

                //左侧file消息
                case ChatMsg.TYPE_CONTENT_FILE:
                    if (msgList.get(position).getFileInfo() == null) {
                        return;
                    }
                    FileInfo fileInfo = msgList.get(position).getFileInfo();
                    String filePath = "";
                    String originPath = fileInfo.getOriginPath();
                    String downloadPath = fileInfo.getDownloadPath();
                    String savePath = fileInfo.getSavePath();
                    if (!TextUtils.isEmpty(originPath)) {
                        filePath = originPath;
                    } else if (!TextUtils.isEmpty(savePath)) {
                        filePath = savePath;
                    } else if (!TextUtils.isEmpty(downloadPath)) {
                        filePath = downloadPath;
                    }

                    switch (fileInfo.getFileType()) {
                        case FileInfo.TYPE_IMG:
                            iHolder.leftImgIv.setVisibility(View.VISIBLE);
                            iHolder.leftVideoRl.setVisibility(View.GONE);
                            iHolder.leftDocRl.setVisibility(View.GONE);
                            iHolder.leftContentTv.setVisibility(View.GONE);
                            iHolder.leftTapeRecordLl.setVisibility(View.GONE);
                            iHolder.leftVoiceCallLl.setVisibility(View.GONE);

                            ImgUtil.load(context, filePath, iHolder.leftImgIv);
                            break;

                        case FileInfo.TYPE_VIDEO:
                            iHolder.leftImgIv.setVisibility(View.GONE);
                            iHolder.leftVideoRl.setVisibility(View.VISIBLE);
                            iHolder.leftDocRl.setVisibility(View.GONE);
                            iHolder.leftContentTv.setVisibility(View.GONE);
                            iHolder.leftTapeRecordLl.setVisibility(View.GONE);
                            iHolder.leftVoiceCallLl.setVisibility(View.GONE);

                            ImgUtil.load(context, filePath, iHolder.leftVideoImgIv);
                            break;

                        case FileInfo.TYPE_DOC:
                        case FileInfo.TYPE_MUSIC:
                            iHolder.leftImgIv.setVisibility(View.GONE);
                            iHolder.leftVideoRl.setVisibility(View.GONE);
                            iHolder.leftDocRl.setVisibility(View.VISIBLE);
                            iHolder.leftContentTv.setVisibility(View.GONE);
                            iHolder.leftTapeRecordLl.setVisibility(View.GONE);
                            iHolder.leftVoiceCallLl.setVisibility(View.GONE);

                            String nameStr = fileInfo.getFileName();
                            String name = nameStr.substring(0, nameStr.lastIndexOf("."));
                            String type = nameStr.substring(nameStr.lastIndexOf("."), nameStr.length());
                            iHolder.leftDocNameTv.setText(name);
                            iHolder.leftDocTypeTv.setText(type);
                            iHolder.leftDocSizeTv.setText(StrUtil.formatFileLength(fileInfo.getFileLength()));

                            if (filePath.endsWith(".txt")) {
                                iHolder.leftDocIconIv.setImageResource(R.drawable.icon_txt);
                            } else if (filePath.endsWith(".doc") || filePath.endsWith(".docx")) {
                                iHolder.leftDocIconIv.setImageResource(R.drawable.icon_word);
                            } else if (filePath.endsWith(".xls") || filePath.endsWith(".xlsx")) {
                                iHolder.leftDocIconIv.setImageResource(R.drawable.icon_excel);
                            } else if (filePath.endsWith(".ppt") || filePath.endsWith(".pptx")) {
                                iHolder.leftDocIconIv.setImageResource(R.drawable.icon_ppt);
                            } else if (filePath.endsWith(".pdf")) {
                                iHolder.leftDocIconIv.setImageResource(R.drawable.icon_pdf);
                            } else if (filePath.endsWith(".mp3") || filePath.endsWith(".mp4")) {
                                iHolder.leftDocIconIv.setImageResource(R.drawable.icon_music);
                            }

                            if (fileInfo.getDownloadState() == FileInfo.TYPE_DOWNLOAD_NOT) {
                                iHolder.leftFileDownloadStateIv.setVisibility(View.VISIBLE);
                                iHolder.leftFileDownloadStateIv.setImageResource(R.drawable.file_download_black);

                            } else if (fileInfo.getDownloadState() == FileInfo.TYPE_DOWNLOADING) {
                                iHolder.leftFileDownloadStateIv.setVisibility(View.VISIBLE);
                                iHolder.leftFileDownloadStateIv.setImageResource(R.drawable.file_download_off_black);
                                iHolder.leftDownloadingIv.setVisibility(View.VISIBLE);
                                iHolder.leftDownloadingIv.startAnimation(AnimationUtils.loadAnimation(context,
                                        R.anim.rotate360_anim));

                            } else if (fileInfo.getDownloadState() == FileInfo.TYPE_DOWNLOAD_SUCCESS) {
                                iHolder.leftFileDownloadStateIv.setVisibility(View.INVISIBLE);

                            } else if (fileInfo.getDownloadState() == FileInfo.TYPE_DOWNLOAD_FAIL) {
                                iHolder.leftFileDownloadStateIv.setVisibility(View.VISIBLE);
                                iHolder.leftFileDownloadStateIv.setImageResource(R.drawable.file_download_again_black);
                            }
                            break;

                        case FileInfo.TYPE_TAPE_RECORD:
                            iHolder.leftImgIv.setVisibility(View.GONE);
                            iHolder.leftVideoRl.setVisibility(View.GONE);
                            iHolder.leftDocRl.setVisibility(View.GONE);
                            iHolder.leftContentTv.setVisibility(View.GONE);
                            iHolder.leftTapeRecordLl.setVisibility(View.VISIBLE);
                            iHolder.leftVoiceCallLl.setVisibility(View.GONE);

                            String durationStr = TimeUtil.formatTimeStr(fileInfo.getDuration());
                            iHolder.leftTapeTimeTv.setText(durationStr);
                            
                            if (fileInfo.getDownloadState() == FileInfo.TYPE_DOWNLOADING){
                                iHolder.leftTapeLoadingIv.setVisibility(View.VISIBLE);
                                iHolder.leftTapeLoadingIv.setAnimation(AnimationUtils.loadAnimation(context,
                                        R.anim.rotate360_anim));
                            }

                            if (fileInfo.getTapeUnreadMark() == 1) {
                                iHolder.leftUnreadRedIv.setVisibility(View.VISIBLE);
                            } else {
                                iHolder.leftUnreadRedIv.setVisibility(View.INVISIBLE);
                            }

                            if (fileInfo.getTapePlayingMark() == 1) {
                                iHolder.leftTapePlayIv.setImageResource(R.drawable.msg_btn_stop);
                                iHolder.leftTapeAnimIv.setImageResource(R.drawable.anim_tape_record_playing);
                                AnimationDrawable animationDrawable = (AnimationDrawable) iHolder
                                        .leftTapeAnimIv.getDrawable();
                                animationDrawable.start();

                            } else {
                                iHolder.leftTapePlayIv.setImageResource(R.drawable.msg_btn_play);
                                if (iHolder.leftTapeAnimIv.getDrawable() instanceof AnimationDrawable) {
                                    AnimationDrawable animationDrawable = (AnimationDrawable) iHolder
                                            .leftTapeAnimIv.getDrawable();
                                    if (animationDrawable != null) {
                                        animationDrawable.stop();
                                    }
                                }
                                iHolder.leftTapeAnimIv.setImageResource(R.drawable.voice_play_07);
                            }
                            break;

                        default:
                            iHolder.leftImgIv.setVisibility(View.GONE);
                            iHolder.leftVideoRl.setVisibility(View.GONE);
                            iHolder.leftDocRl.setVisibility(View.GONE);
                            iHolder.leftContentTv.setVisibility(View.VISIBLE);
                            iHolder.leftTapeRecordLl.setVisibility(View.GONE);
                            iHolder.leftVoiceCallLl.setVisibility(View.GONE);

                            iHolder.leftContentTv.setText(R.string.version_not_support);
                            break;
                    }
                    break;

                //左侧语音通话
                case ChatMsg.TYPE_CONTENT_VOICE_CALL:
                    if (msgList.get(position).getVoiceCall() == null) {
                        return;
                    }
                    iHolder.leftImgIv.setVisibility(View.GONE);
                    iHolder.leftVideoRl.setVisibility(View.GONE);
                    iHolder.leftDocRl.setVisibility(View.GONE);
                    iHolder.leftContentTv.setVisibility(View.GONE);
                    iHolder.leftTapeRecordLl.setVisibility(View.GONE);
                    iHolder.leftVoiceCallLl.setVisibility(View.VISIBLE);

                    switch (msgList.get(position).getVoiceCall().getConnectState()) {
                        case VoiceCall.REFUSE:
                            iHolder.leftVoiceCallStateTv.setText(context.getString(R.string.refused));
                            break;

                        case VoiceCall.CANCEL:
                            iHolder.leftVoiceCallStateTv.setText(context.getString(R.string.other_side_canceled));
                            break;

                        case VoiceCall.FINISH:
                            iHolder.leftVoiceCallStateTv.setText(context.getString(R.string.call_finish)
                                    + TimeUtil.formatTimeStr(
                                    msgList.get(position).getVoiceCall().getDuration()));
                            break;

                        case VoiceCall.TIME_OUT:
                            iHolder.leftVoiceCallStateTv.setText(context.getString(R.string.you_not_accept));
                            break;

                        default:
                            iHolder.leftVoiceCallStateTv.setText(context.getString(R.string.other_side_canceled));
                            break;
                    }
                    break;

                default:
                    iHolder.leftImgIv.setVisibility(View.GONE);
                    iHolder.leftVideoRl.setVisibility(View.GONE);
                    iHolder.leftDocRl.setVisibility(View.GONE);
                    iHolder.leftContentTv.setVisibility(View.VISIBLE);
                    iHolder.leftTapeRecordLl.setVisibility(View.GONE);
                    iHolder.leftVoiceCallLl.setVisibility(View.GONE);

                    iHolder.leftContentTv.setText(R.string.version_not_support);
                    break;
            }

        } else {
            //右侧己方消息
            iHolder.leftContainerRl.setVisibility(View.GONE);
            iHolder.leftExtraStateRl.setVisibility(View.GONE);
            iHolder.rightContainerRl.setVisibility(View.VISIBLE);
            iHolder.rightExtraStateRl.setVisibility(View.VISIBLE);

            iHolder.rightTimeTv.setText(TimeUtil.timestampToStr(msgList
                    .get(position).getCreateTimestamp(), "HH:mm"));

            //发送状态
            if (msgList.get(position).getSendState() == ChatMsg.TYPE_SEND_FAIL) {
                iHolder.rightFailTopIv.setVisibility(View.INVISIBLE);
                iHolder.rightFailCenterIv.setVisibility(View.VISIBLE);
                iHolder.rightTextSendingIv.setVisibility(View.INVISIBLE);

            } else if (msgList.get(position).getSendState() == ChatMsg.TYPE_SENDING) {
                iHolder.rightFailTopIv.setVisibility(View.INVISIBLE);
                iHolder.rightFailCenterIv.setVisibility(View.INVISIBLE);
                iHolder.rightTextSendingIv.setVisibility(View.VISIBLE);
                iHolder.rightTextSendingIv.startAnimation(AnimationUtils
                        .loadAnimation(context, R.anim.rotate360_anim));

            } else if (msgList.get(position).getSendState() == ChatMsg.TYPE_SEND_SUCCESS) {
                iHolder.rightFailTopIv.setVisibility(View.INVISIBLE);
                iHolder.rightFailCenterIv.setVisibility(View.INVISIBLE);
                iHolder.rightTextSendingIv.setVisibility(View.INVISIBLE);
            }

            switch (msgList.get(position).getContentType()) {
                //右侧text消息
                case ChatMsg.TYPE_CONTENT_TEXT:
                case ChatMsg.TYPE_CONTENT_AT:
                    iHolder.rightImgIv.setVisibility(View.GONE);
                    iHolder.rightVideoRl.setVisibility(View.GONE);
                    iHolder.rightDocRl.setVisibility(View.GONE);
                    iHolder.rightContentTv.setVisibility(View.VISIBLE);
                    iHolder.rightTapeRecordLl.setVisibility(View.GONE);
                    iHolder.rightVoiceCallLl.setVisibility(View.GONE);

                    iHolder.rightContentTv.setMovementMethod(LinkMovementMethod.getInstance());
                    SpannableString expressionString = EmoticonUtil.getExpressionString(context,
                            msgList.get(position).getContent());
                    iHolder.rightContentTv.setText(new HyperLinkUtil().getHyperClickableSpan(context,
                            new SpannableStringBuilder(expressionString), msgList.get(position)
                                    .getAtFriendMap(), R.color.blue_main));

                    if (msgList.get(position).getSendState() == ChatMsg.TYPE_SEND_FAIL) {
                        iHolder.rightFailTopIv.setVisibility(View.VISIBLE);
                        iHolder.rightFailCenterIv.setVisibility(View.INVISIBLE);
                    }
                    break;

                //右侧file消息
                case ChatMsg.TYPE_CONTENT_FILE:
                    if (msgList.get(position).getFileInfo() == null) {
                        return;
                    }
                    FileInfo fileInfo = msgList.get(position).getFileInfo();
                    String filePath = "";
                    String originPath = fileInfo.getOriginPath();
                    String downloadPath = fileInfo.getDownloadPath();
                    String savePath = fileInfo.getSavePath();
                    if (!TextUtils.isEmpty(originPath)) {
                        filePath = originPath;
                    } else if (!TextUtils.isEmpty(savePath)) {
                        filePath = savePath;
                    } else if (!TextUtils.isEmpty(downloadPath)) {
                        filePath = downloadPath;
                    }

                    switch (fileInfo.getFileType()) {
                        case FileInfo.TYPE_IMG:
                            iHolder.rightImgIv.setVisibility(View.VISIBLE);
                            iHolder.rightVideoRl.setVisibility(View.GONE);
                            iHolder.rightDocRl.setVisibility(View.GONE);
                            iHolder.rightContentTv.setVisibility(View.GONE);
                            iHolder.rightTapeRecordLl.setVisibility(View.GONE);
                            iHolder.rightVoiceCallLl.setVisibility(View.GONE);

                            ImgUtil.load(context, filePath, iHolder.rightImgIv);
                            break;

                        case FileInfo.TYPE_VIDEO:
                            iHolder.rightImgIv.setVisibility(View.GONE);
                            iHolder.rightVideoRl.setVisibility(View.VISIBLE);
                            iHolder.rightDocRl.setVisibility(View.GONE);
                            iHolder.rightContentTv.setVisibility(View.GONE);
                            iHolder.rightTapeRecordLl.setVisibility(View.GONE);
                            iHolder.rightVoiceCallLl.setVisibility(View.GONE);

                            ImgUtil.load(context, filePath, iHolder.rightVideoImgIv);
                            break;

                        case FileInfo.TYPE_DOC:
                        case FileInfo.TYPE_MUSIC:
                            iHolder.rightImgIv.setVisibility(View.GONE);
                            iHolder.rightVideoRl.setVisibility(View.GONE);
                            iHolder.rightDocRl.setVisibility(View.VISIBLE);
                            iHolder.rightContentTv.setVisibility(View.GONE);
                            iHolder.rightTapeRecordLl.setVisibility(View.GONE);
                            iHolder.rightVoiceCallLl.setVisibility(View.GONE);

                            String nameStr = fileInfo.getFileName();
                            String name = nameStr.substring(0, nameStr.lastIndexOf("."));
                            String type = nameStr.substring(nameStr.lastIndexOf("."), nameStr.length());
                            iHolder.rightDocNameTv.setText(name);
                            iHolder.rightDocTypeTv.setText(type);
                            iHolder.rightDocSizeTv.setText(StrUtil.formatFileLength(fileInfo.getFileLength()));

                            if (filePath.endsWith(".txt")) {
                                iHolder.rightDocIconIv.setImageResource(R.drawable.icon_txt);
                            } else if (filePath.endsWith(".doc") || filePath.endsWith(".docx")) {
                                iHolder.rightDocIconIv.setImageResource(R.drawable.icon_word);
                            } else if (filePath.endsWith(".xls") || filePath.endsWith(".xlsx")) {
                                iHolder.rightDocIconIv.setImageResource(R.drawable.icon_excel);
                            } else if (filePath.endsWith(".ppt") || filePath.endsWith(".pptx")) {
                                iHolder.rightDocIconIv.setImageResource(R.drawable.icon_ppt);
                            } else if (filePath.endsWith(".pdf")) {
                                iHolder.rightDocIconIv.setImageResource(R.drawable.icon_pdf);
                            } else if (filePath.endsWith(".mp3") || filePath.endsWith(".mp4")) {
                                iHolder.rightDocIconIv.setImageResource(R.drawable.icon_music);
                            }
                            break;

                        case FileInfo.TYPE_TAPE_RECORD:
                            iHolder.rightImgIv.setVisibility(View.GONE);
                            iHolder.rightVideoRl.setVisibility(View.GONE);
                            iHolder.rightDocRl.setVisibility(View.GONE);
                            iHolder.rightContentTv.setVisibility(View.GONE);
                            iHolder.rightTapeRecordLl.setVisibility(View.VISIBLE);
                            iHolder.rightVoiceCallLl.setVisibility(View.GONE);

                            String durationStr = TimeUtil.formatTimeStr(fileInfo.getDuration());
                            iHolder.rightTapeTimeTv.setText(durationStr);

                            if (msgList.get(position).getSendState() == ChatMsg.TYPE_SEND_FAIL) {
                                iHolder.rightFailTopIv.setVisibility(View.VISIBLE);
                                iHolder.rightFailCenterIv.setVisibility(View.INVISIBLE);
                            }

                            if (fileInfo.getTapePlayingMark() == 1) {
                                iHolder.rightTapePlayIv.setImageResource(R.drawable.msg_btn_stop);
                                iHolder.rightTapeAnimIv.setImageResource(R.drawable.anim_tape_record_playing);
                                AnimationDrawable animationDrawable = (AnimationDrawable) iHolder.rightTapeAnimIv.getDrawable();
                                animationDrawable.start();

                            } else {
                                iHolder.rightTapePlayIv.setImageResource(R.drawable.msg_btn_play);
                                if (iHolder.rightTapeAnimIv.getDrawable() instanceof AnimationDrawable) {
                                    AnimationDrawable animationDrawable = (AnimationDrawable) iHolder
                                            .rightTapeAnimIv.getDrawable();
                                    if (animationDrawable != null) {
                                        animationDrawable.stop();
                                    }
                                }
                                iHolder.rightTapeAnimIv.setImageResource(R.drawable.voice_play_07);
                            }
                            break;

                        default:
                            iHolder.rightImgIv.setVisibility(View.GONE);
                            iHolder.rightVideoRl.setVisibility(View.GONE);
                            iHolder.rightDocRl.setVisibility(View.GONE);
                            iHolder.rightContentTv.setVisibility(View.VISIBLE);
                            iHolder.rightTapeRecordLl.setVisibility(View.GONE);
                            iHolder.rightVoiceCallLl.setVisibility(View.GONE);

                            iHolder.rightContentTv.setText(R.string.version_not_support);
                            break;
                    }
                    break;

                //右侧语音通话
                case ChatMsg.TYPE_CONTENT_VOICE_CALL:
                    if (msgList.get(position).getVoiceCall() == null) {
                        return;
                    }
                    iHolder.rightImgIv.setVisibility(View.GONE);
                    iHolder.rightVideoRl.setVisibility(View.GONE);
                    iHolder.rightDocRl.setVisibility(View.GONE);
                    iHolder.rightContentTv.setVisibility(View.GONE);
                    iHolder.rightTapeRecordLl.setVisibility(View.GONE);
                    iHolder.rightVoiceCallLl.setVisibility(View.VISIBLE);

                    switch (msgList.get(position).getVoiceCall().getConnectState()) {
                        case VoiceCall.REFUSE:
                            iHolder.rightVoiceCallStateTv.setText(context.getString(R.string.other_side_refused));
                            break;

                        case VoiceCall.CANCEL:
                            iHolder.rightVoiceCallStateTv.setText(context.getString(R.string.canceled));
                            break;

                        case VoiceCall.FINISH:
                            iHolder.rightVoiceCallStateTv.setText(context.getString(R.string.call_finish)
                                    + TimeUtil.formatTimeStr(msgList.get(position).getVoiceCall().getDuration()));
                            break;

                        case VoiceCall.TIME_OUT:
                            iHolder.rightVoiceCallStateTv.setText(context.getString(R.string.other_side_no_response));
                            break;

                        default:
                            iHolder.rightVoiceCallStateTv.setText(context.getString(R.string.canceled));
                            break;
                    }
                    break;

                default:
                    iHolder.rightImgIv.setVisibility(View.GONE);
                    iHolder.rightVideoRl.setVisibility(View.GONE);
                    iHolder.rightDocRl.setVisibility(View.GONE);
                    iHolder.rightContentTv.setVisibility(View.VISIBLE);
                    iHolder.rightTapeRecordLl.setVisibility(View.GONE);
                    iHolder.rightVoiceCallLl.setVisibility(View.GONE);

                    iHolder.rightContentTv.setText(R.string.version_not_support);
                    break;
            }
        }
        setListener(iHolder, position);
    }


    private void setListener(ItemViewHolder iHolder, final int position) {
        iHolder.leftAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgClickListener != null) {
                    msgClickListener.avatarLeftClick(position);
                }
            }
        });

        iHolder.rightAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgClickListener != null) {
                    msgClickListener.avatarRightClick(position);
                }
            }
        });

        iHolder.leftImgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgClickListener != null) {
                    msgClickListener.imgClick(position);
                }
            }
        });

        iHolder.rightImgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgClickListener != null) {
                    msgClickListener.imgClick(position);
                }
            }
        });

        iHolder.leftVideoImgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgClickListener != null) {
                    msgClickListener.videoClick(position);
                }
            }
        });

        iHolder.rightVideoImgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgClickListener != null) {
                    msgClickListener.videoClick(position);
                }
            }
        });

        iHolder.leftDocRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgClickListener != null) {
                    msgClickListener.docClick(position);
                }
            }
        });

        iHolder.rightDocRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgClickListener != null) {
                    msgClickListener.docClick(position);
                }
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

        iHolder.leftTapeRecordLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgClickListener != null) {
                    msgClickListener.tapeRecordClick(position);
                }
            }
        });

        iHolder.rightTapeRecordLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgClickListener != null) {
                    msgClickListener.tapeRecordClick(position);
                }
            }
        });

        iHolder.leftVoiceCallLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgClickListener != null) {
                    msgClickListener.voiceCallClick();
                }
            }
        });

        iHolder.rightVoiceCallLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgClickListener != null) {
                    msgClickListener.voiceCallClick();
                }
            }
        });
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView dateTv, leftContentTv, leftDocNameTv,
                leftDocTypeTv, leftDocSizeTv, leftTapeTimeTv,
                rightContentTv, rightDocNameTv, rightDocTypeTv, rightDocSizeTv, rightTapeTimeTv,
                leftVoiceCallStateTv, rightVoiceCallStateTv, leftTimeTv, rightTimeTv;
        ImageView leftAvatarIv, leftImgIv, leftDocIconIv, leftVideoImgIv, leftTapeAnimIv, leftTapePlayIv,
                rightAvatarIv, rightImgIv, rightDocIconIv, rightVideoImgIv, rightTapeAnimIv, rightTapePlayIv,
                leftTapeLoadingIv, rightTapeSendingIv, rightFailTopIv, rightFailCenterIv, rightTextSendingIv,
                rightFileSendingIv, leftFileDownloadStateIv, leftUnreadRedIv, leftDownloadingIv;
        RelativeLayout leftContainerRl, leftDocRl, leftVideoRl, rightContainerRl, rightDocRl, rightVideoRl,
                leftExtraStateRl, rightExtraStateRl;
        LinearLayout leftTapeRecordLl, rightTapeRecordLl, leftVoiceCallLl, rightVoiceCallLl;

        private ItemViewHolder(View itemView) {
            super(itemView);
            dateTv = itemView.findViewById(R.id.tv_conversation_chat_date);
            leftContentTv = itemView.findViewById(R.id.tv_conversation_chat_left_content);
            leftAvatarIv = itemView.findViewById(R.id.civ_message_chat_left_avatar);
            leftContainerRl = itemView.findViewById(R.id.ll_message_chat_left_container);
            leftImgIv = itemView.findViewById(R.id.iv_message_chat_left_img);
            leftDocNameTv = itemView.findViewById(R.id.tv_message_chat_file_name_left);
            leftDocTypeTv = itemView.findViewById(R.id.tv_message_chat_file_type_left);
            leftDocSizeTv = itemView.findViewById(R.id.tv_message_chat_file_size_left);
            leftDocRl = itemView.findViewById(R.id.rl_message_chat_file_container_left);
            leftDocIconIv = itemView.findViewById(R.id.iv_message_chat_file_icon_left);
            leftVideoRl = itemView.findViewById(R.id.rl_message_chat_video_container_left);
            leftVideoImgIv = itemView.findViewById(R.id.iv_message_chat_video_img_left);
            leftTapeRecordLl = itemView.findViewById(R.id.ll_message_chat_voice_record_container_left);
            leftTapeTimeTv = itemView.findViewById(R.id.tv_message_chat_voice_record_time_left);
            leftTapeAnimIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_anim_left);
            leftTapePlayIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_play_left);
            leftTapeLoadingIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_loading_left);
            leftVoiceCallLl = itemView.findViewById(R.id.ll_message_chat_call_state_container_left);
            leftVoiceCallStateTv = itemView.findViewById(R.id.tv_message_chat_voice_call_state_left);
            leftTimeTv = itemView.findViewById(R.id.tv_conversation_chat_time_left);
            leftExtraStateRl = itemView.findViewById(R.id.rl_conversation_chat_extra_left);
            leftFileDownloadStateIv = itemView.findViewById(R.id.iv_chat_file_download_state);
            leftUnreadRedIv = itemView.findViewById(R.id.iv_conversation_unread_red_point_left);
            leftDownloadingIv = itemView.findViewById(R.id.iv_chat_text_msg_sending_left);

            rightContentTv = itemView.findViewById(R.id.tv_message_chat_right_content);
            rightAvatarIv = itemView.findViewById(R.id.civ_message_chat_right_avatar);
            rightContainerRl = itemView.findViewById(R.id.rl_message_chat_right_container);
            rightImgIv = itemView.findViewById(R.id.iv_message_chat_right_img);
            rightDocRl = itemView.findViewById(R.id.rl_message_chat_file_container_right);
            rightDocNameTv = itemView.findViewById(R.id.tv_message_chat_file_name_right);
            rightDocTypeTv = itemView.findViewById(R.id.tv_message_chat_file_type_right);
            rightDocSizeTv = itemView.findViewById(R.id.tv_message_chat_file_size_right);
            rightDocIconIv = itemView.findViewById(R.id.iv_message_chat_file_icon_right);
            rightVideoRl = itemView.findViewById(R.id.rl_message_chat_video_container_right);
            rightVideoImgIv = itemView.findViewById(R.id.iv_message_chat_video_img_right);
            rightTapeRecordLl = itemView.findViewById(R.id.ll_message_chat_voice_record_container_right);
            rightTapeTimeTv = itemView.findViewById(R.id.tv_message_chat_voice_record_time_right);
            rightTapeAnimIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_anim_right);
            rightTapePlayIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_play_right);
            rightTapeSendingIv = itemView.findViewById(R.id.iv_conversation_chat_tape_record_loading_right);
            rightVoiceCallLl = itemView.findViewById(R.id.ll_message_chat_call_state_container_right);
            rightVoiceCallStateTv = itemView.findViewById(R.id.tv_message_chat_voice_call_state_right);
            rightTimeTv = itemView.findViewById(R.id.tv_conversation_chat_time_right);
            rightExtraStateRl = itemView.findViewById(R.id.rl_conversation_chat_extra_right);
            rightFailTopIv = itemView.findViewById(R.id.iv_chat_send_fail_top_right);
            rightFailCenterIv = itemView.findViewById(R.id.iv_chat_send_fail_center_right);
            rightTextSendingIv = itemView.findViewById(R.id.iv_chat_text_msg_sending_right);
            rightFileSendingIv = itemView.findViewById(R.id.iv_chat_file_loading_right);

        }
    }

}
