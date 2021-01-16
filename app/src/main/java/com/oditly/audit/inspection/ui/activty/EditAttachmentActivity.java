package com.oditly.audit.inspection.ui.activty;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.model.audit.AddAttachment.AddAttachmentInfo;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.EditBSAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.EditBSQuestionAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;
import com.oditly.audit.inspection.util.Headers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditAttachmentActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_header_title)
    TextView mHeaderTitle;
    @BindView(R.id.iv_attachment_image)
    ImageView attachmentImage;
    @BindView(R.id.edit_image)
    ImageButton editImage;
    @BindView(R.id.tv_attachment_name)
    TextView attachmentName;
    @BindView(R.id.cb_add_attachment_critical)
    CheckBox criticalCheckBox;
    @BindView(R.id.tv_critical_decription_btn)
    TextView descriptionBtn;
    @BindView(R.id.tv_attachment_description)
    EditText attachmentDescription;
    @BindView(R.id.tv_edit_btn)
    TextView editBtn;
    @BindView(R.id.attachment_save_btn)
    Button saveBtn;
    @BindView(R.id.attachment_name)
    TextView attachment_name;
    @BindView(R.id.attachment_name_layout)
    LinearLayout attachmentNameLayout;
    @BindView(R.id.ll_parent_progress)
    RelativeLayout mRparenProgressRL;
    String attachType = "";
    String auditId = "";
    String sectionGroupId = "";
    String sectionId = "";
    String questionId = "";
    int isCritical = 0;
    private String editable = "";
    Context context;
  //  private ProgressDialog progressDialog;
    private AddAttachmentInfo addAttachmentInfo;
    private static final String TAG = EditAttachmentActivity.class.getSimpleName();


    public static Drawable sDrawable=null;
    private String mFromWhere="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attachment);
        ButterKnife.bind(this);
        context = this;
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        mHeaderTitle = findViewById(R.id.tv_header_title);
        mHeaderTitle.setText(R.string.text_attachment);
        findViewById(R.id.iv_header_left).setOnClickListener(this);

        editBtn= findViewById(R.id.tv_edit_btn);

        mRparenProgressRL = findViewById(R.id.ll_parent_progress);
        attachmentName = findViewById(R.id.tv_attachment_name);
        criticalCheckBox = findViewById(R.id.cb_add_attachment_critical);
        descriptionBtn = findViewById(R.id.tv_critical_decription_btn);
        attachmentDescription = findViewById(R.id.tv_attachment_description);
        attachmentImage = findViewById(R.id.iv_attachment_image);

        editImage = findViewById(R.id.edit_image);


        saveBtn = findViewById(R.id.attachment_save_btn);
        attachment_name = findViewById(R.id.attachment_name);
        attachmentNameLayout = findViewById(R.id.attachment_name_layout);


        mFromWhere=getIntent().getStringExtra("LOCATION");
        editable = getIntent().getStringExtra("editable");

        auditId = getIntent().getStringExtra("auditId");
        sectionGroupId = getIntent().getStringExtra("sectionGroupId");
        sectionId = getIntent().getStringExtra("sectionId");
        questionId = getIntent().getStringExtra("questionId");

        attachType = getIntent().getStringExtra("attachType");
        addAttachmentInfo = getIntent().getParcelableExtra("attachmentDetail");
     //   progressDialog = new ProgressDialog(context);
      //  progressDialog.setMessage("Loading...");
        if (editable.equals("0")){
            editBtn.setVisibility(View.VISIBLE);
        }else {
            editBtn.setVisibility(View.GONE);
        }
        setData();
        editBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        descriptionBtn.setOnClickListener(this);
        criticalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    isCritical = 1;
                } else {
                    isCritical = 0;
                }
            }
        });


        editImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_edit_btn:
                attachmentDescription.setEnabled(true);
                criticalCheckBox.setEnabled(true);
                saveBtn.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.GONE);
                break;

            case R.id.edit_image:
                Intent intent = new Intent(context, EditImageActivity.class);
                intent.putExtra(AppConstant.FROMWHERE,"EDIT");
                intent.putExtra(AppConstant.AUDIT_ID, auditId);
                intent.putExtra(AppConstant.SECTION_GROUPID, sectionGroupId);
                intent.putExtra(AppConstant.SECTION_ID, sectionId);
                intent.putExtra(AppConstant.QUESTION_ID, questionId);
                intent.putExtra(AppConstant.QUESTION_FILEID, ""+addAttachmentInfo.getAudit_question_file_id());
                intent.putExtra(AppConstant.SECTION_FILEID, ""+addAttachmentInfo.getAudit_section_file_id());
                intent.putExtra("attachType", attachType);
                startActivityForResult(intent,123);
                break;

            case R.id.attachment_save_btn:
                switch (attachType) {
                    case "bsSection":
                        if (!TextUtils.isEmpty(attachmentDescription.getText()))
                            addBsFileAttachment();
                        else
                            AppUtils.toast(this,getString(R.string.text_enter_desc));
                        break;
                    case "bsQuestion":
                        if (!TextUtils.isEmpty(attachmentDescription.getText()))
                          addQuestionFileAttachment();
                        else
                            AppUtils.toast(this,getString(R.string.text_enter_desc));
                        break;

                }
                break;
            case R.id.tv_critical_decription_btn:
                notificationDialog();
                break;
            case R.id.iv_header_left:
                onBackPressed();
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 123 && resultCode == RESULT_OK)
        {
            Uri uri = Uri.fromFile(new File(data.getStringExtra("path")));
            attachmentImage.setImageURI(uri);
        }
    }

    private void setData() {
        if (!AppUtils.isStringEmpty(addAttachmentInfo.getFile_url())) {
          //  progressDialog.show();
            mRparenProgressRL.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(Headers.getUrlWithHeaders(addAttachmentInfo.getFile_url(),
                            AppPreferences.INSTANCE.getAccessToken(context)))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                          //  progressDialog.dismiss();
                            mRparenProgressRL.setVisibility(View.GONE);
                            AppUtils.toast(EditAttachmentActivity.this, "Image not available");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                          //  progressDialog.dismiss();
                            mRparenProgressRL.setVisibility(View.GONE);
                            sDrawable=resource;
                            return false;
                        }
                    })
                    .into(attachmentImage);
        }
        if (!AppUtils.isStringEmpty(addAttachmentInfo.getClient_file_name())) {
            attachmentName.setText(addAttachmentInfo.getClient_file_name());
        }
        if (!AppUtils.isStringEmpty(addAttachmentInfo.getClient_file_name())) {
            attachment_name.setText(addAttachmentInfo.getClient_file_name());
        }
        if (!AppUtils.isStringEmpty(addAttachmentInfo.getDescription())) {
            attachmentDescription.setText(addAttachmentInfo.getDescription());
        }
        if (addAttachmentInfo.getIs_critical() == 1) {
            criticalCheckBox.setChecked(true);
        }
        switch (attachType) {
            case "bsSection":
                attachmentNameLayout.setVisibility(View.VISIBLE);
                attachment_name.setVisibility(View.GONE);
                break;
            case "bsQuestion":
                attachmentNameLayout.setVisibility(View.VISIBLE);
                attachment_name.setVisibility(View.GONE);
                break;

        }
    }

    private void notificationDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(R.string.app_name);
        dialog.setMessage(R.string.text_pleasemark);

        dialog.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    private void addBsFileAttachment() {
        mRparenProgressRL.setVisibility(View.VISIBLE);
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "AddAttachmentResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR))
                    {
                        AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                        attachmentDescription.setEnabled(false);
                        criticalCheckBox.setEnabled(false);
                        saveBtn.setVisibility(View.GONE);
                        editBtn.setVisibility(View.VISIBLE);
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                mRparenProgressRL.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRparenProgressRL.setVisibility(View.GONE);
                AppLogger.e(TAG, "AddAttachmentError: " + error.getMessage());
                AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");

            }
        };

        String url = NetworkURL.BSEDITATTACHMENT;
        EditBSAttachmentRequest editBSAttachmentRequest = new EditBSAttachmentRequest(
                AppPreferences.INSTANCE.getAccessToken(context), url, addAttachmentInfo.getClient_file_name(), auditId,
                addAttachmentInfo.getAudit_section_file_id(), attachmentDescription.getText().toString(), isCritical, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(editBSAttachmentRequest);
    }

    private void addQuestionFileAttachment() {
        mRparenProgressRL.setVisibility(View.VISIBLE);
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "AddAttachmentResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                        attachmentDescription.setEnabled(false);
                        criticalCheckBox.setEnabled(false);
                        saveBtn.setVisibility(View.GONE);
                        editBtn.setVisibility(View.VISIBLE);
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                mRparenProgressRL.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // hideProgressDialog();
                mRparenProgressRL.setVisibility(View.GONE);
                AppLogger.e(TAG, "AddAttachmentError: " + error.getMessage());
                AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");

            }
        };

        String url = NetworkURL.BSEDITATTACHMENT;
        EditBSQuestionAttachmentRequest editBSAttachmentRequest = new EditBSQuestionAttachmentRequest(
                AppPreferences.INSTANCE.getAccessToken(context), url, addAttachmentInfo.getClient_file_name(), auditId,
                addAttachmentInfo.getAudit_section_file_id(), addAttachmentInfo.getAudit_question_file_id(), attachmentDescription.getText().toString(), isCritical, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(editBSAttachmentRequest);
    }


}
