package com.wewin.hichat.view.more;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.adapter.MoreLoginRecordRcvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.model.db.entity.LoginRecord;
import com.wewin.hichat.model.http.HttpMore;

import java.util.ArrayList;
import java.util.List;

/**
 * 账号登录记录
 * Created by Darren on 2019/3/1
 */
public class LoginRecordActivity extends BaseActivity {

    private RecyclerView containerRcv;
    private List<LoginRecord> recordList = new ArrayList<>();
    private MoreLoginRecordRcvAdapter rcvAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_more_login_record;
    }

    @Override
    protected void initViews() {
        containerRcv = findViewById(R.id.rcv_more_login_record_container);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.account_login_record);
        setLeftText(R.string.back);
        initRecyclerView();
        getLoginRecordList();
    }

    private void initRecyclerView(){
        rcvAdapter = new MoreLoginRecordRcvAdapter(getAppContext(), recordList);
        containerRcv.setLayoutManager(new LinearLayoutManager(getAppContext()));
        containerRcv.setAdapter(rcvAdapter);

    }

    private void getLoginRecordList(){
        HttpMore.getLoginRecord(new HttpCallBack(this, ClassUtil.classMethodName(),true){
            @Override
            public void success(Object data, int count) {
                if (data == null){
                    return;
                }
                try {
                    LogUtil.i("getLoginRecordList", data.toString());
                    List<LoginRecord> dataList = JSON.parseArray(data.toString(), LoginRecord.class);
                    recordList.clear();
                    recordList.addAll(dataList);
                    if (rcvAdapter != null){
                        rcvAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

}
