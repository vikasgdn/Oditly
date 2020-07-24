package com.oditly.audit.inspection.ui.activty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class ActionCreateActivity extends BaseActivity implements INetworkEvent {


    private RelativeLayout mProgressBarRL;
    private EditText mTitleET;
    private  TextView mPlanedDateET;
    private Spinner mActionTypeSPN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_action);
        initView();
        initVar();
       getActionFilterFromServer("");

    }

    @Override
    protected void initView() {
        super.initView();

        findViewById(R.id.iv_header_left).setOnClickListener(this);
        findViewById(R.id.btn_create).setOnClickListener(this);
        findViewById(R.id.tv_moreoptions).setOnClickListener(this);

        TextView textView=(TextView)findViewById(R.id.tv_header_title);
        textView.setText(getString(R.string.text_create_action));
        mProgressBarRL=(RelativeLayout)findViewById(R.id.ll_parent_progress);

        mPlanedDateET=(TextView) findViewById(R.id.tv_plandate);
        mTitleET=(EditText) findViewById(R.id.et_title);



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
            case R.id.tv_moreoptions:
                Intent  intent=new Intent(this,ActionCreateMoreActivity.class);
                startActivityForResult(intent,1001);
                break;
            case R.id.btn_create:
                break;
        }

    }
    private void getActionFilterFromServer(String auditid)
    {
        if (NetworkStatus.isNetworkConnected(this)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            String url=NetworkURL.GET_ACTION_FILTER_URL+"23";
            Log.e("team url==> ",""+url);
            NetworkService networkService = new NetworkService(url, NetworkConstant.METHOD_GET, this,this);
            networkService.call( new HashMap<String, String>());
        } else
        {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }



    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response)
    {
        Log.e("Response==>",""+response);

        try {
            JSONObject object = new JSONObject(response);



        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.toast(this, getString(R.string.oops));
        }


        mProgressBarRL.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage)
    {
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(this, getString(R.string.oops));
        mProgressBarRL.setVisibility(View.GONE);

    }
}
