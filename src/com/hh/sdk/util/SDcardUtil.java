package com.hh.sdk.util;

import java.io.File;

import android.os.Environment;

public class SDcardUtil {

	public static boolean isSDMounted() {
		String sdState = Environment.getExternalStorageState(); // 判断sd卡是否存在
		return sdState.equals(Environment.MEDIA_MOUNTED);
	}
	
	public static boolean isSDAvailable() {
		if (!isSDMounted())
			return false;

		File file = Environment.getExternalStorageDirectory();
		if (!file.canRead() || !file.canWrite())
			return false;

		return true;
	}
	
}
