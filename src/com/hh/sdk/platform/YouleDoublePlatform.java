package com.hh.sdk.platform;

import android.app.Activity;
import com.hh.sdk.model.InitInfo;
import com.hh.sdk.model.LocationInfo;
import com.hh.sdk.service.*;
import com.hh.sdk.util.PhoneInformation;
import com.hh.sdk.util.SDKUtils;

/**
 * Created by engine on 15/3/18.
 */
public class YouleDoublePlatform implements Iplatform {
    private PayService mPayService ;
    PhoneInformation phoneInformation ;
    private String province ;
    @Override
    public void init(Activity mActivity, InitService.GameInitListener gameInitListener) {
       gameInitListener.initSuccess();
        mPayService = PayService.getInstance(mActivity);
        phoneInformation = new PhoneInformation(mActivity);
    }

    @Override
    public void login(Activity activity, String uid, LoginService.GameLoginListener gameLoginListener) {
        gameLoginListener.LoginSuccess(null);
    }

    @Override
    public void logOut(Activity activity, LogOutService.GameLogoutListener gameLogoutListener) {
        gameLogoutListener.logoutSuccess();
    }

    @Override
    public void pay(Activity activity, int money, final String order, String extInfo, final OrderGenerateService.OrderGenerateListener listener) {
        InitInfo initInfo = SDKUtils.getMeteData(activity);
        mPayService.requestPay( money, phoneInformation.getDeviceCode(), phoneInformation.getImsi(), province, new PayService.IPayListener() {
            @Override
            public void paySuccess(int money) {
               listener.onSuccess(order,money);
            }

            @Override
            public void payCancel(int money) {
            }

            @Override
            public void payFail(int money) {
                listener.onFail(order,money+"");

            }
        });
    }

    @Override
    public void setLocation(LocationInfo info) {
        mPayService.setLatitued(info.latitude);
        mPayService.setLongtitued(info.longitude);
        this.province = info.province ;
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestory() {
        mPayService.onDestroy();
    }

    @Override
    public void onResume() {

    }
}
