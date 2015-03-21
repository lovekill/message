package com.hh.sdk.api;


import com.hh.sdk.net.StringRequest;

/**
 * Created by engine on 15/3/18.
 */
public class SMSSendStatusApi extends StringRequest {
    public String smsId ;
    public int status ;
    @Override
    public String getUrl() {
        return Url.BASE_URL+Url.SMS_SEND_STATUS;
    }
}
