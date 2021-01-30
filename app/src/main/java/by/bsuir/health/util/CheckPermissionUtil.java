package by.bsuir.health.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pablo on 23.01.2021
 * @project Health
 */
public class CheckPermissionUtil {

    private final int mRequestCode = 1;
    public static boolean showSystemSetting = true;
    private static CheckPermissionUtil permissionsUtil;
    private IPermissionsResult mPermissionsResult;
    AlertDialog mPermissionDialog;

    private static class CheckPermissionInstance {
        private static final CheckPermissionUtil mInstance = new CheckPermissionUtil();
    }

    public static CheckPermissionUtil getInstance() {
        return CheckPermissionInstance.mInstance;
    }

    public void checkPermissions(Activity context, String[] permissions, IPermissionsResult permissionsResult) {
        mPermissionsResult = permissionsResult;
        if (Build.VERSION.SDK_INT < 23) {
            permissionsResult.passPermissions();
            return;
        }
        List<String> mPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        }
        if (mPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(context, permissions, mRequestCode);
        } else {
            permissionsResult.passPermissions();
        }
    }

    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (mRequestCode == requestCode) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
//                        if (showSystemSetting) {
                            mPermissionsResult.repeatPermissions();
//                            showSystemPermissionsSettingDialog(context);
//                        } else {
//                            mPermissionsResult.forbidPermissions();
//                        }
                    }
                    mPermissionsResult.passPermissions();
                }

            }
        }
    }

    private void showSystemPermissionsSettingDialog(final Activity context) {
        final String mPackName = context.getPackageName();
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(context)
                    .setMessage("Permissions have been disabled, please grant them manually")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            context.startActivity(intent);
                            context.finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
//                            mContext.finish();
                            mPermissionsResult.forbidPermissions();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    private void cancelPermissionDialog() {
        if (mPermissionDialog != null) {
            mPermissionDialog.cancel();
            mPermissionDialog = null;
        }
    }

    public interface IPermissionsResult {
        void passPermissions();
        void forbidPermissions();
        void repeatPermissions();
    }
}
