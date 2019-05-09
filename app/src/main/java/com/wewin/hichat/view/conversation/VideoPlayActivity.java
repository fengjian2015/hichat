package com.wewin.hichat.view.conversation;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.androidlib.utils.HttpUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.SystemUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.model.db.entity.FileInfo;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Darren on 2019/2/25
 */
public class VideoPlayActivity extends BaseActivity {

    private SimpleExoPlayerView mExoPlayerView;
    private SimpleExoPlayer mSimpleExoPlayer;
    private Uri playerUri;
    private ProgressBar mProgressBar;
    private FrameLayout downloadFl;
    private FileInfo fileInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_chat_video_play;
    }

    @Override
    protected void initViews() {
        mProgressBar = findViewById(R.id.progressBar);
        mExoPlayerView = findViewById(R.id.sep_message_chat_exo_player);
        downloadFl = findViewById(R.id.fl_message_chat_video_download);
    }

    @Override
    protected void getIntentData() {
        fileInfo = (FileInfo) getIntent().getSerializableExtra(ContactCons.EXTRA_MESSAGE_CHAT_FILE);
        if (fileInfo == null){
            return;
        }
        if (!TextUtils.isEmpty(fileInfo.getOriginPath())) {
            playerUri = Uri.parse(fileInfo.getOriginPath());
        } else if (!TextUtils.isEmpty(fileInfo.getSavePath())) {
            playerUri = Uri.parse(fileInfo.getSavePath());
        } else if (!TextUtils.isEmpty(fileInfo.getDownloadPath())) {
            playerUri = Uri.parse(fileInfo.getDownloadPath());
        }
        LogUtil.i("playerUri", playerUri);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(0);
        initPlayer();
        playVideo();
    }

    @Override
    protected void setListener() {
        downloadFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadVideo(fileInfo.getDownloadPath(),
                        FileUtil.getSDDownloadPath(getAppContext()), fileInfo.getFileName());
            }
        });
    }

    private void downloadVideo(String downloadPath, final String saveDir, final String fileName) {
        HttpUtil.downloadFile(downloadPath, saveDir, fileName,
                new HttpUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess() {
                        ToastUtil.showShort(getAppContext(), R.string.has_download_into_album);
                        SystemUtil.notifyMediaStoreRefresh(getAppContext(), saveDir + fileName);
                        LogUtil.i("onDownloadSuccess");
                    }

                    @Override
                    public void onDownloading(int progress) {

                    }

                    @Override
                    public void onDownloadFailed() {

                    }
                });
    }

    //初始化player
    private void initPlayer() {
        //1.创建一个默认的 TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTackSelectionFactory);
        //2.创建ExoPlayer
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        //3.为SimpleExoPlayer设置播放器
        mExoPlayerView.setPlayer(mSimpleExoPlayer);
    }

    private void playVideo() {
        //测量播放过程中的带宽。 如果不需要，可以为null。
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // 生成加载媒体数据的DataSource实例。
        DataSource.Factory dataSourceFactory
                = new DefaultDataSourceFactory(VideoPlayActivity.this,
                Util.getUserAgent(VideoPlayActivity.this, "useExoPlayer"), bandwidthMeter);
        // 生成用于解析媒体数据的Extractor实例。
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // MediaSource代表要播放的媒体。
        MediaSource videoSource = new ExtractorMediaSource(playerUri, dataSourceFactory, extractorsFactory,
                null, null);
        //Prepare the player with the source.
        mSimpleExoPlayer.prepare(videoSource);
        //添加监听的listener
        mSimpleExoPlayer.addListener(eventListener);
        mSimpleExoPlayer.setPlayWhenReady(true);
    }

    private Player.EventListener eventListener = new Player.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            LogUtil.i("onTimelineChanged");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            LogUtil.i("onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            LogUtil.i("onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            LogUtil.i("onPlayerStateChanged: playWhenReady = " + String.valueOf(playWhenReady)
                    + " playbackState = " + playbackState);
            switch (playbackState) {
                case Player.STATE_ENDED:
                    LogUtil.i("Playback ended!");
                    //Stop playback and return to start position
                    setPlayPause(false);
                    mSimpleExoPlayer.seekTo(0);
                    break;
                case Player.STATE_READY:
                    mProgressBar.setVisibility(View.GONE);
                    LogUtil.i("ExoPlayer ready! pos: " + mSimpleExoPlayer.getCurrentPosition()
                            + " max: " + stringForTime((int) mSimpleExoPlayer.getDuration()));
                    setProgress(0);
                    break;
                case Player.STATE_BUFFERING:
                    LogUtil.i("Playback buffering!");
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case Player.STATE_IDLE:
                    LogUtil.i("ExoPlayer idle!");
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            LogUtil.i("onRepeatModeChanged");
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            LogUtil.i("onPlaybackError: " + error.getMessage());
        }

        @Override
        public void onPositionDiscontinuity() {
            LogUtil.i("onPositionDiscontinuity");
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            LogUtil.i("MainActivity.onPlaybackParametersChanged." + playbackParameters.toString());
        }
    };

    /**
     * Starts or stops playback. Also takes care of the Play/Pause button toggling
     */
    private void setPlayPause(boolean play) {
        mSimpleExoPlayer.setPlayWhenReady(play);
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    protected void onPause() {
        mSimpleExoPlayer.stop();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mSimpleExoPlayer.release();
        super.onStop();
    }

}
