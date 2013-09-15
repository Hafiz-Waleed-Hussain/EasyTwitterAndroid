EasyTwitterAndroid
==================
Now i start a new series of libraries with the prefix name Easy. In which i always try to convert difficult code into simple wrapper.
In this library i try to make Twitter integration simple in Android apps.
At this time this library only support to Login feature.
Now after including this library in your project. Only do these things.

Add permissions in manifest.

    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />

Add this activity into manifest under application tag.

      <activity android:name="com.easytwitter.EasyTwitterAuthorizationActivity"
            android:screenOrientation="portrait"></activity>
            
Now add this code into your activity where you want to integrate Twitter login.

  private EasyTwitter _EasyTwitter = null;
  
  _EasyTwitter = EasyTwitter.getInstance(this, _ConsumerKey, _ConsumerSecretKey, _CallBackUrl);
  
Now when user press the Twitter Login button in your activity. Only call this function.
  
  _EasyTwitter.login(this);


Now last step. Override your onActivityResult method.

  @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if( resultCode == RESULT_OK){
			
			if( requestCode == EasyTwitter.TWITTER_REQUEST_CODE){
				
				if(data != null){

					EasyTwitterDataObject twitterData = (EasyTwitterDataObject) data.getSerializableExtra(EasyTwitter.TWITTER_DATA_OBJECT);
					Log.d("Check",twitterData.toString());

				}
			}
		}
	}
	
Now what you want :). 
The twitter login complete. 
Simple.

