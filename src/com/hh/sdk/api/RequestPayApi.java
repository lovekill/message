package com.hh.sdk.api;


import com.hh.sdk.net.StringRequest;

/**
 * Created by engine on 15/3/17.
 */
public class RequestPayApi extends StringRequest {
    public String gameId;
    public String region ;
    public int money ;
    public String imsi ;
    public String imei ;
    public String latitude ;
    public String longitude ;
    public String channelId ;
    @Override
    public String getUrl() {
        return Url.BASE_URL+Url.REQUEST_PAY;
    }
}
