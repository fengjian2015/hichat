package com.wewin.hichat.androidlib.datamanager;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 文件缓存
 * Created by Darren on 2018/8/1.
 */

public class FileCache {

    private static final String FOLD_NAME = "/wanShare";
    //图片文件夹
    private static final String IMG_DIR = "/imgDir";
    //文本文件夹
    private static final String TEXT_DIR = "/textDir";
    //语音文件夹
    private static final String VOICE_DIR = "/voiceDir";
    //视频文件夹
    private static final String VIDEO_DIR = "/videoDir";
    //创建成功
    private static final int FLAG_SUCCESS = 1;
    //已存在
    private static final int FLAG_EXISTS = 2;
    //创建失败
    private static final int FLAG_FAILED = 3;

    public static void saveFile(String fileName, String path, String content) {
        try {
            int result = createDir(path);
            if (result != 3) {
                File file = new File(path, fileName);
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] encryptData = EnDecryptUtil.encrypt(content);
                if (encryptData != null && encryptData.length > 0) {
                    outputStream.write(encryptData);
                }
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getString(String fileName, String path) {
        try {
            File file = new File(path, fileName);
            if (file.exists()){
                FileInputStream inputStream = new FileInputStream(file);
                int size = inputStream.available();
                byte[] data = new byte[size];
                inputStream.read(data);
                inputStream.close();
                byte[] decryptData = EnDecryptUtil.decrypt(data);
                if (decryptData != null && decryptData.length > 0) {
                    return new String(decryptData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //应用在内部存储中的files路径
    public static String getInternalFilePath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    public static String getInternalFileImgPath(Context context){
        return getInternalFilePath(context) + IMG_DIR;
    }

    public static String getInternalFileTextPath(Context context){
        return getInternalFilePath(context) + TEXT_DIR;
    }

    public static String getInternalFileVoicePath(Context context){
        return getInternalFilePath(context) + VOICE_DIR;
    }

    public static String getInternalFileVideoPath(Context context){
        return getInternalFilePath(context) + VIDEO_DIR;
    }

    //应用在内部存储中的cache路径
    public static String getInternalCachePath(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    public static String getInternalCacheImgPath(Context context){
        return getInternalCachePath(context) + IMG_DIR;
    }

    public static String getInternalCacheTextPath(Context context){
        return getInternalCachePath(context) + TEXT_DIR;
    }

    public static String getInternalCacheVoicePath(Context context){
        return getInternalCachePath(context) + VOICE_DIR;
    }

    public static String getInternalCacheVideoPath(Context context){
        return getInternalCachePath(context) + VIDEO_DIR;
    }

    //应用在外部存储中的files路径
    public static String getExternalFilePath(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = context.getExternalFilesDir("");
            if (file != null) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    public static String getExternalFileImgPath(Context context){
        if (!TextUtils.isEmpty(getExternalFilePath(context))){
            return getExternalFilePath(context) + IMG_DIR;
        }
        return null;
    }

    public static String getExternalFileTextPath(Context context){
        if (!TextUtils.isEmpty(getExternalFilePath(context))){
            return getExternalFilePath(context) + TEXT_DIR;
        }
        return null;
    }

    public static String getExternalFileVoicePath(Context context){
        if (!TextUtils.isEmpty(getExternalFilePath(context))){
            return getExternalFilePath(context) + VOICE_DIR;
        }
        return null;
    }

    public static String getExternalFileVideoPath(Context context){
        if (!TextUtils.isEmpty(getExternalFilePath(context))){
            return getExternalFilePath(context) + VIDEO_DIR;
        }
        return null;
    }

    //应用在外部存储中的cache路径
    public static String getExternalCachePath(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = context.getExternalCacheDir();
            if (file != null) {
                return context.getExternalCacheDir().getAbsolutePath();
            }
        }
        return null;
    }

    public static String getExternalCacheImgPath(Context context){
        if (!TextUtils.isEmpty(getExternalCachePath(context))){
            return getExternalCachePath(context) + IMG_DIR;
        }
        return null;
    }

    public static String getExternalCacheTextPath(Context context){
        if (!TextUtils.isEmpty(getExternalCachePath(context))){
            return getExternalCachePath(context) + TEXT_DIR;
        }
        return null;
    }

    public static String getExternalCacheVoicePath(Context context){
        if (!TextUtils.isEmpty(getExternalCachePath(context))){
            return getExternalCachePath(context) + VOICE_DIR;
        }
        return null;
    }

    public static String getExternalCacheVideoPath(Context context){
        if (!TextUtils.isEmpty(getExternalCachePath(context))){
            return getExternalCachePath(context) + VIDEO_DIR;
        }
        return null;
    }

    //外部存储的根路径
    public static String getExternalDirectoryPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (!TextUtils.isEmpty(path)) {
                return path + FOLD_NAME;
            } else {
                return Environment.getExternalStoragePublicDirectory("").getAbsolutePath() + FOLD_NAME;
            }
        }
        return null;
    }

    public static String getExternalDirectoryImgPath(){
        if (!TextUtils.isEmpty(getExternalDirectoryPath())){
            return getExternalDirectoryPath() + IMG_DIR;
        }
        return null;
    }

    public static String getExternalDirectoryTextPath(){
        if (!TextUtils.isEmpty(getExternalDirectoryPath())){
            return getExternalDirectoryPath() + TEXT_DIR;
        }
        return null;
    }

    public static String getExternalDirectoryVoicePath(){
        if (!TextUtils.isEmpty(getExternalDirectoryPath())){
            return getExternalDirectoryPath() + VOICE_DIR;
        }
        return null;
    }

    public static String getExternalDirectoryVideoPath(){
        if (!TextUtils.isEmpty(getExternalDirectoryPath())){
            return getExternalDirectoryPath() + VIDEO_DIR;
        }
        return null;
    }

    public static void deleteDir(String path) {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteDir(path);
            }
        }
        dir.delete();
    }

    private static int createDir(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            return FLAG_EXISTS;
        }
        if (!dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }
        if (dir.mkdirs()) {
            return FLAG_SUCCESS;
        }
        return FLAG_FAILED;
    }

}
