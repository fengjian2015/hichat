package com.wewin.hichat.androidlib.utils;

import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.ServerConversation.UpdateConversation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darren on 2019/5/2
 **/
public class EntityUtil {


    public static List<ChatRoom> parseUpdateConversation(List<UpdateConversation> conversationList) {
        List<ChatRoom> roomList = new ArrayList<>();
        if (conversationList == null || conversationList.isEmpty()) {
            return roomList;
        }
        for (UpdateConversation conversation : conversationList) {
            ChatRoom chatRoom = new ChatRoom(conversation.getConversationId(),
                    conversation.getConversationType());
            chatRoom.setLastChatMsg(conversation.getLatestMessage());
            if (chatRoom.getLastChatMsg() != null){
                chatRoom.getLastChatMsg().setRoomId(chatRoom.getRoomId());
                chatRoom.getLastChatMsg().setRoomType(chatRoom.getRoomType());
                chatRoom.setLastMsgTime(chatRoom.getLastChatMsg().getCreateTimestamp());
            }
            chatRoom.setAddMark(conversation.getAddMark());
            chatRoom.setAtMark(conversation.getAitMark());
            chatRoom.setAtType(conversation.getAitType());
            chatRoom.setDeleteMark(conversation.getDeleteMark());
            chatRoom.setFriendshipMark(conversation.getFriendshipMark());
            chatRoom.setShieldMark(conversation.getShieldMark());
            chatRoom.setTopMark(conversation.getTopMark());
            chatRoom.setUnreadNum(conversation.getUnreadNum());
            chatRoom.setUpdateMark(conversation.getUpdateMark());
            chatRoom.setUnSyncMsgFirstId(conversation.getUnSyncMsgFirstId());
            roomList.add(chatRoom);
        }
        return roomList;
    }

}
