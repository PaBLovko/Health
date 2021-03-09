package by.bsuir.health.service;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import by.bsuir.health.bean.BluetoothConnector;
import by.bsuir.health.bean.SdFile;
import by.bsuir.health.controller.BluetoothConnectorController;
import by.bsuir.health.bean.Pulse;
import by.bsuir.health.controller.ViewController;
import by.bsuir.health.dao.preference.PrefModel;
import by.bsuir.health.exeption.bluetooth.BluetoothException;
import by.bsuir.health.ui.ViewActivity;

/**
 * @author Pablo on 08.03.2021
 * @project Health
 */
public class ItemClickService implements AdapterView.OnItemClickListener {

    private static final List<OnItemChangedListener> listeners = new ArrayList<>();
    private final ViewActivity viewActivity;
    private final BluetoothConnector bluetoothConnector;
    private ArrayList<SdFile> sdFiles;
    private Pulse pulse;
    private final PrefModel preference;
    private final Activity activity;

    public ItemClickService(ViewActivity viewActivity, ArrayList<SdFile> sdFiles, Pulse pulse,
                            PrefModel preference, Activity activity,
                            BluetoothConnector bluetoothConnector) {
        this.viewActivity = viewActivity;
        this.bluetoothConnector = bluetoothConnector;
        this.sdFiles = sdFiles;
        this.pulse = pulse;
        this.preference = preference;
        this.activity = activity;
    }

    public static void addItemListener(OnItemChangedListener l) {
        listeners.add(l);
    }

    public void setSdFiles(ArrayList<SdFile> sdFiles) {
        this.sdFiles = sdFiles;
    }

    public Pulse getPulse() {
        return pulse;
    }

    public void setPulse(Pulse pulse) {
        this.pulse = pulse;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.equals(viewActivity.getListDevices())) {
            BluetoothDevice device = bluetoothConnector.getBluetoothDevices().get(position);
            if (device != null) {
                viewActivity.setBtnEnableSearchStart();
                viewActivity.setPbProgressNoVisibility();
                try {
                    viewActivity.getProgressDialog().show();
                    BluetoothConnector.ConnectedThread connectedThread =
                            new BluetoothConnectorController().connectToExisting(bluetoothConnector,
                                    device);
                    preference.saveMacAddress(device.getAddress());
                    viewActivity.getProgressDialog().dismiss();
                    viewActivity.showFrameLedControls();
                    pulse = new Pulse(bluetoothConnector, connectedThread, preference,viewActivity);
                    pulse.startTimer();
                    for (OnItemChangedListener l : listeners)
                        l.OnItemChanged();
                } catch (BluetoothException e) {
                    e.printStackTrace();
                    new ViewController().viewWarning(activity, viewActivity, e.getMessage());
                }
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
