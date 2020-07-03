package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountProfileActivity extends BaseActivity implements INetworkEvent {

    private TextView mNameLetterTV;
    private TextView mNameTV;
    private TextView mEmailTV;
    private RelativeLayout mProgressBarRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        AppPreferences.INSTANCE.initAppPreferences(this);
        initView();
        initVar();

    }
    @Override
    protected void initView() {
        super.initView();


        TextView textView=(TextView)findViewById(R.id.tv_title);
        textView.setText(getResources().getString(R.string.s_account));

        mNameLetterTV=(TextView)findViewById(R.id.tv_name_letter);
        mNameTV=(TextView)findViewById(R.id.tv_name);
        mEmailTV=(TextView)findViewById(R.id.tv_email);
        mProgressBarRL=(RelativeLayout)findViewById(R.id.ll_parent_progress);



        findViewById(R.id.tv_signout).setOnClickListener(this);
        findViewById(R.id.tv_support).setOnClickListener(this);
        findViewById(R.id.tv_changepass).setOnClickListener(this);
        findViewById(R.id.tv_privacy).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_version).setOnClickListener(this);
        findViewById(R.id.tv_language).setOnClickListener(this);
        findViewById(R.id.tv_termservice).setOnClickListener(this);

    }
    @Override
    protected void initVar() {
        super.initVar();
        mNameTV.setText(AppPreferences.INSTANCE.getUserFname(this)+AppPreferences.INSTANCE.getUserLName(this));
        mEmailTV.setText(AppPreferences.INSTANCE.getUserEmail(this));
        mNameLetterTV.setText(returnFirstLetter());
    }

    private String returnFirstLetter() {
        String name = "";
        try {

            String fName = AppPreferences.INSTANCE.getUserFname(this);
            String lName = AppPreferences.INSTANCE.getUserLName(this);
            if (fName.length() > 0)
                name = fName.substring(0, 1);
            if (lName.length() > 0)
                name = name + lName.substring(0, 1);
            return name;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return name;
        }
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_signout:
                showLogOutAlert(this);
                break;
            case R.id.tv_version:
                AppDialogs.versionDialog(this);
                break;
            case R.id.tv_language:
                AppDialogs.languageDialog(this);
                break;
            case R.id.tv_support:
                 Intent intent=new Intent(this, SupportActivity.class);
                 startActivity(intent);
                 break;
            case R.id.tv_privacy:
                AppUtils.toast(this,getString(R.string.text_coming_soon));
                break;
            case R.id.tv_termservice:
                   AppUtils.toast(this,getString(R.string.text_coming_soon));
                break;
            case R.id.tv_changepass:
                   setOTPServer();
                break;

        }


    }
    public void setOTPServer()
    {
        if (NetworkStatus.isNetworkConnected(this)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(NetworkConstant.REQ_PARAM_USER, AppPreferences.INSTANCE.getUserEmail(this));
            NetworkService networkService = new NetworkService(NetworkURL.SENDOTP, NetworkConstant.METHOD_POST, this,this);
            networkService.call(params);
        } else

            AppUtils.toast(this, getString(R.string.internet_error));

    }

    public void showLogOutAlert(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        try {

            dialog.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    logOutServerData();
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }


    private void logOutServerData()
    {
        if (NetworkStatus.isNetworkConnected(this)) {
          //  showAppProgressDialog();
            mProgressBarRL.setVisibility(View.VISIBLE);
            NetworkService networkService = new NetworkService(NetworkURL.LOGOUT, NetworkConstant.METHOD_POST, this ,this);
            networkService.call(new HashMap<String, String>());
        } else {
            AppUtils.toast(this, getString(R.string.internet_error));
        }

    }

    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response)
    {
        if(service.equalsIgnoreCase(NetworkURL.LOGOUT)) {
            try {
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (object.getString(AppConstant.RES_KEY_ERROR).equals(AppConstant.ATTRIBUTE_FALSE)) {
                    AppPreferences.INSTANCE.setLogin(false, this);
                    AppPreferences.INSTANCE.setAccessToken("", this);
                    AppUtils.toast(AccountProfileActivity.this, message);
                    AppPreferences.INSTANCE.clearPreferences();


                    Intent intent = new Intent(AccountProfileActivity.this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    AppUtils.toast(AccountProfileActivity.this, message);
                    if (object.getInt(AppConstant.RES_KEY_CODE) == AppConstant.ERROR) {
                        finish();
                        AppPreferences.INSTANCE.setLogin(false, this);
                        AppPreferences.INSTANCE.setAccessToken("", this);
                        AppPreferences.INSTANCE.clearPreferences();
                        Intent intent = new Intent(AccountProfileActivity.this, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    AppLogger.e("TAG", "Forget Password Response: " + message);
                    AppUtils.toast(AccountProfileActivity.this, message);
                    Intent intent = new Intent(AccountProfileActivity.this, ResetPasswordScreen.class);
                    intent.putExtra("username", AppPreferences.INSTANCE.getUserEmail(this));
                    startActivity(intent);
                }else
                    AppUtils.toast(AccountProfileActivity.this, message);
            }
            catch (Exception e){
                e.printStackTrace();
                AppUtils.toast(this, getString(R.string.oops));

            }
        }
        mProgressBarRL.setVisibility(View.GONE);


    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(this, getString(R.string.oops));
        mProgressBarRL.setVisibility(View.GONE);

    }




}
