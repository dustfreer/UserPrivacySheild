package com.security.ass.pinyinime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;



/**
 * �ù㲥�����������ն�ά��ҳ�淵�����Ľ�����ֵ
 * @author Gene
 *
 */
public class QRReceiver extends BroadcastReceiver{

	public static String qrCodeString;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		qrCodeString = intent.getStringExtra("qrCodeString");
		
		Log.e("QRReceiver", "�㲥���յ����ǣ�"+qrCodeString);
	}

}
