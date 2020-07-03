package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AuditSectionsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_header_title)
    TextView mHeaderTitleTV;
    @BindView(R.id.ll_brand_standard)
    LinearLayout llBrandStandard;
    @BindView(R.id.ll_detailed_summary)
    LinearLayout llDetailedSummary;
    @BindView(R.id.ll_executive_summary)
    LinearLayout llExecutiveSummary;
    @BindView(R.id.tv_brand_name)
    TextView tvBrandName;
    @BindView(R.id.tv_location_name)
    TextView tvLocationName;
    @BindView(R.id.tv_audit_name)
    TextView tvAuditName;
    @BindView(R.id.brand_standard_icon)
    ImageView bsIcon;
    @BindView(R.id.brand_standard_text)
    TextView bsText;
    @BindView(R.id.brand_standard_start_btn)
    TextView bsStartBtn;
    @BindView(R.id.detailed_summary_icon)
    ImageView dsIcon;
    @BindView(R.id.detailed_summary_text)
    TextView dsText;
    @BindView(R.id.detailed_summary_start_btn)
    TextView dsStartBtn;
    @BindView(R.id.executive_summary_icon)
    ImageView esIcon;
    @BindView(R.id.executive_summary_text)
    TextView esText;
    @BindView(R.id.executive_summary_start_btn)
    TextView esStartBtn;

    @BindView(R.id.ll_parent_progress)
    RelativeLayout mSpinKitView;


    private static final int SaveBSRequest = 110;
    private static final int SaveDSRequest = 111;
    private static final int SaveESRequest = 112;
    private String auditName = "";
    private String brandName = "";
    private String locationName = "";
    private String auditId = "";
    private String bsStatus = "";
    private String dsStatus = "";
    private String esStatus = "";
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_sections);
        // setStatusBarColor("#0B87E4");
        context = this;
        ButterKnife.bind(AuditSectionsActivity.this);
        initView();
        initVar();
    }


    @Override
    protected void initView() {
        super.initView();

        mHeaderTitleTV = findViewById(R.id.tv_header_title);

        llBrandStandard = findViewById(R.id.ll_brand_standard);
        llDetailedSummary = findViewById(R.id.ll_detailed_summary);
        llExecutiveSummary = findViewById(R.id.ll_executive_summary);
        tvBrandName = findViewById(R.id.tv_brand_name);
        tvLocationName = findViewById(R.id.tv_location_name);
        tvAuditName = findViewById(R.id.tv_audit_name);
        mSpinKitView=findViewById(R.id.ll_parent_progress);

        bsIcon = findViewById(R.id.brand_standard_icon);
        bsText = findViewById(R.id.brand_standard_text);
        bsStartBtn = findViewById(R.id.brand_standard_start_btn);
        dsIcon = findViewById(R.id.detailed_summary_icon);
        dsText = findViewById(R.id.detailed_summary_text);
        dsStartBtn = findViewById(R.id.detailed_summary_start_btn);
        esIcon = findViewById(R.id.executive_summary_icon);
        esText = findViewById(R.id.executive_summary_text);
        esStartBtn = findViewById(R.id.executive_summary_start_btn);
        findViewById(R.id.iv_header_left).setOnClickListener(this);


        llBrandStandard.setOnClickListener(this);
        llDetailedSummary.setOnClickListener(this);
        llExecutiveSummary.setOnClickListener(this);
    }


    @Override
    protected void initVar() {
        super.initVar();
        mHeaderTitleTV.setText(R.string.text_audit_option);

        auditName = getIntent().getStringExtra(AppConstant.AUDIT_NAME);
        brandName = getIntent().getStringExtra(AppConstant.BRAND_NAME);
        locationName = getIntent().getStringExtra(AppConstant.LOCATION_NAME);
        auditId = getIntent().getStringExtra(AppConstant.AUDIT_ID);
        bsStatus = getIntent().getStringExtra(AppConstant.BS_STATUS);
        dsStatus = getIntent().getStringExtra(AppConstant.DS_STATUS);
        esStatus = getIntent().getStringExtra(AppConstant.ES_STATUS);
        setData();
        setBrandStandard();
        setDetailedSummary();
        setExecutiveSummary();
    }

    private void setData(){
        tvBrandName.setText(brandName);
        tvLocationName.setText(locationName);
        tvAuditName.setText(auditName);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_brand_standard:
                 sendToBrandStandard();
                break;
            case R.id.ll_detailed_summary:
                   sendToDetailsSummury();
                break;
            case R.id.ll_executive_summary:
                 sendToExecuteSummary();
                break;
            case  R.id.iv_header_left:
                onBackPressed();
                break;
        }
    }

    private void sendToExecuteSummary() {
        if (Integer.valueOf(esStatus) != 0) {
            Intent executiveSummary = new Intent(context, ExecutiveSummaryAuditActivity.class);
            executiveSummary.putExtra(AppConstant.AUDIT_ID, auditId);
            if (Integer.valueOf(esStatus) < 4)
                executiveSummary.putExtra(AppConstant.EDITABLE, "0");
            else
                executiveSummary.putExtra(AppConstant.EDITABLE, "1");
            startActivityForResult(executiveSummary, SaveESRequest);
        }else
            AppUtils.toast(AuditSectionsActivity.this, "Audit not accessible");
    }

    private void sendToDetailsSummury() {
        if (Integer.valueOf(dsStatus) != 0) {
            Intent detailedSummary = new Intent(context, DetailedSummaryAuditActivity.class);
            detailedSummary.putExtra(AppConstant.AUDIT_ID, auditId);
            if (Integer.valueOf(dsStatus) < 4)
                detailedSummary.putExtra(AppConstant.EDITABLE, "0");
            else
                detailedSummary.putExtra(AppConstant.EDITABLE, "1");
            startActivityForResult(detailedSummary,SaveDSRequest);
        }else
            AppUtils.toast(AuditSectionsActivity.this, "Audit not accessible");
    }

    private void sendToBrandStandard() {
        if (!TextUtils.isEmpty(bsStatus) && Integer.valueOf(bsStatus) != 0)
        {
            Intent brandStandard = new Intent(context, SubSectionsActivity.class);
            brandStandard.putExtra(AppConstant.AUDIT_ID, auditId);
            brandStandard.putExtra(AppConstant.AUDIT_NAME, auditName);
            if (Integer.valueOf(bsStatus) < 4)
                brandStandard.putExtra(AppConstant.EDITABLE, "0");
            else
                brandStandard.putExtra(AppConstant.EDITABLE, "1");
            startActivityForResult(brandStandard,SaveBSRequest);
        }
        else
            AppUtils.toast(AuditSectionsActivity.this, "Audit not accessible");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SaveBSRequest && resultCode == Activity.RESULT_OK){
            bsStatus = data.getStringExtra("status");
            setBrandStandard();

        }

        if (requestCode == SaveDSRequest && resultCode == Activity.RESULT_OK){
            dsStatus = data.getStringExtra("status");
            setDetailedSummary();
        }

        if (requestCode == SaveESRequest && resultCode == Activity.RESULT_OK){
            esStatus = data.getStringExtra("status");
            setExecutiveSummary();
        }
    }

    private void setBrandStandard(){
        switch (bsStatus){
            case "0":
                llBrandStandard.setBackground(context.getResources().getDrawable(R.drawable.na_status_border));
                bsIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.na_status_icon));
                bsText.setTextColor(context.getResources().getColor(R.color.textGrey));
                bsStartBtn.setText(getResources().getString(R.string.text_na));
                bsStartBtn.setTextColor(context.getResources().getColor(R.color.textGrey));
                break;
            case "1":
                llBrandStandard.setBackground(context.getResources().getDrawable(R.drawable.created_status_border));
                bsIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.create_status_icon));
                bsText.setTextColor(context.getResources().getColor(R.color.colorBlack));
                bsStartBtn.setText(getResources().getString(R.string.text_start));
                bsStartBtn.setTextColor(context.getResources().getColor(R.color.colorBlack));
                break;
            case "2":
                llBrandStandard.setBackground(context.getResources().getDrawable(R.drawable.resume_status_border));
                bsIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.resume_status));
                bsText.setTextColor(context.getResources().getColor(R.color.c_orange));
                bsStartBtn.setText(getResources().getString(R.string.text_resume));
                bsStartBtn.setTextColor(context.getResources().getColor(R.color.colorOrange));
                break;
            case "3":
                llBrandStandard.setBackground(context.getResources().getDrawable(R.drawable.rejected_status_border));
                bsIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.rejected_status_icon));
                bsText.setTextColor(context.getResources().getColor(R.color.scoreRed));
                bsStartBtn.setText(getResources().getString(R.string.text_rejected));
                bsStartBtn.setTextColor(context.getResources().getColor(R.color.scoreRed));
                break;
            case "4":
                llBrandStandard.setBackground(context.getResources().getDrawable(R.drawable.submitted_status_border));
                bsIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.submitted_status_icon));
                bsText.setTextColor(context.getResources().getColor(R.color.scoreGreen));
                bsStartBtn.setText(getResources().getString(R.string.text_submitted));
                bsStartBtn.setTextColor(context.getResources().getColor(R.color.scoreGreen));

                // if ds and es both or in N/A case then sign from here
                //  if(dsStatus.equalsIgnoreCase("0") && esStatus.equalsIgnoreCase("0"))
                //          goForSignature();   // need to change in future
                break;
            case "5":
                llBrandStandard.setBackground(context.getResources().getDrawable(R.drawable.reviewed_status_border));
                bsIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.reviewed_status_icon));
                bsText.setTextColor(context.getResources().getColor(R.color.reviewedColor));
                bsStartBtn.setText(getResources().getString(R.string.text_reviewed));
                bsStartBtn.setTextColor(context.getResources().getColor(R.color.reviewedColor));
                goForSignature();
                break;

        }
    }

    private void goForSignature() {
        Intent  intent=new Intent(this,AuditSubmitSignatureActivity.class);
        intent.putExtra(AppConstant.AUDIT_ID,auditId);
        startActivity(intent);
    }


    private void setDetailedSummary(){
        switch (dsStatus){
            case "0":
                llDetailedSummary.setBackground(context.getResources().getDrawable(R.drawable.na_status_border));
                dsIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.na_status_icon));
                dsText.setTextColor(context.getResources().getColor(R.color.textGrey));
                dsStartBtn.setText(getResources().getString(R.string.text_na));
                dsStartBtn.setTextColor(context.getResources().getColor(R.color.textGrey));
                break;
            case "1":
                llDetailedSummary.setBackground(context.getResources().getDrawable(R.drawable.created_status_border));
                dsIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.create_status_icon));
                dsText.setTextColor(context.getResources().getColor(R.color.colorBlack));
                dsStartBtn.setText(getResources().getString(R.string.text_start));
                dsStartBtn.setTextColor(context.getResources().getColor(R.color.colorBlack));
                break;
            case "2":
                llDetailedSummary.setBackground(context.getResources().getDrawable(R.drawable.resume_status_border));
                dsIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.resume_status));
                dsText.setTextColor(context.getResources().getColor(R.color.colorOrange));
                dsStartBtn.setText(getResources().getString(R.string.text_resume));
                dsStartBtn.setTextColor(context.getResources().getColor(R.color.colorOrange));
                break;
            case "3":
                llDetailedSummary.setBackground(context.getResources().getDrawable(R.drawable.rejected_status_border));
                dsIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.rejected_status_icon));
                dsText.setTextColor(context.getResources().getColor(R.color.scoreRed));
                dsStartBtn.setText(getResources().getString(R.string.text_rejected));
                dsStartBtn.setTextColor(context.getResources().getColor(R.color.scoreRed));
                break;
            case "4":
                llDetailedSummary.setBackground(context.getResources().getDrawable(R.drawable.submitted_status_border));
                dsIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.submitted_status_icon));
                dsText.setTextColor(context.getResources().getColor(R.color.scoreGreen));
                dsStartBtn.setText(getResources().getString(R.string.text_submitted));
                dsStartBtn.setTextColor(context.getResources().getColor(R.color.scoreGreen));

                // if es  are  in N/A case then sign from here
                //  if(esStatus.equalsIgnoreCase("0"))
                //        goForSignature();   // need to change in future
                break;
            case "5":
                llDetailedSummary.setBackground(context.getResources().getDrawable(R.drawable.reviewed_status_border));
                dsIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.reviewed_status_icon));
                dsText.setTextColor(context.getResources().getColor(R.color.reviewedColor));
                dsStartBtn.setText(getResources().getString(R.string.text_reviewed));
                dsStartBtn.setTextColor(context.getResources().getColor(R.color.reviewedColor));
                break;
        }
    }

    private void setExecutiveSummary(){
        switch (esStatus){
            case "0":
                llExecutiveSummary.setBackground(context.getResources().getDrawable(R.drawable.na_status_border));
                esIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.na_status_icon));
                esText.setTextColor(context.getResources().getColor(R.color.textGrey));
                esStartBtn.setText(getResources().getString(R.string.text_na));
                esStartBtn.setTextColor(context.getResources().getColor(R.color.textGrey));
                break;
            case "1":
                llExecutiveSummary.setBackground(context.getResources().getDrawable(R.drawable.created_status_border));
                esIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.create_status_icon));
                esText.setTextColor(context.getResources().getColor(R.color.colorBlack));
                esStartBtn.setText(getResources().getString(R.string.text_start));
                esStartBtn.setTextColor(context.getResources().getColor(R.color.colorBlack));
                break;
            case "2":
                llExecutiveSummary.setBackground(context.getResources().getDrawable(R.drawable.resume_status_border));
                esIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.resume_status));
                esText.setTextColor(context.getResources().getColor(R.color.colorOrange));
                esStartBtn.setText(getResources().getString(R.string.text_resume));
                esStartBtn.setTextColor(context.getResources().getColor(R.color.colorOrange));
                break;
            case "3":
                llExecutiveSummary.setBackground(context.getResources().getDrawable(R.drawable.rejected_status_border));
                esIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.rejected_status_icon));
                esText.setTextColor(context.getResources().getColor(R.color.scoreRed));
                esStartBtn.setText(getResources().getString(R.string.text_rejected));
                esStartBtn.setTextColor(context.getResources().getColor(R.color.scoreRed));
                break;
            case "4":
                llExecutiveSummary.setBackground(context.getResources().getDrawable(R.drawable.submitted_status_border));
                esIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.submitted_status_icon));
                esText.setTextColor(context.getResources().getColor(R.color.scoreGreen));
                esStartBtn.setText(getResources().getString(R.string.text_submitted));
                esStartBtn.setTextColor(context.getResources().getColor(R.color.scoreGreen));

                // now both are completed so go for signature
                //  goForSignature();   // need to change in future
                break;
            case "5":
                llExecutiveSummary.setBackground(context.getResources().getDrawable(R.drawable.reviewed_status_border));
                esIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.reviewed_status_icon));
                esText.setTextColor(context.getResources().getColor(R.color.reviewedColor));
                esStartBtn.setText(getResources().getString(R.string.text_reviewed));
                esStartBtn.setTextColor(context.getResources().getColor(R.color.reviewedColor));
                break;
        }
    }

}
