package com.hh.sdk.api;

import com.hh.sdk.net.HttpEngine;
import com.hh.sdk.net.StringRequest;

/**
 * @Description
 * @author ZingQBo
 * @time 2014年12月15日
 */
public class LoginApi extends StringRequest {
	public String gameId;
	public String merchantId;
	public String channelId;
	public String uid;
	public String imei;
	public String imsi;

	@Override
	public String getUrl() {
		return Url.BASE_URL + Url.LOGIN_URL;
	}

	@Override
	public HttpEngine.Method getMethod() {
		return HttpEngine.Method.POST;
	}

}
