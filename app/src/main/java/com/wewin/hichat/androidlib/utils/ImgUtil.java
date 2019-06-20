package com.wewin.hichat.androidlib.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.wewin.hichat.BuildConfig;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.permission.Permission;
import com.wewin.hichat.androidlib.permission.PermissionCallback;
import com.wewin.hichat.androidlib.permission.Rigger;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;

/**
 * Created by Darren on 2019/1/3.
 */
public class ImgUtil {

    public static String cameraOutputPath;
    public static String cropOutputPath;

    public static final int IMG_CROP_SIZE_1 = 1;
    public static final int IMG_CROP_SIZE_2 = 2;
    public static final int IMG_CROP_SIZE_3 = 3;
    public static final int REQUEST_CAMERA = 4;
    public static final int REQUEST_CROP = 5;
    public static final int REQUEST_ALBUM = 6;
    //图片选择
    public static final String ALBUM_OPEN_KEY = "ALBUM_OPEN_KEY";
    public static final String ALBUM_SELECT_MAX_NUM = "ALBUM_SELECT_MAX_NUM";
    public static final String IMG_SELECT_PATH_LIST = "IMG_SELECT_PATH_LIST";
    //图片展示
    public static final String IMG_LIST_KEY = "IMG_LIST_KEY";
    public static final String IMG_CLICK_POSITION = "IMG_CLICK_POSITION";
    public static final String IMG_DONWLOAD = "IMG_DONWLOAD";
    public static final String IMG_BEAN_LIST_KEY = "IMG_BEAN_LIST_KEY";

    private static RequestOptions circleOptions = new RequestOptions()
            .placeholder(R.drawable.img_avatar_default)
            .error(R.drawable.img_avatar_default)
            .circleCrop()
            .fallback(R.drawable.img_avatar_default);

    public static void load(Activity activity, String imgUrl, ImageView imageView) {
        load(activity, imgUrl, imageView, R.drawable.img_avatar_default);
    }

    public static void load(Activity activity, int resourceId, ImageView imageView) {
        load(activity, resourceId, imageView, R.drawable.img_avatar_default);
    }

    public static void load(Fragment fragment, String imgUrl, ImageView imageView) {
        load(fragment, imgUrl, imageView, R.drawable.img_avatar_default);
    }

    public static void load(Fragment fragment, int resourceId, ImageView imageView) {
        load(fragment, resourceId, imageView, R.drawable.img_avatar_default);
    }

    public static void load(Context context, String imgUrl, ImageView imageView) {
        load(context, imgUrl, imageView, R.drawable.img_avatar_default);
    }

    public static void load(Context context, int resourceId, ImageView imageView) {
        load(context, resourceId, imageView, R.drawable.img_avatar_default);
    }

    public static void load(Activity activity, String imgUrl, ImageView imageView, int defaultId) {
        Glide.with(activity).load(imgUrl).apply(getRequestOptions(defaultId)).into(imageView);
    }

    public static void load(Activity activity, int resourceId, ImageView imageView, int defaultId) {
        Glide.with(activity).load(resourceId).apply(getRequestOptions(defaultId)).into(imageView);
    }

    public static void load(Fragment fragment, String imgUrl, ImageView imageView, int defaultId) {
        Glide.with(fragment).load(imgUrl).apply(getRequestOptions(defaultId)).into(imageView);
    }

    public static void load(Fragment fragment, int resourceId, ImageView imageView, int defaultId) {
        Glide.with(fragment).load(resourceId).apply(getRequestOptions(defaultId)).into(imageView);
    }

    public static void load(Context context, String imgUrl, ImageView imageView, int defaultId) {
        Glide.with(context).load(imgUrl).apply(getRequestOptions(defaultId)).into(imageView);
    }

    public static void load(Context context, Integer resourceId, ImageView imageView, int defaultId) {
        Glide.with(context).load(resourceId).apply(getRequestOptions(defaultId)).into(imageView);
    }

