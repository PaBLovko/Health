package by.bsuir.health;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import by.bsuir.health.main.MainActivity;

/**
 * @author Pablo on 23.01.2021
 * @project Health
 */
public class Permission {

    private static final int REQUEST_CODE = 1;

    int accessCoarseLocation;
    int accessFineLocation;
    int readExternalStorage;
    int writeExternalStorage;
    private IPermissionsResult mPermissionsResult;

    public Permission(int accessCoarseLocation, int accessFineLocation,
                      int readExternalStorage, int writeExternalStorage) {
        this.accessCoarseLocation = accessCoarseLocation;
        this.accessFineLocation = accessFineLocation;
        this.readExternalStorage = readExternalStorage;
        this.writeExternalStorage = writeExternalStorage;

    }

    public String[] LocationPermission() {
        List<String> listRequestPermission = new ArrayList<>();
        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED)
            listRequestPermission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        if (accessFineLocation != PackageManager.PERMISSION_GRANTED)
            listRequestPermission.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (!listRequestPermission.isEmpty()) {
            String[] strRequestPermission = listRequestPermission.toArray(new String[0]);
            return strRequestPermission;
        }
        return null;
    }

    public String[] ExternalPermission() {
        List<String> listRequestPermission = new ArrayList<>();
        if (readExternalStorage != PackageManager.PERMISSION_GRANTED)
            listRequestPermission.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (writeExternalStorage != PackageManager.PERMISSION_GRANTED)
            listRequestPermission.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!listRequestPermission.isEmpty()) {
            String[] strRequestPermission = listRequestPermission.toArray(new String[0]);
            return strRequestPermission;
        }
        return null;
    }

    public void access(Activity activity, String[] strRequestPermission){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(strRequestPermission, REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(Activity context, int requestCode, String[] permissions, int[] grantResults) {
        boolean hasPermissionDismiss = false;
        if (REQUEST_CODE == requestCode) {
            for (int grantResult : grantResults) {
                if (grantResult == -1) {
                    hasPermissionDismiss = true;
                    break;
                }
            }
            if (hasPermissionDismiss) {
//                if (showSystemSetting) {
                       mPermissionsResult.restPermissons();
//                    showSystemPermissionsSettingDialog(context);
//                } else {
//                    mPermissionsResult.forbitPermissons();
//                }
            } else {
                mPermissionsResult.passPermissons();
            }
        }

        //        if (requestCode == REQUEST_CODE) {
//            if (grantResults.length > 0) {
//                for (int gr : grantResults) {
//                    // Check if request is granted or not
//                    if (gr != PackageManager.PERMISSION_GRANTED) {
//                                               mPermissionsResult.restPermissons();//return;
//                    }
//                }
//                                mPermissionsResult.passPermissons();//TODO - Add your code here to start Discovery
//            }
//        }
    }

    public interface IPermissionsResult {
        void passPermissons();
        void forbitPermissons();
        void restPermissons();
    }

}

