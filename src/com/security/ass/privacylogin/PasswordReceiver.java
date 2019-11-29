package com.security.ass.privacylogin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PasswordReceiver extends BroadcastReceiver{

	public static String passInfo;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		passInfo = intent.getStringExtra("password");
		Log.e("AccountReceiver", "广播接收到的口令信息是："+passInfo);
	}

}
