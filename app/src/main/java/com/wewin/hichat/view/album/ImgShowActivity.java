package com.wewin.hichat.view.album;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wewin.hichat.MainActivity;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.impl.CustomVpPageChangeListener;
import com.wewin.hichat.androidlib.rxjava.OnRxJavaProcessListener;
import com.wewin.hichat.androidlib.rxjava.RxJavaObserver;
import com.wewin.hichat.androidlib.rxjava.RxJavaScheduler;
import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.androidlib.utils.HttpUtil;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.SystemUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.androidlib.widget.ZoomImageView;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.manager.VoiceCallManager;
import com.wewin.hichat.model.db.entity.ImgUrl;
import com.wewin.hichat.view.album.adapter.ImgShowVpAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.ObservableEmitter;

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
    private boolean showDownload=false;

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
        showDownload=getIntent().getBooleanExtra(ImgUtil.IMG_DONWLOAD,false);
    }

    @Override
    protected void initViewsData() {
        titleTv.setText(childPosition + 1 + "/" + imgList.size());
        if (showDownload){
            downloadFl.setVisibility(View.VISIBLE);
        }else {
            downloadFl.setVisibility(View.GONE);
        }
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
                    getImgPathFromCache(imgList.get(currentPosition).getUrl(),FileUtil.getSDDownloadPath(getAppContext()), imgList.get(currentPosition).getFileName());
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
    List<View> viewList = new ArrayList<>();
    private void initViewPager() {
        for (int i = 0; i < imgList.size(); i++) {
            final View view = View.inflate(this, R.layout.item_album_img_match_parent, null);
            final ZoomImageView imageView = view.findViewById(R.id.iv_img_show);
            Glide.with(this).load(imgList.get(i).getUrl()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    imageView.setImageDrawable(resource);
                    return false;
                }
            }).into(imageView);
            viewList.add(imageView);
        }
        containerVp.setAdapter(new ImgShowVpAdapter(viewList));
        containerVp.setCurrentItem(childPosition);
    }

    private File oldFile;
    private void getImgPathFromCache(final String url, final String saveDir, final String fileName) {
        RxJavaScheduler.execute(new OnRxJavaProcessListener() {
            @Override
            public void process(ObservableEmitter<Object> emitter) {
                try {
                    oldFile= Glide.with(ImgShowActivity.this)
                            .asFile()
                            .load(url)
                            .submit()
                            .get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Log.e("Tag", "path-->" + oldFile.getPath());
                FileUtil.copyFile(oldFile.getPath(),saveDir+ fileName);
            }
        }, new RxJavaObserver<Object>() {
            @Override
            public void onComplete() {
                super.onComplete();
                SystemUtil.notifyMediaStoreRefresh(getAppContext(), saveDir + fileName);
                ToastUtil.showShort(getAppContext(), R.string.has_download_into_album);
            }
        });
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
