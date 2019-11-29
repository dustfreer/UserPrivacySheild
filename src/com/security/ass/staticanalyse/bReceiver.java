package com.security.ass.staticanalyse;

import com.security.ass.staticanalyse.Mservice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;



public class bReceiver extends android.content.BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// ¿ª»úÆô¶¯¼à¿Ø
		System.out.println("bReceiver");
		SharedPreferences preferences = context.getSharedPreferences(
				"shadow_traffic", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("first", true);
		editor.commit();
		Intent intent2 = new Intent(context, Mservice.class);
		context.startService(intent2);

	}

}