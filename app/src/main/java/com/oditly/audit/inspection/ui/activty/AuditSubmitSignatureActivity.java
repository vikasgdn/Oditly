package com.oditly.audit.inspection.ui.activty;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.AddActionAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.AddAuditSignatureRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class AuditSubmitSignatureActivity extends BaseActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String TAG =AddAttachmentActivity.class.getSimpleName(); ;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SignaturePad mSignaturePad;
    private Context context;
    private Button mClearButton;
    private Button mSaveButton;
    private TextView mHeaderTitleTV;
    private String mAuditId="";
    private RelativeLayout mProgressBarRL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_submit_signature);
        context = this;
        mAuditId=getIntent().getStringExtra(AppConstant.AUDIT_ID);
        Log.e("AUDIT ID ===> ",""+mAuditId);

        initView();
        initVar();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void initView() {
        super.initView();

        findViewById(R.id.iv_header_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mProgressBarRL = (RelativeLayout) findViewById(R.id.ll_parent_progress);
        mHeaderTitleTV = findViewById(R.id.tv_header_title);
        mHeaderTitleTV.setText(getString(R.string.text_signature_pad));
        // setActionBar();
        mSignaturePad = findViewById(R.id.signature_pad);


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

    }

    @Override
    protected void initVar() {
        super.initVar();

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }



    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Please save your signature",Toast.LENGTH_SHORT).show();
    }

    private void addAuditSignature(byte[] imageByteData) {
        //  showProgressDialog();
        mProgressBarRL.setVisibility(View.VISIBLE);
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "AddSignatureResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppDialogs.messageDialogWithOKButton(AuditSubmitSignatureActivity.this,getString(R.string.text_auditsubmited));
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        if (object.optString(AppConstant.RES_KEY_MESSAGE).equalsIgnoreCase("Already submitted"))
                            AppDialogs.messageDialogWithOKButton(AuditSubmitSignatureActivity.this,getString(R.string.text_auditsubmited));
                        else
                            AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //   hideProgressDialog();
                mProgressBarRL.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hideProgressDialog();
                mProgressBarRL.setVisibility(View.GONE);
                AppLogger.e(TAG, "AddAttachmentError: " + error.getMessage());
                //AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");
                Toast.makeText(getApplicationContext(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();

            }
        };

        String fileName = "Oditly-" + mAuditId + ".jpeg";
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                AddAuditSignatureRequest addBSAttachmentRequest = new AddAuditSignatureRequest(AppPreferences.INSTANCE.getAccessToken(context), NetworkURL.AUDIT_INTERNAL_SIGNATURE, fileName, imageByteData, mAuditId,task.getResult().getToken(),context,stringListener, errorListener);
                                VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
                            }
                        }
                    });
        }
    }

}
