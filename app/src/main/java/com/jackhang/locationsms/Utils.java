package com.jackhang.locationsms;

import android.app.Activity;
import android.app.Application;

/**
 * @author JackHang
 * @date 2018/9/27.
 */
public class Utils
{
	private static Application mApp;

	public static void init(Activity mActivity)
	{
		if(mApp == null)
		{
			mApp = mActivity.getApplication();
		}
	}

	public static Application getApp()
	{
		return mApp;
	}
}
