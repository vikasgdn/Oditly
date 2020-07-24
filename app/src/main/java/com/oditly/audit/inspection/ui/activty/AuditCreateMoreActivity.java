package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AuditCreateMoreActivity extends BaseActivity {

    private Calendar cal = Calendar.getInstance();
    int startYear = cal.get(Calendar.YEAR);
    int startMonth = cal.get(Calendar.MONTH);
    int startDay = cal.get(Calendar.DAY_OF_MONTH);

    private RelativeLayout mProgressBarRL;
    private TextView mDueDateET;
    private Spinner mReveawerSPN;
    private EditText mInspectionNameET,mBenchMarkET;
    private TextView mYesTV,mNoTV;
    private Spinner mReviewerSPN;
    private RelativeLayout mReviwerRL;
   private ArrayList<String> mReviewerList;
   private ArrayList<String> mReviewerListID;
   private String mReviewerID="";
    private TextView mCommenErrortTV;
    private TextView mBenchMarkErrortTV;
    private EditText mCommentET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_audit_more);
        initView();
        initVar();
      //  getTeamListFromServer(teamId);

    }

    @Override
    protected void initView() {
        super.initView();

        findViewById(R.id.iv_header_left).setOnClickListener(this);
        findViewById(R.id.btn_done).setOnClickListener(this);

        TextView textView=(TextView)findViewById(R.id.tv_header_title);
        textView.setText(getString(R.string.text_create_audit));
        mProgressBarRL=(RelativeLayout)findViewById(R.id.ll_parent_progress);

        mDueDateET =(TextView) findViewById(R.id.tv_duedate);
        mInspectionNameET =(EditText) findViewById(R.id.et_inspection);
        mBenchMarkET =(EditText) findViewById(R.id.et_benchmark);
        mYesTV =(TextView) findViewById(R.id.tv_yes);
        mNoTV =(TextView) findViewById(R.id.tv_no);
        mReveawerSPN =(Spinner) findViewById(R.id.spn_reviewer);
        mReviwerRL =(RelativeLayout) findViewById(R.id.rl_reviewer);
        mCommenErrortTV =(TextView) findViewById(R.id.tv_instructionerror);
         mCommentET = (EditText) findViewById(R.id.et_commentbox);
        mBenchMarkErrortTV=(TextView) findViewById(R.id.tv_benchmarkerror);
        mDueDateET.setOnClickListener(this);
        mYesTV.setOnClickListener(this);
        mNoTV.setOnClickListener(this);


    }

    @Override
    protected void initVar() {
        super.initVar();
        Bundle args = getIntent().getBundleExtra("BUNDLE");
        mReviewerList = args.getStringArrayList(AppConstant.REVIEWRLIST);
        mReviewerListID= args.getStringArrayList(AppConstant.REVIEWRLISTID);

        if(mReviewerList!=null)
        {
            ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mReviewerList);
            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mReveawerSPN.setAdapter(ad);

            mReveawerSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mReviewerID=mReviewerListID.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

      //  Log.e(";;;;;;;;;; ",mReviewerList.toString());
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.tv_duedate:
                DatePickerDialog datePickerDialog1=  new DatePickerDialog(this, (datePicker, i, i1, i2) ->((TextView) view).setText(datePicker.getYear()+"-"+String.format("%02d-%02d",(datePicker.getMonth() + 1),i2)),startYear,startMonth,startDay);
                datePickerDialog1.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog1.show();
                break;
            case R.id.iv_header_left:
                finish();
                break;
            case R.id.tv_yes:
                    mReviwerRL.setVisibility(View.VISIBLE);
                    mYesTV.setBackgroundResource(R.drawable.tv_shape_green_bg);
                    mYesTV.setTextColor(getResources().getColor(R.color.c_white));

                mNoTV.setBackgroundResource(R.drawable.tv_shape_blue_border);
                mNoTV.setTextColor(getResources().getColor(R.color.c_blue));
                break;
            case R.id.tv_no:
                mReviwerRL.setVisibility(View.GONE);
                mNoTV.setTextColor(getResources().getColor(R.color.c_white));
                mNoTV.setBackgroundResource(R.drawable.button_bg_red);

                mYesTV.setBackgroundResource(R.drawable.tv_shape_blue_border);
                mYesTV.setTextColor(getResources().getColor(R.color.c_blue));
                break;
            case R.id.btn_done:
                int benchmarkCount=0;
                String benchmark=mBenchMarkET.getText().toString();
                if (!TextUtils.isEmpty(benchmark))
                       benchmarkCount =Integer.parseInt(benchmark);
                if(benchmarkCount<=100)
                {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(AppConstant.AUDIT_NAME, mInspectionNameET.getText().toString());
                    returnIntent.putExtra(AppConstant.AUDIT_INSTRUCTION, mCommentET.getText().toString());
                    returnIntent.putExtra(AppConstant.AUDIT_DATE, mDueDateET.getText().toString());
                    returnIntent.putExtra(AppConstant.AUDIT_BENCHMARK, mBenchMarkET.getText().toString());
                   returnIntent.putExtra(AppConstant.AUDIT_STARTDATE,"");
                    returnIntent.putExtra(AppConstant.AUDIT_REVIEWERID, mReviewerID);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
               else
                   mBenchMarkErrortTV.setVisibility(View.VISIBLE);
                break;
        }

    }

}
