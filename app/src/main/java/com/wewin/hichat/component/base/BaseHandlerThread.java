package com.wewin.hichat.component.base;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

/**
 * Created by Darren on 2018/12/21.
 */
public abstract class BaseHandlerThread extends HandlerThread implements Handler.Callback {

    private Handler uiHandler;
    private Handler workHandler;
    private final int MESSAGE_WORK = 0;
    public final int MESSAGE_UI = 1;

    protected BaseHandlerThread(String name, int priority) {
        super(name, priority);
    }

    protected abstract Object handleWorkData(Object obj);

    public void setUIHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }

    public void sendWorkData(Object obj){
        if (obj != null){
            Message message = Message.obtain(null, MESSAGE_WORK);
            message.obj = obj;
            if (workHandler != null){
                workHandler.sendMessage(message);
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MESSAGE_WORK){
            Object obj = msg.obj;
            if (obj != null){
                Object outputObject = handleWorkData(obj);
                if (outputObject != null){
                    Message message = Message.obtain(null, MESSAGE_UI);
                    message.obj = outputObject;
                    if (uiHandler != null){
                        uiHandler.sendMessage(message);
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        this.workHandler = new Handler(getLooper(), this);
    }

}
