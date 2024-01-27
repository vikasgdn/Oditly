package com.oditly.audit.inspection.adapter.team;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.interfaces.OnRecyclerViewItemClickListener;
import com.oditly.audit.inspection.model.teamData.TeamList;
import com.oditly.audit.inspection.model.template.TemplateList;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.List;

public class TemplateListAdapter extends RecyclerView.Adapter<TemplateListAdapter.AuditViewHolder> {

    private Context context;
    private List<TemplateList> mAuditList;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;


    public TemplateListAdapter(Context context, List<TemplateList> auditInfoList, OnRecyclerViewItemClickListener listener)
    {
        this.context = context;
        this.mAuditList = auditInfoList;
        this.onRecyclerViewItemClickListener=listener;

    }

    @NonNull
    @Override
    public AuditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_templatelist, parent, false);

        return new AuditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuditViewHolder holder, final int position)
    {

        final TemplateList auditInfo = mAuditList.get(position);
        holder.mTemplateTitleTV.setSelected(true);
        holder.mTemplateTitleTV.setText(auditInfo.getQuestionnaire_title());
        holder.mTemplateIDTV.setText(""+auditInfo.getQuestionnaire_id());
        holder.mUpdatedDateTV.setText(auditInfo.getUpdated_on());
        holder.mCretaedByNameTV.setText(auditInfo.getCreated_by_name());

    }



    @Override
    public int getItemCount() {
        return mAuditList.size();


    }

    public class AuditViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView mTemplateIDTV;
        TextView mUpdatedDateTV;
        TextView mTemplateTitleTV;
        TextView mCretaedByNameTV;


        public AuditViewHolder (View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            mTemplateIDTV = itemView.findViewById(R.id.tv_templateid);
            mUpdatedDateTV = itemView.findViewById(R.id.tv_templatemodifiedon);
            mTemplateTitleTV = itemView.findViewById(R.id.tv_templatename);
            mCretaedByNameTV = itemView.findViewById(R.id.tv_templateaddedby);
           // mCretaedByNameTV.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onRecyclerViewItemClickListener.onItemClick(TemplateListAdapter.this,view,getLayoutPosition());
        }
    }



}
