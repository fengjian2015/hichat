package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.HyperLinkUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.androidlib.utils.EmoticonUtil;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.component.base.BaseRcvAdapter;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.GroupDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FileInfo;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;

import java.util.List;

/**
 * Created by Darren on 2018/12/17.
 */
public class ConversationListRcvAdapter extends BaseRcvAdapter {

    private Context context;
    private List<ChatRoom> roomList;
    private boolean isEditMode;

    public ConversationListRcvAdapter(Context context, List<ChatRoom> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    public void setEditMode(boolean state) {
        isEditMode = state;
    }

    @Override
    protected Context getContext() {
        return context;
    }

    @Override
    protected List getObjectList() {
        return roomList;
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(int viewType) {
        return new ItemViewHolder(View.inflate(context, R.layout.item_conversation_list, null));
    }

    @Override
    protected void bindViewData(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder && !roomList.isEmpty()) {
            ItemViewHolder iHolder = (ItemViewHolder) holder;

            ChatRoom chatRoom = roomList.get(position);
            if (chatRoom == null) {
                return;
            }
            String roomType = chatRoom.getRoomType();
            String roomName = "";
            String roomNote = "";
            String roomAvatar = "";

            if (ChatRoom.TYPE_GROUP.equals(roomType)) {
                GroupInfo groupInfo = GroupDao.getGroup(chatRoom.getRoomId());
                if (groupInfo != null) {
                    roomName = groupInfo.getGroupName();
                    roomAvatar = groupInfo.getGroupAvatar();
                }
                iHolder.temporaryChatTag.setVisibility(View.GONE);

            } else {
                FriendInfo contactUser = ContactUserDao.getContactUser(chatRoom.getRoomId());
                if (contactUser != null) {
                    roomName = contactUser.getUsername();
                    roomNote = contactUser.getFriendNote();
                    roomAvatar = contactUser.getAvatar();
                }
                FriendInfo friendInfo = FriendDao.getFriendInfo(chatRoom.getRoomId());
                if (friendInfo == null || friendInfo.getFriendship() == 0) {
                    iHolder.temporaryChatTag.setVisibility(View.VISIBLE);
                } else {
                    iHolder.temporaryChatTag.setVisibility(View.GONE);
                }
            }

            ImgUtil.load(context, roomAvatar, iHolder.avatarCiv);
            if (!TextUtils.isEmpty(roomNote)) {
                iHolder.nameTv.setText(roomNote);
            } else {
                iHolder.nameTv.setText(roomName);
            }

            if (TimeUtil.isSameDay(chatRoom.getLastMsgTime(), TimeUtil.getServerTimestamp())) {
                iHolder.timeTv.setText(TimeUtil.timestampToStr(chatRoom.getLastMsgTime(), "HH:mm"));
            } else {
                iHolder.timeTv.setText(TimeUtil.getFormatDate(chatRoom.getLastMsgTime()));
            }

            if (isEditMode) {
                iHolder.checkIv.setVisibility(View.VISIBLE);
                iHolder.checkIv.setSelected(roomList.get(position).isChecked());
            } else {
                iHolder.checkIv.setVisibility(View.GONE);
            }

            if (roomList.get(position).getTopMark() == 1) {
                iHolder.topMarkIv.setVisibility(View.VISIBLE);
            } else {
                iHolder.topMarkIv.setVisibility(View.INVISIBLE);
            }

            int unreadNum = roomList.get(position).getUnreadNum();
            if (roomList.get(position).getShieldMark() == 1) {
                iHolder.shieldAlphaIv.setVisibility(View.VISIBLE);
                iHolder.shieldIconIv.setVisibility(View.VISIBLE);

                if (unreadNum == 0) {
                    iHolder.unreadNumTv.setVisibility(View.INVISIBLE);

                } else if (unreadNum < 100) {
                    iHolder.unreadNumTv.setVisibility(View.VISIBLE);
                    iHolder.unreadNumTv.setText(unreadNum + "");
                    iHolder.unreadNumTv.setBackgroundResource(R.drawable.corner_gray_18);

                } else {
                    iHolder.unreadNumTv.setVisibility(View.VISIBLE);
                    iHolder.unreadNumTv.setText("");
                    iHolder.unreadNumTv.setBackgroundResource(R.drawable.con_news_shield);
                }

            } else {
                iHolder.shieldAlphaIv.setVisibility(View.GONE);
                iHolder.shieldIconIv.setVisibility(View.GONE);

                if (unreadNum == 0) {
                    iHolder.unreadNumTv.setVisibility(View.INVISIBLE);

                } else if (unreadNum < 100) {
                    iHolder.unreadNumTv.setVisibility(View.VISIBLE);
                    iHolder.unreadNumTv.setBackgroundResource(R.drawable.corner_blue_18);
                    iHolder.unreadNumTv.setText(unreadNum + "");

                } else {
                    iHolder.unreadNumTv.setVisibility(View.VISIBLE);
                    iHolder.unreadNumTv.setText("");
                    iHolder.unreadNumTv.setBackgroundResource(R.drawable.con_news_normal);
                }
            }

            if (chatRoom.getAtType() == ChatMsg.TYPE_AT_NORMAL) {
                iHolder.atTagTv.setVisibility(View.GONE);

            } else if (chatRoom.getAtType() == ChatMsg.TYPE_AT_SINGLE) {
                iHolder.atTagTv.setVisibility(View.VISIBLE);
                iHolder.atTagTv.setText(R.string.at_me);

            } else if (chatRoom.getAtType() == ChatMsg.TYPE_AT_ALL) {
                iHolder.atTagTv.setVisibility(View.VISIBLE);
                iHolder.atTagTv.setText(R.string.all_members);
            }

            ChatMsg lastMsg = chatRoom.getLastChatMsg();
            if (lastMsg == null) {
                iHolder.contentTv.setText("");

            } else {
                if (ChatMsg.TYPE_CONTENT_FILE == lastMsg.getContentType() && lastMsg.getFileInfo() != null) {
                    FileInfo resource = lastMsg.getFileInfo();
                    switch (resource.getFileType()) {
                        case FileInfo.TYPE_IMG:
                            iHolder.contentTv.setText("[" + context.getString(R.string.image) + "]");
                            break;

                        case FileInfo.TYPE_VIDEO:
                            iHolder.contentTv.setText("[" + context.getString(R.string.video) + "]");
                            break;

                        case FileInfo.TYPE_DOC:
                            iHolder.contentTv.setText("[" + context.getString(R.string.file) + "]");
                            break;

                        case FileInfo.TYPE_MUSIC:
                            iHolder.contentTv.setText("[" + context.getString(R.string.music) + "]");
                            break;

                        case FileInfo.TYPE_TAPE_RECORD:
                            iHolder.contentTv.setText("[" + context.getString(R.string.audio) + "]");
                            break;

                        default:
                            if (!TextUtils.isEmpty(lastMsg.getContentDesc())) {
                                iHolder.contentTv.setText("[" + lastMsg.getContentDesc() + "]");

                            } else {
                                iHolder.contentTv.setText("[版本不支持]");
                            }
                            break;
                    }

                } else if (lastMsg.getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL) {
                    iHolder.contentTv.setText("[" + context.getString(R.string.voice_calls) + "]");

                } else {
                    SpannableString spanStr = new SpannableString(lastMsg.getContent());
                    if (lastMsg.getEmoMark() == 1) {
                        spanStr = EmoticonUtil.getEmoSpanStr(context, spanStr);
                    }
                    iHolder.contentTv.setText(spanStr);
                }
            }

            iHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.itemClick(position);
                }
            });

        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarCiv, topMarkIv, shieldAlphaIv, shieldIconIv, checkIv;
        private TextView nameTv, contentTv, timeTv, unreadNumTv, temporaryChatTag, atTagTv;

        private ItemViewHolder(View itemView) {
            super(itemView);
            avatarCiv = itemView.findViewById(R.id.iv_item_conversation_avatar);
            nameTv = itemView.findViewById(R.id.tv_item_conversation_name);
            contentTv = itemView.findViewById(R.id.tv_item_conversation_message_content);
            timeTv = itemView.findViewById(R.id.tv_item_conversation_time);
            unreadNumTv = itemView.findViewById(R.id.tv_item_conversation_unread_msg_num);
            checkIv = itemView.findViewById(R.id.iv_item_conversation_check);
            topMarkIv = itemView.findViewById(R.id.iv_item_conversation_top_mark);
            shieldAlphaIv = itemView.findViewById(R.id.iv_item_conversation_shield_alpha);
            shieldIconIv = itemView.findViewById(R.id.iv_item_conversation_shield_icon);
            temporaryChatTag = itemView.findViewById(R.id.tv_item_conversation_temporary_chat_tag);
            atTagTv = itemView.findViewById(R.id.tv_item_conversation_message_server);
        }
    }

}
