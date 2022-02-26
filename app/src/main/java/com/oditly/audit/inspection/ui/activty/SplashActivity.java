package com.oditly.audit.inspection.ui.activty;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
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
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SplashActivity extends BaseActivity implements INetworkEvent {

    private static final long SPLACE_TIMEOUT = 2000;
    private static final int UPDATE_CHECK = 1000 * 60 * 1200;   // 20 hour
    private Animation uptodown, downtoup, fromLeft;
    private ImageView mLogoImageIV;
    private TextView mAppNameTv;
    private TextView mSloglanTV;
    FirebaseAuth firebaseAuth;
    private String mRefreshTokenURL=NetworkURL.GET_RefreshToke_OKTA_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppPreferences.INSTANCE.initAppPreferences(this);

        Log.e("CURRENT TIME MILLIS","===> "+System.currentTimeMillis());
        Log.e("OKTA EXPIRE MILLIS","===> "+AppPreferences.INSTANCE.getOktaTokenExpireTime(this));
        initView();
        initVar();
        updateFCMNotification();
        getLanguageListFromServer();
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkRefreshTokenAndForceUpdate();
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
        }
    }

    private void getAppUpdateStatusFromServer() {
        if (NetworkStatus.isNetworkConnected(this)) {
            NetworkService networkService = new NetworkService(NetworkURL.APP_VERSION, NetworkConstant.METHOD_GET, this, this);
            networkService.call(new HashMap<String, String>());
        } else {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }

    private void getLanguageListFromServer() {
        if (NetworkStatus.isNetworkConnected(this)) {
            NetworkService networkService = new NetworkService(NetworkURL.GET_LANGUAGE_LIST, NetworkConstant.METHOD_GET, this, this);
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
      //  Log.e("onNetworkCallCompleted "+service, "||||||  "+response);
        if (service.equalsIgnoreCase(mRefreshTokenURL))
        {
            try {
                JSONObject jsonObject = new JSONObject(response);
                AppUtils.parseRefreshTokenRespone(jsonObject,this);
               // sendToNewActivity();
            } catch (Exception e) {
                AppUtils.toast(this, getString(R.string.oops));
                e.printStackTrace();
            }
        }
        else if (service.equalsIgnoreCase(NetworkURL.POST_FCM_TOKEN)) {
            Log.e("TOKEN  ", "|||||| UPDATED "+AppPreferences.INSTANCE.getFCMToken());
        }
        else if (service.equalsIgnoreCase(NetworkURL.GET_LANGUAGE_LIST)) {
            Log.e("LANGUAGE LIST  ", "|||||| "+response);
        }
        else if(service.equalsIgnoreCase(NetworkURL.GET_PROFILE_DATA))
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
                            return;
                        }
                        String token = task.getResult();
                        AppPreferences.INSTANCE.setFCMToken(token);
                        postTokenToServer();
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

    private void hitGenerateOktaTokeAPI()
    {
        if (NetworkStatus.isNetworkConnected(this))
        {
            mRefreshTokenURL=mRefreshTokenURL+""+ FirebaseApp.getInstance().getOptions().getApiKey();
            NetworkServiceJSON networkService = new NetworkServiceJSON(mRefreshTokenURL, NetworkConstant.METHOD_POST, this, this);
            networkService.call(AppUtils.getTokenJson(this));
        } else
        {
            AppUtils.toast(this, getString(R.string.internet_error));
        }
    }

    private void checkRefreshTokenAndForceUpdate()
    {
        if (System.currentTimeMillis() - AppPreferences.INSTANCE.getLastHitTime(this) > UPDATE_CHECK) {
            AppPreferences.INSTANCE.setLastHitTime(this, System.currentTimeMillis());
            getAppUpdateStatusFromServer();
        } else
        {
            if (AppPreferences.INSTANCE.getProviderName().equalsIgnoreCase(AppConstant.OKTA) && System.currentTimeMillis()>AppPreferences.INSTANCE.getOktaTokenExpireTime(this))
                hitGenerateOktaTokeAPI();
          /*  else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendToNewActivity();
                    }
                }, SPLACE_TIMEOUT);
            }*/
        }

    }


    /*App notification issue --
    App signature issue back issue --
 1. Remove Action Plan Attachments while we view it

2. No pop up warning when exiting sub section for mandatory logic - only working for comment characters logic, not working for media or action plan or mandatory questions

3. Mandatory mark disappears when returning to question from main menu, only for Action Plan

4. Video files not being counted against mandatory media

5. On completing and AP it takes me back to the AP page but does not auto refresh so the old AP still shows and then gives an error when trying to submit
*/
}
