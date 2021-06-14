package com.oditly.audit.inspection.ui.activty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.ui.activty.BaseActivity;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.ui.activty.ResetPasswordScreen;
import com.oditly.audit.inspection.ui.activty.SignInPasswordActivity;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignInEmailActivity extends BaseActivity implements INetworkEvent {

    private Button mSignInBtn;
    private TextInputEditText mEmailET;
    private TextView mEmailErrorTV;
    private String mForgotEmail="";
    private RelativeLayout mSpinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        // setStatusBarColor("#FFFFFF");

        initView();
        initVar();

    }
    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.tv_scheduledemo).setOnClickListener(this);
        findViewById(R.id.rl_password).setVisibility(View.GONE);
        findViewById(R.id.tv_passerror).setVisibility(View.GONE);
        findViewById(R.id.tv_forgotpass).setOnClickListener(this);
        findViewById(R.id.iv_header_left).setOnClickListener(this);
        mSpinKitView=(RelativeLayout) findViewById(R.id.ll_parent_progress);

        mEmailErrorTV=(TextView)findViewById(R.id.tv_emailerror);
        mSignInBtn=(Button)findViewById(R.id.btn_signin);

        mEmailET=(TextInputEditText)findViewById(R.id.et_email);

        mSignInBtn.setOnClickListener(this);
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
                //   if (TextUtils.isEmpty(mEmailET.getText().toString()) || !AppUtils.isValidEmail(mEmailET.getText().toString()))

                if (TextUtils.isEmpty(mEmailET.getText().toString()))
                    mEmailErrorTV.setVisibility(View.VISIBLE);
                else {
                    mEmailErrorTV.setVisibility(View.GONE);
                    AppUtils.hideKeyboard(this, view);
                    validateEmailServerData();
                   /* Intent intent = new Intent(this, SignInPasswordActivity.class);
                    intent.putExtra(AppConstant.EMAIL, mEmailET.getText().toString());
                    startActivity(intent);
             */
                }
                break;
            case R.id.tv_forgotpass:
                AppDialogs.showForgotPassword(mEmailET.getText().toString(),this);
                break;
            case R.id.iv_header_left:
                finish();
                break;
            case R.id.tv_scheduledemo:
                Intent intent=new Intent(this,ScheduleDemoActivity.class);
                startActivity(intent);
                break;

        }



    }


    private void validateEmailServerData()
    {
        if (NetworkStatus.isNetworkConnected(this)) {

            // showAppProgressDialog();
            mSpinKitView.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(NetworkConstant.REQ_PARAM_EMAIL, mEmailET.getText().toString());
          //  params.put(NetworkConstant.REQ_PARAM_USER, mEmailET.getText().toString());
            params.put(NetworkConstant.REQ_PARAM_MOBILE, "1");
            NetworkService networkService = new NetworkService(NetworkURL.CHECK_USER, NetworkConstant.METHOD_POST, this,this);
            networkService.call(params);
        } else

            AppUtils.toast(this, getString(R.string.internet_error));

    }

  /*  public void setOTPServer(String userEmail)
    {
        if (NetworkStatus.isNetworkConnected(this)) {
            mSpinKitView.setVisibility(View.VISIBLE);
            mForgotEmail=userEmail;
            //  showAppProgressDialog();
            Map<String, String> params = new HashMap<>();
            params.put(NetworkConstant.REQ_PARAM_USER, userEmail);
            NetworkService networkService = new NetworkService(NetworkURL.SENDOTP, NetworkConstant.METHOD_POST, this,this);
            networkService.call(params);
        } else
            AppUtils.toast(this, getString(R.string.internet_error));

    }*/


    public void resetPasswordServerData(String userEmail)
    {
        if (NetworkStatus.isNetworkConnected(this)) {

            resetUserPassword(userEmail);
            // showAppProgressDialog();
         /*   mSpinKitView.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(NetworkConstant.REQ_PARAM_MOBILE, "1");
            params.put(NetworkConstant.REQ_PARAM_USER, userEmail);
            NetworkService networkService = new NetworkService(NetworkURL.RESET_PASSWORD_NEW, NetworkConstant.METHOD_POST, this,this);
            networkService.call(params);*/
        } else

            AppUtils.toast(this, getString(R.string.internet_error));

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

    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response)
    {
        if(service.equalsIgnoreCase(NetworkURL.CHECK_USER)) {
            try {
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    Intent intent = new Intent(this, SignInPasswordActivity.class);
                    intent.putExtra(AppConstant.EMAIL, mEmailET.getText().toString());
                    startActivity(intent);
                   // finish();

                } else {
                    mEmailErrorTV.setVisibility(View.VISIBLE);
                    mEmailErrorTV.setText("" + message);
                }
                   // AppUtils.toast(this, message);
            } catch (Exception e) {
                AppUtils.toast(this, getString(R.string.oops));
            }
        }
        else if(service.equalsIgnoreCase(NetworkURL.RESET_PASSWORD_NEW))
        {

            try {
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    AppDialogs.passwordResetMessageDialog(this,"Please check your email for reset password link");
                    //  AppUtils.toast(ResetPasswordScreen.this, message);
                    // finish();
                }else
                    AppUtils.toast(SignInEmailActivity.this, message);
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
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
                    AppUtils.toast(SignInEmailActivity.this, message);
                    Intent intent = new Intent(SignInEmailActivity.this, ResetPasswordScreen.class);
                    intent.putExtra("username", mForgotEmail);
                    startActivity(intent);
                  //  finish();
                }else
                    AppUtils.toast(SignInEmailActivity.this, message);
            }
            catch (Exception e){
                e.printStackTrace();
                AppUtils.toast(this, getString(R.string.oops));

            }
        }
        // hideProgressDialog();
        mSpinKitView.setVisibility(View.GONE);

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(this, getString(R.string.oops));
        // hideProgressDialog();
        mSpinKitView.setVisibility(View.GONE);

    }

}
