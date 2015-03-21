/**
 * @author ZingQBo
 * @time 2014年12月15日下午5:33:51
 */
package com.hh.sdk.service;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import com.hh.sdk.ICallback;
import com.hh.sdk.WebActivity;
import com.hh.sdk.api.NotifyServerApi;
import com.hh.sdk.api.OrderGenerateApi;
import com.hh.sdk.dao.OrderDao;
import com.hh.sdk.model.InitInfo;
import com.hh.sdk.model.Order;
import com.hh.sdk.net.JsonResponse;
import com.hh.sdk.net.NetTask;
import com.hh.sdk.platform.Iplatform;
import com.hh.sdk.util.PhoneInformation;
import com.hh.sdk.util.SDKUtils;
import org.json.JSONObject;

/**
 * @author ZengQBo
 * @Description
 * @time 2014年12月15日
 */
public class OrderGenerateService extends BaseService {
    private Iplatform iplatform;
    private Activity mActivity;
    private ICallback callback;
    private int money;
    private String extInfo;
    private String cpOrderId;
    private Order mOrder;
    private InitInfo mInitInfo;

    public interface OrderGenerateListener {
        public void onSuccess(String orderId, int amount);

        public void onFail(String orderId, String value);
    }

    public OrderGenerateService(Activity activity, Iplatform iplateform) {
        this.mActivity = activity;
        this.iplatform = iplateform;
        mInitInfo = SDKUtils.getMeteData(activity);
    }

    public void dopay(int money, String cpOrderId,String productName,
                      String extInfo, ICallback callback) {
        this.callback = callback;
        this.extInfo = extInfo;
        this.cpOrderId = cpOrderId;
        // 生成订单
        this.money = money;
        orderGenerate(money, cpOrderId, extInfo,productName);
    }

    private OrderGenerateListener listener = new OrderGenerateListener() {

        @Override
        public void onSuccess(String orderId, int amount) {
            callback.paySuccess(cpOrderId, amount);
            //通知服务端成功
            QueryOderStatus.getInstance(mActivity).removeOrderFromCircle(orderId);
            OrderDao.getInstance(mActivity).deleteOrderById(orderId);
            notifyServer(2);
        }

        @Override
        public void onFail(String orderId, String value) {
            callback.onError(ICallback.PAY, value);
            QueryOderStatus.getInstance(mActivity).removeOrderFromCircle(orderId);
            OrderDao.getInstance(mActivity).deleteOrderById(orderId);
            notifyServer(1);
        }
    };

    private void orderGenerate(int money, String cpOrderId,
                               String extInfo,String productName) {
        PhoneInformation phoneInformation = new PhoneInformation(mActivity);
        OrderGenerateApi api = new OrderGenerateApi();
        api.gameId = mInitInfo.gameId;
        api.channelId = mInitInfo.chanelId;
        api.merchantId = mInitInfo.merchantId;
        api.uid = uid;
        api.cpOrderId = cpOrderId;
        api.extInfo = extInfo;
        api.amount = money;
        api.sdkVersion = getSdkVersion();
        api.imei = phoneInformation.getDeviceCode();
        api.imsi = phoneInformation.getImsi();
        api.goodsName = productName ;
        api.setResponse(jsonResponse);
        new NetTask().execute(api);
    }

    private void notifyServer(int status) {
        PhoneInformation phoneInformation = new PhoneInformation(mActivity);
        NotifyServerApi notifyServerApi = new NotifyServerApi();
        notifyServerApi.channelId = mInitInfo.chanelId;
        notifyServerApi.gameId = mInitInfo.gameId;
        notifyServerApi.merchantId = mInitInfo.merchantId;
        notifyServerApi.amount = mOrder.amount;
        notifyServerApi.orderNo = mOrder.orderNo;
        notifyServerApi.status = status;
        notifyServerApi.imei = phoneInformation.getDeviceCode();
    }

    private JsonResponse jsonResponse = new JsonResponse() {

        @Override
        public void requestError(String string) {
            super.requestError(string);
            Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void requestSuccess(JSONObject jsonObject) {
            int code = jsonObject.optInt("code");
            if (code == 0) {
                mOrder = new Order();
                JSONObject json = jsonObject.optJSONObject("data");
                String orderId = json.optString("orderNo");
                mOrder.orderNo = orderId;
                mOrder.amount = money;
                mOrder.cpOrderId = cpOrderId;
                mOrder.extInfo = extInfo;
                mOrder.status = 0;
                mOrder.giveOut = 0;
                int paytype = json.optInt("paymentType");
                if (paytype != 1001) {//短信SDK支付
                    if (paytype == PAY_PLATEFORM) {
                        iplatform.pay(mActivity, money, mOrder.orderNo, extInfo, listener);
                    } else {
                        Toast.makeText(mActivity, "请重新启动游戏后才能支付", Toast.LENGTH_SHORT).show();
                    }
                } else {//支付宝支付
                    Intent intent = new Intent(mActivity, WebActivity.class);
                    intent.putExtra(WebActivity.URL, json.optString("redirectUrl"));
                    mActivity.startActivity(intent);
                }
                if (notifyType == CLIENT_NOTIFY) {
                    OrderDao.getInstance(mActivity).addOrder(mOrder);
                    QueryOderStatus.getInstance(mActivity).addOrderToCircle(mOrder);
                }
            } else {
                callback.onError(ICallback.PAY, jsonObject.toString());
            }
        }
    };
}
