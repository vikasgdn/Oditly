package com.oditly.audit.inspection.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.audit.AddAttachment.AddAttachmentInfo;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.AddBSAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.AddQuestionAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.ApiRequest;
import com.oditly.audit.inspection.network.apirequest.DeleteBSAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.DeleteBSQuestionAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.OktaTokenRefreshRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.ui.activty.AddAttachmentActivity;
import com.oditly.audit.inspection.ui.activty.AuditSubmitSignatureActivity;
import com.oditly.audit.inspection.ui.activty.BaseActivity;
import com.oditly.audit.inspection.ui.activty.EditAttachmentActivity;
import com.oditly.audit.inspection.ui.activty.ExoVideoPlayer;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;
import com.oditly.audit.inspection.util.Headers;

import org.json.JSONObject;

import java.util.ArrayList;

public class AddAttachmentAdapter extends RecyclerView.Adapter<AddAttachmentAdapter.AddAttachmentViewHolder> {

    private Context context;
    private ArrayList<AddAttachmentInfo> orderData;
    private  String attachType;
    private String auditId;
    private String sectionGroupId;
    private String sectionId;
    private String questionId;

    public AddAttachmentAdapter(Context context, ArrayList<AddAttachmentInfo> orderData, String attachType, String auditId, String sectionGroupId, String sectionId, String questionId) {
        this.context = context;
        this.orderData = orderData;
        this.attachType = attachType;
        this.auditId = auditId;
        this.sectionGroupId = sectionGroupId;
        this.sectionId = sectionId;
        this.questionId = questionId;
    }

