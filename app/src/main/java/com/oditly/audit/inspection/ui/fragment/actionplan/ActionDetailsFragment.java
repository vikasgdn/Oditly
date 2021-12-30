package com.oditly.audit.inspection.ui.fragment.actionplan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.actionData.ActionInfo;
import com.oditly.audit.inspection.ui.fragment.BaseFragment;
import com.oditly.audit.inspection.util.AppUtils;

public class ActionDetailsFragment extends BaseFragment {
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
    private ActionInfo mAuditInfoActionPlanData;

    private int mPage;

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
        return inflater.inflate(R.layout.fragment_action_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuditInfoActionPlanData=((OditlyApplication)mActivity.getApplicationContext()).getmActionPlanData();
        initView(getView());
        initVar();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

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

    }

    @Override
    protected void initVar() {
        super.initVar();

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
        }
    }
}
