package com.oditly.audit.inspection.ui.activty;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

public class UpComingFeatureActivity extends BaseActivity implements INetworkEvent {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_feature);
        initView();
        initVar();
    }

    @Override
    protected void initView() {
        super.initView();

        findViewById(R.id.iv_header_left).setOnClickListener(this);
        TextView textView=(TextView)findViewById(R.id.tv_header_title);
        textView.setText(getResources().getString(R.string.text_upcominfeature));
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
        AppUtils.toast(UpComingFeatureActivity.this, "Server temporary unavailable, Please try again");

    }


}
