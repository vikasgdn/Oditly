package com.oditly.audit.inspection.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.oditly.audit.inspection.util.AppUtils;

import java.util.List;

public class AddBSMediaAdapter extends RecyclerView.Adapter<AddBSMediaAdapter.AddAttachmentViewHolder> {

    private Context context;
    private List<Uri> orderData;


    public AddBSMediaAdapter(Context context, List<Uri> imgList)
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
        try {
            Log.e("image in addABP ", ";;;;;;;;;;;  " + orderData.get(position));
            Bitmap bitmapPlain  = MediaStore.Images.Media.getBitmap(context.getContentResolver(),orderData.get(position));
            Bitmap bitmap = AppUtils.resizeImage(orderData.get(position), bitmapPlain, 1400, 1400);

            holder.imageName.setImageBitmap(bitmap);
            holder.imageName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFullImageDialog(context,bitmap);
                }
            });
        }
        catch (Exception e)
        {
            Log.e("image ERROR ", e.getMessage());
            e.printStackTrace();
        }

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



    public static void showFullImageDialog(final Context activity,Bitmap bitmap) {
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

            imageView.setImageBitmap(bitmap);

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
