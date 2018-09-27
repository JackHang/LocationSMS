package com.jackhang.locationsms;

/**
 * @author JackHang
 * @date 2018/9/27.
 */
public class PhoneInfo
{
	private String phoneName,phoneNumber;

	PhoneInfo(String phoneName, String phoneNumber) {
		// TODO Auto-generated constructor stub
		setPhoneName(phoneName);
		setPhoneNumber(phoneNumber);
	}

	public String getPhoneName() {
		return phoneName;
	}

	public void setPhoneName(String phoneName) {
		this.phoneName = phoneName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
