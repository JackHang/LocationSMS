package com.jackhang.Utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.jackhang.bean.PhoneInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JackHang
 * @date 2018/9/27.
 */
public class GetNumber
{
	public static List<PhoneInfo> list = new ArrayList<>();
	public static List<String> nameList = new ArrayList<>();

	public static void getNumber(Context context) {
		Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		String phoneName,phoneNumber;

		if (cursor != null)
		{
			while (cursor.moveToNext()) {
				phoneName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

				PhoneInfo phoneInfo = new PhoneInfo(phoneName, phoneNumber);
				list.add(phoneInfo);
				nameList.add(phoneName);
				System.out.println(phoneName+"  "+phoneNumber);
			}
		}
		if (cursor != null)
		{
			cursor.close();
		}
	}
}
