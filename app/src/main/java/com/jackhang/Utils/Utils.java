package com.jackhang.Utils;

import android.app.Application;

/**
 * @author JackHang
 * @date 2018/9/27.
 */
public class Utils
{
	private static Application mApp;

	public static void init(Application app)
	{
		if(mApp == null)
		{
			mApp = app;
		}
	}

	public static Application getApp()
	{
		return mApp;
	}
}
