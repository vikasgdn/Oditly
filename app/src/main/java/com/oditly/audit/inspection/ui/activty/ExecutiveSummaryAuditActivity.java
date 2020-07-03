package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.localDB.executiveSummary.ExecutiveSummaryRoot;
import com.oditly.audit.inspection.model.audit.ExecutiveSummary.ExecutiveSummaryRootObject;
import com.oditly.audit.inspection.localDB.executiveSummary.ExecutiveSummaryRoot;
import com.oditly.audit.inspection.model.audit.ExecutiveSummary.ExecutiveSummaryInfo;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.ESSaveSubmitRequest;
import com.oditly.audit.inspection.network.apirequest.GetReportRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExecutiveSummaryAuditActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.et_executive_summary_summary)
    EditText esSummary;
    @BindView(R.id.tv_summary_txt)
    TextView summaryTxt;
    @BindView(R.id.cb_executive_summary_na)
    CheckBox naStatus;
    @BindView(R.id.es_save_btn)
    Button esSaveBtn;
    @BindView(R.id.es_submit_btn)
    Button esSubmitBtn;
    @BindView(R.id.es_attachment_count)
    TextView esAttachmentCount;
    @BindView(R.id.es_add_btn)
    Button esAddBtn;
    @BindView(R.id.es_add_file_btn)
    Button esFileBtn;
    @BindView(R.id.tv_header_title)
    TextView mTitleTv;
    @BindView(R.id.count_text)
    TextView count_text;
    @BindView(R.id.ll_parent_progress)
    RelativeLayout mProgreassRL;
    Context context;
    private String auditId = "";
    private String status = "";
    private String editable = "";
    private String auditDate = "";
    private int checked = 0;
    String attachmentCount = "";
    private ExecutiveSummaryInfo info;
    private static final int AttachmentRequest = 120;
    private static final String TAG = ExecutiveSummaryAuditActivity.class.getSimpleName();

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.hideKeyboard(ExecutiveSummaryAuditActivity.this);
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
        setContentView(R.layout.activity_executive_summary_audit);
        AppPreferences.INSTANCE.initAppPreferences(this);
        context = this;
        ButterKnife.bind(ExecutiveSummaryAuditActivity.this);
        initView();
    }

    @Override
    protected void initView() {

        mTitleTv = findViewById(R.id.tv_header_title);
        mTitleTv.setText(R.string.text_executive_summary_s);
        findViewById(R.id.iv_header_left).setOnClickListener(this);
        mProgreassRL=(RelativeLayout)findViewById(R.id.ll_parent_progress);

        esSummary = findViewById(R.id.et_executive_summary_summary);
        summaryTxt = findViewById(R.id.tv_summary_txt);
        naStatus = findViewById(R.id.cb_executive_summary_na);
        esSaveBtn = findViewById(R.id.es_save_btn);
        esSubmitBtn = findViewById(R.id.es_submit_btn);
        esAttachmentCount = findViewById(R.id.es_attachment_count);
        esAddBtn = findViewById(R.id.es_add_btn);
        esFileBtn = findViewById(R.id.es_add_file_btn);
        count_text = findViewById(R.id.count_text);
        auditId = getIntent().getStringExtra(AppConstant.AUDIT_ID);
        editable = getIntent().getStringExtra(AppConstant.EDITABLE);
        //setExecutiveSummary();
        setLocalJSON();
        esSubmitBtn.setOnClickListener(this);
        esSaveBtn.setOnClickListener(this);
        esFileBtn.setOnClickListener(this);
        naStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    checked = 1;
                    AppLogger.e("CheckedNa" , "" + checked);
                    esSummary.setVisibility(View.GONE);
                    esAddBtn.setVisibility(View.GONE);
                    summaryTxt.setVisibility(View.GONE);
                    esFileBtn.setVisibility(View.GONE);
                    esAttachmentCount.setVisibility(View.GONE);
                    count_text.setVisibility(View.GONE);

                } else {
                    checked = 0;
                    AppLogger.e("CheckedNa" , "" + checked);
                    esSummary.setVisibility(View.VISIBLE);
                    esAddBtn.setVisibility(View.VISIBLE);
                    summaryTxt.setVisibility(View.VISIBLE);
                    esFileBtn.setVisibility(View.VISIBLE);
                    esAttachmentCount.setVisibility(View.VISIBLE);
                    count_text.setVisibility(View.VISIBLE);

                }
            }
        });
        /*if (editable == "0"){
            esSummary.setVisibility(View.VISIBLE);
            asAddFile.setVisibility(View.VISIBLE);
            summaryTxt.setVisibility(View.VISIBLE);
            esFileBtn.setVisibility(View.GONE);
        }else {
            esSummary.setVisibility(View.GONE);
            asAddFile.setVisibility(View.GONE);
            summaryTxt.setVisibility(View.GONE);
            esFileBtn.setVisibility(View.VISIBLE);
        }*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.es_save_btn:
                AppUtils.hideKeyboard(context, view);
                if(AppUtils.isNetworkConnected(context)){
                    if (AppUtils.isStringEmpty(auditDate)) {
                        setAuditDate();
                    } else {
                        saveExecutiveSummaryQuestion();
                    }
                }else {
                    localDataSaveDialog();
                }
                break;
            case R.id.es_submit_btn:
                if (validateQuestions()){
                    submitExecutiveSummaryQuestion();
                }
                break;
            case R.id.iv_header_left:
                onBackPressed();
                break;

            case R.id.es_add_file_btn:
                Intent addAttachment = new Intent(context, AddAttachmentActivity.class);
                addAttachment.putExtra("auditId", auditId);
                addAttachment.putExtra("sectionGroupId", "");
                addAttachment.putExtra("sectionId", "");
                addAttachment.putExtra("questionId", "");
                addAttachment.putExtra("attachType", "esSection");
                addAttachment.putExtra("status", status);
                addAttachment.putExtra("editable", editable);
                startActivityForResult(addAttachment, AttachmentRequest);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AttachmentRequest && resultCode == Activity.RESULT_OK){
            String attachmentCount = data.getStringExtra("attachmentCount");
            esAttachmentCount.setText(attachmentCount);
            //answerSavedDialog();
            //AppUtils.toast(SubSectionsActivity.this, "Answer Saved");
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

    private void setExecutiveSummary(){
       // showProgressDialog();
        mProgreassRL.setVisibility(View.VISIBLE);
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "AudioImageResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        ExecutiveSummaryRootObject executiveSummaryRootObject = new GsonBuilder().create()
                                .fromJson(object.toString(), ExecutiveSummaryRootObject.class);
                        if (executiveSummaryRootObject.getData() != null &&
                                executiveSummaryRootObject.getData().toString().length() > 0) {
                            auditDate = executiveSummaryRootObject.getData().getAudit_date();
                            info = executiveSummaryRootObject.getData();
                            setData(executiveSummaryRootObject.getData());
                        }
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context,
                                object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
              //  hideProgressDialog();
                mProgreassRL.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // hideProgressDialog();
                mProgreassRL.setVisibility(View.GONE);
                AppLogger.e(TAG, "AudioImageError: " + error.getMessage());
                AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");

            }
        };

        String integrityUrl = NetworkURL.AUDITEXECUTIVESUMMARY + "?"
                + "audit_id=" + auditId ;
        GetReportRequest getReportRequest = new GetReportRequest(AppPreferences.INSTANCE.getAccessToken(context),
                integrityUrl, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(getReportRequest);
    }

    private void saveExecutiveSummaryQuestion(){
      //  showProgressDialog();
        mProgreassRL.setVisibility(View.VISIBLE);
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "BSSaveResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                        status = "" + object.getJSONObject("data").getInt("exec_sum_status");
                        /*Toast.makeText(context, "Answer Saved", Toast.LENGTH_SHORT).show();
                        Intent result = new Intent();
                        result.putExtra("status", status);
                        setResult(RESULT_OK, result);
                        finish();*/
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context,
                                object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
              //  hideProgressDialog();
                mProgreassRL.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  hideProgressDialog();
                mProgreassRL.setVisibility(View.GONE);
                AppLogger.e(TAG, "BSSaveError: " + error.getMessage());
                AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");

            }
        };

        String saveUrl = NetworkURL.AUDITEXECUTIVESUMMARY ;
        ESSaveSubmitRequest esSaveSubmitRequest = new ESSaveSubmitRequest(AppPreferences.INSTANCE.getAccessToken(context),
                saveUrl, auditId, auditDate, "1", "" + checked, esSummary.getText().toString(), stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(esSaveSubmitRequest);
    }

    private void submitExecutiveSummaryQuestion(){
       // showProgressDialog();
        mProgreassRL.setVisibility(View.VISIBLE);
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "BSSaveResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        Toast.makeText(context, "Answer Submitted", Toast.LENGTH_SHORT).show();
                        //status = "" + object.getInt("bs_status");
                        Intent result = new Intent();
                        result.putExtra("status", status);
                        setResult(RESULT_OK, result);
                        finish();
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context,
                                object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
              //  hideProgressDialog();
                mProgreassRL.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // hideProgressDialog();
                mProgreassRL.setVisibility(View.GONE);
                AppLogger.e(TAG, "BSSaveError: " + error.getMessage());
                AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");

            }
        };

        String submitUrl = NetworkURL.AUDITEXECUTIVESUMMARY ;
        ESSaveSubmitRequest esSaveSubmitRequest = new ESSaveSubmitRequest(AppPreferences.INSTANCE.getAccessToken(context),
                submitUrl, auditId, auditDate, "0", "" + checked, esSummary.getText().toString(), stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(esSaveSubmitRequest);
    }

    private void setData(ExecutiveSummaryInfo executiveSummaryInfo){
        status = "" + executiveSummaryInfo.getExec_sum_status();
        showViewAccStatus(executiveSummaryInfo);

        if (!AppUtils.isStringEmpty(executiveSummaryInfo.getExecutive_summary())) {
            esSummary.setText(executiveSummaryInfo.getExecutive_summary());
        }
        if (!AppUtils.isStringEmpty("" + executiveSummaryInfo.getFile_count())){
            esAttachmentCount.setText("" + executiveSummaryInfo.getFile_count());
        }
        if (executiveSummaryInfo.getIs_na() == 1){
            naStatus.setChecked(true);
            esSummary.setVisibility(View.GONE);
            esAddBtn.setVisibility(View.GONE);
            summaryTxt.setVisibility(View.GONE);
            esFileBtn.setVisibility(View.GONE);
        }else {
            naStatus.setChecked(false);
            esSummary.setVisibility(View.VISIBLE);
            summaryTxt.setVisibility(View.VISIBLE);
            if (editable.equals("0")){
                esSummary.setEnabled(true);
                naStatus.setEnabled(true);
                esAddBtn.setVisibility(View.VISIBLE);
            }else {
                esSummary.setEnabled(false);
                naStatus.setEnabled(false);
                esAddBtn.setVisibility(View.GONE);
            }
        }
    }

   /* private void setActionBar() {
        initToolbar(toolbar);
        setTitle("Executive Summary");
        enableBack(true);
        //enableBackPressed();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }*/

    private void showViewAccStatus(ExecutiveSummaryInfo executiveSummaryInfo){
        switch (executiveSummaryInfo.getExec_sum_status()){
            case 0:
                break;
            case 1:
                esSummary.setEnabled(true);
                esAddBtn.setVisibility(View.VISIBLE);
                esSaveBtn.setVisibility(View.VISIBLE);
                esSubmitBtn.setVisibility(View.VISIBLE);
                break;
            case 2:
                esSummary.setEnabled(true);
                esAddBtn.setVisibility(View.VISIBLE);
                esSaveBtn.setVisibility(View.VISIBLE);
                esSubmitBtn.setVisibility(View.VISIBLE);
                break;
            case 3:
                esSummary.setEnabled(true);
                esAddBtn.setVisibility(View.VISIBLE);
                esSaveBtn.setVisibility(View.VISIBLE);
                esSubmitBtn.setVisibility(View.VISIBLE);
                break;
            case 4:
                esSummary.setEnabled(false);
                esAddBtn.setVisibility(View.GONE);
                esSaveBtn.setVisibility(View.GONE);
                esSubmitBtn.setVisibility(View.GONE);
                break;
            case 5:
                esSummary.setEnabled(false);
                esAddBtn.setVisibility(View.GONE);
                esSaveBtn.setVisibility(View.GONE);
                esSubmitBtn.setVisibility(View.GONE);
                break;
        }
    }

    private boolean validateQuestions(){
        boolean validate = true;

        if(checked == 1){
            return true;
        }else if (esSummary.getText().length() <= 5000){
            AppUtils.toast(ExecutiveSummaryAuditActivity.this, "Enter summary with at least 5000 characters.");
            return  false;
        }

        return validate;
    }

    private void saveLocalDB(){
        String localDB = AppPreferences.INSTANCE.getESLocalDB();
        JSONArray jsonArray = null;
        try {
            if (!AppUtils.isStringEmpty(localDB)) {
                jsonArray = new JSONArray(localDB);

                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ExecutiveSummaryRoot brandStandardRoot = new GsonBuilder().create()
                                .fromJson(jsonObject.toString(), ExecutiveSummaryRoot.class);
                        if (brandStandardRoot.getAuditId().equals(auditId)) {
                            ExecutiveSummaryInfo summaryInfo = brandStandardRoot.getSections();
                            summaryInfo.setIs_na(Integer.valueOf(checked));
                            summaryInfo.setExecutive_summary(esSummary.getText().toString());
                            try {
                                if (info != null) {
                                    if (AppUtils.isStringEmpty(String.valueOf(info.getExec_sum_status()))) {
                                        summaryInfo.setExec_sum_status(info.getExec_sum_status());
                                    }
                                    if (AppUtils.isStringEmpty(String.valueOf(info.getFile_count()))) {
                                        summaryInfo.setFile_count(info.getFile_count());
                                    }
                                    if (AppUtils.isStringEmpty(String.valueOf(info.getAudit_date()))) {
                                        summaryInfo.setAudit_date(info.getAudit_date());
                                    }
                                    if (AppUtils.isStringEmpty(String.valueOf(info.getReviewer_exec_sum_comment()))) {
                                        summaryInfo.setReviewer_exec_sum_comment(info.getReviewer_exec_sum_comment());
                                    }
                                }
                            }catch (Exception e){e.printStackTrace();}

                        }

                    }
                } else {
                    AppLogger.e(TAG, "add new root object with new audit id");
                }
            }else {
                ExecutiveSummaryInfo summaryInfo = new ExecutiveSummaryInfo();
                summaryInfo.setIs_na(Integer.valueOf(checked));
                summaryInfo.setExecutive_summary(esSummary.getText().toString());
                summaryInfo.setExec_sum_status(info.getExec_sum_status());
                summaryInfo.setFile_count(info.getFile_count());
                summaryInfo.setAudit_date(info.getAudit_date());
                summaryInfo.setReviewer_exec_sum_comment(info.getReviewer_exec_sum_comment());
                Gson gson = new Gson();
                String jsonString = gson.toJson(summaryInfo);
                JSONObject jO = new JSONObject(jsonString);


                jsonArray = new JSONArray();
                JSONObject Object = new JSONObject();
                Object.put("auditId", auditId);
                Object.put("sections", jO);
                jsonArray.put(Object);
                AppLogger.e(TAG, "add new root object with new audit id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppPreferences.INSTANCE.setESLocalDB("" + jsonArray);
        Intent result = new Intent();
        setResult(RESULT_OK, result);
        finish();
    }

    private JSONArray setLocalJSON() {
        String localDB = AppPreferences.INSTANCE.getESLocalDB();
        JSONArray jsonArray = null;
        try {
            if (!AppUtils.isStringEmpty(localDB)) {
                jsonArray = new JSONArray(localDB);

                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ExecutiveSummaryRoot brandStandardRoot = new GsonBuilder().create()
                                .fromJson(jsonObject.toString(), ExecutiveSummaryRoot.class);
                        if (brandStandardRoot.getAuditId().equals(auditId)) {
                            answerShowDialog(brandStandardRoot.getSections());
                        }
                    }
                }
            }else {
                setExecutiveSummary();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return jsonArray;
    }

    private void localDataSaveDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(R.string.app_name);
        dialog.setMessage("No Internet Connection. Do you want to save data locally?");

        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveLocalDB();
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

    private void answerShowDialog(final ExecutiveSummaryInfo executiveSummaryInfo) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(R.string.app_name);
        dialog.setMessage("You have unsaved answered locally in your device. Would you like to sync them?");

        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setData(executiveSummaryInfo);
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
                setExecutiveSummary();
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }
}
