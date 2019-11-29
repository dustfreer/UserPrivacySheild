package com.security.ass.staticanalyse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.security.ass.privacy.Util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class MyApplication extends Application {
	
	private Thread.UncaughtExceptionHandler mPrevHandler;

	public static Context appContext = null;
	public static SQLiteDatabase Permission = null;
	public static SQLiteDatabase AdFilter = null;
	public static SQLiteDatabase AntiMalware = null;
	public static SQLiteDatabase Repackage = null;
	public static SQLiteDatabase ActionLog = null;
	public static SQLiteDatabase staticAnalyseLog = null;

	public static String DatabasePath = Environment.getDataDirectory()
			.getAbsolutePath() + "/data/com.security.ass/databases/";

	public void onCreate() {
		appContext = this;
		
		
		//privacy ��application����
		Util.log(null, Log.WARN, "UI started");
		mPrevHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				Util.bug(null, ex);
				if (mPrevHandler != null)
					mPrevHandler.uncaughtException(thread, ex);
			}
		});

		// ���ݿ⿽��
		copyBigDatabase("AdFilter");
		copyBigDatabase("AntiMalware");
		copyBigDatabase("Repackage");

		Permission = openOrCreateDatabase("Permission.db", MODE_PRIVATE, null);
		String sql = "CREATE TABLE IF NOT EXISTS 'PermissionInfo'("
				+ "'package' varchar(45) PRIMARY KEY NOT NULL,"
				+ "'TrustFlag' bit NOT NULL," + "'AdHold' smallint NOT NULL,"
				+ "'Internet' smallint NOT NULL,"
				+ "'PhoneCall' smallint NOT NULL," + "'SMS' smallint NOT NULL,"
				+ "'Location' smallint NOT NULL,"
				+ "'Contact' smallint NOT NULL)";
		Permission.execSQL(sql);

		AdFilter = openOrCreateDatabase("AdFilter.db", MODE_PRIVATE, null);

		AntiMalware = openOrCreateDatabase("AntiMalware.db", MODE_PRIVATE, null);

		Repackage = openOrCreateDatabase("Repackage.db", MODE_PRIVATE, null);

		/***** ��־��� ****/
		ActionLog = openOrCreateDatabase("Actionlog.db", MODE_PRIVATE, null);
		String sql1 = "CREATE TABLE IF NOT EXISTS 'Log'("
				+ "'package' varchar(45) NOT NULL," + "'action' text NOT NULL,"
				+ "'date' varchar(20) PRIMARY KEY NOT NULL)";
		ActionLog.execSQL(sql1);

		staticAnalyseLog = openOrCreateDatabase("staticAnalyseLog.db",
				MODE_PRIVATE, null);
		String sql2 = "CREATE TABLE IF NOT EXISTS 'AnalyseLog'("
				+ "'package' varchar(45) PRIMARY KEY NOT NULL,"
				+ "'md5' varchar(35)," + "'appName' text," + "'version' text,"
				+ "'malware_match' text," + "'combination_permission' text,"
				+ "'dangerpermission' text," + "'dangerintent' text,"
				+ "'description' text)";

		staticAnalyseLog.execSQL(sql2);

		// �����ֻ�������̣���ѯ���ǵķ����Ƿ�����
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(150);
		boolean bPermission = false;
		boolean bTraffic = false;

		// ���б���ƥ��
		for (int i = 0; i < serviceList.size(); i++) {
			if ("com.security.ass.PermissionQueryService"
					.equals(serviceList.get(i).service.getClassName())) {
				bPermission = true;
			}

			if ("com.security.ass.Mservice"
					.equals(serviceList.get(i).service.getClassName())) {
				bTraffic = true;
			}
		}

		// ���δ���� ��������ط���
		if (!bPermission) {
			startService(new Intent(this, PermissionQueryService.class));
		}

		if (!bTraffic) {
			startService(new Intent(this, Mservice.class));
		}

	}

	private void copyBigDatabase(String database) {
		if (!createDBFile(database)) {
			return;
		}

		File db = new File(DatabasePath + database + ".db");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(db);
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// ��ȡ�����˵�����
		TreeSet<String> dbFiles = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				// TODO Auto-generated method stub
				return lhs.compareTo(rhs);
			}
		});

		// ��ȡ���е�dbFile
		try {
			String[] files = getAssets().list("");
			for (String file : files) {
				if (file.contains(database)) {
					dbFiles.add(file);
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (String file : dbFiles) {
			InputStream is = null;
			try {
				is = getAssets().open(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			copyFile(is, fos);
		}

	}

	private boolean createDBFile(String dataBase) {
		File path = new File(DatabasePath);
		if (!path.exists()) {
			path.mkdirs();
		}

		File db = new File(DatabasePath + dataBase + ".db");
		if (!db.exists()) {
			try {
				db.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private boolean copyFile(InputStream is, FileOutputStream fos) {
		byte[] buffer = new byte[1024 * 64];
		int count = 0;
		try {
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}

			is.close();
		} catch (IOException e) {
			Toast.makeText(this, "����ʱ�����쳣", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
