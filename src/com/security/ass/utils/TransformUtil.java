package com.security.ass.utils;

import android.util.Log;

/**
 * ������Ҫ���ַ�ת��֮��Ĵ���
 * 
 * @author Gene
 *
 */
public class TransformUtil {

	// ��һ���ַ���ת����byte����
	public static byte[] stringToByteArray(String string) {
		char[] tempChar = string.toCharArray();

		return null;
	}

	// ��һ��byte����ת����string�ַ���
	public static String byteArrayToString1(byte[] byteArray) {
		String arrayString = new String(byteArray);
		arrayString = String.copyValueOf(arrayString.toCharArray(), 0,
				byteArray.length);
		return arrayString;
	}

	// ��һ��byte����ת����string�ַ���
	public static String byteArrayToString(byte[] byteArray) {

		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			buff.append(Byte.toString(byteArray[i]));
		}
		return buff.toString();
	}

}
