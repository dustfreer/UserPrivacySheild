package com.security.ass.pinyinime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;



/**
 * 该广播接收器，接收二维码页面返回来的解码后的值
 * @author Gene
 *
 */
public class QRReceiver extends BroadcastReceiver{

	public static String qrCodeString;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		qrCodeString = intent.getStringExtra("qrCodeString");
		
		Log.e("QRReceiver", "广播接收到的是："+qrCodeString);
	}

}
