package com.security.ass.ui;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.security.ass.R;
import com.security.ass.utils.SystemValue;
import com.security.ass.utils.TransformUtil;

/**
 * ͨ��SharedPerferences
 * @author Gene
 *
 */
public class LoginActivity extends Activity{
	
	//ҳ��ؼ�����
	private static EditText inputPassEditText = null; 
	private static Button loginButton = null;
	
	public static String masterKey = null;
	
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		
		//ҳ��ؼ���ʼ��
		inputPassEditText = (EditText) findViewById(R.id.inputpass_edittext);
		loginButton = (Button) findViewById(R.id.login_button);
		
		
		
	}
	
	//�����½��ťʱִ��
	@SuppressWarnings("unused")
	public void loginOnClick(View view){
		String inputPass = inputPassEditText.getText().toString();
		Context con = LoginActivity.this;
		SharedPreferences sp = con.getSharedPreferences("SP", MODE_PRIVATE);
		String origPassHash = sp.getString("PassHash", null);
		
		Log.e("LoginActivity", "origPassHash:"+origPassHash);
		
		if(inputPass == null){
			Toast.makeText(getApplicationContext(), "please input!",
					Toast.LENGTH_SHORT).show();
		}else{
			byte[] inputBytes = inputPass.getBytes();
			
			//��ȡsha-1ɢ��ֵ
			MessageDigest sha1;
			try {
				sha1 = MessageDigest.getInstance("MD5");
				sha1.reset();
				sha1.update(inputBytes);
				byte[] resultBytes = sha1.digest();
				
				String arrayString = new String(resultBytes);
				arrayString = String.copyValueOf(arrayString.toCharArray(), 0, resultBytes.length);
				
				String resultString = arrayString;
				
				Log.e("LoginActivity", "inputPassHash:"+resultString);
				
				//ɢ��ֵƥ����ȷ
				if(origPassHash.equals(resultString)){
					//��������������ʽ�����ڴ���
					masterKey = inputPass;
					SystemValue.masterKey = masterKey;
				
					//��ת����½�ɹ�ҳ��
					Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(getApplicationContext(), "error!",
							Toast.LENGTH_SHORT).show();
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	

}
