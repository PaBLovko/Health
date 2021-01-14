package by.bsuir.health.preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import by.bsuir.health.R;

/**
 * @author Pablo on 20.12.2020
 * @project Health
 */

public class PrefFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_settings);

    }

}
