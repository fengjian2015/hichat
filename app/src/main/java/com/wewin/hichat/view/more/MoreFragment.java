package com.wewin.hichat.view.more;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.MainActivity;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.utils.CommonUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.TransUtil;
import com.wewin.hichat.androidlib.utils.VersionUtil;
import com.wewin.hichat.component.base.BaseFragment;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.constant.HttpCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.PromptDialog;
import com.wewin.hichat.component.dialog.SexDialog;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.ImgUrl;
import com.wewin.hichat.model.db.entity.LoginUser;
import com.wewin.hichat.model.db.entity.VersionBean;
import com.wewin.hichat.model.http.HttpLogin;
import com.wewin.hichat.model.http.HttpMore;
import com.wewin.hichat.view.album.ImgShowActivity;
import com.wewin.hichat.view.conversation.DocViewActivity;
import com.wewin.hichat.view.login.LoginActivity;

import java.util.ArrayList;

/**
 * 主页-通知
 * Created by Darren on 2018/12/13.
 */
public class MoreFragment extends BaseFragment {

    private RelativeLayout personalRl;
    private ImageView avatarIv, notifyRedPointIv, audioRemindCheckIv, vibrateRemindCheckIv;
    private TextView nameTv, signTv, phoneNumTv, genderTv, logoutTv, versionTv, newTv;
    private FrameLayout audioRemindFl, vibrateRemindFl, notifyFl, modifyPwdFl, loginRecordFl,
            aboutFl, genderFl, checkFl;
    private SexDialog sexDialog;
    private int genderType;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_more;
    }

    @Override
    protected void initViews() {
        personalRl = parentView.findViewById(R.id.rl_more_personal_container);
        avatarIv = parentView.findViewById(R.id.civ_more_personal_avatar);
        nameTv = parentView.findViewById(R.id.tv_more_personal_name);
        signTv = parentView.findViewById(R.id.tv_more_personal_sign);
        phoneNumTv = parentView.findViewById(R.id.tv_more_personal_phone_num);
        genderTv = parentView.findViewById(R.id.tv_more_personal_gender);
        logoutTv = parentView.findViewById(R.id.tv_more_personal_logout);
        audioRemindFl = parentView.findViewById(R.id.fl_more_personal_audio_remind);
        vibrateRemindFl = parentView.findViewById(R.id.fl_more_personal_vibrate_remind);
        notifyFl = parentView.findViewById(R.id.fl_more_personal_notify);
        modifyPwdFl = parentView.findViewById(R.id.fl_more_personal_modify_pwd);
        loginRecordFl = parentView.findViewById(R.id.fl_more_personal_login_record);
        aboutFl = parentView.findViewById(R.id.fl_more_personal_about);
        genderFl = parentView.findViewById(R.id.fl_more_personal_gender_container);
        audioRemindCheckIv = parentView.findViewById(R.id.iv_more_personal_audio_remind);
        vibrateRemindCheckIv = parentView.findViewById(R.id.iv_more_personal_vibrate_remind);
        notifyRedPointIv = parentView.findViewById(R.id.iv_more_new_notify_res_point);
        versionTv = parentView.findViewById(R.id.tv_more_personal_version);
        newTv = parentView.findViewById(R.id.tv_more_personal_new);
        checkFl = parentView.findViewById(R.id.fl_more_personal_check);
    }

    @Override
    protected void initViewsData() {
        setViewData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (SpCons.getNotifyRedPointVisible(getHostActivity())) {
                notifyRedPointIv.setVisibility(View.VISIBLE);
            } else {
                notifyRedPointIv.setVisibility(View.INVISIBLE);
            }
            genderType = SpCons.getUser(getHostActivity()).getGender();
            getPersonalInfo();
        }
    }

    @Override
    protected void setListener() {
        personalRl.setOnClickListener(this);
        genderFl.setOnClickListener(this);
        audioRemindFl.setOnClickListener(this);
        vibrateRemindFl.setOnClickListener(this);
        notifyFl.setOnClickListener(this);
        modifyPwdFl.setOnClickListener(this);
        loginRecordFl.setOnClickListener(this);
        aboutFl.setOnClickListener(this);
        logoutTv.setOnClickListener(this);
        checkFl.setOnClickListener(this);
        avatarIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_more_personal_container:
                startActivity(new Intent(getActivity(), PersonalInfoModifyActivity.class));
                break;

            case R.id.fl_more_personal_gender_container:
                showSexDialog();
                break;

            case R.id.fl_more_personal_audio_remind:
                modifyAccountInfo(1 - SpCons.getUser(getHostActivity()).getAudioCues(), genderType,
                        SpCons.getUser(getHostActivity()).getVibratesCues());
                break;

            case R.id.fl_more_personal_vibrate_remind:
                modifyAccountInfo(SpCons.getUser(getHostActivity()).getAudioCues(), genderType,
                        1 - SpCons.getUser(getHostActivity()).getVibratesCues());
                break;

            case R.id.fl_more_personal_notify:
                startActivity(new Intent(getActivity(), NotifyActivity.class));
                break;

            case R.id.fl_more_personal_modify_pwd:
                startActivity(new Intent(getActivity(), PasswordModifyActivity.class));
                break;

            case R.id.fl_more_personal_login_record:
                startActivity(new Intent(getActivity(), LoginRecordActivity.class));
                break;

            case R.id.fl_more_personal_about:
                Intent intent = new Intent(getActivity(), DocViewActivity.class);
                intent.putExtra(ContactCons.EXTRA_MESSAGE_CHAT_WEB_PATH, HttpCons.PATH_MORE_ABOUT);
                startActivity(intent);
                break;

            case R.id.tv_more_personal_logout:
                showPromptDialog();
                break;
            case R.id.fl_more_personal_check:
                if (getHostActivity() instanceof MainActivity){
                    ((MainActivity)getHostActivity()).checkVersion();
                }
                break;
            case R.id.civ_more_personal_avatar:
                //多选则跳转图片展示
                Intent intent1 = new Intent(getHostActivity(), ImgShowActivity.class);
                intent1.putExtra(ImgUtil.IMG_CLICK_POSITION, 0);
                ArrayList<ImgUrl> mDataList = new ArrayList<>();
                ImgUrl imgUrl = new ImgUrl(SpCons.getUser(getHostActivity()).getAvatar());
                imgUrl.setFileName(SpCons.getUser(getHostActivity()).getId()+SpCons.getUser(getHostActivity()).getUsername()+".jpg");
                mDataList.add(imgUrl);
                intent1.putExtra(ImgUtil.IMG_LIST_KEY, mDataList);
                intent1.putExtra(ImgUtil.IMG_DONWLOAD, true);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    private void setViewData() {
        ImgUtil.load(getActivity(), SpCons.getUser(getHostActivity()).getAvatar(), avatarIv);
        nameTv.setText(SpCons.getUser(getHostActivity()).getUsername());
        signTv.setText(SpCons.getUser(getHostActivity()).getSign());
        phoneNumTv.setText(SpCons.getUser(getHostActivity()).getPhone());
        if (SpCons.getUser(getHostActivity()).getGender() == 0) {
            genderTv.setText(getString(R.string.female));
        } else if (SpCons.getUser(getHostActivity()).getGender() == 1) {
            genderTv.setText(getString(R.string.male));
        } else {
            genderTv.setText(getString(R.string.keep_secret));
        }
        audioRemindCheckIv.setSelected(TransUtil.intToBoolean(SpCons.getUser(getHostActivity()).getAudioCues()));
        vibrateRemindCheckIv.setSelected(TransUtil.intToBoolean(SpCons.getUser(getHostActivity()).getVibratesCues()));
        try {
            versionTv.setText("V"+VersionUtil.getVersionName(getHostActivity()));
            String version=SpCons.getString(getHostActivity(), SpCons.VERSION_CONTENT);
            if (!TextUtils.isEmpty(version)) {
                VersionBean versionBean = JSON.parseObject(version, VersionBean.class);
                if (VersionUtil.getVersionCode(getHostActivity()) < versionBean.getVersionCode()) {
                    newTv.setVisibility(View.VISIBLE);
                } else {
                    newTv.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPromptDialog() {
        PromptDialog.PromptBuilder builder = new PromptDialog.PromptBuilder(getActivity());
        PromptDialog promptDialog = builder.setPromptContent(R.string.prompt_confirm_logout)
                .setOnConfirmClickListener(new PromptDialog.PromptBuilder.OnConfirmClickListener() {
                    @Override
                    public void confirmClick() {
                        logout();
                    }
                }).create();
        promptDialog.show();
    }

    private void showSexDialog() {
        if (sexDialog == null) {
            SexDialog.SexBuilder sexBuilder = new SexDialog.SexBuilder(getActivity());
            sexDialog = sexBuilder.setOnSexSelectListener(new SexDialog.SexBuilder.OnSexSelectListener() {
                @Override
                public void secretClick() {
                    genderTv.setText(getString(R.string.keep_secret));
                    genderType = 2;
                    modifyAccountInfo(SpCons.getUser(getHostActivity()).getAudioCues(), genderType,
                            SpCons.getUser(getHostActivity()).getVibratesCues());
                }

                @Override
                public void maleClick() {
                    genderTv.setText(getString(R.string.male));
                    genderType = 1;
                    modifyAccountInfo(SpCons.getUser(getHostActivity()).getAudioCues(), genderType,
                            SpCons.getUser(getHostActivity()).getVibratesCues());
                }

                @Override
                public void femaleClick() {
                    genderTv.setText(getString(R.string.female));
                    genderType = 0;
                    modifyAccountInfo(SpCons.getUser(getHostActivity()).getAudioCues(), genderType,
                            SpCons.getUser(getHostActivity()).getVibratesCues());
                }
            }).create();
        }
        sexDialog.show();
    }

    private void modifyAccountInfo(final int audioCues, final int gender, final int vibratesCues) {
        HttpMore.modifyAccountInfo(audioCues, gender, SpCons.getUser(getHostActivity()).getId(),
                SpCons.getUser(getHostActivity()).getSign(),
                SpCons.getUser(getHostActivity()).getUsername(), vibratesCues,
                new HttpCallBack(getActivity(), ClassUtil.classMethodName(),true) {
                    @Override
                    public void success(Object data, int count) {
                        LoginUser user = SpCons.getUser(getHostActivity());
                        user.setGender(gender);
                        user.setAudioCues(audioCues);
                        user.setVibratesCues(vibratesCues);
                        SpCons.setUser(getHostActivity(), user);
                        UserDao.setUser(user);
                        LogUtil.i("saveAccountInfo", SpCons.getUser(getHostActivity()));
                        EventTrans.post(EventMsg.MORE_PERSONAL_INFO_REFRESH);
                    }
                });
    }

    private void getPersonalInfo() {
        HttpMore.getPersonalInfo(getActivity(), new HttpCallBack(getActivity(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    LoginUser loginUser = JSON.parseObject(data.toString(), LoginUser.class);
                    LogUtil.i("getPersonalInfo", loginUser);
                    if (loginUser != null) {
                        UserDao.setUser(loginUser);
                        SpCons.setUser(getHostActivity(), loginUser);
                        setViewData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void logout() {
        HttpLogin.logout(new HttpCallBack(getHostActivity(), ClassUtil.classMethodName(),true) {
            @Override
            public void success(Object data, int count) {
                CommonUtil.clearDataByLogout(getHostActivity());
                startActivity(new Intent(getActivity(), LoginActivity.class));
                EventTrans.post(EventMsg.MORE_LOGOUT_FINISH);
                getHostActivity().finish();
            }
        });
    }


    @Override
    public void onEventTrans(EventMsg msg) {
        if (msg.getKey() == EventMsg.MORE_PERSONAL_INFO_REFRESH
                || msg.getKey() == EventMsg.MORE_PERSONAL_AVATAR_REFRESH) {
            setViewData();

        } else if (msg.getKey() == EventMsg.CONTACT_NOTIFY_REFRESH) {
            if (SpCons.getNotifyRedPointVisible(getHostActivity())) {
                notifyRedPointIv.setVisibility(View.VISIBLE);
            } else {
                notifyRedPointIv.setVisibility(View.INVISIBLE);
            }
        }
    }

}
