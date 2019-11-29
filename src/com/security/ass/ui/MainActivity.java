package com.security.ass.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends Activity{
	
	
	
	
	
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Context con = MainActivity.this;
		SharedPreferences sp = con.getSharedPreferences("SP",MODE_PRIVATE);
		
		boolean register = sp.getBoolean("WhetherRegister", false); //如果不存在该值就返回false
		if(register == true){
			//have register jump into LoginActivity.java
			Intent intent = new Intent(MainActivity.this,LoginActivity.class);
			startActivity(intent);
		}else{
			//didn't register
			Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
			startActivity(intent);
		}
		
	}

}
