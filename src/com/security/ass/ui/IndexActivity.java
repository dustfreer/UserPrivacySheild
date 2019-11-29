package com.security.ass.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.security.ass.R;
import com.security.ass.adintercept.AdInterceptActivity;
import com.security.ass.logrecord.LogRecordActivity;
import com.security.ass.phonelost.LostProtectedActivity;
import com.security.ass.phonelost.PhoneLostActivity;
import com.security.ass.privacy.ActivityMain;
import com.security.ass.privacy.PrivacySettingActivity;
import com.security.ass.privacylogin.AccountSettingActivity;
import com.security.ass.staticanalyse.StaticAnalyseActivity;
import com.security.ass.utils.SystemValue;

public class IndexActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index_activity);

		init();

	}

	/**
	 * 获取设备唯一值并存放到SystemValue中的静态变量中
	 */
	private void init() {
		// DEVICE_ID
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String DEVICE_ID = tm.getDeviceId();
		Log.e("indexActivity", "DEVICE_ID:" + DEVICE_ID);

		// MAC ADDRESS

		// Sim Serial Number
		String SimSerialNumber = tm.getSimSerialNumber();
		Log.e("indexActivity", "SimSerialNumber:" + SimSerialNumber);

		// ANDROID_ID
		String ANDROID_ID = Settings.System.getString(getContentResolver(),
				Settings.System.ANDROID_ID);
		Log.e("indexActivity", "ANDROID_ID:" + ANDROID_ID);

		// Serial Number
		String SerialNumber = android.os.Build.SERIAL;
		Log.e("indexActivity", "SerialNumber:" + SerialNumber);

		SystemValue.device_id = DEVICE_ID;
		SystemValue.sim_serial_number = SimSerialNumber;
		SystemValue.android_id = ANDROID_ID;
		SystemValue.serial_number = SerialNumber;
	}

	/**
	 * 隐私保护设置
	 * 
	 * @param view
	 */
	public void privacySetting(View view) {
		Intent intent = new Intent(IndexActivity.this,
				ActivityMain.class);
		startActivity(intent);
	}

	/**
	 * 账号管理
	 * 
	 * @param view
	 */
	public void accountSetting(View view) {
		Intent intent = new Intent(IndexActivity.this,
				AccountSettingActivity.class);
		startActivity(intent);
	}

	/**
	 * 静态检测
	 * 
	 * @param view
	 */
	public void staticAnalyse(View view) {
		Intent intent = new Intent(IndexActivity.this,
				StaticAnalyseActivity.class);
		startActivity(intent);
	}

	/**
	 * 广告拦截
	 * 
	 * @param view
	 */
	public void adIntercept(View view) {
		Intent intent = new Intent(IndexActivity.this,
				AdInterceptActivity.class);
		startActivity(intent);
	}

	/**
	 * 手机防盗
	 * 
	 * @param view
	 */
	public void phoneLost(View view) {
		Intent intent = new Intent(IndexActivity.this, LostProtectedActivity.class);
		startActivity(intent);
	}

	/**
	 * 日志记录
	 * 
	 * @param view
	 */
	public void logRecord(View view) {
		Intent intent = new Intent(IndexActivity.this, LogRecordActivity.class);
		startActivity(intent);
	}

	/**
	 * 关于
	 * 
	 * @param view
	 */
	public void aboutView(View view) {
		Intent intent = new Intent(IndexActivity.this, AboutViewActivity.class);
		startActivity(intent);
	}

}
