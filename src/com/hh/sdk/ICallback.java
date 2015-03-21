package com.hh.sdk;

/**
 * Created by Administrator on 2014/12/11.
 */
public interface ICallback {
    public static final int INIT = 1 ;
    public static final int LOGIN = 2 ;
    public static final int PAY = 3 ;
    public static final int EXIT = 4 ;
    public void initSuccess() ;
    public void loginSuccess(String uid) ;
    public void logoutSuccess() ;
    public void paySuccess(String orderid,int amount) ;
    public void payFail(String orderId,int amount) ;
    public void onError(int type,String message) ;
}
