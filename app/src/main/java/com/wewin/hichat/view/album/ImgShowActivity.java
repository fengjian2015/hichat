package com.wewin.hichat.view.album;

import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomVpPageChangeListener;
import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.androidlib.utils.HttpUtil;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.SystemUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.model.db.entity.ImgUrl;
import com.wewin.hichat.view.album.adapter.ImgShowVpAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * 大图展示
 * Created by Darren on 2017/8/1.
 */

public class ImgShowActivity extends BaseActivity {

    private ViewPager containerVp;
    private List<ImgUrl> imgList;
    private ImageView backIv;
    private TextView titleTv;
    private int childPosition;
    private FrameLayout downloadFl;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_album_img_show;
    }

    @Override
    protected void initViews() {
        containerVp = findViewById(R.id.vp_img_show);
        backIv = findViewById(R.id.iv_img_show_back);
        titleTv = findViewById(R.id.tv_img_show_title);
        downloadFl = findViewById(R.id.fl_message_chat_img_download);
    }

    @Override
    protected void getIntentData() {
        childPosition = getIntent().getIntExtra(ImgUtil.IMG_CLICK_POSITION, 0);
        imgList = (List<ImgUrl>) getIntent().getSerializableExtra(ImgUtil.IMG_LIST_KEY);
    }

    @Override
    protected void initViewsData() {
        titleTv.setText(childPosition + 1 + "/" + imgList.size());
        initViewPager();
    }

    @Override
    protected void setListener() {
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImgShowActivity.this.finish();
            }
        });

        downloadFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = containerVp.getCurrentItem();
                if (!TextUtils.isEmpty(imgList.get(currentPosition).getFileName())){
                    downloadImg(imgList.get(currentPosition).getUrl(),
                            FileUtil.getSDDownloadPath(getAppContext()),
                            imgList.get(currentPosition).getFileName());
                }
            }
        });

        containerVp.addOnPageChangeListener(new CustomVpPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                if (imgList != null) {
                    titleTv.setText(position + 1 + "/" + imgList.size());
                }
            }
        });
    }

    private void initViewPager() {
        List<View> viewList = new ArrayList<>();
        for (int i = 0; i < imgList.size(); i++) {
            View view = View.inflate(this, R.layout.item_album_img_match_parent, null);
            ImageView imageView = view.findViewById(R.id.iv_img_show);
            Glide.with(this).load(imgList.get(i).getUrl()).into(imageView);
            viewList.add(imageView);
        }
        containerVp.setAdapter(new ImgShowVpAdapter(viewList));
        containerVp.setCurrentItem(childPosition);
    }

    private void downloadImg(String downloadPath, final String saveDir, final String fileName){
        HttpUtil.downloadFile(downloadPath, saveDir, fileName, new HttpUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                SystemUtil.notifyMediaStoreRefresh(getAppContext(), saveDir + fileName);
                ToastUtil.showShort(getAppContext(), R.string.has_download_into_album);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {

            }
        });
    }

}
