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

	// ���Ȩ��
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

	// Σ��Ȩ����
	private static TreeSet<String> dangerPers_c1 = new TreeSet<String>(); // ����۷���
	private static TreeSet<String> dangerPers_c2 = new TreeSet<String>(); // ��ȡ��˽��Ϣ��
	private static TreeSet<String> dangerPers_c3 = new TreeSet<String>(); // �������ĵ���
	private static TreeSet<String> dangerPers_c4 = new TreeSet<String>(); // ������
	private static TreeSet<String> dangerPers_c5 = new TreeSet<String>(); // ������
	private static TreeSet<String> dangerPers_c6 = new TreeSet<String>(); // ���ⰲװӦ����

	public static final HashMap<String, String> PERMISSION_MAP = new HashMap<String, String>();

	static {
		PERMISSION_MAP.put("SEND_SMS", "���������SMS����");
		PERMISSION_MAP.put("WRITE_SMS", "�������д����");
		PERMISSION_MAP.put("CALL_PHONE", "�����̨����绰");
		PERMISSION_MAP.put("WRITE_HISTORY_BOOKMARKS", "����д���������ʷ����ǩ��¼");
		PERMISSION_MAP.put("READ_SMS", "��������ȡ����Ϣ");
		PERMISSION_MAP.put("MOUNT_UNMOUNT_FILESYSTEMS", "�����������ļ�ϵͳ");
		PERMISSION_MAP.put("READ_HISTORY_BOOKMARKS", "�����ȡ�������ʷ��¼");
		PERMISSION_MAP.put("READ_LOGS", "��������ȡϵͳ�ײ���־");
		PERMISSION_MAP.put("RECORD_AUDIO", "�������¼����Ƶ");
		PERMISSION_MAP.put("CAMERA", "�����������ͷ��������");
		PERMISSION_MAP.put("READ_CONTACTS", "��������ȡ�û���ϵ������");
		PERMISSION_MAP.put("ACCESS_FINE_LOCATION", "���������ʾ�ȷλ��(��GPS)");
		PERMISSION_MAP.put("RECEIVE_BOOT_COMPLETED", "������򿪻�����");

		PERMISSION_MAP.put("ACCESS_NETWORK_STATE", "�����������й�GSM������Ϣ");
		PERMISSION_MAP.put("CHANGE_WIFI_STATE", "�������ı�Wi-Fi����״̬");
		PERMISSION_MAP.put("INTERNET", "��������������");
		PERMISSION_MAP.put("RECEIVE_SMS", "���������ն���Ϣ");
		PERMISSION_MAP.put("PROCESS_OUTGOING_CALL", "���������ӡ��޸��йز����绰");
		PERMISSION_MAP.put("MODIFY_PHONE_STATE", "�����޸Ļ���״̬");
		PERMISSION_MAP.put("READ_PHONE_STATE", "���������ʵ绰״̬");
		PERMISSION_MAP.put("INSTALL_PACKAGES", "�������װӦ��");
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

	// Σ����ͼ
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
			if (des.equals("��ȫ"))
				return false;
			if (des.equals("Σ��"))
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
			cv.put("malware_match", "ƥ��");
			cv.put("description", "Σ��");
		} else {
			cv.put("malware_match", "��ƥ��");
			cv.put("description", "��ȫ");
		}
		// ���Ȩ�޼��
		String c_b_permissions = step2(info.requestedPermissions);
		cv.put("combination_permission", c_b_permissions);
		// Σ��Ȩ�޼��
		String d_p_permissions = step3(info.requestedPermissions);
		cv.put("dangerpermission", d_p_permissions);
		// Σ����ͼ���
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
			notify.showNotification(MyApplication.appContext, "[Σ��] "
					+ info.applicationInfo.loadLabel(pm) + "Ϊ��֪����Ӧ�ã�",
					"���ֶ���Ӧ�ã�", "[Σ��] " + info.applicationInfo.loadLabel(pm)
							+ "Ϊ��֪����Ӧ�ã�", R.drawable.warnning);
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

	// ���Ȩ��ƥ��
	private static String step2(String[] pers) {
		String report = null;
		if (pers == null) {
			// report = "��Ӧ��δ�����κζ�������������Ȩ��" + "\r\n";
			report = "��" + ",";
			return report;
		}

		TreeSet<String> perSet = new TreeSet<String>();
		for (String s : pers) {
			perSet.add(s);
		}

		if (subSet(perSet, comPers_adrd)) {
			if (report == null)
				report = new String();

			// report += "��Ӧ�������˶����������adrd�����Ȩ��" + "\r\n";
			report += "�����������adrd" + ",";
		}

		if (subSet(perSet, comPers_Bgserv)) {
			if (report == null)
				report = new String();
			// report += "��Ӧ�������˶����������Bgservd�����Ȩ��" + "\r\n";
			report += "�����������Bgservd" + ",";
		}

		if (subSet(perSet, comPers_droidDreamLight)) {
			if (report == null)
				report = new String();
			// report += "��Ӧ�������˶����������DroidDreamLight�����Ȩ��" + "\r\n";
			report += "�����������DroidDreamLight" + ",";
		}

		if (subSet(perSet, comPers_Geinimi)) {
			if (report == null)
				report = new String();
			// report += "��Ӧ�������˶����������Geinimi�����Ȩ��" + "\r\n";
			report += "�����������Geinimi" + ",";
		}

		if (subSet(perSet, comPers_pjapps)) {
			if (report == null)
				report = new String();
			// report += "��Ӧ�������˶����������pjapps�����Ȩ��" + "\r\n";
			report += "�����������pjapps" + ",";
		}

		if (subSet(perSet, comPers_zsone)) {
			if (report == null)
				report = new String();
			// report += "��Ӧ�������˶����������zsone�����Ȩ��" + "\r\n";
			report += "�����������zsone" + ",";
		}

		if (report == null) {
			// report = "��Ӧ��δ�����κζ�������������Ȩ��" + "\r\n";
			report = "��" + ",";
		}

		return report;
	}

	// Σ��Ȩ��ƥ��
	private static String step3(String[] pers) {
		int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0;
		String report = null;

		if (pers == null) {
			// report = "��Ӧ��δ�����κζ�������������Ȩ��" + "\r\n";
			report = "��" + ",";
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
			report = "��" + ",";
		} else {
			if (c1 > 0) {
				// report += "����۷���в�ȣ�" + c1 + "/4\r\n";
				report += "����۷���в�ȣ�" + c1 + "/4,";
			}

			if (c2 > 0) {
				// report += "��˽й¶��в�ȣ�" + c2 + "/8\r\n";
				report += "��˽й¶��в�ȣ�" + c2 + "/8,";
			}

			if (c3 > 0) {
				// report += "�������ĵ���в�ȣ�" + c3 + "/1\r\n";
				report += "�������ĵ���в�ȣ�" + c3 + "/1,";
			}

			if (c4 > 0) {
				// report += "������в�ȣ�" + c4 + "/3\r\n";
				report += "������в�ȣ�" + c4 + "/3,";
			}

			if (c5 > 0) {
				// report += "������в�ȣ�" + c5 + "/4\r\n";
				report += "������в�ȣ�" + c5 + "/4,";
			}

			if (c6 > 0) {
				// report += "���ⰲװӦ����в��:" + c6 + "/1\r\n";
				report += "���ⰲװӦ����в��:" + c6 + "/1,";
			}
		}

		return report;
	}

	private static String step4(String packageName) {
		String ret = null;
		if (boot_complete.contains(packageName)) {
			if (ret == null)
				ret = new String();
			ret += "BOOT_COMPLETED ����������;";
		}

		if (sms_received.contains(packageName)) {
			if (ret == null)
				ret = new String();
			ret += "SMS_RECEIVED ����ͨ������;";
		}

		if (connectivity_change.contains(packageName)) {
			if (ret == null)
				ret = new String();
			ret += "CONNECTIVITY_CHANGE ����۷�;";
		}

		if (use_present.contains(packageName)) {
			if (ret == null)
				ret = new String();
			ret += "USER_PRESENT ��ȡ��˽��Ϣ;";
		}

		if (phone_state.contains(packageName)) {
			if (ret == null)
				ret = new String();
			ret += "PHONE_STATE ����ͨ�ţ�����۷�;";
		}

		if (new_call.contains(packageName)) {
			if (ret == null)
				ret = new String();
			ret += "NEW_OUTGOING_CALL ����۷�;";
		}

		if (ret == null) {
			ret = "��;";
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
