package com.wewin.hichat.component.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.constant.SpCons;

/**
 * 日期修改广播监听
 * Created by Darren on 2019/2/1
 */
public class DateChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_DATE_CHANGED.equals(action)){
            LogUtil.i("ACTION_DATE_CHANGED");
            SpCons.setTimeChanged(context, true);
            SpCons.setSystemTimestampRight(context, false);
        }
    }
}
