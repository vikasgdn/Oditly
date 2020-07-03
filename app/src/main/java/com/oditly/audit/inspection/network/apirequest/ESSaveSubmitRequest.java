package com.oditly.audit.inspection.network.apirequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;

import java.util.HashMap;
import java.util.Map;

public class ESSaveSubmitRequest extends BaseStringRequest {

    //request params
    private static final String REQ_PARAM_AUDIT_ID = "audit_id";
    private static final String REQ_PARAM_AUDIT_DATE = "audit_date";
    private static final String REQ_PARAM_SAVE = "save";
    private static final String REQ_PARAM_IS_NA = "exec_is_na";
    private static final String REQ_PARAM_ANSWER = "executive_summary";
    public static final String REQ_PARAM_ACCESS_TOKEN = "access-token";
    public static final String REQ_PARAM_DEVICE_ID = "device-id";
    public static final String REQ_PARAM_DEVICE_TYPE = "device-type";
    public static final String REQ_PARAM_DEVICE_VERSION = "device-version";

    private Map<String, String> params = new HashMap<>();
    private Map<String, String> headerParams = new HashMap<>();

    public ESSaveSubmitRequest(String accessToken, String url, String auditId,
                               String auditDate, String save, String isNa, String answer,
                               Response.Listener<String> listener,
                               Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        params.put(REQ_PARAM_AUDIT_ID, auditId);
        params.put(REQ_PARAM_AUDIT_DATE, auditDate);
        params.put(REQ_PARAM_SAVE, save);
        params.put(REQ_PARAM_IS_NA, isNa);
        params.put(REQ_PARAM_ANSWER, answer);
        headerParams.put(REQ_PARAM_ACCESS_TOKEN, accessToken);
        headerParams.put(REQ_PARAM_DEVICE_ID, AppConstant.DEVICE_ID);
        headerParams.put(REQ_PARAM_DEVICE_TYPE, AppConstant.DEVICE_TYPE);
        headerParams.put(REQ_PARAM_DEVICE_VERSION, AppConstant.VERSION);

        AppLogger.e("SaveSubmitParam", ""+params);
        AppLogger.e("SaveSubmitHeader", ""+headerParams);
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
