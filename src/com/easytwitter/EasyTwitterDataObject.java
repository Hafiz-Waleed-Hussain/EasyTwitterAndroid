package com.easytwitter;

import java.io.Serializable;

public class EasyTwitterDataObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _UserId;
	private String _ScreenName;
	private String _AuthToken;
	private String _AuthSecretToken;
	
	public EasyTwitterDataObject(String _UserId, String _ScreenName,
			String _AuthToken, String _AuthSecretToken) {
		super();
		this._UserId = _UserId;
		this._ScreenName = _ScreenName;
		this._AuthToken = _AuthToken;
		this._AuthSecretToken = _AuthSecretToken;
	}

	public String get_UserId() {
		return _UserId;
	}

	public String get_ScreenName() {
		return _ScreenName;
	}

	public String get_AuthToken() {
		return _AuthToken;
	}

	public String get_AuthSecretToken() {
		return _AuthSecretToken;
	}

	@Override
	public String toString() {
		return "EasyTwitterDataObject [_UserId=" + _UserId + ", _ScreenName="
				+ _ScreenName + ", _AuthToken=" + _AuthToken
				+ ", _AuthSecretToken=" + _AuthSecretToken + "]";
	}

	
	
}
