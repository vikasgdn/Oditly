package com.oditly.audit.inspection.network.apirequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 3/5/2018.
 */

public class ResetPasswordRequest extends BaseStringRequest {

    //request params
    private static final String REQ_PARAM_USER = "user";
    private static final String REQ_PARAM_DEVICE_TOKEN = "otp";
    private static final String REQ_PARAM_PASSWORD = "password";
    public static final String REQ_PARAM_DEVICE_ID = "device-id";
    public static final String REQ_PARAM_DEVICE_TYPE = "device-type";
    public static final String REQ_PARAM_DEVICE_VERSION = "device-version";

    private Map<String, String> params = new HashMap<>();
    private Map<String, String> headerParams = new HashMap<>();

    public ResetPasswordRequest(String username,
                                String otp,
                                String newPassword,
                                Response.Listener<String> listener,
                                Response.ErrorListener errorListener) {
        super(Method.POST, NetworkURL.RESETPASSWORD, listener, errorListener);
        params.put(REQ_PARAM_USER, username);
        params.put(REQ_PARAM_DEVICE_TOKEN, otp);
        params.put(REQ_PARAM_PASSWORD, newPassword);

        headerParams.put(REQ_PARAM_DEVICE_ID, AppConstant.DEVICE_ID);
        headerParams.put(REQ_PARAM_DEVICE_TYPE, "android");
        headerParams.put(REQ_PARAM_DEVICE_VERSION, "2");

        AppLogger.e("Change Password Params", params.toString());
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