    //圆形
    public static void loadCircle(Context context, String imgUrl, ImageView imageView) {
        Glide.with(context).load(imgUrl)
                .apply(circleOptions)
                .into(imageView);
    }

    private static RequestOptions getRequestOptions(int defaultId) {
        return new RequestOptions()
                .placeholder(defaultId)
                .error(defaultId)
                .centerCrop()
                .fallback(defaultId);
    }

    public static void compress(Context context, File originFile, LubanCallBack lubanCallBack) {
        try {
            Luban.with(context)
                    .load(originFile)
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                        }
                    })
                    .setCompressListener(lubanCallBack)
                    .launch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void compress(Context context, String filePath, LubanCallBack lubanCallBack) {
        try {
            Luban.with(context)
                    .load(filePath)
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                        }
                    })
                    .setCompressListener(lubanCallBack)
                    .launch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void compress(Context context, List<String> pathList, LubanCallBack lubanCallBack) {
        try {
            Luban.with(context)
                    .load(pathList)
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                        }
                    })
                    .setCompressListener(lubanCallBack)
                    .launch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openCamera(final Activity activity) {
        Rigger.on(activity).permissions(Permission.CAMERA)
                .start(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        takePhoto(activity);
                    }

                    @Override
                    public void onDenied(HashMap<String, Boolean> permissions) {

                    }
                });
    }

    /**
     * 拍照
     */
    private static void takePhoto(Activity activity) {
        try {
            FileUtil.createDir(FileUtil.getSDCachePath(activity));
            cameraOutputPath = FileUtil.getSDCachePath(activity)
                    + TimeUtil.getCurrentTime("yyyyMMdd_HHmmss") + ".jpg";
            File file = new File(cameraOutputPath);
            Uri imgUri;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                imgUri = Uri.fromFile(file);
            } else {
                imgUri = FileProvider.getUriForFile(activity,
                        BuildConfig.APPLICATION_ID + ".provider", file);
            }
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            activity.startActivityForResult(intent, REQUEST_CAMERA);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 剪裁图片
     */
    public static void cropPic(Activity activity, String inputPath, int type) {
        try {
            Uri inputUri;
            Uri outputUri;
            FileUtil.createDir(FileUtil.getSDCachePath(activity));
            cropOutputPath = FileUtil.getSDCachePath(activity)
                    + TimeUtil.getCurrentTime("yyyyMMdd_HHmmss") + ".jpg";
            FileUtil.createFile(cropOutputPath);
            Intent intent = new Intent("com.android.camera.action.CROP");

            if (Build.VERSION.SDK_INT >= 24) {
                inputUri = FileProvider.getUriForFile(activity,
                        BuildConfig.APPLICATION_ID + ".provider", new File(inputPath));
                outputUri = FileProvider.getUriForFile(activity,
                        BuildConfig.APPLICATION_ID + ".provider", new File(cropOutputPath));
                //开启临时权限
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //重点:针对7.0以上的操作
                intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, outputUri));
            } else {
                inputUri = Uri.fromFile(new File(inputPath));
                outputUri = Uri.fromFile(new File(cropOutputPath));
            }
            intent.setDataAndType(inputUri, "image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            intent.putExtra("return-data", false);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("crop", "true");
            // 裁剪框的比例（根据需要显示的图片比例进行设置）
            if (type == IMG_CROP_SIZE_1) {
                intent.putExtra("aspectX", 2);
                intent.putExtra("aspectY", 2);
                // 裁剪后图片的宽高（注意和上面的裁剪比例保持一致)
                intent.putExtra("outputX", 600);
                intent.putExtra("outputY", 600);
            } else if (type == IMG_CROP_SIZE_2) {
                intent.putExtra("aspectX", 26);
                intent.putExtra("aspectY", 15);
                intent.putExtra("outputX", 1040);
                intent.putExtra("outputY", 600);
            } else {
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 500);
                intent.putExtra("outputY", 500);
            }
            activity.startActivityForResult(intent, REQUEST_CROP);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
