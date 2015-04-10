package com.hh.sdk.model;

import java.util.List;

/**
 * Created by engine on 15/3/17.
 */
public class SMSSendModel {
    public String smsId;// String 短信标识
    public String orderId ;
    public String port;//String 发送端口号
    public String sms;//String扣费指令
    public int money;// Integer金额，单位为分
    public List<String> interceptPorts;// List<String> 需要拦截的短信端口列表
    public String regexp;// String 下行扣费成功短信关键字匹配正则表达式
}
