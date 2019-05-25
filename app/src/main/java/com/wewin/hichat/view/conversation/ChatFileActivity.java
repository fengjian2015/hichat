package com.wewin.hichat.view.conversation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomVpPageChangeListener;
import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.androidlib.widget.CustomTabLayout;
import com.wewin.hichat.component.adapter.MainVpAdapter;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天-文件选择
 * Created by Darren on 2018/12/22.
 */
public class ChatFileActivity extends BaseActivity {

    private CustomTabLayout typeCtl;
    private ViewPager containerVp;
    private FileDocumentFragment docFragment;
    private FileVideoFragment videoFragment;
    private FileMusicFragment audioFragment;
    private ChatRoom mChatRoom;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_chat_file_send;
    }

    @Override
    protected void getIntentData() {
        mChatRoom = (ChatRoom) getIntent().getSerializableExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM);
    }

    @Override
    protected void initViews() {
        typeCtl = findViewById(R.id.ctl_message_file_type);
        containerVp = findViewById(R.id.vp_message_file_send);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.host_phone);
        setRightTv(R.string.send);
        typeCtl.setTitleArr(new String[]{getString(R.string.video),
                getString(R.string.musicInfo), getString(R.string.document)});
        initViewPager();
    }

    @Override
    protected void setListener() {
        typeCtl.setOnTabClickListener(new CustomTabLayout.OnTabClickListener() {
            @Override
            public void tabClick(int position, String str) {
                containerVp.setCurrentItem(position);
            }
        });

        containerVp.addOnPageChangeListener(new CustomVpPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                typeCtl.moveToPosition(position);
            }
        });
    }

    @Override
    protected void onRightTvClick() {
        switch (typeCtl.getUnderlinePosition()) {
            case 0:
                for (FileInfo fileInfo : videoFragment.getSelectFileList()) {
                    fileInfo.setFileType(FileInfo.TYPE_VIDEO);
                    ChatRoomManager.uploadFile(getAppContext(), mChatRoom, fileInfo);
                }
                break;

            case 1:
                for (FileInfo fileInfo : audioFragment.getSelectFileList()) {
                    fileInfo.setFileType(FileInfo.TYPE_MUSIC);
                    ChatRoomManager.uploadFile(getAppContext(), mChatRoom, fileInfo);
                }
                break;

            case 2:
                for (FileInfo fileInfo : docFragment.getSelectFileList()) {
                    LogUtil.i("fileInfo", fileInfo);
                    LogUtil.i("fileInfo.isExist", FileUtil.isFileExists(fileInfo.getOriginPath()));
                    fileInfo.setFileType(FileInfo.TYPE_DOC);
                    ChatRoomManager.uploadFile(getAppContext(), mChatRoom, fileInfo);
                }
                break;

            default:

                break;
        }
        getHostActivity().finish();
    }

    private void initViewPager() {
        List<Fragment> fragmentList = new ArrayList<>();
        videoFragment = new FileVideoFragment();
        audioFragment = new FileMusicFragment();
        docFragment = new FileDocumentFragment();
        fragmentList.add(videoFragment);
        fragmentList.add(audioFragment);
        fragmentList.add(docFragment);
        FragmentManager manager = getSupportFragmentManager();
        containerVp.setAdapter(new MainVpAdapter(manager, fragmentList));
    }

}
