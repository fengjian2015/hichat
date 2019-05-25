package com.wewin.hichat;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.permission.AuthorizationCheck;
import com.wewin.hichat.androidlib.permission.Permission;
import com.wewin.hichat.androidlib.permission.PermissionCallback;
import com.wewin.hichat.androidlib.permission.Rigger;
import com.wewin.hichat.androidlib.umeng.UMMessage;
import com.wewin.hichat.androidlib.utils.ActivityUtil;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.ServiceUtil;
import com.wewin.hichat.androidlib.utils.SystemUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.androidlib.utils.VersionUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.androidlib.widget.MainViewPager;
import com.wewin.hichat.component.adapter.MainVpAdapter;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.CallSmallDialog;
import com.wewin.hichat.component.dialog.PromptDialog;
import com.wewin.hichat.component.dialog.VersionDialog;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.component.manager.VoiceCallManager;
import com.wewin.hichat.component.service.ChatSocketService;
import com.wewin.hichat.component.service.DownApkService;
import com.wewin.hichat.model.db.dao.ChatRoomDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.MessageDao;
import com.wewin.hichat.model.db.dao.MessageSendingDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.LoginUser;
import com.wewin.hichat.model.db.entity.VersionBean;
import com.wewin.hichat.model.http.HttpMore;
import com.wewin.hichat.view.contact.ContactFragment;
import com.wewin.hichat.view.conversation.ConversationFragment;
import com.wewin.hichat.view.more.MoreFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.wewin.hichat.component.service.ChatSocketService.NAME_CHAT_SERVICE;

/**
 * @author Darren
 * Created by Darren on 2018/12/13.
 */
public class MainActivity extends BaseActivity {

    private MainViewPager mainViewPager;
    public FrameLayout botContainerFl;
    private long exitTime = 0;
    private LinearLayout conversationLl, contactLl, moreLl;
    private ImageView conversationIv, contactIv, moreIv;
    private TextView conversationTv, contactTv, moreTv, unreadNumTv, notifyPointTv;
    private VersionDialog.VersionBuilder versionBuilder;
    private VersionBean versionBean;

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
        getWindow().setBackgroundDrawable(null);
        UserDao.user = UserDao.getUser();
        LogUtil.i("loginUser", UserDao.user);

        initViewPager();
        startChatSocketService();

        getPersonalInfo();
        getServiceTime();
        getUnreadNotifyCount();

        //当ChatRoomActivity未正常关闭走onDestroy时，当前正在聊天的roomId还会有值，需要清空
        ChatRoomManager.clearRoomInfo();

        applyStoragePermission();

        setUnreadNumView();
        setNotifyPointView();

//        SystemUtil.notifyMediaStoreRefresh(getAppContext(), FileUtil.getSDDirPath(getAppContext()));

        //每次启动将未发送成功的消息置为失败，并清空发送中的消息
        MessageDao.updateSendStateFail();
        MessageSendingDao.clear();

        if (SpCons.getLoginState(getAppContext())) {
            UMMessage.getInstance().setAlias();
        }

        checkVersion();

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

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        if (DownApkService.isDownload) {
            if (versionBean == null) {
                String version = SpCons.getString(getHostActivity(), SpCons.VERSION_CONTENT);
                if (!TextUtils.isEmpty(version)) {
                    versionBean = JSON.parseObject(version, VersionBean.class);
                }
            }
            if (versionBuilder != null && !versionBuilder.isShow()) {
                versionDialog();
            }
        }
        super.onResume();
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
                        FileUtil.createDir(FileUtil.getSDDirPath(getAppContext()));
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

