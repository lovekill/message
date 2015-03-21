package com.hh.sdk.api;

import com.hh.sdk.net.StringRequest;

/**
 * Created by engine on 15/1/30.
 */
public class NotifyServerApi extends StringRequest {
    public String  gameId ;
    public String  merchantId ;
    public String  channelId ;
    public String  uid ;
    public String  imei ;
    public String  orderNo ;
    public int amount ;
    public int status ;
    @Override
    public String getUrl() {
        return Url.BASE_URL+Url.ORDER_NOTIFY_URL;
    }
}
