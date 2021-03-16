package by.bsuir.health.service;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import by.bsuir.health.bean.AsyncTaskConnect;
import by.bsuir.health.bean.BluetoothConnector;
import by.bsuir.health.bean.Pulse;
import by.bsuir.health.bean.SdFile;
import by.bsuir.health.ui.ViewActivity;

/**
 * @author Pablo on 08.03.2021
 * @project Health
 */
public class ItemClickService implements AdapterView.OnItemClickListener {

    private final ViewActivity viewActivity;
    private final BluetoothConnector bluetoothConnector;
    private ArrayList<SdFile> sdFiles;
    private Pulse pulse;
    private final Activity activity;
    private AsyncTaskConnect asyncTaskConnect;

    public ItemClickService(ViewActivity viewActivity, ArrayList<SdFile> sdFiles, Pulse pulse,
                            Activity activity, BluetoothConnector bluetoothConnector) {
        this.viewActivity = viewActivity;
        this.bluetoothConnector = bluetoothConnector;
        this.sdFiles = sdFiles;
        this.pulse = pulse;
        this.activity = activity;
        this.asyncTaskConnect = null;
    }

    public void setSdFiles(ArrayList<SdFile> sdFiles) {
        this.sdFiles = sdFiles;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.equals(viewActivity.getListDevices())) {
            BluetoothDevice device = bluetoothConnector.getBluetoothDevices().get(position);
            if (device != null) {
                asyncTaskConnect = new AsyncTaskConnect(device, viewActivity, bluetoothConnector, pulse, activity);
                asyncTaskConnect.execute();
            }
        }
        if (parent.equals(viewActivity.getListImages())){
            SdFile sdFile = sdFiles.get(position);
            if (sdFile != null){
//                showImage(sdFile.getImage());
                //TODO show image
            }
        }
    }
}
