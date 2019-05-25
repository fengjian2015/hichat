package com.wewin.hichat.view.conversation;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.manage.FileManager;
import com.wewin.hichat.androidlib.rxjava.OnRxJavaProcessListener;
import com.wewin.hichat.androidlib.rxjava.RxJavaObserver;
import com.wewin.hichat.androidlib.rxjava.RxJavaScheduler;
import com.wewin.hichat.component.adapter.MessageFileRcvAdapter;
import com.wewin.hichat.component.base.BaseFragment;
import com.wewin.hichat.component.dialog.LoadingDialog;
import com.wewin.hichat.model.db.entity.FileInfo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableEmitter;

/**
 * 文件选择-视频
 * Created by Darren on 2018/12/22.
 */
public class FileVideoFragment extends BaseFragment {

    private List<FileInfo> fileInfoList = new ArrayList<>();
    private List<FileInfo> mSelectList = new ArrayList<>();
    private RecyclerView containerRcv;
    private MessageFileRcvAdapter rcvAdapter;
    private boolean isDataGetFinish = false;
    private LoadingDialog loadingDialog;
    private boolean isUserVisible = false;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message_chat_file_video;
    }

    @Override
    protected void initViews() {
        containerRcv = parentView.findViewById(R.id.rcv_message_chat_file_video_container);
    }

    @Override
    protected void initViewsData() {
        initRecyclerView();

        if (!isDataGetFinish && isUserVisible){
            showLoadingDialog();
        }

        RxJavaScheduler.execute(new OnRxJavaProcessListener() {
            @Override
            public void process(ObservableEmitter<Object> emitter) {
                List<FileInfo> videoList = FileManager.getInstance(getContext()).getVideoList();
                fileInfoList.clear();
                fileInfoList.addAll(videoList);
            }
        }, new RxJavaObserver<Object>(){
            @Override
            public void onComplete() {
                isDataGetFinish = true;
                if (loadingDialog != null){
                    loadingDialog.dismiss();
                }
                rcvAdapter.notifyDataSetChanged();
            }
        });
    }

    public List<FileInfo> getSelectFileList(){
        return mSelectList;
    }

    private void initRecyclerView(){
        rcvAdapter = new MessageFileRcvAdapter(getContext(), fileInfoList);
        containerRcv.setLayoutManager(new LinearLayoutManager(getContext()));
        containerRcv.setAdapter(rcvAdapter);
        rcvAdapter.setOnSelectListener(new MessageFileRcvAdapter.OnSelectListener() {
            @Override
            public void select(List<FileInfo> fileList) {
                mSelectList.clear();
                mSelectList.addAll(fileList);
            }
        });
    }

    private void showLoadingDialog(){
        if (loadingDialog == null && getHostActivity() != null){
            LoadingDialog.LoadingBuilder loadingBuilder = new LoadingDialog.LoadingBuilder(getHostActivity());
            loadingDialog = loadingBuilder.create();
        }
        if (loadingDialog != null){
            loadingDialog.show();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isUserVisible = isVisibleToUser;
    }

}
