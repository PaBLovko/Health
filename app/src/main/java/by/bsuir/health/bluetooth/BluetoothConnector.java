package by.bsuir.health.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import by.bsuir.health.bluetooth.exception.BluetoothException;
import by.bsuir.health.bluetooth.exception.ConnectionBluetoothException;
import by.bsuir.health.bluetooth.exception.NotSupportedBluetoothException;

/**
 * @author Pablo on 29.01.2021
 * @project Health
 */
public class BluetoothConnector extends Thread{
    private static BluetoothAdapter mBluetoothAdapter;

    private BluetoothSocket socket;
    private DataOutputStream outStream;
    private DataInputStream inputStream;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private String lastSensorValues;

    public BluetoothConnector(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        socket = null;
//        outStream = null;
//        inputStream = null;
        bluetoothDevices = new ArrayList<>();
//        lastSensorValues = "";
    }

    public synchronized void connect(BluetoothDevice device) throws BluetoothException {
        if (!isSupported())
            throw new NotSupportedBluetoothException();
        if (isConnected())
            return;
        try {
            Method m = device.getClass().getMethod("createRfcommSocket", int.class);
            socket = (BluetoothSocket) m.invoke(device, 1);
            socket.connect();

            outStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            socket = null;
            throw new ConnectionBluetoothException(e);
        }
    }

    @Override
    public void run() {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        StringBuilder buffer = new StringBuilder();
        final StringBuilder sbConsole = new StringBuilder();
        while (isConnected()) {
            try {
                int bytes = bis.read();
                buffer.append((char) bytes);
                int eof = buffer.indexOf("\r\n");

                if (eof > 0) {
                    sbConsole.append(buffer.toString());
                    lastSensorValues = buffer.toString();
                    buffer.delete(0, buffer.length());
                }

            } catch (IOException  e) {
                e.printStackTrace();
            }
        }
        try {
            bis.close();
            disconnect();
        } catch (BluetoothException | IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[]command) throws BluetoothException, IOException {
        if (outStream != null) {
            outStream.write(command);
            outStream.flush();
        } else {
            throw new ConnectionBluetoothException("Not connected");
        }
    }

    public int read() throws BluetoothException, IOException {
        int result;
        if (inputStream != null) {
            result = inputStream.read();
        } else {
            throw new ConnectionBluetoothException("Not connected");
        }
        return result;
    }


    public int read(byte[]response) throws BluetoothException, IOException {
        int result;
        if (inputStream != null) {
            result = inputStream.read(response);
        } else {
            throw new ConnectionBluetoothException("Not connected");
        }
        return result;
    }

    public synchronized void disconnect() throws BluetoothException {
        cancelDiscovery();

        if (outStream != null) {
            try {
                outStream.close();
            } catch (IOException e) {}
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {}
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {}
        }
        socket = null;
    }

    public boolean isConnected() {
        return socket != null;
    }
//    /***************************************************************
//     * 	Static methods
//     ***************************************************************/
//    /**
//     * get pared devices
//     * @return devices
//     * @throws BluetoothException
//     */
//    public static Map<String, String> getBondedDevices()
//            throws BluetoothException {
//        if (!isSupported())
//            throw new NotSupportedBluetoothException();
//
//        Map<String, String> mPairedDevicesArrayAdapter = new HashMap<String, String>();
//
//        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//        if (pairedDevices.size() > 0) {
//            for (BluetoothDevice device : pairedDevices) {
//                mPairedDevicesArrayAdapter.put(device.getName(), device.getAddress());
//            }
//        }
//        return mPairedDevicesArrayAdapter;
//    }
    /***************************************************************
     * 	Static methods
     ***************************************************************/
    /**
     * get pared devices
     * @return devices
     * @throws BluetoothException
     */
    public static ArrayList<BluetoothDevice> getBondedDevices()
            throws BluetoothException {
        if (!isSupported())
            throw new NotSupportedBluetoothException();

        Set<BluetoothDevice> deviceSet = mBluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> mPairedDevicesArrayAdapter = new ArrayList<>();
        if (deviceSet.size() > 0) {
            mPairedDevicesArrayAdapter.addAll(deviceSet);
        }
        return mPairedDevicesArrayAdapter;
    }
    /**
     * Cancel discovery
     */
    public static void cancelDiscovery()
            throws BluetoothException {
        if (!isSupported())
            throw new NotSupportedBluetoothException();

        if (getBluetoothAdapter().isDiscovering()) {
            getBluetoothAdapter().cancelDiscovery();
        }
    }
    /**
     * Start discovery
     * @return value
     * @throws BluetoothException
     */
    public static boolean startDiscovery()
            throws BluetoothException {
        if (!isSupported())
            throw new NotSupportedBluetoothException();

        if (getBluetoothAdapter().isDiscovering()) {
            getBluetoothAdapter().cancelDiscovery();
        }
        return getBluetoothAdapter().startDiscovery();
    }
    /**
     * Enable search
     */
    public static void enableSearch()
            throws BluetoothException {
        if (!isSupported())
            throw new NotSupportedBluetoothException();
        if (getBluetoothAdapter().isDiscovering()) {
            getBluetoothAdapter().cancelDiscovery();
        } else {
            //permission.access(this, permission.LocationPermission());
            getBluetoothAdapter().startDiscovery();
        }
    }
    /**
     * Get Adapter name
     * @return name
     * @throws BluetoothException
     */
    public static String getAdapterName()
            throws BluetoothException {
        if (!isSupported())
            throw new NotSupportedBluetoothException();
        return getBluetoothAdapter().getName();
    }
    /**
     * Is bluetooth supported
     * @return support state
     */
    public static boolean isSupported() {
        if (getBluetoothAdapter() == null)
            return false;
        return true;
    }
    /**
     * Is bluetooth enabled
     * @return enable state
     * @throws BluetoothException
     */
    public static boolean isEnabled()
            throws BluetoothException {
        if (!isSupported())
            throw new NotSupportedBluetoothException();
        return getBluetoothAdapter().isEnabled();
    }
    /**
     * get bluetooth adapter
     * @return adapter
     */
    public static BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }
    /**
     * get bluetooth adapter
     * @return ArrayList<adapter>
     */
    public ArrayList<BluetoothDevice> getBluetoothDevices() {
        return bluetoothDevices;
    }
    /**
     * set bluetooth adapter
     */
    public void setBluetoothDevices(ArrayList<BluetoothDevice> bluetoothDevices) {
        this.bluetoothDevices = bluetoothDevices;
    }
    /**
     * Clear devices
     */
    public void clear(){
        bluetoothDevices.clear();
    }
    /**
     * Add device
     */
    public void add(BluetoothDevice bluetoothDevice){
        bluetoothDevices.add(bluetoothDevice);
    }
    /**
     * get bluetooth socket
     * @return socket
     */
    public BluetoothSocket getSocket() {
        return socket;
    }
    /**
     * get last sensor values
     * @return String
     */
    public String getLastSensorValues() {
        return lastSensorValues;
    }
}
