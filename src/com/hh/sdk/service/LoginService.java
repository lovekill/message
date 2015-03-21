package com.hh.sdk.service;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;
import com.hh.sdk.ICallback;
import com.hh.sdk.api.LoginApi;
import com.hh.sdk.model.InitInfo;
import com.hh.sdk.net.JsonResponse;
import com.hh.sdk.net.NetTask;
import com.hh.sdk.platform.Iplatform;
import com.hh.sdk.util.PhoneInformation;
import com.hh.sdk.util.SDKUtils;
import org.json.JSONObject;

import java.io.*;
import java.util.UUID;

/**
 * @author ZengQBo
 * @Description
 * @time 2014年12月15日
 */
public class LoginService extends BaseService {
    private static final String TAG = "LoginService";
    private Iplatform iplatform;
    private Activity mActivity;
    private ICallback callback;
    public static boolean isLogin = false;
    public static String loginTime = "";

    public interface GameLoginListener {
        public void LoginSuccess(String uid);

        public void LoginFail(String value);
    }

    public LoginService(Activity activity, Iplatform iplateform) {
        this.mActivity = activity;
        this.iplatform = iplateform;
    }

    public void login(String uid, ICallback callback) {
        this.callback = callback;
        iplatform.login(mActivity,uid, listener);
    }

    private GameLoginListener listener = new GameLoginListener() {

        @Override
        public void LoginSuccess(String uid) {
            PhoneInformation phoneInformation = new PhoneInformation(mActivity);
            InitInfo initInfo = SDKUtils.getMeteData(mActivity);
            LoginApi loginApi = new LoginApi();
            loginApi.imei = phoneInformation.getDeviceCode();
            if(uid==null) {
                loginApi.uid = generatorUid(phoneInformation);
            }else {
                loginApi.uid = uid ;
            }
            loginApi.imsi = phoneInformation.getImsi();
            loginApi.gameId = initInfo.gameId;
            loginApi.channelId = initInfo.chanelId;
            loginApi.merchantId = initInfo.merchantId;
            loginApi.setResponse(loginResponse);
            new NetTask().execute(loginApi);
        }

        @Override
        public void LoginFail(String value) {
            callback.onError(ICallback.LOGIN, value);
        }
    };

    private JsonResponse loginResponse = new JsonResponse() {
        @Override
        public void requestError(String string) {
            super.requestError(string);
            Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void requestSuccess(JSONObject jsonObject) {
            int code = jsonObject.optInt("code");
            if (code == 0) {
                JSONObject data = jsonObject.optJSONObject("data");
                uid = data.optString("uid");
                notifyType = data.optInt("notifyType");
                callback.loginSuccess(uid);
                QueryOderStatus.getInstance(mActivity).startQuery(callback);
            } else {
                callback.onError(ICallback.LOGIN, "服务端错误");
            }
        }
    };

    private String generatorUid(PhoneInformation phoneInformation) {
        String imie = phoneInformation.getDeviceCode();
        if (!TextUtils.isEmpty(imie)) {
            return imie;
        } else {
            String path = SDKUtils.getSKCardPath() + "/.zhidian.data";
            File file = new File(path);
            if (file.exists()) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    String uid = bufferedReader.readLine();
                    bufferedReader.close();
                    if (TextUtils.isEmpty(uid)) {
                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                        uid = UUID.randomUUID().toString();
                        bufferedWriter.write(uid);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                    }
                    return uid;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    file.createNewFile();
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                    String uid = UUID.randomUUID().toString();
                    bufferedWriter.write(uid);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    return uid;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
