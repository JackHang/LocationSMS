package com.jackhang.locationsms;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * @author JackHang
 * @date 2018/10/6.
 */
public class SettingsActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		initToolBar();
	}

	private void initToolBar()
	{
		Toolbar toolbar = findViewById(R.id.toolbar);

		toolbar.setNavigationIcon(R.drawable.ic_back);
		toolbar.setNavigationOnClickListener(v -> finish());
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
}
