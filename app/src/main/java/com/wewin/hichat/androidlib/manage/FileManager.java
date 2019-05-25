package com.wewin.hichat.androidlib.manage;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.model.db.entity.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darren on 2019/1/25
 */
public class FileManager {

    private static FileManager mInstance;
    private static ContentResolver mContentResolver;
    private static final String GOOGLE_FILE_URI = "https://docs.google.com/viewer?url=";
    public static final String MICROSOFT_FILE_URI = "https://view.officeapps.live.com/op/view.aspx?src=";


    public static FileManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (FileUtil.class) {
                if (mInstance == null) {
                    mInstance = new FileManager();
                    mContentResolver = context.getContentResolver();
                }
            }
        }
        return mInstance;
    }

    /**
     * 通过文件类型得到相应文件的集合
     **/
    public List<FileInfo> getFilesByType(Context context, int fileType) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        Cursor cursor = mContentResolver.query(MediaStore.Files.getContentUri("external"),
                new String[]{"_id", "_data", "_size"}, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            String filePath = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Files.FileColumns.DATA));
            if (getFileType(filePath) == fileType) {
                if (TextUtils.isEmpty(filePath) || !FileUtil.isFileExists(filePath)
                        ) {
                    continue;
                }
                FileInfo fileInfo = new FileInfo();
                fileInfo.setOriginPath(filePath);
                File file = new File(filePath);
                if (TextUtils.isEmpty(file.getName()) || file.getName().startsWith(".")
                        || file.getName().endsWith(".")
                        || file.getName().toLowerCase().contains("log")
                        || file.getName().toLowerCase().contains("track")
                        || file.getName().toLowerCase().contains("download")
                        || file.getName().toLowerCase().contains("com.tencent")
                        || file.getName().toLowerCase().contains("config")
                        || file.getName().toLowerCase().contains("crash")
                        || file.getName().toLowerCase().contains("test")
                        || file.getName().length() > 30
                        || file.length() <= 100) {
                    continue;
                }
                fileInfo.setFileName(file.getName());
                fileInfo.setFileLength(file.length());
                fileInfo.setCreateTime(file.lastModified());
                fileInfo.setFileType(FileInfo.TYPE_DOC);
                fileInfoList.add(fileInfo);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return fileInfoList;
    }

    public List<FileInfo> getVideoList() {
        List<FileInfo> videoList = new ArrayList<>();
        // 视频其他信息的查询条件
        String[] mediaColumns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION};
        Cursor cursor = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            FileInfo info = new FileInfo();
            String path = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            File file = new File(path);
            if (TextUtils.isEmpty(file.getName()) || (!file.getName().endsWith(".mp4")
                    && !file.getName().endsWith(".avi")
                    && !file.getName().endsWith(".wav")
                    && !file.getName().endsWith(".mpg"))
                    || file.length() < 1024) {
                continue;
            }
            info.setFileName(file.getName());
            info.setOriginPath(path);
            info.setDuration(cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
            info.setFileLength(file.length());
            info.setFileType(FileInfo.TYPE_VIDEO);
            info.setCreateTime(file.lastModified());
            videoList.add(info);
        }
        if (cursor != null) {
            cursor.close();
        }
        return videoList;
    }

    /**
     * 获取本机音乐列表
     */
    public List<FileInfo> getMusicList() {
        ArrayList<FileInfo> musicInfoList = new ArrayList<>();
        Cursor cursor = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        while (cursor != null && cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径
            if (!FileUtil.isFileExists(path)) {
                continue;
            }
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
            if (TextUtils.isEmpty(name) || !name.endsWith(".mp3")) {
                continue;
            }
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
            long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时长
            if (size < 1024 * 1024 || duration < 5){
                continue;
            }
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(name);
            fileInfo.setOriginPath(path);
            fileInfo.setFileLength(size);
            fileInfo.setDuration(duration);
            fileInfo.setFileType(FileInfo.TYPE_MUSIC);
            fileInfo.setCreateTime(new File(path).lastModified());
            musicInfoList.add(fileInfo);
        }
        if (cursor != null) {
            cursor.close();
        }
        return musicInfoList;
    }

    private int getFileType(String path) {
        path = path.toLowerCase();
        if (path.endsWith("txt") || path.endsWith(".doc") || path.endsWith(".docx")
                || path.endsWith(".xls") || path.endsWith(".xlsx") || path.endsWith(".ppt")
                || path.endsWith(".pptx")) {
            return FileInfo.TYPE_DOC;
        } else if (path.endsWith(".apk")) {
            return FileInfo.TYPE_APK;
        } else if (path.endsWith(".zip") || path.endsWith(".rar") || path.endsWith(".tar")
                || path.endsWith(".gz")) {
            return FileInfo.TYPE_ZIP;
        } else if (path.endsWith(".mp3")) {
            return FileInfo.TYPE_MUSIC;
        } else if (path.endsWith(".mp4")) {
            return FileInfo.TYPE_VIDEO;
        } else {
            return -1;
        }
    }


}
