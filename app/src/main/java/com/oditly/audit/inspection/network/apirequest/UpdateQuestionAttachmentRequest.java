package com.oditly.audit.inspection.network.apirequest;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;

import java.util.HashMap;
import java.util.Map;

public class UpdateQuestionAttachmentRequest extends BaseStringRequest {

    //request params
    private static final String REQ_PARAM_AUDIT_ID = "audit_id";
    private static final String REQ_PARAM_SECTION_GROUP_ID = "section_group_id";
    private static final String REQ_PARAM_SECTION_ID = "section_id";
    private static final String REQ_PARAM_QUESTION_ID = "question_id";
    private static final String REQ_PARAM_IS_FILE = "file";
    public static final String REQ_PARAM_ACCESS_TOKEN = "access-token";
    public static final String REQ_PARAM_DEVICE_ID = "device-id";
    public static final String REQ_PARAM_DEVICE_TYPE = "device-type";
    public static final String REQ_PARAM_DEVICE_VERSION = "device-version";
    private static final String REQ_PARAM_QUESTION_FILEID = "audit_question_file_id";

    private Map<String, String> params = new HashMap<>();
    private Map<String, String> headerParams = new HashMap<>();
    Map<String, DataPart> multipartParams = new HashMap<>();

    public UpdateQuestionAttachmentRequest(String accessToken, String url, String fileName, byte[] byteData,
                                           String auditId, String sectionGroupId, String sectionId,
                                           String questionId, String questFileId, Context context,
                                           Response.Listener<String> listener,
                                           Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        params.put(REQ_PARAM_AUDIT_ID, auditId);
        params.put(REQ_PARAM_SECTION_GROUP_ID, sectionGroupId);
        params.put(REQ_PARAM_SECTION_ID, sectionId);
        params.put(REQ_PARAM_QUESTION_ID, questionId);
        params.put(REQ_PARAM_QUESTION_FILEID, questFileId);
        headerParams.put(REQ_PARAM_ACCESS_TOKEN, accessToken);
        headerParams.put(REQ_PARAM_DEVICE_ID, AppConstant.DEVICE_ID);
        headerParams.put(REQ_PARAM_DEVICE_TYPE, AppConstant.DEVICE_TYPE);
        headerParams.put(REQ_PARAM_DEVICE_VERSION, AppConstant.VERSION);
        headerParams.put(NetworkConstant.REQ_FIREBASE_ACCESS_TOKEN, AppPreferences.INSTANCE.getFirebaseAccessToken(context));

        DataPart dataPart = new DataPart();
            fileName=fileName+".jpeg";
            dataPart.setType("image/jpeg");
        dataPart.setFileName(fileName);
        dataPart.setContent(byteData);
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
