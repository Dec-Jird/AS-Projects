package com.nilecon.playmobilesdk.Callback;

import android.util.Log;

/**
 * Created by Rawit on 2/10/2016 AD.
 */
public abstract class PlayMobileBindAccountCallback {
    public PlayMobileBindAccountCallback(){

    }
    public void onFailure(String message) {
        Log.e("PlayparkSDKBindAccount", "onFailure: " + message);
    }
}
