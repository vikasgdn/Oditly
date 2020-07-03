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
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.List;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.AuditViewHolder> {

    private Context context;
    private List<TeamList> mAuditList;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;


    public TeamListAdapter(Context context, List<TeamList> auditInfoList,OnRecyclerViewItemClickListener listener)
    {
        this.context = context;
        this.mAuditList = auditInfoList;
        this.onRecyclerViewItemClickListener=listener;

    }

    @NonNull
    @Override
    public AuditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_teamlist, parent, false);

        return new AuditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuditViewHolder holder, final int position)
    {


        final TeamList auditInfo = mAuditList.get(position);
        String dArray[]=auditInfo.getCreated_on().split(" ");
        holder.mNameTV.setText(auditInfo.getTeam_name());
        if (dArray.length>1)
            holder.mCreateDateTV.setText(AppUtils.getFormatedDate(dArray[0]));
        int member= auditInfo.getTeam_users_count();
        if (member>1)
            holder.mCountTV.setText(""+auditInfo.getTeam_users_count()+" Members");
        else
            holder.mCountTV.setText(""+auditInfo.getTeam_users_count()+" Member");




    }



    @Override
    public int getItemCount() {
        return mAuditList.size();


    }

    public class AuditViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView mNameTV;
        TextView mCreateDateTV;
        TextView mCountTV;
        TextView mViewTeamTV;


        public AuditViewHolder (View itemView) {
            super(itemView);

            //  itemView.setOnClickListener(this);
            mNameTV = itemView.findViewById(R.id.tv_teamname);
            mCreateDateTV = itemView.findViewById(R.id.tv_create_date);
            mCountTV = itemView.findViewById(R.id.tv_count);
            mViewTeamTV = itemView.findViewById(R.id.tv_viewteam);
            mViewTeamTV.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onRecyclerViewItemClickListener.onItemClick(TeamListAdapter.this,view,getLayoutPosition());
        }
    }



}
