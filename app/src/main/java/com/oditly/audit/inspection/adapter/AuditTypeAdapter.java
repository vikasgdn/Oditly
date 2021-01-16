package com.oditly.audit.inspection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.audit.AuditType;

import java.util.List;


public class AuditTypeAdapter extends RecyclerView.Adapter<AuditTypeAdapter.AddAttachmentViewHolder> {

    private List<AuditType> auditList;
    CustomItemClickListener customItemClickListener;


    public AuditTypeAdapter(Context context, List<AuditType> imgList, CustomItemClickListener customItemClickLis)
    {
        this.auditList = imgList;
        this.customItemClickListener=customItemClickLis;

    }

    @NonNull
    @Override
    public AddAttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audit_type_list, parent, false);

        return new AddAttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddAttachmentViewHolder holder, int position)
    {

        final AuditType type=auditList.get(position);
        holder.mAuditName.setText(""+type.name);

        holder.mParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onItemClick(type);
            }
        });


    }

    public interface CustomItemClickListener {
        void onItemClick(AuditType brandStandardSections);
    }

    @Override
    public int getItemCount() {
        return auditList.size();


    }

    public class AddAttachmentViewHolder extends RecyclerView.ViewHolder {

        ImageView mAuditImage;
        TextView mAuditName;
        LinearLayout mParentLayout;

        public AddAttachmentViewHolder(View itemView) {
            super(itemView);

            mAuditImage = itemView.findViewById(R.id.iv_image);
            mAuditName = itemView.findViewById(R.id.tv_auditname);
            mParentLayout=itemView.findViewById(R.id.ll_parent);


        }
    }





}
