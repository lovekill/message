package com.hh.sdk.api;

/**
 * Created by Administrator on 2014/12/11.
 */
public class Url {
	public static String BASE_URL= "http://120.24.237.68:8888/Nozzle/" ;
    /**
     * 初始化URL
     */
    public static String  INIT_URL = "gameInit" ;
    /**
     * 登录URL
     */
    public static final String LOGIN_URL = "accountLogin";
    /**
     * 帐号注销URL
     */
    public static final String LOGOUT_URL = "accountLogout";
    /**
     * 生成订单URL
     */
    public static final String ORDER_GENERATE_URL = "standalone/order/generate";
    /**
     * 查询订单 URL
     */
    public static final String ORDER_STATUS_URL= "orderQuery";
    /**
     * 通知订单URL
     */
    public static final String ORDER_NOTIFY_URL= "orderNotify";

    //请求支付接口

    public static final String  REQUEST_PAY = "payMent";

    //发送短信状态
    public static final String SMS_SEND_STATUS="smsStatus" ;
    //拦截短信发送到服务端
    public static final String INTECEPTOR_SMS="smsIntercept";
}
