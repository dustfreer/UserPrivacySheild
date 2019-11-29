package com.security.ass.staticanalyse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.security.ass.R;

public class StaticAnalyseActivity extends Activity {

	protected static final int SCAN_NOT_VIRUS = 90;
	protected static final int FIND_VIRUS = 91;
	protected static final int SCAN_FINISH = 92;

	private LinearLayout ll_scan_status; // 显示扫描的程序信息
	private PackageManager pm; // 应用程序包管理器
	private List<PackageInfo> virusPackInfos; // 用于添加扫描到的病毒信息

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			PackageInfo info = (PackageInfo) msg.obj;
			Log.w("main", "handleMessage");

			switch (msg.what) {
			case SCAN_NOT_VIRUS:
				TextView tv = new TextView(getApplicationContext());
				tv.setText("扫描" + info.applicationInfo.loadLabel(pm) + "安全");
				tv.setTextColor(Color.rgb(00, 150, 00));
				ll_scan_status.addView(tv, 0);
				break;

			case FIND_VIRUS:
				virusPackInfos.add(info);
				TextView tv1 = new TextView(getApplicationContext());
				tv1.setText("扫描" + info.applicationInfo.loadLabel(pm) + "危险");
				tv1.setTextColor(Color.RED);
				ll_scan_status.addView(tv1, 0);

				// 当存在病毒时，出现通知
				MyNotification notify = new MyNotification();
				notify.showNotification(MyApplication.appContext, "[危险] "
						+ info.applicationInfo.loadLabel(pm) + "为已知恶意应用！",
						"发现恶意应用！", "[危险] " + info.applicationInfo.loadLabel(pm)
								+ "为已知恶意应用！", R.drawable.warnning);
				break;

			case SCAN_FINISH:
				Log.w("main", "SCan_finish");
				if (virusPackInfos.size() == 0) {
					Toast.makeText(getApplicationContext(), "扫描完毕，你的手机很安全",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(),
							"扫描完毕，发现" + virusPackInfos.size() + "个已知恶意应用",
							Toast.LENGTH_LONG).show();
					ContentValues cv = new ContentValues();
					cv.put("package", MyApplication.appContext.getPackageName());
					cv.put("action", "检测到" + virusPackInfos.size() + "个已知恶意应用");
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyy-MM-dd HH:mm:ss");
					cv.put("date", sdf.format(date));

					MyApplication.ActionLog.insert("Log", null, cv);
				}

				break;
			default:
				break;

			}

		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.staticanalyse_activity);

		Log.w("main", "onCreate");

		Log.i("MainActivity", "is Booting");
		virusPackInfos = new ArrayList<PackageInfo>();

		pm = getPackageManager();

		ll_scan_status = (LinearLayout) findViewById(R.id.ll_scan_status);
	}

	public void detection(View view) {
		new Thread() {
			public void run() {
				Log.w("main", "detection");
				List<PackageInfo> packinfos = pm
						.getInstalledPackages(PackageManager.GET_SIGNATURES);

				int count = 0;
				virusPackInfos.clear();

				for (PackageInfo info : packinfos) {
					boolean flag = StaticAnalyse.analyse(info.packageName);

					if (!flag) {
						Message msg = Message.obtain();
						msg.what = SCAN_NOT_VIRUS;
						msg.obj = info;
						handler.sendMessage(msg);
					} else {
						Message msg = Message.obtain();
						msg.what = FIND_VIRUS;
						msg.obj = info;
						handler.sendMessage(msg);
					}
					count++;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// 遍历完成
				Message msg = Message.obtain();

				msg.what = SCAN_FINISH;
				handler.sendMessage(msg);
			};
		}.start();
	}

	/*
	// 跳转到详细的页面
	public void detail(View view) {
		Intent intent = new Intent();
		intent.setClass(this, StaticDetectResult.class);
		startActivity(intent);
	}
	*/

}
