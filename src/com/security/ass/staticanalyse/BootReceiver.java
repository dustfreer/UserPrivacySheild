package com.security.ass.staticanalyse;

import com.security.ass.staticanalyse.PermissionQueryService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;



public class BootReceiver extends BroadcastReceiver {
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		Log.i("AndroidGuard", "is Booting");
		System.out.println("AseBootReceiver");
		
		//开机自启动数据库查询服务
		if(arg1.getAction().equals(ACTION)){
			arg0.startService(new Intent(arg0, PermissionQueryService.class ));
		}
	}

}