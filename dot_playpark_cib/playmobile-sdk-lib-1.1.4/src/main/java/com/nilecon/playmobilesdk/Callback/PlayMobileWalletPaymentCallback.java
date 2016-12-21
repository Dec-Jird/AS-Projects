package com.nilecon.playmobilesdk.Callback;

import android.util.Log;

/**
 * Created by Rawit on 2/10/2016 AD.
 */
public abstract  class PlayMobileWalletPaymentCallback {
    public PlayMobileWalletPaymentCallback(){

    }
    public void onFailure(String message) {
        Log.e("PlayparkWalletPayment", "onFailure: " + message);
    }
}
