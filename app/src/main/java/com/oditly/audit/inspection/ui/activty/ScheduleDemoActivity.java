package com.oditly.audit.inspection.ui.activty;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkServiceJSON;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ScheduleDemoActivity extends BaseActivity implements INetworkEvent {


    private EditText mFirstNameET;
    private EditText mLastNameET;
    private EditText mCompanyET;
    private EditText mPhoneNumberET;
    private EditText mEmailET;
    private EditText mDateET;

    private Calendar cal = Calendar.getInstance();
    int startYear = cal.get(Calendar.YEAR);
    int startMonth = cal.get(Calendar.MONTH);
    int startDay = cal.get(Calendar.DAY_OF_MONTH);

    private RelativeLayout mProgressBarRL;

    private TextView mPhoneNoErrorTV,mEmailErrorTV,mFirstNameErrorTV,mDateErrorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduledemo);
        AppPreferences.INSTANCE.initAppPreferences(this);
        initView();
        initVar();
    }

    @Override
    protected void initView() {
        super.initView();

       // setStatusBarColor("#FFFFFF");

        findViewById(R.id.btn_submit).setOnClickListener(this);

        findViewById(R.id.iv_header_left).setOnClickListener(this);
        mProgressBarRL=(RelativeLayout)findViewById(R.id.ll_parent_progress);
        mFirstNameET = (EditText) findViewById(R.id.et_fisrtname);
        mLastNameET = (EditText) findViewById(R.id.et_lastname);
        mCompanyET = (EditText) findViewById(R.id.et_company);
        mPhoneNumberET = (EditText) findViewById(R.id.et_phone);
        mDateET = (EditText) findViewById(R.id.et_date);
        mEmailET = (EditText) findViewById(R.id.et_email);

        mPhoneNoErrorTV = (TextView) findViewById(R.id.tv_phoneerror);
        mEmailErrorTV = (TextView) findViewById(R.id.tv_emailerror);
        mFirstNameErrorTV = (TextView) findViewById(R.id.tv_firstnameerror);
        mDateErrorTV = (TextView) findViewById(R.id.tv_dateerror);
        mDateET.setOnClickListener(this);

    }

    @Override
    protected void initVar() {
        super.initVar();



    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.iv_header_left:
                finish();
                break;
            case R.id.et_date:
                DatePickerDialog datePickerDialog=  new DatePickerDialog(this, (datePicker, i, i1, i2) ->((EditText) view).setText(String.format("%02d-%02d",i2,(datePicker.getMonth() + 1))+"-" + datePicker.getYear()),startYear,startMonth,startDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;
            case R.id.btn_submit:
                if (TextUtils.isEmpty(mFirstNameET.getText().toString()))
                    mFirstNameErrorTV.setVisibility(View.VISIBLE);
                else if (TextUtils.isEmpty(mEmailET.getText().toString()))
                    mEmailErrorTV.setVisibility(View.VISIBLE);
                else if (TextUtils.isEmpty(mPhoneNumberET.getText().toString()))
                    mPhoneNoErrorTV.setVisibility(View.VISIBLE);
                else if (TextUtils.isEmpty(mDateET.getText().toString()))
                    mDateErrorTV.setVisibility(View.VISIBLE);
                else
                    postDemoToServer();
                break;
        }
    }


    public void postDemoToServer()
    {
        if (NetworkStatus.isNetworkConnected(this))
        { try {
            mProgressBarRL.setVisibility(View.VISIBLE);
            Map<String, String> jsonObject = new HashMap<>();
            jsonObject.put("fname",mFirstNameET.getText().toString());
            jsonObject.put("lname",mLastNameET.getText().toString());
            jsonObject.put("cname",mCompanyET.getText().toString());
            jsonObject.put("email",mEmailET.getText().toString());
            jsonObject.put("phone",mPhoneNumberET.getText().toString());
            jsonObject.put("date",mDateET.getText().toString());

            NetworkService networkService = new NetworkService(NetworkURL.POST_DEMO_URL, NetworkConstant.METHOD_POST, this,this);
            networkService.call(jsonObject);
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
        mProgressBarRL.setVisibility(View.GONE);
        AppLogger.e("", "SCHEDULE DEMO : " + response);
        AppDialogs.messageDialogWithOKButton(ScheduleDemoActivity.this,getString(R.string.text_thanksforcontact));

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        AppLogger.e("", "forceUpdateError: " + errorMessage);
        AppUtils.toast(ScheduleDemoActivity.this, "Server temporary unavailable, Please try again");
        mProgressBarRL.setVisibility(View.GONE);
    }


}
