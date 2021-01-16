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
    private TextView mTitleTV;
    private TextView mActionTV;
    private TextView mPlannedDateTV;
    private TextView mDetailsTV;
    private TextView mAssignedTV;
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

        mTitleTV=(TextView)view.findViewById(R.id.tv_title);
        mActionTV=(TextView)view.findViewById(R.id.tv_action);
        mPlannedDateTV=(TextView)view.findViewById(R.id.tv_plandate);
        mDetailsTV=(TextView)view.findViewById(R.id.tv_details);
        mAssignedTV=(TextView)view.findViewById(R.id.tv_assignedto);
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


            mTitleTV.setText(mAuditInfoActionPlanData.getTitle());
            mActionTV.setText(mAuditInfoActionPlanData.getAction_name());
            mPlannedDateTV.setText(AppUtils.getFormatedDate(mAuditInfoActionPlanData.getPlanned_date()));
            mDetailsTV.setText(mAuditInfoActionPlanData.getAction_details());
            mAssignedTV.setText(assignTo);
            mCCEmailTV.setText(userEmail);
            mLocationTV.setText(mAuditInfoActionPlanData.getLocation_title());
            mAttachmentTV.setText("N/A");
            mApprovedToTV.setText("N/A");
        }
    }
}
