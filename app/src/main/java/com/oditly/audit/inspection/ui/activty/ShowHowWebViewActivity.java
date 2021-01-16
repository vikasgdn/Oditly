package com.oditly.audit.inspection.ui.activty;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.util.AppConstant;

public class ShowHowWebViewActivity extends BaseActivity  {

    private String mDocumentURL;
    private RelativeLayout mProgressBar;
    private WebView webView;
    private int mCounter=0;

    public static void start(Context context, String data) {
        Intent i = new Intent(context, ShowHowWebViewActivity.class);
        i.putExtra(AppConstant.FILE_URL, data);
        context.startActivity(i);
    }




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
        textView.setText(getString(R.string.text_ref_image));
        mProgressBar = (RelativeLayout) findViewById(R.id.ll_parent_progress);
        mProgressBar.setVisibility(View.VISIBLE);
         webView = (WebView) findViewById(R.id.webview);
        mDocumentURL=getIntent().getStringExtra(AppConstant.FILE_URL);
        String finalUrl ="https://docs.google.com/gview?embedded=true&url="+ mDocumentURL;
        String iFrameURL="<iframe src='http://docs.google.com/viewer?url="+mDocumentURL+"&embedded=true' width='100%' height='100%' style='border: none;'></iframe>";
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
       // webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
      //  webView.getSettings().setBuiltInZoomControls(true);
      //  webView.getSettings().setUseWideViewPort(true);
       // webView.getSettings().setLoadWithOverviewMode(true);
          webView.getSettings().setAllowFileAccess(true);
      //  webView.loadData(iFrameURL,"text/html", "UTF-8");
        webView.loadUrl(finalUrl);

        webView.setWebViewClient(new WebViewClient() {

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
                Log.e(":::: URL  ","onPageFinished   "+webView.getProgress());
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
