package com.jackhang.locationsms;

/**
 * @author JackHang
 * @date 2018/9/22.
 */
public class Gps
{
	private double mLatitude;
	private double mLongitude;

	public Gps() {
	}

	public Gps(double longitude, double mLatitude) {
		setLatitude(mLatitude);
		setLongitude(longitude);
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double latitude) {
		this.mLatitude = latitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double longitude) {
		this.mLongitude = longitude;
	}

	@Override
	public String toString() {
		return mLongitude + "," + mLatitude;
	}
}
