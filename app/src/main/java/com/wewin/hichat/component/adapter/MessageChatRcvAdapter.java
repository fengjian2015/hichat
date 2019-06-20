package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.EmoticonUtil;
import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.androidlib.utils.HyperLinkUtil;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.NameUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.androidlib.widget.CustomLinkMovementMethod;
import com.wewin.hichat.component.base.BaseMessageChatRcvAdapter;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FileInfo;
import com.wewin.hichat.model.db.entity.ReplyMsgInfo;
import com.wewin.hichat.model.db.entity.VoiceCall;

import java.util.List;

/**
 * Created by Darren on 2018/12/17.
 */
public class MessageChatRcvAdapter extends BaseMessageChatRcvAdapter {

    private Context context;
    private List<ChatMsg> msgList;

    public MessageChatRcvAdapter(Context context, List<ChatMsg> msgList) {
        super(context, msgList);
        this.context = context;
        this.msgList = msgList;
    }

    //右侧文字+@
    @Override
    protected void setRightTextHolder(RightTextHolder rightTextHolder, int position) {
        if (TextUtils.isEmpty(msgList.get(position).getContent())) {
            return;
        }
        rightTextHolder.contentTv.setMovementMethod(CustomLinkMovementMethod.getInstance());
        SpannableString spanStr = new SpannableString(msgList.get(position).getContent());

        if (ChatMsg.TYPE_CONTENT_AT == msgList.get(position).getContentType()
                && msgList.get(position).getAtFriendMap() != null
                && !msgList.get(position).getAtFriendMap().isEmpty()
                && ChatRoom.TYPE_GROUP.equals(msgList.get(position).getRoomType())) {
            spanStr = HyperLinkUtil.getATSpanStr(context, spanStr, msgList.get(position).getAtFriendMap(),
                    R.color.blue_main, msgList.get(position).getRoomId());
        }
        if (msgList.get(position).getEmoMark() == 1) {
            spanStr = EmoticonUtil.getEmoSpanStr(context, spanStr);
        }
        if (msgList.get(position).getPhoneMark() == 1) {
            spanStr = HyperLinkUtil.getPhoneSpanStr(context, spanStr, R.color.blue_main);
        }
        if (msgList.get(position).getUrlMark() == 1) {
            spanStr = HyperLinkUtil.getLinkSpanStr(context, spanStr, R.color.blue_main);
        }

        rightTextHolder.contentTv.setText(spanStr);
    }

    //右侧语音通话
    @Override
    protected void setRightCallHolder(RightCallHolder rightCallHolder, int position) {
        switch (msgList.get(position).getVoiceCall().getConnectState()) {
            case VoiceCall.REFUSE:
                rightCallHolder.callStateTv.setText(context.getString(R.string.other_side_refused));
                break;

            case VoiceCall.CANCEL:
                rightCallHolder.callStateTv.setText(context.getString(R.string.canceled));
                break;

            case VoiceCall.FINISH:
                rightCallHolder.callStateTv.setText(context.getString(R.string.call_finish)
                        + TimeUtil.formatTimeStr(msgList.get(position).getVoiceCall().getDuration()));
                break;

            case VoiceCall.TIME_OUT:
                rightCallHolder.callStateTv.setText(context.getString(R.string.other_side_no_response));
                break;

            default:
                rightCallHolder.callStateTv.setText(context.getString(R.string.canceled));
                break;
        }
    }

    //右侧图片
    @Override
    protected void setRightImgHolder(RightImgHolder rightImgHolder, int position) {
        FileInfo fileInfo = msgList.get(position).getFileInfo();
        String filePath = "";
        if (!TextUtils.isEmpty(fileInfo.getOriginPath())) {
            filePath = fileInfo.getOriginPath();
        } else if (!TextUtils.isEmpty(fileInfo.getSavePath())) {
            filePath = fileInfo.getSavePath();
        } else if (!TextUtils.isEmpty(fileInfo.getDownloadPath())) {
            filePath = fileInfo.getDownloadPath();
        }
        ImgUtil.load(context, filePath, rightImgHolder.imgIv, R.drawable.con_img_vertical);
    }

