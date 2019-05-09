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
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.Notify;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.model.db.entity.Subgroup;
import com.wewin.hichat.model.http.HttpNotify;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友/群通知
 * Created by Darren on 2018/12/26.
 */
public class NotifyActivity extends BaseActivity {

    private RecyclerView containerRcv;
    private List<Notify> notifyList = new ArrayList<>();
    private NotifyListRcvAdapter rcvAdapter;
    private Subgroup subgroup;
    private SmartRefreshLayout refreshLayout;
    private int mainCurrentPage = 1;
    private int totalPages = 0;

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
        getNotifyList(null, 1);
        SpCons.setNotifyRedPointVisible(getAppContext(), false);
        EventTrans.post(EventMsg.CONTACT_NOTIFY_REFRESH);
    }

    @Override
    protected void setListener() {
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getNotifyList(refreshLayout, mainCurrentPage + 1);
            }
        });
    }

    private void initRecyclerView() {
        rcvAdapter = new NotifyListRcvAdapter(getHostActivity(), notifyList);
        LinearLayoutManager manager = new LinearLayoutManager(getAppContext());
        containerRcv.setLayoutManager(manager);
        containerRcv.setAdapter(rcvAdapter);

        rcvAdapter.setOnBtnClickListener(new NotifyListRcvAdapter.OnBtnClickListener() {
            @Override
            public void agreeClick(int position) {
                int notifyType = notifyList.get(position).getType();
                if (notifyType == Notify.TYPE_FRIEND) {
                    if (subgroup == null) {
                        subgroup = (Subgroup) new SpCache(getAppContext())
                                .getObject(SpCons.SP_KEY_FRIEND_SUBGROUP);
                    }
                    if (subgroup != null) {
                        agreeFriendAdd(subgroup.getId(), position);
                    }
                } else if (notifyType == Notify.TYPE_GROUP) {
                    agreeGroupJoin(notifyList.get(position).getId(), position);
                }
            }

            @Override
            public void refuseClick(int position) {
                int notifyType = notifyList.get(position).getType();
                if (notifyType == Notify.TYPE_FRIEND) {
                    refuseFriendAdd(notifyList.get(position).getId(), position);
                } else if (notifyType == Notify.TYPE_GROUP) {
                    refuseGroupJoin(notifyList.get(position).getId(), position);
                }
            }
        });
    }

    private void updateRcv(){
        if (rcvAdapter != null){
            rcvAdapter.notifyDataSetChanged();
        }
    }

    private void getNotifyList(final RefreshLayout refreshLayout, final int currentPage) {
        HttpNotify.getNotifyList(UserDao.user.getId(), currentPage,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count, int pages) {
                        LogUtil.i("currentPage", currentPage);
                        LogUtil.i("pages", pages);
                        totalPages = pages;
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMore(500);
                            mainCurrentPage++;
                            if (currentPage >= totalPages) {
                                refreshLayout.setEnableLoadMore(false);
                            }
                        } else {
                            notifyList.clear();
                        }
                        if (data == null) {
                            return;
                        }
                        try {
                            List<Notify> dataList = JSON.parseArray(data.toString(), Notify.class);
                            LogUtil.i("getNotifyList", dataList);
                            notifyList.addAll(dataList);
                            updateRcv();

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
        HttpNotify.agreeFriendAdd(subgroupId, notifyList.get(position).getId(),
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        notifyList.get(position).setStatus(Notify.STATUS_AGREE);
                        updateRcv();
                        EventTrans.post(EventMsg.CONTACT_FRIEND_ADD_REFRESH);
                    }
                });
    }

    private void refuseFriendAdd(String notifyId, final int position) {
        HttpNotify.refuseFriendAdd(notifyId,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        notifyList.get(position).setStatus(Notify.STATUS_REFUSE);
                        updateRcv();
                    }
                });
    }

    private void agreeGroupJoin(String msgId, final int position) {
        HttpNotify.agreeGroupJoin(msgId,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        notifyList.get(position).setStatus(Notify.STATUS_AGREE);
                        updateRcv();
                        EventTrans.post(EventMsg.CONTACT_GROUP_AGREE_JOIN);
                    }
                });
    }

    private void refuseGroupJoin(String msgId, final int position) {
        HttpNotify.refuseGroupJoin(msgId,
                new HttpCallBack(getApplicationContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        notifyList.get(position).setStatus(Notify.STATUS_REFUSE);
                        updateRcv();
                    }
                });
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        switch (msg.getKey()){
            case EventMsg.CONTACT_NOTIFY_REFRESH:

                break;

            case EventMsg.CONTACT_GROUP_AGREE_JOIN:

                break;


        }

    }

}
