package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.BrandStandardAuditAdapter;
import com.oditly.audit.inspection.adapter.BrandStandardAuditAdapterPagingnation;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestion;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardRefrence;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSection;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSubSection;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkServiceJSON;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.BSSaveSubmitJsonRequest;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandStandardAuditActivityPagingnation extends BaseActivity implements View.OnClickListener, BrandStandardAuditAdapterPagingnation.CustomItemClickListener, INetworkEvent {

    @BindView(R.id.rv_bs_question)
    RecyclerView questionListRecyclerView;

    @BindView(R.id.bs_save_btn)
    Button bsSaveBtn;
    @BindView(R.id.bs_add_file_btn)
    Button fileBtn;

    @BindView(R.id.tv_header_title)
    TextView mTitleTV;
    @BindView(R.id.ll_parent_progress)
    private RelativeLayout mProgressRL;


    private static final String TAG = BrandStandardAuditActivityPagingnation.class.getSimpleName();
    private List<BrandStandardQuestion> mBrandStandardListCurrent;
    private  Context context;
    private String auditId = "",auditDate = "",sectionGroupId = "",sectionId = "",sectionTitle = "",mLocation = "",mChecklist = "",fileCount = "";
    public LayoutInflater inflater;
    private ArrayList<BrandStandardSection> brandStandardSectionArrayList = new ArrayList<>();
    private   BrandStandardSection brandStandardSection;
    private  BrandStandardAuditAdapterPagingnation sectionTabAdapter;
    private static final int AttachmentRequest = 120;
    private static final int QuestionAttachmentRequest = 130;
    private ArrayList<BrandStandardAuditAdapter> bsSubSectionAuditAdaptersList;
    private int  itemClickedPos = 0,currentSectionPosition = 0;
    public int questionCount = 0;
    private BrandStandardAuditAdapterPagingnation currentBrandStandardAuditAdapter;
    public static boolean isAnswerCliked=false;
    private boolean isSaveButtonClick=false,isBackButtonClick=false;

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.hideKeyboard(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_standard_audit_pagignation);
        ButterKnife.bind(BrandStandardAuditActivityPagingnation.this);
        inflater = getLayoutInflater();
        context = this;
        initView();
        initVar();
    }


    @Override
    protected void initView() {
        super.initView();
        AppLogger.e(TAG, ":::: ON CreateCall BRAND STANDARD");

        mTitleTV = findViewById(R.id.tv_header_title);
        mProgressRL=findViewById(R.id.ll_parent_progress);
        questionListRecyclerView = findViewById(R.id.rv_bs_question);
        findViewById(R.id.iv_header_left).setOnClickListener(this);
        fileBtn = findViewById(R.id.bs_add_file_btn);
        bsSaveBtn = findViewById(R.id.bs_save_btn);
        mTitleTV.setSelected(true);  // for morque

        bsSaveBtn.setOnClickListener(this);
        fileBtn.setOnClickListener(this);


    }

    @Override
    protected void initVar() {
        super.initVar();

        Intent intent= getIntent();
        auditId =intent.getStringExtra("auditId");
        auditDate = intent.getStringExtra("auditDate");
        mLocation = intent.getStringExtra(AppConstant.LOCATION_NAME);
        mChecklist = intent.getStringExtra(AppConstant.AUDIT_CHECKLIST);

        AppPreferences.INSTANCE.initAppPreferences(this);
        brandStandardSectionArrayList = new ArrayList<>();
     //   brandStandardSectionArrayList = getIntent().getParcelableArrayListExtra("sectionObject");
         brandStandardSectionArrayList = ((OditlyApplication)getApplication()).getmBrandStandardSectionList();
        currentSectionPosition = getIntent().getIntExtra("position", 0);
        brandStandardSection = brandStandardSectionArrayList.get(currentSectionPosition);
        mBrandStandardListCurrent=new ArrayList<>();

        loadData();
    }


    private void loadData()
    {
        sectionTitle = brandStandardSection.getSection_title();
        bsSubSectionAuditAdaptersList = new ArrayList<>();
        mTitleTV.setText(""+AppUtils.capitalizeHeading(sectionTitle.toLowerCase()));
        questionCount = 0;
        sectionGroupId = "" + brandStandardSection.getSection_group_id();
        sectionId = "" + brandStandardSection.getSection_id();
        fileCount = "" + brandStandardSection.getAudit_section_file_cnt();
        fileBtn.setText("+"+getString(R.string.text_photo)+"     "+fileCount);

        setLocalJSON(brandStandardSection);

    }


    @Override
    public void onItemClick(int questionNo, BrandStandardAuditAdapterPagingnation brandStandardAuditAdapter, int bsQuestionId, String attachtype, int position) {
        itemClickedPos = position;
        questionCount = questionNo;
        currentBrandStandardAuditAdapter = brandStandardAuditAdapter;
        mBrandStandardListCurrent=currentBrandStandardAuditAdapter.getArrayList();
        Intent addAttachment = new Intent(context, AddAttachmentActivity.class);
        addAttachment.putExtra("auditId", auditId);
        addAttachment.putExtra("sectionGroupId", sectionGroupId);
        addAttachment.putExtra("sectionId", sectionId);
        addAttachment.putExtra("questionId", "" + bsQuestionId);
        addAttachment.putExtra("attachType", attachtype);
        startActivityForResult(addAttachment, QuestionAttachmentRequest);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == AttachmentRequest && resultCode == Activity.RESULT_OK)
            {
                String attachmentCount = data.getStringExtra("attachmentCount");
                fileBtn.setText("+"+getString(R.string.text_photo)+"     "+attachmentCount);
            }
           else if (requestCode == QuestionAttachmentRequest && resultCode == Activity.RESULT_OK)
            {
                BrandStandardAuditActivityPagingnation.isAnswerCliked=true;
                String attachmentCount = data.getStringExtra("attachmentCount");
                List<Uri> tempList=new ArrayList<>();
                if(mBrandStandardListCurrent!=null && mBrandStandardListCurrent.get(itemClickedPos).getmImageList()!=null && mBrandStandardListCurrent.get(itemClickedPos).getmImageList().size()>0)
                    tempList.addAll(mBrandStandardListCurrent.get(itemClickedPos).getmImageList());
                tempList.addAll(((OditlyApplication)getApplicationContext()).getmAttachImageList());
                mBrandStandardListCurrent.get(itemClickedPos).setmImageList(tempList);

                if(currentBrandStandardAuditAdapter!=null && attachmentCount!=null)
                    currentBrandStandardAuditAdapter.setattachmentCount(Integer.parseInt(attachmentCount), itemClickedPos);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppLogger.e("AttachmentException", e.getMessage());
            AppUtils.showHeaderDescription(context, e.getMessage());
        }

    }
    public void setQuestionList(ArrayList<BrandStandardQuestion> questionArrayList) {
        sectionTabAdapter = new BrandStandardAuditAdapterPagingnation(context, questionArrayList, BrandStandardAuditActivityPagingnation.this);
        questionListRecyclerView.setAdapter(sectionTabAdapter);
        questionListRecyclerView.setHasFixedSize(true);
        questionListRecyclerView.setItemViewCacheSize(20);
        questionListRecyclerView.setDrawingCacheEnabled(true);
        questionListRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    public void setSubSectionQuestionList(ArrayList<BrandStandardSubSection> subSectionArrayList) {

    }

    private void mergJSONArray(JSONArray arrayJson)
    {
        try {
            JSONArray sourceArray = new JSONArray(arrayJson);
            JSONArray destinationArray = new JSONArray(arrayJson);
            for (int i = 0; i < sourceArray.length(); i++) {
                destinationArray.put(sourceArray.getJSONObject(i));
            }

            String s3 = destinationArray.toString();
        }
        catch (Exception e){e.printStackTrace();}
    }
    public void saveBrandStandardQuestion()
    {
        if (NetworkStatus.isNetworkConnected(this))
        {
            Log.e(";; answer array  :: ",""+getQuestionsArray());
            if(isSaveButtonClick)
                mProgressRL.setVisibility(View.VISIBLE);
            JSONObject object = BSSaveSubmitJsonRequest.createInput(auditId, auditDate, "1", getQuestionsArray());
            NetworkServiceJSON networkService = new NetworkServiceJSON(NetworkURL.BRANDSTANDARD, NetworkConstant.METHOD_POST, this, this);
            networkService.call(object);
        } else
        {
            AppUtils.toast(this, getString(R.string.internet_error));
        }
    }
    private JSONArray  getQuestionsArray() {
        JSONArray jsonArray = new JSONArray();
        ArrayList<BrandStandardQuestion> brandStandardQuestions = sectionTabAdapter.getArrayList();
        ArrayList<BrandStandardQuestion> brandStandardsubsectionQuestions = new ArrayList<>();
        for (int i = 0; i < bsSubSectionAuditAdaptersList.size(); i++) {
            brandStandardsubsectionQuestions.addAll(bsSubSectionAuditAdaptersList.get(i).getArrayList());
        }
        for (int i = 0; i < brandStandardQuestions.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("question_id", brandStandardQuestions.get(i).getQuestion_id());
                jsonObject.put("audit_answer_na", brandStandardQuestions.get(i).getAudit_answer_na());
                jsonObject.put("audit_comment", brandStandardQuestions.get(i).getAudit_comment());
                jsonObject.put("audit_option_id", new JSONArray(brandStandardQuestions.get(i).getAudit_option_id()));
                jsonObject.put("audit_answer", brandStandardQuestions.get(i).getAudit_answer());
                jsonArray.put(jsonObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < brandStandardsubsectionQuestions.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("question_id", brandStandardsubsectionQuestions.get(i).getQuestion_id());
                jsonObject.put("audit_answer_na", brandStandardsubsectionQuestions.get(i).getAudit_answer_na());
                jsonObject.put("audit_comment", brandStandardsubsectionQuestions.get(i).getAudit_comment());
                jsonObject.put("audit_option_id", new JSONArray(brandStandardsubsectionQuestions.get(i).getAudit_option_id()));
                jsonObject.put("audit_answer", brandStandardsubsectionQuestions.get(i).getAudit_answer());
                jsonArray.put(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AppLogger.e("BrandStandardJson", "" + jsonArray);
        return jsonArray;
    }

    private void setLocalJSON(BrandStandardSection brandStandardSection) {
        try{
            setQuestionList(brandStandardSection.getQuestions());
            setSubSectionQuestionList(brandStandardSection.getSub_sections());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean validateCommentOfQuestion() {
        boolean validate = true;
        int count = 0;
        ArrayList<BrandStandardQuestion> brandStandardQuestions = sectionTabAdapter.getArrayList();
        ArrayList<BrandStandardQuestion> brandStandardSubsectionQuestions = new ArrayList<>();
        for (int i = 0; i < bsSubSectionAuditAdaptersList.size(); i++) {
            brandStandardSubsectionQuestions.addAll(bsSubSectionAuditAdaptersList.get(i).getArrayList());
        }
        for (int i = 0; i < brandStandardQuestions.size(); i++) {
            BrandStandardQuestion question = brandStandardQuestions.get(i);
            count += 1;
            if (question.getAudit_answer_na() == 0 && ((question.getAudit_option_id()!=null && question.getAudit_option_id().size()>0) || !TextUtils.isEmpty(question.getAudit_answer())))
            {
                if (question.getHas_comment() > 0 && (AppUtils.isStringEmpty(question.getAudit_comment()) || question.getAudit_comment().length() < question.getHas_comment())) {
                    validate = false;
                    AppUtils.toastDisplayForLong(BrandStandardAuditActivityPagingnation.this, "Please enter the  minimum required " + question.getHas_comment() + " characters comment for question no. " + count);
                    return false;
                }
            }
        }

        for (int i = 0; i < brandStandardSubsectionQuestions.size(); i++) {
            BrandStandardQuestion subQuestion = brandStandardSubsectionQuestions.get(i);
            count += 1;
            if (subQuestion.getAudit_answer_na() == 0 && ((subQuestion.getAudit_option_id()!=null && subQuestion.getAudit_option_id().size()>0) || !TextUtils.isEmpty(subQuestion.getAudit_answer()))) {
                if (subQuestion.getHas_comment() > 0 && (AppUtils.isStringEmpty(subQuestion.getAudit_comment()) || subQuestion.getAudit_comment().length() < subQuestion.getHas_comment())) {
                    validate = false;
                    AppUtils.toastDisplayForLong(BrandStandardAuditActivityPagingnation.this, "Please enter the minimum required " + subQuestion.getHas_comment() + " characters comment for question no." + count);
                    return false;
                }
            }
        }
        return validate;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void onBackPressed() {
        if(isAnswerCliked)
        {
            isBackButtonClick=true; // This is for saving last answer data and hold th page
            isSaveButtonClick=true; // This is only for showing progressBar
            saveSectionOrPagewiseData();
        }
        else
            finish();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_showhow:
                BrandStandardRefrence bsRefrence =(BrandStandardRefrence) view.getTag();
                if (bsRefrence!=null)
                {
                    if (bsRefrence.getFile_type().contains("image"))
                        ShowHowImageActivity.start(this,bsRefrence.getFile_url());
                    else  if (bsRefrence.getFile_type().contains("audio"))
                        AudioPlayerActivity.start(this, bsRefrence.getFile_url());
                    else  if (bsRefrence.getFile_type().contains("video"))
                        ExoVideoPlayer.start(this, bsRefrence.getFile_url());
                    else
                    {
                        ShowHowWebViewActivity.start(this,bsRefrence.getFile_url());
                       // "file_type": "application/pdf",presentation  pptx",
                      //  Toast.makeText(this,"Coming Soon.. "+bsRefrence.getFile_ext(),Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.bs_save_btn:
                AppUtils.hideKeyboard(context, view);
                isSaveButtonClick=true;
                saveSectionOrPagewiseData();
                break;
            case R.id.bs_add_file_btn:
                Intent addAttachment = new Intent(context, AddAttachmentActivity.class);
                addAttachment.putExtra("auditId", auditId);
                addAttachment.putExtra("sectionGroupId", sectionGroupId);
                addAttachment.putExtra("sectionId", sectionId);
                addAttachment.putExtra("questionId", "");
                addAttachment.putExtra("attachType", "bsSection");
                startActivityForResult(addAttachment, AttachmentRequest);
                break;
            case R.id.iv_header_left:
                onBackPressed();
                break;
            case R.id.iv_header_right:
                AppDialogs.brandstandardTitleMessageDialog(this, sectionTitle,mLocation,mChecklist);
                break;

        }
    }




    private boolean saveSectionOrPagewiseData()
    {
        if (AppUtils.isStringEmpty(auditDate))
            auditDate=AppUtils.getAuditDateCurrent();
        if (validateCommentOfQuestion())
            saveBrandStandardQuestion();
        else
            return false;

        return  true;
    }




    @Override
    public void onNetworkCallInitiated(String service) { }
    @Override
    public void onNetworkCallCompleted(String type, String service, String responseStr) {
        isAnswerCliked=false; // because question is saved
        AuditSubSectionsActivity.isDataSaved=true;
        AppLogger.e(TAG, "BSResponse: " + responseStr);
        try {
            JSONObject response = new JSONObject(responseStr);
            if (!response.getBoolean(AppConstant.RES_KEY_ERROR))
            {
                AppUtils.toast((BaseActivity) context, response.getString(AppConstant.RES_KEY_MESSAGE));
                if (isBackButtonClick)
                {
                    isBackButtonClick=false; // This is only for showing progressBar
                    finish();
                }
            } else if (response.getBoolean(AppConstant.RES_KEY_ERROR)) {
                AppUtils.toast((BaseActivity) context, response.getString(AppConstant.RES_KEY_MESSAGE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mProgressRL.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        mProgressRL.setVisibility(View.GONE);
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(this, this.getString(R.string.oops));
    }

}
