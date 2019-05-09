package com.wewin.hichat.view.contact.group;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.adapter.ContactGroupAnnouncementListRcvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.base.BaseRcvAdapter;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.Announcement;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.http.HttpContact;

import java.util.ArrayList;
import java.util.List;

/**
 * 群公告
 * Created by Darren on 2019/1/15.
 */
public class AnnouncementListActivity extends BaseActivity {

    private GroupInfo mGroupInfo;
    private RecyclerView containerRcv;
    private List<Announcement> announcementList = new ArrayList<>();
    private ContactGroupAnnouncementListRcvAdapter rcvAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_group_announcement;
    }

    @Override
    protected void initViews() {
        containerRcv = findViewById(R.id.rcv_contact_group_announcement_list);
    }

    @Override
    protected void getIntentData() {
        mGroupInfo = (GroupInfo) getIntent()
                .getSerializableExtra(ContactCons.EXTRA_CONTACT_GROUP_INFO);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.group_announcement);
        setRightImg(R.drawable.announcement_plus_btn);
        setLeftText(R.string.group_info);

        initRecyclerView();
        getAnnouncementList(1);
    }

    @Override
    protected void onRightImgClick() {
        Intent intent = new Intent(getApplicationContext(), AnnouncementModifyActivity.class);
        intent.putExtra(ContactCons.EXTRA_CONTACT_GROUP_INFO, mGroupInfo);
        startActivity(intent);
    }

    private void initRecyclerView() {
        rcvAdapter = new ContactGroupAnnouncementListRcvAdapter(
                this, announcementList);
        containerRcv.setLayoutManager(new LinearLayoutManager(this));
        containerRcv.setAdapter(rcvAdapter);

        rcvAdapter.setOnMoreClickListener(new ContactGroupAnnouncementListRcvAdapter.OnMoreClickListener() {
            @Override
            public void deleteClick(int position) {
                deleteAnnouncement(position);
            }

            @Override
            public void editClick(int position) {
                Intent intent = new Intent(getApplicationContext(), AnnouncementModifyActivity.class);
                intent.putExtra(ContactCons.EXTRA_CONTACT_GROUP_INFO, mGroupInfo);
                intent.putExtra(ContactCons.EXTRA_CONTACT_GROUP_ANNOUNCEMENT, announcementList.get(position));
                startActivity(intent);
            }
        });

        rcvAdapter.setOnItemClickListener(new BaseRcvAdapter.OnItemClickListener() {
            @Override
            public void itemClick(int position) {
                Intent intent = new Intent(getAppContext(), AnnouncementDetailActivity.class);
                intent.putExtra(ContactCons.EXTRA_CONTACT_GROUP_INFO, mGroupInfo);
                intent.putExtra(ContactCons.EXTRA_CONTACT_GROUP_ANNOUNCEMENT, announcementList.get(position));
                startActivity(intent);
            }
        });
    }

    private void updateRcv() {
        if (rcvAdapter != null) {
            rcvAdapter.notifyDataSetChanged();
        }
    }

    private void getAnnouncementList(int page) {
        HttpContact.getAnnouncementList(mGroupInfo.getId(), 10, page, "",
                new HttpCallBack(getApplicationContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            List<Announcement> dataList = JSON.parseArray(data.toString(), Announcement.class);
                            LogUtil.i("getAnnouncementList", dataList);
                            announcementList.clear();
                            announcementList.addAll(dataList);
                            updateRcv();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void deleteAnnouncement(final int position) {
        HttpContact.deleteAnnouncement(announcementList.get(position).getId(),
                new HttpCallBack(getApplicationContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        announcementList.remove(position);
                        updateRcv();
                    }
                });
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        switch (msg.getKey()){
            case EventMsg.CONTACT_GROUP_ANNOUNCEMENT_REFRESH:
                getAnnouncementList(1);
                break;

            case EventMsg.CONTACT_GROUP_REMOVE_MEMBER:
                GroupInfo groupInfo1 = (GroupInfo) msg.getData();
                FriendInfo receiver = (FriendInfo) msg.getThirdData();
                if (groupInfo1 == null || TextUtils.isEmpty(groupInfo1.getId())
                        || receiver == null || TextUtils.isEmpty(receiver.getId())){
                    return;
                }
                if (groupInfo1.getId().equals(mGroupInfo.getId())
                        && receiver.getId().equals(UserDao.user.getId())){
                    ToastUtil.showShort(getAppContext(), R.string.you_are_moved_out_from_group);
                    getHostActivity().finish();
                }
                break;

            case EventMsg.CONTACT_GROUP_DISBAND:
                String groupId1 = msg.getData().toString();
                if (!TextUtils.isEmpty(groupId1) && groupId1.equals(mGroupInfo.getId())){
                    getHostActivity().finish();
                }
                break;
        }

    }

}
