//package by.bsuir.health;
//
//import android.Manifest;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.CompoundButton;
//import android.widget.Toast;
//
//import by.bsuir.health.main.MainActivity;
//import by.bsuir.health.util.CheckPermissionUtil;
//
///**
// * @author Pablo on 31.01.2021
// * @project Health
// */
//public class ControllerActivity extends AppCompatActivity implements
//        CompoundButton.OnCheckedChangeListener,
//        AdapterView.OnItemClickListener,
//        View.OnClickListener{
//
//    private ViewActivity viewActivity;
//    private CheckPermissionUtil checkPermissionUtil;
//    private String[] permissions;
//    private AppCompatActivity appCompatActivity;
//
//    public ControllerActivity(AppCompatActivity appCompatActivity) {
//        this.appCompatActivity = appCompatActivity;
//        viewActivity = new ViewActivity(appCompatActivity);
//        permissions = new String[]{
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//        };
//        checkPermissionUtil = CheckPermissionUtil.getInstance();
//        checkPermissionUtil.checkPermissions(appCompatActivity, permissions, permissionsResult);
//
//    }
//
//    @Override
//    public void onClick(View v) {
//
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//    }
//
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//    }
//
//    CheckPermissionUtil.IPermissionsResult permissionsResult =
//            new CheckPermissionUtil.IPermissionsResult() {
//                @Override
//                public void passPermissions() {
//                    Toast.makeText(ControllerActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void forbidPermissions() {
//                    //TODO finish
//                    //System.out.println("finish");
//                }
//
//                @Override
//                public void repeatPermissions() {
//                    checkPermissionUtil.checkPermissions(
//                            ControllerActivity.this, permissions, permissionsResult);
//                }
//            };
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        checkPermissionUtil.onRequestPermissionsResult(requestCode, grantResults);
//    }
//
//}
