package com.wewin.hichat.view.album.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Toast;
import com.wewin.hichat.androidlib.permission.Permission;
import com.wewin.hichat.androidlib.permission.PermissionCallback;
import com.wewin.hichat.androidlib.permission.Rigger;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.view.album.model.ImageFolder;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Darren on 2019/1/7.
 */
public class ImgScanUtil {

    private HashSet<String> mDirPathList = new HashSet<>();
    //扫描拿到所有的图片文件夹
    private List<ImageFolder> mImageFolderList = new ArrayList<>();
    //存储文件夹中的图片数量
    private int mPicsSize;
    //图片数量最多的文件夹
    private File mImgDir;
    private OnImgScanListener imgScanListener;
    private Handler mHandler = new Handler();
    private Activity context;

    public interface OnImgScanListener {
        void imgScan(List<ImageFolder> mImageFolders, int mPicsSize, File mImgDir);
    }

    public void setOnImgScanListener(OnImgScanListener imgScanListener) {
        this.imgScanListener = imgScanListener;
    }

    public ImgScanUtil (Activity context){
        this.context = context;
    }

    public void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
//        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
        Rigger.on(context).permissions(Permission.WRITE_EXTERNAL_STORAGE)
                .start(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        childThread.start();
                    }

                    @Override
                    public void onDenied(HashMap<String, Boolean> permissions) {

                    }
                });
    }

    //利用ContentProvider扫描手机中的图片，此方法在运行在子线程中完成图片的扫描，最终获得jpg最多的那个文件夹
    private Thread childThread = new Thread(new Runnable() {
        @Override
        public void run() {
            String firstImage = null;
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver = context.getContentResolver();

            // 只查询jpeg和png的图片
            Cursor mCursor = mContentResolver.query(mImageUri, null,
                    MediaStore.Images.Media.MIME_TYPE + " = ? or "
                            + MediaStore.Images.Media.MIME_TYPE + " = ? ",
                    new String[]{"image/jpeg", "image/png"},
                    MediaStore.Images.Media.DATE_MODIFIED);

            if (mCursor != null && mCursor.getCount() > 0) {
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFolder imageFolder;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPathList.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPathList.add(dirPath);
                        // 初始化imageFolder
                        imageFolder = new ImageFolder();
                        imageFolder.setDir(dirPath);
                        imageFolder.setFirstImagePath(path);
                    }

                    String[] fileNameArr = parentFile.list();
                    List<String> imgList = new ArrayList<>();
                    if (fileNameArr != null && fileNameArr.length > 0) {
                        for (String s : fileNameArr) {
                            if (s.endsWith(".jpg") || s.endsWith(".png")|| s.endsWith(".jpeg")) {
                                imgList.add(s);
                            }
                        }
                    }

                    int picSize = imgList.size();
                    imageFolder.setCount(picSize);
                    mImageFolderList.add(imageFolder);

                    if (picSize > mPicsSize) {
                        mPicsSize = picSize;
                        mImgDir = parentFile;
                    }
                }
                mCursor.close();
            }
            // 扫描完成，辅助的HashSet也就可以释放内存了
            mDirPathList = null;
            // 通知Handler扫描图片完成
            mHandler.sendEmptyMessage(0x110);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (imgScanListener != null) {
                        imgScanListener.imgScan(mImageFolderList, mPicsSize, mImgDir);
                    }
                }
            });
        }
    });


}