    /**
     * 检查版本更新
     */
    public void checkVersion() {
        HttpMore.checkVersion(VersionUtil.getVersionCode(getAppContext()),
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            versionBean = JSON.parseObject(data.toString(), VersionBean.class);
                            if (versionBean != null) {
                                SpCons.setString(getAppContext(), SpCons.VERSION_CONTENT, data.toString());
                                if (VersionUtil.getVersionCode(getHostActivity()) < versionBean.getVersionCode()) {
                                    versionDialog();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void getPersonalInfo() {
        HttpMore.getPersonalInfo(getAppContext(), new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
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
                        SpCons.setUser(getAppContext(), loginUser);
                        LogUtil.i("getPersonalInfo", loginUser);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getUnreadNotifyCount() {
        HttpMore.getNotifyUnreadCount(new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    JSONObject object = JSON.parseObject(data.toString());
                    int unreadCount = object.getInteger("unreadCount");
                    if (unreadCount > 0) {
                        SpCons.setNotifyRedPointVisible(getHostActivity(), true);
                    } else {
                        SpCons.setNotifyRedPointVisible(getHostActivity(), false);
                    }
                    setNotifyPointView();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 版本弹窗
     */
    private void versionDialog() {
        if (!ActivityUtil.isActivityOnTop(MainActivity.this) || versionBean == null) {
            return;
        }
        if (versionBuilder == null) {
            versionBuilder = new VersionDialog.VersionBuilder(this);
        }
        if (versionBean.getStatus() == 0) {
            versionBuilder.setCancelVisible(true);
        } else {
            versionBuilder.setCancelVisible(false);
        }
        if (versionBean.getStatus() != 0 && DownApkService.isDownload) {
            versionBuilder.setDownState(DownApkService.ON_PREPARE);
        } else {
            versionBuilder.setDownState(-1);
        }
        versionBuilder.setPromptContent(versionBean.getIntroduction())
                .setOnConfirmClickListener(new VersionDialog.VersionBuilder.OnConfirmClickListener() {
                    @Override
                    public void confirmClick() {
                        if (versionBuilder.getDownloadState() == DownApkService.ON_COMPLETE) {
                            File file = new File(FileUtil.getSDApkPath(MainActivity.this), "hichat" + versionBean.getVersion() + ".apk");
                            VersionUtil.install(MainActivity.this, file);
                            return;
                        }
                        checkApkPermissions();
                        if (versionBean.getStatus() != 0) {

                        } else {
                            versionBuilder.dismiss();
                        }
                    }
                })
                .setOnCancelClickListener(new VersionDialog.VersionBuilder.OnCancelClickListener() {
                    @Override
                    public void cancelClick() {
                        if (versionBean.getStatus() != 0) {
                            if (!DownApkService.isDownload) {
                                MainActivity.this.finish();
                                System.exit(0);
                            } else {
                                try {
                                    Intent home = new Intent(Intent.ACTION_MAIN);
                                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    home.addCategory(Intent.CATEGORY_HOME);
                                    startActivity(home);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                })
                .setCancelableOnTouchOutside(false)
                .create()
                .show();
    }

    /**
     * 检查权限
     */
    private void checkApkPermissions() {
        if (Build.VERSION.SDK_INT >= 26) {
            boolean b = getPackageManager().canRequestPackageInstalls();
            if (b) {
                downApk();
            } else {
                Rigger.on(MainActivity.this)
                        .isShowDialog(true)
                        .permissions(Permission.REQUEST_INSTALL_PACKAGES, Permission.WRITE_EXTERNAL_STORAGE)
                        .start(new PermissionCallback() {
                            @Override
                            public void onGranted() {
                                downApk();
                            }

                            @Override
                            public void onDenied(HashMap<String, Boolean> permissions) {
                                new PromptDialog.PromptBuilder(MainActivity.this)
                                        .setPromptContent(getString(R.string.install_prompt))
                                        .setOnConfirmClickListener(new PromptDialog.PromptBuilder.OnConfirmClickListener() {
                                            @Override
                                            public void confirmClick() {
                                                AuthorizationCheck.openApplication(MainActivity.this);
                                            }
                                        })
                                        .setOnCancelClickListener(new PromptDialog.PromptBuilder.OnCancelClickListener() {
                                            @Override
                                            public void cancelClick() {
                                                CallSmallDialog.getInstance().setOpenWindow(true);
                                                finish();
                                            }
                                        })
                                        .setCancelVisible(true)
                                        .create()
                                        .show();
                            }
                        });
            }
        } else {
            downApk();
        }
    }

    /**
     * 下载或者直接安装
     */
    private void downApk() {
        //不存在就启动服务
        Intent intent = new Intent(getAppContext(), DownApkService.class);
        intent.putExtra("url", versionBean.getAppDownloadUrl());
        intent.putExtra("versionName", versionBean.getVersion());
        startService(intent);

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
        switch (msg.getKey()) {
            case EventMsg.MORE_LOGOUT_FINISH:
                this.finish();
                break;

            case EventMsg.CONTACT_NOTIFY_REFRESH:
                setNotifyPointView();
                break;

            case EventMsg.CONVERSATION_UNREAD_NUM_REFRESH:
                setUnreadNumView();
                break;
            case EventMsg.DOWN_APK:
                if (versionBuilder != null) {
                    versionBuilder.setDownState((int) msg.getData());
                }
                break;
            default:
                break;
        }
    }

}
