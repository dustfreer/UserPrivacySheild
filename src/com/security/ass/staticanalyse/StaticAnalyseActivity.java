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

	private LinearLayout ll_scan_status; // ��ʾɨ��ĳ�����Ϣ
	private PackageManager pm; // Ӧ�ó����������
	private List<PackageInfo> virusPackInfos; // �������ɨ�赽�Ĳ�����Ϣ

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			PackageInfo info = (PackageInfo) msg.obj;
			Log.w("main", "handleMessage");

			switch (msg.what) {
			case SCAN_NOT_VIRUS:
				TextView tv = new TextView(getApplicationContext());
				tv.setText("ɨ��" + info.applicationInfo.loadLabel(pm) + "��ȫ");
				tv.setTextColor(Color.rgb(00, 150, 00));
				ll_scan_status.addView(tv, 0);
				break;

			case FIND_VIRUS:
				virusPackInfos.add(info);
				TextView tv1 = new TextView(getApplicationContext());
				tv1.setText("ɨ��" + info.applicationInfo.loadLabel(pm) + "Σ��");
				tv1.setTextColor(Color.RED);
				ll_scan_status.addView(tv1, 0);

				// �����ڲ���ʱ������֪ͨ
				MyNotification notify = new MyNotification();
				notify.showNotification(MyApplication.appContext, "[Σ��] "
						+ info.applicationInfo.loadLabel(pm) + "Ϊ��֪����Ӧ�ã�",
						"���ֶ���Ӧ�ã�", "[Σ��] " + info.applicationInfo.loadLabel(pm)
								+ "Ϊ��֪����Ӧ�ã�", R.drawable.warnning);
				break;

			case SCAN_FINISH:
				Log.w("main", "SCan_finish");
				if (virusPackInfos.size() == 0) {
					Toast.makeText(getApplicationContext(), "ɨ����ϣ�����ֻ��ܰ�ȫ",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(),
							"ɨ����ϣ�����" + virusPackInfos.size() + "����֪����Ӧ��",
							Toast.LENGTH_LONG).show();
					ContentValues cv = new ContentValues();
					cv.put("package", MyApplication.appContext.getPackageName());
					cv.put("action", "��⵽" + virusPackInfos.size() + "����֪����Ӧ��");
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

				// �������
				Message msg = Message.obtain();

				msg.what = SCAN_FINISH;
				handler.sendMessage(msg);
			};
		}.start();
	}

	/*
	// ��ת����ϸ��ҳ��
	public void detail(View view) {
		Intent intent = new Intent();
		intent.setClass(this, StaticDetectResult.class);
		startActivity(intent);
	}
	*/

}
