/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.security.ass.pinyinime;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.IBinder;
import android.util.Log;

import com.security.ass.R;

/**
 * This class is used to separate the input method kernel in an individual
 * service so that both IME and IME-syncer can use it.
 * 
 * �ʿ����JNI��������
 * 
 * @ClassName PinyinDecoderService
 * @author keanbin
 */
public class PinyinDecoderService extends Service {
	native static boolean nativeImOpenDecoder(byte fn_sys_dict[],
			byte fn_usr_dict[]);

	/**
	 * JNI�������򏏽��뇒
	 * 
	 * @param fd
	 * @param startOffset
	 * @param length
	 * @param fn_usr_dict
	 * @return
	 */
	native static boolean nativeImOpenDecoderFd(FileDescriptor fd,
			long startOffset, long length, byte fn_usr_dict[]);

	/**
	 * JNI�������������ĳ���
	 * 
	 * @param maxSpsLen
	 * @param maxHzsLen
	 */
	native static void nativeImSetMaxLens(int maxSpsLen, int maxHzsLen);

	/**
	 * JNI�������رս�����
	 * 
	 * @return
	 */
	native static boolean nativeImCloseDecoder();

	/**
	 * JNI����������ƴ����ѯæ?��
	 * 
	 * @param pyBuf
	 * @param pyLen
	 * @return
	 */
	native static int nativeImSearch(byte pyBuf[], int pyLen);

	/**
	 * JNI������ɾ��ָ��λ�õ�ƴ������в���
	 * 
	 * @param pos
	 * @param is_pos_in_splid
	 * @param clear_fixed_this_step
	 * @return
	 */
	native static int nativeImDelSearch(int pos, boolean is_pos_in_splid,
			boolean clear_fixed_this_step);

	/**
	 * JNI����������ƴ����ѯ��Ӧ�������֮ǰ��ѯ������
	 */
	native static void nativeImResetSearch();

	/**
	 * JNI������������ĸ?
	 * 
	 * @��ע Ŀǰû��ʹ��?
	 * @param ch
	 * @return
	 */
	native static int nativeImAddLetter(byte ch);

	/**
	 * JNI��������ȡƴ���ַ���
	 * 
	 * @param decoded
	 * @return
	 */
	native static String nativeImGetPyStr(boolean decoded);

	/**
	 * JNI��������ȡƴ���ַ����ĳ���
	 * 
	 * @param decoded
	 * @return
	 */
	native static int nativeImGetPyStrLen(boolean decoded);

	/**
	 * JNI��������ȡÿ��ƴд�ď�ʼλ�ã��²⣺��د��Ԫ����ƴд�ĞS������
	 * 
	 * @return
	 */
	native static int[] nativeImGetSplStart();

	/**
	 * JNI��������ȡָ��λ�õĺ�?��
	 * 
	 * @param choiceId
	 * @return
	 */
	native static String nativeImGetChoice(int choiceId);

	/**
	 * JNI��������ȡæ?�ʵ����Y
	 * 
	 * @param choiceId
	 * @return
	 */
	native static int nativeImChoose(int choiceId);

	/**
	 * JNI������ȡ������ѡ��
	 * 
	 * @��ע Ŀǰû��ʹ��
	 * @return
	 */
	native static int nativeImCancelLastChoice();

	/**
	 * JNI��������ȡ�̶��ַ��ĳ���
	 * 
	 * @return
	 */
	native static int nativeImGetFixedLen();

	/**
	 * JNI������ȡ�����
	 * 
	 * @��ע Ŀǰû��ʹ��
	 * @return
	 */
	native static boolean nativeImCancelInput();

	/**
	 * JNI������ˢ�»��\
	 * 
	 * @��ע Ŀǰû��ʹ��
	 * @return
	 */
	native static boolean nativeImFlushCache();

	/**
	 * JNI�����������ַ��� fixedStr ��ȡԤ����æ?��
	 * 
	 * @param fixedStr
	 * @return
	 */
	native static int nativeImGetPredictsNum(String fixedStr);

