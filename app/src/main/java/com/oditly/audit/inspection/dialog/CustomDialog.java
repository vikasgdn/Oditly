package com.oditly.audit.inspection.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class CustomDialog extends Dialog {

	public CustomDialog(Context context, int layoutId) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(layoutId);
	}


}
