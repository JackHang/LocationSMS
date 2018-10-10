package com.jackhang.locationsms;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.jackhang.Utils.GetNumber;

/**
 * @author JackHang
 * @date 2018/10/6.
 */
public class SettingsFragment extends PreferenceFragment
{
	ListPreference lp;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getPreferenceManager().setSharedPreferencesName("spUtils");
		addPreferencesFromResource(R.xml.pref_general);

		lp = (ListPreference) findPreference("ContactPhone");

		lp.setEntries(GetNumber.nameList.toArray(new String[GetNumber.nameList.size()]));
		lp.setEntryValues(GetNumber.PhoneList.toArray(new String[GetNumber.PhoneList.size()]));
	}
}
