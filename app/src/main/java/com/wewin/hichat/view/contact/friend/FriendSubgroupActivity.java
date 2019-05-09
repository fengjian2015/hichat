package com.wewin.hichat.view.contact.friend;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.adapter.MessageMoveGroupingRcvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.base.BaseRcvAdapter;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.Subgroup;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.model.http.HttpContact;

import java.util.ArrayList;
import java.util.List;

/**
 * 移动分组
 * Created by Darren on 2019/1/4.
 */
public class FriendSubgroupActivity extends BaseActivity {

    private RecyclerView containerRcv;
    private List<Subgroup> subgroupList = new ArrayList<>();
    private MessageMoveGroupingRcvAdapter rcvAdapter;
    private FriendInfo friendInfo;
    private int checkPosition = 0;
    private String subgroupId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_move_subgroup;
    }

    @Override
    protected void initViews() {
        containerRcv = findViewById(R.id.rcv_contact_move_subgroup_container);
    }

    @Override
    protected void getIntentData() {
        friendInfo = (FriendInfo) getIntent()
                .getSerializableExtra(ContactCons.EXTRA_CONTACT_FRIEND_INFO);
        subgroupId = getIntent().getStringExtra(ContactCons.EXTRA_CONTACT_FRIEND_SUBGROUP_ID);
        LogUtil.i("friendInfo", friendInfo);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.select_grouping);
        setLeftCancelVisible();
        setRightTv(R.string.confirm);

        initRecyclerView();
        getFriendSubgroup();
        if (friendInfo != null) {
            FriendInfo dataInfo = FriendDao.getFriendInfo(friendInfo.getId());
            if (dataInfo != null) {
                friendInfo.setSubgroupId(dataInfo.getSubgroupId());
                friendInfo.setSubgroupName(dataInfo.getSubgroupName());
            }
        }
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void onRightTvClick() {
        if (friendInfo != null) {
            moveFriendSubgroup(friendInfo.getId(), subgroupList.get(checkPosition).getId());
        } else {
            Intent intent = new Intent();
            intent.putExtra(ContactCons.EXTRA_CONTACT_FRIEND_SUBGROUP_ID, subgroupList.get(checkPosition).getId());
            intent.putExtra(ContactCons.EXTRA_CONTACT_FRIEND_SUBGROUP_NAME, subgroupList.get(checkPosition).getGroupName());
            setResult(RESULT_OK, intent);
            this.finish();
        }
    }

    private void initRecyclerView() {
        rcvAdapter = new MessageMoveGroupingRcvAdapter(getApplicationContext(), subgroupList);
        containerRcv.setLayoutManager(new LinearLayoutManager(this));
        containerRcv.setAdapter(rcvAdapter);
        rcvAdapter.setOnItemClickListener(new BaseRcvAdapter.OnItemClickListener() {
            @Override
            public void itemClick(int position) {
                checkPosition = position;
                for (int i = 0; i < subgroupList.size(); i++) {
                    if (i == position) {
                        subgroupList.get(i).setCheckState(1);
                    } else {
                        subgroupList.get(i).setCheckState(0);
                    }
                }
                rcvAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getFriendSubgroup() {
        HttpContact.getFriendSubgroup(UserDao.user.getId(),
                new HttpCallBack(getApplicationContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            List<Subgroup> dataList = JSON.parseArray(data.toString(), Subgroup.class);
                            LogUtil.i("getFriendGrouping", dataList);
                            for (Subgroup subgroup : dataList) {
                                if ((!TextUtils.isEmpty(subgroupId) && subgroupId.equals(subgroup.getId()))
                                        || (friendInfo != null && friendInfo.getSubgroupId() != null
                                        && friendInfo.getSubgroupId().equals(subgroup.getId()))) {
                                    subgroup.setCheckState(1);
                                }
                            }
                            subgroupList.clear();
                            subgroupList.addAll(dataList);
                            rcvAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void moveFriendSubgroup(String friendId, String subgroupId) {
        HttpContact.moveFriendSubgroup(friendId, subgroupId,
                new HttpCallBack(getApplicationContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        ToastUtil.showShort(getApplicationContext(), R.string.modify_success);
                        EventTrans.post(EventMsg.CONTACT_FRIEND_SUBGROUP_REFRESH);
                        FriendSubgroupActivity.this.finish();
                    }
                });
    }

}
