package com.security.ass.ui;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.security.ass.R;
import com.security.ass.utils.TransformUtil;

public class RegisterActivity extends Activity {

	private static EditText regEditText1 = null;
	private static EditText regEditText2 = null;
	private static Button regButton = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);

		regEditText1 = (EditText) findViewById(R.id.regPass1_edittext);
		regEditText2 = (EditText) findViewById(R.id.regPass2_edittext);
		regButton = (Button) findViewById(R.id.register_button);

	}

	// 点击注册按钮
	@SuppressWarnings("unused")
	public void registerOnClick(View view) {

		String firstInput = regEditText1.getText().toString();
		String secondInput = regEditText2.getText().toString();

		Log.e("Register", "firstInput:" + firstInput);
		Log.e("Register", "secondInput:" + secondInput);

		// 注册后，要将注册的口令 取散列值存储在SharedPreference中
		if (firstInput == null || secondInput == null) {
			Toast.makeText(getApplicationContext(), "please input!",
					Toast.LENGTH_SHORT).show();
		} else if (firstInput.equals(secondInput)) {

			Log.e("Register", "firstInput secondInput is not null!");

			byte[] inputBytes = firstInput.getBytes();

			for (int i = 0; i < inputBytes.length; i++) {
				Log.e("Register", "inputBytes:" + inputBytes[i]);
			}

			// 将字符数组转换成字符串
			String testString = new String(inputBytes);
			testString = String.copyValueOf(testString.toCharArray(), 0,
					inputBytes.length);

			Log.e("Register", "inputString:" + testString);

			// 使用sha-1散列算法
			MessageDigest sha1;
			try {
				Log.e("Register", "1");

				sha1 = MessageDigest.getInstance("MD5");
				sha1.reset();
				sha1.update(inputBytes);
				byte[] resultBytes = sha1.digest(); // 获得散列值

				String arrayString = new String(resultBytes);
				Log.e("Register", "2 arrayString:" + arrayString);
				arrayString = String.copyValueOf(arrayString.toCharArray(), 0,
						resultBytes.length);
				Log.e("Register", "4");

				String hashString = arrayString;

				Log.e("Register", "passHash:" + hashString);

				// 将散列值存储
				Context con = RegisterActivity.this;
				SharedPreferences sp = con.getSharedPreferences("SP",
						MODE_PRIVATE);

				Editor editor = sp.edit();
				editor.putBoolean("WhetherRegister", true);
				editor.putString("PassHash", hashString);
				editor.commit();

			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			Toast.makeText(getApplicationContext(), "puts are different!",
					Toast.LENGTH_SHORT).show();
		}
	}

}
