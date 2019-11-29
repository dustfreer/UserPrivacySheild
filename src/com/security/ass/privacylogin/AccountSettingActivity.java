package com.security.ass.privacylogin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



import com.security.ass.cryptogram.AES;


import com.security.ass.utils.SystemValue;
import com.security.ass.R;

public class AccountSettingActivity extends Activity {

	Spinner spinner;

	private PackageManager pm;
	private String selectedPackage;

	private TextView selectPackageView = null;
	private EditText accountEditText = null;
	private EditText passEditText = null;
	private Button encryptButton = null;
	private Button decryptButton = null;
	private Button getAccountButton = null; 

	private String encryptKeyString;
	private String ciphertext = null;
	private String plaintext = null;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_setting_activity);

		selectPackageView = (TextView) findViewById(R.id.select_packageName_textview);
		accountEditText = (EditText) findViewById(R.id.account_edittext);
		passEditText = (EditText) findViewById(R.id.pass_edittext);
		encryptButton = (Button) findViewById(R.id.encrypt_button);
		decryptButton = (Button) findViewById(R.id.decrypt_button);
		getAccountButton = (Button) findViewById(R.id.accountInfo_button);

		// 获取加密密钥
		encryptKeyString = getEncryptKey();

		// 获得应用包名
		pm = getPackageManager();
		List<PackageInfo> packageInfosList = pm
				.getInstalledPackages(PackageManager.GET_SIGNATURES);

		final String[] packageNames = new String[packageInfosList.size()];

		for (int i = 0; i < packageInfosList.size(); i++) {
			PackageInfo pk = packageInfosList.get(i);
			packageNames[i] = pk.packageName;
		}

		// 获取界面布局文件中的Spinner组件
		spinner = (Spinner) findViewById(R.id.spinner);

		// 创建ArrayAdapter对象
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, packageNames);
		// 为Spinner设置Adapter
		spinner.setAdapter(adapter);

		// 获得选取的包名
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				selectedPackage = packageNames[position];
				// 在textview上显示选取的包名
				selectPackageView.setText("选取的包名是：" + selectedPackage);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		
	}

	/**
	 * 主密钥+设备唯一值+包名 取哈希值作为加密密钥
	 */
	private String getEncryptKey() {
		
		
		String device_id = SystemValue.device_id;
		String sim_serial_number = SystemValue.sim_serial_number;
		String android_id = SystemValue.android_id;
		String serial_number = SystemValue.serial_number;
		String superKey = SystemValue.masterKey;
		
		

		Log.e("AccountSettingActivity", "device_id:" + device_id);
		Log.e("AccountSettingActivity", "sim_serial_number:" + sim_serial_number);
		Log.e("AccountSettingActivity", "android_id:" + android_id);
		Log.e("AccountSettingActivity", "serial_number:" + serial_number);
		Log.e("AccountSettingActivity", "superKey:" + superKey);

		String totalString = superKey + device_id + sim_serial_number
				+ android_id + serial_number;
		Log.e("AccountSettingActivity", "totalString:" + totalString);

		String getHashString = null;

		byte[] inputBytes = totalString.getBytes();

		// 获取sha-1散列值
		MessageDigest sha1;
		try {
			sha1 = MessageDigest.getInstance("MD5");
			sha1.reset();
			sha1.update(inputBytes);
			byte[] resultBytes = sha1.digest();

			String testString = null;
			// 将resultBytes转换成string
			for (int i = 0; i < resultBytes.length; i++) {
				testString = testString + Byte.toString(resultBytes[i]);
			}

			Log.e("AccountSettingActivity", "testString:" + testString);

			Log.e("AccountSettingActivity", "resultBytes.length:"
					+ resultBytes.length);

			String arrayString = new String(resultBytes);
			// arrayString = String.copyValueOf(arrayString.toCharArray(), 0,
			// resultBytes.length);

			getHashString = arrayString;

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.e("AccountSettingActivity", "getHashString:" + getHashString);

		return totalString;
	}

	/**
	 * 点击加密按钮，对账号和密码信息加密
	 * 
	 * @param view
	 */
	public void encryptAccount(View view) {
		String account = accountEditText.getText().toString();
		String pass = passEditText.getText().toString();

		// 加密方法是将 主密钥+设备唯一值+包名 取哈希值作为加密密钥
		// 用加密密钥对账号和密码进行加密 为了方便这里暂时使用AES加密

		String totalInfo = account + "|" + pass; // 加在一起，解密后

		//加密
		AES aes = new AES();
		ciphertext = aes.encrypt(encryptKeyString, totalInfo);
		
		
		Log.e("encryptAccount", "选取的包名是："+selectedPackage);
		
		
		//暂时将密文存储在SharedPreferences中
		Context con = AccountSettingActivity.this;
		SharedPreferences accountSP = con.getSharedPreferences("AccountInstored", MODE_PRIVATE);
		
		Editor editor = accountSP.edit();
		editor.putString(selectedPackage, ciphertext);
		editor.commit();
		
		Toast.makeText(getApplicationContext(), "存储成功!",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * 点击解密按钮，将密文解密
	 * 
	 * @param view
	 */
	public void decryptAccount(View view) {
		
		AES aes = new AES();

		try {
			Log.e("AccountSettingActivity", "1");
			plaintext = aes.decrypt("123456", ciphertext);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		selectPackageView.setText("解密明文是：" + plaintext);
		Log.e("AccountSettingActivity", "decryptAccount plaintext:" + plaintext);

	}
	
	/**
	 * 跳转到账号信息页面
	 */
	public void getAccountInfo(View view){
		Intent intent = new Intent(AccountSettingActivity.this,SelectPrivacyAccount.class);
		startActivity(intent);
	}
	
	

}
