package com.nilecon.playmobilesdk.Callback;

/**
 * Created by NILECONTHAILAND on 27/8/2558.
 */
public class PlayMobileSDKInternal {

    public abstract static class RequestLoginCallback extends PlayMobileLoginCallback {
        public RequestLoginCallback() {
        }

        public abstract void onSuccess(String userId, String token);
    }

    public abstract static class RequestLogoutCallback extends PlayMobileLogoutCallback{
        public RequestLogoutCallback() {
        }

        public abstract void onSuccess(String message);
    }

    public abstract static class RequestReceiveCallback extends PlayMobileReceiveCallback {
        public RequestReceiveCallback() {
        }

        public abstract void onSuccess(String userId, String token);
    }

    public abstract static class RequestBinderAccountCallback extends PlayMobileBindAccountCallback{
        public RequestBinderAccountCallback(){

        }
        public abstract void onSuccess(String userId, String token);
    }

    public abstract static class RequestWebTopupCallback extends PlayMobileWebTopupCallback{
        public  RequestWebTopupCallback(){

        }
        public abstract void onSuccess();
    }
    public abstract static class RequestWalletPaymentCallback extends PlayMobileWalletPaymentCallback{
        public RequestWalletPaymentCallback(){

        }
        public abstract  void onSuccess(String token);
    }
    public abstract static class RequestPaymentCallback extends PlaymobilePaymentCallback{
        public RequestPaymentCallback(){

        }
        public abstract void onSuccess(String token);
    }

}
