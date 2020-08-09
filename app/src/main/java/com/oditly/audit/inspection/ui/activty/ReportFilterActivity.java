package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.SpinnerData;
import com.oditly.audit.inspection.model.filterData.AuditType;
import com.oditly.audit.inspection.model.filterData.AuditorBean;
import com.oditly.audit.inspection.model.filterData.DesignationBean;
import com.oditly.audit.inspection.model.filterData.FilterRootObject;
import com.oditly.audit.inspection.model.filterData.LocationBean;
import com.oditly.audit.inspection.model.filterData.TemplateBean;
import com.oditly.audit.inspection.model.teamData.TeamRootObject;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFilterActivity extends BaseActivity implements MultiSelectDialog.SubmitCallbackListener, INetworkEvent {

    private Calendar cal = Calendar.getInstance();
    int startYear = cal.get(Calendar.YEAR);
    int startMonth = cal.get(Calendar.MONTH);
    int startDay = cal.get(Calendar.DAY_OF_MONTH);
    int HourDay = cal.get(Calendar.HOUR_OF_DAY);
    int MinutDay = cal.get(Calendar.MINUTE);

    private TextView mDateFromTV;
    private TextView mDateToET;

    private EditText mLocationListET,mAuditorNameET,mAuditTypeET;

    private RelativeLayout mProgressBarRL;
    private List<LocationBean> mLocationList;
    private List<AuditType> mAuditTypeList;
    private List<AuditorBean> mAuditorNameList;
    private List<DesignationBean> mDesignationList;
    private List<TemplateBean> mTemplteList;
    private int mClick=0;
    private EditText mDesignationET;
    private EditText mTemplateET;

    private ArrayList<Integer> mSelectedAuditID,mSelectedUserNameID,mSelectedLocationID,mSelectedTemplateID,mSelectedDesignationID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_filter);

        initView();
        initVar();
        getFilterdataFromServer();

    }

    @Override
    protected void initView() {
        super.initView();

        findViewById(R.id.iv_header_left).setOnClickListener(this);
        TextView textView=(TextView)findViewById(R.id.tv_header_title);
        textView.setText(getString(R.string.s_reports));
        mDateFromTV=(TextView)findViewById(R.id.tv_date_from);
        mDateToET=(TextView)findViewById(R.id.tv_date_to);
        mProgressBarRL=(RelativeLayout)findViewById(R.id.ll_parent_progress);

        mAuditorNameET=(EditText) findViewById(R.id.et_auditor_name);
        mLocationListET=(EditText)findViewById(R.id.et_location);
        mAuditTypeET=(EditText)findViewById(R.id.et_audit_type);
        mTemplateET=(EditText)findViewById(R.id.et_template);
        mDesignationET=(EditText)findViewById(R.id.et_designation);



        mDateFromTV.setOnClickListener(this);
        mDateToET.setOnClickListener(this);

        mAuditorNameET.setOnClickListener(this);
        mLocationListET.setOnClickListener(this);
        mAuditTypeET.setOnClickListener(this);
        mDesignationET.setOnClickListener(this);
        mTemplateET.setOnClickListener(this);

        findViewById(R.id.tv_reset).setOnClickListener(this);
        findViewById(R.id.tv_done).setOnClickListener(this);



    }



    @Override
    protected void initVar() {
        super.initVar();
        mAuditTypeList=new ArrayList();
        mLocationList=new ArrayList();
        mAuditorNameList=new ArrayList();
        mDesignationList=new ArrayList();
        mTemplteList=new ArrayList();

        mSelectedAuditID=new ArrayList();
        mSelectedUserNameID=new ArrayList();
        mSelectedLocationID=new ArrayList();
        mSelectedTemplateID=new ArrayList();
        mSelectedDesignationID=new ArrayList();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.et_audit_type:
                mClick=1;
                ArrayList<MultiSelectModel> auditTypeArray = new ArrayList<>();

                for(int i=0;i<mAuditTypeList.size();i++)
                {
                    AuditType auditType=mAuditTypeList.get(i);
                    MultiSelectModel data=new MultiSelectModel(auditType.getType_id(),auditType.getName());
                    auditTypeArray.add(data);
                }
                getMultiSelectionDialog(auditTypeArray,getString(R.string.text_audit_type));
                break;
            case R.id.et_auditor_name:
                mClick=2;
                ArrayList<MultiSelectModel> dArray1 = new ArrayList<>();
                for(int i=0;i<mAuditorNameList.size();i++)
                {
                    AuditorBean bean=mAuditorNameList.get(i);
                    MultiSelectModel data1=new MultiSelectModel(bean.getAuditor_id(),bean.getAuditor_name());
                    dArray1.add(data1);
                }
                getMultiSelectionDialog(dArray1,getString(R.string.text_auditor_name));
                break;
            case R.id.et_location:
                mClick=3;
                ArrayList<MultiSelectModel> dArray2 = new ArrayList<>();
                for(int i=0;i<mLocationList.size();i++)
                {
                    LocationBean beanL=mLocationList.get(i);
                    MultiSelectModel data=new MultiSelectModel(beanL.getLocation_id(),beanL.getLocation_title() );
                    dArray2.add(data);
                }
                getMultiSelectionDialog(dArray2,getString(R.string.text_location));
            case R.id.et_template:
                mClick=4;
                ArrayList<MultiSelectModel> dArrayTem = new ArrayList<>();
                for(int i=0;i<mTemplteList.size();i++)
                {
                    TemplateBean beanL=mTemplteList.get(i);
                    MultiSelectModel data=new MultiSelectModel(beanL.getQuestionnaire_id(),beanL.getQuestionnaire_title());
                    dArrayTem.add(data);
                }
                getMultiSelectionDialog(dArrayTem,getString(R.string.text_template));
                break;
            case R.id.et_designation:
                mClick=5;
                ArrayList<MultiSelectModel> dArrayDeg = new ArrayList<>();
                for(int i=0;i<mDesignationList.size();i++)
                {
                    DesignationBean beanL=mDesignationList.get(i);
                    MultiSelectModel data=new MultiSelectModel(beanL.getCustom_role_id(),beanL.getCustom_role_name() );
                    dArrayDeg.add(data);
                }
                getMultiSelectionDialog(dArrayDeg,getString(R.string.text_designation));
                break;
            case R.id.tv_date_to:
                DatePickerDialog datePickerDialog1=  new DatePickerDialog(this, (datePicker, i, i1, i2) ->((TextView) view).setText(String.format("%02d/%02d",i2,(datePicker.getMonth() + 1))+"/" + datePicker.getYear()),startYear,startMonth,startDay);
                datePickerDialog1.show();
                break;
            case R.id.tv_date_from:
                DatePickerDialog datePickerDialog=  new DatePickerDialog(this, (datePicker, i, i1, i2) ->((TextView) view).setText(String.format("%02d/%02d",i2,(datePicker.getMonth() + 1))+"/" + datePicker.getYear()),startYear,startMonth,startDay);
                datePickerDialog.show();
                break;
            case R.id.iv_header_left:
                finish();
                break;
            case R.id.tv_done:
             //   audit_type_id,location_id,custom_role_id(designation),questionnaire_id(template),auditor_id,audit_to_date,audit_from_date

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(NetworkConstant.REQ_PARAM_TODATE, mDateToET.getText().toString());
                    jsonObject.put(NetworkConstant.REQ_PARAM_FROMDATE, mDateToET.getText().toString());
                    jsonObject.put(NetworkConstant.REQ_PARAM_AUDITTYPEID,new JSONArray(mSelectedAuditID));
                    jsonObject.put(NetworkConstant.REQ_PARAM_LOCATIONID,new JSONArray(mSelectedLocationID));
                    jsonObject.put(NetworkConstant.REQ_PARAM_TEMPLATEID,new JSONArray(mSelectedTemplateID));
                    jsonObject.put(NetworkConstant.REQ_PARAM_DESIGNATION, new JSONArray(mSelectedDesignationID));
                    jsonObject.put(NetworkConstant.REQ_PARAM_AUDITORID, new JSONArray(mSelectedUserNameID));
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(AppConstant.JSON_DATA, jsonObject.toString());
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
                catch (Exception e){
                  e.printStackTrace();
                }
                break;
            case R.id.tv_reset:
                mLocationListET.setText("All");
                mAuditorNameET.setText("All");
                mAuditTypeET.setText("All");
                mDesignationET.setText("All");
                mTemplateET.setText("All");
                break;

        }

    }

    private void getFilterdataFromServer()
    {
        if (NetworkStatus.isNetworkConnected(this)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            NetworkService networkService = new NetworkService(NetworkURL.GET_FILTER_DATA, NetworkConstant.METHOD_GET, this,this);
            networkService.call( new HashMap<String, String>());
        } else
        {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }


    private void getMultiSelectionDialog(ArrayList<MultiSelectModel> model,String filterName)
    {
        MultiSelectDialog multiSelectDialog = new MultiSelectDialog()
                .title(filterName) //setting title for dialog
                .titleSize(20)
                .positiveText("Done")
                .negativeText("Cancel")
                .setMinSelectionLimit(1) //you can set minimum checkbox selection limit (Optional)
                .setMaxSelectionLimit(model.size()) //you can set maximum checkbox selection limit (Optional)
                // .preSelectIDsList(alreadySelectedCountries) //List of ids that you need to be selected
                .multiSelectList(model) // the multi select model list with ids and name
                .onSubmit(this);

        multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");

    }


    @Override
    public void onSelected(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String dataString)
    {
       /* for (int i = 0; i < selectedIds.size(); i++) {
            Toast.makeText(ReportFilterActivity.this, "Selected Ids : " + selectedIds.get(i) + "\n" +
                    "Selected Names : " + selectedNames.get(i) + "\n" +
                    "DataString : " + dataString, Toast.LENGTH_SHORT).show();
        }*/

        if(mClick==1)
        {
            mSelectedAuditID.clear();
            mSelectedAuditID.addAll(selectedIds);
            mAuditTypeET.setText(selectedNames.toString());
        }
        else  if(mClick==2)
        {
            mSelectedUserNameID.clear();
            mSelectedUserNameID.addAll(selectedIds);
            mAuditorNameET.setText(selectedNames.toString());
        }
        else if(mClick==3)
        {
            mSelectedLocationID.clear();
            mSelectedLocationID.addAll(selectedIds);
            mLocationListET.setText(selectedNames.toString());
        }
        else if(mClick==4)
        {
            mSelectedTemplateID.clear();
            mSelectedTemplateID.addAll(selectedIds);
            mTemplateET.setText(selectedNames.toString());
        }
        else
        {
            mSelectedDesignationID.clear();
            mSelectedDesignationID.addAll(selectedIds);
            mDesignationET.setText(selectedNames.toString());
        }
    }

    @Override
    public void onCancel() {
        if(mClick==1)
            mAuditTypeET.setText("All");
        else  if(mClick==2)
            mAuditorNameET.setText("All");
        else  if(mClick==3)
            mLocationListET.setText("All");
        else  if(mClick==4)
            mTemplateET.setText("All");
        else
            mDesignationET.setText("All");


    }

    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response)
    {
        try {
            JSONObject object = new JSONObject(response);

            if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {

                FilterRootObject teamRootObject = new GsonBuilder().create().fromJson(object.toString(), FilterRootObject.class);
                Log.e("getAudit_types", "=====> size " + teamRootObject.getData().getAudit_types().size());
                Log.e("getLocations", "=====> size " + teamRootObject.getData().getLocations().size());
                Log.e("getAuditors", "=====> size " + teamRootObject.getData().getAuditors().size());
                if (teamRootObject.getData().getAuditors() != null && teamRootObject.getData().getAuditors().size() > 0)
                {
                    mAuditorNameList.addAll(teamRootObject.getData().getAuditors());
                    mAuditTypeList.addAll(teamRootObject.getData().getAudit_types());
                    mLocationList.addAll(teamRootObject.getData().getLocations());
                    mTemplteList.addAll(teamRootObject.getData().getQuestionnaires());
                    mDesignationList.addAll(teamRootObject.getData().getCustom_roles());


                } else
                    AppUtils.toast((BaseActivity) this, object.getString(AppConstant.RES_KEY_MESSAGE));
            } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                AppUtils.toast((BaseActivity) this, object.getString(AppConstant.RES_KEY_MESSAGE));
            }


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.toast(this, getString(R.string.oops));
        }
        mProgressBarRL.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        mProgressBarRL.setVisibility(View.VISIBLE);
        AppUtils.toast(this, getString(R.string.oops));
    }
}
