package com.oditly.audit.inspection.services;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.localDB.bsoffline.BsOfflineDBImpl;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.GetReportRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;

public class QuestionData extends IntentService {

    private BsOfflineDBImpl bsOfflineDB;
    public QuestionData() {
        super("QuestionData");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        bsOfflineDB=BsOfflineDBImpl.getInstance(getApplicationContext());
         if (intent!=null)
             setBrandStandardQuestion(intent.getStringExtra(AppConstant.AUDIT_ID));
    }

    private void setBrandStandardQuestion(String auditID){
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                bsOfflineDB.deleteOfflineQuestionJSON(auditID);
                bsOfflineDB.saveOffileQuestionJSONToDB(auditID,response);
             //   AppLogger.e("TAG", "BrandStandardResponse: " + response);

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // AppLogger.e("TAG", "AudioImageError: " + error.getMessage());
            }
        };
        String questionListUrl = NetworkURL.BRANDSTANDARD + "?" + "audit_id=" + auditID ;
        GetReportRequest getReportRequest = new GetReportRequest(AppPreferences.INSTANCE.getAccessToken(this), questionListUrl, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(getApplicationContext()).addToRequestQueue(getReportRequest);
    }

}
