/**
 * @author ZingQBo
 * @time 2014年12月15日下午5:07:46
 */
package com.hh.sdk.service;

import android.app.Activity;
import android.widget.Toast;
import com.hh.sdk.ICallback;
import com.hh.sdk.api.LogoutApi;
import com.hh.sdk.model.InitInfo;
import com.hh.sdk.net.JsonResponse;
import com.hh.sdk.net.NetTask;
import com.hh.sdk.platform.Iplatform;
import com.hh.sdk.util.PhoneInformation;
import com.hh.sdk.util.SDKUtils;
import org.json.JSONObject;

/**
 * @Description
 * @author ZengQBo
 * @time 2014年12月15日
 */
public class LogOutService extends BaseService{

	private Iplatform iplatform;
	private Activity mActivity;
	private ICallback callback;

	public interface GameLogoutListener {
		public void logoutSuccess();

		public void logoutFail(String value);
	}

	public LogOutService(Activity activity, Iplatform iplateform) {
		this.mActivity = activity;
		this.iplatform = iplateform;
	}

	public void logout(ICallback callback) {
		this.callback = callback;
		iplatform.logOut(mActivity,listener);

	}

	private void logout() {
		PhoneInformation phoneInformation = new PhoneInformation(mActivity);
		LogoutApi api = new LogoutApi();
		InitInfo initInfo = SDKUtils.getMeteData(mActivity);
		api.gameId = initInfo.gameId ;
		api.channelId = initInfo.chanelId ;
		api.merchantId = initInfo.merchantId ;
		api.uid= uid ;
		api.imei = phoneInformation.getDeviceCode() ;
		api.imsi = phoneInformation.getImsi();
		api.setResponse(jsonResponse);
		new NetTask().execute(api);
	}

	private JsonResponse jsonResponse = new JsonResponse() {
		@Override
		public void requestError(String string) {
			super.requestError(string);
			Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void requestSuccess(JSONObject jsonObject) {
			int code = jsonObject.optInt("code");
			if (code == 0) {
				callback.logoutSuccess();
			} else {
				callback.onError(ICallback.EXIT, jsonObject.toString());
			}
		}
	};

	private GameLogoutListener listener = new GameLogoutListener() {

		@Override
		public void logoutSuccess() {
			logout();
		}

		@Override
		public void logoutFail(String value) {
			callback.onError(ICallback.EXIT, value);
		}
	};
}
