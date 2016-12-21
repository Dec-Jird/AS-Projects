package com.asiasoft.playparksdk.framework;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.asiasoft.playparksdk.PPSWebViewActivity;
import com.nilecon.playmobilesdk.R;
import com.praneat.playparksdk.internal.PPSConstants;
import com.praneat.playparksdk.internal.PlayparkSDKInternal;
import com.praneat.playparksdk.internal.PPSConstants.WebTopUpMode;
import com.praneat.playparksdk.internal.utils.NetworkManager;

/**
 * Main Class of Playpark SDK
 */
public class PlayparkSDK {
	
	/**
	 * Initialize the PlayPark SDK for {@link #requestLogin}. Should be called from your onCreate methods
	 * @param context A context (usually the activity)
	 * @param partnerCode Partner code provided by Asiasoft
	 */
	public static void initLogin(Context context, String partnerCode) {
		
		PlayparkSDKInternal.INSTANCE.initLogin(context, partnerCode);
	}

	/**
	 * Initialize the PlayPark SDK for {@link #requestWalletPayment}. Should be called from your onCreate methods
	 * @param context A context (usually the activity)
	 * @param merchantCode Merchant code provided by Asiasoft
	 * @param merchantKey Merchant key provided by Asiasoft
	 */
	public static void initWalletPayment(Context context, String merchantCode, String merchantKey) {
		
		PlayparkSDKInternal.INSTANCE.initWalletPayment(context, merchantCode, merchantKey);
	}
	
	/**
	 * Initialize the PlayPark SDK for {@link #requestPayment}. Should be called from your onCreate methods
	 * @param context A context (usually the activity)
	 * @param merchantCode Merchant code provided by Asiasoft
	 * @param merchantKey Merchant key provided by Asiasoft
	 */
	public static void initPayment(Context context, String merchantCode, String merchantKey) {
		
		PlayparkSDKInternal.INSTANCE.initPayment(context, merchantCode, merchantKey);
	}
	
	/**
	 * Initialize the PlayPark SDK for {@link #requestPayment} & {@link #requestWalletPayment}. Should be called from your onCreate methods
	 * @param context A context (usually the activity)
	 * @param paymentMerchantCode Merchant code for {@link #requestPayment} provided by Asiasoft
	 * @param paymentMerchantKey Merchant key for {@link #requestPayment} provided by Asiasoft
	 * @param walletPaymentMerchantCode Merchant code for {@link #requestWalletPayment} provided by Asiasoft
	 * @param walletPaymentMerchantKey Merchant key for {@link #requestWalletPayment} provided by Asiasoft
	 */
	public static void initPaymentAndWalletPayment(Context context, String paymentMerchantCode, String paymentMerchantKey, String walletPaymentMerchantCode, String walletPaymentMerchantKey) {
		
		PlayparkSDKInternal.INSTANCE.initPaymentAndWalletPayment(context, paymentMerchantCode, paymentMerchantKey, walletPaymentMerchantCode, walletPaymentMerchantKey);
	}
	
	/**
	 * Get initialize the PlayPark SDK for Authentication flag
	 * @return Initialize the PlayPark SDK flag
	 */
	public static boolean isInitLogin() {
		return PlayparkSDKInternal.INSTANCE.isInitLogin();
	}
	
	/** 
	 * Get initialize the PlayPark SDK for Wallet Payment flag
	 * @return Initialize the PlayPark SDK for Wallet Payment flag
	 */
	public static boolean isInitWalletPayment() {
		return PlayparkSDKInternal.INSTANCE.isInitWalletPayment();
	}
	
	/** 
	 * Get initialize the PlayPark SDK for Payment flag
	 * @return Initialize the PlayPark SDK for Payment flag
	 */
	public static boolean isInitPayment() {
		return PlayparkSDKInternal.INSTANCE.isInitPayment();
	}
	
