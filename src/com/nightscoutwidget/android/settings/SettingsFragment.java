package com.nightscoutwidget.android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;

import com.nightscoutwidget.android.R;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {
	int mAppWidgetId = 0;
	SettingsActivity sact = null;
	Context context;

	public SettingsFragment(int mAppWidgetId, SettingsActivity sact) {
		this.mAppWidgetId = mAppWidgetId;
		this.sact = sact;
		this.context = sact;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* set preferences */
		addPreferencesFromResource(R.xml.preferences);
		addMedtronicOptionsListener();
		PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
		// iterate through all preferences and update to saved value
		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			initSummary(getPreferenceScreen().getPreference(i));
		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		updatePrefSummary(findPreference(key));
	}

	@Override
	public void onResume() {

		super.onResume();
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {

		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		// sact.finishSettings();

		super.onDestroy();
	}

	// iterate through all preferences and update to saved value
	private void initSummary(Preference p) {

		if (p instanceof PreferenceCategory) {
			PreferenceCategory pCat = (PreferenceCategory) p;
			for (int i = 0; i < pCat.getPreferenceCount(); i++) {
				initSummary(pCat.getPreference(i));
			}

		} else if (p instanceof PreferenceScreen) {
			PreferenceScreen pSc = (PreferenceScreen) p;
			for (int i = 0; i < pSc.getPreferenceCount(); i++) {
				initSummary(pSc.getPreference(i));
			}

		} else {

			updatePrefSummary(p);
		}
	}

	// update preference summary
	private void updatePrefSummary(Preference p) {

		if (p instanceof ListPreference) {
			ListPreference listPref = (ListPreference) p;
			p.setSummary(listPref.getEntry());
		}
		if (p instanceof EditTextPreference) {
			EditTextPreference editTextPref = (EditTextPreference) p;
			p.setSummary(editTextPref.getText());
		}
		if (p instanceof MultiSelectListPreference) {
			EditTextPreference editTextPref = (EditTextPreference) p;
			p.setSummary(editTextPref.getText());
		}
		if (p instanceof RingtonePreference) {

			RingtonePreference rtPref = (RingtonePreference) p;
			p.setSummary(rtPref.getTitle());
		}

	}

	private void addMedtronicOptionsListener() {
		final ListPreference mon_type = (ListPreference) findPreference("monitor_type");
		final EditTextPreference med_id = (EditTextPreference) findPreference("medtronic_cgm_id");
		final ListPreference res_units = (ListPreference) findPreference("reservoir_ins_units");
		int index = mon_type.findIndexOfValue(mon_type.getValue());

		if (index == 1) {
			med_id.setEnabled(true);
		} else {
			med_id.setEnabled(false);
		}
		res_units.setEnabled(med_id.isEnabled());
		mon_type.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				final String val = newValue.toString();
				int index = mon_type.findIndexOfValue(val);
				if (index == 1)
					med_id.setEnabled(true);
				else
					med_id.setEnabled(false);
				res_units.setEnabled(med_id.isEnabled());
				return true;
			}
		});
	}
}