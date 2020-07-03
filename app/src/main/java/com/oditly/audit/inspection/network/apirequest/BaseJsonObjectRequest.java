package com.oditly.audit.inspection.network.apirequest;

import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by anil on 1/9/17.
 */

public class BaseJsonObjectRequest extends JsonObjectRequest {

    private static final int TIMEOUT_MS = 45000;
    private static final int MAX_RETRY = 0;


    public BaseJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
        RetryPolicy myRetryPolycy = new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return super.setRetryPolicy(myRetryPolycy);
    }

    protected String toParam(String val) {
        return TextUtils.isEmpty(val) ? "" : val;
    }

    protected String toParam(int val) {
        return "" + val;
    }

}
