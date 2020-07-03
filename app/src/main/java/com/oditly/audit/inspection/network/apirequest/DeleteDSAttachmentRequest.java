package com.oditly.audit.inspection.network.apirequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;

import java.util.HashMap;
import java.util.Map;

public class DeleteDSAttachmentRequest extends BaseStringRequest {

    //request params
    private static final String REQ_PARAM_AUDIT_ID = "audit_id";
    private static final String REQ_PARAM_SECTION_GROUP_ID = "section_group_id";
    private static final String REQ_PARAM_SECTION_ID = "section_id";
    private static final String REQ_PARAM_IS_FILE_NAME = "file_name";
    public static final String REQ_PARAM_ACCESS_TOKEN = "access-token";
    public static final String REQ_PARAM_DEVICE_ID = "device-id";
    public static final String REQ_PARAM_DEVICE_TYPE = "device-type";
    public static final String REQ_PARAM_DEVICE_VERSION = "device-version";

    private Map<String, String> params = new HashMap<>();
    private Map<String, String> headerParams = new HashMap<>();
    Map<String, DataPart> multipartParams = new HashMap<>();

    public DeleteDSAttachmentRequest(String accessToken, String url, String fileName,
                                     String auditId, String sectionGroupId, String sectionId,
                                     Response.Listener<String> listener,
                                     Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        params.put(REQ_PARAM_AUDIT_ID, auditId);
        params.put(REQ_PARAM_SECTION_GROUP_ID, sectionGroupId);
        params.put(REQ_PARAM_SECTION_ID, sectionId);
        params.put(REQ_PARAM_IS_FILE_NAME, fileName);
        headerParams.put(REQ_PARAM_ACCESS_TOKEN, accessToken);
        headerParams.put(REQ_PARAM_DEVICE_ID, AppConstant.DEVICE_ID);
        headerParams.put(REQ_PARAM_DEVICE_TYPE, AppConstant.DEVICE_TYPE);
        headerParams.put(REQ_PARAM_DEVICE_VERSION, AppConstant.VERSION);

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
