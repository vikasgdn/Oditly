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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.OAuthProvider;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignInPasswordActivity extends BaseActivity implements INetworkEvent {

    private Button mSignInBtn;
    private TextInputEditText mPasswordET;
    private ImageView mEyesIV;
    private TextView mPassworderrorTV;
    private String mEmailID;
    private String mForgotEmail = "";
    private RelativeLayout mSpinKitView;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mEmailID = getIntent().getStringExtra(AppConstant.EMAIL);
        initView();
        initVar();

    }

    @Override
    protected void initView()
    {
        super.initView();
        findViewById(R.id.ti_email).setVisibility(View.GONE);
        findViewById(R.id.tv_emailerror).setVisibility(View.GONE);
        findViewById(R.id.tv_forgotpass).setOnClickListener(this);
        findViewById(R.id.iv_header_left).setOnClickListener(this);
        mPassworderrorTV = (TextView) findViewById(R.id.tv_passerror);
        mSpinKitView = (RelativeLayout) findViewById(R.id.ll_parent_progress);

        mSignInBtn = (Button) findViewById(R.id.btn_signin);
        mSignInBtn.setText(getResources().getString(R.string.s_enter));

        mPasswordET = (TextInputEditText) findViewById(R.id.et_password);
        mEyesIV = (ImageView) findViewById(R.id.iv_eyes);

        mSignInBtn.setOnClickListener(this);
        mEyesIV.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();

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
                if (TextUtils.isEmpty(mPasswordET.getText().toString()))
                    mPassworderrorTV.setVisibility(View.VISIBLE);
                else {
                    mPassworderrorTV.setVisibility(View.GONE);
                    AppUtils.hideKeyboard(this, view);
                  //  validateuserCredentialsData();
                    mSpinKitView.setVisibility(View.VISIBLE);
                    firebaseLogin(mEmailID,mPasswordET.getText().toString());
                }
                // overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
            case R.id.tv_forgotpass:
                AppDialogs.showForgotPassword(mEmailID,this);
                break;
            case R.id.iv_header_left:
                finish();
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

        }

    }


    public void setOTPServer(String userEmail) {
        if (NetworkStatus.isNetworkConnected(this)) {
            mForgotEmail = userEmail;
            // showAppProgressDialog();
            mSpinKitView.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(NetworkConstant.REQ_PARAM_USER, userEmail);
            NetworkService networkService = new NetworkService(NetworkURL.SENDOTP, NetworkConstant.METHOD_POST, this, this);
            networkService.call(params);
        } else

            AppUtils.toast(this, getString(R.string.internet_error));

    }


    public void resetPasswordServerData(String userEmail)
    {
        if (NetworkStatus.isNetworkConnected(this)) {

            resetUserPassword(userEmail);
            // showAppProgressDialog();
          /*  mSpinKitView.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(NetworkConstant.REQ_PARAM_MOBILE, "1");
            params.put(NetworkConstant.REQ_PARAM_USER, userEmail);
            NetworkService networkService = new NetworkService(NetworkURL.RESET_PASSWORD_NEW, NetworkConstant.METHOD_POST, this,this);
            networkService.call(params);*/
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
            NetworkService networkService = new NetworkService(NetworkURL.SIGNIN, NetworkConstant.METHOD_POST, this, this);
            networkService.call(params);
        } else {
            AppUtils.toast(this, getString(R.string.internet_error));
        }
    }

    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {

        Log.e("Response", "" + response);
        if (service.equalsIgnoreCase(NetworkURL.SIGNIN)) {
            try {
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR))
                {
                    SignInRootObject signInRootObject = new GsonBuilder().create().fromJson(object.toString(), SignInRootObject.class);
                    if (signInRootObject.getData() != null) {
                        AppUtils.toast(this, message);
                        AppPreferences.INSTANCE.setLogin(true, this);
                        AppPreferences.INSTANCE.setAccessToken(signInRootObject.getData().getAccess_token(), this);
                        AppPreferences.INSTANCE.setUserRole(signInRootObject.getData().getRole_id(), this);
                        // AppPreferences.INSTANCE.setUserId(signInRootObject.getData().getUser_id(), this);
                        AppPreferences.INSTANCE.setUserPic(signInRootObject.getData().getImage());
                        AppPreferences.INSTANCE.setUserEmail(signInRootObject.getData().getEmail());
                        AppPreferences.INSTANCE.setUserFName(signInRootObject.getData().getFname());
                        AppPreferences.INSTANCE.setUserLName(signInRootObject.getData().getLname(), this);
                        AppPreferences.INSTANCE.setClientRoleId(signInRootObject.getData().getClient_role_id());
                        AppPreferences.INSTANCE.setClientRoleName(signInRootObject.getData().getClient_role_name());
                        Intent intent = new Intent(this, AnimationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //new added
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
                    AppUtils.toast(SignInPasswordActivity.this, message);
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
       else if (service.equalsIgnoreCase(NetworkURL.GET_PROFILE_DATA))
        {
            try {
                JSONObject object = new JSONObject(response);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR))
                {
                    String roleId=object.getJSONObject("data").optString("role_id");
                    if (TextUtils.isEmpty(roleId))
                    {
                        AppDialogs.messageDialogWithOKButton(this,"You dont have any access previleges yet. Please contact "+object.getJSONObject("data").optString("created_by_email"));
                    }
                    else {
                        AppPreferences.INSTANCE.setUserId(object.getJSONObject("data").getInt("user_id"), this);
                        AppPreferences.INSTANCE.setUserFName(object.getJSONObject("data").optString("fname"));
                        AppPreferences.INSTANCE.setUserLName(object.getJSONObject("data").optString("lname"),this);
                        AppPreferences.INSTANCE.setUserEmail(object.getJSONObject("data").optString("email"));
                        AppPreferences.INSTANCE.setUserRole(Integer.parseInt(roleId), this);
                        AppPreferences.INSTANCE.setLogin(true, this);
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                }
            }
            catch (Exception e){e.printStackTrace();}
        }
        else {
            try {
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    AppLogger.e("TAG", "Forget Password Response: " + message);
                    AppUtils.toast(SignInPasswordActivity.this, message);
                    Intent intent = new Intent(SignInPasswordActivity.this, ResetPasswordScreen.class);
                    intent.putExtra("username", mForgotEmail);
                    startActivity(intent);
                } else
                    AppUtils.toast(SignInPasswordActivity.this, message);
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.toast(this, getString(R.string.oops));

            }
        }
        mSpinKitView.setVisibility(View.GONE);
        //  hideProgressDialog();

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("onNetworkCallError", "===>" + errorMessage);
        AppUtils.toast(this, getString(R.string.oops));
        //  hideProgressDialog();
        mSpinKitView.setVisibility(View.GONE);

    }

    private void firebaseLogin(String email,String pass) {
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(SignInPasswordActivity.this, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                mSpinKitView.setVisibility(View.GONE);
                if (!task.isSuccessful()) {
                    AppUtils.toastDisplayForLong(SignInPasswordActivity.this, "Please enter valid username and password");
                } else {

                    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

                    AppPreferences.INSTANCE.setUserPic(firebaseUser.getPhotoUrl().toString());
                 /*   AppPreferences.INSTANCE.setUserEmail(firebaseUser.getEmail());
                    String fullName=firebaseUser.getDisplayName();
                    String []Name=fullName.split(" ");
                    if (Name.length>1) {
                        AppPreferences.INSTANCE.setUserFName(Name[0]);
                        AppPreferences.INSTANCE.setUserLName(Name[1], SignInPasswordActivity.this);
                    }
                    else  if (Name.length==1)
                    {
                        AppPreferences.INSTANCE.setUserFName(Name[0]);
                    }
                    Log.e("USER ID TOKEN ",""+firebaseUser.getIdToken(false).getResult().getToken());
*/
                    Task<GetTokenResult> token=   firebaseUser.getIdToken(false);
                    String tokenAuth=  token.getResult().getToken();
                    AppPreferences.INSTANCE.setFirebaseAccessToken("Bearer "+tokenAuth,getApplicationContext());

                   /* String userId= token.getResult().getClaims().get("userId").toString();
                    String roleId= token.getResult().getClaims().get("roleId").toString();
                    if (!TextUtils.isEmpty(userId))
                        AppPreferences.INSTANCE.setUserId(Integer.parseInt(userId), SignInPasswordActivity.this);
                    if (!TextUtils.isEmpty(roleId))
                        AppPreferences.INSTANCE.setClientRoleId(Integer.parseInt(roleId));

                    //Log.e("USER TOKEN ",""+AppPreferences.INSTANCE.getFirebaseAccessToken(SignInPasswordActivity.this));
                    AppPreferences.INSTANCE.setLogin(true, SignInPasswordActivity.this);
                    startActivity(new Intent(SignInPasswordActivity.this, MainActivity.class));
          */
                  getUserProfile();
                }
            }
        });
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

    private void getUserProfile() {
        if (NetworkStatus.isNetworkConnected(this)) {
            //mSpinKitView.setVisibility(View.VISIBLE);
            NetworkService networkService = new NetworkService(NetworkURL.GET_PROFILE_DATA, NetworkConstant.METHOD_GET, this, this);
            networkService.call(new HashMap<String, String>());
        } else {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }
}
