package by.bsuir.health.main;

import android.Manifest;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import by.bsuir.health.CheckPermissionUtil;
import by.bsuir.health.ImageFileFilter;
import by.bsuir.health.ListAdapter;
import by.bsuir.health.ListFile;
import by.bsuir.health.R;
import by.bsuir.health.SdFile;
import by.bsuir.health.bluetooth.BluetoothConnector;
import by.bsuir.health.bluetooth.exception.BluetoothException;
import by.bsuir.health.bluetooth.exception.ConnectionBluetoothException;
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

    private static final int    REQ_ENABLE_BT     = 10;
    public  static final int    BT_BOUNDED        = 21;
    public  static final int    BT_SEARCH         = 22;

    public  static final int    BUZZER            = 30;
    public  static final int    LED               = 31;
    private static final String KEY_MAC_ADDRESS   = "key_mac_address";

    private FrameLayout    frameMessage;
    private LinearLayout   frameControls;

    private LinearLayout        frameStorage;
    private String              storageDirectory;
    private ListView            listImages;
    private ArrayList<SdFile>   sdFiles;

    private RelativeLayout frameLedControls;
    private Button         btnDisconnect;
    private Button         btnStart;
    private Button         btnStorage;
    private Switch         switchBuzzer;
    private Switch         switchLed;
    private EditText       etConsole;

    private Switch         switchEnableBt;
    private Button         btnEnableSearch;
    private ProgressBar    pbProgress;
    private ListView       listDevices;

    private ListAdapter    listAdapter;
    private ListFile       listFile;

    private ProgressDialog      progressDialog;

    private LineGraphSeries     seriesTemp;
    private LineGraphSeries     seriesRand;
    private String              lastSensorValues = "";
    private Handler             handler;
    private Runnable            timer;
    private int                 xTempLastValue = 0;
    private int                 xRandLastValue = 0;

    private PrefModel           preference;
    private SharedPreferences   sharedPreferences;

    private CheckPermissionUtil checkPermissionUtil;
    private String []           permissions;

//    private ImageView           imageview;

    private BluetoothConnector bluetoothConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameMessage        = findViewById(R.id.frame_message);
        frameControls       = findViewById(R.id.frame_control);
        frameStorage        = findViewById(R.id.frame_storage);
        listImages          = findViewById(R.id.lv_image);

        switchEnableBt      = findViewById(R.id.switch_enable_bt);
        btnEnableSearch     = findViewById(R.id.btn_enable_search);
        pbProgress          = findViewById(R.id.pb_progress);
        listDevices         = findViewById(R.id.lv_device);

        frameLedControls    = findViewById(R.id.frameLedControls);
        btnDisconnect       = findViewById(R.id.btn_disconnect);
        btnStart            = findViewById(R.id.btn_start);
        btnStorage          = findViewById(R.id.btn_storage);
        switchBuzzer        = findViewById(R.id.switch_buzzer);
        switchLed           = findViewById(R.id.switch_led);
        etConsole           = findViewById(R.id.et_console);

        GraphView gvGraph   = findViewById(R.id.gv_graph);

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

        btnStorage.setOnClickListener(this);
        listImages.setOnItemClickListener(this);

        btnStart.setOnClickListener(this);
        btnDisconnect.setOnClickListener(this);
        switchLed.setOnCheckedChangeListener(this);
        switchBuzzer.setOnCheckedChangeListener(this);

        sdFiles = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.connecting));
        progressDialog.setMessage(getString(R.string.please_wait));

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        bluetoothConnector = new BluetoothConnector();

        if (BluetoothConnector.getBluetoothAdapter() == null) {
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCreate: " + getString(R.string.bluetooth_not_supported));
            finish();
        }

        try {
            if (BluetoothConnector.isEnabled()) {
                showFrameControls();
                switchEnableBt.setChecked(true);
                setListAdapter(BT_BOUNDED);
            }
        } catch (BluetoothException e) {
            e.printStackTrace();
        }

        PrefModel.addDelayListener(new OnDelayChangedListener() {
            @Override
            public void OnDelayChanged() throws BluetoothException, IOException {
//                if (connectedThread != null && connectThread.isConnect()) {
//                    connectedThread.write(preference.getDelayTimer() + "Delay#");
                if (bluetoothConnector.isConnected()) {
                    bluetoothConnector.write((preference.getDelayTimer() + "Delay#").getBytes());
                }
            }
        });

        permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        checkPermissionUtil = CheckPermissionUtil.getInstance();
        checkPermissionUtil.checkPermissions(this, permissions, permissionsResult);

        if(cardAvailable()) storageDirectory =
                Environment.getExternalStorageDirectory().toString() + "/" + DIR_SD;

