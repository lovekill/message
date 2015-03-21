package com.hh.sdk.api;

import com.hh.sdk.net.StringRequest;

/**
 * Created by engine on 15/1/29.
 */
public class OrderStatusApi extends StringRequest {
    public String gameId ;
    public String merchantId ;
    public String channelId ;
    public String uid ;
    public String orderNos ;
    public String imei ;
    @Override
    public String getUrl() {
        return Url.BASE_URL+Url.ORDER_STATUS_URL;
    }
}
