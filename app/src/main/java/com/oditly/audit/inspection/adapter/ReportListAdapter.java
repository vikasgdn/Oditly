package com.oditly.audit.inspection.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.interfaces.OnRecyclerViewItemClickListener;
import com.oditly.audit.inspection.model.audit.AuditInfo;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.List;


public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.AuditActionViewHolder> {

    private Context context;
    private List<AuditInfo> data;
    private int status;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;


    public ReportListAdapter(Context context, List<AuditInfo> data, OnRecyclerViewItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.onRecyclerViewItemClickListener = listener;
    }

    @Override
    public AuditActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_report_list, parent, false);
        return new AuditActionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AuditActionViewHolder holder, final int position) {
        //TODO : Static data testing

        final AuditInfo auditInfo = data.get(position);
        holder.mAuditTypeTV.setText(""+auditInfo.getAudit_type());
        holder.mAuditNameTV.setText(auditInfo.getAudit_name());
        holder.mAuditIDTV.setText("Audit ID: "+auditInfo.getAudit_id());
        holder.mLocationTV.setText(auditInfo.getLocation_title());
        holder.mScoreTV.setText(auditInfo.getScore_text());
        holder.mAuditorNameTV.setText(auditInfo.getCreator_name());
        holder.mDateTV.setText(AppUtils.getFormatedDate(auditInfo.getAudit_due_date()));

        if (!TextUtils.isEmpty(auditInfo.getCompleted_late_key()))
            holder.mCompletedLateTV.setVisibility(View.VISIBLE);
        else
            holder.mCompletedLateTV.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(auditInfo.getIs_passed())) {
            holder.mAuditStatusTV.setVisibility(View.VISIBLE);
            if (auditInfo.getIs_passed().equalsIgnoreCase("true")) {
                holder.mAuditStatusTV.setText("Passed");
                holder.mAuditStatusTV.setBackgroundResource(R.drawable.tv_shape_green_bg);
            } else if (auditInfo.getIs_passed().equalsIgnoreCase("false")) {
                holder.mAuditStatusTV.setText("Failed");
                holder.mAuditStatusTV.setBackgroundResource(R.drawable.tv_shape_red_bg);
            }
        }
        else{
            holder.mAuditStatusTV.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class AuditActionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mAuditTypeTV;
        TextView mAuditNameTV;
        TextView mLocationTV;
        TextView mAuditIDTV;
        TextView mDateTV;
        TextView mScoreTV;
        TextView mAuditorNameTV;
        TextView mAuditStatusTV;
        TextView mCompletedLateTV;

        public AuditActionViewHolder (View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mAuditNameTV = itemView.findViewById(R.id.tv_auditname);
            mAuditTypeTV = itemView.findViewById(R.id.tv_audittype);
            mLocationTV = itemView.findViewById(R.id.tv_location);
            mAuditIDTV = itemView.findViewById(R.id.tv_auditid);
            mDateTV = itemView.findViewById(R.id.tv_date);
            mScoreTV = itemView.findViewById(R.id.tv_score);
            mAuditorNameTV = itemView.findViewById(R.id.tv_auditorname);
            mAuditStatusTV= itemView.findViewById(R.id.tv_auditstatus);
            mCompletedLateTV= itemView.findViewById(R.id.tv_completelate);

        }

        @Override
        public void onClick(View view) {
            onRecyclerViewItemClickListener.onItemClick(ReportListAdapter.this,view,getLayoutPosition());
        }
    }


}
