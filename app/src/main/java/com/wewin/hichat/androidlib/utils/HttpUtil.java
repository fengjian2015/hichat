package com.wewin.hichat.androidlib.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.wewin.hichat.component.constant.HttpCons;
import com.wewin.hichat.component.constant.SpCons;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

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

/**
 * Created by Darren on 2018/12/17.
 */
public class HttpUtil {

    private static OkHttpClient okHttpClient;

    private HttpUtil() {
    }

    public static void init(Context context) {
        if (okHttpClient == null) {
            synchronized (HttpUtil.class) {
                if (okHttpClient == null) {
                    okHttpClient = getClient(context);
                }
            }
        }
    }

    @NonNull
    private static OkHttpClient getClient(@NonNull Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .hostnameVerifier(getHostnameVerifier())
                .cookieJar(new CookieJarImpl(context));
        SSLSocketFactory socketFactory = getSSLSocketFactory(HttpCons.HTTPS_CER);
        if (socketFactory != null) {
            builder.sslSocketFactory(socketFactory, new TrustAllCerts());
        }
        return builder.build();
    }

    /**
     * Get请求
     */
    public static void get(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(callback);
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
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * Post请求发送JSON数据
     */
    public static void post(String url, String jsonParams, Callback callback) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8")
                , jsonParams);
        Request request = new Request.Builder().url(url).post(body).build();
        okHttpClient.newCall(request).enqueue(callback);
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
                    RequestBody.create(mediaType, file));
        }
        //发出请求参数
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        okHttpClient.newCall(request).enqueue(callback);
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
        final File file = new File(savePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder().url(downloadPath).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (listener != null) {
                    listener.onDownloadFailed();
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                InputStream is = null;
                byte[] buf = new byte[1024 * 1024 * 10];
                int len;
                FileOutputStream fos = null;
                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        is = body.byteStream();
                        long total = body.contentLength();
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
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
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

    // 对包含中文的字符串进行转码，此为UTF-8。服务器那边要进行一次解码
    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class CookieJarImpl implements CookieJar {

        private Context context;
        private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

        CookieJarImpl(Context context) {
            this.context = context;
        }

        @Override
        public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookieList) {
            if (!cookieList.isEmpty()) {
                Cookie cookieResult = null;
                for (Cookie cookie : cookieList) {
                    if ("cuid".equals(cookie.name())) {
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
            }
        }

        @Override
        public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
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
            return cookieList != null ? cookieList : new ArrayList<Cookie>();
        }
    }

    public static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static SSLSocketFactory getSSLSocketFactory(String httpsCer) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream certificate = new ByteArrayInputStream(httpsCer.getBytes("UTF-8"));
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            String certificateAlias = Integer.toString(0);
            keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
    }

}
