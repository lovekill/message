package com.hh.sdk.service;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import com.hh.sdk.ICallback;
import com.hh.sdk.api.OrderStatusApi;
import com.hh.sdk.dao.OrderDao;
import com.hh.sdk.model.InitInfo;
import com.hh.sdk.model.Order;
import com.hh.sdk.net.HttpEngine;
import com.hh.sdk.net.JsonResponse;
import com.hh.sdk.util.PhoneInformation;
import com.hh.sdk.util.SDKUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by engine on 15/1/29.
 */
public class QueryOderStatus extends BaseService implements Runnable {
    private Activity mActivity  ;
    private ICallback mCallback;
    private Handler mHandler ;
    private List<Order> orders ;
    private  InitInfo  mInitInfo ;
    private PhoneInformation mPhoneInfo ;
    private OrderStatusApi api  ;
    private static int TIME = 10000 ;
    private QueryOderStatus(Activity activity){
       this.mActivity = activity ;
        this.mHandler = new Handler() ;
        mInitInfo = SDKUtils.getMeteData(mActivity);
        mPhoneInfo = new PhoneInformation(mActivity);
        api = new OrderStatusApi() ;
        api.channelId=mInitInfo.chanelId ;
        api.gameId = mInitInfo.gameId ;
        api.merchantId = mInitInfo.merchantId ;
        api.imei = mPhoneInfo.getDeviceCode() ;
        api.uid = uid;
        api.setResponse(jsonResponse);
    }
    private static QueryOderStatus instance ;
    public static QueryOderStatus getInstance(Activity activity){
        if(instance == null){
            instance = new QueryOderStatus(activity) ;
        }
        return  instance ;
    }
    public void startQuery(ICallback callback){
       this.mCallback = callback ;
        OrderDao dao = OrderDao.getInstance(mActivity) ;
        orders = dao.getCricleOrder() ;
        if(orders.size()>0) {
            mHandler.postDelayed(this, TIME);
        }
    }

    public void stopQuery(){
        mHandler.removeCallbacks(this);
    }

    public void addOrderToCircle(Order order){
        if(orders.size()==0){
            mHandler.postDelayed(this,TIME);
        }
        orders.add(order);
    }

    public void removeOrderFromCircle(String orderId){
        for(int i =0 ;i<orders.size();i++){
            Order o = orders.get(i) ;
            if(o.orderNo.equals(orderId)) {
                orders.remove(i);
            }
        }
    }
    @Override
    public void run() {
        StringBuilder sb = new StringBuilder() ;
        for (Order o:orders){
           sb.append(o.orderNo).append(",") ;
        }
        api.orderNos = sb.toString() ;
        if(!TextUtils.isEmpty(api.orderNos)) {
//            new NetTask().execute(api);
            api.getHttpResponse().response(new HttpEngine().excuteRquest(api.getUrl(),api.getParams(),api.getMethod()));
        }
    }
    private JsonResponse jsonResponse = new JsonResponse(){
        @Override
        public void requestError(String string) {
            super.requestError(string);
            mHandler.postDelayed(QueryOderStatus.this,TIME);
        }

        @Override
        public void requestSuccess(JSONObject jsonObject) {
            int code = jsonObject.optInt("code") ;
            if(code==0){
                JSONArray orderListArray = jsonObject.optJSONArray("data") ;
                for (int i =0 ;i<orderListArray.length();i++){
                    Order order = getOrderFromJson(orderListArray.optJSONObject(i));
                    if(order!=null){
                        if(order.status==Order.PAY_SUCCESS||order.status==Order.PAY_FAIL){
                            removeOrderFromCircle(order.orderNo);
                            OrderDao.getInstance(mActivity).deleteOrderById(order.orderNo);
                            if(order.status ==Order.PAY_SUCCESS){
                                mCallback.paySuccess(order.cpOrderId,order.amount);
                            }
                        }
                        if(orders.size()!=0){
                            mHandler.postDelayed(QueryOderStatus.this,TIME);
                        }
                    }
                }
            }else {
                mHandler.postDelayed(QueryOderStatus.this,TIME) ;
            }
        }
    } ;
}
