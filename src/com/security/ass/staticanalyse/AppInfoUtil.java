package com.security.ass.staticanalyse;

import java.util.Arrays;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.security.ass.staticanalyse.StaticAnalyse;

public class AppInfoUtil {
	public String appName = "";
	public String packageName = "";
	public String versionName = "";
	public int versionCode = 0;
	public Drawable appIcon = null;
	public String adName = "";
	public int count = 0;
	public String md5 = "";
	public String malware_match;
	public String description;
	public long repackageFlag;
	public String strenth_dec;

	public String getStrenth_dec() {
		return strenth_dec;
	}

	public void setStrenth_dec(String strenth_dec) {
		this.strenth_dec = strenth_dec;
	}

	private String app_sent = "0K";
	// 软件gprs下载总量
	private String app_rev = "0K";
	// 软件流量总量
	private String app_traffic = "0K";
	// 标示
	private boolean ta = true;

	public long getRepackageFlag() {
		return repackageFlag;
	}

	public void setRepackageFlag(long repackageFlag) {
		this.repackageFlag = repackageFlag;
	}

	// 组合权限
	public String combination_permission;
	public List<String> c_b_list;

	public String getCombination_permission() {
		return combination_permission;
	}

	public void setCombination_permission(String combination_permission) {
		this.combination_permission = combination_permission;
	}

	public List<String> getC_b_list() {
		return c_b_list;
	}

	public void setC_b_list() {
		String[] str = combination_permission.split(",");
		c_b_list = Arrays.asList(str);
	}

	// 危险权限
	public String dangerpermission;
	public List<String> d_p_list;

	public String getDangerpermission() {
		return dangerpermission;
	}

	public void setDangerpermission(String dangerpermission) {
		this.dangerpermission = dangerpermission;
	}

	public List<String> getD_p_list() {
		return d_p_list;
	}

	public void setD_p_list() {
		String[] str = dangerpermission.split(",");
		String[] str1 = new String[str.length];
		int count = 0;
		for (String substr : str) {
			char[] chars = new char[1];
			chars[0] = substr.charAt(0);
			String temp = new String();
			if (chars[0] >= 'A' && chars[0] <= 'Z') {
				temp = StaticAnalyse.PERMISSION_MAP.get(substr);
				str1[count] = temp;
			} else {
				str1[count] = substr;
			}
			count++;
		}

		d_p_list = Arrays.asList(str1);
	}

	// 危险意图
	public String dangerintent;
	public List<String> d_i_list;

	public String getDangerintent() {
		return dangerintent;
	}

	public void setDangerintent(String dangerintent) {
		this.dangerintent = dangerintent;
	}

	public List<String> getD_i_list() {
		return d_i_list;
	}

	public void setD_i_list() {
		String[] str = dangerintent.split(";");
		d_i_list = Arrays.asList(str);
	}
	public void setC_b_list(List<String> c_b_list) {
		this.c_b_list = c_b_list;
	}

	public String getMalware_match() {
		return malware_match;
	}

	public void setMalware_match(String malware_match) {
		this.malware_match = malware_match;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getAdName() {
		return adName;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	// //wifi
	// private String wifi_sent;
	// private String wifi_rev;
	// private String wifi_traffic;

	// public String getWifi_sent() {
	// return wifi_sent;
	// }
	// public void setWifi_sent(long wifi_sent) {
	// this.wifi_sent = refreshTraffic(wifi_sent);
	// }
	// public String getWifi_rev() {
	// return wifi_rev;
	// }
	// public void setWifi_rev(long wifi_rev) {
	// this.wifi_rev = refreshTraffic(wifi_rev);
	// }
	// public String getWifi_traffic() {
	// return wifi_traffic;
	// }
	// public void setWifi_traffic(long wifi_traffic) {
	// this.wifi_traffic = refreshTraffic(wifi_traffic);
	// }
	public boolean isTa() {
		return ta;
	}

	public void setTa(boolean ta) {
		this.ta = ta;
	}

	public String getApp_sent() {
		return app_sent;
	}

	public void setApp_sent(long app_sent) {
		this.app_sent = refreshTraffic(app_sent);
	}

	public String getApp_rev() {
		return app_rev;
	}

	public void setApp_rev(long app_rev) {
		this.app_rev = refreshTraffic(app_rev);
	}

	public String getApp_traffic() {
		return app_traffic;
	}

	public void setApp_traffic(long app_traffic) {
		this.app_traffic = refreshTraffic(app_traffic);
	}

	private String refreshTraffic(long lg) {
		String str = "0K";
		int a = 0, b = 1024, c = 1048576;
		;

		Log.v("hells", lg + "");
		if (lg < 1024) {
			str = "0K";
		} else if (lg >= b && lg < c) {
			int d = (int) (lg / b);
			str = d + "K";
		} else {
			int e = (int) (lg / c);
			int f = (int) ((lg - e * c) / 1024);

			str = e + "M" + " " + f + "K";
		}
		return str;
	}
}
