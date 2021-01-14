package by.bsuir.health.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;


public class PrefModel implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final List<OnDelayChangedListener> listeners = new ArrayList<>();

    public static final String KEY_DELAY_TIMER          = "key_delay_timer";
    public static final String KEY_POINTS_COUNT         = "key_points_count";
    public static final String KEY_OPERATION_MODE       = "key_operating_mode";
    public static final String KEY_LAST_CONNECT_DEVICE  = "key_last_connect_device";

    private int     delayTimer;
    private int     pointsCount;
    private String  operationMode;
    private boolean lastConnectDevice;

    public PrefModel(Context context) {
        this.delayTimer                 = 10;
        this.pointsCount                = 300;
        this.operationMode              = "";
        this.lastConnectDevice          = false;
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

    private void uploadingDataToSettings(SharedPreferences preferences){
        onSharedPreferenceChanged(preferences, KEY_DELAY_TIMER);
        onSharedPreferenceChanged(preferences, KEY_POINTS_COUNT);
        onSharedPreferenceChanged(preferences, KEY_OPERATION_MODE);
        onSharedPreferenceChanged(preferences, KEY_LAST_CONNECT_DEVICE);
    }

    public static void addDelayListener(OnDelayChangedListener l) {
        listeners.add(l);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        String string;
        switch (key) {
            case KEY_DELAY_TIMER:
                string = preferences.getString(key, "10");
                assert string != null;
                setDelayTimer(Integer.parseInt(string));
                for (OnDelayChangedListener l : listeners) {
                    l.OnDelayChanged();
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
            default:
                throw new IllegalStateException("Unexpected value: " + key);
        }
    }
}


