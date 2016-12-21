package com.nilecon.playmobilesdk;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.asiasoft.playparksdk.framework.PlayparkSDK;
import com.google.gson.Gson;
import com.nilecon.playmobilesdk.Callback.PlayMobileSDKInternal;
import com.praneat.playparksdk.internal.PPSConstants;
import com.praneat.playparksdk.internal.PlayparkSDKInternal;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rawit on 3/4/2016 AD.
 */
public class PlayMobileSDKUnityBridge extends UnityPlayerActivity {

    private static final String ON_REQUEST_LOGIN_PLAYMOBILE_WITH_PACKAGENAME_SUCCESS_CALLBACK = "OnRequestLoginPlayMobileWithPackageNameSuccess";
    private static final String ON_REQUEST_LOGIN_PLAYMOBILE_WITH_PACKAGENAME_FAILURE_CALLBACK = "OnRequestLoginPlayMobileWithPackageNameFailure";
    private static final String ON_REQUEST_RECEIVE_SUCCESS_CALLBACK = "OnRequestReceiveSuccess";
    private static final String ON_REQUEST_RECEIVE_FAILURE_CALLBACK = "OnRequestReceiveFailure";
    private static final String ON_REQUEST_LOGOUT_PLAYMOBILE_SUCCESS_CALLBAK = "OnRequestLogoutPlayMobileSuccess";
    private static final String ON_REQUEST_LOGOUT_PLAYMOBILE_FAILURE_CALLBAK = "OnRequestLogoutPlayMobileFailure";
    private static final String ON_REQUEST_BINDACCOUNT_SUCCESS_CALLBACK = "OnRequestBindAccountSuccess";
    private static final String ON_REQUEST_BINDACCOUNT_FAILURE_CALLBACK = "OnRequestBindAccountFailure";
    private static final String ON_REQUEST_WEBTOPUP_SUCCESS_CALLBACK = "OnRequestWebTopupSuccess";
    private static final String ON_REQUEST_WEBTOPUP_FAILURE_CALLBACK = "OnRequestWebTopupFailure";
    private static final String ON_REQUEST_WALLETPAYMENT_SUCCESS_CALLBACK = "OnRequestWalletPaymentSuccess";
    private static final String ON_REQUEST_WALLETPAYMENT_FAILURE_CALLBACK = "OnRequestWalletPaymentFAILURE";
    private static final String ON_REQUEST_PAYMENT_SUCCESS_CALLBACK = "OnRequestPaymentSuccess";
    private static final String ON_REQUEST_PAYMENT_FAILURE_CALLBACK = "OnRequestPaymentFailure";

    private static Activity currentActivity;
    private static String gameObjectName;

    public static void init(String gameObjectName)
    {
        PlayMobileSDKUnityBridge.currentActivity = UnityPlayer.currentActivity;
        PlayMobileSDKUnityBridge.gameObjectName = gameObjectName;
        //Log.d(PPSConstants.LOG_TAG, "Unity-Bridge init Bridge currentActivity: " + PlayMobileSDKUnityBridge.currentActivity + " ,gameObjectName: " + gameObjectName);
    }
    public static void initPartnerCode(String partnerCode) {
        //Log.d(PPSConstants.LOG_TAG, "Unity-Bridge initPartnerCode partnerCode: " + partnerCode);
             PlayMobileSDK.initPartnerCode(currentActivity, partnerCode);
    }
    public static void useSandbox(boolean isSandbox)
    {
        PlayMobileSDK.userSandbox(isSandbox);
    }
    public static PlayMobileSDKInternal.RequestLoginCallback onRequestLoginPlayMobileWithPackageNameCallback = new PlayMobileSDKInternal.RequestLoginCallback(){

        @Override
        public void onSuccess(String userId, String token) {
           // Log.d(PPSConstants.LOG_TAG, "Unity-Bridge requestLoginPlaymobileWithPackageName-onSuccess  before parse Data with userId : " + userId + " token :" + token);
            Map<String, String> data = new HashMap<String, String>();
            //Log.d(PPSConstants.LOG_TAG, "Unity-Bridge requestLoginPlaymobileWithPackageName-onSuccess  new HashMap");
            data.put("userId", userId);
            data.put("token", token);
            //Log.d(PPSConstants.LOG_TAG, "Unity-Bridge requestLoginPlaymobileWithPackageName-onSuccess  affter Put data to HashMap");
            JSONObject json = new JSONObject(data);
            //Gson gson = new Gson();
           // Log.d(PPSConstants.LOG_TAG, "Unity-Bridge requestLoginPlaymobileWithPackageName-onSuccess Parse JSONObject :"+json.toString());
            //String jsonText = gson.toJson(data);
            String jsonText = json.toString();

            UnityPlayer.UnitySendMessage(gameObjectName,ON_REQUEST_LOGIN_PLAYMOBILE_WITH_PACKAGENAME_SUCCESS_CALLBACK,jsonText);
           // Log.d(PPSConstants.LOG_TAG, "Unity-Bridge requestLoginPlaymobileWithPackageName-onSuccess userId : " + userId + " token :" + token);
        }

        @Override
        public void onFailure(String message) {
            super.onFailure(message);
            Map<String,String> data = new HashMap<String,String>();
            data.put("message",message);

            //Gson gson = new Gson();
            //String jsonText = gson.toJson(data);

            UnityPlayer.UnitySendMessage(gameObjectName,ON_REQUEST_LOGIN_PLAYMOBILE_WITH_PACKAGENAME_FAILURE_CALLBACK,message);
           // Log.d(PPSConstants.LOG_TAG, "Unity-Bridge requestLoginPlaymobileWithPackageName-onFailure message : " + message );
        }

        @Override
        public void onPlayMobile(boolean canopen, String message) {
            super.onPlayMobile(canopen, message);
           // Log.d(PPSConstants.LOG_TAG, "Unity-Bridge requestLoginPlaymobileWithPackageName-onPlayMobile message : " + message);
        }
    };

