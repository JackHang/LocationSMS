package com.jackhang.locationsms;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author JackHang
 * @date 2018/9/22.
 */
public class MainActivity extends AppCompatActivity
{
	private TextView view;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		view = findViewById(R.id.tv_location);

		if(RxLocationTool.isGpsEnabled(this))
		{
			boolean isregist = RxLocationTool.registerLocation(this, 1000, 10,
					new RxLocationTool.OnLocationChangeListener()
			{
				@Override
				public void getLastKnownLocation(Location location)
				{
//					view.setText("Last Location" + location.getProvider());
				}

				@Override
				public void onLocationChanged(Location location)
				{
					view.setText("Location Changed" + location.getLatitude());
				}

				@Override
				public void onStatusChanged(String provider, int status, Bundle extras)
				{
//					view.setText("change" + provider);
				}
			});
			Toast.makeText(this, "Register status" + isregist, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(this, "GPS off", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		RxLocationTool.unRegisterLocation();
	}
}