    //右侧视频
    @Override
    protected void setRightVideoHolder(RightVideoHolder rightVideoHolder, int position) {
        FileInfo fileInfo = msgList.get(position).getFileInfo();
        if (!TextUtils.isEmpty(fileInfo.getOriginPath())) {
            ImgUtil.load(context, fileInfo.getOriginPath(), rightVideoHolder.videoIv, R.drawable.con_img_vertical);
        } else if (!TextUtils.isEmpty(fileInfo.getSavePath())) {
            ImgUtil.load(context, fileInfo.getSavePath(), rightVideoHolder.videoIv, R.drawable.con_img_vertical);
        } else if (!TextUtils.isEmpty(fileInfo.getDownloadPath())) {
            ImgUtil.load(context, fileInfo.getDownloadPath(), rightVideoHolder.videoIv, R.drawable.con_img_vertical);
        }
    }

    //右侧文档+音乐
    @Override
    protected void setRightDocHolder(RightDocHolder rightDocHolder, int position) {
        FileInfo fileInfo = msgList.get(position).getFileInfo();
        String nameStr = fileInfo.getFileName();
        String name = nameStr.substring(0, nameStr.lastIndexOf("."));
        String type = nameStr.substring(nameStr.lastIndexOf("."), nameStr.length());
        rightDocHolder.nameTv.setText(name);
        rightDocHolder.typeTv.setText(type);
        rightDocHolder.sizeTv.setText(FileUtil.formatFileLength(fileInfo.getFileLength()));

        switch (type) {
            case ".txt":
                rightDocHolder.iconIv.setImageResource(R.drawable.icon_txt);
                break;

            case ".doc":
            case ".docx":
                rightDocHolder.iconIv.setImageResource(R.drawable.icon_word);
                break;

            case ".xls":
            case ".xlsx":
                rightDocHolder.iconIv.setImageResource(R.drawable.icon_excel);
                break;

            case ".ppt":
            case ".pptx":
                rightDocHolder.iconIv.setImageResource(R.drawable.icon_ppt);
                break;

            case ".pdf":
                rightDocHolder.iconIv.setImageResource(R.drawable.icon_pdf);
                break;

            case ".mp3":
            case ".mp4":
                rightDocHolder.iconIv.setImageResource(R.drawable.icon_music);
                break;

            default:
                break;
        }
    }

