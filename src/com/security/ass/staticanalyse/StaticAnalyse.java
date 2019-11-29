package com.security.ass.staticanalyse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.util.Log;

import com.security.ass.R;

import com.security.ass.staticanalyse.MyNotification;

public class StaticAnalyse {
	private static MessageDigest digest = null;

	// 组合权限
	private static TreeSet<String> comPers_adrd = new TreeSet<String>() {
		{
			add("android.permission.INTERNET");
			add("android.permission.ACCESS_NETWORK_STATE");
			add("android.permission.RECEIVE_BOOT_COMPLETED");
		}
	};

	private static TreeSet<String> comPers_Bgserv = new TreeSet<String>() {
		{
			add("android.permission.INTERNET");
			add("android.permission.RECEIVE_SMS");
			add("android.permission.RECEIVE_SMS");
		}
	};

	private static TreeSet<String> comPers_droidDreamLight = new TreeSet<String>() {
		{
			add("android.permission.INTERNET");
			add("android.permission.READ_PHONE_STATE");
		}
	};

	private static TreeSet<String> comPers_Geinimi = new TreeSet<String>() {
		{
			add("android.permission.INTERNET");
			add("android.permission.SEND_SMS");
		}
	};

	private static TreeSet<String> comPers_pjapps = new TreeSet<String>() {
		{
			add("android.permission.INTERNET");
			add("android.permission.RECEIVE_SMS");
		}
	};

	private static TreeSet<String> comPers_zsone = new TreeSet<String>() {
		{
			add("android.permission.RECEIVE_SMS");
			add("android.permission.SEND_SMS");
		}
	};

	// 危险权限类
	private static TreeSet<String> dangerPers_c1 = new TreeSet<String>(); // 恶意扣费类
	private static TreeSet<String> dangerPers_c2 = new TreeSet<String>(); // 窃取隐私信息类
	private static TreeSet<String> dangerPers_c3 = new TreeSet<String>(); // 自启动耗电类
	private static TreeSet<String> dangerPers_c4 = new TreeSet<String>(); // 联网类
	private static TreeSet<String> dangerPers_c5 = new TreeSet<String>(); // 拦截类
	private static TreeSet<String> dangerPers_c6 = new TreeSet<String>(); // 恶意安装应用类

	public static final HashMap<String, String> PERMISSION_MAP = new HashMap<String, String>();

	static {
		PERMISSION_MAP.put("SEND_SMS", "允许程序发送SMS短信");
		PERMISSION_MAP.put("WRITE_SMS", "允许程序写短信");
		PERMISSION_MAP.put("CALL_PHONE", "允许后台拨打电话");
		PERMISSION_MAP.put("WRITE_HISTORY_BOOKMARKS", "允许写入浏览器历史和书签记录");
		PERMISSION_MAP.put("READ_SMS", "允许程序读取短信息");
		PERMISSION_MAP.put("MOUNT_UNMOUNT_FILESYSTEMS", "允许程序挂载文件系统");
		PERMISSION_MAP.put("READ_HISTORY_BOOKMARKS", "允许读取浏览器历史记录");
		PERMISSION_MAP.put("READ_LOGS", "允许程序读取系统底层日志");
		PERMISSION_MAP.put("RECORD_AUDIO", "允许程序录制音频");
		PERMISSION_MAP.put("CAMERA", "允许访问摄像头进行拍照");
		PERMISSION_MAP.put("READ_CONTACTS", "允许程序读取用户联系人数据");
		PERMISSION_MAP.put("ACCESS_FINE_LOCATION", "允许程序访问精确位置(如GPS)");
		PERMISSION_MAP.put("RECEIVE_BOOT_COMPLETED", "允许程序开机自启");

		PERMISSION_MAP.put("ACCESS_NETWORK_STATE", "允许程序访问有关GSM网络信息");
		PERMISSION_MAP.put("CHANGE_WIFI_STATE", "允许程序改变Wi-Fi连接状态");
		PERMISSION_MAP.put("INTERNET", "允许程序访问网络");
		PERMISSION_MAP.put("RECEIVE_SMS", "允许程序接收短信息");
		PERMISSION_MAP.put("PROCESS_OUTGOING_CALL", "允许程序监视、修改有关播出电话");
		PERMISSION_MAP.put("MODIFY_PHONE_STATE", "允许修改话机状态");
		PERMISSION_MAP.put("READ_PHONE_STATE", "允许程序访问电话状态");
		PERMISSION_MAP.put("INSTALL_PACKAGES", "允许程序安装应用");
	}

