package com.easytwitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.easytwitter.utils.DialogBuilder;

public class EasyTwitter {

	public static final int TWITTER_REQUEST_CODE = 1000;

	// Twitter Key Value Constants

	public static final String TWITTER_CALL_BACK_URL = "twitter_call_back_url";

	public static final String TWITTER_AUTHORIZE_URL = "twitter_authorize_url";

	public static final String TWITTER_AUTH_TOKEN = "twitter_auth_token";

	public static final String TWITTER_AUTH_SECRET_TOKEN = "twitter_auth_token";

	public static final String TWITTER_DATA_OBJECT = "twitter_data_object";

	public static final String TWITTER_IS_LOGIN = "twitter_is_login";

	public static final String TWITTER_USER_ID = "twitter_user_id";

	public static final String TWITTER_SCREEN_NAME = "twitter_screen_name";

	// Class level objects

	private static String _CallBackUrl = null;

	private static EasyTwitter _EasyTwitterReference = null;

	static SharedPreferences _TwitterSharedPereference = null;

	static Twitter _Twitter = null;

	private EasyTwitter(Context context, String consumerKey,
			String consumerSecretKey) {

		_TwitterSharedPereference = PreferenceManager
				.getDefaultSharedPreferences(context);
		_Twitter = TwitterFactory.getSingleton();
		_Twitter.setOAuthConsumer(consumerKey, consumerSecretKey);

	}

	public static EasyTwitter getInstance(Context context, String consumerKey,
			String consumerSecretKey, String callBackUrl) {

		if (_EasyTwitterReference == null) {
			_EasyTwitterReference = new EasyTwitter(context, consumerKey,
					consumerSecretKey);
			_CallBackUrl = callBackUrl;
		}
		return _EasyTwitterReference;

	}

	public void login(Activity activityReference) {
		if (isInternetAvailable(activityReference)) {

			if (isLogin()) {

				startActivityForAlreadyLoginUser(activityReference);

			} else {

				GetAuthorizeUrl getAuthorizeUrl = new GetAuthorizeUrl(
						activityReference);
				getAuthorizeUrl.execute();
			}
		} else {
			Toast.makeText(activityReference, R.string.internet_not_available,
					Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isInternetAvailable(Activity activityReference) {
		ConnectivityManager connMgr = (ConnectivityManager) activityReference
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	class GetAuthorizeUrl extends AsyncTask<Void, Void, String> {

		private Dialog _Dialog = null;

		private Activity _ActivityReference = null;

		public GetAuthorizeUrl(Activity activityReference) {

			_ActivityReference = activityReference;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			_Dialog = new DialogBuilder().BuildDialog(_ActivityReference);
			_Dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {

			RequestToken requestToken;
			String authUrl = null;
			try {
				requestToken = _Twitter.getOAuthRequestToken();
				authUrl = requestToken.getAuthorizationURL();
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			return authUrl;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {

				startActivityForLogin(result);

			} else {

				Toast.makeText(_ActivityReference, R.string.please_try_later,
						Toast.LENGTH_SHORT).show();
			}
			_Dialog.dismiss();

		}

		private void startActivityForLogin(String result) {
			Intent intent = new Intent(_ActivityReference,
					EasyTwitterAuthorizationActivity.class);
			intent.putExtra(TWITTER_IS_LOGIN, isLogin());
			intent.putExtra(TWITTER_AUTHORIZE_URL, result);
			intent.putExtra(TWITTER_CALL_BACK_URL, _CallBackUrl);
			_ActivityReference.startActivityForResult(intent,
					TWITTER_REQUEST_CODE);
		}
	}

	private void startActivityForAlreadyLoginUser(Activity activityReference) {

		Intent intent = new Intent(activityReference,
				EasyTwitterAuthorizationActivity.class);
		intent.putExtra(TWITTER_IS_LOGIN, isLogin());
		intent.putExtra(TWITTER_AUTH_TOKEN, getAuthToken());
		intent.putExtra(TWITTER_AUTH_SECRET_TOKEN, getAuthSecretToken());
		activityReference.startActivityForResult(intent, TWITTER_REQUEST_CODE);

	}

	private boolean isLogin() {

		return (getAuthToken() != null && getAuthSecretToken() != null);

	}

	private String getAuthToken() {

		return _TwitterSharedPereference.getString(TWITTER_AUTH_TOKEN, null);

	}

	private String getAuthSecretToken() {

		return _TwitterSharedPereference.getString(TWITTER_AUTH_SECRET_TOKEN,
				null);

	}
}
