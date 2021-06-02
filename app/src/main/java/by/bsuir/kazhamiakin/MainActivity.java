package by.bsuir.kazhamiakin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;

import by.bsuir.kazhamiakin.bean.BluetoothConnector;
import by.bsuir.kazhamiakin.bean.Pulse;
import by.bsuir.kazhamiakin.controller.BluetoothConnectorController;
import by.bsuir.kazhamiakin.controller.ThreadController;
import by.bsuir.kazhamiakin.controller.ViewController;
import by.bsuir.kazhamiakin.dao.preference.OnDelayChangedListener;
import by.bsuir.kazhamiakin.dao.preference.PrefModel;
import by.bsuir.kazhamiakin.dao.preference.SettingsActivity;
import by.bsuir.kazhamiakin.exeption.bluetooth.BluetoothException;
import by.bsuir.kazhamiakin.service.CheckedChangeService;
import by.bsuir.kazhamiakin.service.ClickService;
import by.bsuir.kazhamiakin.service.ItemClickService;
import by.bsuir.kazhamiakin.service.ItemLongClickListener;
import by.bsuir.kazhamiakin.service.OnSwitchChangedListener;
import by.bsuir.kazhamiakin.service.ReceiverService;
import by.bsuir.kazhamiakin.ui.ListAdapter;
import by.bsuir.kazhamiakin.ui.ViewActivity;
import by.bsuir.kazhamiakin.util.CheckPermissionUtil;
import by.bsuir.kazhamiakin.util.IPermissionsResult;

import static by.bsuir.kazhamiakin.ui.ViewActivity.BT_BOUNDED;
import static by.bsuir.kazhamiakin.ui.ViewActivity.REQ_ENABLE_BT;

/**
 * @author Pablo on 07.11.2020
 * @project Health
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ListAdapter listAdapter;
    private Pulse pulse;
    private PrefModel preference;
    private CheckPermissionUtil checkPermissionUtil;
    private BluetoothConnector bluetoothConnector;

    private ViewActivity viewActivity;

    private CheckedChangeService checkedChangeService;
    private ItemClickService itemClickService;
    private ClickService clickService;
    private ReceiverService receiverService;
    private ItemLongClickListener itemLongClickService;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference = new PrefModel(this);
        viewActivity = new ViewActivity(this);
        new ViewController().setProgressDialog(this, viewActivity.getProgressDialog());

        bluetoothConnector = new BluetoothConnector();

        if (BluetoothConnector.getBluetoothAdapter() == null) {
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCreate: " + getString(R.string.bluetooth_not_supported));
            finish();
        }

        try {
            if (BluetoothConnector.isEnabled()) {
                viewActivity.showFrameControllers();
                viewActivity.setSwitchEnableBtChecked(true);
                listAdapter = new ViewController().getListAdapter(
                        this, bluetoothConnector, BT_BOUNDED);
                viewActivity.setListDevices(listAdapter);
            }
        } catch (BluetoothException e) {
            e.printStackTrace();
        }

        checkPermissionUtil = CheckPermissionUtil.getInstance();
        checkPermissionUtil.checkPermissions(this, CheckPermissionUtil.addPermissions(),
                permissionsResult);


        pulse = new Pulse(bluetoothConnector, viewActivity, preference);

        checkedChangeService = new CheckedChangeService(viewActivity, this);

        itemClickService = new ItemClickService(viewActivity, pulse, bluetoothConnector);

        itemLongClickService = new ItemLongClickListener(viewActivity);

        clickService = new ClickService(viewActivity,pulse,this);

        receiverService = new ReceiverService(viewActivity, bluetoothConnector, listAdapter, pulse);

        new BluetoothConnectorController().addReceiver(this, receiverService);

        PrefModel.addDelayListener(new OnDelayChangedListener() {
            @Override
            public void OnDelayChanged() throws BluetoothException, IOException {
                if (pulse.getConnectedThread().isConnected())
                    pulse.getConnectedThread().write(
                            (preference.getDelayTimer() + "Delay#").getBytes());
            }
        });

        CheckedChangeService.addItemListener(new OnSwitchChangedListener() {
            @Override
            public void OnSwitchChanged(String command) {
                try {
                    if (pulse.getBluetoothConnector().isConnected() && command != null)
                        pulse.getConnectedThread().write(command.getBytes());
                } catch (BluetoothException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        new ViewController().setListeners(viewActivity, checkedChangeService, itemClickService,
                clickService, itemLongClickService);
    }

    IPermissionsResult permissionsResult =
            new IPermissionsResult() {
                @Override
                public void passPermissions() {
                    Toast.makeText(MainActivity.this, "Welcome",
                            Toast.LENGTH_SHORT).show();
                }
                @Override
                public void forbidPermissions() {
                    //TODO finish
                }
                @Override
                public void repeatPermissions() {
                    checkPermissionUtil.checkPermissions(
                            MainActivity.this, CheckPermissionUtil.addPermissions(),
                            permissionsResult);
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        pulse.cancelTimer();
        if (item.getItemId() == R.id.item_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.item_storage){
            new ViewController().showStorage(viewActivity);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQ_ENABLE_BT) {
            try {
                if (resultCode == RESULT_OK && BluetoothConnector.isEnabled()) {
                    viewActivity.showFrameControllers();
                    listAdapter = new ViewController().getListAdapter(
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
        if (viewActivity.getFrameStorage().isShown()){
            if (pulse.getConnectedThread() != null && pulse.getConnectedThread().isConnected())
                viewActivity.showFrameControls();
            else if (viewActivity.getSwitchEnableBt().isChecked())
                viewActivity.showFrameControllers();
            else viewActivity.showFrameMessage();
        }else if (viewActivity.getFrameResult().isShown()){
            viewActivity.showFrameStorage();
        }else new ViewController().openQuitDialog(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        checkPermissionUtil.onRequestPermissionsResult(
                requestCode, grantResults);
    }
}