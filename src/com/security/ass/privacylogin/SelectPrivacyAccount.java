package com.security.ass.privacylogin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.security.ass.R;
import com.security.ass.cryptogram.AES;
import com.security.ass.ui.SwipeMenu;
import com.security.ass.ui.SwipeMenuCreator;
import com.security.ass.ui.SwipeMenuItem;
import com.security.ass.ui.SwipeMenuListView;
import com.security.ass.ui.SwipeMenuListView.OnMenuItemClickListener;
import com.security.ass.ui.SwipeMenuListView.OnSwipeListener;


public class SelectPrivacyAccount extends Activity {

	private List<ApplicationInfo> mAppList;
	private AppAdapter mAdapter;
	private Map<String, String> accountInfoMap = new HashMap<String, String>();
	private String[] packageNames = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_privacy_account);
		
		mAppList = new ArrayList<ApplicationInfo>();

		// mAppList = getPackageManager().getInstalledApplications(0);

		// 从shearedPreference中获取账号的包名,将包名信息赋值给packageNames
		accountInfoMap = getAccountEncryptInfoFromSP();
		packageNames = new String[accountInfoMap.size()];
		int i = 0;
		for (Map.Entry<String, String> entry : accountInfoMap.entrySet()) {
			packageNames[i] = entry.getKey();
			i++;
		}
		
		PackageManager pm = getPackageManager();

		// 通过包名获取程序信息，然后将程序信息添加到mAppList中
		for (String pkName : packageNames) {
			Log.e("TestSelectAccountActivity", "packageName:" + pkName);
			try {
				ApplicationInfo item = pm.getApplicationInfo(pkName, 0);
				mAppList.add(item);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.e("TestSelectAccountActivity", "mAppList.size():" + mAppList.size());

		SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);
		mAdapter = new AppAdapter();
		listView.setAdapter(mAdapter);

		// step 1. create a MenuCreator item向左滑动时候出现的按钮选项
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				// set item width
				openItem.setWidth(dp2px(90));
				// set item title
				openItem.setTitle("Open");
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setTitle("Copy");
				deleteItem.setTitleSize(18);
				deleteItem.setTitleColor(Color.BLACK);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		listView.setMenuCreator(creator);

		// step 2. listener item click event 两个按钮选项点击后的效果
		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				ApplicationInfo item = mAppList.get(position);
				switch (index) {
				case 0:
					// open
					open(item);
					break;
				case 1:

					copyAccountInfo(item);

					// delete
					// delete(item);
					// mAppList.remove(position);
					// mAdapter.notifyDataSetChanged();

					break;
				}
			}
		});

		// set SwipeListener
		listView.setOnSwipeListener(new OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});

		// other setting
		// listView.setCloseInterpolator(new BounceInterpolator());

		// 此方法暂时不用
		// test item long click
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(),
						position + " long click", 0).show();
				return false;
			}
		});

	}

	/**
	 * 从SharedPreferences中获取账号加密信息的map
	 * 
	 * @return
	 */
	private Map<String, String> getAccountEncryptInfoFromSP() {
		Map<String, String> mapInfo = new HashMap<String, String>();
		Context con = SelectPrivacyAccount.this;
		SharedPreferences accountSP = con.getSharedPreferences(
				"AccountInstored", MODE_PRIVATE);
		mapInfo = (Map<String, String>) accountSP.getAll();
		return mapInfo;
	}

	/**
	 * 通过item获取包名，由包名获取map中加密的账号信息，再对账号信息解密，解密后发送广播
	 */
	private void copyAccountInfo(ApplicationInfo item) {
		String account = null;
		String pass = null;
		String packageName = item.packageName;
		String encryptInfo = accountInfoMap.get(packageName);
		AES aes = new AES();
		String decryptInfo = aes.decrypt(" ", encryptInfo);
		String[] tempStringArray = decryptInfo.split("\\|");

		account = tempStringArray[0];
		pass = tempStringArray[1];

		// 将账号和口令以广播的形式发送出去
		Intent accountIntent = new Intent();
		accountIntent.setAction("android.intent.receiver.account");
		accountIntent.putExtra("account", account);
		sendBroadcast(accountIntent);
		
		Log.e("TestSelectAccountActivity", "发送账号广播信息是:" + account);

		Intent passIntent = new Intent();
		passIntent.setAction("android.intent.receiver.password");
		passIntent.putExtra("password", pass);
		sendBroadcast(passIntent);
		
		Log.e("TestSelectAccountActivity", "发送口令广播信息是:" + pass);

	}

	private void delete(ApplicationInfo item) {
		// delete app
		try {
			Intent intent = new Intent(Intent.ACTION_DELETE);
			intent.setData(Uri.fromParts("package", item.packageName, null));
			startActivity(intent);
		} catch (Exception e) {
		}
	}

	/**
	 * 此方法暂时不用
	 * 
	 * @param item
	 */
	private void open(ApplicationInfo item) {
		// open app
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(item.packageName);
		List<ResolveInfo> resolveInfoList = getPackageManager()
				.queryIntentActivities(resolveIntent, 0);
		if (resolveInfoList != null && resolveInfoList.size() > 0) {
			ResolveInfo resolveInfo = resolveInfoList.get(0);
			String activityPackageName = resolveInfo.activityInfo.packageName;
			String className = resolveInfo.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName componentName = new ComponentName(
					activityPackageName, className);

			intent.setComponent(componentName);
			startActivity(intent);
		}
	}

	/**
	 * 在此Adapter中设置application
	 * 
	 * @author Gene
	 *
	 */
	class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mAppList.size();
		}

		@Override
		public ApplicationInfo getItem(int position) {
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		// 在此处设置每一个listItem显示的内容
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_list_app, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			ApplicationInfo item = getItem(position);
			holder.iv_icon.setImageDrawable(item.loadIcon(getPackageManager()));
			holder.tv_name.setText(item.loadLabel(getPackageManager()));
			return convertView;
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView tv_name;

			public ViewHolder(View view) {
				iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				tv_name = (TextView) view.findViewById(R.id.tv_name);
				view.setTag(this);
			}
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}

