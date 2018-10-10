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

import java.util.ArrayList;

/**
 * @author JackHang
 * @date 2018/9/26.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver
{
	private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	public AMapLocationClient mLocationClient = null;
	public AMapLocationClientOption mLocationOption = null;
	private boolean sendSMS = false;

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
				if (SPUtils.getInstance().getString(KeyValue.CONTACT_PHONE).equals(phoneNumber.toString()))
				{
					if (checkContent(content.toString()))
					{
						sendSMS = true;
						initLocation(context, phoneNumber.toString());
					}
				}
				else
				{
					if (checkContent(content.toString()))
					{
						sendSMS = true;
						initLocation(context, phoneNumber.toString());
					}
				}
			}
		}
	}

	private void initLocation(Context context, String phoneNumber)
	{
		if (mLocationClient == null)
		{
			mLocationClient = new AMapLocationClient(context.getApplicationContext());
			mLocationClient.setLocationListener(aMapLocation -> {
				if (sendSMS)
				{
					sendSMS = false;
					sendSMS(context, phoneNumber, aMapLocation.getLongitude(), aMapLocation.getLatitude());
					mLocationClient.stopLocation();
					mLocationClient.onDestroy();
				}
			});

			mLocationOption = new AMapLocationClientOption();
			//设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
			mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

			//设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
			mLocationOption.setInterval(5000);

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
	}

	private void sendSMS(Context context, String phoneNumber, double longitude, double latitude)
	{
		// 获取短信管理器
		android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
		StringBuilder message = new StringBuilder();
		switch (SPUtils.getInstance().getInt(KeyValue.MAP_STYLE))
		{
			case KeyValue.BMAP:
				message.append(context.getString(R.string.location_baidu_Url, longitude, latitude));
				break;
			case KeyValue.TMAP:
				message.append(context.getString(R.string.location_baidu_Url, longitude, latitude));
				break;
			case KeyValue.AMAP:
			default:
				message.append(context.getString(R.string.location_Amap_Url, longitude, latitude));
				break;
		}
		message.append(" - 定位求助短信");
		// 发送短信内容（手机短信长度限制）
		ArrayList<String> divideContents = smsManager.divideMessage(message.toString());
		smsManager.sendMultipartTextMessage(phoneNumber, null, divideContents, null, null);
	}

	private boolean checkContent(String content)
	{
		String askMessage = "你在哪里？" + SPUtils.getInstance().getString(KeyValue.HELP_CODE);
		return content.equals("你在哪里？") || content.equals(askMessage);
	}
}
