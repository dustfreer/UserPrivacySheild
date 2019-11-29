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

		// ��ȡ������Կ
		encryptKeyString = getEncryptKey();

		// ���Ӧ�ð���
		pm = getPackageManager();
		List<PackageInfo> packageInfosList = pm
				.getInstalledPackages(PackageManager.GET_SIGNATURES);

		final String[] packageNames = new String[packageInfosList.size()];

		for (int i = 0; i < packageInfosList.size(); i++) {
			PackageInfo pk = packageInfosList.get(i);
			packageNames[i] = pk.packageName;
		}

		// ��ȡ���沼���ļ��е�Spinner���
		spinner = (Spinner) findViewById(R.id.spinner);

		// ����ArrayAdapter����
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, packageNames);
		// ΪSpinner����Adapter
		spinner.setAdapter(adapter);

		// ���ѡȡ�İ���
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				selectedPackage = packageNames[position];
				// ��textview����ʾѡȡ�İ���
				selectPackageView.setText("ѡȡ�İ����ǣ�" + selectedPackage);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		
	}

	/**
	 * ����Կ+�豸Ψһֵ+���� ȡ��ϣֵ��Ϊ������Կ
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

		// ��ȡsha-1ɢ��ֵ
		MessageDigest sha1;
		try {
			sha1 = MessageDigest.getInstance("MD5");
			sha1.reset();
			sha1.update(inputBytes);
			byte[] resultBytes = sha1.digest();

			String testString = null;
			// ��resultBytesת����string
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
	 * ������ܰ�ť�����˺ź�������Ϣ����
	 * 
	 * @param view
	 */
	public void encryptAccount(View view) {
		String account = accountEditText.getText().toString();
		String pass = passEditText.getText().toString();

		// ���ܷ����ǽ� ����Կ+�豸Ψһֵ+���� ȡ��ϣֵ��Ϊ������Կ
		// �ü�����Կ���˺ź�������м��� Ϊ�˷���������ʱʹ��AES����

		String totalInfo = account + "|" + pass; // ����һ�𣬽��ܺ�

		//����
		AES aes = new AES();
		ciphertext = aes.encrypt(encryptKeyString, totalInfo);
		
		
		Log.e("encryptAccount", "ѡȡ�İ����ǣ�"+selectedPackage);
		
		
		//��ʱ�����Ĵ洢��SharedPreferences��
		Context con = AccountSettingActivity.this;
		SharedPreferences accountSP = con.getSharedPreferences("AccountInstored", MODE_PRIVATE);
		
		Editor editor = accountSP.edit();
		editor.putString(selectedPackage, ciphertext);
		editor.commit();
		
		Toast.makeText(getApplicationContext(), "�洢�ɹ�!",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * ������ܰ�ť�������Ľ���
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

		selectPackageView.setText("���������ǣ�" + plaintext);
		Log.e("AccountSettingActivity", "decryptAccount plaintext:" + plaintext);

	}
	
	/**
	 * ��ת���˺���Ϣҳ��
	 */
	public void getAccountInfo(View view){
		Intent intent = new Intent(AccountSettingActivity.this,SelectPrivacyAccount.class);
		startActivity(intent);
	}
	
	

}
