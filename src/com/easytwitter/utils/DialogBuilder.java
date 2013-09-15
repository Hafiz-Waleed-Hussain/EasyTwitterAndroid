package com.easytwitter.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

public class DialogBuilder {

	public DialogBuilder() {

	}
	
	public Dialog BuildDialog(Context context) {

		Dialog dialog = new ProgressDialog(context);
		dialog.setCancelable(false);
		return dialog;
	}
}
