package com.hh.sdk.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

import com.hh.sdk.model.InitInfo;


public class SDKUtils {
	public static InitInfo getMeteData(Context context) {
		ApplicationInfo info;
		InitInfo model = new InitInfo();
		try {
			info = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			String gameId= info.metaData.getString("zmappid");
			String chanelId= info.metaData.getString("zmchanelId");
			if (gameId!= null & gameId.startsWith("zmappid:")) {
				model.gameId=gameId.split(":")[1];
			}

			if (chanelId!= null & chanelId.startsWith("zmchanelId:")) {
				model.chanelId=chanelId.split(":")[1];
			}

			return model;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}
	
    public static String getAppId(Context context) {
       ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            String gameId = info.metaData.getString("gameId");
        	if (gameId.startsWith("gameId:")) {
        		gameId = gameId.split(":")[1];
        	}
            return gameId;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

	public static String getMeteDate(Context context,String key) {
		ApplicationInfo info;
		try {
			info = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			String gameId = info.metaData.getString(key);
			if (gameId.startsWith(key+":")) {
				gameId = gameId.split(":")[1];
			}
			return gameId;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
    public static String getSKCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


}
