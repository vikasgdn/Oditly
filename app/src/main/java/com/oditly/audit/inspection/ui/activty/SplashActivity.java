package com.oditly.audit.inspection.ui.activty;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkServiceJSON;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;

public class SplashActivity extends BaseActivity implements INetworkEvent {

    private static final long SPLACE_TIMEOUT = 2000;
    private static final int UPDATE_CHECK = 1000 * 60 * 1200;   // 20 hour
    private Animation uptodown, downtoup, fromLeft;
    private ImageView mLogoImageIV;
    private TextView mAppNameTv;
    private TextView mSloglanTV;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppPreferences.INSTANCE.initAppPreferences(this);

     //   Log.e("TOKEN SPLASH ",""+FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).getResult().getToken());
        //updateFCMNotification();
        initView();
        initVar();
        updateFCMNotification();
       // getUserProfile();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void initView() {
        super.initView();

        firebaseAuth = FirebaseAuth.getInstance();

        setStatusBarColor("#0B87E4");

        findViewById(R.id.btn_signin).setOnClickListener(this);
        findViewById(R.id.btn_signin_microsoft).setOnClickListener(this);
        findViewById(R.id.tv_schedule).setOnClickListener(this);


        mLogoImageIV = (ImageView) findViewById(R.id.iv_logoimage);
        mAppNameTv = (TextView) findViewById(R.id.tv_appname);
        mSloglanTV = (TextView) findViewById(R.id.tv_slogan);

    }

    @Override
    protected void initVar() {
        super.initVar();
        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.pull_from_right);
        fromLeft = AnimationUtils.loadAnimation(this, R.anim.push_out_right);

        mAppNameTv.setAnimation(fromLeft);
        mSloglanTV.setAnimation(downtoup);

        if (System.currentTimeMillis() - AppPreferences.INSTANCE.getLastHitTime(this) > UPDATE_CHECK) {
            AppPreferences.INSTANCE.setLastHitTime(this, System.currentTimeMillis());
            getAppUpdateStatusFromServer();
        } else
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendToNewActivity();
                }
            }, SPLACE_TIMEOUT);
        }

    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.tv_schedule:
                Intent intent=new Intent(this,ScheduleDemoActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_signin_microsoft:
                if (firebaseAuth.getCurrentUser()!=null)
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                else
                    AuthenticateWithMicrosoftOAuth();
                break;
            case R.id.btn_signin:
                if (AppPreferences.INSTANCE.isLogin(this)) {
                    Intent intent2 = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent2);
                }else {
                    Intent intent1 = new Intent(this, SignInEmailActivity.class);
                    startActivity(intent1);
                }
                break;

        }


    }

    private void sendToNewActivity() {
        if (AppPreferences.INSTANCE.isLogin(this)) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }/* else {
            Intent intent = new Intent(this, SignInEmailActivity.class);
            startActivity(intent);
        }*/
    }

    private void getAppUpdateStatusFromServer() {
        if (NetworkStatus.isNetworkConnected(this)) {
            //mSpinKitView.setVisibility(View.VISIBLE);
            NetworkService networkService = new NetworkService(NetworkURL.APP_VERSION, NetworkConstant.METHOD_GET, this, this);
            networkService.call(new HashMap<String, String>());
        } else {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
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

    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {
        Log.e("onNetworkCallCompleted "+service, "||||||  "+response);

        if (service.equalsIgnoreCase(NetworkURL.POST_FCM_TOKEN)) {
            Log.e("TOKEN  ", "|||||| UPDATED "+AppPreferences.INSTANCE.getFCMToken());
        } else if(service.equalsIgnoreCase(NetworkURL.GET_PROFILE_DATA))
        {
            //  {"error":false,"data":{"user_id":3,"uid":"eZqETckFW3Tw53eeIjM3MUWrrOF3","tenant_uid":null,"role_id":200,"client_id":3,"fname":"Test","lname":"Client","email":"testclient@oditly.com","image":"https:\/\/api.account.oditly.com\/assets\/profile\/dummy.jpg","phone":1234567890,"dob":null,"gender":1,"address":"test address","city_id":707,"state_id":10,"country_id":101,"zone_id":194,"zipcode":123456,"user_status":1,"created_on":"2020-05-03 22:09:15","gender_text":"male","custom_role_id":null,"custom_role_name":null,"country_name":"India","zone_name":"Asia\/Kolkata","state_name":"Delhi","city_name":"New Delhi","role_name":"Client SuperAdmin","role_resource":"client","client_status":1,"industry_id":3,"tenant_id":null,"company_contact_email":"info@oditly.com","company_support_email":"support@oditly.com","company_name":"Test Client","company_logo":"https:\/\/api.account.oditly.com\/assets\/company_logo\/20200510_141240_52359315172925.png","ia_status":1,"training_status":1,"star_employee":0,"faq_report_name":"FAQ","can_gm_approve_action_plan":1},
            try {

                JSONObject object = new JSONObject(response);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR))
                {
                   AppPreferences.INSTANCE.setUserId(object.getJSONObject("data").getInt("user_id"),this);
                    AppPreferences.INSTANCE.setUserRole(object.getJSONObject("data").getInt("role_id"),this);
                }
            }
            catch (Exception e){e.printStackTrace();}
        }
        else {
            try {
                JSONObject object = new JSONObject(response);

                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    int versionServer = object.getJSONObject("data").getInt("version");
                    boolean status = object.getJSONObject("data").getBoolean("force_update");
                    Log.e("version  ", "||||||" + getVersionCode(this));
                    if (versionServer > getVersionCode(this))
                        AppDialogs.openPlayStoreDialog(SplashActivity.this);
                    else
                        sendToNewActivity();
                } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    sendToNewActivity();
                }
            } catch(JSONException e){
                e.printStackTrace();
                sendToNewActivity();
            }
        }
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        AppLogger.e("", "forceUpdateError: " + errorMessage);
        AppUtils.toast(SplashActivity.this, "Server temporary unavailable, Please try again");

    }

    public  int getVersionCode(Context context) {
        try
        {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            if(pInfo!=null)
                return pInfo.versionCode;

        } catch(PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return  0;
    }

    private void updateFCMNotification() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            //  Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        AppPreferences.INSTANCE.setFCMToken(token);
                        postTokenToServer();
                        // Log and toast
                        Log.d("TAG", token);
                        // Toast.makeText(SplashActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void postTokenToServer()
    {
        if (NetworkStatus.isNetworkConnected(this))
        {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(AppConstant.TOKEN,AppPreferences.INSTANCE.getFCMToken());
                NetworkServiceJSON networkService = new NetworkServiceJSON(NetworkURL.POST_FCM_TOKEN, NetworkConstant.METHOD_POST, this, this);
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
    public void getUserProfileData()
    {
        if (NetworkStatus.isNetworkConnected(this))
        {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(AppConstant.TOKEN,AppPreferences.INSTANCE.getFCMToken());
                NetworkServiceJSON networkService = new NetworkServiceJSON(NetworkURL.POST_FCM_TOKEN, NetworkConstant.METHOD_POST, this, this);
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
                                    AppUtils.toastDisplayForLong(SplashActivity.this, ""+e.getMessage());
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

                                        AppUtils.toastDisplayForLong(SplashActivity.this,"Welcome " + userEmail);
                                    } else {
                                        AppUtils.toastDisplayForLong(SplashActivity.this,"The sign-in user's account does not belong to one of the tenants that this Web App accepts users from.");
                                    }

                                    AppPreferences.INSTANCE.setLogin(true,SplashActivity.this);
                                    AppPreferences.INSTANCE.setUserEmail(userEmail);
                                    String []Name=username.split(" ");
                                    if (Name.length>1) {
                                        AppPreferences.INSTANCE.setUserFName(Name[0]);
                                        AppPreferences.INSTANCE.setUserLName(Name[1], SplashActivity.this);
                                    }
                                    else  if (Name.length==1)
                                    {
                                        AppPreferences.INSTANCE.setUserFName(Name[0]);
                                    }

                                    GetTokenResult getTokenResult= authResult.getUser().getIdToken(false).getResult();
                                    String tokenAuth=  getTokenResult.getToken();
                                    AppPreferences.INSTANCE.setFirebaseAccessToken("Bearer "+tokenAuth,getApplicationContext());

                                    String userId= getTokenResult.getClaims().get("userId")==null?"":getTokenResult.getClaims().get("userId").toString();
                                    String roleId= getTokenResult.getClaims().get("roleId")==null?"":getTokenResult.getClaims().get("roleId").toString();

                                    getUserProfile();

                                    Log.e("USER ID Microsoft ===> ",""+userId);


                                    if (!TextUtils.isEmpty(userId))
                                        AppPreferences.INSTANCE.setUserId(Integer.parseInt(userId), SplashActivity.this);
                                    if (!TextUtils.isEmpty(roleId))
                                        AppPreferences.INSTANCE.setClientRoleId(Integer.parseInt(roleId));

                                   // Log.e("USER TOKEN ",""+AppPreferences.INSTANCE.getFirebaseAccessToken(SplashActivity.this));
                                    AppPreferences.INSTANCE.setLogin(true, SplashActivity.this);
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    AppUtils.toastDisplayForLong(SplashActivity.this, ""+e.getMessage());
                                }
                            });
        }
    }

}
