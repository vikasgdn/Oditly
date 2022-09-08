package com.oditly.audit.inspection.ui.fragment.actionplan;

import android.app.MediaRouteButton;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.ActionDetailsAttachmentListAdapter;
import com.oditly.audit.inspection.databinding.FragmentActionDetailsBinding;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.model.actionData.ActionInfo;
import com.oditly.audit.inspection.model.actionData.ActionRootObject;
import com.oditly.audit.inspection.model.actionData.BrandStandardQuestionForAction;
import com.oditly.audit.inspection.model.audit.AddAttachment.AddAttachmentInfo;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.ui.activty.BaseActivity;
import com.oditly.audit.inspection.ui.fragment.BaseFragment;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionDetailsFragment extends BaseFragment implements View.OnClickListener, INetworkEvent {
    public static final String ARG_PAGE = "ARG_PAGE";
    private TextView mActionTitleTV;
    private TextView mActionPlanIdTV;
    private TextView mAuditIdTV;
    private TextView mDueDateTV;
    private TextView mCorrectiveActionRequiredTV;
    private TextView mAssignedBYTV;
    private TextView mAssigneeTV;
    private TextView mCCEmailTV;
    private TextView mAttachmentTV;
    private TextView mLocationTV;
    private TextView mApprovedToTV;




    private ActionInfo mAuditInfoActionPlanDataForID;

    private int mPage;

    String mActionDetailsURL="";

    private FragmentActionDetailsBinding actionDetailsBinding;

    private ActionDetailsAttachmentListAdapter mActionAttachmentAdapter;
    private ActionDetailsAttachmentListAdapter mActionQuestionAttachmentAdapter;
    private ArrayList<AddAttachmentInfo> mQuestionMediaList;
    private ArrayList<AddAttachmentInfo> mActionMediaList;
    private RelativeLayout mSpinKitView;

    public static ActionDetailsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ActionDetailsFragment fragment = new ActionDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.actionDetailsBinding = FragmentActionDetailsBinding.inflate(getLayoutInflater(), container, false);
        return actionDetailsBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuditInfoActionPlanDataForID=((OditlyApplication)mActivity.getApplicationContext()).getmActionPlanData();
        initView(getView());
        initVar();
        getACtionPlanData();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mSpinKitView=(RelativeLayout)view.findViewById(R.id.ll_parent_progress);
        mActionTitleTV=(TextView)view.findViewById(R.id.tv_title);
        mAuditIdTV=(TextView)view.findViewById(R.id.tv_auditid);
        mActionPlanIdTV=(TextView)view.findViewById(R.id.tv_action_plan_id);
        mDueDateTV=(TextView)view.findViewById(R.id.tv_duedate);
        mCorrectiveActionRequiredTV=(TextView)view.findViewById(R.id.tv_details);
        mAssigneeTV=(TextView)view.findViewById(R.id.tv_assignee);
        mAssignedBYTV=(TextView)view.findViewById(R.id.tv_assigned_by);
        mCCEmailTV=(TextView)view.findViewById(R.id.tv_ccemail);
        mAttachmentTV=(TextView)view.findViewById(R.id.tv_attachment);
        mLocationTV=(TextView)view.findViewById(R.id.tv_location);
        mApprovedToTV=(TextView)view.findViewById(R.id.tv_approveto);

        mActionTitleTV.setOnClickListener(this);

    }

    @Override
    protected void initVar() {
        super.initVar();


        mActionMediaList=new ArrayList<>();
        mQuestionMediaList=new ArrayList<>();

        mActionAttachmentAdapter=new ActionDetailsAttachmentListAdapter(mActivity,mActionMediaList);
        actionDetailsBinding.rvImagelistActionplan.setAdapter(mActionAttachmentAdapter);


        mActionQuestionAttachmentAdapter=new ActionDetailsAttachmentListAdapter(mActivity,mQuestionMediaList);
        actionDetailsBinding.rvImagelistInspection.setAdapter(mActionQuestionAttachmentAdapter);



    }


    private void getACtionPlanData()
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {
            mSpinKitView.setVisibility(View.VISIBLE);
            mActionDetailsURL= NetworkURL.GET_ACTIONPLAN_DATA+mAuditInfoActionPlanDataForID.getAction_plan_id();
            Log.e("Filter url==> ",""+mActionDetailsURL);
            NetworkService networkService = new NetworkService(mActionDetailsURL, NetworkConstant.METHOD_GET, this,mActivity);
            networkService.call( new HashMap<String, String>());
        } else
        {
            AppUtils.toast(mActivity, getString(R.string.internet_error));

        }
    }

    @Override
    public void onNetworkCallInitiated(String service) {
        //  mSpinKitView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {
        mSpinKitView.setVisibility(View.GONE);
        actionDetailsBinding.llParentdata.setVisibility(View.VISIBLE);
        ActionRootObject actionDetailsRootObject = new GsonBuilder().create().fromJson(response, ActionRootObject.class);
        if (actionDetailsRootObject.getData() != null && actionDetailsRootObject.getData().size() > 0)
        {
            ActionInfo actionInfo=actionDetailsRootObject.getData().get(0);
            setActionDetailsData(actionInfo);
        }
        else
        {
            AppUtils.toast((BaseActivity) mActivity,actionDetailsRootObject.getMessage());
        }


    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("ACTION PLAN ERRROR ",""+errorMessage);
        mSpinKitView.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        //getACtionPlanData();
    }


    private void setActionDetailsData(ActionInfo mAuditInfoActionPlanData )
    {
        if(mAuditInfoActionPlanData!=null)
        {
            String userEmail="N/A",assignTo="";
            if(mAuditInfoActionPlanData.getAssigned_user_email()!=null && mAuditInfoActionPlanData.getAssigned_user_email().length()>0)
                userEmail=mAuditInfoActionPlanData.getAssigned_user_email();
            if(mAuditInfoActionPlanData.getAssigned_users()!=null && mAuditInfoActionPlanData.getAssigned_users().size()>0)
            {
                for(int i=0;i<mAuditInfoActionPlanData.getAssigned_users().size();i++)
                {
                    if(i==0)
                        assignTo=mAuditInfoActionPlanData.getAssigned_users().get(i).getAssigned_user_name();
                    else
                        assignTo=assignTo+","+mAuditInfoActionPlanData.getAssigned_users().get(i).getAssigned_user_name();
                }
            }
            else
                assignTo="N/A";

            String mAuditID="N/A";
            if (mAuditInfoActionPlanData.getAudit_id()!=0)
                mAuditID=""+mAuditInfoActionPlanData.getAudit_id();


            mAuditIdTV.setText(mAuditID);
            mActionTitleTV.setText(mAuditInfoActionPlanData.getTitle());
            mActionPlanIdTV.setText(""+mAuditInfoActionPlanData.getAction_plan_id());
            mDueDateTV.setText(AppUtils.getFormatedDate(mAuditInfoActionPlanData.getPlanned_date()));
            mCorrectiveActionRequiredTV.setText(mAuditInfoActionPlanData.getAction_details());
            mAssigneeTV.setText(assignTo);
            mAssignedBYTV.setText(mAuditInfoActionPlanData.getCreator_fname()+" "+mAuditInfoActionPlanData.getCreator_lname());
            mCCEmailTV.setText(userEmail);
            mLocationTV.setText(mAuditInfoActionPlanData.getLocation_title());
            mAttachmentTV.setText("N/A");
            mApprovedToTV.setText("N/A");

            if (mAuditInfoActionPlanData.getFiles()!=null && mAuditInfoActionPlanData.getFiles().size()>0)
            {
                mActionMediaList.addAll(mAuditInfoActionPlanData.getFiles());
                mActionAttachmentAdapter.notifyDataSetChanged();

            }
            else
            {
                actionDetailsBinding.tvActionplanAttachmentNotfount.setVisibility(View.VISIBLE);
                actionDetailsBinding.rvImagelistActionplan.setVisibility(View.GONE);
            }

            if (mAuditInfoActionPlanData.getSections()!=null && mAuditInfoActionPlanData.getSections().size()>0)
            {
                if (mAuditInfoActionPlanData.getSections().get(0).getQuestions()!=null &&mAuditInfoActionPlanData.getSections().get(0).getQuestions().size()>0 )
                {
                    BrandStandardQuestionForAction questionForAction = mAuditInfoActionPlanData.getSections().get(0).getQuestions().get(0);

                    if (questionForAction!=null)
                    {
                        actionDetailsBinding.tvQuestionTitle.setText(questionForAction.getQuestion_title());
                        actionDetailsBinding.tvComment.setText(questionForAction.getAudit_comment());
                        String answerText="";
                        if (questionForAction.getOptions()!=null && questionForAction.getOptions().size()>0) {
                            for (int i = 0; i < questionForAction.getOptions().size(); i++) {
                                answerText = answerText + "  " + questionForAction.getOptions().get(i).getOption_text();
                            }
                        }
                        else
                            answerText=questionForAction.getAudit_answer();

                        actionDetailsBinding.tvAnswer.setText(answerText);

                        if (questionForAction.getFiles()!=null && questionForAction.getFiles().size()>0)
                        {
                            mQuestionMediaList.addAll(questionForAction.getFiles());
                            mActionQuestionAttachmentAdapter.notifyDataSetChanged();
                            actionDetailsBinding.tvInspectionAttachmentNotfount.setVisibility(View.GONE);
                        }
                        else
                        {
                            actionDetailsBinding.tvInspectionAttachmentNotfount.setVisibility(View.VISIBLE);
                            actionDetailsBinding.rvImagelistInspection.setVisibility(View.GONE);
                        }


                    }


                }
            }
            else
            {
                actionDetailsBinding.tvQuestionText.setVisibility(View.GONE);
                actionDetailsBinding.tvQuestionTitle.setVisibility(View.GONE);
                actionDetailsBinding.tvAnswerText.setVisibility(View.GONE);
                actionDetailsBinding.tvAnswer.setVisibility(View.GONE);
                actionDetailsBinding.tvCommentText.setVisibility(View.GONE);
                actionDetailsBinding.tvComment.setVisibility(View.GONE);
                actionDetailsBinding.rlQuetionimage.setVisibility(View.GONE);
                actionDetailsBinding.tvInspectionAttachment.setVisibility(View.GONE);
             //   actionDetailsBinding.tvInspectionAttachmentNotfount.setVisibility(View.GONE);


                actionDetailsBinding.divQuestion.setVisibility(View.GONE);
                actionDetailsBinding.divAnswer.setVisibility(View.GONE);
                actionDetailsBinding.divComment.setVisibility(View.GONE);
                actionDetailsBinding.divInspectionFile.setVisibility(View.GONE);


            }

        }
    }
}
