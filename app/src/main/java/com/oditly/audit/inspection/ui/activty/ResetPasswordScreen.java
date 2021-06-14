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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
        mOTPET.setText(username);


    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case  R.id.btn_reset:

            /*    String otp,password,confPass;
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
                else {*/
                    AppUtils.hideKeyboard(context, view);
                   // resetPasswordServerData();
                resetUserPassword(username);
               // }
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
    public void resetUserPassword(String email){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mSpinKitView.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mSpinKitView.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Reset password instructions has sent to your email", Toast.LENGTH_SHORT).show();
                        }else{
                            mSpinKitView.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Email don't exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mSpinKitView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetPasswordServerData()
    {
        if (NetworkStatus.isNetworkConnected(this)) {

            // showAppProgressDialog();
            mSpinKitView.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(NetworkConstant.REQ_PARAM_MOBILE, "1");
            params.put(NetworkConstant.REQ_PARAM_USER, username);
          //  params.put(NetworkConstant.REQ_PARAM_OTP, mOTPET.getText().toString());
           // params.put(NetworkConstant.REQ_PARAM_PASSWORD, mPasswordET.getText().toString());

            NetworkService networkService = new NetworkService(NetworkURL.RESET_PASSWORD_NEW, NetworkConstant.METHOD_POST, this,this);
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
                AppDialogs.passwordResetMessageDialog(this,"Please check your email for reset password link");
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




}
