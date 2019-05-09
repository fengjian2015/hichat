package com.wewin.hichat.androidlib.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;

import com.wewin.hichat.androidlib.utils.ActivityUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.dialog.PromptDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/3/20.
 * 权限校验 开启应用
 */

public class AuthorizationCheck {

    public static String ACCESS_NETWORK_STATE = "应用程序获取网络状态信息";
    public static String ACCESS_WIFI_STATE = "应用程序获取WI-FI网络状态信息";
    public static String BATTERY_STATES = "应用程序获取电池状态信息";
    public static String BLUETOOTH = "应用程序链接匹配的蓝牙设备";
    public static String BLUETOOTH_ADMIN = "应用程序发现匹配的蓝牙设备";
    public static String BROADCAST_SMS = "应用程序广播收到短信提醒";
    public static String CALL_PHONE = "应用程序拨打电话";
    public static String CAMERA = "应用程序使用照相机";
    public static String CHANGE_NETWORK_STATE = "应用程序改变网络连接状态";
    public static String CHANGE_WIFI_STATE = "应用程序改变WIFI网络连接状态";
    public static String DELETE_CACHE_FILES = "应用程序删除缓存文件";
    public static String DELETE_PACKAGES = "应用程序删除安装包";
    public static String FLASHLIGHT = "应用程序访问闪光灯";
    public static String INTERNET = "应用程序打开网络Socket";
    public static String MODIFY_AUDIO_SETTINGS = "应用程序修改全局声音设置";
    public static String PROCESS_OUTGOING_CALLS = "应用程序监听、控制、取消呼出电话";
    public static String READ_CONTACTS = "应用程序读取联系人信息";
    public static String READ_HISTORY_BOOKMARKS = "应用程序读取历史书签";
    public static String READ_OWNER_DATA = "应用程序读取用户数据";
    public static String READ_PHONE_STATE = "应用读取手机状态";
    public static String READ_PHONE_SMS = "应用程序读取短信";
    public static String REBOOT = "应用程序重启系统";
    public static String RECEIVE_MMS = "应用程序接受、监控、处理彩信";
    public static String RECEIVE_SMS = "应用程序接受、监控、处理短信";
    public static String RECORD_AUDIO = "应用程序录音";
    public static String SEND_SMS = "应用程序发送短信";
    public static String SET_ORIENTATION = "应用程序旋转屏幕";
    public static String SET_TIME = "应用程序设置时间";
    public static String SET_TIME_ZONE = "应用程序设置时区";
    public static String SET_WALLPAPER = "应用程序设置桌面壁纸";
    public static String VIBRATE = "应用程序控制震动器";
    public static String WRITE_CONTACTS = "应用程序写入用户联系人";
    public static String WRITE_HISTORY_BOOKMARKS = "应用程序写入历史书签";
    public static String WRITE_OWNER_DATA = "应用程序写入用户数据";
    public static String WRITE_SMS = "应用程序写短信";
    public static String READ_EXTERNAL_STORAGE = "应用存储空间";
    public static String WRITE_EXTERNAL_STORAGE = "应用存储空间";
    public static String SYSTEM_ALERT_WINDOW = "应用悬浮窗";
    public static String REQUEST_INSTALL_PACKAGES="允许安装未知来源应用";

    public static int REQUEST_CODE = 111;


