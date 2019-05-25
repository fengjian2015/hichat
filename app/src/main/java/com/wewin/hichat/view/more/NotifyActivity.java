package com.wewin.hichat.view.more;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.alibaba.fastjson.JSON;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.component.adapter.NotifyListRcvAdapter;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.androidlib.datamanager.SpCache;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.Notify;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.model.db.entity.Subgroup;
import com.wewin.hichat.model.http.HttpMore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 好友/群通知
 * Created by Darren on 2018/12/26.
 */
public class NotifyActivity extends BaseActivity {

    private RecyclerView containerRcv;
    private List<Notify> mNotifyList = new ArrayList<>();
    private NotifyListRcvAdapter rcvAdapter;
    private Subgroup subgroup;
    private SmartRefreshLayout refreshLayout;
    private int mainCurrentPage = 1;
    private int totalCount = 0;
    private final int LIMIT_PAGE = 15;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_more_notify;
    }

    @Override
    protected void initViews() {
        containerRcv = findViewById(R.id.rcv_notify_friend_container);
        refreshLayout = findViewById(R.id.srl_refresh_layout);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.notify);
        setLeftText(R.string.back);
        initRecyclerView();
        SpCons.setNotifyRedPointVisible(getAppContext(), false);
        EventTrans.post(EventMsg.CONTACT_NOTIFY_REFRESH);
    }

    @Override
    protected void setListener() {
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getNotifyList(mainCurrentPage + 1);
            }
        });
    }

    private void initRecyclerView() {
        rcvAdapter = new NotifyListRcvAdapter(getHostActivity(), mNotifyList);
        LinearLayoutManager manager = new LinearLayoutManager(getAppContext());
        containerRcv.setLayoutManager(manager);
        containerRcv.setAdapter(rcvAdapter);

        rcvAdapter.setOnBtnClickListener(new NotifyListRcvAdapter.OnBtnClickListener() {
            @Override
            public void agreeClick(int position) {
                String noticeType = mNotifyList.get(position).getNoticeType();
                if (ChatRoom.TYPE_SINGLE.equals(noticeType)) {
                    if (subgroup == null) {
                        subgroup = (Subgroup) new SpCache(getAppContext())
                                .getObject(SpCons.SP_KEY_FRIEND_SUBGROUP);
                    }
                    if (subgroup != null) {
                        agreeFriendAdd(subgroup.getId(), position);
                    }
                } else if (ChatRoom.TYPE_GROUP.equals(noticeType)) {
                    agreeGroupJoin(mNotifyList.get(position).getNoticeId(), position);
                }
            }

            @Override
            public void refuseClick(int position) {
                String noticeType = mNotifyList.get(position).getNoticeType();
                if (ChatRoom.TYPE_SINGLE.equals(noticeType)) {
                    refuseFriendAdd(mNotifyList.get(position).getNoticeId(), position);
                } else if (ChatRoom.TYPE_GROUP.equals(noticeType)) {
                    refuseGroupJoin(mNotifyList.get(position).getNoticeId(), position);
                }
            }
        });
    }

    private void updateRcv() {
        if (rcvAdapter != null) {
            Collections.sort(mNotifyList, new Notify.TimeRiseComparator());
            rcvAdapter.notifyDataSetChanged();
        }
    }

    private void getNotifyList(final int currentPage) {
        HttpMore.getNotifyList(LIMIT_PAGE, currentPage,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        LogUtil.i("currentPage", currentPage);
                        LogUtil.i("count", count);
                        if (currentPage == 1) {
                            totalCount = count;
                            mNotifyList.clear();
                        } else {
                            refreshLayout.finishLoadMore(500);
                            mainCurrentPage++;
                            if (mNotifyList.size() >= totalCount) {
                                refreshLayout.setEnableLoadMore(false);
                            }
                        }
                        if (data == null) {
                            return;
                        }
                        try {
                            List<Notify> dataList = JSON.parseArray(data.toString(), Notify.class);
                            Set<Notify> dataSet = new HashSet<>();
                            dataSet.addAll(dataList);
                            dataSet.addAll(mNotifyList);
                            mNotifyList.clear();
                            mNotifyList.addAll(dataSet);
                            updateRcv();
                            if (currentPage == 1){
                                setNotifyRead(mNotifyList.get(0).getNoticeId());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(String desc) {
                        super.failure(desc);
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMore(500);
                        }
                    }
                });
    }

    private void agreeFriendAdd(String subgroupId, final int position) {
        HttpMore.agreeFriendAdd(subgroupId, mNotifyList.get(position).getNoticeId(),
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        mNotifyList.get(position).setStatus(Notify.STATUS_AGREE);
                        updateRcv();
                        EventTrans.post(EventMsg.CONTACT_FRIEND_ADD_REFRESH);
                    }
                });
    }

    private void refuseFriendAdd(String notifyId, final int position) {
        HttpMore.refuseFriendAdd(notifyId,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        mNotifyList.get(position).setStatus(Notify.STATUS_REFUSE);
                        updateRcv();
                    }
                });
    }

    private void agreeGroupJoin(String msgId, final int position) {
        HttpMore.agreeGroupJoin(msgId,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        mNotifyList.get(position).setStatus(Notify.STATUS_AGREE);
                        updateRcv();
                        EventTrans.post(EventMsg.CONTACT_GROUP_AGREE_JOIN);
                    }
                });
    }

    private void refuseGroupJoin(String msgId, final int position) {
        HttpMore.refuseGroupJoin(msgId,
                new HttpCallBack(getApplicationContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        mNotifyList.get(position).setStatus(Notify.STATUS_REFUSE);
                        updateRcv();
                    }
                });
    }

    private void setNotifyRead(String noticeId){
        HttpMore.setNotifyRead(noticeId,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()){
                    @Override
                    public void success(Object data, int count) {
                        super.success(data, count);
                    }
                });
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        switch (msg.getKey()) {
            case EventMsg.CONTACT_NOTIFY_REFRESH:
                Notify notifyInfo = (Notify) msg.getData();
                if (notifyInfo != null) {
                    boolean isMatch = false;
                    for (int i = 0; i < mNotifyList.size(); i++) {
                        if (mNotifyList.get(i).getNoticeId().equals(notifyInfo.getNoticeId())) {
                            mNotifyList.remove(i);
                            mNotifyList.add(i, notifyInfo);
                            isMatch = true;
                        }
                    }
                    if (!isMatch) {
                        mNotifyList.add(0, notifyInfo);
                    }
                } else {
                    getNotifyList(mainCurrentPage);
                }
                break;

            default:
                break;
        }
    }

}
