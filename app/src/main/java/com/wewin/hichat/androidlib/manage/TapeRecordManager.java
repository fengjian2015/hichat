package com.wewin.hichat.androidlib.manage;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import com.czt.mp3recorder.MP3Recorder;
import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import java.io.File;
import java.io.IOException;

/**
 * 录音
 * Created by Darren on 2019/2/2
 */
public class TapeRecordManager {

    private static TapeRecordManager instance;
    private String currentVoicePath;
    private MediaPlayer mediaPlayer;
    private MP3Recorder mRecorder;

    public interface OnVoiceListener {
        void stop(String savePath);
    }

    public static TapeRecordManager getInstance() {
        if (instance == null) {
            synchronized (TapeRecordManager.class) {
                if (instance == null) {
                    instance = new TapeRecordManager();
                }
            }
        }
        return instance;
    }

    public void startRecord(Context context) {
        try {
            String savePath = FileUtil.getSDTapeRecordPath(context);
            FileUtil.createDir(savePath);
            FileUtil.createDir(savePath + ".nomedia");
            String filename = TimeUtil.getCurrentTime("yyMMddHHmmss") + "tape.mp3";
            File file = new File(savePath, filename);
            currentVoicePath = file.getAbsolutePath();
            mRecorder = new MP3Recorder(file);
            mRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord(OnVoiceListener voiceListener) {
        if (mRecorder == null) {
            return;
        }
        //5.0以上在调用stop的时候会报错，捕获异常清理
        try {
            mRecorder.stop();
            if (voiceListener != null) {
                voiceListener.stop(currentVoicePath);
            }

        } catch (Exception e) {
            e.printStackTrace();
            File file = new File(currentVoicePath);
            if (file.exists()){
                file.delete();
            }
        }
    }

    public boolean getFlushAndRelease(){
        if (mRecorder!=null) {
            return mRecorder.getFlushAndRelease();
        }else {
            return true;
        }
    }

    public void playRecord(String path, MediaPlayer.OnCompletionListener completionListener, final MediaPlayer.OnErrorListener errorListener) {
        try {
            //初始化播放器
            mediaPlayer = new MediaPlayer();
            //设置播放音频数据文件
            mediaPlayer.setDataSource(path);
            //设置播放监听事件
            if (completionListener != null) {
                mediaPlayer.setOnCompletionListener(completionListener);
            }
            //播放发生错误监听事件
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    playEndOrFail();
                    errorListener.onError(mediaPlayer, i, i1);
                    return true;
                }
            });
            //播放器音量配置
            mediaPlayer.setVolume(1, 1);
            //是否循环播放
            mediaPlayer.setLooping(false);
            //准备及播放
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
            //播放失败处理
            errorListener.onError(mediaPlayer, 0, 0);
            playEndOrFail();
        }
    }

    /**
     * 停止播放或播放失败处理
     */
    public void playEndOrFail() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 取消录音
     */
    public void cancelRecord() {
        mRecorder.stop();
        if (currentVoicePath != null) {
            File file = new File(currentVoicePath);
            file.delete();
        }
    }

}
