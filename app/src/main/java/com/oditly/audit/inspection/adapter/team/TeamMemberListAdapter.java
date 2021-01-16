package com.oditly.audit.inspection.adapter.team;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.teamData.TeamInfo;
import com.oditly.audit.inspection.model.teamData.TeamMembers;

import java.util.List;

public class TeamMemberListAdapter extends RecyclerView.Adapter<TeamMemberListAdapter.AuditViewHolder> {

    private Context context;
    private List<TeamMembers> mAuditList;


    public TeamMemberListAdapter(Context context, List<TeamMembers> auditInfoList)
    {
        this.context = context;
        this.mAuditList = auditInfoList;

    }

    @NonNull
    @Override
    public AuditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_team_member_display, parent, false);

        return new AuditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuditViewHolder holder, final int position)
    {

        final TeamMembers auditInfo = mAuditList.get(position);
        holder.mNameTV.setText(auditInfo.getName());
        holder.mEmailTV.setText(auditInfo.getEmail());
        holder.mRoleTV.setText(auditInfo.getCustom_role_name()==null?"N/A":auditInfo.getCustom_role_name());
    }

    public void filterList(List<TeamMembers> filterdNames) {
        this.mAuditList = filterdNames;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mAuditList.size();
    }

    public class AuditViewHolder extends RecyclerView.ViewHolder {


        TextView mNameTV;
        TextView mEmailTV;
        TextView mRoleTV;



        public AuditViewHolder (View itemView) {
            super(itemView);

            mNameTV = itemView.findViewById(R.id.tv_name);
            mEmailTV = itemView.findViewById(R.id.tv_email);
            mRoleTV = itemView.findViewById(R.id.tv_role);


        }
    }




}
