package com.wewin.hichat.androidlib.permission;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.wewin.hichat.androidlib.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者：created by jason on 2019/4/5 13
 * 注意部分方法搭配使用才有效
 */
public class RiggerPresenter {
    private PermissionCallback callback;

    private boolean isAdded = false;
    private RiggerFragment riggerFragment;
    private SupportRiggerFragment supportRiggerFragment;
    private String[] permissions;
    private String[] permissionsName;
    private boolean isShow = false;
    private Activity activity;
    //记录权限申请后的结果，成功一个移除一个
    private HashMap<String, Boolean> result = new HashMap<>();

    public RiggerPresenter(RiggerFragment riggerFragment) {
        initData();
        this.riggerFragment = riggerFragment;
    }

    public RiggerPresenter(SupportRiggerFragment supportRiggerFragment) {
        initData();
        this.supportRiggerFragment = supportRiggerFragment;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (Permission.permissionMap.size() <= 0) {
            Permission.permissionMap.clear();
            for (int i=0;i<Permission.permissions.length;i++) {
                Permission.permissionMap.put(Permission.permissions[i],Permission.permissionsName[i]);
            }
        }
    }

    /**
     * 设置回调
     *
     * @param callback
     */
    public void setPermissionCallback(PermissionCallback callback) {
        this.callback = callback;
    }

    /**
     * 获取回调接口
     *
     * @return
     */
    public PermissionCallback getCallBack() {
        return callback;
    }

    /**
     * 设置
     *
     * @param activity
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * 设置要申请的权限
     *
     * @param permissions
     */
    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
        String[] permissionsName=new String[permissions.length];

        for(int i=0;i<permissions.length;i++){
            permissionsName[i]= (String) Permission.permissionMap.get(permissions[i]);
        }
        setPermissionsName(permissionsName);
        result.clear();
    }

    /**
     * 设置要申请的名字
     * 必须和权限一致，且isShow为true才有效
     *
     * @param permissionsName
     */
    public void setPermissionsName(String[] permissionsName) {
        this.permissionsName = permissionsName;
    }

    /**
     * 是否显示弹窗
     * 必须和setPermissionsName搭配使用，
     *
     * @param isShow
     */
    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }

    /**
     * 设置结果
     *
     * @return
     */
    public HashMap<String, Boolean> getResult() {
        return result;
    }


    /**
     * 开始
     */
    public void start() {
        if (!getIsAdded()) return;
        if (permissions == null) return;
        if (isShow) {
            if (supportRiggerFragment != null) {
                AuthorizationCheck.authorizationPermission(this, activity, supportRiggerFragment, permissions, permissionsName);
            } else if (riggerFragment != null) {
                AuthorizationCheck.authorizationPermission(this, activity, riggerFragment, permissions, permissionsName);
            }

        } else {
            if (supportRiggerFragment != null) {
                AuthorizationCheck.authorizationPermission(this, activity, supportRiggerFragment, permissions);
            } else if (riggerFragment != null) {
                AuthorizationCheck.authorizationPermission(this, activity, riggerFragment, permissions);
            }
        }
    }


    /**
     * 发送回调 ,判断当前权限申请完毕，完毕再发送回调
     */
    public void sendCallBack() {
        if (callback == null) return;
        if (result.size() == permissions.length) {
            boolean isSuccess = true;
            for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                if (entry.getValue() == false) {
                    isSuccess = false;
                } else {
//                    hashMap;
                }
            }
            if (isSuccess) {
                callback.onGranted();
            } else {
                callback.onDenied(result);
            }
        }
    }

    /**
     * 判断是否add
     *
     * @return
     */
    public boolean getIsAdded() {
        return isAdded;
    }

    public void onAdded() {
        isAdded = true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                getResult().put(permissions[i], true);
                sendCallBack();

            } else {
                getResult().put(permissions[i], false);
                sendCallBack();
            }
        }

    }

    /**
     * 这里可以处理onActivityResult回调，并写入回调方法中，暂未开发
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            LogUtil.i("Rigger activity result successful.");
//            deliverResult(requestCode,data);
        } else if (requestCode == Activity.RESULT_CANCELED) {
            LogUtil.i("Rigger activity result canceled.");
//            deliverCanceled(requestCode);
        }
    }

    public void onDestroy() {
        isAdded = false;
    }
}
