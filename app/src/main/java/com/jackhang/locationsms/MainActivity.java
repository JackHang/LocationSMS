package com.jackhang.locationsms;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.jackhang.Utils.GetNumber;
import com.jackhang.Utils.PermissionHelper;
import com.jackhang.Utils.SPUtils;
import com.jackhang.constant.KeyValue;

/**
 * @author JackHang
 * @date 2018/9/22.
 */
public class MainActivity extends AppCompatActivity implements AMapLocationListener, PermissionHelper.PermissionCallback
{
	public static final String TAG = MainActivity.class.getSimpleName();
	public AMapLocationClient mLocationClient = null;
	public AMapLocationClientOption mLocationOption = null;
	private PermissionHelper mPermissionHelper;
	private String selectedContacts;
	private boolean sendSOSMessage = false;
	private boolean sendMessage = false;
	private double lat, lon;
	private String[] permissions = new String[]{
			Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS,
			Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.READ_CONTACTS
	};
//	private String[] permissionsLocation = new String[]{
//			Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
//	};
//	private String[] permissionsContacts = new String[]{
//			Manifest.permission.READ_CONTACTS
//	};
//	private String[] permissionsSMS = new String[]{
//			Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initToolBar();

		initLoaction();

		TextView locationLonLat = findViewById(R.id.tv_location);
		locationLonLat.setText(getString(R.string.locationLonLat, lon, lat));
		TextView locationAddress = findViewById(R.id.tv_address);
		locationAddress.setText(getString(R.string.locationAddress, ""));

		findViewById(R.id.button_sos).setOnClickListener(v ->
		{
			if (lat == 0 || lon == 0)
			{
				Toast.makeText(this, "定位中，请稍后", Toast.LENGTH_SHORT).show();
				sendSOSMessage = true;
				return;
			}
			sendSOSMessage();

		});

		permissionCheck();
	}

	private void initToolBar()
	{
		Toolbar toolbar = findViewById(R.id.toolbar);

		toolbar.inflateMenu(R.menu.base_toolbar_menu);//设置右上角的填充菜单
		toolbar.setOnMenuItemClickListener(item -> {
			int menuItemId = item.getItemId();
			if (menuItemId == R.id.action_about)
			{
//				Toast.makeText(MainActivity.this , R.string.menuAbout , Toast.LENGTH_SHORT).show();
				startActivity(new Intent(this, SettingsActivity.class));
			}
			return true;
		});
	}

	private void sendSOSMessage()
	{
		if (mPermissionHelper.checkSelfPermission(new String[]{Manifest.permission.READ_CONTACTS}))
		{
			String ContactPhone = SPUtils.getInstance().getString(KeyValue.CONTACT_PHONE, "");
			if (TextUtils.isEmpty(ContactPhone))
			{
				Toast.makeText(this, R.string.noContactsHint, Toast.LENGTH_SHORT).show();
				choiceContacts();
				return;
			}
			// 获取短信管理器
			android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
			String message;
			switch (SPUtils.getInstance().getInt(KeyValue.MAP_STYLE))
			{
				case KeyValue.BMAP:
					message = getString(R.string.location_baidu_Url, lon, lat);
					break;
				case KeyValue.TMAP:
					message = getString(R.string.location_baidu_Url, lon, lat);
					break;
				case KeyValue.AMAP:
				default:
					message = getString(R.string.location_Amap_Url, lon, lat);
					break;
			}
			// 发送短信内容（手机短信长度限制）
			if (!sendMessage)
			{
				sendMessage = true;
				smsManager.sendTextMessage(ContactPhone, null, message, null, null);
			}
			else
			{
				Toast.makeText(this, R.string.messageSendHint, Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			mPermissionHelper.request(this);
		}
	}

	private void choiceContacts()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.contactsChoseTitle);
		final ListView localListView = new ListView(this);
		localListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		localListView.setAdapter(new ArrayAdapter<>(this, R.layout.dialog_singlechoise, GetNumber.nameList.toArray(new String[GetNumber.nameList.size()])));
		builder.setView(localListView);
		builder.setPositiveButton(R.string.buttonPositive, (dialog, which) ->
		{
			SparseBooleanArray localSparseBooleanArray = localListView.getCheckedItemPositions();
			for (int i = 0; i < GetNumber.list.size(); i++)
			{
				if (localSparseBooleanArray.get(i))
				{
					selectedContacts = GetNumber.list.get(i).getPhoneNumber();
					SPUtils.getInstance().put(KeyValue.CONTACT_PHONE, selectedContacts);
					break;
				}
			}
			dialog.dismiss();
		});
		builder.create().show();
	}

	private void permissionCheck()
	{
		int SMS = 101;
		mPermissionHelper = new PermissionHelper(this, permissions, SMS);
		mPermissionHelper.request(this);
	}

	private void initLoaction()
	{
		mLocationClient = new AMapLocationClient(getApplicationContext());
		mLocationClient.setLocationListener(this);

		mLocationOption = new AMapLocationClientOption();
		//设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

		//设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
		mLocationOption.setInterval(1000);

		//单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
		mLocationOption.setHttpTimeOut(20000);

		//关闭缓存机制
		mLocationOption.setLocationCacheEnable(true);

		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		//启动定位
		mLocationClient.startLocation();
	}


	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mLocationClient.stopLocation();
		mLocationClient.onDestroy();
	}

	@Override
	public void onLocationChanged(AMapLocation aMapLocation)
	{
		lat = aMapLocation.getLatitude();
		lon = aMapLocation.getLongitude();
		TextView locationLonLat = findViewById(R.id.tv_location);
		locationLonLat.setText(getString(R.string.locationLonLat, lon, lat));
		TextView locationAddress = findViewById(R.id.tv_address);
		locationAddress.setText(getString(R.string.locationAddress, aMapLocation.getAddress()));
		sendMessage = false;
		if (sendSOSMessage)
		{
			sendSOSMessage = false;
			sendSOSMessage();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (mPermissionHelper != null)
		{
			mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	@Override
	public void onPermissionGranted()
	{
		Log.d(TAG, "onPermissionGranted() called");
		GetNumber.getNumber(MainActivity.this);
	}

	@Override
	public void onIndividualPermissionGranted(String[] grantedPermission)
	{
		Log.d(TAG, "onIndividualPermissionGranted() called with: grantedPermission = [" + TextUtils.join(",", grantedPermission) + "]");
	}

	@Override
	public void onPermissionDenied()
	{
		Log.d(TAG, "onPermissionDenied() called");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.permissionDenyTitle);
		builder.setMessage(R.string.privacyPolicy);
		builder.setPositiveButton(R.string.buttonPositive, (dialog, witch) ->
				mPermissionHelper.request(this));
		builder.setNegativeButton(R.string.buttonNegative, (dialog, witch) -> dialog.dismiss());
	}

	@Override
	public void onPermissionDeniedBySystem()
	{
		Log.d(TAG, "onPermissionDeniedBySystem() called");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.permissionDenyTitle);
		builder.setMessage(R.string.privacyPolicy);
		builder.setPositiveButton(R.string.buttonPositive, (dialog, witch) ->
				mPermissionHelper.openAppDetailsActivity());
		builder.setNegativeButton(R.string.buttonNegative, (dialog, witch) -> dialog.dismiss());
	}
}