    public static PlayMobileSDKInternal.RequestReceiveCallback onRequestReceiveCallback = new PlayMobileSDKInternal.RequestReceiveCallback(){

        @Override
        public void onSuccess(String userId, String token) {
            Map<String, String> data = new HashMap<String, String>();
            data.put("userId", userId);
            data.put("token", token);
            JSONObject json = new JSONObject(data);
            //Gson gson = new Gson();
            //String jsonText = gson.toJson(data);
            String jsonText = json.toString();

            UnityPlayer.UnitySendMessage(gameObjectName,ON_REQUEST_RECEIVE_SUCCESS_CALLBACK,jsonText);
            //Log.d(PPSConstants.LOG_TAG, "Unity-Bridge onRequestReceiveCallback-onSuccess userId : " + userId + " token :" + token);
        }

        @Override
        public void onFailure(String message) {
            super.onFailure(message);
            UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_RECEIVE_FAILURE_CALLBACK, message);
        }
    };

    public static PlayMobileSDKInternal.RequestLogoutCallback onRequestLogoutCallback = new PlayMobileSDKInternal.RequestLogoutCallback(){

        @Override
        public void onSuccess(String message) {
            UnityPlayer.UnitySendMessage(gameObjectName,ON_REQUEST_LOGOUT_PLAYMOBILE_SUCCESS_CALLBAK,message);
        }

        @Override
        public void onFailure(String message) {
            super.onFailure(message);
            UnityPlayer.UnitySendMessage(gameObjectName,ON_REQUEST_LOGOUT_PLAYMOBILE_FAILURE_CALLBAK,message);
        }
    };
    public static  PlayMobileSDKInternal.RequestBinderAccountCallback onRequestBindAccountCallback = new PlayMobileSDKInternal.RequestBinderAccountCallback(){

        @Override
        public void onSuccess(String userId, String token) {
            Map<String, String> data = new HashMap<String, String>();
            data.put("userId", userId);
            data.put("token", token);
            JSONObject json = new JSONObject(data);
            //Gson gson = new Gson();
            //String jsonText = gson.toJson(data);
            String jsonText = json.toString();

            UnityPlayer.UnitySendMessage(gameObjectName,ON_REQUEST_BINDACCOUNT_SUCCESS_CALLBACK,jsonText);
           // Log.d(PPSConstants.LOG_TAG, "Unity-Bridge onRequestBindAccountCallback-onSuccess userId : " + userId + " token :" + token);
        }

        @Override
        public void onFailure(String message) {
            super.onFailure(message);

            UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_BINDACCOUNT_FAILURE_CALLBACK, message);
            //Log.d(PPSConstants.LOG_TAG, "Unity-Bridge onRequestBindAccountCallback-onFailure message : " + message );
        }
    };

    // Topup Callback Area

    public static PlayMobileSDKInternal.RequestWebTopupCallback onRequestWebTopupCallback = new PlayMobileSDKInternal.RequestWebTopupCallback (){

        @Override
        public void onSuccess() {
            String message = "Success";
            UnityPlayer.UnitySendMessage(gameObjectName,ON_REQUEST_WEBTOPUP_SUCCESS_CALLBACK,message);
        }

        @Override
        public void onFailure(String message) {
            super.onFailure(message);
            UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_WEBTOPUP_FAILURE_CALLBACK, message);
        }
    };

    public static  PlayMobileSDKInternal.RequestWalletPaymentCallback onRequestWalletPaymentCallback = new PlayMobileSDKInternal.RequestWalletPaymentCallback(){

        @Override
        public void onSuccess(String token) {
            UnityPlayer.UnitySendMessage(gameObjectName,ON_REQUEST_WALLETPAYMENT_SUCCESS_CALLBACK,token);
        }

        @Override
        public void onFailure(String message) {
            super.onFailure(message);
            UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_WALLETPAYMENT_FAILURE_CALLBACK, message);
        }
    };

    public static PlayMobileSDKInternal.RequestPaymentCallback onRequestPaymentCallback = new PlayMobileSDKInternal.RequestPaymentCallback(){

        @Override
        public void onSuccess(String token) {
            UnityPlayer.UnitySendMessage(gameObjectName,ON_REQUEST_PAYMENT_SUCCESS_CALLBACK,token);
        }

        @Override
        public void onFailure(String message) {
            super.onFailure(message);
            UnityPlayer.UnitySendMessage(gameObjectName, ON_REQUEST_PAYMENT_FAILURE_CALLBACK, message);
        }
    };

    public static void requestLoginPlaymobileWithPackageName(String partnerCode, int domainInt,String action)
    {
        PPSConstants.DomainType domainType = PPSConstants.DomainType.values()[domainInt];
        //Log.d(PPSConstants.LOG_TAG, "Unity-Bridge requestLoginPlaymobileWithPackageName partnerCode : " + partnerCode+ " action :"+action);
        PlayMobileSDK.requestLoginPlaymobileWithPackageName(currentActivity,partnerCode,domainType,action,onRequestLoginPlayMobileWithPackageNameCallback);
    }
    public static void requestAutoLoginPlayMobileWithPackageName(String partnerCode)
    {
        PlayMobileSDK.requestAutoLoginPlaymobileWithPackageName(currentActivity,partnerCode,onRequestLoginPlayMobileWithPackageNameCallback);
    }
    public static void getReceiveParameterWithIntent()
    {
       // Log.d(PPSConstants.LOG_TAG, "Unity-Bridge getReceiveParameterWithIntent " );
        PlayMobileSDK.getReceiveParameterWithIntent(currentActivity, onRequestReceiveCallback);
    }
    public static boolean isInit()
    {
        return PlayMobileSDK.isInit();
    }
    public static void requestLogoutPlayMobile()
    {
        //Log.d(PPSConstants.LOG_TAG,"Unity-Bridge requestLogoutPlayMobile");
        PlayMobileSDK.requestLogoutPlaymobile(currentActivity,onRequestLogoutCallback);
    }
    public static int getLoginStatus()
    {
        //Log.d(PPSConstants.LOG_TAG, "Unity-Bridge requestLogoutPlayMobile");
        return PlayMobileSDK.getLoginStatus(currentActivity);
    }
    public static void requestBindAccount()
    {
        //Log.d(PPSConstants.LOG_TAG,"Unity-Bridge requestBindAccount");
        PlayMobileSDK.requestBinderAccount(currentActivity, onRequestBindAccountCallback);

    }
    public static void initWalletPayment(String merchantCode,String merchantKey)
    {
        //Log.d(PPSConstants.LOG_TAG,"Unity-Bridge initWalletPayment");
        PlayMobileSDK.initWalletPayment(currentActivity, merchantCode, merchantKey);
    }
    public static void initPayment(String merchantCode,String merchantKey)
    {
       // Log.d(PPSConstants.LOG_TAG,"Unity-Bridge initPayment");
        PlayMobileSDK.initPayment(currentActivity, merchantCode, merchantKey);
    }
    public static void initPaymentAndWalletPayment(String paymentMerchantCode,String paymentMerchantKey,String walletMerchantCode,String walletMerchantKey)
    {
       // Log.d(PPSConstants.LOG_TAG,"Unity-Bridge initPaymentAndWalletPayment");
        PlayMobileSDK.initPaymentAndWalletPayment(currentActivity, paymentMerchantCode, paymentMerchantKey, walletMerchantCode, walletMerchantKey);
    }
    public static void requestWebTopup(PPSConstants.WebTopUpMode mode) {
       // Log.d(PPSConstants.LOG_TAG,"Unity-Bridge requestWebTopup");
        PlayMobileSDK.requestWebTopup(currentActivity, mode,onRequestWebTopupCallback);
    }
    public static void requestWalletPayment(String transactionId, String customerId,
                                            String itemName, String itemAmount, String itemCurrency)
    {
       // Log.d(PPSConstants.LOG_TAG,"Unity-Bridge requestWalletPayment");
        PlayMobileSDK.requestWalletPayment(currentActivity,transactionId,customerId,itemName,itemAmount,itemCurrency,onRequestWalletPaymentCallback);
    }
    public static void requestPayment(String transactionId, String customerId) {
       // Log.d(PPSConstants.LOG_TAG,"Unity-Bridge requestPayment");
        PlayMobileSDK.requestPayment(currentActivity,transactionId,customerId,onRequestPaymentCallback);
    }
}
