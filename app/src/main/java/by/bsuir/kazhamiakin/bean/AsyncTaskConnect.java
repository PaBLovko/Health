package by.bsuir.kazhamiakin.bean;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import by.bsuir.kazhamiakin.controller.BluetoothConnectorController;
import by.bsuir.kazhamiakin.controller.ViewController;
import by.bsuir.kazhamiakin.exeption.bluetooth.BluetoothException;
import by.bsuir.kazhamiakin.ui.ViewActivity;
/**
 * @author Pablo on 15.03.2021
 * @project Health
 */

public class AsyncTaskConnect extends AsyncTask<Void, Void, Void> {

    private BluetoothDevice device;
    private ViewActivity viewActivity;
    private BluetoothConnector bluetoothConnector;
    private Pulse pulse;

    public AsyncTaskConnect(BluetoothDevice device, BluetoothConnector bluetoothConnector,
                            Pulse pulse, ViewActivity viewActivity) {
        this.device = device;
        this.viewActivity = viewActivity;
        this.bluetoothConnector = bluetoothConnector;
        this.pulse = pulse;
    }

    @Override
    protected Void doInBackground(Void... params){
        try {
            BluetoothConnector.ConnectedThread connectedThread =
                    new BluetoothConnectorController().connectToExisting(bluetoothConnector, device);
            pulse.getPreference().saveMacAddress(device.getAddress());
            pulse.setConnectedThread(connectedThread);
        } catch (BluetoothException e) {
            e.printStackTrace();
            new ViewController().viewWarning(viewActivity.getActivity(), viewActivity, e.getMessage());
            this.cancel(true);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        viewActivity.setBtnEnableSearchStart();
        viewActivity.setPbProgressNoVisibility();
        viewActivity.getProgressDialog().show();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        viewActivity.getProgressDialog().dismiss();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        viewActivity.getProgressDialog().dismiss();
        viewActivity.showFrameControls();
    }
}
