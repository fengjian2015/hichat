package com.wewin.hichat.androidlib.manage;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import com.wewin.hichat.androidlib.utils.LogUtil;
import java.io.File;

/**
 * Created by Darren on 2019/4/29
 **/
public class MediaScanManager {

    private MediaScannerConnection mConn = null;
    private ScannerClient mClient = null;
    private File mFile = null;
    private String mMimeType = null;


    public MediaScanManager(Context context){
        if (mClient == null) {
            mClient = new ScannerClient();
        }
        if (mConn == null) {
            mConn = new MediaScannerConnection(context, mClient);
        }
    }

    class ScannerClient implements MediaScannerConnection.MediaScannerConnectionClient {
        @Override
        public void onMediaScannerConnected() {
            if (mFile == null) {
                return;
            }
            scan(mFile, mMimeType);
        }
        @Override
        public void onScanCompleted(String path, Uri uri) {
            mConn.disconnect();
        }

        private void scan(File file, String type) {
            LogUtil.i("scan ", file.getAbsolutePath());
            if (file.isFile()) {
                mConn.scanFile(file.getAbsolutePath(), null);
                return;
            }
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File f : file.listFiles()) {
                scan(f, type);
            }
        }
    }

    public void scanFile(File file, String mimeType) {
        mFile = file;
        mMimeType = mimeType;
        mConn.connect();
    }

}