	static {
		dangerPers_c1.add("android.permission.SEND_SMS");
		dangerPers_c1.add("android.permission.WRITE_SMS");
		dangerPers_c1.add("android.permission.CALL_PHONE");
		dangerPers_c1.add("android.permission.WRITE_HISTORY_BOOKMARKS");

		dangerPers_c2.add("android.permission.READ_SMS");
		dangerPers_c2.add("android.permission.MOUNT_UNMOUNT_FILESYSTEMS");
		dangerPers_c2.add("android.permission.READ_HISTORY_BOOKMARKS");
		dangerPers_c2.add("android.permission.READ_LOGS");
		dangerPers_c2.add("android.permission.RECORD_AUDIO");
		dangerPers_c2.add("android.permission.CAMERA");
		dangerPers_c2.add("android.permission.READ_CONTACTS");
		dangerPers_c2.add("android.permission.ACCESS_FINE_LOCATION");

		dangerPers_c3.add("android.permission.RECEIVE_BOOT_COMPLETED");

		dangerPers_c4.add("android.permission.ACCESS_NETWORK_STATE");
		dangerPers_c4.add("android.permission.CHANGE_WIFI_STATE");
		dangerPers_c4.add("android.permission.INTERNET");

		dangerPers_c5.add("android.permission.RECEIVE_SMS");
		dangerPers_c5.add("android.permission.PROCESS_OUTGOING_CALL");
		dangerPers_c5.add("android.permission.MODIFY_PHONE_STATE");
		dangerPers_c5.add("android.permission.READ_PHONE_STATE");

		dangerPers_c6.add("android.permission.INSTALL_PACKAGES");
	}

	// 危险意图
	public static TreeSet<String> boot_complete = new TreeSet<String>();
	public static TreeSet<String> sms_received = new TreeSet<String>();
	public static TreeSet<String> connectivity_change = new TreeSet<String>();
	public static TreeSet<String> use_present = new TreeSet<String>();
	public static TreeSet<String> phone_state = new TreeSet<String>();
	public static TreeSet<String> new_call = new TreeSet<String>();

	public static void initIntent() {
		PackageManager pm = MyApplication.appContext.getPackageManager();

		Intent intent1 = new Intent("android.intent.action.BOOT_COMPLETED");
		List<ResolveInfo> boot_complete = pm.queryBroadcastReceivers(intent1,
				PackageManager.GET_RECEIVERS);
		for (ResolveInfo info : boot_complete) {
			StaticAnalyse.boot_complete.add(info.activityInfo.packageName);
		}

		Intent intent2 = new Intent("android.provider.Telephony.SMS_RECEIVED");
		List<ResolveInfo> sms_received = pm.queryBroadcastReceivers(intent2,
				PackageManager.GET_RECEIVERS);
		for (ResolveInfo info : sms_received) {
			StaticAnalyse.sms_received.add(info.activityInfo.packageName);
		}

		Intent intent3 = new Intent("android.net.conn.CONNECTIVITY_CHANGE");
		List<ResolveInfo> connectivity_change = pm.queryBroadcastReceivers(
				intent3, PackageManager.GET_RECEIVERS);
		for (ResolveInfo info : connectivity_change) {
			StaticAnalyse.connectivity_change
					.add(info.activityInfo.packageName);
		}

		Intent intent4 = new Intent("android.intent.action.USER_PRESENT");
		List<ResolveInfo> use_present = pm.queryBroadcastReceivers(intent4,
				PackageManager.GET_RECEIVERS);
		for (ResolveInfo info : use_present) {
			StaticAnalyse.use_present.add(info.activityInfo.packageName);
		}

		Intent intent5 = new Intent("android.intent.action.PHONE_STATE");
		List<ResolveInfo> phone_state = pm.queryBroadcastReceivers(intent5,
				PackageManager.GET_RECEIVERS);
		for (ResolveInfo info : phone_state) {
			StaticAnalyse.phone_state.add(info.activityInfo.packageName);
		}

		Intent intent6 = new Intent("android.intent.action.NEW_OUTGOING_CALL");
		List<ResolveInfo> new_call = pm.queryBroadcastReceivers(intent6,
				PackageManager.GET_RECEIVERS);
		for (ResolveInfo info : new_call) {
			StaticAnalyse.new_call.add(info.activityInfo.packageName);
		}
	}

