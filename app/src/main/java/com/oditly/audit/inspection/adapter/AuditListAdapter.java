package com.oditly.audit.inspection.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.audit.AuditInfo;
import com.oditly.audit.inspection.ui.activty.AuditSectionsActivity;
import com.oditly.audit.inspection.ui.activty.AuditSubmitSignatureActivity;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.List;


public class AuditListAdapter extends RecyclerView.Adapter<AuditListAdapter.AuditActionViewHolder> {

    private Context context;
    private List<AuditInfo> data;
    private int status;


    public AuditListAdapter(Context context, List<AuditInfo> data, int status) {
        this.context = context;
        this.data = data;
        this.status = status;
    }

    @Override
    public AuditActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_auditlist, parent, false);
        return new AuditActionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AuditActionViewHolder holder, final int position) {
        //TODO : Static data testing

        final AuditInfo auditInfo = data.get(position);
        holder.mAuditTypeTV.setText(""+auditInfo.getAudit_type());
        holder.mAuditNameTV.setText(auditInfo.getAudit_name());
        holder.mLocationTV.setText(auditInfo.getLocation_title());
        holder.mDateTV.setText(AppUtils.getFormatedDate(auditInfo.getAudit_due_date()));

        if (status==1){
            holder.mActionTV.setText(context.getResources().getString(R.string.text_start));
            holder.mActionTV.setTextColor(context.getResources().getColor(R.color.c_green));
            holder.mActionTV.setBackgroundResource(R.drawable.button_border_green);
            holder.mStatusTV.setTextColor(context.getResources().getColor(R.color.c_green));
            holder.mStatusTV.setText("Due (Overdue after "+AppUtils.getFormatedDateDayMonth(auditInfo.getAudit_due_date())+")");
        }else if (status==2){
            holder.mActionTV.setText(context.getResources().getString(R.string.text_resume));
            holder.mActionTV.setTextColor(context.getResources().getColor(R.color.c_yellow));
            holder.mActionTV.setBackgroundResource(R.drawable.button_border_yello);
            holder.mStatusTV.setTextColor(context.getResources().getColor(R.color.c_yellow));
            holder.mStatusTV.setText("Incomplete (Overdue after "+AppUtils.getFormatedDateDayMonth(auditInfo.getAudit_due_date())+")");
        }else if (status==3){
            holder.mActionTV.setText(context.getResources().getString(R.string.s_overdue));
            holder.mActionTV.setTextColor(context.getResources().getColor(R.color.c_red));
            holder.mActionTV.setBackgroundResource(R.drawable.button_border_red);
            holder.mStatusTV.setTextColor(context.getResources().getColor(R.color.c_red));

            if(auditInfo.getOverdue_days()>1)
                holder.mStatusTV.setText("Overdue ( "+auditInfo.getOverdue_days()+" Days )");
            else
                holder.mStatusTV.setText("Overdue ( "+auditInfo.getOverdue_days()+" Day )");
        }
        holder.mActionTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startAudit = new Intent(context, AuditSectionsActivity.class);
                startAudit.putExtra(AppConstant.BRAND_NAME, auditInfo.getBrand_name());
                startAudit.putExtra(AppConstant.LOCATION_NAME, auditInfo.getLocation_title());
                startAudit.putExtra(AppConstant.AUDIT_NAME, auditInfo.getAudit_name());
                startAudit.putExtra(AppConstant.AUDIT_ID, "" + auditInfo.getAudit_id());
                startAudit.putExtra(AppConstant.BS_STATUS, "" + auditInfo.getBrand_std_status());
                startAudit.putExtra(AppConstant.ES_STATUS, "" + auditInfo.getExec_sum_status());
                startAudit.putExtra(AppConstant.DS_STATUS, "" + auditInfo.getDetailed_sum_status());
                AppLogger.e("bsStatus:", "-" + auditInfo.getAudit_status());
                context.startActivity(startAudit);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class AuditActionViewHolder extends RecyclerView.ViewHolder {

        TextView mAuditTypeTV;
        TextView mAuditNameTV;
        TextView mLocationTV;
        TextView mStatusTV;
        TextView mDateTV;
        TextView mActionTV;
        LinearLayout reviewerContainer;

        public AuditActionViewHolder (View itemView) {
            super(itemView);

            mAuditNameTV = itemView.findViewById(R.id.tv_auditname);
            mAuditTypeTV = itemView.findViewById(R.id.tv_audittype);
            mLocationTV = itemView.findViewById(R.id.tv_location);
            mStatusTV = itemView.findViewById(R.id.tv_status);
            mDateTV = itemView.findViewById(R.id.tv_date);
            mActionTV = itemView.findViewById(R.id.tv_resume);

        }
    }

    private void notificationDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(R.string.app_name);
        dialog.setMessage("You are not allowed to perform any function in this audit");

        dialog.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }


    private void goForSignature(String auditId)
    {
        Intent intent=new Intent(context, AuditSubmitSignatureActivity.class);
        intent.putExtra(AppConstant.AUDIT_ID,auditId);
        context.startActivity(intent);
    }
}
