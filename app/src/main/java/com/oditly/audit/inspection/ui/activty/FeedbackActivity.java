package com.oditly.audit.inspection.ui.activty;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends BaseActivity implements INetworkEvent {


    private EditText mFeedbackET;


    private TextView mFeedbackErrorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
        initVar();
    }

    @Override
    protected void initView() {
        super.initView();


        findViewById(R.id.btn_submit).setOnClickListener(this);
        findViewById(R.id.iv_header_left).setOnClickListener(this);
        mFeedbackET = (EditText) findViewById(R.id.et_feedback);
        mFeedbackErrorTV = (TextView) findViewById(R.id.tv_feedback_error);
        TextView textView=(TextView)findViewById(R.id.tv_header_title);
        textView.setText(getResources().getString(R.string.text_feedback));


    }

    @Override
    protected void initVar() {
        super.initVar();



    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.iv_header_left:
                finish();
                break;
            case R.id.btn_submit:
               /* if (TextUtils.isEmpty(mFeedbackET.getText().toString()))
                    mFeedbackErrorTV.setVisibility(View.VISIBLE);
                else*/
                    postDemoToServer();
                break;
        }
    }


    public void postDemoToServer()
    {
        if (NetworkStatus.isNetworkConnected(this))
        { try {

                AppUtils.toast(this,"Coming Soon..");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        } else
        {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }


    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {
        AppLogger.e("", "SCHEDULE DEMO : " + response);
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        AppLogger.e("", "forceUpdateError: " + errorMessage);
        AppUtils.toast(FeedbackActivity.this, "Server temporary unavailable, Please try again");

    }


}
