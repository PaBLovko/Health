package by.bsuir.health.dao.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import by.bsuir.health.exeption.bluetooth.BluetoothException;

import static android.content.Context.MODE_PRIVATE;


public class PrefModel implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final List<OnDelayChangedListener> listeners = new ArrayList<>();

    public static final String KEY_DELAY_TIMER          = "key_delay_timer";
    public static final String KEY_POINTS_COUNT         = "key_points_count";
    public static final String KEY_OPERATION_MODE       = "key_operating_mode";
    public static final String KEY_LAST_CONNECT_DEVICE  = "key_last_connect_device";
    public static final String KEY_MAC_ADDRESS          = "key_mac_address";
    public static final String KEY_FILTER_MODE          = "key_filter_mode";

    private final SharedPreferences sharedPreferences;

    private int     delayTimer;
    private int     pointsCount;
    private String  operationMode;
    private boolean lastConnectDevice;
    private boolean filterMode;

    public PrefModel(Context context) {
        this.delayTimer                 = 60000;
        this.pointsCount                = 300;
        this.operationMode              = "";
        this.lastConnectDevice          = false;
        this.filterMode                 = false;
        this.sharedPreferences = context.getSharedPreferences(KEY_MAC_ADDRESS, MODE_PRIVATE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.registerOnSharedPreferenceChangeListener(this);
        uploadingDataToSettings(preferences);
        //полная очистка настроек
        //preferences.edit().clear().commit();
    }

    public int getDelayTimer() { return delayTimer; }

    public void setDelayTimer(int delayTimer) {
        this.delayTimer = delayTimer;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(int pointsCount) {
        this.pointsCount = pointsCount;
    }

    public String getOperationMode() { return operationMode; }

    public void setOperationMode(String operationMode) { this.operationMode = operationMode; }

    public boolean isLastConnectDevice() { return lastConnectDevice; }

    public void setLastConnectDevice(boolean lastConnectDevice) {
        this.lastConnectDevice = lastConnectDevice;
    }

    public boolean isFilterMode() {
        return filterMode;
    }

    public void setFilterMode(boolean filterMode) {
        this.filterMode = filterMode;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    private void uploadingDataToSettings(SharedPreferences preferences){
        onSharedPreferenceChanged(preferences, KEY_DELAY_TIMER);
        onSharedPreferenceChanged(preferences, KEY_POINTS_COUNT);
        onSharedPreferenceChanged(preferences, KEY_OPERATION_MODE);
        onSharedPreferenceChanged(preferences, KEY_LAST_CONNECT_DEVICE);
        onSharedPreferenceChanged(preferences, KEY_FILTER_MODE);
    }

    public void saveMacAddress(String macAddressNow){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MAC_ADDRESS, macAddressNow);
        editor.apply();
    }

    public static void addDelayListener(OnDelayChangedListener l) {
        listeners.add(l);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        String string;
        switch (key) {
            case KEY_DELAY_TIMER:
                string = preferences.getString(key, "60000");
                assert string != null;
                setDelayTimer(Integer.parseInt(string));
                for (OnDelayChangedListener l : listeners) {
                    try {
                        l.OnDelayChanged();
                    } catch (BluetoothException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case KEY_POINTS_COUNT:
                string = preferences.getString(key, "300");
                assert string != null;
                setPointsCount(Integer.parseInt(string));
                break;
            case KEY_OPERATION_MODE:
                setOperationMode(preferences.getString(key, ""));
                break;
            case KEY_LAST_CONNECT_DEVICE:
                setLastConnectDevice(preferences.getBoolean(key, false));
                break;
            case KEY_FILTER_MODE:
                setFilterMode(preferences.getBoolean(key, false));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + key);
        }
    }
}


