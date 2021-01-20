package by.bsuir.health.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import by.bsuir.health.ListAdapter;
import by.bsuir.health.R;
import by.bsuir.health.SdFile;
import by.bsuir.health.preference.OnDelayChangedListener;
import by.bsuir.health.preference.PrefActivity;
import by.bsuir.health.preference.PrefModel;

/**
 * @author Pablo on 07.11.2020
 * @project Health
 */

public class MainActivity extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemClickListener,
        View.OnClickListener{

    private static final String TAG               = MainActivity.class.getSimpleName();
    public  static final int REQUEST_CODE = 1;

    private static final int    REQ_ENABLE_BT     = 10;
    public  static final int    BT_BOUNDED        = 21;
    public  static final int    BT_SEARCH         = 22;

    public  static final int    BUZZER            = 30;
    public  static final int    LED               = 31;
    private static final String KEY_MAC_ADDRESS   = "key_mac_address";

    private FrameLayout    frameMessage;
    private LinearLayout   frameControls;

    private RelativeLayout frameLedControls;
    private Button         btnDisconnect;
    private Switch         switchBuzzer;
    private Switch         switchLed;
    private EditText       etConsole;

    private Switch         switchEnableBt;
    private Button         btnEnableSearch;
    private ProgressBar    pbProgress;
    private ListView       listDevices;

    private BluetoothAdapter           bluetoothAdapter;
    private ListAdapter                listAdapter;
    private ArrayList<BluetoothDevice> bluetoothDevices;

    private ConnectThread       connectThread;
    private ConnectedThread     connectedThread;

    private ProgressDialog      progressDialog;

    private LineGraphSeries     seriesTemp;
    private LineGraphSeries     seriesRand;
    private String              lastSensorValues = "";
    private int                 lastDelay;
    private Handler             handler;
    private Runnable            timer;
    private int                 xTempLastValue = 0;
    private int                 xRandLastValue = 0;

    private PrefModel           preference;
    private SharedPreferences   sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameMessage        = findViewById(R.id.frame_message);
        frameControls       = findViewById(R.id.frame_control);

        switchEnableBt      = findViewById(R.id.switch_enable_bt);
        btnEnableSearch     = findViewById(R.id.btn_enable_search);
        pbProgress          = findViewById(R.id.pb_progress);
        listDevices         = findViewById(R.id.lv_bt_device);

        frameLedControls    = findViewById(R.id.frameLedControls);
        btnDisconnect       = findViewById(R.id.btn_disconnect);
        switchBuzzer        = findViewById(R.id.switch_buzzer);
        switchLed            = findViewById(R.id.switch_led);
        etConsole            = findViewById(R.id.et_console);

        GraphView gvGraph = findViewById(R.id.gv_graph);

        preference          = new PrefModel(this);
        sharedPreferences   = getSharedPreferences("MAC_ADDRESS", MODE_PRIVATE);

        seriesTemp                  = new LineGraphSeries();
        seriesRand                  = new LineGraphSeries();
        seriesTemp.setColor(Color.GREEN);
        seriesRand.setColor(Color.RED);

        gvGraph.addSeries(seriesTemp);
        gvGraph.addSeries(seriesRand);
        gvGraph.getViewport().setMinX(0);
        gvGraph.getViewport().setMaxX(preference.getPointsCount());
        gvGraph.getViewport().setXAxisBoundsManual(true);

        switchEnableBt.setOnCheckedChangeListener(this);
        btnEnableSearch.setOnClickListener(this);
        listDevices.setOnItemClickListener(this);

        btnDisconnect.setOnClickListener(this);
        switchLed.setOnCheckedChangeListener(this);
        switchBuzzer.setOnCheckedChangeListener(this);

        bluetoothDevices = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.connecting));
        progressDialog.setMessage(getString(R.string.please_wait));

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCreate: " + getString(R.string.bluetooth_not_supported));
            finish();
        }

        if (bluetoothAdapter.isEnabled()) {
            showFrameControls();
            switchEnableBt.setChecked(true);
            setListAdapter(BT_BOUNDED);
        }

        accessExternalPermission();

        ArrayList<SdFile> sdFiles = new ArrayList<>();
        final String DIR_SD = "MyFiles";

        ArrayList<String> FilesInFolder = GetFiles(
                Environment.getExternalStorageDirectory().toString() + "/" + DIR_SD);
        //Toast.makeText(MainActivity.this, "Millis: " + FilesInFolder, Toast.LENGTH_SHORT).show();

