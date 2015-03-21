package com.hh.sdk.platform;

import android.app.Activity;
import com.hh.sdk.model.LocationInfo;
import com.hh.sdk.service.InitService;
import com.hh.sdk.service.LogOutService;
import com.hh.sdk.service.LoginService;
import com.hh.sdk.service.OrderGenerateService;

/**
 * Created by Administrator on 2014/12/11.
 */
public interface Iplatform {

	/**
	 * @param mActivity 
	 * @param gameInitListener
	 */
	public void init(Activity mActivity, InitService.GameInitListener gameInitListener);

	/**
	 * @param activity
	 * @param gameLoginListener
	 */
	public void login(Activity activity,String uid,
			LoginService.GameLoginListener gameLoginListener);
	
	


	/**
	 * @param activity
	 * @param gameLogoutListener
	 */
	public void logOut(Activity activity,
			LogOutService.GameLogoutListener gameLogoutListener);
	

	/**
	 * @param activity
	 * @param money
	 * @param order
	 * @param listener
	 */
	public void pay(Activity activity, int money, String order,
			 String extInfo, OrderGenerateService.OrderGenerateListener listener);


	public void setLocation(LocationInfo info) ;

	public void onPause();

	public void onDestory();

	public void onResume() ;
	


}
