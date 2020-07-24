package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.ui.activty.BaseActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;


public class ResetPasswordScreen extends BaseActivity implements INetworkEvent {

    @BindView(R.id.et_enteryourotp)
    EditText mOTPET;
    @BindView(R.id.et_password)
    EditText mPasswordET;
    @BindView(R.id.et_password_conf)
    EditText mConfirmPasswordET;
    @BindView(R.id.username_text)
    TextView usernametxt;
   /* @BindView(R.id.tv_header_title)
    TextView mTitleTV;*/

    @BindView(R.id.tv_otperror)
    TextView mOTPErrorTV;
    @BindView(R.id.tv_passerror)
    TextView mPassErrorTV;
    @BindView(R.id.tv_passerror_conf)
    TextView mConfPassErrorTV;

    @BindView(R.id.iv_eyes)
    ImageView mEyesIV;
    @BindView(R.id.iv_eyes_conf)
    ImageView mEyesConfIV;

    @BindView(R.id.ll_parent_progress)
    RelativeLayout mSpinKitView;

    private String username = "";
    private Context context;
    private static final String TAG = ResetPasswordScreen.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_screen);
     //   setStatusBarColor("#0B87E4");
        context = this;
        ButterKnife.bind(ResetPasswordScreen.this);
        initView();
        initVar();
    }

    @Override
    protected void initView() {
        super.initView();
        mOTPET = findViewById(R.id.et_enteryourotp);
        mPasswordET = findViewById(R.id.et_password);
        mConfirmPasswordET = findViewById(R.id.et_password_conf);
        usernametxt = findViewById(R.id.username_text);
        mSpinKitView=findViewById(R.id.ll_parent_progress);
      //  mTitleTV=findViewById(R.id.tv_header_title);

        mOTPErrorTV = findViewById(R.id.tv_otperror);
        mPassErrorTV = findViewById(R.id.tv_passerror);
        mConfPassErrorTV = findViewById(R.id.tv_passerror_conf);

        mEyesIV=findViewById(R.id.iv_eyes);
        mEyesConfIV=findViewById(R.id.iv_eyes_conf);

        findViewById(R.id.iv_header_left).setOnClickListener(this);
        findViewById(R.id.iv_header_right).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_header_right).setOnClickListener(this);
        findViewById(R.id.btn_reset).setOnClickListener(this);
        mEyesConfIV.setOnClickListener(this);
        mEyesIV.setOnClickListener(this);
     //   mTitleTV=findViewById(R.id.tv_header_title);


    }

    @Override
    protected void initVar() {
        super.initVar();
       // mTitleTV.setText(R.string.text_resetpassword);
        username = getIntent().getStringExtra("username");
        usernametxt.setText(username);


    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case  R.id.btn_reset:

                String otp,password,confPass;
                otp=mOTPET.getText().toString();
                password=mPasswordET.getText().toString();
                confPass=mConfirmPasswordET.getText().toString();

                if(TextUtils.isEmpty(otp)) {
                    visibilityGone();
                    mOTPErrorTV.setVisibility(View.VISIBLE);
                }
                else if(TextUtils.isEmpty(password) || !AppUtils.isValidPassword (password)) {
                    visibilityGone();
                    mPassErrorTV.setVisibility(View.VISIBLE);
                }
                else if(TextUtils.isEmpty(confPass) || !AppUtils.isValidPassword (confPass)) {
                    visibilityGone();
                    mConfPassErrorTV.setVisibility(View.VISIBLE);
                }
                else if(!password.equalsIgnoreCase(confPass)) {
                    visibilityGone();
                    mConfPassErrorTV.setVisibility(View.VISIBLE);
                    mConfPassErrorTV.setText(R.string.text_password_not_same);
                }
                else {
                    AppUtils.hideKeyboard(context, view);
                    resetPasswordServerData();
                }
                break;
            case R.id.iv_header_left:
                finish();
                break;
            case R.id.iv_header_right:
                AppDialogs.passwordAlgoDialog(ResetPasswordScreen.this);
                break;

            case R.id.iv_eyes:
                if (mEyesIV.isSelected()) {
                    mEyesIV.setSelected(false);
                    mPasswordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    mEyesIV.setSelected(true);
                    mPasswordET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                break;
            case R.id.iv_eyes_conf:
                if (mEyesConfIV.isSelected()) {
                    mEyesConfIV.setSelected(false);
                    mConfirmPasswordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    mEyesConfIV.setSelected(true);
                    mConfirmPasswordET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                break;
        }
    }

    private void visibilityGone()
    {
        mConfPassErrorTV.setVisibility(View.GONE);
        mPassErrorTV.setVisibility(View.GONE);
        mOTPErrorTV.setVisibility(View.GONE);

    }
    private void resetPasswordServerData()
    {
        if (NetworkStatus.isNetworkConnected(this)) {

            // showAppProgressDialog();
            mSpinKitView.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(NetworkConstant.REQ_PARAM_MOBILE, "1");
            params.put(NetworkConstant.REQ_PARAM_USER, username);
            params.put(NetworkConstant.REQ_PARAM_OTP, mOTPET.getText().toString());
            params.put(NetworkConstant.REQ_PARAM_PASSWORD, mPasswordET.getText().toString());

            NetworkService networkService = new NetworkService(NetworkURL.RESETPASSWORD, NetworkConstant.METHOD_POST, this,this);
            networkService.call(params);
        } else

            AppUtils.toast(this, getString(R.string.internet_error));

    }

    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {
        AppLogger.e(TAG, "Change Password Response: " + response);

        try {
            JSONObject object = new JSONObject(response);
            String message = object.getString(AppConstant.RES_KEY_MESSAGE);
            if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                passwordResetMessageDialog(this,"Password changed successfully, please login again to continue");
                //  AppUtils.toast(ResetPasswordScreen.this, message);
               // finish();
            }else
                AppUtils.toast(ResetPasswordScreen.this, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        mSpinKitView.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        AppLogger.e(TAG, "ChangePasswordError: " + errorMessage);
        AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");
        mSpinKitView.setVisibility(View.GONE);
    }

    private  void passwordResetMessageDialog(final Activity activity,String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_password_rule);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView textView=dialog.findViewById(R.id.tv_dialog_message);
        dialog.findViewById(R.id.tv_pass_rule).setVisibility(View.GONE);
        textView.setText(""+message);

        try {


            dialog.findViewById(R.id.tv_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                    dialog.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }


}