//        lv = (ListView)findViewById(R.id.filelist);
//
//        lv.setAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, FilesInFolder));
//
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                // Clicking on items
//            }
//        });

        PrefModel.addDelayListener(new OnDelayChangedListener() {
            @Override
            public void OnDelayChanged() {
                if (connectedThread != null && connectThread.isConnect()) {
                    connectedThread.write(preference.getDelayTimer() + "Delay#");
                }
            }
        });
    }



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
        lastDelay = preference.getDelayTimer();
        cancelTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (connectedThread != null) {
            startTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
        unregisterReceiver(receiver);
        if (connectThread != null) {
            connectThread.cancel();
        }
        if (connectedThread != null) {
            connectedThread.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnEnableSearch)) {
            enableSearch();
        } else if (v.equals(btnDisconnect)) {
            cancelTimer();
            if (connectedThread != null) {
                connectedThread.cancel();
            }
            if (connectThread != null) {
                connectThread.cancel();
            }
            showFrameControls();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.equals(listDevices)) {
            BluetoothDevice device = bluetoothDevices.get(position);
            if (device != null) {
                btnEnableSearch.setText(R.string.start_search);
                pbProgress.setVisibility(View.GONE);
                connectThread = new ConnectThread(device);
                connectThread.start();
                startTimer();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(switchEnableBt)) {
            enableBt(isChecked);
            if (!isChecked) {
                showFrameMessage();
            }
        } else if (buttonView.equals(switchBuzzer)) {
            // TODO включение или отключение динамика
            enableCheckBox(BUZZER, isChecked);
        } else if (buttonView.equals(switchLed)) {
            // TODO включение или отключение светодиода
            enableCheckBox(LED, isChecked);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQ_ENABLE_BT) {
            if (resultCode == RESULT_OK && bluetoothAdapter.isEnabled()) {
                showFrameControls();
                setListAdapter(BT_BOUNDED);
            } else if (resultCode == RESULT_CANCELED) {
                enableBt(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                MainActivity.this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Таки да!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }

    private void showFrameMessage() {
        frameMessage.setVisibility(View.VISIBLE);
        frameLedControls.setVisibility(View.GONE);
        frameControls.setVisibility(View.GONE);
    }

    private void showFrameControls() {
        frameMessage.setVisibility(View.GONE);
        frameLedControls.setVisibility(View.GONE);
        frameControls.setVisibility(View.VISIBLE);
    }

    private void showFrameLedControls() {
        frameLedControls.setVisibility(View.VISIBLE);
        frameMessage.setVisibility(View.GONE);
        frameControls.setVisibility(View.GONE);
    }

    private void enableBt(boolean flag) {
        if (flag) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQ_ENABLE_BT);
        } else {
            bluetoothAdapter.disable();
        }
    }

    private void setListAdapter(int type) {
        bluetoothDevices.clear();
        int iconType = R.drawable.ic_bluetooth_bounded_device;
        switch (type) {
            case BT_BOUNDED:
                bluetoothDevices = getBoundedDevices();
                iconType = R.drawable.ic_bluetooth_bounded_device;
                break;
            case BT_SEARCH:
                iconType = R.drawable.ic_bluetooth_search_device;
                break;
        }
        listAdapter = new ListAdapter(this, bluetoothDevices, iconType);
        listDevices.setAdapter(listAdapter);
    }

    private ArrayList<BluetoothDevice> getBoundedDevices() {
        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> tmpArrayList = new ArrayList<>();
        if (deviceSet.size() > 0) {
            tmpArrayList.addAll(deviceSet);
        }
        return tmpArrayList;
    }

    public ArrayList<String> GetFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<String>();
        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++)
                MyFiles.add(files[i].getName());
        }
        return MyFiles;
    }

    private void enableSearch() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        } else {
            //accessLocationPermission();
            bluetoothAdapter.startDiscovery();
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        btnEnableSearch.setText(R.string.stop_search);
                        pbProgress.setVisibility(View.VISIBLE);
                        setListAdapter(BT_SEARCH);
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        btnEnableSearch.setText(R.string.start_search);
                        pbProgress.setVisibility(View.GONE);
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device != null) {
                            bluetoothDevices.add(device);
                            listAdapter.notifyDataSetChanged();
                        }
                        if(preference.isLastConnectDevice()){
                            assert device != null;
                            if(device.getAddress().equals(sharedPreferences.getString(
                                    KEY_MAC_ADDRESS, ""))){
                                btnEnableSearch.setText(R.string.start_search);
                                pbProgress.setVisibility(View.GONE);
                                setListAdapter(BT_SEARCH);
                                connectToExisting(device);
                            }
                        }
                        break;
                }
            }
        }
    };

    private void connectToExisting(BluetoothDevice device){
        connectThread = new ConnectThread(device);
        connectThread.start();
        startTimer();
    }

    /**
     * Запрос на разрешение данных о местоположении (для Marshmallow 6.0 и выше)
     */
