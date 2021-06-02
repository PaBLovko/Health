package by.bsuir.kazhamiakin.dao.preference;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import by.bsuir.kazhamiakin.R;

/**
 * @author Pablo on 20.12.2020
 * @project Health
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_settings, new SettingsFragment())
                .commit();
    }
}
