package de.tastykatana.zeiterfassung.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.tastykatana.zeiterfassung.R;

/**
 * Created by matthias on 2/15/17.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}
