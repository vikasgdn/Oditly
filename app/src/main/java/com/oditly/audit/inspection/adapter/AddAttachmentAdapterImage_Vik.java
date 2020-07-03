package com.oditly.audit.inspection.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oditly.audit.inspection.R;

import java.util.List;

public class AddAttachmentAdapterImage_Vik extends RecyclerView.Adapter<AddAttachmentAdapterImage_Vik.AddAttachmentViewHolder> {

    private Context context;
    private List<String> orderData;


    public AddAttachmentAdapterImage_Vik(Context context, List<String> imgList)
    {
        this.context = context;
        this.orderData = imgList;

    }

    @NonNull
    @Override
    public AddAttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_layout, parent, false);

        return new AddAttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddAttachmentViewHolder holder, int position)
    {

        String image= orderData.get(position);
        final Uri uri= Uri.parse(image);

        holder.imageName.setImageURI(uri);
        holder.imageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showFullImageDialog(context,uri);
            }
        });


    }

    @Override
    public int getItemCount() {
        return orderData.size();


    }

    public class AddAttachmentViewHolder extends RecyclerView.ViewHolder {

        ImageView imageName;

        public AddAttachmentViewHolder(View itemView) {
            super(itemView);

            imageName = itemView.findViewById(R.id.iv_image);

        }
    }



    public static void showFullImageDialog(final Context activity, Uri uri) {
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


            imageView.setImageURI(uri);

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
