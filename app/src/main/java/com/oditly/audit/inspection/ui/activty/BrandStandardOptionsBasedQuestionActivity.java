package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.BrandStandardOptionBasedQuestionsAdapter;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestion;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestionsOption;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardRefrence;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkServiceJSON;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class BrandStandardOptionsBasedQuestionActivity extends BaseActivity implements View.OnClickListener, BrandStandardOptionBasedQuestionsAdapter.CustomItemClickListener,INetworkEvent {

    RecyclerView questionListRecyclerView;
    Button bsSaveBtn;
    TextView mTitleTV;
    private RelativeLayout mProgressRL;
    ImageView mHeaderDescriptionIV;
    public LayoutInflater inflater;

    private static final String TAG = BrandStandardOptionsBasedQuestionActivity.class.getSimpleName();
    private ArrayList<BrandStandardQuestion> mBrandStandardSubQuestList;
    private  Context context;
    private BrandStandardOptionBasedQuestionsAdapter mAdapter;
    private int itemClickedPos=0;
    private String mAuditId="";
    private String mSectionGroupId="";
    private String mSectionId="";
    private int isGalleryDisasble=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_standard_optionbased_question);
        ButterKnife.bind(BrandStandardOptionsBasedQuestionActivity.this);
        inflater = getLayoutInflater();
        context = this;
        initView();
        initVar();
    }


    @Override
    protected void initView() {
        super.initView();
        mTitleTV = findViewById(R.id.tv_header_title);
        mProgressRL=findViewById(R.id.ll_parent_progress);
        questionListRecyclerView = findViewById(R.id.rv_bs_question);
        findViewById(R.id.iv_header_left).setOnClickListener(this);
        bsSaveBtn = findViewById(R.id.bs_save_btn);
        mTitleTV.setSelected(true);  // for morque
        mHeaderDescriptionIV = findViewById(R.id.iv_header_right);
        bsSaveBtn.setOnClickListener(this);


        mHeaderDescriptionIV.setVisibility(View.VISIBLE);
        mHeaderDescriptionIV.setImageResource(R.drawable.ic_info);
        mHeaderDescriptionIV.setOnClickListener(this);
        mTitleTV.setSelected(true);  // for morque


    }

    @Override
    protected void initVar() {
        super.initVar();
        Intent intent= getIntent();
        mAuditId=intent.getStringExtra(AppConstant.AUDIT_ID);
        mSectionGroupId=intent.getStringExtra(AppConstant.SECTION_GROUPID);
        mSectionId=intent.getStringExtra(AppConstant.SECTION_ID);
        isGalleryDisasble= intent.getIntExtra(AppConstant.GALLERY_DISABLE,1);

        mBrandStandardSubQuestList=((OditlyApplication)getApplicationContext()).getmSubQuestionForOptions();
        mAdapter = new BrandStandardOptionBasedQuestionsAdapter(this, mBrandStandardSubQuestList, BrandStandardOptionsBasedQuestionActivity.this);
        questionListRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == AppConstant.QuestionAttachmentRequest && resultCode == Activity.RESULT_OK) {
                String attachmentCount = data.getStringExtra("attachmentCount");
                this.mBrandStandardSubQuestList.get(itemClickedPos).setmImageList(((OditlyApplication) getApplicationContext()).getmAttachImageList());
                mAdapter.setattachmentCount(Integer.parseInt(attachmentCount), this.itemClickedPos);
                AppLogger.e("AttachmentException","Imageattached");
            }
            else if (requestCode == 1021 && resultCode == Activity.RESULT_OK) {
                this.mAdapter.setActionCreatedFlag(this.itemClickedPos);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppLogger.e("AttachmentException", e.getMessage());
            AppUtils.showHeaderDescription(context, e.getMessage());
        }

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_showhow:
                BrandStandardRefrence bsRefrence =(BrandStandardRefrence) view.getTag();
                if (bsRefrence!=null)
                {
                    if (bsRefrence.getFile_type().contains("image"))
                        ShowHowImageActivity.start(this,bsRefrence.getFile_url(),"");
                    else  if (bsRefrence.getFile_type().contains("audio"))
                        AudioPlayerActivity.start(this, bsRefrence.getFile_url());
                    else  if (bsRefrence.getFile_type().contains("video"))
                        ExoVideoPlayer.start(this, bsRefrence.getFile_url(),"");
                    else
                        ShowHowWebViewActivity.start(this,bsRefrence.getFile_url(),"");
                }
                break;
            case R.id.bs_save_btn:
                AppUtils.hideKeyboard(context, view);
                if (saveSectionOrPagewiseData())
                    AppUtils.toast(this,getString(R.string.text_answer_hasbeen_save));
                break;

            case R.id.iv_header_left:
                onBackPressed();
                break;
            case R.id.iv_header_right:
                // AppDialogs.brandstandardTitleMessageDialog(this, sectionTitle,mLocation,mChecklist);
                break;
            case R.id.ll_actioncreate:
                BrandStandardQuestion bsQuestion = (BrandStandardQuestion) view.getTag();
                if (bsQuestion!=null && bsQuestion.isCan_create_action_plan()) {
                    this.itemClickedPos = bsQuestion.getmClickPosition();
                    Intent actionPlan = new Intent(this.context, ActionCreateActivity.class);
                    actionPlan.putExtra("auditid", mAuditId);
                    actionPlan.putExtra(AppConstant.SECTION_GROUPID, mSectionGroupId);
                    actionPlan.putExtra(AppConstant.SECTION_ID, mSectionId);
                    actionPlan.putExtra(AppConstant.QUESTION_ID, "" + bsQuestion.getQuestion_id());
                    actionPlan.putExtra(AppConstant.FROMWHERE, "Audit");
                    startActivityForResult(actionPlan, 1021);
                }
                else
                    AppUtils.toast(this, getString(R.string.text_actionplan_has_been_created_forthis_question));


        }
    }


    private boolean saveSectionOrPagewiseData()
    {
        if (validateCommentOfQuestion())
            return  true;
        else
            return false;
    }

    @Override
    public void onItemClick(int questionNo, BrandStandardOptionBasedQuestionsAdapter brandStandardAuditAdapter, int bsQuestionId, String attachtype, int position) {
        itemClickedPos = questionNo;
        Intent addAttachment = new Intent(context, AddAttachmentActivity.class);
        addAttachment.putExtra("auditId", mAuditId);
        addAttachment.putExtra("sectionGroupId", mSectionGroupId);
        addAttachment.putExtra("sectionId", mSectionId);
        addAttachment.putExtra("questionId", ""+bsQuestionId);
        addAttachment.putExtra("attachType", attachtype);
        addAttachment.putExtra(AppConstant.GALLERY_DISABLE, isGalleryDisasble);
        startActivityForResult(addAttachment, AppConstant.QuestionAttachmentRequest);
    }
    private boolean validateCommentOfQuestion()
    {
        boolean validate = true;
        int count = 0;

        for (int i = 0; i < mBrandStandardSubQuestList.size(); i++) {
            BrandStandardQuestion question = mBrandStandardSubQuestList.get(i);
            count += 1;
            int mMediaCount=question.getMedia_count(),mCommentCount=question.getHas_comment(),mActionPlanRequred=0;
            for (int k = 0; k < question.getOptions().size(); k++) {
                BrandStandardQuestionsOption option = question.getOptions().get(k);
                if (question.getAudit_option_id() != null && question.getAudit_option_id().contains(new Integer(option.getOption_id()))) {
                    if (question.getQuestion_type().equalsIgnoreCase("checkbox"))
                    {
                        if (mActionPlanRequred==0)
                            mActionPlanRequred=option.getAction_plan_required();
                        if (mMediaCount<option.getMedia_count())
                            mMediaCount = option.getMedia_count();
                        if (mCommentCount<option.getCommentCount())
                            mCommentCount = option.getCommentCount();
                    }
                    else {
                        mMediaCount = option.getMedia_count();
                        mCommentCount = option.getCommentCount();
                        mActionPlanRequred=option.getAction_plan_required();
                        break;
                    }

                }
            }

            if (question.getQuestion_type().equalsIgnoreCase("textarea") || question.getQuestion_type().equalsIgnoreCase("text") || question.getQuestion_type().equalsIgnoreCase("number") || question.getQuestion_type().equalsIgnoreCase("datetime") || question.getQuestion_type().equalsIgnoreCase("date") || question.getQuestion_type().equalsIgnoreCase("slider") || question.getQuestion_type().equalsIgnoreCase("temperature") || question.getQuestion_type().equalsIgnoreCase("measurement") || question.getQuestion_type().equalsIgnoreCase("target"))
            {
                if ((AppUtils.isStringEmpty(question.getAudit_answer()) || question.getAudit_answer().equalsIgnoreCase("0")) && question.getAudit_answer_na() == 0 && question.getIs_required()==1) {
                    String message=getString(R.string.text_youhavenot_answer_question);
                    AppUtils.toastDisplayForLong(this,message.replace("CCC",""+count));
                    return false;
                }
            }
            else {
                if (question.getIs_required() == 1 && (question.getAudit_option_id() == null || question.getAudit_option_id().size() == 0) && !question.getQuestion_type().equalsIgnoreCase("media")) {
                    String message=getString(R.string.text_youhavenot_answer_question);
                    AppUtils.toastDisplayForLong(BrandStandardOptionsBasedQuestionActivity.this, message.replace("CCC",""+count));
                    return false;
                }
            }



            if (mMediaCount > 0 && question.getAudit_question_file_cnt() < mMediaCount) {
               // String message="Please submit the required " + mMediaCount + " image(s) for question no. " + count;
                String message=getString(R.string.text_submit_requredmedia_count).replace("MMM",""+mMediaCount).replace("CCC",""+count);
                AppUtils.toastDisplayForLong(BrandStandardOptionsBasedQuestionActivity.this, message);
                return false;
            }
            if (mCommentCount> 0 && mCommentCount > question.getAudit_comment().length()) {
              //  String message = "Please enter the  minimum required " + mCommentCount + " characters comment for question no. " + count;
                String message=getString(R.string.text_enter_requredcomment_count).replace("XXX",""+mCommentCount).replace("CCC",""+count);
                AppUtils.toastDisplayForLong(BrandStandardOptionsBasedQuestionActivity.this, message);
                return false;
            }
            if(mActionPlanRequred>0 && question.getAction_plan()==null)
            {
                String message = getString(R.string.text_create_actionplanfor_question)+" " + count;
                AppUtils.toastDisplayForLong(this,message);
                return false;
            }
        }


        return validate;
    }

    public void saveSingleBrandStandardQuestionEveryClick(BrandStandardQuestion bsQuestion)
    {
        if (NetworkStatus.isNetworkConnected(this))
        {
            try {
                itemClickedPos=bsQuestion.getmClickPosition();
                if (bsQuestion.getQuestion_type().equalsIgnoreCase(AppConstant.QUESTION_TEXTAREA) || bsQuestion.getQuestion_type().equalsIgnoreCase(AppConstant.QUESTION_TEXT) || bsQuestion.getQuestion_type().equalsIgnoreCase(AppConstant.QUESTION_NUMBER) || bsQuestion.getQuestion_type().equalsIgnoreCase(AppConstant.QUESTION_MEASUREMENT) || bsQuestion.getQuestion_type().equalsIgnoreCase(AppConstant.QUESTION_TARGET) || bsQuestion.getQuestion_type().equalsIgnoreCase(AppConstant.QUESTION_TEMPRATURE) )
                    this.mAdapter.updatehParticularPosition(itemClickedPos);

                JSONObject object = new JSONObject();
                object.put("audit_id", mAuditId);
                object.put("question_id", bsQuestion.getQuestion_id());
                object.put("audit_answer", bsQuestion.getAudit_answer());
                object.put("audit_option_id", new JSONArray(bsQuestion.getAudit_option_id()));
                object.put("audit_comment", bsQuestion.getAudit_comment());
                Log.e("JSON OBJECT QUESTION==> ",""+object.toString());
                NetworkServiceJSON networkService = new NetworkServiceJSON(NetworkURL.BRANDSTANDARD_QUESTIONWISE_ANSWER, NetworkConstant.METHOD_POST, this, this);
                networkService.call(object);
            }
            catch (Exception e) {e.printStackTrace();}
        } else
        {
            AppUtils.toast(this, getString(R.string.internet_error));
        }
    }

    @Override
    public void onBackPressed()
    {
        if (validateCommentOfQuestion())
            finish();
    }

    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {
       Log.e("DATA SAVE ==> ",""+service+" || "+response);
       // this.mAdapter.updatehParticularPosition(itemClickedPos);
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {

    }
}