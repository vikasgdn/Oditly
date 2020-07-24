package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.SubSectionTabAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardInfo;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestion;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardRootObject;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSection;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSubSection;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.BSSaveSubmitJsonRequest;
import com.oditly.audit.inspection.network.apirequest.GetReportRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubSectionsActivity extends BaseActivity implements SubSectionTabAdapter.CustomItemClickListener, INetworkEvent {

    @BindView(R.id.tv_header_title)
    TextView mHeaderTitleTV;
    @BindView(R.id.rv_sub_section_tab)
    RecyclerView subSectionTabList;
    @BindView(R.id.continue_btn)
    Button continueBtn;
    @BindView(R.id.simpleProgressBar)
    ProgressBar statusProgressBar;
    @BindView(R.id.completeProgressBar)
    ProgressBar completeProgressBar;
    @BindView(R.id.tv_status_text)
    TextView statusText;
    @BindView(R.id.rejected_comment_layout)
    LinearLayout rejectedCommentLayout;
    @BindView(R.id.tv_rejected_comment)
    TextView rejectedComment;
    @BindView(R.id.ll_parent_progress)
    RelativeLayout mSpinKitView;
    @BindView(R.id.tv_auditname)
    TextView mAuditNameTV;

    public static  boolean isDataSaved=true;

    private String status = "";
    private String auditId = "";
    private String auditDate = "";
    private String editable = "";
    private String mAuditName="";

    // private String bsStatus = "";
    private  Context context;
    private static final int FillQuestionRequest = 101;
    private  JSONArray answerArray ;
    private ArrayList<BrandStandardSection> brandStandardSections;
    private static final String TAG = SubSectionsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_sections);
        ButterKnife.bind(this);
        context = this;
        isDataSaved=true;
        AppPreferences.INSTANCE.initAppPreferences(this);
        initView();
        initVar();
    }



    @Override
    protected void initView() {
        super.initView();
        mHeaderTitleTV = findViewById(R.id.tv_header_title);
        subSectionTabList = findViewById(R.id.rv_sub_section_tab);
        continueBtn = findViewById(R.id.continue_btn);
        statusProgressBar= findViewById(R.id.simpleProgressBar);
        completeProgressBar= findViewById(R.id.completeProgressBar);
        statusText= findViewById(R.id.tv_status_text);
        rejectedComment= findViewById(R.id.tv_rejected_comment);
        rejectedCommentLayout= findViewById(R.id.rejected_comment_layout);
        mSpinKitView=findViewById(R.id.ll_parent_progress);
        mAuditNameTV=findViewById(R.id.tv_auditname);

        findViewById(R.id.iv_header_left).setOnClickListener(this);
        continueBtn.setOnClickListener(this);

    }

    @Override
    protected void initVar() {
        super.initVar();
        mHeaderTitleTV.setText(R.string.inspection_option);
        auditId = getIntent().getStringExtra(AppConstant.AUDIT_ID);
        mAuditName = getIntent().getStringExtra(AppConstant.AUDIT_NAME);
        editable = getIntent().getStringExtra(AppConstant.EDITABLE);

        mAuditNameTV.setText(mAuditName+" (ID:"+auditId+")");

        if (editable.equals("0")){
            continueBtn.setVisibility(View.VISIBLE);
        }else {
            continueBtn.setVisibility(View.GONE);
        }


    }
    @Override
    protected void onResume() {
        super.onResume();
        if (AppUtils.isNetworkConnected(context))
        {
            if (isDataSaved)
                setBrandStandardQuestion();
        }else {
            AppUtils.toastDisplayForLong(this,getString(R.string.internet_error));
            finish();
        }

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.iv_header_left:
                onBackPressed();
                break;
            case R.id.continue_btn:
                if (brandStandardSections !=null && validateSubmitQuestion(brandStandardSections))
                    submitBrandStandardQuestion();
                break;
        }
    }

    private String messsage="";
    private void setBrandStandardQuestion(){
        mSpinKitView.setVisibility(View.VISIBLE);
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                isDataSaved=false;
                AppLogger.e(TAG, "BrandStandardResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    messsage =  object.getString(AppConstant.RES_KEY_MESSAGE);
                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        BrandStandardRootObject brandStandardRootObject = new GsonBuilder().create().fromJson(object.toString(), BrandStandardRootObject.class);
                        if (brandStandardRootObject.getData() != null && brandStandardRootObject.getData().toString().length() > 0) {
                            auditDate = brandStandardRootObject.getData().getAudit_date();
                            status = "" + brandStandardRootObject.getData().getBrand_std_status();
                            setRejectedComment(brandStandardRootObject.getData());
                            setQuestionList(brandStandardRootObject.getData());
                            float count = 0;
                            float totalCount = 0;
                            int[] result = statusQuestionCount(brandStandardRootObject.getData().getSections());
                            count = (float) result[0];
                            totalCount = (float) result[1];
                            setProgressBar(count, totalCount);
                            //brandStandardAuditAdapter.notifyDataSetChanged();
                        }
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    AppUtils.toast((BaseActivity) context,messsage);
                }
                //   hideProgressDialog();
                mSpinKitView.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // hideProgressDialog();
                mSpinKitView.setVisibility(View.GONE);
                AppLogger.e(TAG, "AudioImageError: " + error.getMessage());
                AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");

            }
        };

        String integrityUrl = NetworkURL.BRANDSTANDARD + "?" + "audit_id=" + auditId ;
        GetReportRequest getReportRequest = new GetReportRequest(AppPreferences.INSTANCE.getAccessToken(this), integrityUrl, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(getReportRequest);
    }

    private void setQuestionList(BrandStandardInfo info){
        brandStandardSections = new ArrayList<>();
        brandStandardSections.addAll(info.getSections());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,3,LinearLayoutManager.VERTICAL,false);
        SubSectionTabAdapter subSectionTabAdapter = new SubSectionTabAdapter(this, brandStandardSections, editable, SubSectionsActivity.this);
        subSectionTabList.setLayoutManager(gridLayoutManager);
        subSectionTabList.setAdapter(subSectionTabAdapter);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent result = new Intent();
        result.putExtra("status", status);
        setResult(RESULT_OK, result);
        finish();
    }

    private void setProgressBar(float filledQuestionCount, float totalQuestionCount){
        try {

            float percent = (filledQuestionCount / totalQuestionCount) * 100;
            DecimalFormat decimalFormat = new DecimalFormat("0.0");

            statusText.setText("" + decimalFormat.format(percent) + "% Completed");
            int intValue = (int)percent;
            AppLogger.e(TAG, "value" + intValue);

            if (intValue == 100){
                completeProgressBar.setVisibility(View.VISIBLE);
                statusProgressBar.setVisibility(View.GONE);
                completeProgressBar.setProgress(intValue);
                statusText.setTextColor(getResources().getColor(R.color.scoreGreen));
            }else {
                completeProgressBar.setVisibility(View.GONE);
                statusProgressBar.setVisibility(View.VISIBLE);
                statusProgressBar.setProgress(intValue);
                statusProgressBar.setMax(100);
                statusText.setTextColor(getResources().getColor(R.color.c_blue));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setRejectedComment(BrandStandardInfo brandStandardInfo){
        if (!AppUtils.isStringEmpty(brandStandardInfo.getReviewer_brand_std_comment())){
            rejectedCommentLayout.setVisibility(View.VISIBLE);
            rejectedComment.setText(brandStandardInfo.getReviewer_brand_std_comment());
        }else {
            rejectedCommentLayout.setVisibility(View.GONE);
        }
    }

    private int[] statusQuestionCount(ArrayList<BrandStandardSection> brandStandardSection){
        int totalCount = 0;
        int count = 0;

        //totalCount = brandStandardSection.getQuestions().size();
        for (int i = 0 ; i < brandStandardSection.size() ; i++ ) {
            ArrayList<BrandStandardQuestion> brandStandardQuestion = brandStandardSection.get(i).getQuestions();
            for (int j = 0; j < brandStandardQuestion.size(); j++) {

                if (brandStandardQuestion.get(j).getAudit_option_id().size() != 0
                        || brandStandardQuestion.get(j).getAudit_answer_na() == 1
                        || !AppUtils.isStringEmpty(brandStandardQuestion.get(j).getAudit_answer())) {
                    count += 1;
                }
                totalCount += 1;
            }

            ArrayList<BrandStandardSubSection> brandStandardSubSections = brandStandardSection.get(i).getSub_sections();

            for (int k = 0; k < brandStandardSubSections.size(); k++) {
                ArrayList<BrandStandardQuestion> brandStandardSubQuestion = brandStandardSubSections.get(k).getQuestions();
                for (int j = 0; j < brandStandardSubQuestion.size(); j++) {
                    if (brandStandardSubQuestion.get(j).getAudit_option_id().size() != 0
                            || brandStandardSubQuestion.get(j).getAudit_answer_na() == 1
                            || !AppUtils.isStringEmpty(brandStandardSubQuestion.get(j).getAudit_comment())) {
                        count += 1;
                    }
                    totalCount += 1;
                }
            }
        }

        return new int[]{count, totalCount};

    }

    @Override
    public void onItemClick(ArrayList<BrandStandardSection> brandStandardSections, int fileCount, int pos) {
        // Toast.makeText(this," Internal Audit",Toast.LENGTH_SHORT).show();

        Intent startAudit = new Intent(context, BrandStandardAuditActivity.class);
        startAudit.putParcelableArrayListExtra("sectionObject", brandStandardSections);
        startAudit.putExtra("position", pos);
        startAudit.putExtra("editable", editable);
        startAudit.putExtra("auditId", auditId);
        startAudit.putExtra("auditDate", auditDate);
        startAudit.putExtra("status", status);
        startAudit.putExtra("fileCount", ""+fileCount);
        startActivityForResult(startAudit, FillQuestionRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FillQuestionRequest && resultCode == Activity.RESULT_OK){
            //answerSavedDialog();
            //AppUtils.toast(SubSectionsActivity.this, "Answer Saved");
        }
    }


    private boolean validateSubmitQuestion(ArrayList<BrandStandardSection> brandStandardSection){
        boolean validate = true;
        int count = 0;
        ArrayList<BrandStandardQuestion> brandStandardQuestionsSubmissions = new ArrayList<>();


        //totalCount = brandStandardSection.getQuestions().size();
        for (int i = 0 ; i < brandStandardSection.size() ; i++ ) {
            ArrayList<BrandStandardQuestion> brandStandardQuestion = brandStandardSection.get(i).getQuestions();
            count = 0;
            for (int j = 0; j < brandStandardQuestion.size(); j++) {
                count += 1;
                brandStandardQuestionsSubmissions.add(brandStandardQuestion.get(j));
                if (brandStandardQuestion.get(j).getQuestion_type().equals("textarea")|| brandStandardQuestion.get(j).getQuestion_type().equals("text")){
                    if (AppUtils.isStringEmpty(brandStandardQuestion.get(j).getAudit_answer())
                            && brandStandardQuestion.get(j).getAudit_answer_na() == 0) {
                        AppUtils.toastDisplayForLong(SubSectionsActivity.this, "You have not answered " +
                                "question no " + count + " in " + brandStandardSection.get(i).getSection_group_title()
                                + " of section " + brandStandardSection.get(i).getSection_title());
                        return false;
                    }
                }else {
                    if (brandStandardQuestion.get(j).getAudit_option_id().size() == 0
                            && brandStandardQuestion.get(j).getAudit_answer_na() == 0) {
                        AppUtils.toastDisplayForLong(SubSectionsActivity.this, "You have not answered " +
                                "question no " + count + " in " + brandStandardSection.get(i).getSection_group_title()
                                + " of section " + brandStandardSection.get(i).getSection_title());
                        return false;
                    }
                }
            }

            ArrayList<BrandStandardSubSection> brandStandardSubSections = brandStandardSection.get(i).getSub_sections();
            try {
                for (int k = 0; k < brandStandardSubSections.size(); k++)
                {
                    ArrayList<BrandStandardQuestion> brandStandardSubQuestion = brandStandardSubSections.get(k).getQuestions();
                    for (int j = 0; j < brandStandardSubQuestion.size(); j++) {
                        brandStandardQuestionsSubmissions.add(brandStandardSubQuestion.get(j));
                        count += 1;
                        if (brandStandardQuestion.size()>0 && (brandStandardSubQuestion.get(j).getQuestion_type().equalsIgnoreCase("textarea") || brandStandardQuestion.get(j).getQuestion_type().equalsIgnoreCase("text")))
                        {

                            if (AppUtils.isStringEmpty(brandStandardSubQuestion.get(j).getAudit_answer()) && brandStandardSubQuestion.get(j).getAudit_answer_na() == 0) {
                                AppUtils.toastDisplayForLong(SubSectionsActivity.this, "You have not answered " + "question no " + count + " in " + brandStandardSection.get(i).getSection_group_title() + " of section " + brandStandardSection.get(i).getSection_title());
                                return false;
                            }
                        } else {
                            if (brandStandardSubQuestion.size()>0 &&(brandStandardSubQuestion.get(j).getAudit_option_id().size() == 0 && brandStandardSubQuestion.get(j).getAudit_answer_na() == 0))
                            {
                                AppUtils.toastDisplayForLong(SubSectionsActivity.this, "You have not answered " +
                                        "question no " + count + " in " + brandStandardSection.get(i).getSection_group_title()
                                        + " of section " + brandStandardSection.get(i).getSection_title());
                                return false;
                            }
                        }
                    }
                }
            }
            catch (Exception e){e.printStackTrace();}

        }
        answerArray = AppUtils.getQuestionsArray (brandStandardQuestionsSubmissions);
        return validate;
    }



    private void submitBrandStandardQuestion()
    {
        mSpinKitView.setVisibility(View.VISIBLE);
        JSONObject object = BSSaveSubmitJsonRequest.createInput(auditId, auditDate, "0", answerArray);
        AppLogger.e(TAG, "" + object);
        Response.Listener stringListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AppLogger.e(TAG, "BSResponse: " + response);
                try {
                    if (!response.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        //AppUtils.toast((BaseActivity) context, response.getString(ApiResponseKeys.RES_KEY_MESSAGE));
                        Toast.makeText(context, "Answer Submitted", Toast.LENGTH_SHORT).show();
                        status = "" + response.getJSONObject("data").getInt("brand_std_status");
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
                mSpinKitView.setVisibility(View.GONE);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSpinKitView.setVisibility(View.GONE);
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        String message = obj.getString("message");
                        AppLogger.e("Error: ", "" + obj);
                        AppUtils.toast((BaseActivity) context, message);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };

        BSSaveSubmitJsonRequest bsSaveSubmitJsonRequest = new BSSaveSubmitJsonRequest(AppPreferences.INSTANCE.getAccessToken(this), NetworkURL.BRANDSTANDARD, object, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(bsSaveSubmitJsonRequest);

    }

    public void saveBrandStandardQuestionForNA(JSONArray jsonArray) {
        mSpinKitView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(auditDate))
            auditDate=AppUtils.getAuditDateCurrent();
        JSONObject object = BSSaveSubmitJsonRequest.createInput(auditId, auditDate, "1", jsonArray);
        AppLogger.e(TAG, "" + object);
        Response.Listener stringListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AppLogger.e(TAG, "BSResponse: " + response);
                try {
                    if (!response.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, response.getString(AppConstant.RES_KEY_MESSAGE));
                        setBrandStandardQuestion();    // for updating score and refresing adapter
                    }
                    else if (response.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, response.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                mSpinKitView.setVisibility(View.GONE);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSpinKitView.setVisibility(View.GONE);
                AppUtils.toast((BaseActivity) context, getString(R.string.oops));
            }
        };
        BSSaveSubmitJsonRequest bsSaveSubmitJsonRequest = new BSSaveSubmitJsonRequest(AppPreferences.INSTANCE.getAccessToken(context), NetworkURL.BRANDSTANDARD, object, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(bsSaveSubmitJsonRequest);

    }

    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {

    }
}
