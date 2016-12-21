package com.asiasoft.playparksdk.framework;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.praneat.playparksdk.internal.PPSConstants;
import com.praneat.playparksdk.internal.PlayparkSDKInternal;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class PlayparkSDKUnityBridge extends UnityPlayerActivity 
{
	private static final String ON_REQUEST_LOGIN_SUCCESS_CALLBACK = "OnRequestLoginSuccess";
	private static final String ON_REQUEST_LOGIN_FAILURE_CALLBACK = "OnRequestLoginFailure";
	private static final String ON_REQUEST_SWITCH_ACCOUNT_SUCCESS_CALLBACK = "OnRequestSwitchAccountSuccess";
	private static final String ON_REQUEST_SWITCH_ACCOUNT_FAILURE_CALLBACK = "OnRequestSwitchAccountFailure";
	private static final String ON_REQUEST_WEB_TOP_UP_SUCCESS_CALLBACK = "OnRequestWebTopUpSuccess";
	private static final String ON_REQUEST_WEB_TOP_UP_FAILURE_CALLBACK = "OnRequestWebTopUpFailure";
	private static final String ON_REQUEST_WALLET_PAYMENT_SUCCESS_CALLBACK = "OnRequestWalletPaymentSuccess";
	private static final String ON_REQUEST_WALLET_PAYMENT_FAILURE_CALLBACK = "OnRequestWalletPaymentFailure";
	private static final String ON_REQUEST_PAYMENT_SUCCESS_CALLBACK = "OnRequestPaymentSuccess";
	private static final String ON_REQUEST_PAYMENT_FAILURE_CALLBACK = "OnRequestPaymentFailure";
	
	private static Activity currentActivity;
	private static String gameObjectName;
	
	//region initialized method
	public static void init(String gameObjectName)
	{	
		PlayparkSDKUnityBridge.currentActivity = UnityPlayer.currentActivity;
		com.asiasoft.playparksdk.framework.PlayparkSDKUnityBridge.gameObjectName = gameObjectName;
	}
	
	public static void initLogin(String partnerCode) 
	{
		PlayparkSDK.initLogin(currentActivity, partnerCode);
	}

	public static void initWalletPayment(String merchantCode, String merchantKey) 
	{
		PlayparkSDK.initWalletPayment(currentActivity, merchantCode, merchantKey);
	}
	
	public static void initPayment(String merchantCode, String merchantKey)
	{
		PlayparkSDK.initPayment(currentActivity, merchantCode, merchantKey);
	}
	
	public static void initPaymentAndWalletPayment(String paymentMerchantCode, String paymentMerchantKey, 
			String walletPaymentMerchantCode, String walletPaymentMerchantKey)
	{
		PlayparkSDK.initPaymentAndWalletPayment(currentActivity, 
				paymentMerchantCode, paymentMerchantKey, 
				walletPaymentMerchantCode, walletPaymentMerchantKey);
	}
	//endregion
	
	//region authentication method
	private static PlayparkSDKInternal.RequestLoginCallback onRequestLoginCallback = new PlayparkSDKInternal.RequestLoginCallback() 
	{
		@Override
		public void onSuccess(String userId, String token) {
			
			Map<String, String> data = new HashMap<String, String>();
			data.put("userId", userId);
			data.put("token", token);
			
			Gson gson = new Gson();
			String jsonText = gson.toJson(data);

			UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_LOGIN_SUCCESS_CALLBACK, jsonText);
		}

		@Override
		public void onFailure(String message) {
			
			super.onFailure(message);

			Map<String, String> data = new HashMap<String, String>();
			data.put("message", message);
			
			Gson gson = new Gson();
			String jsonText = gson.toJson(data);

			UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_LOGIN_FAILURE_CALLBACK, jsonText);
		}
	};
	
	public static void requestLogin() 
	{
		int domainType = PPSConstants.DomainType.NONE.ordinal();
		requestLogin(domainType);
	}
	
	public static void requestLogin(int type)
	{
		PPSConstants.DomainType domainType = PPSConstants.DomainType.fromInt(type);
		PlayparkSDK.requestLogin(currentActivity, domainType, onRequestLoginCallback);
	}
	
	public static void requestAutoLogin() 
	{
		PlayparkSDK.requestAutoLogin(currentActivity, onRequestLoginCallback);
	}
	//endregion
	
	//region switch account method
	public static void requestSwitchAccount()
	{
		int domainType = PPSConstants.DomainType.NONE.ordinal();
		requestSwitchAccount(domainType);
	}
	
	public static void requestSwitchAccount(int type)
	{
		PPSConstants.DomainType domainType = PPSConstants.DomainType.fromInt(type);
		PlayparkSDK.requestSwitchAccount(currentActivity, domainType, new PlayparkSDKInternal.RequestSwitchAccountCallback() {
			
			@Override
			public void onSuccess() {
				
				UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_SWITCH_ACCOUNT_SUCCESS_CALLBACK, "");
			}
			
			@Override
			public void onFailure(String message) {
				
				super.onFailure(message);
				
				Map<String, String> data = new HashMap<String, String>();
				data.put("message", message);
				
				Gson gson = new Gson();
				String jsonText = gson.toJson(data);

				UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_SWITCH_ACCOUNT_FAILURE_CALLBACK, jsonText);
			}
		});
	}
	//endregion
	
	//region bind account method
	public static void requestBindAccount()
	{
		PlayparkSDK.requestBindAccount(currentActivity, onRequestLoginCallback);
	}
	//endregion
	
	//region web top-up method
	public static void requestWebTopUp(int mode)
	{
		PPSConstants.WebTopUpMode webTopUpMode = PPSConstants.WebTopUpMode.fromInt(mode);
		PlayparkSDK.requestWebTopUp(currentActivity, webTopUpMode, new PlayparkSDKInternal.RequestWebTopUpCallback() {
			
			@Override
			public void onSuccess() {
				
				UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_WEB_TOP_UP_SUCCESS_CALLBACK, "");
			}
			
			@Override
			public void onFailure(String message) {
				
				super.onFailure(message);
				
				Map<String, String> data = new HashMap<String, String>();
				data.put("message", message);
				
				Gson gson = new Gson();
				String jsonText = gson.toJson(data);

				UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_WEB_TOP_UP_FAILURE_CALLBACK, jsonText);
			}
		});
	}
	//endregion
	
	public static void requestWalletPayment(String transactionId, String customerId, 
			String itemName, String itemAmount, String itemCurrency) {
		
		PlayparkSDK.requestWalletPayment(currentActivity, transactionId, customerId, 
				itemName, itemAmount, itemCurrency, 
				new PlayparkSDKInternal.RequestWalletPaymentCallback() {
			
					@Override
					public void onSuccess(String token) {
						
						Map<String, String> data = new HashMap<String, String>();
						data.put("token", token);
						
						Gson gson = new Gson();
						String jsonText = gson.toJson(data);

						UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_WALLET_PAYMENT_SUCCESS_CALLBACK, jsonText);
					}
					
					@Override
					public void onFailure(String message) {
						
						super.onFailure(message);

						Map<String, String> data = new HashMap<String, String>();
						data.put("message", message);
						
						Gson gson = new Gson();
						String jsonText = gson.toJson(data);

						UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_WALLET_PAYMENT_FAILURE_CALLBACK, jsonText);
					}
				});
	}
	
	public static void requestPayment(String transactionId, String customerId)
	{
		PlayparkSDK.requestPayment(currentActivity, transactionId, customerId, 
				new PlayparkSDKInternal.RequestPaymentCallback()
				{
					@Override
					public void onSuccess(String token) {
						
						Map<String, String> data = new HashMap<String, String>();
						data.put("token", token);
						
						Gson gson = new Gson();
						String jsonText = gson.toJson(data);
		
						UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_PAYMENT_SUCCESS_CALLBACK, jsonText);
					}
					
					@Override
					public void onFailure(String message) {
						
						super.onFailure(message);
		
						Map<String, String> data = new HashMap<String, String>();
						data.put("message", message);
						
						Gson gson = new Gson();
						String jsonText = gson.toJson(data);
		
						UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_PAYMENT_FAILURE_CALLBACK, jsonText);
					}
				});
	}
	
	public static int getLoginStatus() {
		PPSConstants.LoginStatus status = PlayparkSDK.getLoginStatus();
		int result = status.ordinal();
		return result;
	}
	
	public static boolean isLoggedIn() {
		return PlayparkSDK.isLoggedIn();
	}
	
	public static void useSandbox(boolean isSandbox) 
	{
		PlayparkSDK.useSandbox(isSandbox);
	}
	
	public static boolean isSandbox() 
	{
		return PlayparkSDK.isSandbox();
	}

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onBackPressed()
	{
		// ignore the back button event
		// super.onBackPressed();
	}
}
