package com.oditly.audit.inspection.network;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.oditly.audit.inspection.network.apirequest.ApiRequest;
import com.oditly.audit.inspection.network.apirequest.ApiRequestJSON;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.util.AppLogger;

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

                networkEvent.onNetworkCallCompleted("",mURL, response);

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                AppLogger.e("TAG", "ERROR Response: " + error);

                networkEvent.onNetworkCallError(mURL, error.toString());


            }
        };

            ApiRequest signInRequest = new ApiRequest(request, mMethod, mURL, mContext, stringListener, errorListener);
            VolleyNetworkRequest.getInstance(mContext).addToRequestQueue(signInRequest);

    }


}
