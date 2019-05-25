package com.wewin.hichat.androidlib.manage;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import com.wewin.hichat.androidlib.widget.CustomCountDownTimer;

/**
 * 铃声/震动
 * Created by Darren on 2019/3/13
 */
public class RingVibrateManager {

    private static RingVibrateManager instance;
    private MediaPlayer mediaPlayer;
    private boolean canPlayRing = true;
    private boolean canVibrate = true;
    private CustomCountDownTimer ringCountDownTimer = new CustomCountDownTimer(3000, 3000){
        @Override
        protected void onFinish() {
            canPlayRing = true;
            ringCountDownTimer.cancel();
        }
    };
    private CustomCountDownTimer vibrateCountDownTimer = new CustomCountDownTimer(3000, 3000){
        @Override
        protected void onFinish() {
            canVibrate = true;
            vibrateCountDownTimer.cancel();
        }
    };

    private RingVibrateManager() {
    }

    public static RingVibrateManager getInstance() {
        if (instance == null) {
            synchronized (RingVibrateManager.class) {
                if (instance == null) {
                    instance = new RingVibrateManager();
                }
            }
        }
        return instance;
    }

    //获取手机默认来电铃声的Uri
    public void playCallRing(Context context){
        Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        playRing(context, ringUri);
    }

    //获取手机默认短信铃声的Uri
    public void playSmsRing(Context context){
        Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        playRing(context, ringUri);
    }

    //播放铃声
    private void playRing(Context context, Uri ringUri) {
        if (!canPlayRing){
            return;
        }
        try {
            canPlayRing = false;
            ringCountDownTimer.start();
            stop();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, ringUri);
            //播放铃声流
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

            //设置播放监听事件
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stop();
                }
            });
            //播放发生错误监听事件
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    stop();
                    return true;
                }
            });
            //播放器音量配置
            mediaPlayer.setVolume(1, 1);
            //是否循环播放
            mediaPlayer.setLooping(false);
            //准备及播放
            mediaPlayer.prepare();

        } catch (Exception e) {
            e.printStackTrace();
            //播放失败处理
            stop();
        }
    }

    //停止播放或播放失败处理
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //震动milliseconds毫秒
    public void vibrate(Context context) {
        if (!canVibrate){
            return;
        }
        canVibrate = false;
        vibrateCountDownTimer.start();
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if (vibrator != null){
            vibrator.vibrate(400);//400毫秒
        }
    }

    //取消震动
    public void vibrateCancle(Context context){
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if (vibrator != null){
            vibrator.cancel();
        }
    }

}
