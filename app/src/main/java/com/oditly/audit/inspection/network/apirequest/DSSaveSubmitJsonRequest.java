package com.oditly.audit.inspection.network.apirequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by namitbajaj on 10/18/17.
 */

public class DSSaveSubmitJsonRequest extends BaseJsonObjectRequest {

    //request params
    private static final String REQ_PARAM_AUDIT_ID = "audit_id";
    private static final String REQ_PARAM_AUDIT_DATE = "audit_date";
    private static final String REQ_PARAM_SAVE = "save";
    private static final String REQ_PARAM_SUMMARY = "summary";
    public static final String REQ_PARAM_ACCESS_TOKEN = "access-token";
    public static final String REQ_PARAM_DEVICE_ID = "device-id";
    public static final String REQ_PARAM_DEVICE_TYPE = "device-type";
    public static final String REQ_PARAM_DEVICE_VERSION = "device-version";

    HashMap<String, String> headers = new HashMap<String, String>();

    public DSSaveSubmitJsonRequest(String accessToken, String url, JSONObject jsonRequest,
                                   Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener) {
        super(Method.POST, url, jsonRequest, listener, errorListener);

        headers.put(REQ_PARAM_ACCESS_TOKEN, accessToken);
        headers.put(REQ_PARAM_DEVICE_ID, AppConstant.DEVICE_ID);
        headers.put(REQ_PARAM_DEVICE_TYPE, AppConstant.DEVICE_TYPE);
        headers.put(REQ_PARAM_DEVICE_VERSION, AppConstant.VERSION);
    }

    public static JSONObject createInput(String auditId, String auditDate, String save, JSONArray answer) {



        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(REQ_PARAM_AUDIT_ID, auditId);
            jsonObject.put(REQ_PARAM_AUDIT_DATE, auditDate);
            jsonObject.put(REQ_PARAM_SAVE, save);
            jsonObject.put(REQ_PARAM_SUMMARY, answer);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AppLogger.e("SearchObject: ",""+jsonObject);

        return jsonObject;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        /*HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("REQ_PARAM_AUTHORIZATION", "");*/
        return headers;
    }
}