package com.wewin.hichat.view.contact.friend;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.datamanager.DataCache;
import com.wewin.hichat.androidlib.datamanager.SpCache;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.Subgroup;
import com.wewin.hichat.model.http.HttpContact;


/**
 * 临时会话查看好友信息
 * Created by Darren on 2019/3/27
 */
public class TemporaryFriendInfoActivity extends BaseActivity {

    private ImageView avatarIv;
    private TextView nameTv, signTv, genderTv, onlineStateTv, addFriendTv, sendMsgTv;
    private FriendInfo friendInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_temporary_friend_info;
    }

    @Override
    protected void getIntentData() {
        friendInfo = (FriendInfo)getIntent().getSerializableExtra(ContactCons.EXTRA_CONTACT_FRIEND_INFO);
    }

    @Override
    protected void initViews() {
        avatarIv = findViewById(R.id.iv_contact_temporary_friend_info_avatar);
        nameTv = findViewById(R.id.tv_contact_temporary_friend_info_name);
        signTv = findViewById(R.id.tv_contact_temporary_friend_info_sign);
        genderTv = findViewById(R.id.tv_contact_temporary_friend_info_gender);
        onlineStateTv = findViewById(R.id.tv_contact_temporary_friend_info_online_state);
        addFriendTv = findViewById(R.id.tv_contact_temporary_friend_info_add_friend);
        sendMsgTv = findViewById(R.id.tv_contact_temporary_friend_info_send_msg);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.friend_info);
        setLeftText(R.string.back);

        if (friendInfo != null){
            ImgUtil.load(this, friendInfo.getAvatar(), avatarIv);
            nameTv.setText(friendInfo.getUsername());
            signTv.setText(friendInfo.getSign());
            switch (friendInfo.getGender()){
                case 0:
                    genderTv.setText(getString(R.string.female));
                    break;
                case 1:
                    genderTv.setText(getString(R.string.male));
                    break;
                case 2:
                    genderTv.setText(getString(R.string.keep_secret));
                    break;
            }
        }
    }

    @Override
    protected void setListener() {
        addFriendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCache spCache = new SpCache(getAppContext());
                Subgroup friendSubgroup = (Subgroup) spCache.getObject(SpCons.SP_KEY_FRIEND_SUBGROUP);
                applyAddFriend(friendInfo.getId(), UserDao.user.getUsername() + getString(R.string.apply_add_friend),
                        friendSubgroup.getId());
            }
        });
        sendMsgTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventTrans.post(EventMsg.CONVERSATION_CHAT_FINISH);
                ChatRoomManager.startSingleRoomActivity(getHostActivity(), friendInfo);
            }
        });
    }

    private void applyAddFriend(String friendId, String verifyInfo, String groupingId) {
        HttpContact.applyAddFriend(friendId, verifyInfo, groupingId, UserDao.user.getId(),
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        ToastUtil.showShort(getAppContext(), R.string.apply_add_friend_sent);
                    }
                });
    }

}
