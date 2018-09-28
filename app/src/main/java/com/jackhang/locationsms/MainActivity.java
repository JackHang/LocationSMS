package com.jackhang.locationsms;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.jackhang.Utils.GetNumber;
import com.jackhang.Utils.PermissionHelper;
import com.jackhang.Utils.SPUtils;
import com.jackhang.Utils.Utils;
import com.jackhang.constant.KeyValue;

/**
 * @author JackHang
 * @date 2018/9/22.
 */
public class MainActivity extends AppCompatActivity implements AMapLocationListener
{
	public static final String TAG = MainActivity.class.getSimpleName();
	public AMapLocationClient mLocationClient = null;
	public AMapLocationClientOption mLocationOption = null;
	private double lat, lng;
	private TextView view;
	private PermissionHelper mPermissionHelper;
	private String selectedContacts;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Utils.init(this);


		view = findViewById(R.id.tv_location);

		initLoaction();

		GetNumber.getNumber(this);

		findViewById(R.id.btn_baidu).setOnClickListener(v ->
		{
			Uri uri = Uri.parse(getString(R.string.location_baidu_Url, lng, lat));
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		});
		findViewById(R.id.btn_click_amap).setOnClickListener(v ->
		{
			Uri uri = Uri.parse(getString(R.string.location_Amap_Url, lng, lat));
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		});
		findViewById(R.id.btn_click_tencent).setOnClickListener((View v) ->
		{
//			Uri uri = Uri.parse(getString(R.string.location_tencent_Url, lng, lat));
//			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//			startActivity(intent);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.permissionDenyTitle);
			final ListView localListView = new ListView(this);
			localListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			localListView.setAdapter(new ArrayAdapter<>(this, R.layout.dialog_singlechoise, GetNumber.nameList.toArray(new String[GetNumber.nameList.size()])));
			builder.setView(localListView);
			builder.setPositiveButton("Done", (dialog, which) ->
			{
				SparseBooleanArray localSparseBooleanArray = localListView.getCheckedItemPositions();
				for (int i = 0; i < GetNumber.list.size(); i++)
				{
					if(localSparseBooleanArray.get(i))
					{
						selectedContacts = GetNumber.list.get(i).getPhoneNumber();
//						SPUtils.getInstance().put(KeyValue.CONTACT_PHONE, GetNumber.list.get(i).getPhoneNumber());
//						SPUtils.getInstance().put(KeyValue.CONTACT_NAME, GetNumber.list.get(i).getPhoneName());
						break;
					}
				}
				SPUtils.getInstance().put(KeyValue.CONTACT_PHONE, selectedContacts);
				dialog.dismiss();
			});
			builder.create().show();
		});

//		myLocationPermissionRequest();
//		mySMSPermissionRequest();
		permissionCheck();
	}

	private void permissionCheck()
	{
		int SMS = 101;
		mPermissionHelper = new PermissionHelper(this, new String[]{
				Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS,
				Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
				Manifest.permission.READ_CONTACTS}, SMS);
		mPermissionHelper.request(new PermissionHelper.PermissionCallback()
		{
			@Override
			public void onPermissionGranted() {
				Log.d(TAG, "onPermissionGranted() called");
			}

			@Override
			public void onIndividualPermissionGranted(String[] grantedPermission) {
				Log.d(TAG, "onIndividualPermissionGranted() called with: grantedPermission = [" + TextUtils.join(",",grantedPermission) + "]");
			}

			@Override
			public void onPermissionDenied() {
				Log.d(TAG, "onPermissionDenied() called");
			}

			@Override
			public void onPermissionDeniedBySystem() {
				Log.d(TAG, "onPermissionDeniedBySystem() called");
			}
		});
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
		mLocationOption.setLocationCacheEnable(false);

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
//		locationUrl = getString(R.string.location_baidu_Url,aMapLocation.getLongitude(),aMapLocation.getLatitude());
		lng = aMapLocation.getLongitude();
		lat = aMapLocation.getLatitude();
		view.setText(getString(R.string.location_tencent_Url, lng, lat));
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode,permissions,grantResults);
		if (mPermissionHelper != null) {
			mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
