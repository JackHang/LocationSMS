package com.jackhang.locationsms;

import android.app.Application;

import com.jackhang.Utils.SPUtils;
import com.jackhang.Utils.Utils;
import com.jackhang.constant.KeyValue;

/**
 * @author JackHang
 * @date 2018/9/28.
 */
public class MyApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		Utils.init(this);
		if (!SPUtils.getInstance().getBoolean(KeyValue.APP_INIT, false))
		{
			SPUtils.getInstance().put(KeyValue.APP_INIT, true);
			SPUtils.getInstance().put(KeyValue.MAP_STYLE, KeyValue.AMAP);
			SPUtils.getInstance().put(KeyValue.HELP_CODE, KeyValue.HELP_CODE_DEFAULT);
		}
	}
}
