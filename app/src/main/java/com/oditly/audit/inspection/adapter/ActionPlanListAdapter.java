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
        holder.tvAuditName.setText(auditInfo.getAction_name()+"( ID: "+auditInfo.getAudit_id()+")");
        holder.tvLocation.setText(auditInfo.getCity_name());
        holder.mDateTV.setText(AppUtils.getFormatedDate(auditInfo.getPlanned_date()));
      //  holder.mDeartmentTV.setText(auditInfo.getD);
        holder.mTitleTV.setText(auditInfo.getTitle());

        holder.mStatusTV.setText(auditInfo.getStatus_name());

        if(auditInfo.getStatus_name().equalsIgnoreCase("pending Approval"))
            holder.mStatusTV.setTextColor(context.getResources().getColor(R.color.c_yellow));
        else if(auditInfo.getStatus_name().equalsIgnoreCase("Rejected"))
            holder.mStatusTV.setTextColor(context.getResources().getColor(R.color.c_red));
        else if(auditInfo.getStatus_name().equalsIgnoreCase("approved"))
            holder.mStatusTV.setTextColor(context.getResources().getColor(R.color.c_green));




            holder.mActionTV.setOnClickListener(new View.OnClickListener() {
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
        TextView mDeartmentTV;
        LinearLayout reviewerContainer;

        public AuditViewHolder (View itemView) {
            super(itemView);

            tvAuditName = itemView.findViewById(R.id.tv_auditname);
            tvLocation = itemView.findViewById(R.id.tv_location);
            mTitleTV = itemView.findViewById(R.id.tv_title);
            mDeartmentTV = itemView.findViewById(R.id.tv_department);
            mStatusTV = itemView.findViewById(R.id.tv_status);
            mDateTV = itemView.findViewById(R.id.tv_date);
            mActionTV = itemView.findViewById(R.id.tv_resume);

        }
    }



    private void notificationDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(R.string.app_name);
        dialog.setMessage(R.string.text_youarenot);

        dialog.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }




}
