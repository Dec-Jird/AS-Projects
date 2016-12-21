package com.nilecon.playmobilesdk.Callback;

import android.util.Log;

/**
 * Created by NILECONTHAILAND on 1/9/2558.
 */
public class PlayMobileLogoutCallback {
    public PlayMobileLogoutCallback() {
    }

    public void onFailure(String message) {
        Log.e("PlayparkLoginSDK", "onFailure: " + message);
    }
}
