package com.wewin.hichat.view.contact;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.adapter.MainVpAdapter;
import com.wewin.hichat.view.contact.friend.FriendSearchFragment;
import com.wewin.hichat.view.contact.group.GroupCreateFragment;
import com.wewin.hichat.view.contact.group.GroupSearchFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 加好友、加群、建群
 * Created by Darren on 2018/12/24.
 */
public class AddActivity extends BaseActivity {

    private ViewPager containerVp;
    private LinearLayout addFriendLl, addGroupLl, createGroupLl;
    private ImageView leftPointIv, centerPointIv, rightPointIv;
    private GroupCreateFragment groupCreateFragment;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_add;
    }

    @Override
    protected void initViews() {
        containerVp = findViewById(R.id.vp_contact_add_container);
        addFriendLl = findViewById(R.id.ll_contact_add_friend);
        addGroupLl = findViewById(R.id.ll_contact_add_group);
        createGroupLl = findViewById(R.id.ll_contact_add_create_group);
        leftPointIv = findViewById(R.id.iv_contact_add_left_point);
        centerPointIv = findViewById(R.id.iv_contact_add_center_point);
        rightPointIv = findViewById(R.id.iv_contact_add_right_point);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.add);
        setLeftText(R.string.main_contact);
        initViewPager();
    }

    @Override
    protected void setListener() {
        addFriendLl.setOnClickListener(this);
        addGroupLl.setOnClickListener(this);
        createGroupLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_contact_add_friend:
                containerVp.setCurrentItem(0);
                resetBluePoint(0);
                break;

            case R.id.ll_contact_add_group:
                containerVp.setCurrentItem(1);
                resetBluePoint(1);
                break;

            case R.id.ll_contact_add_create_group:
                containerVp.setCurrentItem(2);
                resetBluePoint(2);
                break;
        }
    }

    @Override
    protected void onRightTvClick() {
        if (groupCreateFragment != null){
            groupCreateFragment.createGroup();
        }
    }

    private void resetBluePoint(int position){
        switch (position){
            case 0:
                leftPointIv.setVisibility(View.VISIBLE);
                centerPointIv.setVisibility(View.INVISIBLE);
                rightPointIv.setVisibility(View.INVISIBLE);
                setRightTv(0);
                break;

            case 1:
                leftPointIv.setVisibility(View.INVISIBLE);
                centerPointIv.setVisibility(View.VISIBLE);
                rightPointIv.setVisibility(View.INVISIBLE);
                setRightTv(0);
                break;

            case 2:
                leftPointIv.setVisibility(View.INVISIBLE);
                centerPointIv.setVisibility(View.INVISIBLE);
                rightPointIv.setVisibility(View.VISIBLE);
                setRightTv(R.string.finish);
                break;
        }
    }

    private void initViewPager() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new FriendSearchFragment());
        fragmentList.add(new GroupSearchFragment());
        groupCreateFragment = new GroupCreateFragment();
        fragmentList.add(groupCreateFragment);
        FragmentManager manager = getSupportFragmentManager();
        containerVp.setAdapter(new MainVpAdapter(manager, fragmentList));
        containerVp.setOffscreenPageLimit(2);
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        if (msg.getKey() == EventMsg.CONTACT_FRIEND_ADD_FINISH
                || msg.getKey() == EventMsg.CONTACT_GROUP_APPLY_JOIN_FINISH
                || msg.getKey() == EventMsg.CONTACT_SEND_MSG_FINISH){
            this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case ImgUtil.REQUEST_CAMERA:
                    LogUtil.i("ImgUtil.REQUEST_CAMERA");
                    if (!TextUtils.isEmpty(ImgUtil.cameraOutputPath)){
                        ImgUtil.cropPic(getHostActivity(), ImgUtil.cameraOutputPath, ImgUtil.IMG_CROP_SIZE_1);
                    }
                    break;

                case ImgUtil.REQUEST_CROP:
                    LogUtil.i("ImgUtil.REQUEST_CROP");
                    if (!TextUtils.isEmpty(ImgUtil.cropOutputPath)){
                        if (groupCreateFragment != null){
                            groupCreateFragment.setCameraImg(ImgUtil.cropOutputPath);
                        }
                    }
                    break;
            }

        }
    }
}
