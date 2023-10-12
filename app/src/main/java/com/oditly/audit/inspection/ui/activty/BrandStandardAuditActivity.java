package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.BrandStandardAuditAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.localDB.bsoffline.BsOffLineDB;
import com.oditly.audit.inspection.localDB.bsoffline.BsOfflineDBImpl;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestion;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestionsOption;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandStandardAuditActivity extends BaseActivity implements View.OnClickListener, BrandStandardAuditAdapter.CustomItemClickListener, INetworkEvent {

    @BindView(R.id.rv_bs_question)
    RecyclerView questionListRecyclerView;
    @BindView(R.id.ll_bs_sub_section_question)
    LinearLayout subSectionQuestionLayout;
    @BindView(R.id.bs_save_btn)
    Button bsSaveBtn;
    @BindView(R.id.bs_addmedia)
    Button mediaAddBtn;
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.prev_btn)
    Button prevBtn;
    @BindView(R.id.iv_header_right)
    ImageView mHeaderDescriptionIV;
    @BindView(R.id.timertext)
    TextView timerText;
    @BindView(R.id.score_text)
    TextView scoreText;
    @BindView(R.id.tv_header_title)
    TextView mTitleTV;
    @BindView(R.id.ll_parent_progress)
    private RelativeLayout mProgressRL;
    @BindView(R.id.ns_nestedscroolview)
    private NestedScrollView mNestedScrollView;
    private BsOffLineDB mBsOfflineDB;

    private static final String TAG = BrandStandardAuditActivity.class.getSimpleName();
    private List<BrandStandardQuestion> mBrandStandardListCurrent;
    private  Context context;
    private String auditId = "",auditDate = "",sectionGroupId = "",sectionId = "",sectionTitle = "",mLocation = "",mChecklist = "",fileCount = "";
    public LayoutInflater inflater;
    private float totalMarks = 0, marksObtained = 0;
    private ArrayList<BrandStandardSection> brandStandardSectionArrayList = new ArrayList<>();
    private   BrandStandardSection brandStandardSection;
    private  BrandStandardAuditAdapter sectionTabAdapter;
    private static final int AttachmentRequest = 120;
    private static final int QuestionAttachmentRequest = 130;
    private ArrayList<BrandStandardAuditAdapter> bsSubSectionAuditAdaptersList;
    private int  itemClickedPos = 0,currentSectionPosition = 0,Seconds, Minutes;
    public int questionCount = 0;
    private BrandStandardAuditAdapter currentBrandStandardAuditAdapter;
    private long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    private  Handler handler;
    public static boolean isAnswerCliked=false;
    private boolean isSaveButtonClick=false,isBackButtonClick=false;
    private int isGalleryDisasble=1;
    private String mSectionWeightage="";
    private int mNextPreviousClick=0; // 0,1=next,2=prev;
    public boolean isDialogSaveClicked=false;

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.hideKeyboard(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_standard_audit);
        ButterKnife.bind(BrandStandardAuditActivity.this);
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
        mNestedScrollView=findViewById(R.id.ns_nestedscroolview);

        questionListRecyclerView = findViewById(R.id.rv_bs_question);
        subSectionQuestionLayout = findViewById(R.id.ll_bs_sub_section_question);
        mediaAddBtn = findViewById(R.id.bs_addmedia);
        bsSaveBtn = findViewById(R.id.bs_save_btn);
        nextBtn = findViewById(R.id.next_btn);
        prevBtn = findViewById(R.id.prev_btn);
        mHeaderDescriptionIV = findViewById(R.id.iv_header_right);
        timerText = findViewById(R.id.timertext);
        scoreText = findViewById(R.id.score_text);

        bsSaveBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        prevBtn.setOnClickListener(this);
        mediaAddBtn.setOnClickListener(this);
        findViewById(R.id.iv_header_left).setOnClickListener(this);
        mHeaderDescriptionIV.setVisibility(View.VISIBLE);
        mHeaderDescriptionIV.setImageResource(R.drawable.ic_info);
        mHeaderDescriptionIV.setOnClickListener(this);
        mTitleTV.setSelected(true);  // for morque


    }

    @Override
    protected void initVar() {
        super.initVar();

        mBsOfflineDB= BsOfflineDBImpl.getInstance(this);
        Intent intent= getIntent();
        auditId =intent.getStringExtra("auditId");
        auditDate = intent.getStringExtra("auditDate");
        mLocation = intent.getStringExtra(AppConstant.LOCATION_NAME);
        mChecklist = intent.getStringExtra(AppConstant.AUDIT_CHECKLIST);
        TimeBuff  = intent.getIntExtra(AppConstant.AUDIT_TIMER,0)*1000;
        isGalleryDisasble= intent.getIntExtra(AppConstant.GALLERY_DISABLE,1);



        AppPreferences.INSTANCE.initAppPreferences(this);
        handler = new Handler();
        brandStandardSectionArrayList = new ArrayList<>();
        //  brandStandardSectionArrayList = getIntent().getParcelableArrayListExtra("sectionObject");
        AppLogger.e("brandStandardSectionArrayList", " size "+brandStandardSectionArrayList);
        //  brandStandardSectionArrayList = ((OditlyApplication)getApplication()).getmBrandStandardSectionList();
        //  brandStandardSectionArrayList = AuditSubSectionsActivity.brandStandardSectionsStatic;

        String json = mBsOfflineDB.getBrandSectionJSON("bs");
        Type type = new TypeToken<ArrayList<BrandStandardSection>>() {}.getType();
        brandStandardSectionArrayList =  new Gson().fromJson(json, type);

        AppLogger.e("brandStandardSectionArrayList", " size "+brandStandardSectionArrayList);
        currentSectionPosition = getIntent().getIntExtra("position", 0);
        AppLogger.e("Position", " position  "+currentSectionPosition);

        brandStandardSection = brandStandardSectionArrayList.get(currentSectionPosition);
        mBrandStandardListCurrent=new ArrayList<>();

        setPrevNextButton();
        loadData();
    }

    private void loadData()
    {
        mNestedScrollView.scrollTo(0,0); // new added
        sectionTitle = brandStandardSection.getSection_title();
        bsSubSectionAuditAdaptersList = new ArrayList<>();
        mTitleTV.setText(""+AppUtils.capitalizeHeading(sectionTitle.toLowerCase()));
        questionCount = 0;
        sectionGroupId = "" + brandStandardSection.getSection_group_id();
        sectionId = "" + brandStandardSection.getSection_id();
        fileCount = "" + brandStandardSection.getAudit_section_file_cnt();
        mSectionWeightage = brandStandardSection.getSection_weightage();

        setLocalJSON(brandStandardSection);
        mediaAddBtn.setText("+"+getString(R.string.text_photo)+"     "+fileCount);
        removeHandlerCallBck();
        countNA_Answers();
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 1000);
    }


    @Override
    public void onItemClick(int questionNo, BrandStandardAuditAdapter brandStandardAuditAdapter, int bsQuestionId, String attachtype, int position) {
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
        addAttachment.putExtra(AppConstant.GALLERY_DISABLE, isGalleryDisasble);
        startActivityForResult(addAttachment, QuestionAttachmentRequest);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            AppUtils.deleteCache(this); // clear Cache
          //  clearApplicationData();
            if (requestCode == AttachmentRequest && resultCode == Activity.RESULT_OK) {
                try {
                    String attachmentCount = data.getStringExtra("attachmentCount");
                    Button button = this.mediaAddBtn;
                    button.setText("+" + getString(R.string.text_photo) + "     " + attachmentCount);
                } catch (Exception e) {
                    e.printStackTrace();
                    AppLogger.e("AttachmentException", e.getMessage());
                    AppUtils.showHeaderDescription(this.context, e.getMessage());
                }
            } else if (requestCode == QuestionAttachmentRequest && resultCode == Activity.RESULT_OK) {
                BrandStandardAuditActivity.isAnswerCliked=true;
                String attachmentCount2 = data.getStringExtra("attachmentCount");
                List<Uri> tempList = new ArrayList<>();
                if (!(this.mBrandStandardListCurrent == null || this.mBrandStandardListCurrent.get(this.itemClickedPos).getmImageList() == null || this.mBrandStandardListCurrent.get(this.itemClickedPos).getmImageList().size() <= 0)) {
                    tempList.addAll(this.mBrandStandardListCurrent.get(this.itemClickedPos).getmImageList());
                }
                tempList.addAll(((OditlyApplication) getApplicationContext()).getmAttachImageList());
                this.mBrandStandardListCurrent.get(this.itemClickedPos).setmImageList(tempList);
                if (!(this.currentBrandStandardAuditAdapter == null || attachmentCount2 == null)) {
                    this.currentBrandStandardAuditAdapter.setattachmentCount(Integer.parseInt(attachmentCount2), this.itemClickedPos);
                }
            } else if (requestCode == 1021 && resultCode == -1) {
                BrandStandardAuditActivity.isAnswerCliked=true;
                this.currentBrandStandardAuditAdapter.setActionCreatedFlag(this.itemClickedPos);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppLogger.e("AttachmentException", e.getMessage());
            AppUtils.showHeaderDescription(context, "IMAGE ISSSUE "+e.getMessage());
        }

    }
    public void setQuestionList(ArrayList<BrandStandardQuestion> questionArrayList) {
        sectionTabAdapter = new BrandStandardAuditAdapter(context, questionArrayList, BrandStandardAuditActivity.this);
        questionListRecyclerView.setAdapter(sectionTabAdapter);
        questionListRecyclerView.setHasFixedSize(true);
        questionListRecyclerView.setItemViewCacheSize(20);
        questionListRecyclerView.setDrawingCacheEnabled(true);
        questionListRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }
    public void sendToQuestionBasedActivity(ArrayList<BrandStandardQuestion> brandStandardQuestions)
    {
        Intent actionPlan = new Intent(this.context, BrandStandardOptionsBasedQuestionActivity.class);
        actionPlan.putExtra(AppConstant.AUDIT_ID,auditId);
        actionPlan.putExtra(AppConstant.SECTION_GROUPID,sectionGroupId);
        actionPlan.putExtra(AppConstant.SECTION_ID,sectionId);
        actionPlan.putExtra(AppConstant.GALLERY_DISABLE, isGalleryDisasble);
        ((OditlyApplication)context.getApplicationContext()).setmSubQuestionForOptions(brandStandardQuestions);
        startActivityForResult(actionPlan, AppConstant.SUBQUESTION_REQUESTCODE);
    }
    public void setSubSectionQuestionList(ArrayList<BrandStandardSubSection> subSectionArrayList) {
        subSectionQuestionLayout.removeAllViews();
        if (subSectionArrayList != null || subSectionArrayList.size() != 0)
        {
            for (int l = 0; l < subSectionArrayList.size(); l++) {
                BrandStandardSubSection brandStandardSubSection = subSectionArrayList.get(l);
                View view = inflater.inflate(R.layout.brand_standard_audit_subsection, null);
                TextView subSectionTitle = view.findViewById(R.id.tv_bs_sub_section_title);
                RecyclerView subSectionQuestionListRV = view.findViewById(R.id.rv_bs_sub_section_question);
                subSectionTitle.setText(brandStandardSubSection.getSub_section_title());

                BrandStandardAuditAdapter subSectionTabAdapter = new BrandStandardAuditAdapter(context, brandStandardSubSection.getQuestions(), BrandStandardAuditActivity.this);
                subSectionQuestionListRV.setLayoutManager(new LinearLayoutManager(context));
                subSectionQuestionListRV.setAdapter(subSectionTabAdapter);

                bsSubSectionAuditAdaptersList.add(subSectionTabAdapter);
                subSectionQuestionLayout.addView(view);
            }
        }
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
            JSONObject object = BSSaveSubmitJsonRequest.createInputNew(auditId,sectionId,sectionGroupId);
            NetworkServiceJSON networkService = new NetworkServiceJSON(NetworkURL.BRANDSTANDARD_SECTION_SAVE_NEW, NetworkConstant.METHOD_POST, this, this);
            networkService.call(object);
        } else
        {
            AppUtils.toast(this, getString(R.string.internet_error));
        }
    }

    public void handelNextPreviousPopUpYESNoClick()
    {
        if (isBackButtonClick) {
            isBackButtonClick = false; // This is only for showing progressBar
            finish();
        } else if (isDialogSaveClicked) {
            if (mNextPreviousClick == 1)
                setNextButtonSetUP();
            else if (mNextPreviousClick == 2)
                setPreviousButtonSetUP();

            isDialogSaveClicked = false;
            mNextPreviousClick = 0;
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
                jsonObject.put("options", AppUtils.getOptionQuestionArray(brandStandardQuestions.get(i).getOptions(),brandStandardQuestions.get(i).getAudit_option_id()));

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
                jsonObject.put("options", AppUtils.getOptionQuestionArray(brandStandardsubsectionQuestions.get(i).getOptions(),brandStandardsubsectionQuestions.get(i).getAudit_option_id()));

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
        int mMediaCount=0,mCommentCount=0,mActionPlanRequred=0;

        ArrayList<BrandStandardQuestion> brandStandardQuestions = sectionTabAdapter.getArrayList();
        ArrayList<BrandStandardQuestion> brandStandardSubsectionQuestions = new ArrayList<>();
        for (int i = 0; i < bsSubSectionAuditAdaptersList.size(); i++) {
            brandStandardSubsectionQuestions.addAll(bsSubSectionAuditAdaptersList.get(i).getArrayList());
        }
        for (int i = 0; i < brandStandardQuestions.size(); i++) {
            BrandStandardQuestion question = brandStandardQuestions.get(i);
            count += 1;

            mMediaCount=question.getMedia_count();
            mCommentCount=question.getHas_comment();
            mActionPlanRequred=0;
            if (question.getAudit_option_id()!=null && question.getAudit_option_id().size()>0)
            {
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
            }

            if (((question.getAudit_option_id()!=null && question.getAudit_option_id().size()>0) || !TextUtils.isEmpty(question.getAudit_answer()))) {

                if(mActionPlanRequred>0 && question.getAction_plan()==null)
                {
                    String message = getString(R.string.text_create_actionplanfor_question)+" " + count;
                    AppDialogs.messageDialogWithYesNo(BrandStandardAuditActivity.this, message);
                    return false;
                }
                if(mMediaCount>0 && question.getAudit_question_file_cnt() < mMediaCount)
                {
                    // String message = "Please submit the required " + mMediaCount + " image(s) for question no. " + count+ " in section";
                    String message=getString(R.string.text_submit_requredmedia_count).replace("MMM",""+mMediaCount).replace("CCC",""+count);
                    AppDialogs.messageDialogWithYesNo(BrandStandardAuditActivity.this, message);
                    return false;
                }
                if (mCommentCount>0 &&  question.getAudit_comment().length() < mCommentCount)
                {
                    //   String message = "Please enter the  minimum required " + mCommentCount + " characters comment for question no. " + count;
                    String message=getString(R.string.text_enter_requredcomment_count).replace("XXX",""+mCommentCount).replace("CCC",""+count);
                    AppDialogs.messageDialogWithYesNo(BrandStandardAuditActivity.this, message);
                    return false;
                }

            }
        }

        for (int i = 0; i < brandStandardSubsectionQuestions.size(); i++) {
            BrandStandardQuestion question = brandStandardSubsectionQuestions.get(i);
            count += 1;
            mMediaCount=question.getMedia_count();
            mCommentCount=question.getHas_comment();
            mActionPlanRequred=0;
            if (question.getAudit_option_id()!=null && question.getAudit_option_id().size()>0)
            {
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
            }

            if (((question.getAudit_option_id()!=null && question.getAudit_option_id().size()>0) || !TextUtils.isEmpty(question.getAudit_answer()))) {

                if(mActionPlanRequred>0 && question.getAction_plan()==null)
                {
                    String message = getString(R.string.text_create_actionplanfor_question)+" " + count;
                    AppDialogs.messageDialogWithYesNo(BrandStandardAuditActivity.this, message);
                    return false;
                }
                if(mMediaCount>0 && question.getAudit_question_file_cnt() < mMediaCount)
                {
                    String message=getString(R.string.text_submit_requredmedia_count).replace("MMM",""+mMediaCount).replace("CCC",""+count);
                    AppDialogs.messageDialogWithYesNo(BrandStandardAuditActivity.this, message);
                    return false;
                }
                if (mCommentCount>0 &&  question.getAudit_comment().length() < mCommentCount)
                {
                    // String message = "Please enter the  minimum required " + mCommentCount+ " characters comment for question no. " + count;
                    String message=getString(R.string.text_enter_requredcomment_count).replace("XXX",""+mCommentCount).replace("CCC",""+count);
                    AppDialogs.messageDialogWithYesNo(BrandStandardAuditActivity.this, message);

                    return false;
                }

            }
        }
        return validate;
    }




    private void removeHandlerCallBck() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onBackPressed() {
        if(isAnswerCliked)
        {
            isBackButtonClick=true; // This is for saving last answer data and hold th page
            isSaveButtonClick=true; // This is only for showing progressBar
            if(saveSectionOrPagewiseData())
                finish();
        }
        else
            finish();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_showhow:
                BrandStandardRefrence bsRefrence = (BrandStandardRefrence) view.getTag();
                if (bsRefrence != null) {
                    if (bsRefrence.getFile_type().contains("image"))
                        ShowHowImageActivity.start(this, bsRefrence.getFile_url(),"");
                    else if (bsRefrence.getFile_type().contains("audio"))
                        AudioPlayerActivity.start(this, bsRefrence.getFile_url());
                    else if (bsRefrence.getFile_type().contains("video"))
                        ExoVideoPlayer.start(this, bsRefrence.getFile_url(),"");
                    else {
                        ShowHowWebViewActivity.start(this, bsRefrence.getFile_url(),"");
                    }
                }
                break;
            case R.id.ll_actioncreate:
                BrandStandardQuestion bsQuestion = (BrandStandardQuestion) view.getTag();
                if (bsQuestion == null) {
                    return;
                }
                if (bsQuestion.isCan_create_action_plan()) {
                    this.itemClickedPos = bsQuestion.getmClickPosition();
                    this.currentBrandStandardAuditAdapter = bsQuestion.getStandardAuditAdapter();
                    Intent actionPlan = new Intent(this.context, ActionCreateActivity.class);
                    actionPlan.putExtra("auditid", this.auditId);
                    actionPlan.putExtra(AppConstant.SECTION_GROUPID, this.sectionGroupId);
                    actionPlan.putExtra(AppConstant.SECTION_ID, this.sectionId);
                    actionPlan.putExtra(AppConstant.QUESTION_ID, "" + bsQuestion.getQuestion_id());
                    actionPlan.putExtra(AppConstant.FROMWHERE, "Audit");
                    startActivityForResult(actionPlan, 1021);
                }
                else
                    AppUtils.toast(this, getString(R.string.text_actionplan_has_been_created_forthis_question));
                break;
            case R.id.bs_save_btn:
                mNextPreviousClick=0;
                AppUtils.hideKeyboard(context, view);
                if (bsSaveBtn.getText().toString().equalsIgnoreCase(getString(R.string.text_submitgoto)))
                    onBackPressed();  // because we want to auto direct on SUbmit paeg like BackButton
                else {
                    isSaveButtonClick = true;
                    saveSectionOrPagewiseData();
                }

                break;
            case R.id.bs_addmedia:
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
            case R.id.next_btn:
                isSaveButtonClick=true;  //for showing loader
                if(isAnswerCliked)
                {
                    mNextPreviousClick=1;
                    isDialogSaveClicked=true;   // as we are having same behaviour like dialog loader and change page after response;
                  //  saveSectionOrPagewiseData();

                    if (saveSectionOrPagewiseData()) {
                        isAnswerCliked=false;
                        setNextButtonSetUP();
                    }
                }
                else
                    setNextButtonSetUP();

                break;
            case R.id.prev_btn:
                isSaveButtonClick=true;  //for showing loader
                if(isAnswerCliked)
                {
                    mNextPreviousClick=2;
                    isDialogSaveClicked=true;   // as we are having same behaviour like dialog loader and change page after response;
                    if (saveSectionOrPagewiseData()) {
                        isAnswerCliked=false;
                        setPreviousButtonSetUP();
                    }

                }
                else
                    setPreviousButtonSetUP();

                break;
        }
    }

    private void setNextButtonSetUP() {
        if (currentSectionPosition < brandStandardSectionArrayList.size() - 1) {
            brandStandardSectionArrayList.set(currentSectionPosition, brandStandardSection);
            currentSectionPosition++;
            brandStandardSection = brandStandardSectionArrayList.get(currentSectionPosition);
            loadData();

            if (currentSectionPosition == brandStandardSectionArrayList.size() - 1) {
                changeSaveButtonToGoToSubmit(false);
                nextBtn.setText("N/A");
                nextBtn.setEnabled(false);
                prevBtn.setEnabled(true);
                prevBtn.setText("< " +getString(R.string.text_back)+ " (" + (currentSectionPosition ) + "/" + brandStandardSectionArrayList.size() + ")\n" + brandStandardSectionArrayList.get(currentSectionPosition - 1).getSection_title());
            } else {
                changeSaveButtonToGoToSubmit(true);
                nextBtn.setText("(" + (currentSectionPosition + 2) + "/" + brandStandardSectionArrayList.size() + ") Next >\n" + brandStandardSectionArrayList.get(currentSectionPosition + 1).getSection_title());
                prevBtn.setText("< "+getString(R.string.text_back) + " (" + (currentSectionPosition ) + "/" + brandStandardSectionArrayList.size() + ")\n" + brandStandardSectionArrayList.get(currentSectionPosition - 1).getSection_title());
                nextBtn.setEnabled(true);
                prevBtn.setEnabled(true);
            }
        }
    }

    private void setPreviousButtonSetUP() {
        if (currentSectionPosition > 0) {
            brandStandardSectionArrayList.set(currentSectionPosition, brandStandardSection);
            currentSectionPosition--;
            brandStandardSection = brandStandardSectionArrayList.get(currentSectionPosition);
            loadData();
            if (currentSectionPosition == 0) {
                prevBtn.setText("N/A");
                prevBtn.setEnabled(false);
                nextBtn.setText("(" + (currentSectionPosition + 2) + "/" + brandStandardSectionArrayList.size() + ") Next >\n" + brandStandardSectionArrayList.get(currentSectionPosition + 1).getSection_title());
                nextBtn.setEnabled(true);
            } else {
                nextBtn.setText("(" + (currentSectionPosition + 2) + "/" + brandStandardSectionArrayList.size() + ") Next >\n" + brandStandardSectionArrayList.get(currentSectionPosition + 1).getSection_title());
                prevBtn.setText("< "+getString(R.string.text_back)+ " (" + (currentSectionPosition ) + "/" + brandStandardSectionArrayList.size() + ")\n" + brandStandardSectionArrayList.get(currentSectionPosition - 1).getSection_title());
                nextBtn.setEnabled(true);
                prevBtn.setEnabled(true);
            }
        }
    }
    private void changeSaveButtonToGoToSubmit(boolean isSave)
    {
        if(isSave) {
            bsSaveBtn.setText(getString(R.string.text_save));
            mediaAddBtn.setVisibility(View.VISIBLE);
        }
        else {
            bsSaveBtn.setText(getString(R.string.text_submitgoto));
            mediaAddBtn.setVisibility(View.GONE);
        }
    }
    private void setPrevNextButton() {
        if (brandStandardSectionArrayList.size() == 1)
        {
            nextBtn.setVisibility(View.GONE);
            prevBtn.setVisibility(View.GONE);
            changeSaveButtonToGoToSubmit(false);


        } else if (brandStandardSectionArrayList.size() > 1)
        {
            if (currentSectionPosition == 0) {
                changeSaveButtonToGoToSubmit(true);
                prevBtn.setEnabled(false);
                prevBtn.setText("N/A");
                nextBtn.setText("(" + (currentSectionPosition + 2) + "/" + brandStandardSectionArrayList.size() + ")" +getString(R.string.text_next)+ ">\n" + brandStandardSectionArrayList.get(currentSectionPosition + 1).getSection_title());
            } else if (currentSectionPosition == brandStandardSectionArrayList.size() - 1) {
                changeSaveButtonToGoToSubmit(false);
                nextBtn.setEnabled(false);
                nextBtn.setText("N/A");
                prevBtn.setText("< " +getString(R.string.text_back)+ " (" + (currentSectionPosition ) + "/" + brandStandardSectionArrayList.size() + ")\n" + brandStandardSectionArrayList.get(currentSectionPosition - 1).getSection_title());
            } else {
                changeSaveButtonToGoToSubmit(true);
                nextBtn.setText("(" + (currentSectionPosition + 2) + "/" + brandStandardSectionArrayList.size() + ")"+getString(R.string.text_next)+" >\n" + brandStandardSectionArrayList.get(currentSectionPosition + 1).getSection_title());
                prevBtn.setText("< "+getString(R.string.text_back) + " (" + (currentSectionPosition ) + "/" + brandStandardSectionArrayList.size() + ")\n" + brandStandardSectionArrayList.get(currentSectionPosition - 1).getSection_title());
            }
        }
    }

    private boolean saveSectionOrPagewiseData()
    {
        if (validateCommentOfQuestion()) {
           // AuditSubSectionsActivity.isDataSaved = true;
           // finish();
            return true;
        }
        else
            return false;
    }
    public void countNA_Answers() {
        totalMarks = 0;
        marksObtained = 0;
        for (int i = 0; i < brandStandardSection.getQuestions().size(); i++) {
            BrandStandardQuestion brandStandardQuestion = brandStandardSection.getQuestions().get(i);
            if (!(brandStandardQuestion.getAudit_answer_na() == 1)) {
                if (brandStandardQuestion.getQuestion_type().equals("radio")) {
                    totalMarks = totalMarks + brandStandardQuestion.getOptions().get(0).getOption_mark();
                }
                else if(brandStandardQuestion.getQuestion_type().equals("target") && !TextUtils.isEmpty(brandStandardQuestion.getMax_mark()))
                {
                    totalMarks = totalMarks + Float.parseFloat(brandStandardQuestion.getMax_mark());
                }
                else {
                    for (int k = 0; k < brandStandardQuestion.getOptions().size(); k++) {
                        totalMarks = totalMarks + brandStandardQuestion.getOptions().get(k).getOption_mark();
                    }
                }
                if (brandStandardQuestion.getQuestion_type().equalsIgnoreCase("target") && !TextUtils.isEmpty(brandStandardQuestion.getAudit_answer()))
                {
                    float targetScore=Float.parseFloat(brandStandardQuestion.getAudit_answer());
                    float maxMarks=Float.parseFloat(brandStandardQuestion.getMax_mark());
                    if (targetScore>maxMarks)
                        targetScore=maxMarks;

                    marksObtained=marksObtained+targetScore;
                }
                if (brandStandardQuestion.getAudit_option_id().size() > 0) {
                    for (int l = 0; l < brandStandardQuestion.getAudit_option_id().size(); l++) {
                        for (int m = 0; m < brandStandardQuestion.getOptions().size(); m++) {
                            if (brandStandardQuestion.getAudit_option_id().get(l) == brandStandardQuestion.getOptions().get(m).getOption_id()) {
                                marksObtained = marksObtained + brandStandardQuestion.getOptions().get(m).getOption_mark();
                                break;
                            }
                        }
                    }
                }
            } else {

            }
        }
        for (int j = 0; j < brandStandardSection.getSub_sections().size(); j++) {
            ArrayList<BrandStandardQuestion> brandStandardQuestions = brandStandardSection.
                    getSub_sections().get(j).getQuestions();
            for (int i = 0; i < brandStandardQuestions.size(); i++) {
                //totalQuestionCount++;
                BrandStandardQuestion brandStandardQuestion = brandStandardQuestions.get(i);

                if (!(brandStandardQuestion.getAudit_answer_na() == 1)) {
                    if (brandStandardQuestion.getQuestion_type().equals("radio")) {
                        totalMarks = totalMarks + brandStandardQuestion.getOptions().get(0).getOption_mark();
                    }
                    else if(brandStandardQuestion.getQuestion_type().equals("target") && !TextUtils.isEmpty(brandStandardQuestion.getMax_mark()))
                    {
                        totalMarks = totalMarks + Float.parseFloat(brandStandardQuestion.getMax_mark());
                    }
                    else {
                        for (int k = 0; k < brandStandardQuestion.getOptions().size(); k++) {
                            totalMarks = totalMarks + brandStandardQuestion.getOptions().get(k).getOption_mark();
                        }
                    }

                    if (brandStandardQuestion.getQuestion_type().equalsIgnoreCase("target") && !TextUtils.isEmpty(brandStandardQuestion.getAudit_answer()))
                    {
                        float targetScore=Float.parseFloat(brandStandardQuestion.getAudit_answer());
                        float maxMarks=Float.parseFloat(brandStandardQuestion.getMax_mark());
                        if (targetScore>maxMarks)
                            targetScore=maxMarks;

                        marksObtained=marksObtained+targetScore;
                    }
                    if (brandStandardQuestion.getAudit_option_id().size() > 0) {
                        for (int l = 0; l < brandStandardQuestion.getAudit_option_id().size(); l++) {
                            for (int m = 0; m < brandStandardQuestion.getOptions().size(); m++) {
                                if (brandStandardQuestion.getAudit_option_id().get(l) ==
                                        brandStandardQuestion.getOptions().get(m).getOption_id()) {
                                    marksObtained = marksObtained + brandStandardQuestion.getOptions().get(m).getOption_mark();
                                    break;
                                }
                            }
                        }
                    }
                }

            }
        }
     /*   if (!TextUtils.isEmpty(brandStandardSection.getSection_weightage()))
        {
            float weightage = Float.parseFloat(mSectionWeightage);
            scoreText.setText("Score: " + (int)((marksObtained/totalMarks) * (weightage / 100)) + "% (" + marksObtained + "/" + totalMarks + ")");
        }
        else*/
        scoreText.setText(getString(R.string.text_score)+":" + (int) (((float) marksObtained / (float) totalMarks) * 100) + "% (" + marksObtained + "/" + totalMarks + ")");

    }

    //-----------------------Audit Times-------------------------------------
    public Runnable runnable = new Runnable() {

        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            timerText.setText("" + String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds));
            handler.postDelayed(this, 1000);
        }

    };


    @Override
    public void onNetworkCallInitiated(String service) { }
    @Override
    public void onNetworkCallCompleted(String type, String service, String responseStr)
    {
        if (service.equalsIgnoreCase(NetworkURL.BRANDSTANDARD_QUESTIONWISE_ANSWER)) {

            //newly added for position refresh
         //   this.currentBrandStandardAuditAdapter.updatehParticularPosition(itemClickedPos);
        }
        else
        {
            isAnswerCliked = false; // because question is saved
            AuditSubSectionsActivity.isDataSaved = true;
            try {
                JSONObject response = new JSONObject(responseStr);
                if (!response.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    AppUtils.toast((BaseActivity) context, response.getString(AppConstant.RES_KEY_MESSAGE));
                    if (isBackButtonClick) {
                        isBackButtonClick = false; // This is only for showing progressBar
                        finish();
                    } else if (isDialogSaveClicked) {
                        if (mNextPreviousClick == 1)
                            setNextButtonSetUP();
                        else if (mNextPreviousClick == 2)
                            setPreviousButtonSetUP();

                        isDialogSaveClicked = false;
                        mNextPreviousClick = 0;
                    }
                } else if (response.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    AppUtils.toast((BaseActivity) context, response.getString(AppConstant.RES_KEY_MESSAGE));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mProgressRL.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        mProgressRL.setVisibility(View.GONE);
        AppUtils.toast(this, this.getString(R.string.oops));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeHandlerCallBck();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();

        Toast.makeText(this,"----LOW MEMORY-----"+memoryInfo.totalMem,Toast.LENGTH_SHORT).show();

    }

    // Get a MemoryInfo object for the device's current memory status.
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    private void clearApplicationData()
    {
        ((ActivityManager)getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
    }


 public void saveSingleBrandStandardQuestionEveryClick(BrandStandardQuestion bsQuestion)
    {
        if (NetworkStatus.isNetworkConnected(this))
        {
            try {
                itemClickedPos=bsQuestion.getmClickPosition();
                questionCount=bsQuestion.getQuestionCount();
                this.currentBrandStandardAuditAdapter = bsQuestion.getStandardAuditAdapter();
                if (bsQuestion.getQuestion_type().equalsIgnoreCase(AppConstant.QUESTION_TEXTAREA) || bsQuestion.getQuestion_type().equalsIgnoreCase(AppConstant.QUESTION_TEXT) || bsQuestion.getQuestion_type().equalsIgnoreCase(AppConstant.QUESTION_NUMBER) || bsQuestion.getQuestion_type().equalsIgnoreCase(AppConstant.QUESTION_MEASUREMENT) || bsQuestion.getQuestion_type().equalsIgnoreCase(AppConstant.QUESTION_TARGET) || bsQuestion.getQuestion_type().equalsIgnoreCase(AppConstant.QUESTION_TEMPRATURE) )
                    this.currentBrandStandardAuditAdapter.updatehParticularPosition(itemClickedPos);

                JSONObject object = new JSONObject();
                object.put("audit_id", auditId);
                object.put("question_id", bsQuestion.getQuestion_id());
                object.put("audit_answer", bsQuestion.getAudit_answer());
                object.put("audit_option_id", new JSONArray(bsQuestion.getAudit_option_id()));
                object.put("audit_comment", bsQuestion.getAudit_comment());
                Log.e("JSONOBJECTQUESTION==> ",""+object.toString());
                NetworkServiceJSON networkService = new NetworkServiceJSON(NetworkURL.BRANDSTANDARD_QUESTIONWISE_ANSWER, NetworkConstant.METHOD_POST, this, this);
                networkService.call(object);
            }
            catch (Exception e) {e.printStackTrace();}
        } else
        {
            AppUtils.toast(this, getString(R.string.internet_error));
        }
    }

}