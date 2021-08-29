package com.oditly.audit.inspection.network;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.network.apirequest.ApiRequest;
import com.oditly.audit.inspection.network.apirequest.ApiRequestJSON;
import com.oditly.audit.inspection.network.apirequest.OktaTokenRefreshRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;


/**
 * Created by MAds on 3/24/2015.
 */
public class NetworkService {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String mURL;
    private int mMethod;
    private INetworkEvent networkEvent;
    private Context mContext;

    public NetworkService(String url, int method, INetworkEvent networkEvent, Context context) {
        mURL = url;
        mMethod = method;
        this.networkEvent = networkEvent;
        this.mContext=context;
    }

    public void call(Map<String,String> request) {
        SignIn(request);
    }

    private void SignIn(Map<String,String> request) {
        //  showProgressDialog();

        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(String response) {
                AppLogger.e("TAG", " SUCCESS Response: " + response);

                networkEvent.onNetworkCallCompleted("", mURL, response);

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                AppLogger.e("TAG", "ERROR Response: " + error);

                networkEvent.onNetworkCallError(mURL, error.toString());


            }
        };
        if (mURL.equalsIgnoreCase(NetworkURL.CHECK_USER))
        {
            ApiRequest signInRequest = new ApiRequest(request, mMethod, mURL, "", mContext, stringListener, errorListener);
            VolleyNetworkRequest.getInstance(mContext).addToRequestQueue(signInRequest);

        /*    Response.Listener<JSONObject> jsonListener = new Response.Listener<JSONObject>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(JSONObject response) {
                    AppLogger.e("TAG", " SUCCESS Response: " + response);
                    networkEvent.onNetworkCallCompleted("",mURL, response.toString());
                }
            };
            Response.ErrorListener errListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AppLogger.e("TAG", "ERROR Response: " + error);
                    networkEvent.onNetworkCallError(mURL, error.toString());
                }
            };
            OktaTokenRefreshRequest tokenRequest = new OktaTokenRefreshRequest(AppUtils.getTokenJson(mContext),jsonListener, errListener);
            VolleyNetworkRequest.getInstance(mContext).addToRequestQueue(tokenRequest);
*/

        } else {
            if (AppPreferences.INSTANCE.getProviderName().equalsIgnoreCase(AppConstant.OKTA)) {
                ApiRequest signInRequest = new ApiRequest(request, mMethod, mURL, AppPreferences.INSTANCE.getOktaToken(mContext), mContext, stringListener, errorListener);
                VolleyNetworkRequest.getInstance(mContext).addToRequestQueue(signInRequest);

            } else
            {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        ApiRequest signInRequest = new ApiRequest(request, mMethod, mURL, task.getResult().getToken(), mContext, stringListener, errorListener);
                                        VolleyNetworkRequest.getInstance(mContext).addToRequestQueue(signInRequest);
                                        Log.e("Task isSuccessful : ", "" + task.getResult().getToken());
                                    }
                                }
                            });
                }
            }
        }
    }

}
