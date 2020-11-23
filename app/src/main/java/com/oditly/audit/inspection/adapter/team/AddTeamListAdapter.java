package com.oditly.audit.inspection.adapter.team;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.teamData.TeamInfo;
import com.oditly.audit.inspection.ui.activty.AddTeamMemberActivity;

import java.util.HashSet;
import java.util.List;

public class AddTeamListAdapter extends RecyclerView.Adapter<AddTeamListAdapter.AuditViewHolder> {

    private Context context;
    private List<TeamInfo> mAuditList;
    private HashSet<Integer> mHashSet;


    public AddTeamListAdapter(Context context, List<TeamInfo> auditInfoList)
    {
        this.context = context;
        this.mAuditList = auditInfoList;
        mHashSet=new HashSet<>();

    }

    @NonNull
    @Override
    public AuditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_addteam, parent, false);

        return new AuditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuditViewHolder holder, final int position)
    {


        final TeamInfo auditInfo = mAuditList.get(position);
        holder.mNameTV.setText(auditInfo.getName());
        holder.mEmailTV.setText(auditInfo.getEmail());
        holder.mRoleTV.setText(auditInfo.getRole_name());
        holder.mSelectMemberCB.setTag(auditInfo);

        if (auditInfo.isSelected())
            holder.mSelectMemberCB.setChecked(true);
        else
            holder.mSelectMemberCB.setChecked(false);

        holder.mSelectMemberCB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                CheckBox cb = (CheckBox) v;
                if(cb.isChecked())
                    mAuditList.get(position).setSelected(true);
                else
                    mAuditList.get(position).setSelected(false);
              //  Toast.makeText(v.getContext(), "Clicked on Checkbox: " + position, Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mAuditList.size();


    }


    public class AuditViewHolder extends RecyclerView.ViewHolder {


        TextView mNameTV;
        TextView mEmailTV;
        TextView mRoleTV;
        CheckBox mSelectMemberCB;


        public AuditViewHolder (View itemView) {
            super(itemView);

            mNameTV = itemView.findViewById(R.id.tv_name);
            mEmailTV = itemView.findViewById(R.id.tv_email);
            mRoleTV = itemView.findViewById(R.id.tv_role);
            mSelectMemberCB = itemView.findViewById(R.id.cb_select);

        }

    }
    public void selectAll(){

        for (TeamInfo teamInfo : mAuditList)
            teamInfo.setSelected(true);
        notifyDataSetChanged();
    }
    public void unselectall(){
        mHashSet.clear();
        for (TeamInfo teamInfo : mAuditList)
            teamInfo.setSelected(false);
        notifyDataSetChanged();
    }
    public void filterList(List<TeamInfo> filterdNames) {
        this.mAuditList = filterdNames;
        notifyDataSetChanged();
    }
    public HashSet getInvitedMember()
    {
        for (TeamInfo teamInfo : mAuditList)
             if (teamInfo.isSelected())
                 mHashSet.add(teamInfo.getUser_id());
        return mHashSet;
    }



}
