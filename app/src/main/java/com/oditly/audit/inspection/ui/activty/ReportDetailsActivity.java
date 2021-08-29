package com.oditly.audit.inspection.ui.activty;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.oditly.audit.inspection.BuildConfig;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.network.DownloadPdfTask;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.OktaTokenRefreshRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONObject;

import java.io.File;

public class ReportDetailsActivity extends BaseActivity implements  DownloadPdfTask.PDFDownloadFinishedListner{
    private static final int REQUEST_FOR_WRITE_PDF = 1005;
    private TextView mAuditNameTV;
    private TextView mAuditIDTV;
    private TextView mAuditTypeTV;
    private TextView mCheckListTV;
    private TextView mLocationTV;
    private TextView mAuditorTV;
    private TextView mAssignedByTV;
    private TextView mScoreTV;
    private TextView mStartDateTV;
    private TextView mNoncomplencecTV;
    private RelativeLayout mProgressBarRL;

    private  int mAuditID=0;
    private String mAuditName="";
    private String mPdfFileUrl="";
    private TextView mCompleteDateTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);
        initView();
        initVar();

    }
    @Override
    protected void initView() {
        super.initView();

        mAuditID=getIntent().getIntExtra(AppConstant.AUDIT_ID,0);
        mAuditName=getIntent().getStringExtra(AppConstant.AUDIT_NAME);
        mPdfFileUrl=NetworkURL.GET_REPORT_URL+mAuditID;
        mAuditNameTV=(TextView)findViewById(R.id.tv_auditname);
        mAuditIDTV=(TextView)findViewById(R.id.tv_auditid);
        mAuditTypeTV=(TextView)findViewById(R.id.tv_audittype);
        mCheckListTV=(TextView)findViewById(R.id.tv_checklist);
        mLocationTV=(TextView)findViewById(R.id.tv_location);
        mAuditorTV=(TextView)findViewById(R.id.tv_auditor);
        mAssignedByTV=(TextView)findViewById(R.id.tv_assignedby);
        mScoreTV=(TextView)findViewById(R.id.tv_auditscore);
        mStartDateTV=(TextView)findViewById(R.id.tv_startdate);
        mCompleteDateTV=(TextView)findViewById(R.id.tv_completedate);
        mNoncomplencecTV=(TextView)findViewById(R.id.tv_noncomplence);
        mProgressBarRL=(RelativeLayout) findViewById(R.id.ll_parent_progress);

        findViewById(R.id.iv_header_left).setOnClickListener(this);

        findViewById(R.id.tv_view).setOnClickListener(this);
        findViewById(R.id.tv_share).setOnClickListener(this);
        findViewById(R.id.tv_export).setOnClickListener(this);

        TextView textView=(TextView)findViewById(R.id.tv_header_title);
        textView.setText(getString(R.string.s_reports));

    }

    @Override
    protected void initVar() {
        super.initVar();

        if(getIntent()!=null)
        {
            Intent intent=getIntent();

            String url=intent.getStringExtra(AppConstant.AUDIT_DWNLD_URL);
            String startDate= intent.getStringExtra(AppConstant.AUDIT_STARTDATE);
            String endDate= intent.getStringExtra(AppConstant.AUDIT_COMPLETION_DATE);
            mAuditNameTV.setText(mAuditName);
            mAuditIDTV.setText(""+mAuditID);
            mAuditTypeTV.setText(intent.getStringExtra(AppConstant.AUDIT_TYPE_NAME));
            mCheckListTV.setText(intent.getStringExtra(AppConstant.AUDIT_CHECKLIST));
            mLocationTV.setText(intent.getStringExtra(AppConstant.AUDIT_LOCATION));
            mAuditorTV.setText(intent.getStringExtra(AppConstant.AUDIT_AUDITOR));
            mAssignedByTV.setText(intent.getStringExtra(AppConstant.AUDIT_ASSIGNED_BY));
            mScoreTV.setText(intent.getStringExtra(AppConstant.AUDIT_SCORE));
            mNoncomplencecTV.setText(intent.getStringExtra(AppConstant.AUDIT_NON_COMPLIANCE));
            mStartDateTV.setText(AppUtils.getFormatedDateWithTime(startDate));
            mCompleteDateTV.setText(AppUtils.getFormatedDateWithTime(endDate));

        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_view:
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AppConstant.ODITLY + "/" + mAuditID + ".pdf");
                if(file.exists())
                    viewDownLoadedPdf();
                else
                    AppUtils.toast(this,"Please export report to view");
                break;
            case R.id.tv_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, "Report from Oditly");
                share.putExtra(Intent.EXTRA_TEXT,  mAuditName+ "\n Check this report via Oditly https://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(share, "Share via :"));
                break;
            case R.id.tv_export:
                downloadPdf();
                break;
            case R.id.iv_header_left:
                finish();
                break;
        }
    }

    public void downloadPdf() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, AppConstant.GALLERY_PERMISSION_REQUEST);
        }  else {
            mProgressBarRL.setVisibility(View.VISIBLE);
            if (AppPreferences.INSTANCE.getProviderName().equalsIgnoreCase(AppConstant.OKTA)) {
                if (System.currentTimeMillis()<AppPreferences.INSTANCE.getOktaTokenExpireTime(this))
                {
                    new DownloadPdfTask(ReportDetailsActivity.this, mPdfFileUrl,mAuditID,AppPreferences.INSTANCE.getOktaToken(this), ReportDetailsActivity.this::onPDFDownloadFinished);
                }
                else
                {
                    Response.Listener<JSONObject> jsonListener = new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            AppLogger.e("TAG", " Token SUCCESS Response: " + response);
                            AppUtils.parseRefreshTokenRespone(response,ReportDetailsActivity.this);
                            new DownloadPdfTask(ReportDetailsActivity.this, mPdfFileUrl,mAuditID,AppPreferences.INSTANCE.getOktaToken(ReportDetailsActivity.this), ReportDetailsActivity.this::onPDFDownloadFinished);
                        }
                    };
                    Response.ErrorListener errListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AppLogger.e("TAG", "ERROR Response: " + error);
                        }
                    };
                    OktaTokenRefreshRequest tokenRequest = new OktaTokenRefreshRequest(AppUtils.getTokenJson(ReportDetailsActivity.this),jsonListener, errListener);
                    VolleyNetworkRequest.getInstance(ReportDetailsActivity.this).addToRequestQueue(tokenRequest);
                }
            }
            else {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        new DownloadPdfTask(ReportDetailsActivity.this, mPdfFileUrl, mAuditID, task.getResult().getToken(), ReportDetailsActivity.this::onPDFDownloadFinished);
                                    }
                                }
                            });
                }
            }

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_FOR_WRITE_PDF) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadPdf();
            } else {
                AppUtils.toast(this,"Permission Denied");
            }
        }
    }
    @Override
    public void onPDFDownloadFinished(String path) {
        mProgressBarRL.setVisibility(View.GONE);
        if (TextUtils.isEmpty(path))
            AppUtils.toast(this,getString(R.string.oops));
        else
            viewDownLoadedPdf();
    }

    private void viewDownLoadedPdf() {
        File file =null;
        if(Build.VERSION.SDK_INT >= 29) {
            String downloadDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
            file = new File(downloadDir, mAuditID+".pdf");
        }
        else
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AppConstant.ODITLY + "/" + mAuditID + ".pdf");

        Uri excelPath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            excelPath = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        else
            excelPath = Uri.fromFile(file);
        //  Uri data = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID +".provider",file);
        Log.e("PDF file===> ", "" + excelPath.toString());
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(excelPath, "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            AppUtils.toast(this, getString(R.string.text_no_viewer));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
