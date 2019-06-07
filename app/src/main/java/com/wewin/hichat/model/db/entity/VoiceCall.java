package com.wewin.hichat.model.db.entity;

import java.io.Serializable;

/**
 * 语音通话
 * @author Darren
 * Created by Darren on 2019/2/14
 */
public class VoiceCall implements Serializable {

    public static final int INVITE = 0;//邀请
    public static final int CONNECT = 1;//接通
    public static final int REFUSE = 2;//拒绝
    public static final int CANCEL = 3;//取消
    public static final int FINISH = 4;//结束
    public static final int TIME_OUT = 5;//超时
    public static final int BUSY = 6;//用户正忙

    private String inviteUserId;//发起邀请者id
    private String channel;//通话频道号
    private int connectState;//是否接通语音通话, 0:邀请; 1:接通; 2:拒绝, 3:取消, 4:结束, 5:超时, 6:用户正忙
    private long duration;

    public VoiceCall() {}

    public VoiceCall(String inviteUserId, String channel, int connectState, long duration) {
        this.inviteUserId = inviteUserId;
        this.channel = channel;
        this.connectState = connectState;
        this.duration = duration;
    }

    public VoiceCall(String channel, int connectState, long duration) {
        this.channel = channel;
        this.connectState = connectState;
        this.duration = duration;
    }

    public String getInviteUserId() {
        return inviteUserId;
    }

    public void setInviteUserId(String inviteUserId) {
        this.inviteUserId = inviteUserId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getConnectState() {
        return connectState;
    }

    public void setConnectState(int connectState) {
        this.connectState = connectState;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "VoiceCall{" +
                "inviteUserId='" + inviteUserId + '\'' +
                ", channel='" + channel + '\'' +
                ", connectState=" + connectState +
                ", duration=" + duration +
                '}';
    }

}
