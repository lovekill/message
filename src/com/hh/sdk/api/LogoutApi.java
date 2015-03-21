/**
 * @author ZingQBo
 * @time 2014年12月15日下午5:21:09
 */
package com.hh.sdk.api;

import com.hh.sdk.net.HttpEngine;
import com.hh.sdk.net.StringRequest;

/**
 * @Description 
 * @author ZingQBo
 * @time 2014年12月15日
 */
public class LogoutApi extends StringRequest {
	
	public String gameId;
	public String merchantId;
	public String channelId;
	public String uid;
	public String imei;
	public String imsi ;


	/* (non-Javadoc)
	 * @see com.zhidian.cpaSDK.net.StringRequest#getUrl()
	 */
	@Override
	public String getUrl() {
		return Url.BASE_URL + Url.LOGOUT_URL;
	}

	@Override
	public HttpEngine.Method getMethod() {
		return HttpEngine.Method.POST;
	}
}
