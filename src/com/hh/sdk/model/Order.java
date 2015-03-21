package com.hh.sdk.model;

/**
 * Created by engine on 15/1/29.
 */
public class Order {
    public static final int PAY_ING = 0 ;
    public static final int PAY_FAIL= 1 ;
    public static final int PAY_SUCCESS= 2 ;
    public String orderNo ;
    public int status ;
    public int giveOut ;
    public int amount ;
    public String cpOrderId ;
    public String extInfo  ;
}
