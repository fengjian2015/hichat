package com.wewin.hichat.view.contact.friend;

import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.base.BaseFragment;
import com.wewin.hichat.component.adapter.ContactFriendElvAdapter;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.androidlib.datamanager.DataCache;
import com.wewin.hichat.androidlib.datamanager.SpCache;
import com.wewin.hichat.component.dialog.PromptDialog;
import com.wewin.hichat.component.dialog.PromptInputDialog;
import com.wewin.hichat.component.dialog.SelectDialog;
import com.wewin.hichat.androidlib.rxJava.OnRxJavaProcessListener;
import com.wewin.hichat.androidlib.rxJava.RxJavaObserver;
import com.wewin.hichat.androidlib.rxJava.RxJavaScheduler;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.Subgroup;
import com.wewin.hichat.model.http.HttpContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.ObservableEmitter;

/**
 * 联系人-好友
 * Created by Darren on 2018/12/22.
 */
public class ContactFriendFragment extends BaseFragment {

    private List<Subgroup> subgroupFriendList = new ArrayList<>();
    private ContactFriendElvAdapter elvAdapter;
    private ExpandableListView containerElv;
    private LinearLayout createGroupingLl;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact_friend;
    }

    @Override
    protected void initViews() {
        containerElv = parentView.findViewById(R.id.elv_contact_friend_container);
        createGroupingLl = parentView.findViewById(R.id.ll_contact_friend_create_grouping);
    }

    @Override
    protected void initViewsData() {
        initExpandableListView();
        getSubgroupFriendList();
    }

    @Override
    protected void setListener() {
        createGroupingLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPromptInputDialog("", getString(R.string.create_grouping), "");
            }
        });
    }

    private void initExpandableListView() {
        elvAdapter = new ContactFriendElvAdapter(getHostActivity(), subgroupFriendList);
        containerElv.setAdapter(elvAdapter);
        containerElv.expandGroup(0);
        containerElv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Subgroup subgroup = subgroupFriendList.get(groupPosition);
                ChatRoomManager.startSingleRoomActivity(getHostActivity(),
                        subgroup.getList().get(childPosition));
                return true;
            }
        });
        elvAdapter.setOnGroupItemLongClickListener(new ContactFriendElvAdapter.OnGroupItemLongClickListener() {
            @Override
            public void longClick(int groupPosition) {
                if (subgroupFriendList.get(groupPosition).getIsDefault() != Subgroup.TYPE_FRIEND
                        && subgroupFriendList.get(groupPosition).getIsDefault() != Subgroup.TYPE_PHONE_CONTACT
                        && subgroupFriendList.get(groupPosition).getIsDefault() != Subgroup.TYPE_BLACK)
                    showSelectDialog(groupPosition);
            }
        });
        elvAdapter.setOnGroupItemClickListener(new ContactFriendElvAdapter.OnGroupItemClickListener() {
            @Override
            public void itemClick(int groupPosition) {
                if (!containerElv.isGroupExpanded(groupPosition)) {
                    containerElv.expandGroup(groupPosition);
                } else {
                    containerElv.collapseGroup(groupPosition);
                }
            }
        });
    }

    private void updateElv() {
        Collections.sort(subgroupFriendList, new Subgroup.SubgroupComparator());
        if (elvAdapter != null) {
            elvAdapter.notifyDataSetChanged();
        }
    }

    private void showSelectDialog(final int groupPosition) {
        String[] selectStrArr;
        if (subgroupFriendList.get(groupPosition).getIsDefault() == Subgroup.TYPE_FRIEND
                || subgroupFriendList.get(groupPosition).getIsDefault() == Subgroup.TYPE_BLACK
                || subgroupFriendList.get(groupPosition).getIsDefault() == Subgroup.TYPE_PHONE_CONTACT) {
            selectStrArr = new String[]{"重命名"};
        } else {
            selectStrArr = new String[]{"重命名", "删除"};
        }
        SelectDialog.SelectBuilder builder = new SelectDialog.SelectBuilder(getHostActivity());
        SelectDialog selectDialog = builder.setSelectStrArr(selectStrArr)
                .setTextColor(R.color.blue_main)
                .setOnLvItemClickListener(new SelectDialog.SelectBuilder.OnLvItemClickListener() {
                    @Override
                    public void itemClick(int lvItemPosition) {
                        if (lvItemPosition == 0) {
                            showPromptInputDialog(subgroupFriendList.get(groupPosition).getId(),
                                    getString(R.string.rename_grouping),
                                    subgroupFriendList.get(groupPosition).getGroupName());

                        } else if (lvItemPosition == 1) {
                            showPromptDialog(groupPosition);
                        }
                    }
                }).create();
        selectDialog.show();
    }

    private void showPromptDialog(final int groupPosition) {
        PromptDialog.PromptBuilder builder = new PromptDialog.PromptBuilder(getHostActivity());
        PromptDialog promptDialog = builder.setPromptContent(R.string.subgroup_delete_prompt)
                .setCancelVisible(true)
                .setOnConfirmClickListener(new PromptDialog.PromptBuilder.OnConfirmClickListener() {
                    @Override
                    public void confirmClick() {
                        deleteSubgroup(subgroupFriendList.get(groupPosition).getId());
                    }
                }).create();
        promptDialog.show();
    }

    private void showPromptInputDialog(final String groupingId, String title, String inputStr) {
        PromptInputDialog.PromptInputBuilder builder = new PromptInputDialog.PromptInputBuilder(getHostActivity());
        PromptInputDialog promptInputDialog = builder.setTitle(title)
                .setInputStr(inputStr)
                .setMaxInputLength(7)
                .setOnConfirmListener(new PromptInputDialog.PromptInputBuilder.OnConfirmListener() {
                    @Override
                    public void confirm(String inputStr) {
                        if (TextUtils.isEmpty(groupingId)) {
                            createSubgroup(inputStr);
                        } else {
                            renameSubgroup(groupingId, inputStr);
                        }
                    }
                }).create();
        promptInputDialog.show();
    }

    private void getSubgroupFriendList() {
        HttpContact.getSubgroupFriendList(new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    final List<Subgroup> dataList = JSON.parseArray(data.toString(), Subgroup.class);
                    LogUtil.i("getSubgroupFriendList", dataList);
                    subgroupFriendList.clear();
                    for (Subgroup subgroup : dataList) {
                        if (subgroup.getIsDefault() != Subgroup.TYPE_TEMPORARY) {
                            //临时会话不显示
                            subgroupFriendList.add(subgroup);
                        }
                    }
                    updateElv();

                    RxJavaScheduler.execute(new OnRxJavaProcessListener() {
                        @Override
                        public void process(ObservableEmitter<Object> emitter) {
                            for (Subgroup subgroup : dataList) {
                                if (subgroup.getIsDefault() == Subgroup.TYPE_FRIEND) {
                                    DataCache spCache = new SpCache(getContext());
                                    spCache.setObject(SpCons.SP_KEY_FRIEND_SUBGROUP, subgroup);
                                }
                                FriendDao.addFriendList(subgroup.getList(), subgroup);
                                ContactUserDao.addContactUserList(subgroup.getList());
                            }
                        }
                    }, new RxJavaObserver<Object>() {
                        @Override
                        public void onComplete() {
                            EventTrans.post(EventMsg.CONTACT_FRIEND_LIST_GET_REFRESH);
                            EventTrans.post(EventMsg.CONTACT_ADD_FRIEND_PROCESS_REFRESH);
                            EventTrans.post(EventMsg.CONTACT_LIST_GET_REFRESH);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createSubgroup(String subgroupName) {
        HttpContact.createSubgroup(subgroupName, UserDao.user.getId(),
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        getSubgroupFriendList();
                    }
                });
    }

    private void renameSubgroup(String subgroupId, String subgroupName) {
        HttpContact.renameSubgroup(subgroupId, subgroupName,
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        getSubgroupFriendList();
                    }
                });
    }

    private void deleteSubgroup(String subgroupId) {
        HttpContact.deleteFriendSubgroup(subgroupId,
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        getSubgroupFriendList();
                    }
                });
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        if (msg.getKey() == EventMsg.CONTACT_FRIEND_ADD_REFRESH
                || msg.getKey() == EventMsg.CONTACT_FRIEND_NOTE_REFRESH
                || msg.getKey() == EventMsg.CONTACT_FRIEND_SUBGROUP_REFRESH
                || msg.getKey() == EventMsg.CONTACT_FRIEND_DELETE_REFRESH
                || msg.getKey() == EventMsg.CONTACT_FRIEND_BLACK_REFRESH
                || msg.getKey() == EventMsg.CONTACT_NOTIFY_REFRESH
                || msg.getKey() == EventMsg.CONTACT_FRIEND_AVATAR_REFRESH
                || msg.getKey() == EventMsg.CONTACT_FRIEND_NAME_REFRESH
                || msg.getKey() == EventMsg.CONTACT_FRIEND_INFO_REFRESH
                || msg.getKey() == EventMsg.CONTACT_DELETE_BY_OTHER
                || msg.getKey() == EventMsg.CONTACT_PHONE_CONTACT_REFRESH) {
            getSubgroupFriendList();
        }
    }

}
