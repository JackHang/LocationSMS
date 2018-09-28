package com.jackhang.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * @author JackHang
 * @date 2018/9/26.
 */
public  class SmsBroadcastReceiver extends BroadcastReceiver
{
	private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		//判断广播消息
		if (action != null && action.equals(SMS_RECEIVED_ACTION)){
			Bundle bundle = intent.getExtras();
			//如果不为空
			if (bundle!=null){
				//将pdus里面的内容转化成Object[]数组
				// pdus ：protocol data unit  ：
				Object pdusData[] = (Object[]) bundle.get("pdus");
				//解析短信
				SmsMessage[] msg = new SmsMessage[0];
				if (pdusData != null)
				{
					msg = new SmsMessage[pdusData.length];
				}
				for (int i = 0;i < msg.length;i++){
					byte pdus[] = (byte[]) pdusData[i];
					msg[i] = SmsMessage.createFromPdu(pdus);
				}
				//获取短信内容
				StringBuilder content = new StringBuilder();
				//获取地址
				StringBuilder phoneNumber = new StringBuilder();
				//分析短信具体参数
				for (SmsMessage temp : msg){
					content.append(temp.getMessageBody());
					phoneNumber.append(temp.getOriginatingAddress());
				}
				System.out.println("发送者号码："+phoneNumber.toString()+"  短信内容："+content.toString());
				if(checkContent(content.toString()))
				{
					sendSMS(phoneNumber.toString());
				}
				//可用于发命令执行相应的操作
               /* if ("#*location*#".equals(content.toString().trim())){
                    abortBroadcast();//截断短信广播
                }else if ("#*alarm*#".equals(content.toString().trim())){
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.guoge);
                    //播放音乐
                    mediaPlayer.start();
                    abortBroadcast();//截断短信广播
                }else if ("#*wipe*#".equals(content.toString().trim())){
                    abortBroadcast();//截断短信广播
                }else if ("#*lockscreen*#".equals(content.toString().trim())){
                    abortBroadcast();//截断短信广播
                }*/
			}
		}
	}

	private void sendSMS(String phoneNumber)
	{
//
		// 获取短信管理器
		android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
		// 发送短信内容（手机短信长度限制）
		smsManager.sendTextMessage(phoneNumber, null, "我现在在家", null, null);
	}

	private boolean checkContent(String content)
	{
		return content.equals("where r u");
	}
}
