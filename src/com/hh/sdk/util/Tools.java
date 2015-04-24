package com.hh.sdk.util;

import java.io.IOException;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Environment;
import android.util.Log;

public class Tools {
	private static final String tag = "[Tools]";
	
	private static final String META_DATA_AppId = "ZMAppId";
	
	public static boolean isPortrait(Context context) {
		Configuration config = context.getResources().getConfiguration();
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return false;
		}
		return true;
	}
	
	public static String getMerchantId() {
		
		String merchantid = null;

		if (SDcardUtil.isSDAvailable()) {
			String filename = Environment.getExternalStorageDirectory()
					+ "/zhimeng123.evn";
			try {
				IniReader reader;
				reader = new IniReader(filename);
				merchantid = reader.getValue("Usr", "merchantid");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				//Log.w(tag, "getMerchantId error", e);
			}
		}
		return merchantid;
	}


	public static String getMerchantPwd() {
		
		String merchantpwd = null;

		if (SDcardUtil.isSDAvailable()) {
			String filename = Environment.getExternalStorageDirectory()
					+ "/zhimeng123.evn";
			try {
				IniReader reader;
				reader = new IniReader(filename);
				merchantpwd = reader.getValue("Usr", "merchantpwd");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				//Log.w(tag, "getMerchantPwd error", e);
			}
		}

		if (merchantpwd == null || merchantpwd.trim().equals("")) {
			merchantpwd = "zz$r0oiljy";
		}

		return merchantpwd;
	}
	
	
	public static boolean isTestEnvironment() {
		
		String merchantpwd = null;

		if (SDcardUtil.isSDAvailable()) {
			String filename = Environment.getExternalStorageDirectory()
					+ "/zhimeng123.evn";
			try {
				IniReader reader;
				reader = new IniReader(filename);
				merchantpwd = reader.getValue("Env", "url_prefix");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				//Log.w(tag, "get test url error", e);
			}
		}

		//if not password, we consider it's work environment
		if (merchantpwd == null || merchantpwd.trim().equals("")) {
			return false;
		}		

		return true;
	}
	
	public static String getAppId(Context context) {
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			String appidStr = appInfo.metaData.get(META_DATA_AppId).toString();
			int appid = Integer.parseInt(appidStr);
			Log.i(tag, "getAppId = " + appid);
			return appid + "";
		} catch (Exception e) {
			Log.w(tag, "getAppId error", e);
		}
		return "-1";
	}
}
