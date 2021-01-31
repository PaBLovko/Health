package by.bsuir.health.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import by.bsuir.health.ViewActivity;
import by.bsuir.health.util.CheckPermissionUtil;
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

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQ_ENABLE_BT = 10;
    public static final int BT_BOUNDED = 21;
    public static final int BT_SEARCH = 22;

    public static final int BUZZER = 30;
    public static final int LED = 31;

    private String storageDirectory;

    private ArrayList<SdFile> sdFiles;

    private ListAdapter listAdapter;
    private ListFile listFile;

    private String lastSensorValues = "";
    private Handler handler;
    private Runnable timer;
    private int xTempLastValue = 0;
    private int xRandLastValue = 0;

    private PrefModel preference;

    private CheckPermissionUtil checkPermissionUtil;
    private String[] permissions;

    private BluetoothConnector bluetoothConnector;
    private BluetoothConnector.ConnectedThread connectedThread;

    private ViewActivity viewActivity;

//    private ControllerActivity controllerActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        controllerActivity = new ControllerActivity(this);
        viewActivity = new ViewActivity(this);
        preference = new PrefModel(this);

        viewActivity.setGvGraph(preference.getPointsCount());

        viewActivity.setProgressDialog(this);

        setListeners();

        sdFiles = new ArrayList<>();

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
                viewActivity.showFrameControls();
                viewActivity.setSwitchEnableBtChecked(true);
                setListAdapter(BT_BOUNDED);
            }
        } catch (BluetoothException e) {
            e.printStackTrace();
        }

        PrefModel.addDelayListener(new OnDelayChangedListener() {
            @Override
            public void OnDelayChanged() throws BluetoothException, IOException {
                if (connectedThread.isConnected()) {
                    connectedThread.write((preference.getDelayTimer() + "Delay#").getBytes());
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

        if (cardAvailable()) storageDirectory =
                Environment.getExternalStorageDirectory().toString() + "/" + DIR_SD;

//        imageview = (ImageView) findViewById();
    }

    void setListeners(){
        viewActivity.getSwitchEnableBt().setOnCheckedChangeListener(this);
        viewActivity.getBtnEnableSearch().setOnClickListener(this);
        viewActivity.getListDevices().setOnItemClickListener(this);
        viewActivity.getBtnStorage().setOnClickListener(this);
        viewActivity.getListImages().setOnItemClickListener(this);
        viewActivity.getBtnStart().setOnClickListener(this);
        viewActivity.getBtnDisconnect().setOnClickListener(this);
        viewActivity.getSwitchLed().setOnCheckedChangeListener(this);
        viewActivity.getSwitchBuzzer().setOnCheckedChangeListener(this);
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
                    //System.out.println("finish");
                }

                @Override
                public void repeatPermissions() {
                    checkPermissionUtil.checkPermissions(
                            MainActivity.this, permissions, permissionsResult);
                }
            };

//    ViewActivity.IViewActivityResult iViewActivityResult =
//            new ViewActivity.IViewActivityResult() {
//                @Override
//                public void onClick(View v) {
//                    onClickController(v);
//                }
//
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    onItemClickController(parent, view, position, id);
//                }
//
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    onCheckedChangedController(buttonView, isChecked);
//                }
//            };


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
        for (File file : files) sdFiles.add(new SdFile(
                BitmapFactory.decodeFile(file.getAbsolutePath()),
                file.getName(),
                file.lastModified()));
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
        } catch (BluetoothException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
//    public void onClickController(View v) {
        if (v.equals(viewActivity.getBtnEnableSearch())) {
            try {
                BluetoothConnector.enableSearch();
            } catch (BluetoothException e) {
                e.printStackTrace();
            }
            try {
                BluetoothConnector.enableSearch();
            } catch (BluetoothException e) {
                e.printStackTrace();
            }
        } else if (v.equals(viewActivity.getBtnDisconnect())) {
            try {
                disconnection();
            } catch (BluetoothException | IOException e) {
                e.printStackTrace();
            }
            viewActivity.showFrameControls();
        } else if (v.equals(viewActivity.getBtnStart())){
//
        } else if (v.equals(viewActivity.getBtnStorage())){
            try {
                disconnection();
            } catch (BluetoothException | IOException e) {
                e.printStackTrace();
            }
            setListFile();
            viewActivity.showFrameStorage();
        }
    }

    private void setListFile() {
        sdFiles = updateSdList(getFiles(storageDirectory));
        listFile = new ListFile(this, sdFiles);
        viewActivity.setListImages(listFile);

    }

    private void disconnection() throws BluetoothException, IOException {
        cancelTimer();
        if (bluetoothConnector.isConnected()) bluetoothConnector.disconnect();
        if (connectedThread.isConnected()) connectedThread.disconnect();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//    public void onItemClickController(AdapterView<?> parent, View view, int position, long id) {
        if (parent.equals(viewActivity.getListDevices())) {
            BluetoothDevice device = bluetoothConnector.getBluetoothDevices().get(position);
            if (device != null) {
                viewActivity.setBtnEnableSearchStart();
                viewActivity.setPbProgressNoVisibility();
                try {
                    connectToExisting(device);
                } catch (BluetoothException e) {
                    e.printStackTrace();
                    viewWarning(e.getMessage());
                }
            }
        }
        if (parent.equals(viewActivity.getListImages())){
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
//    public void onCheckedChangedController(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(viewActivity.getSwitchEnableBt())) {
            enableBt(isChecked);
            if (!isChecked) viewActivity.showFrameMessage();
        } else if (buttonView.equals(viewActivity.getSwitchBuzzer())) {
            try {
                enableCheckBox(BUZZER, isChecked); // TODO включение или отключение динамика
            } catch (BluetoothException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (buttonView.equals(viewActivity.getSwitchLed())) {
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
                    viewActivity.showFrameControls();
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
        viewActivity.setListDevices(listAdapter);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        viewActivity.setBtnEnableSearchStop();
                        viewActivity.setPbProgressVisibility();
                        try {
                            setListAdapter(BT_SEARCH);
                        } catch (BluetoothException e) {
                            e.printStackTrace();
                        }
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        viewActivity.setBtnEnableSearchStart();
                        viewActivity.setPbProgressNoVisibility();
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device =
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device != null) {
                            bluetoothConnector.add(device);
                            listAdapter.notifyDataSetChanged();
                        }
                        if(preference.isLastConnectDevice()){
                            assert device != null;
                            if(device.getAddress().equals(preference.getSharedPreferences().getString(
                                    PrefModel.KEY_MAC_ADDRESS, ""))){
                                viewActivity.setBtnEnableSearchStart();
                                viewActivity.setPbProgressNoVisibility();
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
                viewActivity.getProgressDialog().dismiss();
                Toast.makeText(MainActivity.this,
                        message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isDevice(String device){
        String nameDevice = "HC-05";
        return device.equals(nameDevice);
    }

    private void connectToExisting(BluetoothDevice device) throws BluetoothException {
        viewActivity.getProgressDialog().show();
        if(isDevice(device.getName()))
            bluetoothConnector.connect(device);
        else throw new ConnectionBluetoothException("Not connected to this device");
        preference.saveMacAddress(device.getAddress());
        connectedThread = new BluetoothConnector.ConnectedThread(bluetoothConnector.getSocket());
        connectedThread.start();
        viewActivity.getProgressDialog().dismiss();
        viewActivity.showFrameLedControls();
        startTimer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        checkPermissionUtil.onRequestPermissionsResult(requestCode, grantResults);
    }

    private void enableCheckBox(int led, boolean state) throws BluetoothException, IOException {
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
            connectedThread.write(command.getBytes());
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
                if(connectedThread.getLastSensorValues() != null){
                    lastSensorValues = connectedThread.getLastSensorValues();
                }
//                etConsole.setText(lastSensorValues);
//                etConsole.setMovementMethod(movementMethod);
                viewActivity.setEtConsoleAndMovementMethod(lastSensorValues, movementMethod);
                Map dataSensor = parseData(lastSensorValues);
                if (dataSensor != null) {
                    if (dataSensor.containsKey("Temp") && dataSensor.containsKey("rand")) {
                        int temp = Integer.parseInt(dataSensor.get("Temp").toString());
                        int rand = Integer.parseInt(dataSensor.get("rand").toString().trim());

                        if(preference.getOperationMode().equals("pulse")){
                               viewActivity.getSeriesTemp().appendData(new DataPoint(xTempLastValue, temp), true, preference.getPointsCount());
//                            seriesTemp.appendData(new DataPoint(xTempLastValue, temp), true, preference.getPointsCount());
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
