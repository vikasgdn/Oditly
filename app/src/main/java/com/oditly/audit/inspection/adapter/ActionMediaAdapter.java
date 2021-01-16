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
       /* holder.imageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showFullImageDialog(context,orderData.get(position));
            }
        });*/

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



    public static void showFullImageDialog(final Context activity,Uri bitmap) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fullimage);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView imageView= dialog.findViewById(R.id.iv_fullimage);


        try {

           // String uri ="file:///storage/emulated/0/Android/data/com.oditly.audit.inspection/files/Oditly/com.oditly.audit.inspection.ui.activty.AddAttachmentActivity1599040075852/Captured_1599040080529.jpg";

            imageView.setImageURI(bitmap);

            dialog.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }




}
