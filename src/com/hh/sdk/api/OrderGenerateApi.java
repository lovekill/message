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
public class OrderGenerateApi extends StringRequest {
	
	public String uid;
	public String gameId;
	public String merchantId;
	public String channelId;
	public int sdkVersion;
	public int amount;
	public String cpOrderId;
	public String extInfo;
	public String imei;
	public String imsi;
	public String goodsName ;

	/* (non-Javadoc)
	 * @see com.zhidian.cpaSDK.net.StringRequest#getUrl()
	 */
	@Override
	public String getUrl() {
		return Url.BASE_URL + Url.ORDER_GENERATE_URL;
	}

	@Override
	public HttpEngine.Method getMethod() {
		return HttpEngine.Method.POST;
	}
}