    /**
     * 权限检查与申请，不强制提醒，无需回调
     * @param activity
     * @param checkAuthorization
     * @return
     */
    public static boolean authorizationPermission(Activity activity, String... checkAuthorization) {
        if (!authorizationCheck(checkAuthorization, activity)) {
            //2.没有权限，弹出对话框申请
            ActivityCompat.requestPermissions(activity, checkAuthorization, REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 权限检查与申请，不强制提醒 ,配合RiggerPresenter回调
     * @param activity
     * @param checkAuthorization
     * @return
     */
    public static boolean authorizationPermission(RiggerPresenter presenter,Activity activity,Fragment fragment, String[] checkAuthorization) {
        boolean isAuthorization = true;
        for (int i=0;i< checkAuthorization.length;i++) {
            if (!authorizationCheck(checkAuthorization, activity)){
                isAuthorization=false;
            }else {
                presenter.getResult().put(checkAuthorization[i],true);
                presenter.sendCallBack();
            }
        }
        if(isAuthorization==false){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment.requestPermissions( checkAuthorization, REQUEST_CODE);
            }
        }
        return isAuthorization;
    }


    /**
     * 权限检查与申请，不强制提醒 ,配合RiggerPresenter回调
     * @param activity
     * @param checkAuthorization
     * @return
     */
    public static boolean authorizationPermission(RiggerPresenter presenter,Activity activity, android.app.Fragment fragment, String[] checkAuthorization) {
        boolean isAuthorization = true;
        for (int i=0;i< checkAuthorization.length;i++) {
            if (!authorizationCheck(checkAuthorization, activity)){
                isAuthorization=false;
            }else {
                presenter.getResult().put(checkAuthorization[i],true);
                presenter.sendCallBack();
            }
        }
        if(isAuthorization==false){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment.requestPermissions( checkAuthorization, REQUEST_CODE);
            }
        }
        return isAuthorization;
    }




    /**
     * .
     * 单个权限申请，没有拒绝过则弹出授权，拒绝过跳转提示，无需回调
     *
     * @param activity
     * @param checkAuthorization  权限
     * @param authorizationPromot 权限名字
     * @return true 有权限
     */
    public static boolean authorizationPermission(Activity activity, String checkAuthorization, String authorizationPromot) {
        boolean isAuthorization = true;
            //1.检测权限
            if (!authorizationCheck(checkAuthorization, activity)) {
                //获取上次被拒权限列表
                final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, new String[]{checkAuthorization}, true);
                boolean isRefuse = false;
                for (String au : shouldRationalePermissionsList) {
                    if (au.equals(checkAuthorization)) {
                        isRefuse = true;
                    }
                }
                if (isRefuse) {
                    authorizationPromot(activity, authorizationPromot, true, true);
                } else {
                    //2.没有权限，弹出对话框申请
                    ActivityCompat.requestPermissions(activity, new String[]{checkAuthorization}, REQUEST_CODE);
                }
                isAuthorization = false;
            }
        return isAuthorization;
    }



