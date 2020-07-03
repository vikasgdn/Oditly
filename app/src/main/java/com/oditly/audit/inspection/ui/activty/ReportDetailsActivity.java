package com.oditly.audit.inspection.ui.activty;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.github.pdfviewer.PDFView;
import com.oditly.audit.inspection.BuildConfig;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.network.DownloadPdfTask;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

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
    private  int mAuditID=0;
    private String mAuditName="";
    private String mPdfFileUrl="";


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
        mNoncomplencecTV=(TextView)findViewById(R.id.tv_noncomplence);

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
            mAuditNameTV.setText(mAuditName);
            mAuditIDTV.setText(""+mAuditID);
            mAuditTypeTV.setText(intent.getStringExtra(AppConstant.AUDIT_TYPE_NAME));
            mCheckListTV.setText(intent.getStringExtra(AppConstant.AUDIT_CHECKLIST));
            mLocationTV.setText(intent.getStringExtra(AppConstant.AUDIT_LOCATION));
            mAuditorTV.setText(intent.getStringExtra(AppConstant.AUDIT_AUDITOR));
            mAssignedByTV.setText(intent.getStringExtra(AppConstant.AUDIT_ASSIGNED_BY));
            mScoreTV.setText(intent.getStringExtra(AppConstant.AUDIT_SCORE));
            mNoncomplencecTV.setText(intent.getStringExtra(AppConstant.AUDIT_NON_COMPLIANCE));
            mStartDateTV.setText(AppUtils.getFormatedDate(startDate));

        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_view:
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AppConstant.ODITLY + "/" + mAuditID + ".pdf");
                if(file.exists())
                    viewPdf();
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(ReportDetailsActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FOR_WRITE_PDF);
        } else {
            DownloadPdfTask downloadTask = new DownloadPdfTask(this, mPdfFileUrl,mAuditID, this);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_FOR_WRITE_PDF) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { ;
                downloadPdf();
            } else {

                AppUtils.toast(this,"Permission Denied");
            }
        }
    }
    @Override
    public void onPDFDownloadFinished(String path) {

        // AppUtils.toast(this,"File downloaded successfully");
        viewPdf();
    }

    private void viewPdf() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AppConstant.ODITLY + "/" + mAuditID + ".pdf");
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