    @NonNull
    @Override
    public AddAttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_attachment_layout, parent, false);

        return new AddAttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddAttachmentViewHolder holder, int position) {
        final AddAttachmentInfo addAttachmentInfo = orderData.get(position);
        String fileType = addAttachmentInfo.getFile_type();
        holder.attachedImage.setTag(R.id.pos_tag,""+position);
        if (fileType.contains("image/")) {
            holder.playButton.setVisibility(View.GONE);
            if (!AppUtils.isStringEmpty(addAttachmentInfo.getThumb_url())) {
                RequestOptions requestOptions = new RequestOptions()
                        .override(600,200)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                        .skipMemoryCache(true);
                Glide.with(context).load(Headers.getUrlWithHeaders(addAttachmentInfo.getThumb_url(), AppPreferences.INSTANCE.getAccessToken(context))).apply(requestOptions).into(holder.attachedImage);
            }
        }
        else
        {
            if (!AppUtils.isStringEmpty(addAttachmentInfo.getFile_url())) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.mipmap.video_preview);
                requestOptions.error(R.mipmap.video_preview);
                Glide.with(context).load(Headers.getUrlWithHeaders(addAttachmentInfo.getFile_url(), AppPreferences.INSTANCE.getAccessToken(context))).apply(requestOptions).into(holder.attachedImage);
            }
            holder.playButton.setVisibility(View.VISIBLE);

        }
        holder.imageName.setText(addAttachmentInfo.getFile_name());


        holder.attachedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    int pos = Integer.parseInt(view.getTag(R.id.pos_tag).toString());
                    if (orderData.get(pos).getFile_type().contains("image")) {
                        Intent intent = new Intent(context, EditAttachmentActivity.class);
                        intent.putExtra("auditId", auditId);
                        intent.putExtra("sectionGroupId", sectionGroupId);
                        intent.putExtra("sectionId", sectionId);
                        intent.putExtra("questionId", questionId);
                        intent.putExtra("attachmentDetail", addAttachmentInfo);
                        intent.putExtra("attachType", attachType);
                        intent.putExtra("editable", "0");
                        context.startActivity(intent);
                    } else {
                        ExoVideoPlayer.start(context,orderData.get(pos).getFile_url(),"");
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNotificationDialog(addAttachmentInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public class AddAttachmentViewHolder extends RecyclerView.ViewHolder {

        ImageView attachedImage;
        ImageView playButton;
        TextView imageName;
        RelativeLayout deleteIcon;

        public AddAttachmentViewHolder(View itemView) {
            super(itemView);

            attachedImage = itemView.findViewById(R.id.iv_add_attachment_image);
            playButton = itemView.findViewById(R.id.iv_playbutton);
            imageName = itemView.findViewById(R.id.tv_add_attachment_file_name);
            deleteIcon = itemView.findViewById(R.id.iv_remove_attachment_icon);
        }
    }

    private void deleteNotificationDialog(final AddAttachmentInfo addAttachmentInfo) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(R.string.app_name);
        dialog.setMessage("Do you want to delete the attachment");

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (attachType) {
                    case "bsSection":
                        deleteBsSectionAndQuestionAttachment(addAttachmentInfo,AppConstant.BS_SECTION);
                        break;
                    case "bsQuestion":
                        deleteBsSectionAndQuestionAttachment(addAttachmentInfo,AppConstant.BS_QUESTION);
                        break;
                }
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    private void deleteBsSectionAndQuestionAttachment(AddAttachmentInfo addAttachmentInfo,String type) {
        ((AddAttachmentActivity) context).showProgressDialog();
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e("", "DeleteBSResponse: " + response);
                try {
                    if (type.equalsIgnoreCase(AppConstant.BS_SECTION)) {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                            AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                            ((AddAttachmentActivity) context).getOldMediaAttachmentList("bsSection");
                        } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                            AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                        }
                    }
                    else
                    {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                            AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                            ((AddAttachmentActivity) context).getOldMediaAttachmentList("bsQuestion");
                        } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                            AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((AddAttachmentActivity) context).hideProgressDialog();
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((AddAttachmentActivity) context).hideProgressDialog();
                AppLogger.e("", "AddAttachmentError: " + error.getMessage());
                AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");

            }
        };

        String url = NetworkURL.BSDELETEATTACHMENT;

        if (AppPreferences.INSTANCE.getProviderName().equalsIgnoreCase(AppConstant.OKTA))
        {
            if (System.currentTimeMillis()<AppPreferences.INSTANCE.getOktaTokenExpireTime(context))
            {
                if (type.equalsIgnoreCase(AppConstant.BS_SECTION)) {
                    DeleteBSAttachmentRequest editBSAttachmentRequest = new DeleteBSAttachmentRequest(AppPreferences.INSTANCE.getAccessToken(context), url, auditId, addAttachmentInfo.getAudit_section_file_id(), "", "", AppPreferences.INSTANCE.getOktaToken(context), context, stringListener, errorListener);
                    VolleyNetworkRequest.getInstance(context).addToRequestQueue(editBSAttachmentRequest);
                } else {
                    DeleteBSQuestionAttachmentRequest editBSAttachmentRequest = new DeleteBSQuestionAttachmentRequest(AppPreferences.INSTANCE.getAccessToken(context), url, auditId, addAttachmentInfo.getAudit_section_file_id(), addAttachmentInfo.getAudit_question_file_id(), AppPreferences.INSTANCE.getOktaToken(context), context, stringListener, errorListener);
                    VolleyNetworkRequest.getInstance(context).addToRequestQueue(editBSAttachmentRequest);
                }
            }
            else
            {
                Response.Listener<JSONObject> jsonListener = new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        AppLogger.e("TAG", " Token SUCCESS Response: " + response);
                        AppUtils.parseRefreshTokenRespone(response,context);
                        if (type.equalsIgnoreCase(AppConstant.BS_SECTION)) {
                            DeleteBSAttachmentRequest editBSAttachmentRequest = new DeleteBSAttachmentRequest(AppPreferences.INSTANCE.getAccessToken(context), url, auditId, addAttachmentInfo.getAudit_section_file_id(), "", "", AppPreferences.INSTANCE.getOktaToken(context), context, stringListener, errorListener);
                            VolleyNetworkRequest.getInstance(context).addToRequestQueue(editBSAttachmentRequest);
                        } else {
                            DeleteBSQuestionAttachmentRequest editBSAttachmentRequest = new DeleteBSQuestionAttachmentRequest(AppPreferences.INSTANCE.getAccessToken(context), url, auditId, addAttachmentInfo.getAudit_section_file_id(), addAttachmentInfo.getAudit_question_file_id(), AppPreferences.INSTANCE.getOktaToken(context), context, stringListener, errorListener);
                            VolleyNetworkRequest.getInstance(context).addToRequestQueue(editBSAttachmentRequest);
                        }
                    }
                };
                Response.ErrorListener errListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppLogger.e("TAG", "ERROR Response: " + error);
                    }
                };
                OktaTokenRefreshRequest tokenRequest = new OktaTokenRefreshRequest(AppUtils.getTokenJson(context),jsonListener, errListener);
                VolleyNetworkRequest.getInstance(context).addToRequestQueue(tokenRequest);
            }

        }
        else
        {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    String token = task.getResult().getToken();
                                    if (type.equalsIgnoreCase(AppConstant.BS_SECTION)) {
                                        DeleteBSAttachmentRequest editBSAttachmentRequest = new DeleteBSAttachmentRequest(AppPreferences.INSTANCE.getAccessToken(context), url, auditId, addAttachmentInfo.getAudit_section_file_id(), "", "", token, context, stringListener, errorListener);
                                        VolleyNetworkRequest.getInstance(context).addToRequestQueue(editBSAttachmentRequest);
                                    } else {
                                        DeleteBSQuestionAttachmentRequest editBSAttachmentRequest = new DeleteBSQuestionAttachmentRequest(AppPreferences.INSTANCE.getAccessToken(context), url, auditId, addAttachmentInfo.getAudit_section_file_id(), addAttachmentInfo.getAudit_question_file_id(), token, context, stringListener, errorListener);
                                        VolleyNetworkRequest.getInstance(context).addToRequestQueue(editBSAttachmentRequest);
                                    }

                                }
                            }
                        });
            }
        }

    }

  /*  private void deleteQuestionFileAttachment(AddAttachmentInfo addAttachmentInfo) {
        ((AddAttachmentActivity) context).showProgressDialog();
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e("", "DeleteBSQuestionResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                        ((AddAttachmentActivity) context).getOldMediaAttachmentList("bsQuestion");
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((AddAttachmentActivity) context).hideProgressDialog();
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((AddAttachmentActivity) context).hideProgressDialog();
                AppLogger.e("", "AddAttachmentError: " + error.getMessage());
                AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");
            }
        };

        String url = NetworkURL.BSDELETEATTACHMENT;
        DeleteBSQuestionAttachmentRequest editBSAttachmentRequest = new DeleteBSQuestionAttachmentRequest(
                AppPreferences.INSTANCE.getAccessToken(context), url, auditId,
                addAttachmentInfo.getAudit_section_file_id(),
                addAttachmentInfo.getAudit_question_file_id(),context,
                stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(editBSAttachmentRequest);
    }*/

}