//    private void accessLocationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            int accessCoarseLocation = this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
//            int accessFineLocation = this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
//
//            List<String> listRequestPermission = new ArrayList<String>();
//
//            if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
//                listRequestPermission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
//            }
//            if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
//                listRequestPermission.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
//            }
//
//            if (!listRequestPermission.isEmpty()) {
//                String[] strRequestPermission = listRequestPermission.toArray(new String[listRequestPermission.size()]);
//                this.requestPermissions(strRequestPermission, REQUEST_CODE_LOC);
//            }
//        }
//    }

        private void accessExternalPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int readExternalStorage = this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeExternalStorage = this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            List<String> listRequestPermission = new ArrayList<>();
            if (readExternalStorage != PackageManager.PERMISSION_GRANTED)
                listRequestPermission.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            if (writeExternalStorage != PackageManager.PERMISSION_GRANTED)
                listRequestPermission.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (!listRequestPermission.isEmpty()) {
                String[] strRequestPermission = listRequestPermission.toArray(new String[0]);
                this.requestPermissions(strRequestPermission, REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int gr : grantResults) {
                    // Check if request is granted or not
                    if (gr != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                //TODO - Add your code here to start Discovery
            }
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket bluetoothSocket = null;
        private boolean success = false;
        private String nameThisDevice;
        private String macAddressNow;
        private String nameDevice;

        public ConnectThread(BluetoothDevice device) {
            try {
                Method method = device.getClass().getMethod("createRfcommSocket", int.class);
                bluetoothSocket = (BluetoothSocket) method.invoke(device, 1);
                progressDialog.show();
                nameDevice = "HC-05";
                nameThisDevice = device.getName();
                macAddressNow = device.getAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void saveMacAddress(){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_MAC_ADDRESS, macAddressNow);
            editor.apply();
        }

        @Override
        public void run() {
            try {
                bluetoothSocket.connect();
                success = nameThisDevice.equals(nameDevice);
                progressDialog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,
                                "Не могу соединиться!",Toast.LENGTH_SHORT).show();
                    }
                });
                cancel();
            }
            if (success) {
                saveMacAddress();
                connectedThread = new ConnectedThread(bluetoothSocket);
                connectedThread.start();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showFrameLedControls();
                    }
                });
            }
        }

        public boolean isConnect() {
            return bluetoothSocket.isConnected();
        }

        public void cancel() {
            try {
                Log.d(TAG, "cancel: " + this.getClass().getSimpleName());
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread  extends  Thread {
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private boolean isConnected;

        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.inputStream = inputStream;
            this.outputStream = outputStream;
            isConnected = true;
        }

        @Override
        public void run() {
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            StringBuilder buffer = new StringBuilder();
            final StringBuilder sbConsole = new StringBuilder();

            while (isConnected) {
                try {
                    int bytes = bis.read();
                    buffer.append((char) bytes);
                    int eof = buffer.indexOf("\r\n");

                    if (eof > 0) {
                        sbConsole.append(buffer.toString());
                        lastSensorValues = buffer.toString();
                        buffer.delete(0, buffer.length());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                bis.close();
                cancel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void write(String command) {
            byte[] bytes = command.getBytes();
            if (outputStream != null) {
                try {
                    outputStream.write(bytes);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            try {
                isConnected = false;
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void enableCheckBox(int led, boolean state) {
        if (connectedThread != null && connectThread.isConnect()) {
            String command = "";
            switch (led) {
                case BUZZER:
                    command = (state) ? "buzzer on##" : "buzzer off#";
                    break;
                case LED:
                    command = (state) ? "led on#" : "led off#";
                    break;
            }
            connectedThread.write(command);
        }
    }

    private Map parseData(String data) {    // temp:37|humidity:80
        if (data.indexOf('|') > 0) {
            HashMap map = new HashMap();
            String[] pairs = data.split("\\|");
            for (String pair: pairs) {
                String[] keyValue = pair.split(":");
                map.put(keyValue[0], keyValue[1]);
            }
            return map;
        }
        return null;
    }


    private void startTimer() {
        cancelTimer();
        handler = new Handler();
        final MovementMethod movementMethod = new ScrollingMovementMethod();
        handler.postDelayed(timer = new Runnable() {
            @Override
            public void run() {
                etConsole.setText(lastSensorValues);
                etConsole.setMovementMethod(movementMethod);
                Map dataSensor = parseData(lastSensorValues);
                if (dataSensor != null) {
                    if (dataSensor.containsKey("Temp") && dataSensor.containsKey("rand")) {
                        int temp = Integer.parseInt(dataSensor.get("Temp").toString());
                        int rand = Integer.parseInt(dataSensor.get("rand").toString().trim());

                        if(preference.getOperationMode().equals("pulse")){
                            seriesTemp.appendData(new DataPoint(xTempLastValue, temp), true, preference.getPointsCount());
                        }
                        if(preference.getOperationMode().equals("spo")){
//                            seriesRand.appendData(new DataPoint(xRandLastValue, rand), true, preference.getPointsCount());
                        }
                        if(preference.getOperationMode().equals("cardio")){
                            //seriesTemp.appendData(new DataPoint(xTempLastValue, temp), true, preference.getPointsCount());
                        }
//                        Toast.makeText(MainActivity.this, "Millis: " + dataSensor.get("millis"), Toast.LENGTH_SHORT).show();
                    }
                    xTempLastValue++;
                    xRandLastValue++;
                }
                handler.postDelayed(this, preference.getDelayTimer());
            }
        }, preference.getDelayTimer());
    }

    private void cancelTimer() {
        if (handler != null) {
            handler.removeCallbacks(timer);
        }
    }
}
