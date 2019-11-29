package com.security.ass.staticanalyse;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

//自己定义的通知栏类
public class MyNotification {
	public void showNotification(Context context,String tickerText,String contentTitle,String contentText,int drawableIcon){
		
		//设置显示的字体
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setContentText(contentTitle).setSmallIcon(drawableIcon);
		mBuilder.setTicker(tickerText);
		mBuilder.setAutoCancel(true);
		mBuilder.setOngoing(true);
		
		Intent resultIntent = new Intent(context,StaticAnalyseActivity.class);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED); // 设置为如果应用已经启动，则不重复启动

		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
				0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(0, mBuilder.build());
		
		
		
		
	}
	
	
	

}