    /**
     * 没有拒绝过则弹出授权，拒绝过跳转提示，无需回调
     *  多个权限申请
     * @param activity
     * @param checkAuthorization  权限
     * @param authorizationPromot 权限名字
     * @return true 有权限
     */
    public static boolean authorizationPermission(Activity activity, String[] checkAuthorization, String[] authorizationPromot) {
        boolean isAuthorization = true;
        for (int i=0;i< checkAuthorization.length;i++) {
            //1.检测权限
            if (!authorizationCheck(checkAuthorization[i], activity)) {
                //获取上次被拒权限列表
                final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, new String[]{checkAuthorization[i]}, true);
                boolean isRefuse = false;
                for (String au : shouldRationalePermissionsList) {
                    if (au.equals(checkAuthorization[i])) {
                        isRefuse = true;
                    }
                }
                if (isRefuse) {
                    authorizationPromot(activity, authorizationPromot[i], true, true);
                } else {
                    //2.没有权限，弹出对话框申请
                    ActivityCompat.requestPermissions(activity, checkAuthorization, REQUEST_CODE);
                }
                isAuthorization = false;
            }
        }
        return isAuthorization;
    }

    /**
     * 没有拒绝过则弹出授权，拒绝过跳转提示,配合RiggerPresenter回调
     *
     * @param activity
     * @param checkAuthorization  权限
     * @param authorizationPromot 权限名字
     * @return true 有权限
     */
    public static boolean authorizationPermission(RiggerPresenter presenter,Activity activity,Fragment fragment, String[] checkAuthorization, String[] authorizationPromot) {
        boolean isAuthorization = true;
        for (int i=0;i< checkAuthorization.length;i++) {
            //1.检测权限
            if (!authorizationCheck(checkAuthorization[i], activity)) {
                //获取上次被拒权限列表
                final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, new String[]{checkAuthorization[i]}, true);
                boolean isRefuse = false;
                for (String au : shouldRationalePermissionsList) {
                    if (au.equals(checkAuthorization[i])) {
                        isRefuse = true;
                    }
                }
                if (isRefuse) {
                    authorizationPromot(activity, authorizationPromot[i], true, true);
                    presenter.getResult().put(checkAuthorization[i],false);
                    presenter.sendCallBack();
                } else {
                    //2.没有权限，弹出对话框申请
                    fragment.requestPermissions( checkAuthorization, REQUEST_CODE);
                }
                isAuthorization = false;
            }else {
                presenter.getResult().put(checkAuthorization[i],true);
                presenter.sendCallBack();
            }
        }
        return isAuthorization;
    }

    /**
     * 没有拒绝过则弹出授权，拒绝过跳转提示,配合RiggerPresenter回调
     *
     * @param activity
     * @param checkAuthorization  权限
     * @param authorizationPromot 权限名字
     * @return true 有权限
     */
    public static boolean authorizationPermission(RiggerPresenter presenter,Activity activity, android.app.Fragment fragment, String[] checkAuthorization, String[] authorizationPromot) {
        boolean isAuthorization = true;
        for (int i=0;i< checkAuthorization.length;i++) {
            //1.检测权限
            if (!authorizationCheck(checkAuthorization[i], activity)) {
                //获取上次被拒权限列表
                final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, new String[]{checkAuthorization[i]}, true);
                boolean isRefuse = false;
                for (String au : shouldRationalePermissionsList) {
                    if (au.equals(checkAuthorization[i])) {
                        isRefuse = true;
                    }
                }
                if (isRefuse) {
                    authorizationPromot(activity, authorizationPromot[i], true, true);
                    presenter.getResult().put(checkAuthorization[i],false);
                    presenter.sendCallBack();
                } else {
                    //2.没有权限，弹出对话框申请
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        fragment.requestPermissions( checkAuthorization, REQUEST_CODE);
                    }
                }
                isAuthorization = false;
            }else {
                presenter.getResult().put(checkAuthorization[i],true);
                presenter.sendCallBack();
            }
        }
        return isAuthorization;
    }


    /**
     * 权限校验  只校验系统版本为6.0及以上时，默认6.0以下的校验权限为true
     *
     * @param checkAuthorization 被校验 Manifest.permission.READ_CONTACTS
     * @param activity           当前Activty
     * @return true 为有权限   false 没有权限
     */

    private static boolean authorizationCheck(String[] checkAuthorization, Activity activity) {
        int targetSdkVersion = 0;
        try {
            final PackageInfo info = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        for (String authorization : checkAuthorization) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    if (activity.checkSelfPermission(authorization) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    if (PermissionChecker.checkSelfPermission(activity, authorization) != PermissionChecker.PERMISSION_GRANTED) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
        return true;
    }


    /**
     * ,配合RiggerPresenter回调
     * 权限校验  只校验系统版本为6.0及以上时，默认6.0以下的校验权限为true
     *
     * @param checkAuthorization 被校验 Manifest.permission.READ_CONTACTS
     * @param activity           当前Activty
     * @return true 为有权限   false 没有权限
     */

    private static boolean authorizationCheck(RiggerPresenter presenter,String[] checkAuthorization, Activity activity) {
        int targetSdkVersion = 0;
        try {
            final PackageInfo info = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        for (String authorization : checkAuthorization) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    if (activity.checkSelfPermission(authorization) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    } else {
                        presenter.getResult().put(authorization,true);
                        presenter.sendCallBack();
                        return true;
                    }
                } else {
                    if (PermissionChecker.checkSelfPermission(activity, authorization) != PermissionChecker.PERMISSION_GRANTED) {
                        return false;
                    } else {
                        presenter.getResult().put(authorization,true);
                        presenter.sendCallBack();
                        return true;
                    }
                }
            }
        }
        return true;
    }


    /**
     * 权限校验  只校验系统版本为6.0及以上时，默认6.0以下的校验权限为true
     * 单个权限
     *
     * @param checkAuthorization 被校验 Manifest.permission.READ_CONTACTS
     * @param activity           当前Activty
     * @return true 为有权限   false 没有权限
     */
    private static boolean authorizationCheck(String checkAuthorization, Activity activity) {
        int targetSdkVersion = 0;
        try {
            final PackageInfo info = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                if (activity.checkSelfPermission(checkAuthorization) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {
                    return true;
                }
            } else {
                if (PermissionChecker.checkSelfPermission(activity, checkAuthorization) != PermissionChecker.PERMISSION_GRANTED) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * 权限提示
     *
     * @param activity            当前Activity
     * @param authorizationPrompt 权限名称
     * @param withDialog          是否为Dialog提示，true为Dialog ，false 为 Toast
     * @param isMustOpen          当前权限是否必须开启
     */
    public static void authorizationPromot(final Activity activity, String authorizationPrompt,
                                           boolean withDialog, final boolean isMustOpen) {
//        String promotWithMustOpen = activity.getString(R.string.not_obtainable) + authorizationPromot + activity.getString(R.string.open_access_path);
        String promptWithRead = "由于无法获得" + authorizationPrompt + "权限，请到当前设备管理应用或设置中开启\\n设置路径：设置->应用->权限";
        if (!withDialog) {
            if (isMustOpen) {
                ToastUtil.showShort(activity, promptWithRead);
                openApplication(activity);
            } else {
                ToastUtil.showShort(activity, promptWithRead);
            }
            return;
        }
        if (isMustOpen) {
            promptDialog(activity, promptWithRead, isMustOpen);
        } else {
            promptDialog(activity, promptWithRead, isMustOpen);
        }
    }

    private static void promptDialog(final Activity activity, String promptContent, final boolean isMustOpen) {
        if (activity.isFinishing()) {
            return;
        }
        PromptDialog.PromptBuilder builder = new PromptDialog.PromptBuilder(activity);
        if (isMustOpen) {
            if (!ActivityUtil.isActivityOnTop(activity)) return;
            builder.setPromptContent(promptContent)
                    .setCancelVisible(true)
                    .setOnConfirmClickListener(new PromptDialog.PromptBuilder.OnConfirmClickListener() {
                        @Override
                        public void confirmClick() {
                            if (isMustOpen) {
                                openApplication(activity);
                            }
                        }
                    }).create();

        } else {
            builder.setCancelVisible(false)
                    .setPromptContent(promptContent)
                    .create();
        }

    }

    private static void openApplication(Activity activity) {
        Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        String pkg = "com.android.settings";
        String cls = "com.android.settings.applications.InstalledAppDetails";
        i.setComponent(new ComponentName(pkg, cls));
        i.setData(Uri.parse("package:" + activity.getPackageName()));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
    }

    /**
     * 获取某些权限列表
     *
     * @param object
     * @param requestPermissions
     * @param isShouldRationale  true 被拒权限列表
     * @return
     */
    private static ArrayList<String> getNoGrantedPermission(Object object, String[] requestPermissions, boolean isShouldRationale) {
        ArrayList<String> permissions = new ArrayList<>();
        for (int i = 0; i < requestPermissions.length; i++) {
            String requestPermission = requestPermissions[i];
            int checkSelfPermission = PermissionChecker.checkSelfPermission(getActivityOrFragmentContext(object), requestPermission);

            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                boolean flag = shouldShowRequestPermissionRationale(object, requestPermission);
                if (flag) {
                    if (isShouldRationale) {
                        permissions.add(requestPermission);
                    }

                } else {
                    if (!isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                }
            }
        }
        return permissions;
    }

    @TargetApi(23)
    private static Context getActivityOrFragmentContext(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getContext();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getContext();
        }
        return null;
    }

    @TargetApi(23)
    private static boolean shouldShowRequestPermissionRationale(Object object, String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }
}
