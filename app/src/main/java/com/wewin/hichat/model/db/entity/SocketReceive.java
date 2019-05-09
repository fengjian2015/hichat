package com.wewin.hichat.model.db.entity;


/**
 * Created by Darren on 2018/12/22.
 */
public class SocketReceive {

    private int code;
    private String type;
    private ChatMsg remote;
    private SocketServer server;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ChatMsg getRemote() {
        return remote;
    }

    public void setRemote(ChatMsg remote) {
        this.remote = remote;
    }

    public SocketServer getServer() {
        return server;
    }

    public void setServer(SocketServer server) {
        this.server = server;
    }

    @Override
    public String toString() {
        return "SocketReceive{" +
                "code=" + code +
                ", type='" + type + '\'' +
                ", remote=" + remote +
                ", server=" + server +
                '}';
    }
}
