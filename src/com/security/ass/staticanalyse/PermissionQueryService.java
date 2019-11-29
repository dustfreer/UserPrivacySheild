package com.security.ass.staticanalyse;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.WindowManager;




import com.security.ass.R;

import com.security.ass.staticanalyse.Query.Stub;
import com.security.ass.staticanalyse.MyApplication;
import com.security.ass.staticanalyse.MyNotification;

public class PermissionQueryService extends Service {
	
	public static final int SHOW_ACTION = 0x0001;
	private Handler handler;

	// private static int
	
	private Stub mBinder = null;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {

		mBinder = new PermissionQueryBinder();

		MyNotification aesNofi = new MyNotification();
		aesNofi.showNotification(getApplicationContext(), "���ڱ���������˽��ȫ",
				"XXX", "���ڱ���������˽��ȫ", R.drawable.ic_launcher);
		
		System.out.println("���ǵ�һ������PermissionQueryService");
		
		handler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch(msg.what){
				case SHOW_ACTION:
					showAlarm((String) msg.obj);
					break;
				}
			}
		};
	}

	public void showAlarm(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MyApplication.appContext);
		builder.setTitle("XXXȨ�޹���");
		builder.setMessage(msg);
		builder.setPositiveButton("ȷ��", null);
		AlertDialog dialog = builder.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}

	private final class PermissionQueryBinder extends Stub {

		@Override
		public boolean queryConnect(String ip, int port) throws RemoteException {

			String packName = getPackageByUID(this.getCallingUid());
			Cursor cursor = MyApplication.Permission.query("PermissionInfo",
					null, "package = ?", new String[] { packName }, null, null,
					null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				int trust = cursor.getInt(cursor.getColumnIndex("TrustFlag"));
				int f = cursor.getInt(cursor.getColumnIndex("Internet"));
				cursor.close();
				if (trust > 0) {
					if (f > 0) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}

			return false;
		}

		@Override
		public boolean queryAdUrl(String url) throws RemoteException {

			String packName = getPackageByUID(this.getCallingUid());
			Cursor cursor = MyApplication.Permission.query("PermissionInfo",
					null, "package = ?", new String[] { packName }, null, null,
					null);

			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				int trust = cursor.getInt(cursor.getColumnIndex("TrustFlag"));
				int adhold = cursor.getInt(cursor.getColumnIndex("AdHold"));
				cursor.close();

				if (trust > 0) {
					if (adhold > 0) {
						Cursor cursor1 = MyApplication.AdFilter.query("AdUrl",
								null, "url = ?", new String[] { url }, null,
								null, null);
						cursor.moveToFirst();
						int count = cursor1.getCount();
						cursor1.close();
						if (count != 0) {
							return true;
						} else {
							return false;
						}
					}
				}
			}
			return false;
		}

		public String getPackageByUID(int uid) {
			PackageManager pm = getPackageManager();
			String pk = pm.getNameForUid(uid);

			return pk;
		}

		@Override
		public boolean queryLocation() throws RemoteException {
			String packName = getPackageByUID(this.getCallingUid());
			Cursor cursor = MyApplication.Permission.query("PermissionInfo",
					null, "package = ?", new String[] { packName }, null, null,
					null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				PackageManager pm = MyApplication.appContext.getPackageManager();
				ApplicationInfo info = null;
				try {
					 info = pm.getApplicationInfo(packName, PackageManager.GET_ACTIVITIES);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				String appName = (String) info.loadLabel(pm);
				int trust = cursor.getInt(cursor.getColumnIndex("TrustFlag"));
				int location = cursor.getInt(cursor.getColumnIndex("Location"));
				cursor.close();
				if (trust > 0) {
					if (location > 0) {
						new alertAndWrite(packName, "�ܾ�" + appName + "��ȡλ����Ϣ").start();
						return true;
					}else{
						new alertAndWrite(packName, "����" + appName + "��ȡλ����Ϣ").start();
					}
				}
			}

			return false;
		}

		@Override
		public boolean queryContact() throws RemoteException {
			String packName = getPackageByUID(this.getCallingUid());
			Cursor cursor = MyApplication.Permission.query("PermissionInfo",
					null, "package = ?", new String[] { packName }, null, null,
					null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				PackageManager pm = MyApplication.appContext.getPackageManager();
				ApplicationInfo info = null;
				try {
					 info = pm.getApplicationInfo(packName, PackageManager.GET_ACTIVITIES);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				String appName = (String) info.loadLabel(pm);
				
				int trust = cursor.getInt(cursor.getColumnIndex("TrustFlag"));
				int contact = cursor.getInt(cursor.getColumnIndex("Contact"));
				cursor.close();
				if (trust > 0) {
					if (contact > 0) {
						new alertAndWrite(packName, "�ܾ�" + appName + "��ȡͨѶ¼").start();
						return true;
					}else{
						new alertAndWrite(packName, "����" + appName + "��ȡͨѶ¼").start();
						return false;
					}
				}
			}
			return false;
		}

		@Override
		public boolean tel_phone() throws RemoteException {
			String packName = getPackageByUID(this.getCallingUid());
			Cursor cursor = MyApplication.Permission.query("PermissionInfo",
					null, "package = ?", new String[] { packName }, null, null,
					null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				PackageManager pm = MyApplication.appContext.getPackageManager();
				ApplicationInfo info = null;
				try {
					 info = pm.getApplicationInfo(packName, PackageManager.GET_ACTIVITIES);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				String appName = (String) info.loadLabel(pm);
				
				int trust = cursor.getInt(cursor.getColumnIndex("TrustFlag"));
				int tel = cursor.getInt(cursor.getColumnIndex("PhoneCall"));
				cursor.close();
				if (trust > 0) {
					if (tel > 0) {
						new alertAndWrite(packName, "�ܾ�" + appName + "����绰").start();
						return true;
					}else{
						new alertAndWrite(packName, "����" + appName + "����绰").start();
						return false;
					}
				}
			}
			return false;	
		}

		@Override
		public boolean sendSMS() throws RemoteException {
			String packName = getPackageByUID(this.getCallingUid());
			Cursor cursor = MyApplication.Permission.query("PermissionInfo",
					null, "package = ?", new String[] { packName }, null, null,
					null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				PackageManager pm = MyApplication.appContext.getPackageManager();
				ApplicationInfo info = null;
				try {
					 info = pm.getApplicationInfo(packName, PackageManager.GET_ACTIVITIES);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				String appName = (String) info.loadLabel(pm);
				
				int trust = cursor.getInt(cursor.getColumnIndex("TrustFlag"));
				int sms = cursor.getInt(cursor.getColumnIndex("SMS"));
				cursor.close();
				if (trust > 0) {
					if (sms > 0) {
						new alertAndWrite(packName, "�ܾ�" + appName + "���Ͷ���").start();
						return true;
					}else{
						new alertAndWrite(packName, "����" + appName + "���Ͷ���").start();
						return false;
					}
				}
			}
			return false;	
		}
	}

	private class alertAndWrite extends Thread {
		private String PackageName;
		private String action;
		private Date date;

		public alertAndWrite(String packageName, String action) {
			this.PackageName = packageName;
			this.action = action;
			date = new Date();
		}

		@Override
		public void run() {
			
			try {
				this.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
//			showAlarm(action);
			Message msg = Message.obtain();
			msg.what = SHOW_ACTION;
			msg.obj = action;
			handler.sendMessage(msg);
			
			ContentValues cv = new ContentValues();
			cv.put("package", PackageName);
			cv.put("action", action);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			cv.put("date", sdf.format(date));
			
			MyApplication.ActionLog.insert("Log", null, cv);
		}

	}
}
