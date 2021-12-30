package com.oditly.audit.inspection.ui.activty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.OAuthProvider;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class MicroSoftLoginActivity extends BaseActivity implements INetworkEvent {

    private RelativeLayout mSpinKitView;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_octa_microsoftlogin);
        initView();
        initVar();

    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.btn_signin_octa).setVisibility(View.GONE);
        findViewById(R.id.iv_octa).setVisibility(View.GONE);
        findViewById(R.id.iv_microsoft).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_signin_microsoft).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_signin_microsoft).setOnClickListener(this);

        mSpinKitView=(RelativeLayout) findViewById(R.id.ll_parent_progress);

        String providerId=getIntent().getStringExtra(AppConstant.PROVIDER_ID);


    }

    @Override
    protected void initVar() {
        super.initVar();
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_signin_microsoft:
                if (firebaseAuth.getCurrentUser() != null)
                    startActivity(new Intent(MicroSoftLoginActivity.this, MainActivity.class));
                else
                    AuthenticateWithMicrosoftOAuth();
                break;
        }
    }

    @Override
    public void onNetworkCallInitiated(String service) {
        mSpinKitView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {
        mSpinKitView.setVisibility(View.GONE);

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
                    AppPreferences.INSTANCE.setUserRole(Integer.parseInt(roleId), this);
                    AppPreferences.INSTANCE.setUserEmail(object.getJSONObject("data").optString("email"));
                    AppPreferences.INSTANCE.setLogin(true, this);
                    startActivity(new Intent(this, MainActivity.class));
                }
            }
        }
        catch (Exception e){e.printStackTrace();}


    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(this, getString(R.string.oops));
        mSpinKitView.setVisibility(View.GONE);

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


    private void AuthenticateWithMicrosoftOAuth() {

        OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");
        // Force re-consent.
        // provider.addCustomParameter("prompt", "consent");
        // Target specific email with login hint.
        //provider.addCustomParameter("login_hint", "sumiran@mismosystems.com");
        provider.addCustomParameter("tenant", "common");

        /*List<String> scopes =
                new ArrayList<String>() {
                    {
                        add("mail.read");
                        add("calendars.read");
                    }
                };

        provider.setScopes(scopes);*/



        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
                                    // The OAuth ID token can also be retrieved:
                                    // authResult.getCredential().getIdToken().
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    AppUtils.toastDisplayForLong(MicroSoftLoginActivity.this, ""+e.getMessage());
                                }
                            });
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
            firebaseAuth
                    .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    Object[] userProfile = authResult.getAdditionalUserInfo().getProfile().values().toArray();
                                    String userEmail = (userProfile[5].toString().trim().toLowerCase());
                                    String username = (userProfile[1].toString().trim());

                                    if (userEmail.contains("oditly") || userEmail.contains("mismo")) {

                                        AppUtils.toastDisplayForLong(MicroSoftLoginActivity.this,"Welcome " + userEmail);
                                    } else {
                                        AppUtils.toastDisplayForLong(MicroSoftLoginActivity.this,"The sign-in user's account does not belong to one of the tenants that this Web App accepts users from.");
                                    }

                                   /* AppPreferences.INSTANCE.setLogin(true,MicroSoftLoginActivity.this);
                                    AppPreferences.INSTANCE.setUserEmail(userEmail);
                                    String []Name=username.split(" ");
                                    if (Name.length>1) {
                                        AppPreferences.INSTANCE.setUserFName(Name[0]);
                                        AppPreferences.INSTANCE.setUserLName(Name[1], MicroSoftLoginActivity.this);
                                    }
                                    else  if (Name.length==1)
                                    {
                                        AppPreferences.INSTANCE.setUserFName(Name[0]);
                                    }*/

                                    GetTokenResult getTokenResult= authResult.getUser().getIdToken(false).getResult();
                                    String tokenAuth=  getTokenResult.getToken();
                                    AppPreferences.INSTANCE.setFirebaseAccessToken("Bearer "+tokenAuth,getApplicationContext());

                                    // String userId= getTokenResult.getClaims().get("userId")==null?"":getTokenResult.getClaims().get("userId").toString();
                                    // String roleId= getTokenResult.getClaims().get("roleId")==null?"":getTokenResult.getClaims().get("roleId").toString();



                                    //   Log.e("USER ID Microsoft ===> ",""+userId);

/*

                                    if (!TextUtils.isEmpty(userId))
                                        AppPreferences.INSTANCE.setUserId(Integer.parseInt(userId), MicroSoftLoginActivity.this);
                                    if (!TextUtils.isEmpty(roleId))
                                        AppPreferences.INSTANCE.setClientRoleId(Integer.parseInt(roleId));
*/

                                    getUserProfile();
                                    // Log.e("USER TOKEN ",""+AppPreferences.INSTANCE.getFirebaseAccessToken(SplashActivity.this));

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    AppUtils.toastDisplayForLong(MicroSoftLoginActivity.this, ""+e.getMessage());
                                }
                            });
        }
    }

}