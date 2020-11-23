package com.oditly.audit.inspection.ui.activty;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.network.NetworkURL;


public class ChatSupportActivity extends BaseActivity {

	private RelativeLayout mProgressBarRL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatsupport);
		initView();
		initVar();
	}

	@Override
	protected void initVar() {
		super.initVar();
	}

	@Override
	protected void initView() {
		super.initView();

		mProgressBarRL=(RelativeLayout) findViewById(R.id.ll_parent_progress);
		WebView webView=(WebView)findViewById(R.id.gameWv);

		findViewById(R.id.iv_header_left).setOnClickListener(this);
		TextView mTitle =(TextView) findViewById(R.id.tv_header_title);
		mTitle.setText(getString(R.string.text_chatsupport));


		mProgressBarRL.setVisibility(View.VISIBLE);
		WebSettings webSetting = webView.getSettings();
		webSetting.setJavaScriptEnabled(true);
		webSetting.setAllowFileAccess(true);
		webSetting.setAllowFileAccessFromFileURLs(true);
		webSetting.setDomStorageEnabled(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			webSetting.setMediaPlaybackRequiresUserGesture(false);
		}
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		 webView.loadUrl(NetworkURL.URL_CHATSUPPORT);

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);

			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mProgressBarRL.setVisibility(View.GONE);

			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId())
		{
			case R.id.iv_header_left:
				finish();
				break;
			case R.id.iv_header_right:
				finish();
				break;

		}
	}
}
