package com.oditly.audit.inspection.ui.activty;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.firebase.FirebaseApp;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkServiceJSON;
import com.oditly.audit.inspection.network.NetworkServiceOkta;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class OctaLoginActivity extends BaseActivity implements INetworkEvent {

    //https://firebase.google.com/docs/reference/rest/auth/#section-refresh-token
    private boolean isAuthnticated = false;
    private boolean isError = false;
    private RelativeLayout mSpinKitView;

    private String mProviderId="";
    private String mRefreshTokenURL=NetworkURL.GET_RefreshToke_OKTA_URL;
    private String mRefreshToken="";

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
        findViewById(R.id.btn_signin_microsoft).setVisibility(View.GONE);
        findViewById(R.id.iv_microsoft).setVisibility(View.GONE);
        findViewById(R.id.btn_signin_octa).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_signin_octa).setOnClickListener(this);
        findViewById(R.id.iv_header_left).setOnClickListener(this);

        mSpinKitView=(RelativeLayout) findViewById(R.id.ll_parent_progress);
        mProviderId=getIntent().getStringExtra(AppConstant.PROVIDER_ID);

    }

    @Override
    protected void initVar() {
        super.initVar();

        Intent intent = getIntent();
        String action = intent.getAction();
        try{
            Uri data = intent.getData();
            //ID token
            String status = data.getQueryParameter("status");
            // REFRESH TOKEN
            mRefreshToken = data.getQueryParameter("refreshToken");
            mRefreshTokenURL=mRefreshTokenURL+""+ FirebaseApp.getInstance().getOptions().getApiKey();
            Log.d("REFRESH_TOKEN_URL", mRefreshTokenURL);


            if(status.equals("ERROR")){
                this.isError=true;
                AppUtils.toast(this,getString(R.string.oops));
            }else if(status.equals("SUCCESS")){
                this.isAuthnticated = true;
                Toast.makeText(getApplicationContext(),"Login success" , Toast.LENGTH_LONG).show();
                //  Log.d("ID_TOKEN", authKey);
                Log.e("REFRESH_TOKEN","==> "+ mRefreshToken);
                hitGenerateOktaTokeAPI();

            }

        }catch (Exception e){
            Log.e("INTENT_FILTER",e.getMessage());
        }


    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.iv_header_left:
                finish();
                break;
            case R.id.btn_signin_octa:
                String url = NetworkURL.OKTA_LIVE_URL + mProviderId;

                Log.e("OCTA URL ", "===> " + url);
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                // set toolbar color and/or setting custom actions before invoking build()
                // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
                builder.setToolbarColor(Color.parseColor("#0B87E4"));
                CustomTabsIntent customTabsIntent = builder.build();
                // and launch the desired Url with CustomTabsIntent.launchUrl()
                customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
                break;
        }
    }

    private void hitGenerateOktaTokeAPI()
    {
        if (NetworkStatus.isNetworkConnected(this))
        {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("grant_type","refresh_token");
                jsonObject.put("refresh_token", mRefreshToken);
                NetworkServiceOkta networkService = new NetworkServiceOkta(jsonObject,mRefreshTokenURL, NetworkConstant.METHOD_POST, this, this);
                networkService.call(null);
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

    @Override
    public void onNetworkCallInitiated(String service) {
        mSpinKitView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {
        mSpinKitView.setVisibility(View.GONE);
        Log.e("onNetworkCallComplete " + service, "===>" + response);
        if (service.equalsIgnoreCase(mRefreshTokenURL)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                AppUtils.parseRefreshTokenRespone(jsonObject,this);
                getUserProfile();

            } catch (Exception e) {
                AppUtils.toast(this, getString(R.string.oops));
                e.printStackTrace();
            }
        }
        else
        {
            try {

                JSONObject object = new JSONObject(response);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR))
                {
                    String roleId=object.getJSONObject("data").optString("role_id");
                    if (TextUtils.isEmpty(roleId))
                        AppDialogs.messageDialogWithOKButton(this,"You dont have any access previleges yet. Please contact "+object.getJSONObject("data").optString("created_by_email"));
                    else {
                        AppPreferences.INSTANCE.setUserId(object.getJSONObject("data").getInt("user_id"), this);
                        AppPreferences.INSTANCE.setUserRole(Integer.parseInt(roleId), this);
                        AppPreferences.INSTANCE.setUserFName(object.getJSONObject("data").optString("fname"));
                        AppPreferences.INSTANCE.setUserLName(object.getJSONObject("data").optString("lname"),this);
                        AppPreferences.INSTANCE.setUserEmail(object.getJSONObject("data").optString("email"));
                        AppPreferences.INSTANCE.setLogin(true, this);

                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                }
            }
            catch (Exception e){e.printStackTrace();}
        }

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(this, getString(R.string.oops));
        mSpinKitView.setVisibility(View.GONE);

    }

    private void getUserProfile() {
        if (NetworkStatus.isNetworkConnected(this)) {
            mSpinKitView.setVisibility(View.VISIBLE);
            NetworkService networkService = new NetworkService(NetworkURL.GET_PROFILE_DATA, NetworkConstant.METHOD_GET, this, this);
            networkService.call(new HashMap<String, String>());
        } else {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }
}