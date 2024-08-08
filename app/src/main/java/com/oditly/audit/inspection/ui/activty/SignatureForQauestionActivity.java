package com.oditly.audit.inspection.ui.activty;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkServiceJSON;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.AddAuditSignatureRequestForQuestion;
import com.oditly.audit.inspection.network.apirequest.OktaTokenRefreshRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;
import com.oditly.audit.inspection.util.Headers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class SignatureForQauestionActivity extends BaseActivity implements INetworkEvent {

    private static final String TAG =AddAttachmentActivity.class.getSimpleName(); ;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SignaturePad mSignaturePad;
    private Context context;
    private Button mClearButton;
    private Button mSaveButton;
    private TextView mHeaderTitleTV;
    private String  mAuditId="0",mQuestionID="0",mSignatureImageURL="";
    private RelativeLayout mProgressBarRL;
    private LinearLayout mButtonContainerLL;
    private RelativeLayout mImageContainerLL;

    private ImageView mSignatureImageIV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_submit_signature);
        context = this;
        mAuditId=getIntent().getStringExtra(AppConstant.AUDIT_ID);
        mSignatureImageURL=getIntent().getStringExtra(AppConstant.SIGNATURE);
        mQuestionID=""+getIntent().getIntExtra(AppConstant.QUESTION_ID,0);

        initView();
        initVar();
    }

    @Override
    protected void initView() {
        super.initView();

        mProgressBarRL = (RelativeLayout) findViewById(R.id.ll_parent_progress);
        mHeaderTitleTV = findViewById(R.id.tv_header_title);
        mHeaderTitleTV.setText(getString(R.string.text_signature_pad));
        mButtonContainerLL=(LinearLayout)findViewById(R.id.buttons_container) ;
        mSignaturePad = findViewById(R.id.signature_pad);
        mSignatureImageIV=(ImageView)findViewById(R.id.iv_signature_image);
        mImageContainerLL=(RelativeLayout) findViewById(R.id.ll_image_container);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_delete).setOnClickListener(this);

        findViewById(R.id.iv_header_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                // Toast.makeText(context, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = findViewById(R.id.clear_button);
        mSaveButton = findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                byte[] imageByteData = new byte[0];
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                imageByteData = byteArrayOutputStream.toByteArray();
                addAuditSignature(imageByteData);
            }
        });

    }

    @Override
    protected void initVar() {
        super.initVar();

        if (!AppUtils.isStringEmpty(mSignatureImageURL)) {
            mButtonContainerLL.setVisibility(View.GONE);
            mSignaturePad.setVisibility(View.GONE);
            mImageContainerLL.setVisibility(View.VISIBLE);

            RequestOptions requestOptions = new RequestOptions()
                    .override(600,200)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                    .skipMemoryCache(true);
            Glide.with(context).load(Headers.getUrlWithHeaders(mSignatureImageURL, AppPreferences.INSTANCE.getAccessToken(context))).apply(requestOptions).into(mSignatureImageIV);
        }
        else
        {
            mImageContainerLL.setVisibility(View.GONE);
            mButtonContainerLL.setVisibility(View.VISIBLE);
            mSignaturePad.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_delete:
                deleteSignatureFromServer();
                break;
        }
    }

    @Override
    public void onBackPressed() {
       finish();
    }

    private void addAuditSignature(byte[] imageByteData)
    {
        mProgressBarRL.setVisibility(View.VISIBLE);
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "AddSignatureResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR))
                    {

                           AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                            Intent intent=new Intent();
                            intent.putExtra("SignatueDelete","No");
                            intent.putExtra("URL",object.optJSONObject("data").optString("file_url"));
                            setResult(RESULT_OK,intent);
                            finish();
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        if (object.optString(AppConstant.RES_KEY_MESSAGE).equalsIgnoreCase("Already submitted"))
                            AppDialogs.messageDialogWithOKButton(SignatureForQauestionActivity.this,getString(R.string.text_auditsubmited));
                        else
                            AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mProgressBarRL.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hideProgressDialog();
                mProgressBarRL.setVisibility(View.GONE);
                AppLogger.e(TAG, "AddAttachmentError: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();

            }
        };

        String fileName = "Oditly-" + mAuditId + ".jpeg";
        if (AppPreferences.INSTANCE.getProviderName().equalsIgnoreCase(AppConstant.OKTA))
        {
            if (System.currentTimeMillis()<AppPreferences.INSTANCE.getOktaTokenExpireTime(this))
            {
                AddAuditSignatureRequestForQuestion addBSAttachmentRequest = new AddAuditSignatureRequestForQuestion(AppPreferences.INSTANCE.getAccessToken(context), NetworkURL.AUDIT_INTERNAL_SIGNATURE_QUESTION, fileName, imageByteData, mAuditId,mQuestionID,AppPreferences.INSTANCE.getOktaToken(context), context,stringListener, errorListener);
                VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
            }
            else
            {
                Response.Listener<JSONObject> jsonListener = new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        AppLogger.e("TAG", " Token SUCCESS Response: " + response);
                        AppUtils.parseRefreshTokenRespone(response, SignatureForQauestionActivity.this);
                        AddAuditSignatureRequestForQuestion addBSAttachmentRequest = new AddAuditSignatureRequestForQuestion(AppPreferences.INSTANCE.getAccessToken(context), NetworkURL.AUDIT_INTERNAL_SIGNATURE_QUESTION, fileName, imageByteData, mAuditId,mQuestionID,AppPreferences.INSTANCE.getOktaToken(context), context,stringListener, errorListener);
                        VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
                    }
                };
                Response.ErrorListener errListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppLogger.e("TAG", "ERROR Response: " + error);
                    }
                };
                OktaTokenRefreshRequest tokenRequest = new OktaTokenRefreshRequest(AppUtils.getTokenJson(SignatureForQauestionActivity.this),jsonListener, errListener);
                VolleyNetworkRequest.getInstance(SignatureForQauestionActivity.this).addToRequestQueue(tokenRequest);
            }

        }
        else {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    AddAuditSignatureRequestForQuestion addBSAttachmentRequest = new AddAuditSignatureRequestForQuestion(AppPreferences.INSTANCE.getAccessToken(context), NetworkURL.AUDIT_INTERNAL_SIGNATURE_QUESTION, fileName, imageByteData, mAuditId,mQuestionID, task.getResult().getToken(), context, stringListener, errorListener);
                                    VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
                                }
                            }
                        });
            }
        }
    }



    private void deleteSignatureFromServer() {
        if (NetworkStatus.isNetworkConnected(this)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            try {
                JSONObject params = new JSONObject();
                params.put(NetworkConstant.REQ_PARAM_AUDIT_ID, mAuditId);
                params.put(NetworkConstant.REQ_PARAM_QUSITION_ID, mQuestionID);
                NetworkServiceJSON networkService = new NetworkServiceJSON(NetworkURL.AUDIT_INTERNAL_SIGNATURE_QUESTION_REMOVE, NetworkConstant.METHOD_POST, this, this);
                networkService.call(params);
            } catch (Exception e) {
                AppUtils.toast(this, getString(R.string.internet_error));
                e.printStackTrace();
            }
        } else
            AppUtils.toast(this, getString(R.string.internet_error));

    }

    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {
        mProgressBarRL.setVisibility(View.GONE);
        Log.e("RESPONSE==>",""+response);
        try {
            JSONObject object = new JSONObject(response);
            if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                Intent intent=new Intent();
                if (service.equalsIgnoreCase(NetworkURL.AUDIT_INTERNAL_SIGNATURE_QUESTION_REMOVE))
                    intent.putExtra("SignatueDelete","Yes");
                else
                    intent.putExtra("SignatueDelete","No");
                setResult(RESULT_OK,intent);
                finish();
            } else
            {
                    AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
            }
        }
        catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        mProgressBarRL.setVisibility(View.VISIBLE);

    }
}