//        imageview = (ImageView) findViewById();
    }

    CheckPermissionUtil.IPermissionsResult permissionsResult =
            new CheckPermissionUtil.IPermissionsResult() {
        @Override
        public void passPermissions() {
            Toast.makeText(MainActivity.this, "Welcom", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void forbidPermissions() {
            //System.out.println("finish");
        }
        @Override
        public void repeatPermissions() {
            checkPermissionUtil.checkPermissions(MainActivity.this, permissions, permissionsResult);
        }
    };


    boolean cardAvailable(){        // проверяем доступность SD
        //Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static final String LOG_TAG = "myLogs";
    public static final String FILENAME = "file";
    public static final String DIR_SD = "MyFiles";
    public static final String FILENAME_SD = "fileSD";

    void writeFileSD() {
        cardAvailable();
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write("Содержимое файла на SD");
            // закрываем поток
            bw.close();
//            Log.d(TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<SdFile> updateSdList(ArrayList<File> files){
        ArrayList<SdFile> sdFiles = new ArrayList<>();
        for (File file : files) sdFiles.add(new SdFile(BitmapFactory.decodeFile(file.getAbsolutePath()), file.getName(), file.lastModified()));
        return sdFiles;
    }

    public ArrayList<File> getFiles(String DirectoryPath){
        File f = new File(DirectoryPath);
        f.mkdirs();
        File[] files = f.listFiles(new ImageFileFilter());
        if (files.length == 0)
            return null;
        else return new ArrayList<>(Arrays.asList(files));
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
        cancelTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (connectedThread != null) {
        if (bluetoothConnector.isConnected()) {
            startTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        try {
            disconnection();
        } catch (BluetoothException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnEnableSearch)) {
//            enableSearch();
            try {
                bluetoothConnector.enableSearch();
            } catch (BluetoothException e) {
                e.printStackTrace();
            }
            try {
                BluetoothConnector.enableSearch();
            } catch (BluetoothException e) {
                e.printStackTrace();
            }
        } else if (v.equals(btnDisconnect)) {
            try {
                disconnection();
            } catch (BluetoothException e) {
                e.printStackTrace();
            }
            showFrameControls();
        } else if (v.equals(btnStart)){
//
        } else if (v.equals(btnStorage)){
            try {
                disconnection();
            } catch (BluetoothException e) {
                e.printStackTrace();
            }
            setListFile();
            showFrameStorage();
        }
    }

    private void setListFile() {
        sdFiles = updateSdList(getFiles(storageDirectory));
        listFile = new ListFile(this, sdFiles);
        listImages.setAdapter(listFile);
    }

    private void disconnection() throws BluetoothException {
        cancelTimer();
//        if (connectedThread != null) connectedThread.cancel();
//        if (connectThread != null) connectThread.cancel();
        if (bluetoothConnector.isConnected()) bluetoothConnector.disconnect();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.equals(listDevices)) {
            BluetoothDevice device = bluetoothConnector.getBluetoothDevices().get(position);
            if (device != null) {
                btnEnableSearch.setText(R.string.start_search);
                pbProgress.setVisibility(View.GONE);
                try {
                    connectToExisting(device);
                } catch (BluetoothException e) {
                    e.printStackTrace();
                    viewWarning(e.getMessage());
                }
            }
        }
        if (parent.equals(listImages)){
            SdFile sdFile = sdFiles.get(position);
            if (sdFile != null){
                showImage(sdFile.getImage());
            }
        }
    }

    private void showImage(Bitmap bitmap){
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(switchEnableBt)) {
            enableBt(isChecked);
            if (!isChecked) showFrameMessage();
        } else if (buttonView.equals(switchBuzzer)) {
            try {
                enableCheckBox(BUZZER, isChecked); // TODO включение или отключение динамика
            } catch (BluetoothException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (buttonView.equals(switchLed)) {
            try {
                enableCheckBox(LED, isChecked);// TODO включение или отключение светодиода
            } catch (BluetoothException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQ_ENABLE_BT) {
            try {
                if (resultCode == RESULT_OK && BluetoothConnector.isEnabled()) {
                    showFrameControls();
                    setListAdapter(BT_BOUNDED);
                } else if (resultCode == RESULT_CANCELED) {
                    enableBt(true);
                }
            } catch (BluetoothException e) {
                e.printStackTrace();
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
        frameStorage.setVisibility(View.GONE);
    }

    private void showFrameControls() {
        frameMessage.setVisibility(View.GONE);
        frameLedControls.setVisibility(View.GONE);
        frameControls.setVisibility(View.VISIBLE);
        frameStorage.setVisibility(View.GONE);
    }

    private void showFrameLedControls() {
        frameLedControls.setVisibility(View.VISIBLE);
        frameMessage.setVisibility(View.GONE);
        frameControls.setVisibility(View.GONE);
        frameStorage.setVisibility(View.GONE);
    }

    private void showFrameStorage(){
        frameLedControls.setVisibility(View.GONE);
        frameMessage.setVisibility(View.GONE);
        frameControls.setVisibility(View.GONE);
        frameStorage.setVisibility(View.VISIBLE);
    }

    private void enableBt(boolean flag) {
        if (flag) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQ_ENABLE_BT);
        } else {
            BluetoothConnector.getBluetoothAdapter().disable();
        }
    }

    private void setListAdapter(int type) throws BluetoothException {
        bluetoothConnector.clear();
        int iconType = R.drawable.ic_bluetooth_bounded_device;
        switch (type) {
            case BT_BOUNDED:
                bluetoothConnector.setBluetoothDevices(BluetoothConnector.getBondedDevices());
                iconType = R.drawable.ic_bluetooth_bounded_device;
                break;
            case BT_SEARCH:
                iconType = R.drawable.ic_bluetooth_search_device;
                break;
        }
        listAdapter = new ListAdapter(this, bluetoothConnector.getBluetoothDevices(), iconType);
        listDevices.setAdapter(listAdapter);
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
                        try {
                            setListAdapter(BT_SEARCH);
                        } catch (BluetoothException e) {
                            e.printStackTrace();
                        }
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        btnEnableSearch.setText(R.string.start_search);
                        pbProgress.setVisibility(View.GONE);
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device != null) {
                            bluetoothConnector.add(device);
                            listAdapter.notifyDataSetChanged();
                        }
                        if(preference.isLastConnectDevice()){
                            assert device != null;
                            if(device.getAddress().equals(sharedPreferences.getString(
                                    KEY_MAC_ADDRESS, ""))){
                                btnEnableSearch.setText(R.string.start_search);
                                pbProgress.setVisibility(View.GONE);
                                try {
                                    setListAdapter(BT_SEARCH);
                                    connectToExisting(device);
                                } catch (BluetoothException e) {
                                    e.printStackTrace();
                                    viewWarning(e.getMessage());
                                }
                            }
                        }
                        break;
                }
            }
        }
    };

    private void viewWarning(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,
                        message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isDevice(String device){
        String nameDevice = "HC-05";
        return device.equals(nameDevice);
    }

    private void readStream(){
        StringBuilder buffer = new StringBuilder();
        final StringBuilder sbConsole = new StringBuilder();
        try {
            while (bluetoothConnector.isConnected()) {
                int bytes = bluetoothConnector.read();
                buffer.append((char) bytes);
                int eof = buffer.indexOf("\r\n");

                if (eof > 0) {
                    sbConsole.append(buffer.toString());
                    lastSensorValues = buffer.toString();
                    buffer.delete(0, buffer.length());
                }
            }
            bluetoothConnector.disconnect();
        } catch (IOException | BluetoothException e) {
            e.printStackTrace();
        }
    }

    private void connectToExisting(BluetoothDevice device) throws BluetoothException {
        progressDialog.show();
        if(isDevice(device.getName()))
            bluetoothConnector.connect(device);
        else throw new ConnectionBluetoothException("Not connected to this device");;
        saveMacAddress(device.getAddress());
        progressDialog.dismiss();
//        bluetoothConnector = new BluetoothConnector();
        bluetoothConnector.start();
        showFrameLedControls();
//            runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                showFrameLedControls();
////                readStream();
//            }
//        });
        startTimer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        checkPermissionUtil.onRequestPermissionsResult(requestCode, grantResults);
    }

    public void saveMacAddress(String macAddressNow){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MAC_ADDRESS, macAddressNow);
        editor.apply();
    }

//    private class ConnectThread extends Thread {
//        private BluetoothSocket bluetoothSocket = null;
//        private boolean success = false;
//        private String nameThisDevice;
//        private String macAddressNow;
//        private String nameDevice;
//
//        public ConnectThread(BluetoothDevice device) {
//            try {
//                Method method = device.getClass().getMethod("createRfcommSocket", int.class);
//                bluetoothSocket = (BluetoothSocket) method.invoke(device, 1);
//                progressDialog.show();
//                nameDevice = "HC-05";
//                nameThisDevice = device.getName();
//                macAddressNow = device.getAddress();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void run() {
//            try {
//                bluetoothSocket.connect();
//                success = nameThisDevice.equals(nameDevice);
//                progressDialog.dismiss();
//            } catch (IOException e) {
//                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressDialog.dismiss();
//                        Toast.makeText(MainActivity.this,
//                                "Не могу соединиться!",Toast.LENGTH_SHORT).show();
//                    }
//                });
//                cancel();
//            }
//            if (success) {
//                saveMacAddress(macAddressNow);
//                connectedThread = new ConnectedThread(bluetoothSocket);
//                connectedThread.start();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        showFrameLedControls();
//                    }
//                });
//            }
//        }
//
//        public boolean isConnect() {
//            return bluetoothSocket.isConnected();
//        }
//
//        public void cancel() {
//            try {
//                Log.d(TAG, "cancel: " + this.getClass().getSimpleName());
//                bluetoothSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    private class ConnectedThread  extends  Thread {
//        private final InputStream inputStream;
//        private final OutputStream outputStream;
//        private boolean isConnected;
//
//        public ConnectedThread(BluetoothSocket bluetoothSocket) {
//            InputStream inputStream = null;
//            OutputStream outputStream = null;
//
//            try {
//                inputStream = bluetoothSocket.getInputStream();
//                outputStream = bluetoothSocket.getOutputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            this.inputStream = inputStream;
//            this.outputStream = outputStream;
//            isConnected = true;
//        }
//
//        @Override
//        public void run() {
//            BufferedInputStream bis = new BufferedInputStream(inputStream);
//            StringBuilder buffer = new StringBuilder();
//            final StringBuilder sbConsole = new StringBuilder();
//
//            while (isConnected) {
//                try {
//                    int bytes = bis.read();
//                    buffer.append((char) bytes);
//                    int eof = buffer.indexOf("\r\n");
//
//                    if (eof > 0) {
//                        sbConsole.append(buffer.toString());
//                        lastSensorValues = buffer.toString();
//                        buffer.delete(0, buffer.length());
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            try {
//                bis.close();
//                cancel();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void write(String command) {
//            byte[] bytes = command.getBytes();
//            if (outputStream != null) {
//                try {
//                    outputStream.write(bytes);
//                    outputStream.flush();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public void cancel() {
//            try {
//                isConnected = false;
//                inputStream.close();
//                outputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void enableCheckBox(int led, boolean state) throws BluetoothException, IOException {
//        if (connectedThread != null && connectThread.isConnect()) {
        if (bluetoothConnector.isConnected()) {
            String command = "";
            switch (led) {
                case BUZZER:
                    command = (state) ? "buzzer on#" : "buzzer off#";
                    break;
                case LED:
                    command = (state) ? "led on#" : "led off#";
                    break;
            }
            bluetoothConnector.write(command.getBytes());
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

//    private String readStream(){
//
//    }

    private void startTimer() {
        cancelTimer();
        handler = new Handler();
        final MovementMethod movementMethod = new ScrollingMovementMethod();
        handler.postDelayed(timer = new Runnable() {
            @Override
            public void run() {
//                if(bluetoothConnector.getLastSensorValues() != null){
//                    lastSensorValues = bluetoothConnector.getLastSensorValues();
//                }
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