	/**
	 * JNI��������ȡָ��λ�õ�Ԥ����?��
	 * 
	 * @param predictNo
	 * @return
	 */
	native static String nativeImGetPredictItem(int predictNo);

	// Sync related
	/**
	 * JNI������ͬ�����û��ʵ䣬�²⣺�ǲ��Ǽ�ס�û��ĳ��ô�?
	 * 
	 * @��ע Ŀǰû��ʹ��
	 * @param user_dict
	 * @param tomerge
	 * @return
	 */
	native static String nativeSyncUserDict(byte[] user_dict, String tomerge);

	/**
	 * JNI��������ʼ�û��ʵ�ͬ�{
	 * 
	 * @��ע Ŀǰû��ʹ��
	 * @param user_dict
	 * @return
	 */
	native static boolean nativeSyncBegin(byte[] user_dict);

	/**
	 * JNI������ͬ�����
	 * 
	 * @��ע Ŀǰû��ʹ��
	 * @return
	 */
	native static boolean nativeSyncFinish();

	/**
	 * JNI������ͬ����ȡLemmas
	 * 
	 * @��ע Ŀǰû��ʹ��
	 * @return
	 */
	native static String nativeSyncGetLemmas();

	/**
	 * JNI������ͬ������Lemmas
	 * 
	 * @��ע Ŀǰû��ʹ��
	 * @param tomerge
	 * @return
	 */
	native static int nativeSyncPutLemmas(String tomerge);

	/**
	 * JNI������ͬ����ȡ��������
	 * 
	 * @��ע Ŀǰû��ʹ��
	 * @return
	 */
	native static int nativeSyncGetLastCount();

	/**
	 * JNI������ͬ����ȡ�S���Y
	 * 
	 * @��ע Ŀǰû��ʹ��
	 * @return
	 */
	native static int nativeSyncGetTotalCount();

	/**
	 * JNI������ͬ���������
	 * 
	 * @��ע Ŀǰû��ʹ��
	 * @return
	 */
	native static boolean nativeSyncClearLastGot();

	/**
	 * JNI������ͬ����ȡ���Y
	 * 
	 * @��ע Ŀǰû��ʹ��
	 * @return
	 */
	native static int nativeSyncGetCapacity();

	/**
	 * �c����ļ�·������
	 */
	private final static int MAX_PATH_FILE_LENGTH = 100;

	/**
	 * �Ƿ���ɳ�ʼ��
	 */
	private static boolean inited = false;

	/**
	 * �û��Ĵʵ��ķ�
	 */
	private String mUsr_dict_file;

	// ���뱾�غ�����
	static {
		try {
			System.loadLibrary("jni_pinyinime");
		} catch (UnsatisfiedLinkError ule) {
			Log.e("PinyinDecoderService",
					"WARNING: Could not load jni_pinyinime natives");
		}
	}

	/**
	 * Get file name of the specified dictionary ��ȡ�û��ʵ���ļ���
	 * 
	 * @param usr_dict
	 * @return
	 */
	private boolean getUsrDictFileName(byte usr_dict[]) {
		if (null == usr_dict) {
			return false;
		}

		for (int i = 0; i < mUsr_dict_file.length(); i++)
			usr_dict[i] = (byte) mUsr_dict_file.charAt(i);
		usr_dict[mUsr_dict_file.length()] = 0;

		return true;
	}

