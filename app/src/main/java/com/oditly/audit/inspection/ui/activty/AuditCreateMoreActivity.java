package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

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

    int startHour = cal.get(Calendar.HOUR_OF_DAY);
    int startMinute = cal.get(Calendar.MINUTE);

    private RelativeLayout mProgressBarRL;
    private LinearLayout mTimeAMPMLL;
    private TextView mDueDateET;
    private TextView mStartDateET;
    private TextView mDueTimeET;
    private Spinner mReveawerSPN;
    private Spinner mAMPMSPN;
    private Spinner mHourSPN;
    private EditText mInspectionNameET,mBenchMarkET;
    private TextView mYesTV,mNoTV;
    private RelativeLayout mReviwerRL;
    private ArrayList<String> mReviewerList;
    private ArrayList<String> mReviewerListID;
    private String mReviewerID="";
    private String mHourStart="0";
    private TextView mCommenErrortTV;
    private TextView mBenchMarkErrortTV;
    private EditText mCommentET;
    private boolean isTimeInPM=false;


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
        findViewById(R.id.rl_hour).setOnClickListener(this);
        findViewById(R.id.rl_ampm).setOnClickListener(this);

        TextView textView=(TextView)findViewById(R.id.tv_header_title);
        textView.setText(getString(R.string.text_create_audit));
        mProgressBarRL=(RelativeLayout)findViewById(R.id.ll_parent_progress);
        mTimeAMPMLL=(LinearLayout) findViewById(R.id.ll_timeampm);

        mDueDateET =(TextView) findViewById(R.id.tv_duedate);
        mDueTimeET =(TextView) findViewById(R.id.tv_duetime);
        mStartDateET =(TextView) findViewById(R.id.tv_startdate);
        mInspectionNameET =(EditText) findViewById(R.id.et_inspection);
        mBenchMarkET =(EditText) findViewById(R.id.et_benchmark);
        mYesTV =(TextView) findViewById(R.id.tv_yes);
        mNoTV =(TextView) findViewById(R.id.tv_no);
        mReveawerSPN =(Spinner) findViewById(R.id.spn_reviewer);
        mAMPMSPN =(Spinner) findViewById(R.id.spn_ampm);
        mHourSPN =(Spinner) findViewById(R.id.spn_hour);
        mReviwerRL =(RelativeLayout) findViewById(R.id.rl_reviewer);
        mCommenErrortTV =(TextView) findViewById(R.id.tv_instructionerror);
        mCommentET = (EditText) findViewById(R.id.et_commentbox);
        mBenchMarkErrortTV=(TextView) findViewById(R.id.tv_benchmarkerror);
        mDueDateET.setOnClickListener(this);
        mDueTimeET.setOnClickListener(this);
        mStartDateET.setOnClickListener(this);
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

        mHourSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mHourStart=mHourSPN.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mAMPMSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mAMPMSPN.getSelectedItem().toString().equalsIgnoreCase("PM"))
                    isTimeInPM =true;
                else
                    isTimeInPM=false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mHourSPN.setEnabled(false);
        mAMPMSPN.setEnabled(false);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.ll_timeampm:
                if(TextUtils.isEmpty(mStartDateET.getText().toString()))
                    AppUtils.toast(this,getString(R.string.text_select_startdate_first));
                break;
            case R.id.tv_duedate:
                DatePickerDialog datePickerDialog1=  new DatePickerDialog(this, (datePicker, i, i1, i2) ->((TextView) view).setText(datePicker.getYear()+"-"+String.format("%02d-%02d",(datePicker.getMonth() + 1),i2)),startYear,startMonth,startDay);
                datePickerDialog1.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog1.show();
                break;
            case R.id.tv_startdate:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker v, int year, int monthOfYear, int dayOfMonth) {
                                ((TextView) view).setText(year+"-"+String.format("%02d-%02d",(monthOfYear + 1),dayOfMonth));
                                mHourSPN.setEnabled(true);
                                mAMPMSPN.setEnabled(true);
                            }
                        }, startYear, startMonth, startDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();


              /*  DatePickerDialog datePickerDialog=  new DatePickerDialog(this, (datePicker, i, i1, i2) -> ((TextView) view).setText(datePicker.getYear()+"-"+String.format("%02d-%02d",(datePicker.getMonth() + 1),i2)),startYear,startMonth,startDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();*/
                break;
            case R.id.tv_duetime:
                if(!TextUtils.isEmpty(mDueDateET.getText().toString())) {
                    TimePickerDialog mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            mDueTimeET.setText(selectedHour + ":" + selectedMinute + ":00");
                        }
                    }, startHour, startMinute, true);//Yes 24 hour time
                    mTimePicker.setTitle(getString(R.string.text_select_time));
                    mTimePicker.show();
                }
                else
                    AppUtils.toast(this,getString(R.string.text_select_duedate_first));
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
                int startTime=0;
                if (!mHourStart.contains("Select"))
                { if (isTimeInPM) {
                    if (mHourStart.equalsIgnoreCase("12"))
                        startTime =0;
                    else
                        startTime = Integer.parseInt(mHourStart) + 12;
                }
                else
                    startTime=Integer.parseInt(mHourStart);
                }
                int benchmarkCount=0;
                String benchmark=mBenchMarkET.getText().toString();
                if (!TextUtils.isEmpty(benchmark))
                    benchmarkCount =Integer.parseInt(benchmark);
                if(benchmarkCount<=100)
                {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(AppConstant.AUDIT_NAME, mInspectionNameET.getText().toString());
                    returnIntent.putExtra(AppConstant.AUDIT_INSTRUCTION, mCommentET.getText().toString());
                    returnIntent.putExtra(AppConstant.AUDIT_DATE, mDueDateET.getText().toString()+" "+mDueTimeET.getText().toString());
                    returnIntent.putExtra(AppConstant.AUDIT_BENCHMARK, mBenchMarkET.getText().toString());
                    returnIntent.putExtra(AppConstant.AUDIT_STARTDATE,mStartDateET.getText().toString());
                    returnIntent.putExtra(AppConstant.AUDIT_STARTHOUR,""+startTime);
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
