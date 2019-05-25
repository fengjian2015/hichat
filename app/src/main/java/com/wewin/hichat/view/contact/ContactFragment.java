package com.wewin.hichat.view.contact;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.base.BaseFragment;
import com.wewin.hichat.component.adapter.MainVpAdapter;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.view.contact.friend.ContactFriendFragment;
import com.wewin.hichat.view.contact.group.ContactGroupFragment;
import com.wewin.hichat.view.search.FriendGroupSearchActivity;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 主页-联系人页
 * Created by Darren on 2018/12/13.
 */
public class ContactFragment extends BaseFragment {

    private ViewPager containerVp;
    private final int REQUEST_SEARCH = 1;
    private RadioButton friendRb, groupRb;
    private ImageView addIv;
    private FrameLayout searchFl;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initViews() {
        containerVp = parentView.findViewById(R.id.vp_contact_container);
        friendRb = parentView.findViewById(R.id.rb_contact_friend);
        groupRb = parentView.findViewById(R.id.rb_contact_group);
        addIv = parentView.findViewById(R.id.iv_contact_add);
        searchFl = parentView.findViewById(R.id.fl_contact_search_container);
    }

    @Override
    protected void initViewsData() {
        initViewPager();
    }

    @Override
    protected void setListener() {
        friendRb.setOnClickListener(this);
        groupRb.setOnClickListener(this);
        addIv.setOnClickListener(this);
        searchFl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_contact_friend:
                containerVp.setCurrentItem(0);
                break;

            case R.id.rb_contact_group:
                containerVp.setCurrentItem(1);
                break;

            case R.id.iv_contact_add:
                startActivity(new Intent(getActivity(), AddActivity.class));
                break;

            case R.id.fl_contact_search_container:
                startActivity(new Intent(getActivity(), FriendGroupSearchActivity.class));
                break;

            default:

                break;
        }
    }

    private void initViewPager() {
        List<Fragment> fragmentList = new ArrayList<>();
        ContactFriendFragment friendFragment = new ContactFriendFragment();
        ContactGroupFragment groupFragment = new ContactGroupFragment();
        fragmentList.add(friendFragment);
        fragmentList.add(groupFragment);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            FragmentManager manager = activity.getSupportFragmentManager();
            containerVp.setAdapter(new MainVpAdapter(manager, fragmentList));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_SEARCH) {
            FriendInfo selectFriend = (FriendInfo) data
                    .getSerializableExtra(ContactCons.EXTRA_CONTACT_FRIEND_INFO);
            if (selectFriend != null) {
                ChatRoomManager.startSingleRoomActivity(getHostActivity(), selectFriend.getId());
            }
        }
    }

}
