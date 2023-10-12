package com.oditly.audit.inspection.network.apirequest;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.HashMap;
import java.util.Map;

public class DeleteBSQuestionAttachmentRequest extends BaseStringRequest {

    //request params
    private static final String REQ_PARAM_AUDIT_ID = "audit_id";
    private static final String REQ_PARAM_SECTION_FILE_ID = "audit_section_file_id";
    private static final String REQ_PARAM_QUESTION_FILE_ID = "audit_question_file_id";
    private static final String REQ_PARAM_QUESTION_ID = "question_id";
    public static final String REQ_PARAM_ACCESS_TOKEN = "access-token";
    public static final String REQ_PARAM_DEVICE_ID = "device-id";
    public static final String REQ_PARAM_DEVICE_TYPE = "device-type";
    public static final String REQ_PARAM_DEVICE_VERSION = "device-version";

    private Map<String, String> params = new HashMap<>();
    private Map<String, String> headerParams = new HashMap<>();


    public DeleteBSQuestionAttachmentRequest(String accessToken, String url, String auditId,
                                             int sectionFileId, int questionFileId,String questionId,String tokenFirebase, Context context,
                                             Response.Listener<String> listener,
                                             Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        params.put(REQ_PARAM_AUDIT_ID, auditId);
        params.put(REQ_PARAM_SECTION_FILE_ID, ""+sectionFileId);
        params.put(REQ_PARAM_QUESTION_FILE_ID, ""+questionFileId);
        params.put(REQ_PARAM_QUESTION_ID, ""+questionId);
        headerParams.put(REQ_PARAM_ACCESS_TOKEN, accessToken);
        headerParams.put(REQ_PARAM_DEVICE_ID, AppConstant.DEVICE_ID);
        headerParams.put(REQ_PARAM_DEVICE_TYPE, AppConstant.DEVICE_TYPE);
        headerParams.put(REQ_PARAM_DEVICE_VERSION, AppConstant.VERSION);
        headerParams.put("Authorization","Bearer "+tokenFirebase);
        AppLogger.e("AttachmentParam", ""+params);
        AppLogger.e("AttachmentHeader", ""+headerParams);

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
