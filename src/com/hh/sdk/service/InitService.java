package com.hh.sdk.service;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hh.sdk.ICallback;
import com.hh.sdk.HHSDK;
import com.hh.sdk.api.InitApi;
import com.hh.sdk.model.InitInfo;
import com.hh.sdk.model.LocationInfo;
import com.hh.sdk.net.JsonResponse;
import com.hh.sdk.net.NetTask;
import com.hh.sdk.platform.Iplatform;
import com.hh.sdk.platform.YouleDoublePlatform;
import com.hh.sdk.util.PhoneInformation;
import com.hh.sdk.util.SDKUtils;
import org.json.JSONObject;

/**
 * Created by Administrator on 2014/12/11.
 */
public class InitService extends BaseService {
    private static final String TAG = "InitService";
    private Iplatform iplatform;
    private Activity mActivity;
    private ICallback callback;
    public static String location = "";
    public static String latitued = "";
    public static String lontitued = "";

    public interface GameInitListener {
        public void initSuccess();

        public void initFail(String value);
    }

    public InitService(Activity activity, Iplatform iplateform) {
        this.mActivity = activity;
        this.iplatform = iplateform;
        // 获取地理位置信息
        //	new InitLBS().initLBS(activity);

    }

    public void init(ICallback callback) {
        this.callback = callback;
        initSDKServer();
    }

    private JsonResponse jsonResponse = new JsonResponse() {
        @Override
        public void requestError(String string) {
            super.requestError(string);
            Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void requestSuccess(JSONObject jsonObject) {
            Toast.makeText(mActivity, jsonObject.toString(), Toast.LENGTH_SHORT).show();
            int code = jsonObject.optInt("code");
            if (code == 0) {
                iplatform = new YouleDoublePlatform();
                HHSDK.iplateform = iplatform;
                if (iplatform != null) {
                    initPlatform();
                } else {
                    Toast.makeText(mActivity, "初始化平台失败，没有找到支付方式", Toast.LENGTH_SHORT).show();
                }
            } else {
                callback.onError(ICallback.INIT, jsonObject.toString());
            }
        }
    };

    private GameInitListener gameInitListener = new GameInitListener() {
        @Override
        public void initSuccess() {
            initLocation();
            callback.initSuccess();
        }

        @Override
        public void initFail(String value) {
            callback.onError(ICallback.INIT, value);
        }
    };

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
                        LocationInfo info = new LocationInfo();
                        info.province = mProvince;
                        info.latitude = mLatitude;
                        info.longitude = mLongitude;
                        HHSDK.iplateform.setLocation(info);
                    }
                });
        mLocationClient.start();// 将开启与获取位置分开，就可以尽量的在后面的使用中获取到位置
        mLocationClient.requestLocation();
    }

    private void initPlatform() {
        iplatform.init(mActivity, gameInitListener);
    }

    private void initSDKServer() {
        Log.e(TAG, "======= initSDKServer =======");// FIXME
        PhoneInformation phoneInformation = new PhoneInformation(mActivity);
        InitApi api = new InitApi();
        InitInfo initInfo = SDKUtils.getMeteData(mActivity);
        api.gameId = initInfo.gameId;
        api.channelId = initInfo.chanelId;
        api.merchantId = initInfo.merchantId;
        api.imei = phoneInformation.getDeviceCode();
        api.imsi = phoneInformation.getImsi();
        api.latitude = latitued;
        api.longitude = lontitued;
        api.location = location;
        api.manufacturer = phoneInformation.getBrand();
        api.model = phoneInformation.getPhoneModel();
        api.networkCountryIso = phoneInformation.getNetworkCountryIso();
        api.phonetype = phoneInformation.getPhoneType();
        api.networkType = phoneInformation.getNetworkType();
        api.platform = phoneInformation.getReleaseVersion();
        api.resolution = phoneInformation.getResolution();
        api.simoperatorname = phoneInformation.getSimOperatorName();
        api.systemVersion = phoneInformation.getSdkVersion();
        api.sdkVersion = getSdkVersion();
        api.setResponse(jsonResponse);
        new NetTask().execute(api);
    }

    public void destroy() {
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(mListener);
            mLocationClient.stop();
        }
    }
}
