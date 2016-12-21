package com.asiasoft.playparksdk;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nilecon.playmobilesdk.R;
import com.praneat.playparksdk.internal.PPSCallback;
import com.praneat.playparksdk.internal.PPSConstants;
import com.praneat.playparksdk.internal.PPSConstants.WebTopUpMode;
import com.praneat.playparksdk.internal.PPSJavaScriptInterface;
import com.praneat.playparksdk.internal.PlayparkSDKInternal;
import com.praneat.playparksdk.internal.PlayparkSDKInternal.RequestPaymentCallback;
import com.praneat.playparksdk.internal.PlayparkSDKInternal.RequestWalletPaymentCallback;
import com.praneat.playparksdk.internal.PlayparkSDKInternal.RequestWebTopUpCallback;
import com.praneat.playparksdk.internal.http.BaseHttpRequest;
import com.praneat.playparksdk.internal.http.PaymentHttpRequest;
import com.praneat.playparksdk.internal.http.WalletPaymentHttpRequest;
import com.praneat.playparksdk.internal.http.WebTopUpHttpRequest;
import com.praneat.playparksdk.internal.payment.PPSRequestPaymentObject;
import com.praneat.playparksdk.internal.payment.PPSRequestWalletPaymentObject;
import com.praneat.playparksdk.internal.payment.PSSRequestWebTopUpObject;

public class PPSWebViewActivity extends Activity {
	
	private String _url;
	
	private int _mode;
	private String _gamePartnerCode;
	
	private Timer _timer;
	private boolean _isLoadingFinished = true;
	private boolean _isRedirect = false;
	
	private WebView _webView;
	private ProgressDialog _progressDialog;
	
	private WebTopUpHttpRequest _webTopUpHttpRequest;
	private WalletPaymentHttpRequest _walletPaymentHttpRequest;
	private PaymentHttpRequest _paymentHttpRequest;
	
