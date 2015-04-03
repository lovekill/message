package com.hh.sdk;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hh.sdk.platform.Iplatform;
import com.hh.sdk.service.InitService;
import com.hh.sdk.service.LogOutService;
import com.hh.sdk.service.LoginService;

/**
 * Created by Administrator on 2014/12/11.
 */
public class HHSDK {
    private static HHSDK instance;
    private Activity mActivity;
    public static Iplatform iplateform;
    private ICallback mCallBack;
    private InitService mInitService ;

    private HHSDK(Activity activity, ICallback callback) {
        this.mActivity = activity;
        this.mCallBack = callback;
        // 初始化 plateform
    }

    public static HHSDK getInstance(Activity activity, ICallback mCallBack) {
        if (instance == null) {
            instance = new HHSDK(activity, mCallBack);
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init() {
        Log.e("ZDSDK","init");
        mInitService = new InitService(mActivity,iplateform) ;
        mInitService.init(mCallBack);
//        new InitService(mActivity, iplateform).init(mCallBack);
    }

    private LocationClient mLocationClient;
    /**
     * 维度
     */
    private double mLatitude = -1;
    /**
     * 经度
     */
    private double mLongitude = -1;
    /**
     * 省份
     */
    private String mProvince = "";

    private BDLocationListener mListener;

    public void initLocation() {
        mLocationClient = new LocationClient(mActivity);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false); // 打开gps
        option.setAddrType("all");
        option.setPriority(LocationClientOption.NetWorkFirst); // 设置网络优先
        option.setProdName("支付SDK"); // 设置产品线名称
        mLocationClient.setLocOption(option);
        mLocationClient
                .registerLocationListener(mListener = new BDLocationListener() {

                    @Override
                    public void onReceivePoi(BDLocation loc) {
                    }

                    @Override
                    public void onReceiveLocation(BDLocation loc) {
                        Log.e("", "latitude = " + loc.getLatitude()
                                + "  longitude = " + loc.getLongitude());
                        Log.e("", "province = " + loc.getProvince());
                        mProvince = loc.getProvince();
                        mLatitude = loc.getLatitude();
                        mLongitude = loc.getLongitude();
                    }
                });
        mLocationClient.start();// 将开启与获取位置分开，就可以尽量的在后面的使用中获取到位置
        mLocationClient.requestLocation();
    }

    /**
     * 登录
     */
    public void login(Activity activity, String uid) {
        Log.e("ZDSDK","login");
        if (iplateform != null) {
            new LoginService(activity, iplateform).login(uid, mCallBack);
        } else {
            Toast.makeText(activity, "初始化没有成功不能登陆！", Toast.LENGTH_SHORT).show();
        }
    }

    ;


    /**
     * 注销
     */
    public void logOut(Activity activity) {
        if (iplateform != null) {
            new LogOutService(activity, iplateform).logout(mCallBack);
        } else {
            mCallBack.logoutSuccess();
        }
    }

    ;


    /**
     * 支付接口
     *
     * @param money     充值金额
     * @param cpOrderId CP订单号
     * @param extInfo   自定义参数
     * @param callback  回调
     */
    public void doPay(int money, String cpOrderId,String productName,
                      String extInfo,  ICallback callback) {
        iplateform.pay(mActivity,money,cpOrderId,extInfo,callback);
    }

    ;

    /**
     * 暂停
     */
    public void onPause() {
        if (iplateform != null) {
            iplateform.onPause();
        }
    }

    public void onResume() {
        if (iplateform != null) {
            iplateform.onResume();
        }
    }

    /**
     * 销毁
     */
    public void onDestory() {
        if (iplateform != null) {
            iplateform.onDestory();
            mInitService.destroy();
        }
    }
}
