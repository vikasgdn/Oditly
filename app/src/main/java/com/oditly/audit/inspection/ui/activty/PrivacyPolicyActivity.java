package com.oditly.audit.inspection.ui.activty;

import android.annotation.TargetApi;
import android.app.MediaRouteButton;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;

public class PrivacyPolicyActivity extends BaseActivity  {

    public  static final String PRIVACYPOLICY =  "https://www.oditly.com/pdf/PrivacyPolicy.pdf";
    private RelativeLayout mProgressBar;
    private int mCounter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        initView();
        initVar();

    }

    @Override
    protected void initView() {
        super.initView();

        findViewById(R.id.iv_header_left).setOnClickListener(this);
        TextView textView=(TextView)findViewById(R.id.tv_header_title);
        String mLocation=getIntent().getStringExtra(AppConstant.FROMWHERE);
        if (mLocation.equalsIgnoreCase(AppConstant.PRIVACY_POLICY))
            textView.setText(getString(R.string.s_privacy_policy));
        else
            textView.setText(getString(R.string.s_term));

        mProgressBar = (RelativeLayout) findViewById(R.id.ll_parent_progress);
        mProgressBar.setVisibility(View.VISIBLE);
        WebView wb = (WebView) findViewById(R.id.webview);
        wb.getSettings().setJavaScriptEnabled(true);
       // wb.loadUrl("https://docs.google.com/gview?embedded=true&url="+PRIVACYPOLICY);
        if (mLocation.equalsIgnoreCase(AppConstant.PRIVACY_POLICY))
            wb.loadUrl(NetworkURL.URL_PRIVACY_POLICY);
        else
            wb.loadUrl(NetworkURL.URL_TERM_AND_CONDITION);
        wb.setWebViewClient(new WebViewClient() {


            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
            {
                Log.e(":::: URL SHOULD ",""+request.getUrl().toString());
                view.loadUrl(request.getUrl().toString());
                return false;            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                mCounter++;
                Log.e(":::: URL  ","onPageStarted");

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mCounter<3)
                    view.loadUrl(url);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Log.e(":: onReceivedHttpError ",""+errorResponse);

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(":::: URL ERROR ",""+error);
            }
        });
}

    @Override
    protected void initVar() {
        super.initVar();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        finish();

    }



}
