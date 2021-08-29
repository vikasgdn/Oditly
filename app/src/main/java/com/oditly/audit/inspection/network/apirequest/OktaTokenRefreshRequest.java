package com.oditly.audit.inspection.network.apirequest;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.firebase.FirebaseApp;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 3/5/2018.
 */

public class OktaTokenRefreshRequest extends BaseJsonObjectRequest {

    private static String RefreshTokenURL=NetworkURL.GET_RefreshToke_OKTA_URL+""+ FirebaseApp.getInstance().getOptions().getApiKey();

    private Map<String, String> params = new HashMap<>();
    private Map<String, String> headerParams = new HashMap<>();

    public OktaTokenRefreshRequest(JSONObject param,Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, RefreshTokenURL, param, listener, errorListener);
        AppLogger.e("API PARAMS URL ",RefreshTokenURL+" || "+ params.toString());
        AppLogger.e("API HEADERS", headerParams.toString());
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headerParams;
    }
}
