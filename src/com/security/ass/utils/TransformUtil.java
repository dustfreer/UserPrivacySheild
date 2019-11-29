package com.security.ass.utils;

import android.util.Log;

/**
 * 该类主要做字符转换之类的处理
 * 
 * @author Gene
 *
 */
public class TransformUtil {

	// 将一个字符串转换成byte数组
	public static byte[] stringToByteArray(String string) {
		char[] tempChar = string.toCharArray();

		return null;
	}

	// 将一个byte数组转换成string字符串
	public static String byteArrayToString1(byte[] byteArray) {
		String arrayString = new String(byteArray);
		arrayString = String.copyValueOf(arrayString.toCharArray(), 0,
				byteArray.length);
		return arrayString;
	}

	// 将一个byte数组转换成string字符串
	public static String byteArrayToString(byte[] byteArray) {

		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			buff.append(Byte.toString(byteArray[i]));
		}
		return buff.toString();
	}

}
