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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.oditly.audit.inspection.BuildConfig;
import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.model.LanguageBeanData;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SplashActivity extends BaseActivity implements INetworkEvent {

    private static final long SPLACE_TIMEOUT = 2000;
    private static final int UPDATE_CHECK =  5*60*1000;   // 5 minute
    private Animation uptodown, downtoup, fromLeft;
    private ImageView mLogoImageIV;
    private TextView mAppNameTv;
    private TextView mSloglanTV;
    FirebaseAuth firebaseAuth;
    private String mRefreshTokenURL=NetworkURL.GET_RefreshToke_OKTA_URL;
    private TextView mScheduleTV;
    private Button mLoginBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppPreferences.INSTANCE.initAppPreferences(this);

        AppUtils.setApplicationLanguage(this,AppPreferences.INSTANCE.getSelectedLang(this));

        AppUtils.deleteCache(this);
        initView();
        initVar();
        updateFCMNotification();
        getLanguageListFromServer();
        getAppUpdateStatusFromServer();
        checkRefreshToken();
    }

    @Override
    protected void initView() {
        super.initView();

        firebaseAuth = FirebaseAuth.getInstance();

        setStatusBarColor("#0B87E4");

        findViewById(R.id.btn_signin).setOnClickListener(this);
        findViewById(R.id.btn_signin_microsoft).setOnClickListener(this);
        findViewById(R.id.tv_schedule).setOnClickListener(this);
        findViewById(R.id.tv_privacy).setOnClickListener(this);


        mLogoImageIV = (ImageView) findViewById(R.id.iv_logoimage);
        mAppNameTv = (TextView) findViewById(R.id.tv_appname);
        mSloglanTV = (TextView) findViewById(R.id.tv_slogan);
        mScheduleTV = (TextView) findViewById(R.id.tv_schedule);
        mLoginBTN = (Button) findViewById(R.id.btn_signin);



    }

    @Override
    protected void initVar() {
        super.initVar();
        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.pull_from_right);
        fromLeft = AnimationUtils.loadAnimation(this, R.anim.push_out_right);

        mAppNameTv.setAnimation(fromLeft);
        mSloglanTV.setAnimation(downtoup);
        mScheduleTV.setText(getString(R.string.text_schedule_demo_oditly));
        mLoginBTN.setText(getString(R.string.s_signin));
        mSloglanTV.setText(getString(R.string.s_digitize));

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
            case R.id.tv_privacy:
                Intent intent1=new Intent(this, PrivacyPolicyActivity.class);
                intent1.putExtra(AppConstant.FROMWHERE,AppConstant.PRIVACY_POLICY);
                startActivity(intent1);
                break;
            case R.id.btn_signin:
                sendToNewActivity();
                break;
        }
    }


    private void sendToNewActivity() {
        if (AppPreferences.INSTANCE.getAppVersionFromServer(this)>BuildConfig.VERSION_CODE)
        {
            if(!isFinishing())
                AppDialogs.openPlayStoreDialog(this);
        }
        else {
            if (AppPreferences.INSTANCE.isLogin(this)) {
                Intent intent2 = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent2);
            } else {
                Intent intent1 = new Intent(this, SignInEmailActivity.class);
                startActivity(intent1);
            }
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
        Log.e("onNetworkCallCompleted "+service, "||||||  "+response);
        if (service.equalsIgnoreCase(mRefreshTokenURL))
        {
            try {
                JSONObject jsonObject = new JSONObject(response);
                AppUtils.parseRefreshTokenRespone(jsonObject,this);
                // sendToNewActivity();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (service.equalsIgnoreCase(NetworkURL.POST_FCM_TOKEN)) {
            Log.e("TOKEN  ", "|||||| UPDATED "+AppPreferences.INSTANCE.getFCMToken());
        }
        else if (service.equalsIgnoreCase(NetworkURL.GET_LANGUAGE_LIST))
        {
            Log.e("GET_LANGUAGE_LIST  ", "|||||| "+response);
            try {
                JSONObject object = new JSONObject(response);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR))
                {
                    JSONArray jsonArray= object.optJSONArray("data");

                    List<LanguageBeanData> mLaguageList=new ArrayList<LanguageBeanData>();

                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        LanguageBeanData beanData=new LanguageBeanData();
                        beanData.setLanguage_id(jsonObject.optInt("language_id",0));
                        beanData.setLanguage_code(jsonObject.optString("language_code"));
                        beanData.setLanguage_name(jsonObject.optString("language_name"));
                        beanData.setDisplay_name(jsonObject.optString("display_name"));
                        mLaguageList.add(beanData);
                    }
                    ((OditlyApplication)getApplicationContext()).setmLanguageList(mLaguageList);  //save laguageList

                }
            }
            catch (Exception e)
            {

            }
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
                    AppPreferences.INSTANCE.setAppVersionFromServer(versionServer,this);
                }
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        AppLogger.e("", "forceUpdateError: " + errorMessage);
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

    private void checkRefreshToken()
    {

        if (AppPreferences.INSTANCE.getProviderName().equalsIgnoreCase(AppConstant.OKTA) && System.currentTimeMillis()>AppPreferences.INSTANCE.getOktaTokenExpireTime(this))
            hitGenerateOktaTokeAPI();

    }
}
