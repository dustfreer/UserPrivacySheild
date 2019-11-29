package com.security.ass.privacylogin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AccountReceiver extends BroadcastReceiver{

	public static String accountInfo;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		accountInfo = intent.getStringExtra("account");
		Log.e("AccountReceiver", "广播接收到的账号信息是："+accountInfo);
	}

}