    @Override
    protected void setRightReplyHolder(RightReplyHolder mHolder, int position) {
        ChatMsg chatMsg = msgList.get(position);
        ReplyMsgInfo replyMsgInfo = chatMsg.getReplyMsgInfo();
        FileInfo fileInfo = replyMsgInfo.getFileInfo();
        mHolder.replyNameTv.setText(NameUtil.getName(replyMsgInfo.getSenderInfo().getId()));
        mHolder.replyContentTv.setVisibility(View.VISIBLE);
        //文本
        mHolder.replyContentTv.setMovementMethod(CustomLinkMovementMethod.getInstance());
        SpannableString spanStr = new SpannableString(msgList.get(position).getContent());
        if (msgList.get(position).getAtFriendMap() != null
                && !msgList.get(position).getAtFriendMap().isEmpty()
                && ChatRoom.TYPE_GROUP.equals(msgList.get(position).getRoomType())) {
            spanStr = HyperLinkUtil.getATSpanStr(context, spanStr, msgList.get(position).getAtFriendMap(),
                    R.color.blue_main, msgList.get(position).getRoomId());
        }
        if (msgList.get(position).getEmoMark() == 1) {
            spanStr = EmoticonUtil.getEmoSpanStr(context, spanStr);
        }
        if (msgList.get(position).getPhoneMark() == 1) {
            spanStr = HyperLinkUtil.getPhoneSpanStr(context, spanStr, R.color.blue_main);
        }
        if (msgList.get(position).getUrlMark() == 1) {
            spanStr = HyperLinkUtil.getLinkSpanStr(context, spanStr, R.color.blue_main);
        }
        mHolder.replyContentTv.setText(spanStr);
        mHolder.replyStateIv.setVisibility(View.GONE);
        mHolder.recordLl.setVisibility(View.GONE);
        mHolder.replyTypeTv.setVisibility(View.VISIBLE);
        mHolder.replyVideoIv.setVisibility(View.GONE);
        if (fileInfo == null) {
            //回复文本消息
            mHolder.replyIv.setVisibility(View.GONE);
            mHolder.replyTypeTv.setMovementMethod(CustomLinkMovementMethod.getInstance());
            SpannableString replyStr = new SpannableString(replyMsgInfo.getContent());
            replyStr = EmoticonUtil.getEmoSpanStr(context, replyStr);
            replyStr = HyperLinkUtil.getPhoneSpanStr(context, replyStr, R.color.blue_main);
            replyStr = HyperLinkUtil.getLinkSpanStr(context, replyStr, R.color.blue_main);
            mHolder.replyTypeTv.setText(replyStr);
        } else {
            mHolder.replyIv.setVisibility(View.VISIBLE);
            String filePath = "";
            switch (fileInfo.getFileType()) {
                case FileInfo.TYPE_IMG:
                case FileInfo.TYPE_VIDEO:
                    //回复图片
                    if (!TextUtils.isEmpty(fileInfo.getOriginPath())) {
                        filePath = fileInfo.getOriginPath();
                    } else if (!TextUtils.isEmpty(fileInfo.getSavePath())) {
                        filePath = fileInfo.getSavePath();
                    } else if (!TextUtils.isEmpty(fileInfo.getDownloadPath())) {
                        filePath = fileInfo.getDownloadPath();
                    }
                    ImgUtil.load(context, filePath, mHolder.replyIv, R.drawable.con_img_vertical);
                    if (FileInfo.TYPE_VIDEO == fileInfo.getFileType()) {
                        mHolder.replyTypeTv.setText(context.getString(R.string.video));
                        mHolder.replyVideoIv.setVisibility(View.VISIBLE);
                    } else {
                        mHolder.replyTypeTv.setText(context.getString(R.string.image));
                    }
                    break;
                case FileInfo.TYPE_DOC:
                case FileInfo.TYPE_MUSIC:
                    String nameStr = fileInfo.getFileName();
                    String type = nameStr.substring(nameStr.lastIndexOf("."), nameStr.length());
                    FileUtil.imageTypeView(type, mHolder.replyIv);
                    if (fileInfo.getFileType() == FileInfo.TYPE_MUSIC) {
                        mHolder.replyTypeTv.setText(context.getString(R.string.musicInfo));
                    } else {
                        mHolder.replyTypeTv.setText(context.getString(R.string.document));
                    }
                    //左侧 文档下载状态
                    if (fileInfo.getDownloadState()==FileInfo.TYPE_DOWNLOAD_NOT){
                        mHolder.replyStateIv.setVisibility(View.VISIBLE);
                        mHolder.replyStateIv.setImageResource(R.drawable.file_download_black);
                        mHolder.loadingIv.setVisibility(View.GONE);
                        mHolder.loadingIv.clearAnimation();
                    }else if(fileInfo.getDownloadState()==FileInfo.TYPE_DOWNLOADING){
                        mHolder.replyStateIv.setVisibility(View.VISIBLE);
                        mHolder.replyStateIv.setImageResource(R.drawable.file_download_off_black);
                        mHolder.loadingIv.setVisibility(View.VISIBLE);
                        mHolder.loadingIv.startAnimation(AnimationUtils.loadAnimation(context,
                                R.anim.rotate360_anim));
                    }else if(fileInfo.getDownloadState()==FileInfo.TYPE_DOWNLOAD_SUCCESS){
                        mHolder.replyStateIv.setVisibility(View.GONE);
                        mHolder.loadingIv.setVisibility(View.GONE);
                        mHolder.loadingIv.clearAnimation();
                    }else if(fileInfo.getDownloadState()==FileInfo.TYPE_DOWNLOAD_FAIL){
                        mHolder.replyStateIv.setVisibility(View.VISIBLE);
                        mHolder.replyStateIv.setImageResource(R.drawable.file_download_again_black);
                        mHolder.loadingIv.setVisibility(View.GONE);
                        mHolder.loadingIv.clearAnimation();
                    }
                    break;
                case FileInfo.TYPE_TAPE_RECORD:
                    mHolder.replyIv.setVisibility(View.GONE);
                    mHolder.replyTypeTv.setVisibility(View.GONE);
                    mHolder.recordLl.setVisibility(View.VISIBLE);
                    String durationStr = TimeUtil.formatTimeStr(fileInfo.getDuration());
                    mHolder.recordTimeTv.setText(durationStr);
                    if (fileInfo.getTapePlayingMark() == 1) {
                        mHolder.recordPlayIv.setImageResource(R.drawable.msg_btn_stop_white);
                        mHolder.animIv.setImageResource(R.drawable.anim_tape_record_playing_white);
                        AnimationDrawable animationDrawable = (AnimationDrawable) mHolder
                                .animIv.getDrawable();
                        animationDrawable.start();

                    } else {
                        mHolder.recordPlayIv.setImageResource(R.drawable.msg_btn_play_white);
                        if (mHolder.animIv.getDrawable() instanceof AnimationDrawable) {
                            AnimationDrawable animationDrawable = (AnimationDrawable) mHolder
                                    .animIv.getDrawable();
                            if (animationDrawable != null) {
                                animationDrawable.stop();
                            }
                        }
                        mHolder.animIv.setImageResource(R.drawable.voice_play_white_07);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //右侧录音
    @Override
    protected void setRightTapeHolder(RightTapeHolder rightTapeHolder, int position) {
        FileInfo fileInfo = msgList.get(position).getFileInfo();
        String durationStr = TimeUtil.formatTimeStr(fileInfo.getDuration());
        rightTapeHolder.durationTv.setText(durationStr);

        if (fileInfo.getTapePlayingMark() == 1) {
            rightTapeHolder.stateIv.setImageResource(R.drawable.msg_btn_stop);
            rightTapeHolder.animIv.setImageResource(R.drawable.anim_tape_record_playing);
            AnimationDrawable animationDrawable = (AnimationDrawable) rightTapeHolder.animIv.getDrawable();
            animationDrawable.start();

        } else {
            rightTapeHolder.stateIv.setImageResource(R.drawable.msg_btn_play);
            if (rightTapeHolder.animIv.getDrawable() instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) rightTapeHolder
                        .animIv.getDrawable();
                if (animationDrawable != null) {
                    animationDrawable.stop();
                }
            }
            rightTapeHolder.animIv.setImageResource(R.drawable.voice_play_07);
        }
    }

    //左侧文字+@
    @Override
    protected void setLeftTextHolder(LeftTextHolder leftTextHolder, int position) {
        if (TextUtils.isEmpty(msgList.get(position).getContent())) {
            return;
        }
        leftTextHolder.contentTv.setMovementMethod(CustomLinkMovementMethod.getInstance());
        SpannableString spanStr = new SpannableString(msgList.get(position).getContent());
        if (ChatMsg.TYPE_CONTENT_AT == msgList.get(position).getContentType()
                && msgList.get(position).getAtFriendMap() != null
                && !msgList.get(position).getAtFriendMap().isEmpty()
                && ChatRoom.TYPE_GROUP.equals(msgList.get(position).getRoomType())) {
            spanStr = HyperLinkUtil.getATSpanStr(context, spanStr, msgList.get(position).getAtFriendMap(),
                    R.color.blue_main, msgList.get(position).getRoomId());
        }
        if (msgList.get(position).getEmoMark() == 1) {
            spanStr = EmoticonUtil.getEmoSpanStr(context, spanStr);
        }
        if (msgList.get(position).getPhoneMark() == 1) {
            spanStr = HyperLinkUtil.getPhoneSpanStr(context, spanStr, R.color.blue_main);
        }
        if (msgList.get(position).getUrlMark() == 1) {
            spanStr = HyperLinkUtil.getLinkSpanStr(context, spanStr, R.color.blue_main);
        }
        leftTextHolder.contentTv.setText(spanStr);
    }

    //左侧语音通话
    @Override
    protected void setLeftCallHolder(LeftCallHolder leftCallHolder, int position) {
        switch (msgList.get(position).getVoiceCall().getConnectState()) {
            case VoiceCall.REFUSE:
                leftCallHolder.callStateTv.setText(context.getString(R.string.refused));
                break;

            case VoiceCall.CANCEL:
                leftCallHolder.callStateTv.setText(context.getString(R.string.other_side_canceled));
                break;

            case VoiceCall.FINISH:
                leftCallHolder.callStateTv.setText(context.getString(R.string.call_finish)
                        + TimeUtil.formatTimeStr(msgList.get(position).getVoiceCall().getDuration()));
                break;

            case VoiceCall.TIME_OUT:
                leftCallHolder.callStateTv.setText(context.getString(R.string.you_not_accept));
                break;

            default:
                leftCallHolder.callStateTv.setText(context.getString(R.string.other_side_canceled));
                break;
        }
    }

    //左侧图片
    @Override
    protected void setLeftImgHolder(LeftImgHolder leftImgHolder, int position) {
        FileInfo fileInfo = msgList.get(position).getFileInfo();
        String filePath = "";
        if (!TextUtils.isEmpty(fileInfo.getOriginPath())) {
            filePath = fileInfo.getOriginPath();
        } else if (!TextUtils.isEmpty(fileInfo.getSavePath())) {
            filePath = fileInfo.getSavePath();
        } else if (!TextUtils.isEmpty(fileInfo.getDownloadPath())) {
            filePath = fileInfo.getDownloadPath();
        }
        ImgUtil.load(context, filePath, leftImgHolder.imgIv, R.drawable.con_img_vertical);
    }

    //左侧视频
    @Override
    protected void setLeftVideoHolder(LeftVideoHolder leftVideoHolder, int position) {
        FileInfo fileInfo = msgList.get(position).getFileInfo();
        if (!TextUtils.isEmpty(fileInfo.getOriginPath())) {
            ImgUtil.load(context, fileInfo.getOriginPath(), leftVideoHolder.videoIv, R.drawable.con_img_vertical);
        } else if (!TextUtils.isEmpty(fileInfo.getSavePath())) {
            ImgUtil.load(context, fileInfo.getSavePath(), leftVideoHolder.videoIv, R.drawable.con_img_vertical);
        } else if (!TextUtils.isEmpty(fileInfo.getDownloadPath())) {
            ImgUtil.load(context, fileInfo.getDownloadPath(), leftVideoHolder.videoIv, R.drawable.con_img_vertical);
        }
    }

    //左侧文档+音乐
    @Override
    protected void setLeftDocHolder(LeftDocHolder leftDocHolder, int position) {
        FileInfo fileInfo = msgList.get(position).getFileInfo();
        String nameStr = fileInfo.getFileName();
        String name = nameStr.substring(0, nameStr.lastIndexOf("."));
        String type = nameStr.substring(nameStr.lastIndexOf("."), nameStr.length());
        leftDocHolder.nameTv.setText(name);
        leftDocHolder.typeTv.setText(type);
        leftDocHolder.sizeTv.setText(FileUtil.formatFileLength(fileInfo.getFileLength()));

        switch (type) {
            case ".txt":
                leftDocHolder.iconIv.setImageResource(R.drawable.icon_txt);
                break;

            case ".doc":
            case ".docx":
                leftDocHolder.iconIv.setImageResource(R.drawable.icon_word);
                break;

            case ".xls":
            case ".xlsx":
                leftDocHolder.iconIv.setImageResource(R.drawable.icon_excel);
                break;

            case ".ppt":
            case ".pptx":
                leftDocHolder.iconIv.setImageResource(R.drawable.icon_ppt);
                break;

            case ".pdf":
                leftDocHolder.iconIv.setImageResource(R.drawable.icon_pdf);
                break;

            case ".mp3":
            case ".mp4":
                leftDocHolder.iconIv.setImageResource(R.drawable.icon_music);
                break;

            default:
                break;

        }
    }

    //左侧录音
    @Override
    protected void setLeftTapeHolder(LeftTapeHolder leftTapeHolder, int position) {
        FileInfo fileInfo = msgList.get(position).getFileInfo();
        String durationStr = TimeUtil.formatTimeStr(fileInfo.getDuration());
        leftTapeHolder.durationTv.setText(durationStr);

        if (fileInfo.getTapePlayingMark() == 1) {
            leftTapeHolder.stateIv.setImageResource(R.drawable.msg_btn_stop);
            leftTapeHolder.animIv.setImageResource(R.drawable.anim_tape_record_playing);
            AnimationDrawable animationDrawable = (AnimationDrawable) leftTapeHolder
                    .animIv.getDrawable();
            animationDrawable.start();

        } else {
            leftTapeHolder.stateIv.setImageResource(R.drawable.msg_btn_play);
            if (leftTapeHolder.animIv.getDrawable() instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) leftTapeHolder
                        .animIv.getDrawable();
                if (animationDrawable != null) {
                    animationDrawable.stop();
                }
            }
            leftTapeHolder.animIv.setImageResource(R.drawable.voice_play_07);
        }
    }

    @Override
    protected void setLeftReplyHolder(LeftReplyHolder mHolder, int position) {
        ChatMsg chatMsg = msgList.get(position);
        ReplyMsgInfo replyMsgInfo = chatMsg.getReplyMsgInfo();
        FileInfo fileInfo = replyMsgInfo.getFileInfo();
        mHolder.replyNameTv.setText(NameUtil.getName(replyMsgInfo.getSenderInfo().getId()));
        mHolder.replyContentTv.setVisibility(View.VISIBLE);
        //文本
        mHolder.replyContentTv.setMovementMethod(CustomLinkMovementMethod.getInstance());
        SpannableString spanStr = new SpannableString(msgList.get(position).getContent());
        if ( msgList.get(position).getAtFriendMap() != null
                && !msgList.get(position).getAtFriendMap().isEmpty()
                && ChatRoom.TYPE_GROUP.equals(msgList.get(position).getRoomType())) {
            spanStr = HyperLinkUtil.getATSpanStr(context, spanStr, msgList.get(position).getAtFriendMap(),
                    R.color.blue_main, msgList.get(position).getRoomId());
        }
        if (msgList.get(position).getEmoMark() == 1) {
            spanStr = EmoticonUtil.getEmoSpanStr(context, spanStr);
        }
        if (msgList.get(position).getPhoneMark() == 1) {
            spanStr = HyperLinkUtil.getPhoneSpanStr(context, spanStr, R.color.blue_main);
        }
        if (msgList.get(position).getUrlMark() == 1) {
            spanStr = HyperLinkUtil.getLinkSpanStr(context, spanStr, R.color.blue_main);
        }

        mHolder.replyContentTv.setText(spanStr);
        mHolder.replyStateIv.setVisibility(View.GONE);
        mHolder.recordLl.setVisibility(View.GONE);
        mHolder.replyTypeTv.setVisibility(View.VISIBLE);
        mHolder.replyVideoIv.setVisibility(View.GONE);
        if (fileInfo == null) {
            //回复文本消息
            mHolder.replyIv.setVisibility(View.GONE);
            mHolder.replyTypeTv.setMovementMethod(CustomLinkMovementMethod.getInstance());
            SpannableString replyStr = new SpannableString(replyMsgInfo.getContent());
            replyStr = EmoticonUtil.getEmoSpanStr(context, replyStr);
            replyStr = HyperLinkUtil.getPhoneSpanStr(context, replyStr, R.color.blue_main);
            replyStr = HyperLinkUtil.getLinkSpanStr(context, replyStr, R.color.blue_main);
            mHolder.replyTypeTv.setText(replyStr);
        } else {
            mHolder.replyIv.setVisibility(View.VISIBLE);
            String filePath = "";
            switch (fileInfo.getFileType()) {
                case FileInfo.TYPE_IMG:
                case FileInfo.TYPE_VIDEO:
                    //回复图片
                    if (!TextUtils.isEmpty(fileInfo.getOriginPath())) {
                        filePath = fileInfo.getOriginPath();
                    } else if (!TextUtils.isEmpty(fileInfo.getSavePath())) {
                        filePath = fileInfo.getSavePath();
                    } else if (!TextUtils.isEmpty(fileInfo.getDownloadPath())) {
                        filePath = fileInfo.getDownloadPath();
                    }
                    ImgUtil.load(context, filePath, mHolder.replyIv, R.drawable.con_img_vertical);
                    if (FileInfo.TYPE_VIDEO == fileInfo.getFileType()) {
                        mHolder.replyTypeTv.setText(context.getString(R.string.video));
                        mHolder.replyVideoIv.setVisibility(View.VISIBLE);
                    } else {
                        mHolder.replyTypeTv.setText(context.getString(R.string.image));
                    }
                    break;
                case FileInfo.TYPE_DOC:
                case FileInfo.TYPE_MUSIC:
                    String nameStr = fileInfo.getFileName();
                    String type = nameStr.substring(nameStr.lastIndexOf("."), nameStr.length());
                    FileUtil.imageTypeView(type, mHolder.replyIv);
                    if (fileInfo.getFileType() == FileInfo.TYPE_MUSIC) {
                        mHolder.replyTypeTv.setText(context.getString(R.string.musicInfo));
                    } else {
                        mHolder.replyTypeTv.setText(context.getString(R.string.document));
                    }
                    //左侧 文档下载状态
                    if (fileInfo.getDownloadState()==FileInfo.TYPE_DOWNLOAD_NOT){
                        mHolder.replyStateIv.setVisibility(View.VISIBLE);
                        mHolder.replyStateIv.setImageResource(R.drawable.file_download_black);
                        mHolder.loadingIv.setVisibility(View.GONE);
                        mHolder.loadingIv.clearAnimation();
                    }else if(fileInfo.getDownloadState()==FileInfo.TYPE_DOWNLOADING){
                        mHolder.replyStateIv.setVisibility(View.VISIBLE);
                        mHolder.replyStateIv.setImageResource(R.drawable.file_download_off_black);
                        mHolder.loadingIv.setVisibility(View.VISIBLE);
                        mHolder.loadingIv.startAnimation(AnimationUtils.loadAnimation(context,
                                R.anim.rotate360_anim));
                    }else if(fileInfo.getDownloadState()==FileInfo.TYPE_DOWNLOAD_SUCCESS){
                        mHolder.replyStateIv.setVisibility(View.GONE);
                        mHolder.loadingIv.setVisibility(View.GONE);
                        mHolder.loadingIv.clearAnimation();
                    }else if(fileInfo.getDownloadState()==FileInfo.TYPE_DOWNLOAD_FAIL){
                        mHolder.replyStateIv.setVisibility(View.VISIBLE);
                        mHolder.replyStateIv.setImageResource(R.drawable.file_download_again_black);
                        mHolder.loadingIv.setVisibility(View.GONE);
                        mHolder.loadingIv.clearAnimation();
                    }
                    break;
                case FileInfo.TYPE_TAPE_RECORD:
                    mHolder.replyIv.setVisibility(View.GONE);
                    mHolder.replyTypeTv.setVisibility(View.GONE);
                    mHolder.recordLl.setVisibility(View.VISIBLE);
                    String durationStr = TimeUtil.formatTimeStr(fileInfo.getDuration());
                    mHolder.recordTimeTv.setText(durationStr);
                    if (fileInfo.getTapePlayingMark() == 1) {
                        mHolder.recordPlayIv.setImageResource(R.drawable.msg_btn_stop_white);
                        mHolder.animIv.setImageResource(R.drawable.anim_tape_record_playing_white);
                        AnimationDrawable animationDrawable = (AnimationDrawable) mHolder
                                .animIv.getDrawable();
                        animationDrawable.start();

                    } else {
                        mHolder.recordPlayIv.setImageResource(R.drawable.msg_btn_play_white);
                        if (mHolder.animIv.getDrawable() instanceof AnimationDrawable) {
                            AnimationDrawable animationDrawable = (AnimationDrawable) mHolder
                                    .animIv.getDrawable();
                            if (animationDrawable != null) {
                                animationDrawable.stop();
                            }
                        }
                        mHolder.animIv.setImageResource(R.drawable.voice_play_white_07);
                    }
                    break;
                default:
                    break;
            }
        }
    }


}
