package com.oditly.audit.inspection.network.apirequest;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateActionPlanAdHocAttachmentRequest extends BaseStringRequest {

    //request params
    private static final String REQ_PARAM_AUDIT_ID = "audit_id";
    private static final String REQ_PARAM_SECTION_GROUP_ID = "section_group_id";
    private static final String REQ_PARAM_SECTION_ID = "section_id";
    private static final String REQ_PARAM_QUESTION_ID = "question_id";
    private static final String REQ_PARAM_DESCRIPTION = "description";
    private static final String REQ_PARAM_IS_CRITICAL = "is_critical";
    private static final String REQ_PARAM_IS_FILE = "file";
    private static final String REQ_PARAM_LATITUDE = "latitude";
    private static final String REQ_PARAM_LONGITUDE = "longitude";
    public static final String REQ_PARAM_ACCESS_TOKEN = "access-token";
    public static final String REQ_PARAM_DEVICE_ID = "device-id";
    public static final String REQ_PARAM_DEVICE_TYPE = "device-type";
    public static final String REQ_PARAM_DEVICE_VERSION = "device-version";

    private Map<String, String> params = new HashMap<>();
    private Map<String, String> headerParams = new HashMap<>();
    Map<String, DataPart> multipartParams = new HashMap<>();

    public CreateActionPlanAdHocAttachmentRequest(String accessToken, String mLocatioID, String mPriorityID, String mSectionID, String date, String title, String details, String count, JSONArray jsArray, ArrayList<Uri> mURIimageList, Response.Listener<String> stringListener, Response.ErrorListener errorListener) {

        super(Method.POST, NetworkURL.ACTION_PLAN_ADD, stringListener, errorListener);
        params.put(NetworkConstant.REQ_PARAM_MOBILE, "1");
        params.put(NetworkConstant.REQ_PARAM_LOCATIONID, mLocatioID);
        params.put(NetworkConstant.REQ_PARAM_PRIORITYID, mPriorityID);
        params.put(NetworkConstant.REQ_PARAM_SECTIONID, mSectionID);
        params.put(NetworkConstant.REQ_PARAM_PLANNED_DATE, date);
        params.put(NetworkConstant.REQ_PARAM_ASSIGNED_USERID, jsArray.toString());
        params.put(NetworkConstant.REQ_PARAM_TITLE, title);
        params.put(NetworkConstant.REQ_PARAM_ACTION_DETAILS, details);
        params.put(NetworkConstant.REQ_PARAM_COMPLETE_MEDIA_COUNT, count);

        headerParams.put(REQ_PARAM_ACCESS_TOKEN, accessToken);
        headerParams.put(REQ_PARAM_DEVICE_ID, AppConstant.DEVICE_ID);
        headerParams.put(REQ_PARAM_DEVICE_TYPE, AppConstant.DEVICE_TYPE);
        headerParams.put(REQ_PARAM_DEVICE_VERSION, AppConstant.VERSION);

        for (int i=0;i<mURIimageList.size();i++)
        {
            DataPart dataPart = new DataPart();
            String fileName="";
            byte[] imageByteData = new byte[0];
            try {
                imageByteData = AppUtils.readBytes(mURIimageList.get(i), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            dataPart.setType("image/jpeg");
            dataPart.setFileName(fileName);
            dataPart.setContent(imageByteData);
            multipartParams.put(REQ_PARAM_IS_FILE, dataPart);
        }

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
