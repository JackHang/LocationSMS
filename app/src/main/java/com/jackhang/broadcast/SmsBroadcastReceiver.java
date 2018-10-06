package com.jackhang.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.jackhang.Utils.SPUtils;
import com.jackhang.constant.KeyValue;
import com.jackhang.locationsms.R;

/**
 * @author JackHang
 * @date 2018/9/26.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver
{
	private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	public AMapLocationClient mLocationClient = null;
	public AMapLocationClientOption mLocationOption = null;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		//判断广播消息
		if (action != null && action.equals(SMS_RECEIVED_ACTION))
		{
			Bundle bundle = intent.getExtras();
			//如果不为空
			if (bundle != null)
			{
				//将pdus里面的内容转化成Object[]数组
				// pdus ：protocol data unit  ：
				Object pdusData[] = (Object[]) bundle.get("pdus");
				//解析短信
				SmsMessage[] msg = new SmsMessage[0];
				if (pdusData != null)
				{
					msg = new SmsMessage[pdusData.length];
				}
				for (int i = 0; i < msg.length; i++)
				{
					byte pdus[] = (byte[]) pdusData[i];
					msg[i] = SmsMessage.createFromPdu(pdus);
				}
				//获取短信内容
				StringBuilder content = new StringBuilder();
				//获取地址
				StringBuilder phoneNumber = new StringBuilder();
				//分析短信具体参数
				for (SmsMessage temp : msg)
				{
					content.append(temp.getMessageBody());
					phoneNumber.append(temp.getOriginatingAddress());
				}
				System.out.println("发送者号码：" + phoneNumber.toString() + "  短信内容：" + content.toString());
				if (checkContent(content.toString()))
				{
					initLocation(context, phoneNumber.toString());
				}
			}
		}
	}

	private void initLocation(Context context, String phoneNumber)
	{
		mLocationClient = new AMapLocationClient(context.getApplicationContext());
		mLocationClient.setLocationListener(new AMapLocationListener()
		{
			@Override
			public void onLocationChanged(AMapLocation aMapLocation)
			{
				sendSMS(context, phoneNumber, aMapLocation.getLongitude(), aMapLocation.getLatitude());
			}
		});

		mLocationOption = new AMapLocationClientOption();
		//设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

		//设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
		mLocationOption.setInterval(1000);

		//单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
		mLocationOption.setHttpTimeOut(50000);

		//关闭缓存机制
		mLocationOption.setLocationCacheEnable(false);

		//获取最近3s内精度最高的一次定位结果：
		mLocationOption.setOnceLocationLatest(true);

		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		//启动定位
		mLocationClient.startLocation();
	}

	private void sendSMS(Context context, String phoneNumber, double longitude, double latitude)
	{
		// 获取短信管理器
		android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
		String message;
		switch (SPUtils.getInstance().getInt(KeyValue.MAP_STYLE))
		{
			case KeyValue.BMAP:
				message = context.getString(R.string.location_baidu_Url, longitude, latitude);
				break;
			case KeyValue.TMAP:
				message = context.getString(R.string.location_baidu_Url, longitude, latitude);
				break;
			case KeyValue.AMAP:
			default:
				message = context.getString(R.string.location_Amap_Url, longitude, latitude);
				break;
		}
		// 发送短信内容（手机短信长度限制）
		smsManager.sendTextMessage(phoneNumber, null, message, null, null);
	}

	private boolean checkContent(String content)
	{
		return content.equals("你在哪里？");
	}
}
