//package com.nilecon.playmobilesdk;
//
//import android.app.Application;
//import android.content.Context;
//import android.content.SharedPreferences;
//
//public class SharedPrefs {
//    protected final static String TAG = SharedPrefs.class.getSimpleName();
//
//    private static SharedPrefs instance;
//
//    private Context context;
//    private SharedPreferences prefs;
//
//    private SharedPrefs(Context context) {
//        if (context instanceof Application) {
//            this.context = context;
//        } else {
//            this.context = context.getApplicationContext();
//        }
//        prefs = context.getSharedPreferences("SDKPlayMobile", Context.MODE_PRIVATE);
//    }
//
//    public static SharedPrefs getInstance(Context context) {
//        if (instance == null) {
//            instance = new SharedPrefs(context);
//        }
//        return instance;
//    }
//
//    public void clearPrefs() {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.clear();
//        editor.commit();
//    }
//
//    public void isPlayMobile(boolean isPlayMobile) {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean("isPlayMobile", isPlayMobile);
//        editor.commit();
//    }
//
//    public boolean getPlayMobile() {
//        return prefs.getBoolean("isPlayMobile", false);
//    }
//
//    public void setToken(String Token) {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("tokenUser", Token);
//        editor.commit();
//    }
//
//    public String getToken() {
//        return prefs.getString("tokenUser", null);
//    }
//
//    public void setUserId(String UserId) {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("userIdUser", UserId);
//        editor.commit();
//    }
//
//    public String getUserId() {
//        return prefs.getString("userIdUser", null);
//    }
//
//    public void setApiKey(String ApiKey) {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("apikey", ApiKey);
//        editor.commit();
//    }
//
//    public String getApiKey() {
//        return prefs.getString("apikey", null);
//    }
//}