	/**
	 * SDK provide the function to open the Playpark Authen Gateway Url to authenticate the player via WebView and get userId & token for verifying with PlayPark API.<br/>
	 * (This method will initiate PPSWebViewActivity)
	 * @param context A context (usually the activity)
	 * @param callback Callback method {@link PlayparkSDKInternal.RequestLoginCallback}
	 */
	public static void requestLogin(Context context, PlayparkSDKInternal.RequestLoginCallback callback) {

		requestLogin(context, PPSConstants.DomainType.NONE, callback);
	}

	/**
	 * SDK provide the function to open the Playpark Authen Gateway Url to authenticate the player via WebView and get userId & token for verifying with PlayPark API.<br/>
	 * (This method will initiate PPSWebViewActivity)
	 * @param context A context (usually the activity)
	 * @param type Domain type {@link PPSConstants.DomainType}
	 * @param callback Callback method {@link PlayparkSDKInternal.RequestLoginCallback}
	 */
	public static void requestLogin(Context context, PPSConstants.DomainType type, PlayparkSDKInternal.RequestLoginCallback callback) {

		NetworkManager networkManager = new NetworkManager(context);

		if(networkManager.isNetworkConnected()) {
			if (PlayparkSDKInternal.INSTANCE.isInitLogin()) {

				PlayparkSDKInternal.INSTANCE.requestLogin(type, callback);
				Intent intent = new Intent(context, PPSWebViewActivity.class);
				intent.putExtra("mode", PPSConstants.ACTION_MODE_LOGIN);
				context.startActivity(intent);

			} else {
				if(callback != null) {
					String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_UNINITIALIZED_SDK_LOGIN, context.getString(R.string.pps_uninitialized_sdk_login));
					callback.onFailure(failureMessage);
				}
			}
		} else {
			if(callback != null) {
				String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_NO_INTERNET_CONNECTION, context.getString(R.string.pps_no_internet_connection));
				callback.onFailure(failureMessage);
			}
		}
	}

	/**
	 * SDK provide the function to open the Playpark Authen Gateway Url to auto authenticate the player via WebView use with Game Login and Play Wallet Login and get userId & token for verifying with PlayPark API.<br/>
	 * (This method will initiate PPSWebViewActivity)
	 * @param context A context (usually the activity)
	 * @param callback Callback method {@link PlayparkSDKInternal.RequestLoginCallback}
	 */
	public static void requestAutoLogin(Context context, PlayparkSDKInternal.RequestLoginCallback callback) {

		NetworkManager networkManager = new NetworkManager(context);

		if(networkManager.isNetworkConnected()) {
			if (PlayparkSDKInternal.INSTANCE.isInitLogin()) {

				PlayparkSDKInternal.INSTANCE.requestAutoLogin(callback);
				Intent intent = new Intent(context, PPSWebViewActivity.class);
				intent.putExtra("mode", PPSConstants.ACTION_MODE_AUTO_LOGIN);
				context.startActivity(intent);

			} else {
				if(callback != null) {
					String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_UNINITIALIZED_SDK_LOGIN, context.getString(R.string.pps_uninitialized_sdk_login));
					callback.onFailure(failureMessage);
				}
			}
		} else {
			if(callback != null) {
				String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_NO_INTERNET_CONNECTION, context.getString(R.string.pps_no_internet_connection));
				callback.onFailure(failureMessage);
			}
		}
	}
	public static void requestGameToken(Context context,String gamePartnerCode, PlayparkSDKInternal.RequestLoginCallback callback) {

		NetworkManager networkManager = new NetworkManager(context);

		if(networkManager.isNetworkConnected()) {
			if (PlayparkSDKInternal.INSTANCE.isInitLogin()) {

				PlayparkSDKInternal.INSTANCE.requestGameToken(callback);
				Intent intent = new Intent(context, PPSWebViewActivity.class);
				intent.putExtra("mode", PPSConstants.ACTION_MODE_GAME_REQUEST_TOKEN);
				intent.putExtra("gamePartnerCode",gamePartnerCode);
				context.startActivity(intent);

			} else {
				if(callback != null) {
					String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_UNINITIALIZED_SDK_LOGIN, context.getString(R.string.pps_uninitialized_sdk_login));
					callback.onFailure(failureMessage);
				}
			}
		} else {
			if(callback != null) {
				String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_NO_INTERNET_CONNECTION, context.getString(R.string.pps_no_internet_connection));
				callback.onFailure(failureMessage);
			}
		}
	}
	public static void gameRequestAutoLogin(Context context,String gamePartnerCode, PlayparkSDKInternal.RequestLoginCallback callback)
	{
		NetworkManager networkManager = new NetworkManager(context);
		if(networkManager.isNetworkConnected()) {
			if (PlayparkSDKInternal.INSTANCE.isInitLogin()) {

				PlayparkSDKInternal.INSTANCE.requestGameToken(callback);
				Intent intent = new Intent(context, PPSWebViewActivity.class);
				intent.putExtra("mode", PPSConstants.ACTION_MODE_GAME_REQUEST_TOKEN);
				intent.putExtra("gamePartnerCode",gamePartnerCode);
				context.startActivity(intent);

			} else {
				if(callback != null) {
					String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_UNINITIALIZED_SDK_LOGIN, context.getString(R.string.pps_uninitialized_sdk_login));
					callback.onFailure(failureMessage);
				}
			}
		} else {
			if(callback != null) {
				String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_NO_INTERNET_CONNECTION, context.getString(R.string.pps_no_internet_connection));
				callback.onFailure(failureMessage);
			}
		}
	}

	/**
	 * SDK provide the function to open the Playpark Authen Gateway Url to switch the player account via WebView.<br/>
	 * (This method will initiate PPSWebViewActivity)
	 * @param context A context (usually the activity)
	 * @param callback Callback method {@link PlayparkSDKInternal.RequestSwitchAccountCallback}
	 */
	public static void requestSwitchAccount(Context context, PlayparkSDKInternal.RequestSwitchAccountCallback callback) {

		requestSwitchAccount(context, PPSConstants.DomainType.NONE, callback);
	}

	/**
	 * SDK provide the function to open the Playpark Authen Gateway Url to switch the player account via WebView.<br/>
	 * (This method will initiate PPSWebViewActivity)
	 * @param context A context (usually the activity)
	 * @param type Domain type {@link PPSConstants.DomainType}
	 * @param callback Callback method {@link PlayparkSDKInternal.RequestSwitchAccountCallback}
	 */
	public static void requestSwitchAccount(Context context, PPSConstants.DomainType type, PlayparkSDKInternal.RequestSwitchAccountCallback callback) {

		NetworkManager networkManager = new NetworkManager(context);

		if(networkManager.isNetworkConnected()) {
			if (PlayparkSDKInternal.INSTANCE.isInitLogin()) {

				PlayparkSDKInternal.INSTANCE.requestSwitchAccount(type, callback);
				Intent intent = new Intent(context, PPSWebViewActivity.class);
				intent.putExtra("mode", PPSConstants.ACTION_MODE_SWITCH_ACCOUNT);
				context.startActivity(intent);

			} else {
				if(callback != null) {
					String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_UNINITIALIZED_SDK_LOGIN, context.getString(R.string.pps_uninitialized_sdk_login));
					callback.onFailure(failureMessage);
				}
			}
		} else {
			if(callback != null) {
				String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_NO_INTERNET_CONNECTION, context.getString(R.string.pps_no_internet_connection));
				callback.onFailure(failureMessage);
			}
		}
	}

	/**
	 * SDK provide the function to open the Playpark Authen Gateway Url to bind the player account with Login ID.<br/>
	 * (This method will initiate PPSWebViewActivity)
	 * @param context A context (usually the activity)
	 * @param callback Callback method {@link PlayparkSDKInternal.RequestLoginCallback}
	 */
	public static void requestBindAccount(Context context, PlayparkSDKInternal.RequestLoginCallback callback) {

		NetworkManager networkManager = new NetworkManager(context);

		if(networkManager.isNetworkConnected()) {
			if (PlayparkSDKInternal.INSTANCE.isInitLogin()) {

				PlayparkSDKInternal.INSTANCE.requestLogin(callback);
				Intent intent = new Intent(context, PPSWebViewActivity.class);
				intent.putExtra("mode", PPSConstants.ACTION_MODE_BIND_ACCOUNT);
				context.startActivity(intent);

			} else {
				if(callback != null) {
					String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_UNINITIALIZED_SDK_LOGIN, context.getString(R.string.pps_uninitialized_sdk_login));
					callback.onFailure(failureMessage);
				}
			}
		} else {
			if(callback != null) {
				String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_NO_INTERNET_CONNECTION, context.getString(R.string.pps_no_internet_connection));
				callback.onFailure(failureMessage);
			}
		}
	}

	/**
	 * SDK provide the function to initial the Playpark Web Top-up.<br/>
	 * (This method will initiate PPSWebViewActivity)
	 * @param context A context (usually the activity)
	 * @param mode Web Top-up mode {@link WebTopUpMode}
	 * @param callback Callback method {@link PlayparkSDKInternal.RequestWebTopUpCallback}
	 */
	public static void requestWebTopUp(Context context, WebTopUpMode mode, PlayparkSDKInternal.RequestWebTopUpCallback callback) {

		NetworkManager networkManager = new NetworkManager(context);

		if(networkManager.isNetworkConnected()) {
			if (PlayparkSDKInternal.INSTANCE.isInitLogin()) {

				PlayparkSDKInternal.INSTANCE.requestWebTopUp(mode, callback);
				Intent intent = new Intent(context, PPSWebViewActivity.class);
				intent.putExtra("mode", PPSConstants.ACTION_MODE_WEB_TOP_UP);
				context.startActivity(intent);

			} else {
				if(callback != null) {
					String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_UNINITIALIZED_SDK_LOGIN, context.getString(R.string.pps_uninitialized_sdk_login));
					callback.onFailure(failureMessage);
				}
			}
		} else {
			if(callback != null) {
				String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_NO_INTERNET_CONNECTION, context.getString(R.string.pps_no_internet_connection));
				callback.onFailure(failureMessage);
			}
		}
	}

	/**
	 * SDK provide the function to open the Playpark Wallet Gateway Url and get token for validate with PlayPark API.<br/>
	 * (This method will initiate PPSWebViewActivity)
	 * @param context A context (usually the activity)
	 * @param transactionId Merchant's transaction id. (must be unique) <p><u>example:</u> TR201408280001</p>
	 * @param customerId Merchant's customer Master id of player. ID of player, first 5 chars is prefix (included dotted).
	 * <p><u>example:</u><li>THPP.playid001</li><li>SGPP.playid002</li><li>INFB.100001865868994</li><li>INPP.2013092399034DB</li></p>
	 * @param itemName Merchant's item name
	 * @param itemAmount Merchant's item amount
	 * @param itemCurrency Item amount's currency <p><u>example:</u> THB, SGDM</p>
	 * @param callback Callback method {@link PlayparkSDKInternal.RequestWalletPaymentCallback}
	 */
	public static void requestWalletPayment(Context context, String transactionId, String customerId,
			String itemName, String itemAmount, String itemCurrency,
			PlayparkSDKInternal.RequestWalletPaymentCallback callback) {

		NetworkManager networkManager = new NetworkManager(context);

		if(networkManager.isNetworkConnected()) {
			if (PlayparkSDKInternal.INSTANCE.isInitWalletPayment()) {

				PlayparkSDKInternal.INSTANCE.requestWalletPayment(transactionId, customerId, itemName, itemAmount, itemCurrency, callback);

				Intent intent = new Intent(context, PPSWebViewActivity.class);
				intent.putExtra("mode", PPSConstants.ACTION_MODE_WALLET_PAYMENT);
				context.startActivity(intent);

			} else {
				if(callback != null) {
					String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_UNINITIALIZED_SDK_WALLET_PAYMENT, context.getString(R.string.pps_uninitialized_sdk_wallet_payment));
					callback.onFailure(failureMessage);
				}
			}
		} else {
			if(callback != null) {
				String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_NO_INTERNET_CONNECTION, context.getString(R.string.pps_no_internet_connection));
				callback.onFailure(failureMessage);
			}
		}
	}

	/**
	 * SDK provide the function to open the Playpark Direct Payment Gateway Url and get token for validate with PlayPark API.<br/>
	 * (This method will initiate PPSWebViewActivity)
	 * @param context A context (usually the activity)
	 * @param transactionId Merchant's transaction id. (must be unique) <p><u>example:</u> TR201408280001</p>
	 * @param customerId Merchant's customer Master id of player. ID of player, first 5 chars is prefix (included dotted).
	 * <p><u>example:</u><li>THPP.playid001</li><li>SGPP.playid002</li><li>INFB.100001865868994</li><li>INPP.2013092399034DB</li></p>
	 * @param callback Callback method {@link PlayparkSDKInternal.RequestWalletPaymentCallback}
	 */
	public static void requestPayment(Context context, String transactionId, String customerId, 
			PlayparkSDKInternal.RequestPaymentCallback callback) {
		
		NetworkManager networkManager = new NetworkManager(context);
		
		if(networkManager.isNetworkConnected()) {
			if (PlayparkSDKInternal.INSTANCE.isInitPayment()) {
				
				PlayparkSDKInternal.INSTANCE.requestPayment(transactionId, customerId, callback);
				
				Intent intent = new Intent(context, PPSWebViewActivity.class);
				intent.putExtra("mode", PPSConstants.ACTION_MODE_PAYMENT);
				context.startActivity(intent);
				
			} else {
				if(callback != null) {
					String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_UNINITIALIZED_SDK_PAYMENT, context.getString(R.string.pps_uninitialized_sdk_payment));
					callback.onFailure(failureMessage);
				}
			}
		} else {
			if(callback != null) {
				String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_NO_INTERNET_CONNECTION, context.getString(R.string.pps_no_internet_connection));
				callback.onFailure(failureMessage);
			}
		}
	}
	
	/**
	 * Get login status
	 * @return Returns login status
	 */
	public static PPSConstants.LoginStatus getLoginStatus() {
		return PlayparkSDKInternal.INSTANCE.getLoginStatus();
	}
	
	/**
	 * Get logged in flag
	 * @return Returns logged in flag
	 */
	public static boolean isLoggedIn() {
		return PlayparkSDKInternal.INSTANCE.isLoggedIn();
	}
	
	/**
	 * Set sandbox mode flag for test host name
	 * @param isSandbox sandbox mode flag
	 */
	public static void useSandbox(boolean isSandbox) {
		PlayparkSDKInternal.INSTANCE.useSandbox(isSandbox);
	}
	
	/**
	 * Get sandbox mode flag
	 * @return Returns sandbox mode flag
	 */
	public static boolean isSandbox() {
		return PlayparkSDKInternal.INSTANCE.isSandbox();
	}

	public static String getAppkey()
	{
		return PlayparkSDKInternal.INSTANCE.getAppkey();
	}

	public static void setAppkey(String appkey)
	{
		//Log.d(PPSConstants.LOG_TAG, "PlayparkSDK -> setAppkey()  appkey: "+appkey);
		PlayparkSDKInternal.INSTANCE.setAppkey(appkey);
	}
	public static boolean isPlayMobile()
	{
		return PlayparkSDKInternal.INSTANCE.getPlayMobile();
	}
	public static  void setIsPlayMobile(boolean isPlayMobile)
	{
		PlayparkSDKInternal.INSTANCE.setIsPlayMobile(isPlayMobile);
	}
}
