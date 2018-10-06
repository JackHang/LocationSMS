package com.jackhang.locationsms;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

/**
 * @author JackHang
 * @date 2018/10/6.
 */
public class SettingsFragment extends PreferenceFragment
{
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.pref_general);
	}
}
