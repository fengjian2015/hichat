package com.wewin.hichat.view.contact.group;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.Announcement;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.http.HttpContact;

/**
 * 编辑群公告
 * Created by Darren on 2019/1/15.
 */
public class AnnouncementModifyActivity extends BaseActivity {

    private GroupInfo mGroupInfo;
    private Announcement announcement;
    private EditText titleEt, contentEt;
    private TextView titleCountTv, contentCountTv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_group_announcement_edit;
    }

    @Override
    protected void initViews() {
        titleEt = findViewById(R.id.et_contact_group_announcement_title);
        contentEt = findViewById(R.id.et_contact_group_announcement_content);
        titleCountTv = findViewById(R.id.tv_contact_group_announcement_title_count);
        contentCountTv = findViewById(R.id.tv_contact_group_announcement_content_count);
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
        setRightTv(R.string.release);
        setLeftText(R.string.group_announcement);
    }

    @Override
    protected void setListener() {
        titleEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                titleCountTv.setText(s.toString().trim().length() + "/40");
            }
        });

        contentEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                contentCountTv.setText(s.toString().trim().length() + "/500");
            }
        });

        if (announcement != null) {
            setCenterTitle(R.string.announcement_edit);
            titleEt.setText(announcement.getTitle());
            contentEt.setText(announcement.getContent());
        } else {
            setCenterTitle(R.string.announcement_create);
        }
    }

    @Override
    protected void onRightTvClick() {
        String title = titleEt.getText().toString().trim();
        String content = contentEt.getText().toString().trim();

        if (title.length() < 4) {
            ToastUtil.showShort(this, R.string.title_min_length_limit);

        } else if (content.length() < 15) {
            ToastUtil.showShort(this, R.string.content_min_length_limit);

        } else {
            createAnnouncement(title, content);
        }
    }

    private void createAnnouncement(final String title, final String content) {
        HttpContact.createAnnouncement(SpCons.getUser(getAppContext()).getId(), content, mGroupInfo.getId(), title,
                new HttpCallBack(this, ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        ToastUtil.showShort(getApplicationContext(), R.string.commit_success);
                        announcement = new Announcement();
                        announcement.setTitle(title);
                        announcement.setContent(content);
                        EventTrans.post(EventMsg.CONTACT_GROUP_ANNOUNCEMENT_REFRESH);
                        finish();
                    }
                });
    }


    @Override
    public void onEventTrans(EventMsg msg) {
        switch (msg.getKey()) {
            case EventMsg.CONTACT_GROUP_REMOVE_MEMBER:
                GroupInfo groupInfo1 = (GroupInfo) msg.getData();
                FriendInfo receiver = (FriendInfo) msg.getThirdData();
                if (groupInfo1 == null || TextUtils.isEmpty(groupInfo1.getId())
                        || receiver == null || TextUtils.isEmpty(receiver.getId())) {
                    return;
                }
                if (groupInfo1.getId().equals(mGroupInfo.getId())
                        && receiver.getId().equals(SpCons.getUser(getAppContext()).getId())) {
                    ToastUtil.showShort(getAppContext(), R.string.you_are_moved_out_from_group);
                    getHostActivity().finish();
                }
                break;

            case EventMsg.CONTACT_GROUP_DISBAND:
                String groupId1 = msg.getData().toString();
                if (!TextUtils.isEmpty(groupId1) && groupId1.equals(mGroupInfo.getId())) {
                    getHostActivity().finish();
                }
                break;

            default:

                break;

        }
    }

}
