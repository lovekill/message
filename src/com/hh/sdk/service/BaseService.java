package com.hh.sdk.service;

import com.hh.sdk.model.Order;
import org.json.JSONObject;

/**
 * Created by engine on 15/1/29.
 */
public abstract class BaseService {
    public static final int SERVER_NOTIFY = 1 ;
    public static final int CLIENT_NOTIFY= 2 ;
    protected static String uid ;
    protected static int notifyType ;
    protected static int PAY_PLATEFORM;
    protected static final String CHANLE_KEY = "zd_channelId" ;
    protected static final String GAME_KEY= "zd_channelId" ;
    protected static final String MERCHANT_KEY= "zd_merchantId" ;
    protected int getSdkVersion(){
        return 1;
    }

    public Order getOrderFromJson(JSONObject jsonObject){
        if(jsonObject==null) return  null ;
        Order order = new Order() ;
        order.orderNo = jsonObject.optString("orderNo") ;
        order.status= jsonObject.optInt("status");
        order.giveOut = jsonObject.optInt("giveOut");
        order.cpOrderId = jsonObject.optString("cpOrderId") ;
        order.extInfo = jsonObject.optString("extInfo") ;
        order.amount = jsonObject.optInt("amount") ;
        return  order ;
    }
}
