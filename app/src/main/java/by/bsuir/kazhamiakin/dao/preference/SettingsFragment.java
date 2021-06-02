package by.bsuir.kazhamiakin.dao.preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import by.bsuir.kazhamiakin.R;

/**
 * @author Pablo on 20.12.2020
 * @project Health
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_settings);
    }
}