	/**
	 * ��ʼ��ƴ�����`
	 */
	private void initPinyinEngine() {
		byte usr_dict[];
		usr_dict = new byte[MAX_PATH_FILE_LENGTH];

		// Here is how we open a built-in dictionary for access through
		// a file descriptor...
		// ��ȡ�ʿ� R.raw.dict_pinyin ���ļ�������
		AssetFileDescriptor afd = getResources().openRawResourceFd(
				R.raw.dict_pinyin);
		if (Environment.getInstance().needDebug()) {
			Log.i("foo", "Dict: start=" + afd.getStartOffset() + ", length="
					+ afd.getLength() + ", fd=" + afd.getParcelFileDescriptor());
		}
		if (getUsrDictFileName(usr_dict)) {
			// JNI�������򏏽��뇒
			inited = nativeImOpenDecoderFd(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getLength(), usr_dict);
		}
		try {
			afd.close();
		} catch (IOException e) {
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// ��ȡ�û��ʵ�"usr_dict.dat"��·��?"usr_dict.dat"����fileĿ¼��?
		// �²⣺����getFileStreamPath("usr_dict.dat")���疨"usr_dict.dat"�����ڣ������openFileOutput�����������ļ�?
		mUsr_dict_file = getFileStreamPath("usr_dict.dat").getPath();
		// This is a hack to make sure our "files" directory has been
		// created.
		try {
			openFileOutput("dummy", 0).close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		initPinyinEngine();
	}

	@Override
	public void onDestroy() {
		// JNI�������رս�����
		nativeImCloseDecoder();
		inited = false;
		super.onDestroy();
	}

	/**
	 * ���ⲿ���õĽӿ�
	 */
	private final IPinyinDecoderService.Stub mBinder = new IPinyinDecoderService.Stub() {

		/**
		 * ����12345
		 */
		public int getInt() {
			return 12345;
		}

		/**
		 * ���Öc��ĳ���
		 */
		public void setMaxLens(int maxSpsLen, int maxHzsLen) {
			nativeImSetMaxLens(maxSpsLen, maxHzsLen);
		}

		/**
		 * ����ƴ����ѯ��?��
		 */
		public int imSearch(byte[] pyBuf, int pyLen) {
			return nativeImSearch(pyBuf, pyLen);
		}

		/**
		 * ɾ��ָ��λ�õ�ƴ������в�ѯ
		 */
		public int imDelSearch(int pos, boolean is_pos_in_splid,
				boolean clear_fixed_this_step) {
			return nativeImDelSearch(pos, is_pos_in_splid,
					clear_fixed_this_step);
		}

		/**
		 * ����ƴ����ѯ��Ӧ�������֮ǰ��ѯ������
		 */
		public void imResetSearch() {
			nativeImResetSearch();
		}

		/**
		 * ������ĸ?
		 * 
		 * @��ע Ŀǰû��ʹ��?
		 */
		public int imAddLetter(byte ch) {
			return nativeImAddLetter(ch);
		}

		/**
		 * ��ȡƴ���ַ�د
		 */
		public String imGetPyStr(boolean decoded) {
			return nativeImGetPyStr(decoded);
		}

		/**
		 * ��ȡƴ���ַ����ĳ���
		 */
		public int imGetPyStrLen(boolean decoded) {
			return nativeImGetPyStrLen(decoded);
		}

		/**
		 * ��ȡÿ��ƴд�Ŀ�ʼλ�ã��²⣺��د��Ԫ����ƴд�ĞS������
		 */
		public int[] imGetSplStart() {
			return nativeImGetSplStart();
		}

		/**
		 * ��ȡָ��λ�õ�æ?��
		 */
		public String imGetChoice(int choiceId) {
			return nativeImGetChoice(choiceId);
		}

		/**
		 * ��ȡ�����?��
		 * 
		 * @��ע Ŀǰû��ʹ��?
		 */
		public String imGetChoices(int choicesNum) {
			String retStr = null;
			for (int i = 0; i < choicesNum; i++) {
				if (null == retStr)
					retStr = nativeImGetChoice(i);
				else
					retStr += " " + nativeImGetChoice(i);
			}
			return retStr;
		}

		/**
		 * ��ȡ��?���б���choicesStartλ�õ�æ?�ʴ�sentFixedLen��ʼ��ȡ?
		 */
		public List<String> imGetChoiceList(int choicesStart, int choicesNum,
				int sentFixedLen) {
			Vector<String> choiceList = new Vector<String>();
			for (int i = choicesStart; i < choicesStart + choicesNum; i++) {
				String retStr = nativeImGetChoice(i);
				if (0 == i)
					retStr = retStr.substring(sentFixedLen);
				choiceList.add(retStr);
			}
			return choiceList;
		}

		/**
		 * ��ȡ��?�ʵ����Y
		 */
		public int imChoose(int choiceId) {
			return nativeImChoose(choiceId);
		}

		/**
		 * ȡ���c���ѡ��
		 * 
		 * @��ע Ŀǰû��ʹ��
		 */
		public int imCancelLastChoice() {
			return nativeImCancelLastChoice();
		}

		/**
		 * ��ȡ�̶��ַ��ĳ���
		 */
		public int imGetFixedLen() {
			return nativeImGetFixedLen();
		}

		/**
		 * ȡ������
		 * 
		 * @��ע Ŀǰû��ʹ��
		 */
		public boolean imCancelInput() {
			return nativeImCancelInput();
		}

		/**
		 * ˢ�»���
		 * 
		 * @��ע Ŀǰû��ʹ��
		 */
		public void imFlushCache() {
			nativeImFlushCache();
		}

		/**
		 * �����ַ�د fixedStr ��ȡԤ����æ?��
		 */
		public int imGetPredictsNum(String fixedStr) {
			return nativeImGetPredictsNum(fixedStr);
		}

		/**
		 * ��ȡָ��λ�õ�Ԥ��æ?��
		 */
		public String imGetPredictItem(int predictNo) {
			return nativeImGetPredictItem(predictNo);
		}

		/**
		 * ��ȡ��?���б�
		 */
		public List<String> imGetPredictList(int predictsStart, int predictsNum) {
			Vector<String> predictList = new Vector<String>();
			for (int i = predictsStart; i < predictsStart + predictsNum; i++) {
				predictList.add(nativeImGetPredictItem(i));
			}
			return predictList;
		}

		/**
		 * ͬ�����û��ʵ䣬�²⣺�ǲ��Ǽ�ס�û��ĳ��ô�?
		 * 
		 * @��ע Ŀǰû��ʹ��
		 */
		public String syncUserDict(String tomerge) {
			byte usr_dict[];
			usr_dict = new byte[MAX_PATH_FILE_LENGTH];

			if (getUsrDictFileName(usr_dict)) {
				return nativeSyncUserDict(usr_dict, tomerge);
			}
			return null;
		}

		/**
		 * ��ʼ�û��ʵ�ͬ�{
		 * 
		 * @��ע Ŀǰû��ʹ��
		 */
		public boolean syncBegin() {
			byte usr_dict[];
			usr_dict = new byte[MAX_PATH_FILE_LENGTH];

			if (getUsrDictFileName(usr_dict)) {
				return nativeSyncBegin(usr_dict);
			}
			return false;
		}

		/**
		 * ͬ������
		 * 
		 * @��ע Ŀǰû��ʹ��
		 */
		public void syncFinish() {
			nativeSyncFinish();
		}

		/**
		 * ͬ������Lemmas
		 * 
		 * @��ע Ŀǰû��ʹ��
		 */
		public int syncPutLemmas(String tomerge) {
			return nativeSyncPutLemmas(tomerge);
		}

		/**
		 * ͬ����ȡLemmas
		 * 
		 * @��ע Ŀǰû��ʹ��
		 */
		public String syncGetLemmas() {
			return nativeSyncGetLemmas();
		}

		/**
		 * ͬ����ȡ�c�������
		 * 
		 * @��ע Ŀǰû��ʹ��
		 */
		public int syncGetLastCount() {
			return nativeSyncGetLastCount();
		}

		/**
		 * ͬ����ȡ�����Y
		 * 
		 * @��ע Ŀǰû��ʹ��
		 */
		public int syncGetTotalCount() {
			return nativeSyncGetTotalCount();
		}

		/**
		 * ͬ����Ֆc���
		 * 
		 * @��ע Ŀǰû��ʹ��
		 */
		public void syncClearLastGot() {
			nativeSyncClearLastGot();
		}

		/**
		 * ͬ����ȡ����
		 * 
		 * @��ע Ŀǰû��ʹ��
		 */
		public int imSyncGetCapacity() {
			return nativeSyncGetCapacity();
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
}