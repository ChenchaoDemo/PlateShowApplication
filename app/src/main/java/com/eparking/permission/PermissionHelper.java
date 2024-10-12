package com.eparking.permission;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangzhen on 2020/5/12.
 * 权限帮助类
 */

public class PermissionHelper {
    private Activity mActivity;
    private PermissionInterface mPermissionInterface;
    public static final int APP_SETTINGS_RC = 2048;

    public PermissionHelper(@NonNull Activity activity, @NonNull PermissionInterface permissionInterface) {
        mActivity = activity;
        mPermissionInterface = permissionInterface;
    }

    /**
     * 开始请求权限。
     * 方法内部已经对Android M 或以上版本进行了判断，外部使用不再需要重复判断。
     * 如果设备还不是M或以上版本，则也会回调到requestPermissionsSuccess方法。
     */
    public void requestPermissions() {
        String[] deniedPermissions = PermissionUtil.getDeniedPermissions(mActivity, mPermissionInterface.getPermissions());
        if (deniedPermissions != null && deniedPermissions.length > 0) {
            List<String> deniedShowRationalePermissions=new ArrayList<>();
            // Should we show an explanation?
            // Permission is not granted
            for (String deniedPermission : deniedPermissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, deniedPermission)) {
                    SharedPreferencesHelper.putInt(mActivity, "callbackInterfaceType", 2);
                    deniedShowRationalePermissions.add(deniedPermission);
                }
            }
            if(deniedShowRationalePermissions.size()>0){
                mPermissionInterface.onShowRationale(deniedShowRationalePermissions.toArray(new String[deniedShowRationalePermissions.size()]));
                return;
            }

            PermissionUtil.requestPermissions(mActivity, deniedPermissions, mPermissionInterface.getPermissionsRequestCode());

        } else {
            mPermissionInterface.requestPermissionsSuccess();
        }
    }

    /**
     * 在Activity中的onRequestPermissionsResult中调用
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return true 代表对该requestCode感兴趣，并已经处理掉了。false 对该requestCode不感兴趣，不处理。
     */
    public boolean requestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == mPermissionInterface.getPermissionsRequestCode()) {
            boolean isAllGranted = true;//是否全部权限已授权
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                //已全部授权
                mPermissionInterface.requestPermissionsSuccess();
            } else {
                //权限有缺失
                if (SharedPreferencesHelper.getInt(mActivity, "callbackInterfaceType", 0) == 1)
                    mPermissionInterface.requestPermissionsFail();
            }
            /**
             * callbackInterfaceType 0 1 2
             * 0代表第一次进行申请授权操作
             * 1代表不是第一次进入申请授权操作，并且直接执行请求失败接口
             * 2代表不是第一次进入申请授权操作，并且直接执行解释操作接口，不执行请求失败接口
             */
            SharedPreferencesHelper.putInt(mActivity, "callbackInterfaceType", 1);
            return true;
        }
        return false;
    }

    /**
     * 打开 APP 的权限详情设置
     * 在onActivityResult中接收requestCode=2048的权限回调结果，重新执行权限相关逻辑
     *
     * @param context
     * @param permissionShow 权限描述 推荐样式“定位-帮助您推荐上车地点”
     */
    public void openAppDetails(@NonNull final Activity context, String... permissionShow) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("需要给该应用授权");
        StringBuilder msg = new StringBuilder();
        if (permissionShow != null && permissionShow.length > 0) {
            for (int i = 0; i < permissionShow.length; i++) {
                msg.append(permissionShow[i]);
                msg.append("\n");
            }
        }
        msg.append("\n请到 “应用信息 -> 权限” 中授予！");
        builder.setMessage(msg.toString());
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivityForResult(intent, APP_SETTINGS_RC);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
