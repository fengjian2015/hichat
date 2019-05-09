package com.wewin.hichat;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.permission.Permission;
import com.wewin.hichat.androidlib.permission.PermissionCallback;
import com.wewin.hichat.androidlib.permission.Rigger;
import com.wewin.hichat.androidlib.umeng.UMMessage;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.ServiceUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.androidlib.widget.MainViewPager;
import com.wewin.hichat.component.adapter.MainVpAdapter;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.service.ChatSocketService;
import com.wewin.hichat.model.db.dao.ChatRoomDao;
import com.wewin.hichat.model.db.dao.MessageDao;
import com.wewin.hichat.model.db.dao.MessageSendingDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.LoginUser;
import com.wewin.hichat.model.http.HttpMore;
import com.wewin.hichat.view.contact.ContactFragment;
import com.wewin.hichat.view.conversation.ConversationFragment;
import com.wewin.hichat.view.more.MoreFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.wewin.hichat.component.service.ChatSocketService.NAME_CHAT_SERVICE;

/**
 * Created by Darren on 2018/12/13.
 */
public class MainActivity extends BaseActivity {

    private MainViewPager mainViewPager;
    public FrameLayout botContainerFl;
    private long exitTime = 0;
    private LinearLayout conversationLl, contactLl, moreLl;
    private ImageView conversationIv, contactIv, moreIv;
    private TextView conversationTv, contactTv, moreTv, unreadNumTv, notifyPointTv;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        setCenterTitle(0);
        mainViewPager = findViewById(R.id.vp_main_fragment_container);
        botContainerFl = findViewById(R.id.fl_main_bot_container);
        conversationLl = findViewById(R.id.ll_main_tab_conversation_container);
        conversationIv = findViewById(R.id.iv_main_tab_conversation);
        conversationTv = findViewById(R.id.tv_main_tab_conversation);
        contactLl = findViewById(R.id.ll_main_tab_contact_container);
        contactIv = findViewById(R.id.iv_main_tab_contact);
        contactTv = findViewById(R.id.tv_main_tab_contact);
        moreLl = findViewById(R.id.ll_main_tab_more_container);
        moreIv = findViewById(R.id.iv_main_tab_more);
        moreTv = findViewById(R.id.tv_main_tab_more);
        unreadNumTv = findViewById(R.id.tv_main_conversation_unread_num);
        notifyPointTv = findViewById(R.id.tv_main_notify_point);
    }

    @Override
    protected void initViewsData() {
        UserDao.user = UserDao.getUser();
        LogUtil.i("loginUser", UserDao.user);

        initViewPager();
        startChatSocketService();

        getPersonalInfo();
        getServiceTime();

        //当ChatRoomActivity未正常关闭走onDestroy时，当前正在聊天的roomId还会有值，需要清空
        SpCons.setCurrentRoomId(getAppContext(), "");
        SpCons.setCurrentRoomType(getAppContext(), "");

        applyStoragePermission();

        setUnreadNumView();
        setNotifyPointView();

        FileUtil.createDir(FileUtil.getSDDirPath(getAppContext()));
//        SystemUtil.notifyMediaStoreRefresh(getAppContext());

        //每次启动将未发送成功的消息置为失败，并清空发送中的消息
        MessageDao.updateSendStateFail();
        MessageSendingDao.clear();

        if (SpCons.getLoginState(getAppContext())){
            UMMessage.getInstance().setAlias();
        }
    }

    @Override
    protected void setListener() {
        conversationLl.setOnClickListener(this);
        contactLl.setOnClickListener(this);
        moreLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_main_tab_conversation_container:
                mainViewPager.setCurrentItem(0);
                conversationIv.setImageResource(R.drawable.icon_con_blue);
                conversationTv.setTextColor(getResources().getColor(R.color.blue_main));
                contactIv.setImageResource(R.drawable.icon_contact_gray);
                contactTv.setTextColor(getResources().getColor(R.color.gray_ac));
                moreIv.setImageResource(R.drawable.icon_more_gray);
                moreTv.setTextColor(getResources().getColor(R.color.gray_ac));
                break;

            case R.id.ll_main_tab_contact_container:
                mainViewPager.setCurrentItem(1);
                conversationIv.setImageResource(R.drawable.icon_con_gray);
                conversationTv.setTextColor(getResources().getColor(R.color.gray_ac));
                contactIv.setImageResource(R.drawable.icon_contact_blue);
                contactTv.setTextColor(getResources().getColor(R.color.blue_main));
                moreIv.setImageResource(R.drawable.icon_more_gray);
                moreTv.setTextColor(getResources().getColor(R.color.gray_ac));
                break;

            case R.id.ll_main_tab_more_container:
                mainViewPager.setCurrentItem(2);
                conversationIv.setImageResource(R.drawable.icon_con_gray);
                conversationTv.setTextColor(getResources().getColor(R.color.gray_ac));
                contactIv.setImageResource(R.drawable.icon_contact_gray);
                contactTv.setTextColor(getResources().getColor(R.color.gray_ac));
                moreIv.setImageResource(R.drawable.icon_more_blue);
                moreTv.setTextColor(getResources().getColor(R.color.blue_main));
                break;
        }
    }

    private void initViewPager() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new ConversationFragment());
        fragmentList.add(new ContactFragment());
        fragmentList.add(new MoreFragment());
        FragmentManager manager = getSupportFragmentManager();
        mainViewPager.setAdapter(new MainVpAdapter(manager, fragmentList));
        mainViewPager.setOffscreenPageLimit(2);
    }

    public void setUnreadNumView() {
        int unreadNum = ChatRoomDao.getTotalUnreadNum();
        if (unreadNum == 0) {
            unreadNumTv.setVisibility(View.INVISIBLE);
        } else if (unreadNum < 100) {
            unreadNumTv.setVisibility(View.VISIBLE);
            unreadNumTv.setBackgroundResource(R.drawable.corner_red_10);
            unreadNumTv.setText(String.valueOf(unreadNum));
        } else {
            unreadNumTv.setVisibility(View.VISIBLE);
            unreadNumTv.setText("");
            unreadNumTv.setBackgroundResource(R.drawable.con_num_mor);
        }
    }

    public void setNotifyPointView() {
        if (SpCons.getNotifyRedPointVisible(getAppContext())) {
            notifyPointTv.setVisibility(View.VISIBLE);
        } else {
            notifyPointTv.setVisibility(View.INVISIBLE);
        }
    }

    private void applyStoragePermission() {
        Rigger.on(this).permissions(Permission.WRITE_EXTERNAL_STORAGE, Permission.RECORD_AUDIO)
                .start(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        LogUtil.setWriteFileSwitch(true);
                        //删除前一天的log日志
                        LogUtil.clearTimeout();
                    }

                    @Override
                    public void onDenied(HashMap<String, Boolean> permissions) {
                        if (permissions.get(Permission.WRITE_EXTERNAL_STORAGE)) {
                            LogUtil.setWriteFileSwitch(true);
                            //删除前一天的log日志
                            LogUtil.clearTimeout();
                        }
                    }
                });

    }

    //启动ChatSocket服务
    private void startChatSocketService() {
        if (ServiceUtil.isServiceRunning(getAppContext(), NAME_CHAT_SERVICE)) {
            return;
        }
        Intent intent = new Intent(getAppContext(), ChatSocketService.class);
        intent.putExtra(LoginCons.EXTRA_LOGIN_USER_ID, UserDao.user.getId());
        startService(intent);
    }

    private void getServiceTime() {
        final long beforeTimestamp = System.currentTimeMillis();
        HttpMore.getServiceTimestamp(new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    long backTimestamp = System.currentTimeMillis();
                    long serviceTimestamp = Long.parseLong(data.toString());
                    LogUtil.i("getServiceTime", TimeUtil.timestampToStr(serviceTimestamp
                            + backTimestamp - beforeTimestamp));
                    TimeUtil.initTime(serviceTimestamp + backTimestamp - beforeTimestamp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(String desc) {
                TimeUtil.initTime(System.currentTimeMillis());
            }
        });
    }

    private void getPersonalInfo() {
        HttpMore.getPersonalInfo(new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    LoginUser loginUser = JSON.parseObject(data.toString(), LoginUser.class);
                    if (loginUser != null) {
                        UserDao.setUser(loginUser);
                        UserDao.user = loginUser;
                        LogUtil.i("getPersonalInfo", loginUser);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getAppContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        if (msg.getKey() == EventMsg.MORE_LOGOUT_FINISH) {
            this.finish();

        } else if (msg.getKey() == EventMsg.CONTACT_NOTIFY_REFRESH) {
            setNotifyPointView();
        }
    }

}
