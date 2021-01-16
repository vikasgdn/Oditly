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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppPreferences.INSTANCE.initAppPreferences(this);
        initView();
        initVar();

    }

    @Override
    protected void initView() {
        super.initView();

        setStatusBarColor("#0B87E4");

        findViewById(R.id.btn_signin).setOnClickListener(this);
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

    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {
        try {
            JSONObject object = new JSONObject(response);

            if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                int versionServer = object.getJSONObject("data").getInt("version");
                boolean status = object.getJSONObject("data").getBoolean("force_update");
                Log.e("version  ","||||||"+getVersionCode(this));
                if (versionServer>getVersionCode(this))
                    AppDialogs.openPlayStoreDialog(SplashActivity.this);
                else
                    sendToNewActivity();
            } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                sendToNewActivity();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            sendToNewActivity();
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
}
