package com.tnyoo.facebook;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;

import java.util.Arrays;

public class MainActivity extends Activity {

    private static final String TAG = "Facebook_AND";
    private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";

    public void debugLog(String string) {
        // TODO Auto-generated method stub
        Log.i(TAG, "[FB AND] - " + string);
        Toast.makeText(this.getBaseContext(), string, Toast.LENGTH_SHORT).show();
    }

    public static final String[] permissionList = {"public_profile", "user_friends", "email"};
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private LoginButton loginButton;
    private boolean isResumed = false;
    private boolean userSkippedLogin = false;
    private ShareDialog shareDialog;
    private AppInviteDialog appInviteDialog;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        // Initialize the SDK before executing any other operations,
        initFBSdk(activity);
        if (savedInstanceState != null) {
            userSkippedLogin = savedInstanceState.getBoolean(USER_SKIPPED_LOGIN_KEY);
        }

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (isResumed) {
//                    AccessToken.getCurrentAccessToken()
                    if (currentAccessToken != null) {
                        debugLog("is Resumed: User Already Login. currentAccessToken: " + currentAccessToken);
                        String accessToken = currentAccessToken.getToken();
                        String userId = currentAccessToken.getUserId();

                        // 获取当前登录用户AccessToken
                        Log.d(TAG, "Login onSuccess. AccessToken: "
                                + accessToken + ", UserId: " + userId);

                    } else {
                        debugLog("onCurrentAccessTokenChanged: try Login. ");
                        sdkLogin();
                    }
                }
            }
        };

        setContentView(R.layout.activity_main);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(permissionList);
        shareDialog = new ShareDialog(this);
        appInviteDialog = new AppInviteDialog(this);
        registerCallbacks();
    }

    private void initFBSdk(Activity activity) {
        FacebookSdk.sdkInitialize(activity, new FacebookSdk.InitializeCallback() {

            @Override
            public void onInitialized() {
                Log.d(TAG, "onInitialized Success");
            }
        });

        this.callbackManager = CallbackManager.Factory.create();
    }

    // 初始化-登陆
    public void Init(View view) { // 联盟为应用分配的应用ID

    }

    public void sdkLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(permissionList));
    }

    public void sdkLogout() {
        LoginManager.getInstance().logOut();
    }

    public void sdkAppInvite(String appLinkUrl, String previewImageUrl) {
        initFBSdk(activity);

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            appInviteDialog.show(this, content);
        }
    }

    public void sdkShareLink(String contentTitle, String contentDescription, String contentUrl) {
        initFBSdk(activity);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(contentTitle)
                    .setContentDescription(contentDescription)
                    .setContentUrl(Uri.parse(contentUrl))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    private void registerCallbacks() {
        // 注册登录回调
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onCancel() {
                        // App code
                        debugLog("Login onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        debugLog("Login onError. exception: " + exception.toString());
                    }

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String accessToken = loginResult.getAccessToken().getToken();
                        String userId = loginResult.getAccessToken().getUserId();

                        // 获取当前登录用户AccessToken
                        Log.d(TAG, "Login onSuccess. AccessToken: "
                                + accessToken + ", UserId: " + userId);
                    }
                });

        appInviteDialog.registerCallback(callbackManager, new FacebookCallback<AppInviteDialog.Result>() {
            @Override
            public void onSuccess(AppInviteDialog.Result result) {
                debugLog("AppInvite onSuccess. result: " + result.getData());
            }

            @Override
            public void onCancel() {
                debugLog("AppInvite onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                debugLog("AppInvite onError. facebook Exception : " + error.toString());
            }
        });

        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            /**
             * Called when the dialog completes without error.
             * @param result Result from the dialog
             */
            @Override
            public void onSuccess(Sharer.Result result) {
                debugLog("Share onSuccess. AccessToken: "
                        + result.getPostId());
            }

            /**
             * Called when the dialog is canceled.
             */
            @Override
            public void onCancel() {
                debugLog("Share onCancel");
            }

            /**
             * Called when the dialog finishes with an error.
             * @param error The error that occurred
             */
            @Override
            public void onError(FacebookException error) {
                debugLog("Share onError. facebook Exception : " + error.toString());
            }
        });

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onCancel() {
                // App code
                debugLog("Login onCancel");
            }

            @Override
            public void onError(FacebookException e) {
                debugLog("Login onError. facebook Exception : " + e.toString());
            }

            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                String userId = loginResult.getAccessToken().getUserId();

                // 获取当前登录用户AccessToken
                Log.d(TAG, "Login onSuccess. AccessToken: "
                        + accessToken + ", UserId: " + userId);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
        AppEventsLogger.activateApp(this);

        if (AccessToken.getCurrentAccessToken() != null) {
            // if the user already logged in, try to show the selection fragment
            debugLog("onResume: User Already Login. currentAccessToken: " + AccessToken.getCurrentAccessToken());
            userSkippedLogin = false;
        } else if (userSkippedLogin) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!FacebookSdk.isInitialized()) {
            initFBSdk(activity);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        debugLog("onActivityResult. data: " + data.getDataString());
    }

    private boolean isLoggedIn() {
        AccessToken accesstoken = AccessToken.getCurrentAccessToken();
        return !(accesstoken == null || accesstoken.getPermissions().isEmpty());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    public void Login(View view) {
        sdkLogin();
    }

    public void Logout(View view) {
        sdkLogout();
    }

    public void AppInvite(View view) {
        String appLinkUrl, previewImageUrl;
        appLinkUrl = "https://play.google.com/store/apps/details?id=com.playpark.dot";
        previewImageUrl = "https://lh3.googleusercontent.com/n_TleFiN1Jbg-VZuL249EtZfstkh9kSDRnLP15XkYDd4e3ZbWwQzHNRW893dn7GsTpY=h900";
        sdkAppInvite(appLinkUrl, previewImageUrl);
    }

    public void ShareLink(View view) {
        String contentTitle, contentDescription, contentUrl;

        contentTitle = "Hello Facebook";
        contentDescription = "The 'Hello Facebook' sample  showcases simple Facebook integration";
        contentUrl = "https://play.google.com/store/apps/details?id=com.playpark.dot";
        sdkShareLink(contentTitle, contentDescription, contentUrl);
    }

}
