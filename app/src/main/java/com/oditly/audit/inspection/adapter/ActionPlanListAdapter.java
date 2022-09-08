package com.oditly.audit.inspection.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.actionData.ActionInfo;
import com.oditly.audit.inspection.ui.activty.ActionPlanLandingActivity;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.List;

public class ActionPlanListAdapter extends RecyclerView.Adapter<ActionPlanListAdapter.AuditViewHolder> {

    private Context context;
    private List<ActionInfo> mAuditList;
    private int status=0;


    public ActionPlanListAdapter(Context context, List<ActionInfo> auditInfoList, int status)
    {
        this.context = context;
        this.mAuditList = auditInfoList;
        this.status=status;

    }

    @NonNull
    @Override
    public AuditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_actionplan, parent, false);

        return new AuditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuditViewHolder holder, int position)
    {


        final ActionInfo auditInfo = mAuditList.get(position);
        holder.tvAuditName.setText(auditInfo.getTitle());
        holder.tvLocation.setText(auditInfo.getLocation_title());
        holder.mDateTV.setText(AppUtils.getFormatedDate(auditInfo.getPlanned_date()));
        if (auditInfo.getAudit_id()==0)
            holder.mInspectionORadocTV.setText(context.getResources().getString(R.string.text_adhoc));
        else
            holder.mInspectionORadocTV.setText(context.getResources().getString(R.string.text_audit)+": "+auditInfo.getAudit_name());
        holder.mTitleTV.setText(auditInfo.getTitle());

        holder.mStatusTV.setText(auditInfo.getStatus_name());

        holder.mLowMediaumHieghTV.setText(auditInfo.getPriority_name());

        if(auditInfo.getPriority_name().equalsIgnoreCase("High")) {
            holder.mLowMediaumHieghTV.setTextColor(context.getResources().getColor(R.color.c_red));
            holder.mLowMediaumHieghTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_baseline_flag_red ,0);
        }
       else if(auditInfo.getPriority_name().equalsIgnoreCase("Urgent")) {
            holder.mLowMediaumHieghTV.setTextColor(context.getResources().getColor(R.color.c_red_urgent));
            holder.mLowMediaumHieghTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_red_urgent ,0);
        }
        else if(auditInfo.getPriority_name().equalsIgnoreCase("Low")) {
            holder.mLowMediaumHieghTV.setTextColor(context.getResources().getColor(R.color.c_green));
            holder.mLowMediaumHieghTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_baseline_flag_24 ,0);
        }
        else if(auditInfo.getPriority_name().equalsIgnoreCase("Medium")) {
            holder.mLowMediaumHieghTV.setTextColor(context.getResources().getColor(R.color.c_orange));
            holder.mLowMediaumHieghTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_baseline_flag_orange ,0);
        }



        if(auditInfo.getStatus_name().equalsIgnoreCase("pending Approval"))
            holder.mStatusTV.setTextColor(context.getResources().getColor(R.color.c_blue));
        else if(auditInfo.getStatus_name().equalsIgnoreCase("Rejected"))
            holder.mStatusTV.setTextColor(context.getResources().getColor(R.color.c_red));
        else if(auditInfo.getStatus_name().equalsIgnoreCase("approved"))
            holder.mStatusTV.setTextColor(context.getResources().getColor(R.color.c_green));
        else if(auditInfo.getStatus_name().equalsIgnoreCase("Open"))
        {
            holder.mStatusTV.setText(context.getResources().getString(R.string.text_overdue_on)+" "+AppUtils.getFormatedDate(auditInfo.getPlanned_date()));
            holder.mStatusTV.setTextColor(context.getResources().getColor(R.color.c_orange));
        }
        else if(auditInfo.getStatus_name().equalsIgnoreCase("Overdue"))
        {
           // holder.mStatusTV.setText("Overdue By "+auditInfo.getOverdue_days()+" Days");
            holder.mStatusTV.setText(context.getResources().getString(R.string.text_overdue_by_days).replace("XXX",auditInfo.getOverdue_days()));
            holder.mStatusTV.setTextColor(context.getResources().getColor(R.color.c_red));
        }




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OditlyApplication)context.getApplicationContext()).setmActionPlanData(auditInfo);  // seting in application class because we only send limited data using intent
                Intent startAudit = new Intent(context, ActionPlanLandingActivity.class);
                context.startActivity(startAudit);
            }
        });
    }



    @Override
    public int getItemCount() {
        return mAuditList.size();


    }

    public class AuditViewHolder extends RecyclerView.ViewHolder {


        TextView tvAuditName;
        TextView tvLocation;
        TextView mStatusTV;
        TextView mDateTV;
        TextView mActionTV;
        TextView mTitleTV;

        TextView mLowMediaumHieghTV;
        TextView mInspectionORadocTV;
        View itemView;

        public AuditViewHolder (View itemView) {
            super(itemView);


            this.itemView=itemView;
            tvAuditName = itemView.findViewById(R.id.tv_auditname);
            tvLocation = itemView.findViewById(R.id.tv_location);
            mTitleTV = itemView.findViewById(R.id.tv_title);
            mInspectionORadocTV = itemView.findViewById(R.id.tv_inspectionname);
            mStatusTV = itemView.findViewById(R.id.tv_status);
            mDateTV = itemView.findViewById(R.id.tv_date);
            mActionTV = itemView.findViewById(R.id.tv_resume);
            mLowMediaumHieghTV=itemView.findViewById(R.id.tv_lowhighmedium);

        }
    }




}
