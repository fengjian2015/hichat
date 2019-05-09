package com.wewin.hichat.androidlib.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.wewin.hichat.component.constant.SpCons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * Created by Darren on 2018/12/17.
 */
public class HttpUtil {

    private static OkHttpClient client;

    private HttpUtil() {
    }

    public static void init(Context context) {
        if (client == null) {
            synchronized (HttpUtil.class) {
                if (client == null)
                    client = getClient(context);
            }
        }
    }

    private static OkHttpClient getClient(Context context) {
        return new OkHttpClient.Builder()
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .cookieJar(new CookieJarImpl(context))
                .build();
    }

    private static class CookieJarImpl implements CookieJar {

        private Context context;
        private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

        CookieJarImpl(Context context) {
            this.context = context;
        }

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookieList) {
            if (!cookieList.isEmpty()) {
                Cookie cookieResult = null;
                for (Cookie cookie : cookieList) {
                    if (cookie.name().equals("cuid")) {
                        cookieResult = cookie;
                        break;
                    }
                }
                if (cookieResult != null) {
                    String cuid = cookieResult.value();
                    String domain = cookieResult.domain();
                    SpCons.setCuid(context, cuid);
                    SpCons.setDomain(context, domain);
                    cookieStore.put(HttpUrl.parse(url.encodedPath()), cookieList);
                }
//                LogUtil.i("saveFromResponse", cookieStore);
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            String spCuid = SpCons.getCuid(context);
            String spDomain = SpCons.getDomain(context);
            List<Cookie> cookieList;
            if (!TextUtils.isEmpty(spCuid)) {
                Cookie cookie = new Cookie.Builder()
                        .domain(spDomain)
                        .name("cuid")
                        .value(spCuid).build();
                cookieList = new ArrayList<>();
                cookieList.add(cookie);
            } else {
                cookieList = cookieStore.get(HttpUrl.parse(url.encodedPath()));
            }
//            LogUtil.i("loadForRequest", cookieList);
            return cookieList != null ? cookieList : new ArrayList<Cookie>();
        }
    }

    /**
     * Get请求
     */
    public static void get(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * Post请求发送键值对数据
     */
    public static void post(String url, Map<String, String> mapParams, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : mapParams.keySet()) {
            if (!TextUtils.isEmpty(key) && mapParams.get(key) != null) {
                builder.add(key, mapParams.get(key));
            }
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * Post请求发送JSON数据
     */
    public static void post(String url, String jsonParams, Callback callback) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8")
                , jsonParams);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 上传文件和表单参数
     */
    public static void postFileFormData(String url, String fileParamName, File file,
                                        Map<String, String> paramMap, Callback callback) {
        List<File> fileList = new ArrayList<>();
        fileList.add(file);
        postFileFormData(url, fileParamName, fileList, paramMap, callback);
    }

    public static void postFileFormData(String url, String fileParamName, List<File> fileList,
                                        Map<String, String> paramMap, Callback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (String key : paramMap.keySet()) {
            builder.addFormDataPart(key, paramMap.get(key));
        }
        for (final File file : fileList) {
            final MediaType mediaType = MediaType.parse(judgeType(file.getAbsolutePath()));
            builder.addFormDataPart(fileParamName, encode(file.getName()),
                    getCustomRequestBody(mediaType, file, new ProgressListener() {
                        @Override
                        public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                            LogUtil.i((totalBytes - remainingBytes) * 100 / totalBytes + "%");
                        }
                    }));
        }
        //发出请求参数
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 根据文件路径判断MediaType
     */
    private static String judgeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 下载文件
     */
    public static void downloadFile(String downloadPath, String saveDir, String saveFileName,
                                    final OnDownloadListener listener) {
        // 储存下载文件的目录
        FileUtil.createDir(saveDir);
        final String savePath = saveDir + saveFileName;
        Request request = new Request.Builder().url(downloadPath).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    listener.onDownloadFailed();
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len;
                FileOutputStream fos = null;

                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        is = body.byteStream();
                        long total = body.contentLength();
                        File file = new File(savePath);
                        fos = new FileOutputStream(file);
                        long sum = 0;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            sum += len;
                            int progress = (int) (sum * 1.0f / total * 100);
                            // 下载中
                            if (listener != null) {
                                listener.onDownloading(progress);
                            }
                        }
                        fos.flush();
                        // 下载完成
                        if (listener != null) {
                            listener.onDownloadSuccess();
                        }

                    } else {
                        if (listener != null) {
                            listener.onDownloadFailed();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onDownloadFailed();
                    }

                } finally {
                    try {
                        if (is != null)
                            is.close();
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }

    //带进度的OkHttp RequestBody
    private static RequestBody getCustomRequestBody(final MediaType contentType, final File file,
                                                    final ProgressListener listener) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(@NonNull BufferedSink bufferedSink) throws IOException {
                try {
                    Source source = Okio.source(file);
                    bufferedSink.writeAll(source);
                    /*Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 1024 * 4)) != -1; ) {
                        bufferedSink.write(buf, readCount);
                        listener.onProgress(contentLength(), remaining -= readCount, remaining == 0);
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private interface ProgressListener {
        void onProgress(long totalBytes, long remainingBytes, boolean done);
    }

    // 对包含中文的字符串进行转码，此为UTF-8。服务器那边要进行一次解码
    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class FileRequestBody extends RequestBody {
        private RequestBody mRequestBody;
        private LoadingListener mLoadingListener;
        private long mContentLength;

        public FileRequestBody(RequestBody requestBody, LoadingListener loadingListener) {
            mRequestBody = requestBody;
            mLoadingListener = loadingListener;
        }

        //文件的总长度
        @Override
        public long contentLength() {
            try {
                if (mContentLength == 0)
                    mContentLength = mRequestBody.contentLength();
                return mContentLength;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        public MediaType contentType() {
            return mRequestBody.contentType();
        }

        @Override
        public void writeTo(@NonNull BufferedSink sink) throws IOException {
            ByteSink byteSink = new ByteSink(sink);
            BufferedSink mBufferedSink = Okio.buffer(byteSink);
            mRequestBody.writeTo(mBufferedSink);
            mBufferedSink.flush();
        }


        private final class ByteSink extends ForwardingSink {
            //已经上传的长度
            private long mByteLength = 0L;

            ByteSink(Sink delegate) {
                super(delegate);
            }

            @Override
            public void write(@NonNull Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                mByteLength += byteCount;
                mLoadingListener.onProgress(mByteLength, contentLength());
            }
        }

        public interface LoadingListener {
            void onProgress(long currentLength, long totalLength);
        }
    }

}
