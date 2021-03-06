package by.bsuir.kazhamiakin.bean;

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

import by.bsuir.kazhamiakin.exeption.bluetooth.BluetoothException;
import by.bsuir.kazhamiakin.exeption.bluetooth.ConnectionBluetoothException;
import by.bsuir.kazhamiakin.exeption.bluetooth.NotSupportedBluetoothException;

import static by.bsuir.kazhamiakin.exeption.bluetooth.BluetoothException.NOT_CONNECTED;

/**
 * @author Pablo on 29.01.2021
 * @project Health
 */
public class BluetoothConnector {

    private static BluetoothAdapter mBluetoothAdapter;

    private BluetoothSocket socket;
    private ArrayList<BluetoothDevice> bluetoothDevices;

    public BluetoothConnector(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevices = new ArrayList<>();
    }

    public synchronized void connect(BluetoothDevice device) throws BluetoothException {
        if (isNotSupported())
            throw new NotSupportedBluetoothException();
        if (isConnected())
            return;
        try {
            Method m =
                    device.getClass().getMethod("createRfcommSocket", int.class);
            socket = (BluetoothSocket) m.invoke(device, 1);
            socket.connect();
        } catch (Exception e) {
            socket = null;
            throw new ConnectionBluetoothException(NOT_CONNECTED);
        }
    }

    public static class ConnectedThread extends Thread {
        private DataOutputStream outputStream;
        private DataInputStream inputStream;
        private boolean isConnected;
        private String lastSensorValues;
        private BluetoothSocket bluetoothSocket;

        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
            this.inputStream = null;
            this.outputStream = null;
        }

        public synchronized void connect(){
            try {
                outputStream = new DataOutputStream(bluetoothSocket.getOutputStream());
                inputStream = new DataInputStream(bluetoothSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            isConnected = true;
        }

        @Override
        public void run() {
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            StringBuilder buffer = new StringBuilder();
            final StringBuilder sbConsole = new StringBuilder();
            try {
                while (isConnected) {
                    int bytes = bis.read();
                    buffer.append((char) bytes);
                    int eof = buffer.indexOf("\r\n");
                    if (eof > 0) {
                        sbConsole.append(buffer.toString());
                        lastSensorValues = buffer.toString();
                        buffer.delete(0, buffer.length());
                    }
                }
                bis.close();
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isConnected() {
            return isConnected;
        }

        public String getLastSensorValues() {
            return lastSensorValues;
        }

        public synchronized void disconnect() throws IOException {
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            isConnected = false;
        }

        public void write(byte[]command) throws BluetoothException, IOException {
            if (outputStream != null) {
                outputStream.write(command);
                outputStream.flush();
            } else throw new ConnectionBluetoothException(NOT_CONNECTED);
        }

        public int read() throws BluetoothException, IOException {
            int result;
            if (inputStream != null) {
                result = inputStream.read();
            } else throw new ConnectionBluetoothException(NOT_CONNECTED);
            return result;
        }

        public int read(byte[]response) throws BluetoothException, IOException {
            int result;
            if (inputStream != null) {
                result = inputStream.read(response);
            } else {
                throw new ConnectionBluetoothException(NOT_CONNECTED);
            }
            return result;
        }
    }

    public synchronized void disconnect() throws BluetoothException, IOException {
        cancelDiscovery();
        if (socket != null) socket.close();
        socket = null;
    }

    public boolean isConnected() {
        return socket != null;
    }

    /**
     * get pared devices
     * @return devices
     * @throw BluetoothException
     */
    public static ArrayList<BluetoothDevice> getBondedDevices()
            throws BluetoothException {
        if (isNotSupported())
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
        if (isNotSupported())
            throw new NotSupportedBluetoothException();

        if (getBluetoothAdapter().isDiscovering()) {
            getBluetoothAdapter().cancelDiscovery();
        }
    }
    /**
     * Start discovery
     * @return value
     * @throw BluetoothException
     */
    public static boolean startDiscovery()
            throws BluetoothException {
        if (isNotSupported())
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
        if (isNotSupported())
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
     * @throw BluetoothException
     */
    public static String getAdapterName()
            throws BluetoothException {
        if (isNotSupported())
            throw new NotSupportedBluetoothException();
        return getBluetoothAdapter().getName();
    }
    /**
     * Is bluetooth supported
     * @return support state
     */
    public static boolean isNotSupported() {
        return getBluetoothAdapter() == null;
    }
    /**
     * Is bluetooth enabled
     * @return enable state
     * @throw BluetoothException
     */
    public static boolean isEnabled()
            throws BluetoothException {
        if (isNotSupported())
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
}
