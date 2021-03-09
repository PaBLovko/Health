package by.bsuir.health;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import by.bsuir.health.bean.BluetoothConnector;
import by.bsuir.health.bean.ListAdapter;
import by.bsuir.health.bean.Pulse;
import by.bsuir.health.bean.SdFile;
import by.bsuir.health.controller.BluetoothConnectorController;
import by.bsuir.health.controller.ThreadController;
import by.bsuir.health.controller.ViewController;
import by.bsuir.health.dao.preference.OnDelayChangedListener;
import by.bsuir.health.dao.preference.PrefActivity;
import by.bsuir.health.dao.preference.PrefModel;
import by.bsuir.health.dao.storage.Storage;
import by.bsuir.health.exeption.bluetooth.BluetoothException;
import by.bsuir.health.service.CheckedChangeService;
import by.bsuir.health.service.ClickService;
import by.bsuir.health.service.ItemClickService;
import by.bsuir.health.service.OnItemChangedListener;
import by.bsuir.health.service.ReceiverService;
import by.bsuir.health.ui.ViewActivity;
import by.bsuir.health.util.CheckPermissionUtil;

import static by.bsuir.health.dao.storage.Storage.DIR_SD;
import static by.bsuir.health.ui.ViewActivity.BT_BOUNDED;
import static by.bsuir.health.ui.ViewActivity.REQ_ENABLE_BT;

/**
 * @author Pablo on 07.11.2020
 * @project Health
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String storageDirectory;
    private ArrayList<SdFile> sdFiles;
    private ListAdapter listAdapter;
    private Pulse pulse;
    private PrefModel preference;
    private CheckPermissionUtil checkPermissionUtil;
    private String[] permissions;

    private BluetoothConnector bluetoothConnector;
    private BluetoothConnector.ConnectedThread connectedThread;

    private ViewActivity viewActivity;

    private CheckedChangeService checkedChangeService;
    private ItemClickService itemClickService;
    private ClickService clickService;
    private ReceiverService receiverService;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference = new PrefModel(this);
        viewActivity = new ViewActivity(this);
        viewActivity.setGvGraph(preference.getPointsCount());
        viewActivity.setProgressDialog(this);

        sdFiles = new ArrayList<>();

        bluetoothConnector = new BluetoothConnector();

        if (BluetoothConnector.getBluetoothAdapter() == null) {
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCreate: " + getString(R.string.bluetooth_not_supported));
            finish();
        }

        try {
            if (BluetoothConnector.isEnabled()) {
                viewActivity.showFrameControls();
                viewActivity.setSwitchEnableBtChecked(true);
                listAdapter = new BluetoothConnectorController().getListAdapter(
                        this, bluetoothConnector, BT_BOUNDED);
                viewActivity.setListDevices(listAdapter);
            }
        } catch (BluetoothException e) {
            e.printStackTrace();
        }

        PrefModel.addDelayListener(new OnDelayChangedListener() {
            @Override
            public void OnDelayChanged() throws BluetoothException, IOException {
                if (connectedThread.isConnected())
                    connectedThread.write((preference.getDelayTimer() + "Delay#").getBytes());
            }
        });

        addPermissions();
        checkPermissionUtil = CheckPermissionUtil.getInstance();
        checkPermissionUtil.checkPermissions(this, permissions, permissionsResult);

        if (Storage.cardAvailable()) storageDirectory =
                Environment.getExternalStorageDirectory().toString() + DIR_SD;

//        imageview = (ImageView) findViewById();

        checkedChangeService = new CheckedChangeService(viewActivity, this,
                bluetoothConnector, connectedThread);
        itemClickService = new ItemClickService(viewActivity,sdFiles, pulse, preference,
                this, bluetoothConnector);
        clickService = new ClickService(viewActivity,pulse,this, sdFiles, storageDirectory);
        receiverService = new ReceiverService(viewActivity, bluetoothConnector, preference, listAdapter,
                this, pulse);
        new BluetoothConnectorController().addReceiver(this, receiverService);

        ItemClickService.addItemListener(new OnItemChangedListener() {
            @Override
            public void OnItemChanged() {
                if (pulse == null) {
                    pulse = itemClickService.getPulse();
                    updateDataFoConnect();
                    receiverService.setPulse(pulse);
                }
            }
        });

        ReceiverService.addItemListener(new OnItemChangedListener() {
            @Override
            public void OnItemChanged() {
                if (pulse == null) {
                    pulse = receiverService.getPulse();
                    updateDataFoConnect();
                    itemClickService.setPulse(pulse);
                }
            }
        });

        ClickService.addItemListener(new OnItemChangedListener() {
            @Override
            public void OnItemChanged() {
                sdFiles = clickService.getSdFiles();
                itemClickService.setSdFiles(sdFiles);
            }
        });

        new ViewController().setListeners(viewActivity, checkedChangeService, itemClickService, clickService);
    }

    private void updateDataFoConnect(){
        bluetoothConnector = pulse.getBluetoothConnector();
        connectedThread = pulse.getConnectedThread();
        checkedChangeService.setConnectedThread(connectedThread);
        checkedChangeService.setBluetoothConnector(bluetoothConnector);
        clickService.setPulse(pulse);
    }

    private void addPermissions() {
        permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
    }

    CheckPermissionUtil.IPermissionsResult permissionsResult =
            new CheckPermissionUtil.IPermissionsResult() {
                @Override
                public void passPermissions() {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void forbidPermissions() {
                    //TODO finish
                }
                @Override
                public void repeatPermissions() {
                    checkPermissionUtil.checkPermissions(
                            MainActivity.this, permissions, permissionsResult);
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_preference) {
            Intent intent = new Intent(this, PrefActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(pulse != null) pulse.cancelTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pulse != null)
            if (pulse.getBluetoothConnector().isConnected())
                pulse.startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiverService);
        try {
            new ThreadController().disconnection(pulse);
        } catch (BluetoothException | IOException e) {
            e.printStackTrace();
        }
    }

    private void showImage(Bitmap bitmap){
        //TODO showImageFullScreen
//        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//
//        int w = displayMetrics.widthPixels;
//        int h = displayMetrics.heightPixels;
//
//        try{
//            if (bitmap != null){
//                //toast
//                wallpaperManager.setBitmap(bitmap);
//                wallpaperManager.suggestDesiredDimensions(w, h);
//            }else { Log.i("clipcodes", "Bitmap Null");}
//        }catch (Exception e){
//            Log.i("clipcodes", "Can't ser wallpaper");
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQ_ENABLE_BT) {
            try {
                if (resultCode == RESULT_OK && BluetoothConnector.isEnabled()) {
                    viewActivity.showFrameControls();
                    listAdapter = new BluetoothConnectorController().getListAdapter(
                            this, bluetoothConnector, BT_BOUNDED);
                    viewActivity.setListDevices(listAdapter);
                } else if (resultCode == RESULT_CANCELED)
                    new BluetoothConnectorController().enableBt(this, true);
            } catch (BluetoothException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new ViewController().openQuitDialog(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        checkPermissionUtil.onRequestPermissionsResult(requestCode, grantResults);
    }
}