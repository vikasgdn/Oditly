package com.oditly.audit.inspection.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.interfaces.OnRecyclerViewItemClickListener;
import com.oditly.audit.inspection.model.actionData.ActionCommentData;
import com.oditly.audit.inspection.model.audit.AuditInfo;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.List;


public class ActionCommentListAdapter extends RecyclerView.Adapter<ActionCommentListAdapter.AuditActionViewHolder> {

    private Context context;
    private List<ActionCommentData> data;
    private int status;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;


    public ActionCommentListAdapter(Context context, List<ActionCommentData> data, OnRecyclerViewItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.onRecyclerViewItemClickListener = listener;
    }

    @Override
    public AuditActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_action_update_commentlist, parent, false);
        return new AuditActionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AuditActionViewHolder holder, final int position) {
        //TODO : Static data testing

        final ActionCommentData auditInfo = data.get(position);
        holder.mNameTV.setText(""+auditInfo.getCreated_by_name());
        holder.mEmailTV.setText(auditInfo.getCreated_by_email());
        holder.mDateTV.setText(AppUtils.getFormatedDate(auditInfo.getCreated_on()));
        holder.mCommentTV.setText(auditInfo.getComment());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class AuditActionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mNameTV;
        TextView mEmailTV;
        TextView mDateTV;
        TextView mCommentTV;


        public AuditActionViewHolder (View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mNameTV = itemView.findViewById(R.id.tv_name);
            mEmailTV = itemView.findViewById(R.id.tv_email);
            mDateTV = itemView.findViewById(R.id.tv_date);
            mCommentTV = itemView.findViewById(R.id.tv_comment);

        }

        @Override
        public void onClick(View view) {
            onRecyclerViewItemClickListener.onItemClick(ActionCommentListAdapter.this,view,getLayoutPosition());
        }
    }


}
