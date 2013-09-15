package com.easytwitter;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.easytwitter.utils.DialogBuilder;

public class EasyTwitterAuthorizationActivity extends Activity {

	private WebView _WebView = null;

	private String _TwitterCallBackUrl;

	private String _TwitterAuthorizeUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isLogin()) {

			getAccessTokenForAlreadyLoginUser();

		} else {

			initAuthorizeAndCallBackUrls();
			setUpWebView();

		}

	}

	private boolean isLogin() {

		return getIntent().getBooleanExtra(EasyTwitter.TWITTER_IS_LOGIN, false);

	}

	private void getAccessTokenForAlreadyLoginUser() {

		String authToken = getIntent().getStringExtra(
				EasyTwitter.TWITTER_AUTH_TOKEN);
		String authSecretToken = getIntent().getStringExtra(
				EasyTwitter.TWITTER_AUTH_SECRET_TOKEN);
		new GetAccessToken(authToken, authSecretToken).execute();

	}

	private void initAuthorizeAndCallBackUrls() {

		Intent getIntent = getIntent();
		_TwitterAuthorizeUrl = getIntent
				.getStringExtra(EasyTwitter.TWITTER_AUTHORIZE_URL);
		_TwitterCallBackUrl = getIntent
				.getStringExtra(EasyTwitter.TWITTER_CALL_BACK_URL);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setUpWebView() {

		_WebView = new WebView(this);
		_WebView.getSettings().setJavaScriptEnabled(true);
		_WebView.setWebViewClient(myWebViewClient);
		setContentView(_WebView);
		_WebView.loadUrl(_TwitterAuthorizeUrl);

	}

	private WebViewClient myWebViewClient = new WebViewClient() {

		private Dialog _Dialog = null;

		public void onPageStarted(WebView view, String url,
				android.graphics.Bitmap favicon) {

			_Dialog = new DialogBuilder()
					.BuildDialog(EasyTwitterAuthorizationActivity.this);
			_Dialog.show();
		};

		public void onPageFinished(WebView view, String url) {

			_Dialog.dismiss();

		};

		public void onLoadResource(WebView view, String url) {

			Uri uri = Uri.parse(url);
			Uri callbackUri = Uri.parse(_TwitterCallBackUrl);
			String loadingUrlHost = uri.getHost();
			String callbackUrlHost = callbackUri.getHost();

			if (loadingUrlHost.equals(callbackUrlHost)) {

				String token = uri.getQueryParameter("oauth_token");
				String verifier = uri.getQueryParameter("oauth_verifier");

				if (token != null) {
					_WebView.setVisibility(WebView.INVISIBLE);
					new GetAccessToken().execute(verifier);

				} else {
					super.onLoadResource(view, url);
				}
			}

		};

	};

	class GetAccessToken extends AsyncTask<String, Void, EasyTwitterDataObject> {

		private Dialog _Dialog = null;

		private String _AuthToken = null;

		private String _AuthSecretToken = null;

		public GetAccessToken() {

		}

		public GetAccessToken(String _AuthToken, String _AuthSecretToken) {

			this._AuthToken = _AuthToken;
			this._AuthSecretToken = _AuthSecretToken;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			_Dialog = new DialogBuilder()
					.BuildDialog(EasyTwitterAuthorizationActivity.this);
			_Dialog.show();
		}

		@Override
		protected EasyTwitterDataObject doInBackground(String... params) {

			AccessToken accessToken;
			EasyTwitterDataObject dataObject;

			try {

				try {

					accessToken = EasyTwitter._Twitter
							.getOAuthAccessToken(params[0]);

				} catch (ArrayIndexOutOfBoundsException e) {

					accessToken = new AccessToken(_AuthToken, _AuthSecretToken);

				}

				EasyTwitter._Twitter.setOAuthAccessToken(accessToken);

				dataObject = makeDataObject(accessToken);

			} catch (TwitterException e) {

				dataObject = null;

			} finally {

			}
			return dataObject;
		}

		@Override
		protected void onPostExecute(EasyTwitterDataObject result) {

			super.onPostExecute(result);
			_Dialog.dismiss();
			sendDataToActivity(result);

		}
	}

	private EasyTwitterDataObject makeDataObject(AccessToken accessToken) {

		String userId = accessToken.getUserId() > 0 ? accessToken.getUserId()+""
				: null;
		String screenName = accessToken.getScreenName();
		if (userId == null)
			userId = EasyTwitter._TwitterSharedPereference.getString(
					EasyTwitter.TWITTER_USER_ID, "");

		if (screenName == null)
			screenName = EasyTwitter._TwitterSharedPereference.getString(
					EasyTwitter.TWITTER_SCREEN_NAME, "");

		EasyTwitterDataObject dataObject = new EasyTwitterDataObject(userId,
				screenName, accessToken.getToken(),
				accessToken.getTokenSecret());
		saveAccessTokenToLocalStorage(dataObject);

		return dataObject;
	}

	private void saveAccessTokenToLocalStorage(EasyTwitterDataObject dataObject) {

		Editor editor = EasyTwitter._TwitterSharedPereference.edit();
		editor.putString(EasyTwitter.TWITTER_AUTH_TOKEN,
				dataObject.get_AuthToken());
		editor.putString(EasyTwitter.TWITTER_AUTH_SECRET_TOKEN,
				dataObject.get_AuthSecretToken());
		editor.putString(EasyTwitter.TWITTER_USER_ID, dataObject.get_UserId());
		editor.putString(EasyTwitter.TWITTER_SCREEN_NAME,
				dataObject.get_ScreenName());

		editor.commit();
	}

	private void sendDataToActivity(EasyTwitterDataObject result) {
		Intent intent = null;

		if (result != null) {

			intent = new Intent();
			intent.putExtra(EasyTwitter.TWITTER_DATA_OBJECT, result);
		}

		setResult(RESULT_OK, intent);
		finish();
	}

}