	public static boolean analyse(String packageName) {

		String des;
		Cursor cursor = MyApplication.staticAnalyseLog.query("AnalyseLog",
				null, "package = ?", new String[] { packageName }, null, null,
				null);
		if (cursor != null && cursor.moveToFirst()) {
			des = cursor.getString(cursor.getColumnIndex("description"));
			if (des.equals("安全"))
				return false;
			if (des.equals("危险"))
				return true;
		}

		PackageInfo info = null;
		try {
			info = MyApplication.appContext.getPackageManager()
					.getPackageInfo(
							packageName,
							PackageManager.GET_ACTIVITIES
									| PackageManager.GET_INTENT_FILTERS
									| PackageManager.GET_PERMISSIONS
									| PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File apk = new File(info.applicationInfo.sourceDir);
		String md5 = getFileMD5(apk);
		ContentValues cv = new ContentValues();
		cv.put("package", packageName);
		cv.put("md5", md5);
		cv.put("appName", (String) info.applicationInfo
				.loadLabel(MyApplication.appContext.getPackageManager()));
		if (info.versionName == null) {
			cv.put("version", "");
		} else {
			cv.put("version", info.versionName);
		}

		boolean flag = step1(md5);
		if (flag) {
			PackageManager pm = MyApplication.appContext.getPackageManager();
			cv.put("malware_match", "匹配");
			cv.put("description", "危险");
		} else {
			cv.put("malware_match", "不匹配");
			cv.put("description", "安全");
		}
		// 组合权限检测
		String c_b_permissions = step2(info.requestedPermissions);
		cv.put("combination_permission", c_b_permissions);
		// 危险权限检测
		String d_p_permissions = step3(info.requestedPermissions);
		cv.put("dangerpermission", d_p_permissions);
		// 危险意图检测
		String d_i_permissions = step4(info.packageName);
		cv.put("dangerintent", d_i_permissions);

		MyApplication.staticAnalyseLog.insert("AnalyseLog", null, cv);
		return flag;
	}

	public static boolean analyse1(String packageName) {
		PackageInfo info = null;
		try {
			info = MyApplication.appContext.getPackageManager()
					.getPackageInfo(
							packageName,
							PackageManager.GET_ACTIVITIES
									| PackageManager.GET_INTENT_FILTERS
									| PackageManager.GET_PERMISSIONS);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File apk = new File(info.applicationInfo.sourceDir);
		String md5 = getFileMD5(apk);
		boolean flag = step1(md5);
		if (flag) {
			PackageManager pm = MyApplication.appContext.getPackageManager();
			MyNotification notify = new MyNotification();
			notify.showNotification(MyApplication.appContext, "[危险] "
					+ info.applicationInfo.loadLabel(pm) + "为已知恶意应用！",
					"发现恶意应用！", "[危险] " + info.applicationInfo.loadLabel(pm)
							+ "为已知恶意应用！", R.drawable.warnning);
		}
		return flag;
	}

	public static boolean step1(String md5) {
		Log.w("StaticAnalyse", "step1 query malware");
		Cursor cursor = MyApplication.AntiMalware.query("malware", null,
				"md5 = ?", new String[] { md5 }, null, null, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}

	public static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}

		if (digest == null) {
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;

		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] buf = digest.digest();
		digest.reset();
		return toHexString(buf);
	}

	private static char hexChar[] = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String toHexString(byte b[]) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0xf]);
		}
		return sb.toString();
	}

	// 组合权限匹配
	private static String step2(String[] pers) {
		String report = null;
		if (pers == null) {
			// report = "该应用未申请任何恶意软件家族组合权限" + "\r\n";
			report = "无" + ",";
			return report;
		}

		TreeSet<String> perSet = new TreeSet<String>();
		for (String s : pers) {
			perSet.add(s);
		}

		if (subSet(perSet, comPers_adrd)) {
			if (report == null)
				report = new String();

			// report += "该应用申请了恶意软件家族adrd的组合权限" + "\r\n";
			report += "恶意软件家族adrd" + ",";
		}

		if (subSet(perSet, comPers_Bgserv)) {
			if (report == null)
				report = new String();
			// report += "该应用申请了恶意软件家族Bgservd的组合权限" + "\r\n";
			report += "恶意软件家族Bgservd" + ",";
		}

		if (subSet(perSet, comPers_droidDreamLight)) {
			if (report == null)
				report = new String();
			// report += "该应用申请了恶意软件家族DroidDreamLight的组合权限" + "\r\n";
			report += "恶意软件家族DroidDreamLight" + ",";
		}

		if (subSet(perSet, comPers_Geinimi)) {
			if (report == null)
				report = new String();
			// report += "该应用申请了恶意软件家族Geinimi的组合权限" + "\r\n";
			report += "恶意软件家族Geinimi" + ",";
		}

		if (subSet(perSet, comPers_pjapps)) {
			if (report == null)
				report = new String();
			// report += "该应用申请了恶意软件家族pjapps的组合权限" + "\r\n";
			report += "恶意软件家族pjapps" + ",";
		}

		if (subSet(perSet, comPers_zsone)) {
			if (report == null)
				report = new String();
			// report += "该应用申请了恶意软件家族zsone的组合权限" + "\r\n";
			report += "恶意软件家族zsone" + ",";
		}

		if (report == null) {
			// report = "该应用未申请任何恶意软件家族组合权限" + "\r\n";
			report = "无" + ",";
		}

		return report;
	}

	// 危险权限匹配
	private static String step3(String[] pers) {
		int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0;
		String report = null;

		if (pers == null) {
			// report = "该应用未申请任何恶意软件家族组合权限" + "\r\n";
			report = "无" + ",";
			return report;
		}

		for (String s : pers) {
			boolean flag = true;
			if (dangerPers_c1.contains(s)) {
				c1 += 1;
			} else if (dangerPers_c2.contains(s)) {
				c2 += 1;
			} else if (dangerPers_c3.contains(s)) {
				c3 += 1;
			} else if (dangerPers_c4.contains(s)) {
				c4 += 1;
			} else if (dangerPers_c5.contains(s)) {
				c5 += 1;
			} else if (dangerPers_c6.contains(s)) {
				c6 += 1;
			} else {
				flag = false;
			}

			if (flag) {
				if (report == null)
					report = new String();
				report += (s.substring(19) + ",");
			}
		}
		if (report == null) {
			report = "无" + ",";
		} else {
			if (c1 > 0) {
				// report += "恶意扣费威胁度：" + c1 + "/4\r\n";
				report += "恶意扣费威胁度：" + c1 + "/4,";
			}

			if (c2 > 0) {
				// report += "隐私泄露威胁度：" + c2 + "/8\r\n";
				report += "隐私泄露威胁度：" + c2 + "/8,";
			}

			if (c3 > 0) {
				// report += "自启动耗电威胁度：" + c3 + "/1\r\n";
				report += "自启动耗电威胁度：" + c3 + "/1,";
			}

			if (c4 > 0) {
				// report += "联网威胁度：" + c4 + "/3\r\n";
				report += "联网威胁度：" + c4 + "/3,";
			}

			if (c5 > 0) {
				// report += "拦截威胁度：" + c5 + "/4\r\n";
				report += "拦截威胁度：" + c5 + "/4,";
			}

			if (c6 > 0) {
				// report += "恶意安装应用威胁度:" + c6 + "/1\r\n";
				report += "恶意安装应用威胁度:" + c6 + "/1,";
			}
		}

		return report;
	}

	private static String step4(String packageName) {
		String ret = null;
		if (boot_complete.contains(packageName)) {
			if (ret == null)
				ret = new String();
			ret += "BOOT_COMPLETED 程序自启动;";
		}

		if (sms_received.contains(packageName)) {
			if (ret == null)
				ret = new String();
			ret += "SMS_RECEIVED 拦截通信内容;";
		}

		if (connectivity_change.contains(packageName)) {
			if (ret == null)
				ret = new String();
			ret += "CONNECTIVITY_CHANGE 恶意扣费;";
		}

		if (use_present.contains(packageName)) {
			if (ret == null)
				ret = new String();
			ret += "USER_PRESENT 窃取隐私信息;";
		}

		if (phone_state.contains(packageName)) {
			if (ret == null)
				ret = new String();
			ret += "PHONE_STATE 拦截通信，恶意扣费;";
		}

		if (new_call.contains(packageName)) {
			if (ret == null)
				ret = new String();
			ret += "NEW_OUTGOING_CALL 恶意扣费;";
		}

		if (ret == null) {
			ret = "无;";
		}

		return ret;
	}

	private static boolean subSet(Set<String> father, Set<String> sub) {

		for (String str : sub) {
			if (!father.contains(str)) {
				return false;
			}
		}
		return true;
	}
}
