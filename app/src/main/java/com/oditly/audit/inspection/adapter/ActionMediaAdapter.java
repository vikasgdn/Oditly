package com.oditly.audit.inspection.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.ui.fragment.actionplan.ActionCompleteFragment;

import java.util.List;

public class ActionMediaAdapter extends RecyclerView.Adapter<ActionMediaAdapter.AddAttachmentViewHolder> {

    private Context context;
    private List<Uri> orderData;
    private ActionCompleteFragment completeFragment;


    public ActionMediaAdapter(Context context, ActionCompleteFragment fragment, List<Uri> imgList)
    {
        this.context = context;
        this.orderData = imgList;
        this.completeFragment= fragment;

    }

    @NonNull
    @Override
    public AddAttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_action_layout, parent, false);

        return new AddAttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddAttachmentViewHolder holder, int position)
    {
        Log.e("image",";;;;;;;;;;;  "+orderData.get(position));
        holder.imageName.setImageURI(orderData.get(position));
        holder.imageName.setTag(position);
        holder.imageUpload.setTag(position);
        holder.imageName.setOnClickListener(completeFragment);
        holder.imageUpload.setOnClickListener(completeFragment);
    }

    @Override
    public int getItemCount() {
        return orderData.size();


    }

    public class AddAttachmentViewHolder extends RecyclerView.ViewHolder {

        ImageView imageName;
        ImageView imageUpload;

        public AddAttachmentViewHolder(View itemView) {
            super(itemView);

            imageName = itemView.findViewById(R.id.iv_image);
            imageUpload = itemView.findViewById(R.id.iv_imageupload);

        }
    }






}
