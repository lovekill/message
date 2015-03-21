package com.hh.sdk.api;

import com.hh.sdk.net.HttpEngine;
import com.hh.sdk.net.StringRequest;

/**
 * Created by Administrator on 2014/12/11.
 */
public class InitApi extends StringRequest {
    public String gameId;
    public String channelId;
    public String merchantId ;
    public String imei ;
    public String manufacturer ;
    public String model ;
    public String systemVersion ;
    public String platform;
    public String latitude ;
    public String longitude ;
    public String imsi ;
    public String location ;
    public String networkCountryIso ;
    public String networkType ;
    public String phonetype ;
    public String simoperatorname ;
    public String resolution ;
    public int sdkVersion ;
    @Override
    public String getUrl() {
        return Url.BASE_URL + Url.INIT_URL;
    }

    @Override
    public HttpEngine.Method getMethod() {
        return HttpEngine.Method.GET;
    }
}
