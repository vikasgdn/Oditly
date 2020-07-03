package com.oditly.audit.inspection.network.apirequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;

import java.util.HashMap;
import java.util.Map;

public class AddAuditSignatureRequest extends BaseStringRequest {

    //request params
    private static final String REQ_PARAM_AUDIT_ID = "audit_id";
    private static final String REQ_PARAM_IS_FILE = "file";
    public static final String REQ_PARAM_ACCESS_TOKEN = "access-token";
    public static final String REQ_PARAM_DEVICE_ID = "device-id";
    public static final String REQ_PARAM_DEVICE_TYPE = "device-type";
    public static final String REQ_PARAM_DEVICE_VERSION = "device-version";

    private Map<String, String> params = new HashMap<>();
    private Map<String, String> headerParams = new HashMap<>();
    Map<String, DataPart> multipartParams = new HashMap<>();

    public AddAuditSignatureRequest(String accessToken, String url, String fileName, byte[] byteData, String auditId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        params.put(REQ_PARAM_AUDIT_ID, auditId);
        headerParams.put(REQ_PARAM_ACCESS_TOKEN, accessToken);
        headerParams.put(REQ_PARAM_DEVICE_ID, AppConstant.DEVICE_ID);
        headerParams.put(REQ_PARAM_DEVICE_TYPE, AppConstant.DEVICE_TYPE);
        headerParams.put(REQ_PARAM_DEVICE_VERSION, AppConstant.VERSION);

        DataPart dataPart = new DataPart();
        dataPart.setFileName(fileName);
        dataPart.setContent(byteData);
        dataPart.setType("image/jpeg");
        multipartParams.put(REQ_PARAM_IS_FILE, dataPart);

        AppLogger.e("UploadImageName", ""+fileName);
        AppLogger.e("UploadImageByteData", ""+byteData);
        AppLogger.e("AttachmentParam", ""+params);
        AppLogger.e("AttachmentHeader", ""+headerParams);
        AppLogger.e("AttachmentMultiPartParam", ""+multipartParams);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headerParams;
    }

    @Override
    protected Map<String, DataPart> getByteData() throws AuthFailureError {
        return multipartParams;
    }
}
