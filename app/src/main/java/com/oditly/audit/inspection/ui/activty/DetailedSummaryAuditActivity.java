package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.DetailedSummaryAuditAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.ui.activty.BaseActivity;
import com.oditly.audit.inspection.localDB.detailedSummary.DetailedSummaryRoot;
import com.oditly.audit.inspection.model.audit.DetailedSummary.DetailedSummaryInfo;
import com.oditly.audit.inspection.model.audit.DetailedSummary.DetailedSummaryRootObject;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.DSSaveSubmitJsonRequest;
import com.oditly.audit.inspection.network.apirequest.GetReportRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedSummaryAuditActivity extends BaseActivity implements View.OnClickListener, DetailedSummaryAuditAdapter.CustomItemClickListener {

    @BindView(R.id.rv_detailed_summary_audit)
    RecyclerView rvDetailedSummaryAudit;
    @BindView(R.id.ds_save_btn)
    Button dsSaveBtn;
    @BindView(R.id.ds_submit_btn)
    Button dsSubmitBtn;
    @BindView(R.id.tv_header_title)
    TextView mTitleTV;
    @BindView(R.id.ll_parent_progress)
    RelativeLayout mProgressRL;
    Context context;
    private String status = "";
    private String editable = "";
    private String auditId = "";
    private String auditDate = "";
    public LayoutInflater inflater;
    private static final int AttachmentRequest = 120;
    public static DetailedSummaryInfo detailedSummary = new DetailedSummaryInfo();
    ArrayList<DetailedSummaryInfo> arrayList = new ArrayList<>();
    DetailedSummaryAuditAdapter detailedSummaryAuditAdapter;
    private int itemClickedPos = 0;
    private static final String TAG = DetailedSummaryAuditActivity.class.getSimpleName();

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.hideKeyboard(DetailedSummaryAuditActivity.this);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent result = new Intent();
        result.putExtra("status", status);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_summary_audit);
        AppPreferences.INSTANCE.initAppPreferences(this);
        inflater = getLayoutInflater();
        context = this;
        ButterKnife.bind(DetailedSummaryAuditActivity.this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        mTitleTV = findViewById(R.id.tv_header_title);
        mTitleTV.setText(R.string.text_detailed_summary_s);
     //   setActionBar();
        rvDetailedSummaryAudit = findViewById(R.id.rv_detailed_summary_audit);
        mProgressRL=(RelativeLayout)findViewById(R.id.ll_parent_progress);
        dsSaveBtn = findViewById(R.id.ds_save_btn);
        dsSubmitBtn = findViewById(R.id.ds_submit_btn);
        findViewById(R.id.iv_header_left).setOnClickListener(this);
        auditId = getIntent().getStringExtra(AppConstant.AUDIT_ID);
        editable = getIntent().getStringExtra(AppConstant.EDITABLE);


        //setDetailedSummaryQuestion();
        setLocalJSON();
        dsSaveBtn.setOnClickListener(this);
        dsSubmitBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ds_save_btn:
                AppUtils.hideKeyboard(context, view);
                if(AppUtils.isNetworkConnected(context)){
                    if (AppUtils.isStringEmpty(auditDate)) {
                        setAuditDate();
                    } else {
                        saveDetailedSummaryQuestion();
                    }
                }else {
                    localDataSaveDialog(detailedSummaryAuditAdapter.getArrayList());
                }
                break;
            case R.id.ds_submit_btn:
                AppUtils.hideKeyboard(context, view);
                if (validateInput(arrayList)) {
                    submitDetailedSummaryQuestion();
                }
                break;
            case R.id.iv_header_left:
                  onBackPressed();
                break;
        }
    }

    private void setAuditDate() {
        Calendar auditMonthCal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth);
                        auditDate = AppUtils.getAuditDate(cal.getTime());
                    }
                }, auditMonthCal.get(Calendar.YEAR), auditMonthCal.get(Calendar.MONTH),
                auditMonthCal.get(Calendar.YEAR));
        datePickerDialog.getDatePicker().setMaxDate(auditMonthCal.getTimeInMillis());
        datePickerDialog.setTitle("Select Audit Date");
        datePickerDialog.show();
    }

    private void setDetailedSummaryQuestion(){
      //  showProgressDialog();
        mProgressRL.setVisibility(View.VISIBLE);
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "AudioImageResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        DetailedSummaryRootObject brandStandardRootObject = new GsonBuilder().create()
                                .fromJson(object.toString(), DetailedSummaryRootObject.class);
                        if (brandStandardRootObject.getData() != null &&
                                brandStandardRootObject.getData().toString().length() > 0) {
                            status = "" + brandStandardRootObject.getData().get(0).getDetailed_sum_status();
                            auditDate = brandStandardRootObject.getData().get(0).getAudit_date();
                            showViewAccStatus(brandStandardRootObject.getData().get(0));
                            arrayList.addAll(brandStandardRootObject.getData());
                            setSummaryData(arrayList);
                            //setData(brandStandardRootObject.getData());
                            detailedSummaryAuditAdapter.notifyDataSetChanged();
                        }
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context,
                                object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
               // hideProgressDialog();
                mProgressRL.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // hideProgressDialog();
                mProgressRL.setVisibility(View.GONE);
                AppLogger.e(TAG, "AudioImageError: " + error.getMessage());
                AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");

            }
        };

        String integrityUrl = NetworkURL.AUDITDETAILEDSUMMARY + "?" + "audit_id=" + auditId ;
        GetReportRequest getReportRequest = new GetReportRequest(AppPreferences.INSTANCE.getAccessToken(context),
                integrityUrl, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(getReportRequest);
    }

    private void setSummaryData(ArrayList<DetailedSummaryInfo> arrayList){
        ArrayList<DetailedSummaryInfo> integrityInfos = new ArrayList<>();
        integrityInfos.addAll(arrayList);
        detailedSummaryAuditAdapter = new DetailedSummaryAuditAdapter(context, integrityInfos, editable, DetailedSummaryAuditActivity.this);
        rvDetailedSummaryAudit.setLayoutManager(new LinearLayoutManager(context));
        rvDetailedSummaryAudit.setAdapter(detailedSummaryAuditAdapter);
    }

    private void saveDetailedSummaryQuestion(){
      //  showProgressDialog();
        mProgressRL.setVisibility(View.VISIBLE);
        JSONObject object = DSSaveSubmitJsonRequest.createInput(auditId, auditDate, "1", getQuestionsArray());
        AppLogger.e(TAG, "" + object);
        Response.Listener stringListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AppLogger.e(TAG, "BSResponse: " + response);
                try {
                    if (!response.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, response.getString(AppConstant.RES_KEY_MESSAGE));
                        //setDetailedSummaryQuestion();
                        status = "" + response.getJSONObject("data").getInt("detailed_sum_status");
                        /*Toast.makeText(context, "Answer Saved", Toast.LENGTH_SHORT).show();
                        Intent result = new Intent();
                        result.putExtra("status", status);
                        setResult(RESULT_OK, result);
                        finish();*/
                    } else if (response.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context,
                                response.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
              //  hideProgressDialog();
                mProgressRL.setVisibility(View.GONE);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               //  hideProgressDialog();
                mProgressRL.setVisibility(View.GONE);
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        String message = obj.getString("message");
                        AppLogger.e("Error: ", "" + obj);
                        AppUtils.toast((BaseActivity) context,
                                message);
                    } catch (UnsupportedEncodingException e1) {
                        //Couldn't properly decode data to string
                            /*if (context != null) {
                                AppUtils.toast((BaseActivity) context,
                                        getString(R.string.alert_msg_invalid_response));
                            }*/
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                            /*if (context != null) {
                                AppUtils.toast((BaseActivity) context,
                                        getString(R.string.alert_msg_invalid_response));
                            }*/
                        //returned data is not JSONObject?
                        e2.printStackTrace();
                    }

                }
            }
        };

        String brandstandard = NetworkURL.AUDITDETAILEDSUMMARY ;

        DSSaveSubmitJsonRequest dsSaveSubmitJsonRequest = new DSSaveSubmitJsonRequest(
                AppPreferences.INSTANCE.getAccessToken(context), brandstandard, object, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(dsSaveSubmitJsonRequest);

    }

    private void submitDetailedSummaryQuestion(){
       // showProgressDialog();
        mProgressRL.setVisibility(View.VISIBLE);
        JSONObject object = DSSaveSubmitJsonRequest.createInput(auditId, auditDate, "0", getQuestionsArray());
        AppLogger.e(TAG, "" + object);
        Response.Listener stringListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AppLogger.e(TAG, "BSResponse: " + response);
                try {
                    if (!response.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        //AppUtils.toast((BaseActivity) context, response.getString(ApiResponseKeys.RES_KEY_MESSAGE));
                        Toast.makeText(context, "Answer Submitted", Toast.LENGTH_SHORT).show();
                        //setDetailedSummaryQuestion();
                        //status = "" + response.getInt("bs_status");
                        Intent result = new Intent();
                        result.putExtra("status", status);
                        setResult(RESULT_OK, result);
                        finish();
                    } else if (response.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, response.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
               // hideProgressDialog();
                mProgressRL.setVisibility(View.GONE);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   hideProgressDialog();
                mProgressRL.setVisibility(View.GONE);
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        String message = obj.getString("message");
                        AppLogger.e("Error: ", "" + obj);
                        AppUtils.toast((BaseActivity) context,
                                message);
                    } catch (UnsupportedEncodingException e1) {
                        //Couldn't properly decode data to string
                            /*if (context != null) {
                                AppUtils.toast((BaseActivity) context,
                                        getString(R.string.alert_msg_invalid_response));
                            }*/
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                            /*if (context != null) {
                                AppUtils.toast((BaseActivity) context,
                                        getString(R.string.alert_msg_invalid_response));
                            }*/
                        //returned data is not JSONObject?
                        e2.printStackTrace();
                    }

                }
            }
        };

        String brandstandard = NetworkURL.AUDITDETAILEDSUMMARY ;

        DSSaveSubmitJsonRequest dsSaveSubmitJsonRequest = new DSSaveSubmitJsonRequest(
                AppPreferences.INSTANCE.getAccessToken(context), brandstandard, object,
                stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(dsSaveSubmitJsonRequest);

    }


    private JSONArray getQuestionsArray (){
        JSONArray jsonArray = new JSONArray();
        ArrayList<DetailedSummaryInfo> arrayList = detailedSummaryAuditAdapter.getArrayList();
        for (int i =0;i<arrayList.size();i++){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("section_group_id",arrayList.get(i).getSection_group_id());
                jsonObject.put("section_id",arrayList.get(i).getSection_id());
                jsonObject.put("staff_name",arrayList.get(i).getStaff_name());
                jsonObject.put("date_time",arrayList.get(i).getDate() +" "+arrayList.get(i).getTime() + ":00");
                jsonObject.put("is_na",arrayList.get(i).getIs_na());
                jsonObject.put("summary",arrayList.get(i).getSummary());
                jsonObject.put("key_negative",arrayList.get(i).getKey_negative());
                jsonObject.put("key_positive",arrayList.get(i).getKey_positive());
                jsonObject.put("recommendation",arrayList.get(i).getRecommendation());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return jsonArray;
    }

    public boolean validateInput(ArrayList<DetailedSummaryInfo> arrayList){
        boolean validate = true;
        for (int i = 0 ; i < arrayList.size() ; i++) {
            if(arrayList.get(i).getIs_na() == 0){
                DetailedSummaryInfo detailedSummaryInfo = arrayList.get(i);
                if (AppUtils.isStringEmpty(detailedSummaryInfo.getStaff_name())) {
                    AppUtils.toast(DetailedSummaryAuditActivity.this, "Please fill staff name in " + detailedSummaryInfo.getSection_title());
                    return false;
                } else if (AppUtils.isStringEmpty(detailedSummaryInfo.getDate())) {
                    AppUtils.toast(DetailedSummaryAuditActivity.this, "Please fill date in " + detailedSummaryInfo.getSection_title());
                    return false;
                } else if (AppUtils.isStringEmpty(detailedSummaryInfo.getTime())) {
                    AppUtils.toast(DetailedSummaryAuditActivity.this, "Please fill time in " + detailedSummaryInfo.getSection_title());
                    return false;
                } else if (AppUtils.isStringEmpty(detailedSummaryInfo.getStaff_name()) && detailedSummaryInfo.getStaff_name().length() < 100) {
                    AppUtils.toast(DetailedSummaryAuditActivity.this, "Please fill atleast 100 character in " + detailedSummaryInfo.getSection_title());
                    return false;
                }
            }

        }
        return validate;
    }

    private void showViewAccStatus(DetailedSummaryInfo detailedSummaryInfo){
        switch (detailedSummaryInfo.getDetailed_sum_status()){
            case 0:
                break;
            case 1:
                dsSaveBtn.setVisibility(View.VISIBLE);
                dsSubmitBtn.setVisibility(View.VISIBLE);
                break;
            case 2:
                dsSaveBtn.setVisibility(View.VISIBLE);
                dsSubmitBtn.setVisibility(View.VISIBLE);
                break;
            case 3:
                dsSaveBtn.setVisibility(View.VISIBLE);
                dsSubmitBtn.setVisibility(View.VISIBLE);
                break;
            case 4:
                dsSaveBtn.setVisibility(View.GONE);
                dsSubmitBtn.setVisibility(View.GONE);
                break;
            case 5:
                dsSaveBtn.setVisibility(View.GONE);
                dsSubmitBtn.setVisibility(View.GONE);
                break;
        }
    }

  /*  private void setActionBar() {
        initToolbar(toolbar);
        setTitle("Detailed Summary");
        enableBack(true);
        //enableBackPressed();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }*/

    @Override
    public void onItemClick(int sectionGroupId, int sectionId, String attachtype, int position) {
        itemClickedPos = position;
        Intent addAttachment = new Intent(context, AddAttachmentActivity.class);
        addAttachment.putExtra("auditId", auditId);
        addAttachment.putExtra("sectionGroupId", "" +sectionGroupId);
        addAttachment.putExtra("sectionId", "" +sectionId);
        addAttachment.putExtra("questionId", "");
        addAttachment.putExtra("attachType", attachtype);
        addAttachment.putExtra("editable", editable);
        startActivityForResult(addAttachment, AttachmentRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AttachmentRequest && resultCode == Activity.RESULT_OK) {
            String attachmentCount = data.getStringExtra("attachmentCount");
            detailedSummaryAuditAdapter.setattachmentCount(Integer.parseInt(attachmentCount),itemClickedPos);

        }
    }

    private void saveLocalDB(ArrayList<DetailedSummaryInfo> detailedSummaryInfos){
        boolean ifExist = false;
        String localDB = AppPreferences.INSTANCE.getDSLocalDB();
        JSONArray jsonArray = null;
        try {
            if (!AppUtils.isStringEmpty(localDB)) {
                jsonArray = new JSONArray(localDB);

                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        DetailedSummaryRoot detailedSummaryRoot = new GsonBuilder().create()
                                .fromJson(jsonObject.toString(), DetailedSummaryRoot.class);
                        ArrayList<DetailedSummaryInfo> arrayList = new ArrayList<>();
                        arrayList.addAll(detailedSummaryRoot.getSections());
                        if (detailedSummaryRoot.getAuditId().equals(auditId)) {
                            //arrayList.replaceAll(detailedSummaryInfos);
                            AppLogger.e(TAG, "replace db data of same section with new one");

                        } else {
                            arrayList.addAll(detailedSummaryInfos);
                            AppLogger.e(TAG, "add new root object with new audit id");
                        }
                    }
                } else {
                    AppLogger.e(TAG, "add new root object with new audit id");
                }
            } else {

                String jsonString = new Gson().toJson(detailedSummaryInfos);
                JSONArray jsonArray1 = new JSONArray(jsonString);
                jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("auditId", auditId);
                jsonObject.put("sections", jsonArray1);
                jsonArray.put(jsonObject);
                AppLogger.e(TAG, "add new root object with new audit id");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppPreferences.INSTANCE.setDSLocalDB("" + jsonArray);
        Intent result = new Intent();
        setResult(RESULT_OK, result);
        finish();
    }

    private JSONArray setLocalJSON() {
        String localDB = AppPreferences.INSTANCE.getDSLocalDB();
        JSONArray jsonArray = null;
        try {
            if (!AppUtils.isStringEmpty(localDB)) {
                //AppPrefs.setDSLocalDB(context, "");
                jsonArray = new JSONArray(localDB);

                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        DetailedSummaryRoot detailedSummaryRoot = new GsonBuilder().create()
                                .fromJson(jsonObject.toString(), DetailedSummaryRoot.class);
                        ArrayList<DetailedSummaryInfo> arrayList = new ArrayList<>();
                        arrayList.addAll(detailedSummaryRoot.getSections());
                        if (detailedSummaryRoot.getAuditId().equals(auditId)) {
                            answerShowDialog(arrayList);
                            AppLogger.e(TAG, "You have already saved data for this section id do you want to override it");
                        } else {

                        }
                    }
                }
            }else {
                setDetailedSummaryQuestion();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



        return jsonArray;
    }

    private void localDataSaveDialog(final ArrayList<DetailedSummaryInfo> detailedSummaryInfos) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(R.string.app_name);
        dialog.setMessage("No Internet Connection. Do you want to save data locally?");

        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveLocalDB(detailedSummaryInfos);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    private void answerShowDialog(final ArrayList<DetailedSummaryInfo> detailedSummaryInfos) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(R.string.app_name);
        dialog.setMessage("You have unsaved answered locally in your device. Would you like to sync them?");

        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setSummaryData(detailedSummaryInfos);
                AppLogger.e(TAG, "Replace it in adapter");
                /*if (true) {
                    AppLogger.e(TAG, "Replace it in adapter");
                } else {
                    AppLogger.e(TAG, "Continue and dismiss dialog");
                }*/
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                setDetailedSummaryQuestion();
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }
}
