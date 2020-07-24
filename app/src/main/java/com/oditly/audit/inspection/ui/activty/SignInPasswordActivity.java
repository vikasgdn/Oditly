package com.oditly.audit.inspection.ui.activty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.model.signin.SignInRootObject;
import com.oditly.audit.inspection.model.signin.SignInRootObject;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignInPasswordActivity extends BaseActivity implements INetworkEvent {

    private Button mSignInBtn;
    private TextInputEditText mPasswordET;
    private ImageView mEyesIV;
    private TextView mPassworderrorTV;
    private String mEmailID;
    private String mForgotEmail="";
    private RelativeLayout mSpinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mEmailID=getIntent().getStringExtra(AppConstant.EMAIL);
        initView();
        initVar();

    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.ti_email).setVisibility(View.GONE);
        findViewById(R.id.tv_emailerror).setVisibility(View.GONE);
        findViewById(R.id.tv_forgotpass).setOnClickListener(this);
        findViewById(R.id.iv_header_left).setOnClickListener(this);
        mPassworderrorTV=(TextView)findViewById(R.id.tv_passerror);
        mSpinKitView=(RelativeLayout)findViewById(R.id.ll_parent_progress);

        mSignInBtn=(Button)findViewById(R.id.btn_signin);
        mSignInBtn.setText(getResources().getString(R.string.s_enter));

        mPasswordET=(TextInputEditText)findViewById(R.id.et_password);
        mEyesIV=(ImageView)findViewById(R.id.iv_eyes);

        mSignInBtn.setOnClickListener(this);
        mEyesIV.setOnClickListener(this);
    }

    @Override
    protected void initVar() {
        super.initVar();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_signin:
                if(TextUtils.isEmpty(mPasswordET.getText().toString()))
                    mPassworderrorTV.setVisibility(View.VISIBLE);
                else
                {
                    mPassworderrorTV.setVisibility(View.GONE);
                    AppUtils.hideKeyboard(this, view);
                    validateuserCredentialsData();

                }
                // overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
            case R.id.tv_forgotpass:
                AppDialogs.showForgotPassword(this);
                break;
            case R.id.iv_header_left:
                finish();
                break;
            case R.id.iv_eyes:
                if(mEyesIV.isSelected()) {
                    mEyesIV.setSelected(false);
                    mPasswordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                else {
                    mEyesIV.setSelected(true);
                    mPasswordET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                break;

        }

    }


    public void setOTPServer(String userEmail)
    {
        if (NetworkStatus.isNetworkConnected(this)) {
            mForgotEmail=userEmail;
           // showAppProgressDialog();
            mSpinKitView.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(NetworkConstant.REQ_PARAM_USER, userEmail);
            NetworkService networkService = new NetworkService(NetworkURL.SENDOTP, NetworkConstant.METHOD_POST, this,this);
            networkService.call(params);
        } else

            AppUtils.toast(this, getString(R.string.internet_error));

    }
    private void validateuserCredentialsData() {
        if (NetworkStatus.isNetworkConnected(this)) {
          //  showAppProgressDialog();
            mSpinKitView.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(NetworkConstant.REQ_PARAM_USER, mEmailID);
            params.put(NetworkConstant.REQ_PARAM_PASSWORD, mPasswordET.getText().toString());
            params.put(NetworkConstant.REQ_PARAM_MOBILE, "1");
            NetworkService networkService = new NetworkService(NetworkURL.SIGNIN, NetworkConstant.METHOD_POST, this,this);
            networkService.call(params);
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

        if(service.equalsIgnoreCase(NetworkURL.SIGNIN)) {
            try {
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    SignInRootObject signInRootObject = new GsonBuilder().create().fromJson(object.toString(), SignInRootObject.class);
                    if (signInRootObject.getData() != null) {
                        AppUtils.toast(this, message);
                        AppPreferences.INSTANCE.setLogin(true,this);
                        AppPreferences.INSTANCE.setAccessToken(signInRootObject.getData().getAccess_token(),this);
                        AppPreferences.INSTANCE.setUserRole(signInRootObject.getData().getRole_id(), this);
                        AppPreferences.INSTANCE.setUserPic(signInRootObject.getData().getImage());
                        AppPreferences.INSTANCE.setUserEmail(signInRootObject.getData().getEmail());
                        AppPreferences.INSTANCE.setUserFName(signInRootObject.getData().getFname());
                        AppPreferences.INSTANCE.setUserLName(signInRootObject.getData().getLname(),this);
                        AppPreferences.INSTANCE.setClientRoleId(signInRootObject.getData().getClient_role_id());
                        AppPreferences.INSTANCE.setClientRoleName(signInRootObject.getData().getClient_role_name());
                        Intent intent = new Intent(this, AnimationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK); //new added
                        startActivity(intent);
                        finish();
                    }
                } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    mPassworderrorTV.setVisibility(View.VISIBLE);
                    mPassworderrorTV.setText(R.string.text_invalidpass);
                  //  AppUtils.toast((BaseActivity) this, object.getString(AppConstant.RES_KEY_MESSAGE));

                }
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.toast(this, getString(R.string.oops));
            }
        }
        else
        {
            try {
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    AppLogger.e("TAG", "Forget Password Response: " + message);
                    AppUtils.toast(SignInPasswordActivity.this, message);
                    Intent intent = new Intent(SignInPasswordActivity.this, ResetPasswordScreen.class);
                    intent.putExtra("username", mForgotEmail);
                    startActivity(intent);
                }else
                    AppUtils.toast(SignInPasswordActivity.this, message);
            }
            catch (Exception e){
                e.printStackTrace();
                AppUtils.toast(this, getString(R.string.oops));

            }
        }
        mSpinKitView.setVisibility(View.GONE);
      //  hideProgressDialog();

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(this, getString(R.string.oops));
      //  hideProgressDialog();
        mSpinKitView.setVisibility(View.GONE);

    }

}
