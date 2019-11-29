package com.security.ass.staticanalyse;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

//�Լ������֪ͨ����
public class MyNotification {
	public void showNotification(Context context,String tickerText,String contentTitle,String contentText,int drawableIcon){
		
		//������ʾ������
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setContentText(contentTitle).setSmallIcon(drawableIcon);
		mBuilder.setTicker(tickerText);
		mBuilder.setAutoCancel(true);
		mBuilder.setOngoing(true);
		
		Intent resultIntent = new Intent(context,StaticAnalyseActivity.class);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED); // ����Ϊ���Ӧ���Ѿ����������ظ�����

		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
				0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(0, mBuilder.build());
		
		
		
		
	}
	
	
	

}
