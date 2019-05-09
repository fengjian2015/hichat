package com.wewin.hichat.view.contact.group;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.Announcement;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.http.HttpContact;

/**
 * Created by Darren on 2019/3/26
 */
public class AnnouncementDetailActivity extends BaseActivity {

    private ImageView avatarIv, moreIv;
    private TextView nameTv, contentTv, timeTv, titleTv;
    private PopupWindow morePop;
    private Announcement announcement;
    private GroupInfo mGroupInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_group_announcement_detail;
    }

    @Override
    protected void initViews() {
        avatarIv = findViewById(R.id.iv_contact_announcement_detail_avatar);
        moreIv = findViewById(R.id.iv_contact_announcement_detail_more);
        nameTv = findViewById(R.id.tv_contact_announcement_detail_name);
        contentTv = findViewById(R.id.tv_contact_announcement_detail_content);
        timeTv = findViewById(R.id.tv_contact_announcement_detail_time);
        titleTv = findViewById(R.id.tv_contact_announcement_detail_title);
    }

    @Override
    protected void getIntentData() {
        mGroupInfo = (GroupInfo) getIntent()
                .getSerializableExtra(ContactCons.EXTRA_CONTACT_GROUP_INFO);
        announcement = (Announcement) getIntent()
                .getSerializableExtra(ContactCons.EXTRA_CONTACT_GROUP_ANNOUNCEMENT);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.group_announcement);
        setRightImg(R.drawable.announcement_plus_btn);
        setLeftText(R.string.back);

        if (announcement != null) {
            ImgUtil.load(this, announcement.getAccount().getAvatar(), avatarIv);
            nameTv.setText(announcement.getAccount().getUsername());
            titleTv.setText(announcement.getTitle());
            contentTv.setText(announcement.getContent());
            timeTv.setText(announcement.getAccount().getUsername() + "发表于" +
                    TimeUtil.timestampToStr(announcement.getPostTime(), "yyyy年MM月dd日"));
        }
    }

    @Override
    protected void setListener() {
        moreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMorePop();
            }
        });
    }

    @Override
    protected void onRightImgClick() {
        Intent intent = new Intent(getAppContext(), AnnouncementModifyActivity.class);
        intent.putExtra(ContactCons.EXTRA_CONTACT_GROUP_INFO, mGroupInfo);
        startActivity(intent);
    }

    private void showMorePop() {
        if (morePop == null) {
            initMorePop();
        }
        morePop.showAsDropDown(moreIv, -(int) (moreIv.getWidth() * 2.8),
                -(int) (moreIv.getHeight() / 1.2));
    }

    private void initMorePop() {
        View popView = View.inflate(getAppContext(), R.layout.pop_contact_group_announcement_more, null);
        TextView deleteTv = popView.findViewById(R.id.tv_pop_contact_group_announcement_delete);
        TextView editTv = popView.findViewById(R.id.tv_pop_contact_group_announcement_edit);

        morePop = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        morePop.setFocusable(true);

        deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morePop.dismiss();
                deleteAnnouncement(announcement.getId());
            }
        });

        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morePop.dismiss();
                Intent intent = new Intent(getApplicationContext(), AnnouncementModifyActivity.class);
                intent.putExtra(ContactCons.EXTRA_CONTACT_GROUP_INFO, mGroupInfo);
                intent.putExtra(ContactCons.EXTRA_CONTACT_GROUP_ANNOUNCEMENT, announcement);
                startActivity(intent);
            }
        });
    }

    private void deleteAnnouncement(String announcementId) {
        HttpContact.deleteAnnouncement(announcementId, new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                EventTrans.post(EventMsg.CONTACT_GROUP_ANNOUNCEMENT_REFRESH);
                getHostActivity().finish();
            }
        });
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        switch (msg.getKey()){
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
