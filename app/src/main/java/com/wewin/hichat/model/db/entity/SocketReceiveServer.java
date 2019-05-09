package com.wewin.hichat.model.db.entity;

/**
 * Created by Darren on 2019/4/23
 **/
public class SocketReceiveServer {

    private String action;
    private ServerData data;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ServerData getData() {
        return data;
    }

    public void setData(ServerData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SocketReceiveServer{" +
                "action='" + action + '\'' +
                ", data=" + data +
                '}';
    }

    public static class ServerData{
        private String groupId;
        private String localMsgId;
        private String msgId;
        private String receiverId;
        private String roomType;


        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getLocalMsgId() {
            return localMsgId;
        }

        public void setLocalMsgId(String localMsgId) {
            this.localMsgId = localMsgId;
        }

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverId) {
            this.receiverId = receiverId;
        }

        public String getRoomType() {
            return roomType;
        }

        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }

        @Override
        public String toString() {
            return "ServerData{" +
                    "groupId='" + groupId + '\'' +
                    ", localMsgId='" + localMsgId + '\'' +
                    ", msgId='" + msgId + '\'' +
                    ", receiverId='" + receiverId + '\'' +
                    ", roomType='" + roomType + '\'' +
                    '}';
        }
    }


}
