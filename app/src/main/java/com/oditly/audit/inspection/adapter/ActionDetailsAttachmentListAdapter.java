package com.oditly.audit.inspection.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.model.audit.AddAttachment.AddAttachmentInfo;
import com.oditly.audit.inspection.ui.activty.AudioPlayerActivity;
import com.oditly.audit.inspection.ui.activty.EditAttachmentActivity;
import com.oditly.audit.inspection.ui.activty.ExoVideoPlayer;
import com.oditly.audit.inspection.ui.activty.ShowHowImageActivity;
import com.oditly.audit.inspection.ui.activty.ShowHowWebViewActivity;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;
import com.oditly.audit.inspection.util.Headers;

import java.util.List;

public class ActionDetailsAttachmentListAdapter extends RecyclerView.Adapter<ActionDetailsAttachmentListAdapter.AddAttachmentViewHolder> {

    private Context context;
    private List<AddAttachmentInfo> mAttachmentListData;


    public ActionDetailsAttachmentListAdapter(Context context, List<AddAttachmentInfo> imgList)
    {
        this.context = context;
        this.mAttachmentListData = imgList;

    }

    @NonNull
    @Override
    public AddAttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_layout_actiondetails, parent, false);

        return new AddAttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddAttachmentViewHolder holder, int position)
    {
        AddAttachmentInfo attachmentInfo=mAttachmentListData.get(position);
        try {
            if (!AppUtils.isStringEmpty(attachmentInfo.getFile_url())) {

                if (attachmentInfo.getFile_type().contains("video"))
                {
                    holder.imageName.setImageResource(R.mipmap.video);

                }
                else if (attachmentInfo.getFile_type().contains("audio"))
                {
                   holder.imageName.setImageResource(R.mipmap.audio);
                }
                else if (attachmentInfo.getFile_type().contains("image")) {
                    RequestOptions requestOptions = new RequestOptions()
                            .override(600, 200)
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                            .skipMemoryCache(true);
                    Glide.with(context).load(Headers.getUrlWithHeaders(attachmentInfo.getThumb_url(), AppPreferences.INSTANCE.getAccessToken(context))).apply(requestOptions).into(holder.imageName);
                }
                else
                {
                    holder.imageName.setImageResource(R.mipmap.file);
                }
            }
        }
        catch (Exception e)
        {
            Log.e("image ERROR ", e.getMessage());
            e.printStackTrace();
        }


        holder.imageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    AddAttachmentInfo attachmentInfo1=mAttachmentListData.get(position);
                    if (attachmentInfo1!=null)
                    {
                        if (attachmentInfo1.getFile_type().contains("image"))
                            ShowHowImageActivity.start(context,attachmentInfo1.getFile_url(), AppConstant.ACTIONPLAN);
                        else  if (attachmentInfo1.getFile_type().contains("audio"))
                            AudioPlayerActivity.start(context, attachmentInfo1.getFile_url());
                        else  if (attachmentInfo1.getFile_type().contains("video"))
                            ExoVideoPlayer.start(context, attachmentInfo1.getFile_url(),AppConstant.ACTIONPLAN);
                        else
                            ShowHowWebViewActivity.start(context,attachmentInfo1.getFile_url(),AppConstant.ACTIONPLAN);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return mAttachmentListData.size();


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