	//region override method
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pps_activity_web_view);

		if (savedInstanceState == null) {
        	Bundle extras = getIntent().getExtras();
        	if(extras != null) {
        		_mode = extras.getInt("mode");
				_gamePartnerCode = extras.getString("gamePartnerCode");
        	}
		} else {
			_mode = savedInstanceState.getInt("mode");
		}

		final ImageButton closeButton = (ImageButton) findViewById(R.id.close_button);
		closeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				PPSCallback callback = PlayparkSDKInternal.INSTANCE.getCallback(_mode);
				if(callback != null) {
					String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_USER_CANCELLED, getString(R.string.pps_user_cancelled));
					callback.onFailure(failureMessage);
				}

				finish();
			}
		});

		initTitleTextView();

		initUrl();
		initWebView();
		startAction();
	}

	@Override
	public void onBackPressed() {

		PPSCallback callback = PlayparkSDKInternal.INSTANCE.getCallback(_mode);
		if(callback != null) {
			String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_USER_CANCELLED, getString(R.string.pps_user_cancelled));
			callback.onFailure(failureMessage);
		}

		finish();
	}
	@Override
	public void finish() {

//		Log.v(PPSConstants.LOG_TAG, "PPSWebViewActivity.finish");
		hideProgressDialog(); // Dismiss Dialog before Activity will be destroy.
		switch(_mode) {
			case PPSConstants.ACTION_MODE_SWITCH_ACCOUNT:
				PlayparkSDKInternal.INSTANCE.clearRequestSwitchAccountObject();
				break;
			case PPSConstants.ACTION_MODE_WEB_TOP_UP:
				clearWebTopUpHttpRequest();
				PlayparkSDKInternal.INSTANCE.clearRequestWebTopUpObject();
				break;
			case PPSConstants.ACTION_MODE_WALLET_PAYMENT:
				clearWalletPaymentHttpRequest();
				PlayparkSDKInternal.INSTANCE.clearRequestWalletPaymentObject();
				break;
			case PPSConstants.ACTION_MODE_PAYMENT:
				clearPaymentHttpRequest();
				PlayparkSDKInternal.INSTANCE.clearRequestPaymentObject();
				break;
			default:
				PlayparkSDKInternal.INSTANCE.clearRequestLoginObject();
				break;
		}

		stopTimer();

		super.finish();
	}
	//endregion

	//region private method
	private void initUrl() {

		switch(_mode) {
			case PPSConstants.ACTION_MODE_LOGIN:
				_url = PlayparkSDKInternal.INSTANCE.getLoginUrl();
				break;
			case PPSConstants.ACTION_MODE_AUTO_LOGIN:
				_url = PlayparkSDKInternal.INSTANCE.getAutoLoginUrl();
				break;
			case PPSConstants.ACTION_MODE_GAME_REQUEST_TOKEN:
				_url = PlayparkSDKInternal.INSTANCE.getGameRequestTokenUrl(_gamePartnerCode);
				break;
			case PPSConstants.ACTION_MODE_SWITCH_ACCOUNT:
				_url = PlayparkSDKInternal.INSTANCE.getSwitchAccountUrl();
				break;
			case PPSConstants.ACTION_MODE_BIND_ACCOUNT:
				_url = PlayparkSDKInternal.INSTANCE.getBindAccountUrl();
				break;
			case PPSConstants.ACTION_MODE_WEB_TOP_UP:
			case PPSConstants.ACTION_MODE_WALLET_PAYMENT:
			case PPSConstants.ACTION_MODE_PAYMENT:
				_url = "";
				break;

			default:
				_url = null;
				break;
		}

	}

	private void initTitleTextView() {

		final TextView titleTextView = (TextView) findViewById(R.id.action_bar_title_text_view);
		switch(_mode) {
			case PPSConstants.ACTION_MODE_SWITCH_ACCOUNT:
				titleTextView.setText(getString(R.string.pps_action_mode_switch_account));
				break;
			case PPSConstants.ACTION_MODE_BIND_ACCOUNT:
				titleTextView.setText(getString(R.string.pps_action_mode_bind_account));
				break;
			case PPSConstants.ACTION_MODE_WEB_TOP_UP:
				titleTextView.setText(getString(R.string.pps_action_mode_web_top_up));
				break;
			case PPSConstants.ACTION_MODE_WALLET_PAYMENT:
				titleTextView.setText(getString(R.string.pps_action_mode_wallet_payment));
				break;
			case PPSConstants.ACTION_MODE_PAYMENT:
				titleTextView.setText(getString(R.string.pps_action_mode_payment));
				break;
			default:
				titleTextView.setText(getString(R.string.pps_action_mode_login));
				break;
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {

		_webView = (WebView) findViewById(R.id.web_view);
		PPSJavaScriptInterface jsInterface = new PPSJavaScriptInterface(com.asiasoft.playparksdk.PPSWebViewActivity.this, _webView, _mode);
		//PPSJavaScriptInterface jsInterface = new PPSJavaScriptInterface(this, _webView, _mode);
		_webView.addJavascriptInterface(jsInterface, PPSConstants.JS_INTERFACE_NAME);
		_webView.setWebViewClient(new WebViewClientAdapter());
		_webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		_webView.setInitialScale(1);

		_webView.setWebChromeClient(new WebChromeClient());

		WebSettings webviewSettings = _webView.getSettings();
		webviewSettings.setJavaScriptEnabled(true);
		webviewSettings.setAppCacheEnabled(false);
		webviewSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webviewSettings.setLoadWithOverviewMode(true);
		webviewSettings.setUseWideViewPort(true);
		webviewSettings.setDomStorageEnabled(true);
	}

	private void startAction() {

		//Log.d(PPSConstants.LOG_TAG, "PPSWebViewActivity.Url: " + _url);
		if(_url != null) {

			showProgressDialog(true);

			switch (_mode) {
				case PPSConstants.ACTION_MODE_WEB_TOP_UP:
					getWebTopUpData();
					break;
				case PPSConstants.ACTION_MODE_WALLET_PAYMENT:
					getWalletPaymentData();
					break;
				case PPSConstants.ACTION_MODE_PAYMENT:
					getPaymentData();
					break;
				default :
					_webView.loadUrl(_url);
			}

		} else {

			AlertDialog.Builder dialog = new AlertDialog.Builder(com.asiasoft.playparksdk.PPSWebViewActivity.this);
			dialog.setTitle(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_invalid_action_mode_title));
			dialog.setMessage(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_invalid_action_mode_message));
			dialog.setIcon(android.R.drawable.ic_dialog_alert);
			dialog.setCancelable(false);
			dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,
						int whichButton) {
					finish();
				}
			});

			dialog.show();
		}
	}

	private void getWebTopUpData() {

//		Log.v(PPSConstants.LOG_TAG, "PPSWebViewActivity.getWebTopUpData");
		PSSRequestWebTopUpObject object = PlayparkSDKInternal.INSTANCE.getRequestWebTopUpObject();

		if(object != null) {

			final RequestWebTopUpCallback callback = object.getRequestWebTopUpCallback();

			String partnerCode = PlayparkSDKInternal.INSTANCE.getPartnerCode();
			final WebTopUpMode mode = object.getWebTopUpMode();

			clearWebTopUpHttpRequest();

			_webTopUpHttpRequest = new WebTopUpHttpRequest(com.asiasoft.playparksdk.PPSWebViewActivity.this);
			_webTopUpHttpRequest.getWebTopUpData(partnerCode, mode, new WebTopUpHttpRequest.GetWebTopUpDataCallback() {

				@Override
				public void onSuccess(int code, String message, String token, String url,
						WebTopUpMode mode) {

//					Log.v(PPSConstants.LOG_TAG, "PPSWebViewActivity.getWebTopUpData.onSuccess ==> code: " + code + " ,token: " + token + " ,url: " + url);
					if(code == WalletPaymentHttpRequest.SUCCESS) {

						_url = url;

						switch (mode) {
						case WEB_VIEW:
							_webView.loadUrl(_url);
							break;
						default:
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(_url));
							startActivity(intent);
							finish();
							break;
						}

						if(callback != null) {
							callback.onSuccess();
						}
					} else {
						if(callback != null) {
							String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(code, message);
							callback.onFailure(failureMessage);
						}
						finish();
					}
				}

				@Override
				public void onFailure(final String message) {

//					Log.v(PPSConstants.LOG_TAG, "PPSWebViewActivity.getWebTopUpData.onFailure ==> message: " + message);
					hideProgressDialog();

					AlertDialog.Builder dialog = new AlertDialog.Builder(com.asiasoft.playparksdk.PPSWebViewActivity.this);
					dialog.setTitle(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_loading_failed_title));
					dialog.setMessage(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_loading_failed_message));
					dialog.setIcon(android.R.drawable.ic_dialog_info);
					dialog.setCancelable(false);
					dialog.setPositiveButton(getString(R.string.pps_retry_button), new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {

							showProgressDialog(true);
							getWebTopUpData();
						}
					});

					dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {

							if(callback != null) {
								callback.onFailure(message);
							}
							finish();
						}
					});

					dialog.show();
				}
			});
		} else {

			AlertDialog.Builder dialog = new AlertDialog.Builder(com.asiasoft.playparksdk.PPSWebViewActivity.this);
			dialog.setTitle(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_invalid_config_title));
			dialog.setMessage(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_invalid_config_message));
			dialog.setIcon(android.R.drawable.ic_dialog_alert);
			dialog.setCancelable(false);
			dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,
						int whichButton) {
					finish();
				}
			});

			dialog.show();
		}
	}

	private void getWalletPaymentData() {

//		Log.v(PPSConstants.LOG_TAG, "PPSWebViewActivity.getWalletPaymentData");
		PPSRequestWalletPaymentObject object = PlayparkSDKInternal.INSTANCE.getRequestWalletPaymentObject();

		if(object != null) {

			final RequestWalletPaymentCallback callback = object.getRequestWalletPaymentCallback();

			String merchantCode = PlayparkSDKInternal.INSTANCE.getWalletPaymentMerchantCode();
			String merchantKey = PlayparkSDKInternal.INSTANCE.getWalletPaymentMerchantKey();
			String transactionId = object.getTransactionId();
			String customerId = object.getCustomerId();
			String itemName = object.getItemName();
			String itemAmount = object.getItemAmount();
			String itemCurrency = object.getItemCurrency();

			clearWalletPaymentHttpRequest();

			_walletPaymentHttpRequest = new WalletPaymentHttpRequest();
			_walletPaymentHttpRequest.getWalletPaymentData(merchantCode, merchantKey, transactionId, customerId, itemName, itemAmount, itemCurrency, new WalletPaymentHttpRequest.GetWalletPaymentDataCallback() {

				@Override
				public void onSuccess(int code, String message, String token, String url) {

//					Log.v(PPSConstants.LOG_TAG, "PPSWebViewActivity.getWalletPaymentData.onSuccess ==> code: " + code + " ,token: " + token + " ,url: " + url);
					if(code == WalletPaymentHttpRequest.SUCCESS) {
						_url = url;
						_webView.loadUrl(_url);
					} else {
						if(callback != null) {
							String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(code, message);
							callback.onFailure(failureMessage);
						}
						finish();
					}
				}

				@Override
				public void onFailure(final String message) {

//					Log.v(PPSConstants.LOG_TAG, "PPSWebViewActivity.getWalletPaymentData.onFailure ==> message: " + message);
					hideProgressDialog();

					AlertDialog.Builder dialog = new AlertDialog.Builder(com.asiasoft.playparksdk.PPSWebViewActivity.this);
					dialog.setTitle(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_loading_failed_title));
					dialog.setMessage(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_loading_failed_message));
					dialog.setIcon(android.R.drawable.ic_dialog_info);
					dialog.setCancelable(false);
					dialog.setPositiveButton(getString(R.string.pps_retry_button), new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {

							showProgressDialog(true);
							getWalletPaymentData();
						}
					});

					dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {

							if(callback != null) {
								callback.onFailure(message);
							}
							finish();
						}
					});

					dialog.show();
				}
			});
		} else {

			AlertDialog.Builder dialog = new AlertDialog.Builder(com.asiasoft.playparksdk.PPSWebViewActivity.this);
			dialog.setTitle(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_invalid_config_title));
			dialog.setMessage(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_invalid_config_message));
			dialog.setIcon(android.R.drawable.ic_dialog_alert);
			dialog.setCancelable(false);
			dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,
						int whichButton) {
					finish();
				}
			});

			dialog.show();
		}
	}

	private void getPaymentData() {

//		Log.v(PPSConstants.LOG_TAG, "PPSWebViewActivity.getPaymentData");
		PPSRequestPaymentObject object = PlayparkSDKInternal.INSTANCE.getRequestPaymentObject();

		if(object != null) {

			final RequestPaymentCallback callback = object.getRequestPaymentCallback();

			String merchantCode = PlayparkSDKInternal.INSTANCE.getPaymentMerchantCode();
			String merchantKey = PlayparkSDKInternal.INSTANCE.getPaymentMerchantKey();
			String transactionId = object.getTransactionId();
			String customerId = object.getCustomerId();

			clearPaymentHttpRequest();

			_paymentHttpRequest = new PaymentHttpRequest();
			_paymentHttpRequest.getPaymentData(merchantCode, merchantKey, transactionId, customerId, new PaymentHttpRequest.GetPaymentDataCallback() {

				@Override
				public void onSuccess(int code, String message, String token, String url) {

//					Log.v(PPSConstants.LOG_TAG, "PPSWebViewActivity.getPaymentData.onSuccess ==> code: " + code + " ,token: " + token + " ,url: " + url);
					if(code == WalletPaymentHttpRequest.SUCCESS) {
						_url = url;
						_webView.loadUrl(_url);
					} else {
						if(callback != null) {
							String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(code, message);
							callback.onFailure(failureMessage);
						}
						finish();
					}
				}

				@Override
				public void onFailure(final String message) {

//					Log.v(PPSConstants.LOG_TAG, "PPSWebViewActivity.getPaymentData.onFailure ==> message: " + message);
					hideProgressDialog();

					AlertDialog.Builder dialog = new AlertDialog.Builder(com.asiasoft.playparksdk.PPSWebViewActivity.this);
					dialog.setTitle(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_loading_failed_title));
					dialog.setMessage(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_loading_failed_message));
					dialog.setIcon(android.R.drawable.ic_dialog_info);
					dialog.setCancelable(false);
					dialog.setPositiveButton(getString(R.string.pps_retry_button), new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {

							showProgressDialog(true);
							getPaymentData();
						}
					});

					dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {

							if(callback != null) {
								callback.onFailure(message);
							}
							finish();
						}
					});

					dialog.show();
				}
			});
		} else {

			AlertDialog.Builder dialog = new AlertDialog.Builder(com.asiasoft.playparksdk.PPSWebViewActivity.this);
			dialog.setTitle(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_invalid_config_title));
			dialog.setMessage(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_invalid_config_message));
			dialog.setIcon(android.R.drawable.ic_dialog_alert);
			dialog.setCancelable(false);
			dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,
						int whichButton) {
					finish();
				}
			});

			dialog.show();
		}
	}

	private void clearWebTopUpHttpRequest() {
		clearHttpRequest(_webTopUpHttpRequest);
	}

	private void clearWalletPaymentHttpRequest() {
		clearHttpRequest(_walletPaymentHttpRequest);
	}

	private void clearPaymentHttpRequest() {
		clearHttpRequest(_paymentHttpRequest);
	}

	private void clearHttpRequest(BaseHttpRequest request) {

		if (request != null) {
			request.abort();
			request = null;
		}
	}

	private void showProgressDialog(boolean isForceShow) {

		if(isForceShow) {
			hideProgressDialog();
		}

		if(_progressDialog == null) {

			_progressDialog = new ProgressDialog(com.asiasoft.playparksdk.PPSWebViewActivity.this);
			_progressDialog.setTitle(getString(R.string.pps_progress_title));
			_progressDialog.setMessage(getString(R.string.pps_progress_message));
			_progressDialog.setIndeterminate(true);
			_progressDialog.setCancelable(true);
			_progressDialog.setCanceledOnTouchOutside(false);
			_progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {

					PPSCallback callback = PlayparkSDKInternal.INSTANCE.getCallback(_mode);
					if(callback != null) {
						String failureMessage = PlayparkSDKInternal.INSTANCE.getFailureMessage(PPSConstants.ERROR_CODE_USER_CANCELLED, getString(R.string.pps_user_cancelled));
						callback.onFailure(failureMessage);
					}

					finish();
				}
			});
			if(!PPSWebViewActivity.this.isFinishing())
				_progressDialog.show();
		}
    }

	private void  hideProgressDialog() {

        if(!PPSWebViewActivity.this.isFinishing()&&_progressDialog != null) {

            _progressDialog.dismiss();
            _progressDialog = null;
        }
    }

	@Override
	protected void onPause() {
		hideProgressDialog();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		hideProgressDialog();
		super.onDestroy();
	}

	private void alertTimeoutDialog() {

		//Make sure Activity sill running
		if(!PPSWebViewActivity.this.isFinishing()) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(com.asiasoft.playparksdk.PPSWebViewActivity.this);
			dialog.setTitle(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_loading_failed_title));
			dialog.setMessage(com.asiasoft.playparksdk.PPSWebViewActivity.this.getString(R.string.pps_dialog_loading_failed_message));
			dialog.setIcon(android.R.drawable.ic_dialog_info);
			dialog.setCancelable(false);
			dialog.setPositiveButton(android.R.string.ok, null);

			dialog.show();
		}
	}
	
	private void startTimer() {
		
		stopTimer();
		_timer = new Timer();
		_timer.schedule(new LoadURLTimeoutTask(), PPSConstants.REQUEST_TIME_OUT);
	}
	
	private void stopTimer() {
		
		if (_timer != null) {
			_timer.cancel();
			_timer = null;
		}
	}
	//endregion
	
	private class WebViewClientAdapter extends WebViewClient {
		
		@Override
		public void onPageStarted(WebView webView, String url, Bitmap favicon) {
			
//			Log.v(PPSConstants.LOG_TAG, "onPageStarted url:" + url + " ,_isLoadingFinished: " + _isLoadingFinished + " ,_isRedirect: " + _isRedirect);
			
			showProgressDialog(false);
			startTimer();
			
			_isLoadingFinished = false;
		}

		@Override
		public void onPageFinished(WebView webView, String url) {

//			Log.v(PPSConstants.LOG_TAG, "onPageFinished url:" + url + " ,_isLoadingFinished: " + _isLoadingFinished + " ,_isRedirect: " + _isRedirect);

			if (!_isRedirect) {
				_isLoadingFinished = true;
			}

			if (_isLoadingFinished && !_isRedirect) {
				stopTimer();
				hideProgressDialog();
			} else {
				_isRedirect = false;
			}
			webView.addJavascriptInterface(com.asiasoft.playparksdk.PPSWebViewActivity.this, "Android");
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView webView,
				String urlNewString) {

//			Log.v(PPSConstants.LOG_TAG, "shouldOverrideUrlLoading urlNewString:" + urlNewString + " ,_isLoadingFinished: " + _isLoadingFinished + " ,_isRedirect: " + _isRedirect);
			
			if (!_isLoadingFinished) {
				_isRedirect = true;
			}

			_isLoadingFinished = false;

			webView.loadUrl(urlNewString);

			return true;
		}
	}
	
	private class LoadURLTimeoutTask extends TimerTask {
		
		public void run() {
			
//			Log.v(PPSConstants.LOG_TAG, "LoadURLTimeoutTask" + " ,_isLoadingFinished: " + _isLoadingFinished + " ,_isRedirect: " + _isRedirect);

			com.asiasoft.playparksdk.PPSWebViewActivity.this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					if (!_isLoadingFinished) {
						
						if(_webView != null) {
							_webView.stopLoading();
						}
						
						stopTimer();
						alertTimeoutDialog();
					}
				}
			});
		}
	}
}
