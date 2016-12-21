package com.nilecon.playmobilesdk.Callback;

import android.util.Log;

/**
 * Created by NILECONTHAILAND on 27/8/2558.
 */

public abstract class PlayMobileLoginCallback {
    public PlayMobileLoginCallback() {
    }

    public void onFailure(String message) {
        Log.e("PlayparkLoginSDK", "onFailure: " + message);
    }

    public void onPlayMobile(boolean canopen,String message) {
        Log.e("PlayparkLoginSDK", "onPlayMobile: " + message);
    }
}
