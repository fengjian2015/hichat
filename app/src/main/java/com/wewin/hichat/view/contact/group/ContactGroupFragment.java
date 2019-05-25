package com.wewin.hichat.view.contact.group;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.adapter.ContactGroupLvAdapter;
import com.wewin.hichat.component.base.BaseFragment;
import com.wewin.hichat.androidlib.threadpool.SingleThreadPool;
import com.wewin.hichat.model.db.dao.ChatRoomDao;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.GroupDao;
import com.wewin.hichat.model.db.dao.GroupMemberDao;
import com.wewin.hichat.model.db.entity.BaseSearchEntity;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.http.HttpContact;
import com.wewin.hichat.view.search.CharacterParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 联系人-群聊
 * Created by Darren on 2018/12/22.
 */
public class ContactGroupFragment extends BaseFragment {

    private List<GroupInfo> mGroupList = new ArrayList<>();
    private ListView containerLv;
    private ContactGroupLvAdapter lvAdapter;
    //汉字转换成拼音的类
    private CharacterParser characterParser = CharacterParser.getInstance();


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact_group;
    }

    @Override
    protected void initViews() {
        containerLv = parentView.findViewById(R.id.lv_contact_group_container);
    }

    @Override
    protected void initViewsData() {
        initListView();
        mGroupList.addAll(GroupDao.getGroupList());
        if (!mGroupList.isEmpty() && lvAdapter != null) {
            rebuildData(mGroupList);
        }
        getGroupList();
    }

    private void initListView() {
        lvAdapter = new ContactGroupLvAdapter(getActivity(), mGroupList);
        containerLv.setAdapter(lvAdapter);
        containerLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatRoomManager.startGroupRoomActivity(getHostActivity(),
                        mGroupList.get(position).getId());
            }
        });
    }

    private void processGroupListData(List<GroupInfo> groupDataList) {
        mGroupList.clear();
        mGroupList.addAll(groupDataList);
        rebuildData(mGroupList);
        GroupDao.addGroupList(mGroupList);
        ChatRoomDao.updateTopShieldByGroupList(GroupDao.getGroupList());
        EventTrans.post(EventMsg.CONTACT_GROUP_LIST_GET_REFRESH);
        //获取所有群成员，保存其名称及头像到ContactDao
        for (GroupInfo groupInfo : mGroupList) {
            getGroupMemberList(groupInfo.getId());
        }
    }

    /**
     * 为ListView填充数据
     */
    private void rebuildData(List<GroupInfo> dataList) {
        for (GroupInfo groupInfo : dataList) {
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(groupInfo.getGroupName());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                groupInfo.setSortLetter(sortString.toUpperCase());
            } else {
                groupInfo.setSortLetter("#");
            }
        }
        Collections.sort(dataList, new BaseSearchEntity.SortComparator());
        if (lvAdapter != null) {
            lvAdapter.updateListView(dataList);
        }
    }

    private void getGroupList() {
        HttpContact.getGroupList(new HttpCallBack(getActivity(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    List<GroupInfo> dataList = JSON.parseArray(data.toString(), GroupInfo.class);
                    LogUtil.i("getGroupList", dataList);
                    processGroupListData(dataList);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getGroupMemberList(final String groupId) {
        HttpContact.getGroupMemberList(groupId, -1,
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void successOnChildThread(Object data, int count, int pages) {
                        if (data == null) {
                            return;
                        }
                        try {
                            final List<FriendInfo> memberList = JSON.parseArray(data.toString(),
                                    FriendInfo.class);
                            LogUtil.i("getGroupMemberList", memberList);
                            SingleThreadPool.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {
                                    ContactUserDao.addContactUserList(memberList);
                                    GroupMemberDao.addGroupMemberList(groupId, memberList);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        if (msg.getKey() == EventMsg.CONTACT_GROUP_CREATE_REFRESH
                || msg.getKey() == EventMsg.CONTACT_GROUP_AGREE_JOIN
                || msg.getKey() == EventMsg.CONTACT_GROUP_DISBAND
                || msg.getKey() == EventMsg.CONTACT_GROUP_INFO_REFRESH
                || msg.getKey() == EventMsg.CONTACT_GROUP_AVATAR_REFRESH) {
            getGroupList();

        } else if (msg.getKey() == EventMsg.CONTACT_GROUP_REMOVE_MEMBER) {
            FriendInfo receiver = (FriendInfo) msg.getThirdData();
            if (receiver == null || TextUtils.isEmpty(receiver.getId())){
                return;
            }
            if (SpCons.getUser(getHostActivity()).getId().equals(receiver.getId())){
                getGroupList();
            }

        } else if (msg.getKey() == EventMsg.CONTACT_GROUP_INVITE_MEMBER_REFRESH) {
            List<FriendInfo> receiverList = (List<FriendInfo>) msg.getData();
            if (receiverList == null || receiverList.isEmpty()){
                return;
            }
            for (FriendInfo friend : receiverList) {
                if (SpCons.getUser(getHostActivity()).getId().equals(friend.getId())) {
                    getGroupList();
                    break;
                }
            }

        } else if (msg.getKey() == EventMsg.CONTACT_GROUP_QUIT) {
            String receiverId = msg.getSecondData().toString();
            if (!TextUtils.isEmpty(receiverId) && SpCons.getUser(getHostActivity()).getId().equals(receiverId)) {
                getGroupList();
            }
        }
    }

}
