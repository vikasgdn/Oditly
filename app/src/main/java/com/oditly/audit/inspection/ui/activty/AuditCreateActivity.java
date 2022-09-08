package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.app.MediaRouteButton;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.team.TeamMemberListAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.model.audit.createaudit.AditorReviewBean;
import com.oditly.audit.inspection.model.audit.createaudit.AuditFilterRootObject;
import com.oditly.audit.inspection.model.filterData.AuditType;
import com.oditly.audit.inspection.model.filterData.AuditorBean;
import com.oditly.audit.inspection.model.filterData.FilterRootObject;
import com.oditly.audit.inspection.model.filterData.TemplateBean;
import com.oditly.audit.inspection.model.teamData.TeamMemberRootObject;
import com.oditly.audit.inspection.model.teamData.TeamMembers;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkServiceJSON;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuditCreateActivity extends BaseActivity implements INetworkEvent,MultiSelectDialog.SubmitCallbackListener {


    private RelativeLayout mProgressBarRL;
    private Spinner mAuditTypeSPN,mTemplateSPN,mLocationSPN;
    private List<String> mLocationList,mLocationListID;
    private ArrayList<String> mReviewerList,getmReviewerListID;

    private List<String> mAuditsTypeList,mAuditTypeIDList;
    private List<String> mTemplateTypeList,mTemplateTypeIDList;
    private EditText mAuditorNameET;
    private List<AditorReviewBean> mAuditorNameList;
    private String mBenchMark="",mInstruction="",mAuditName="",mAuditDate="",mReviewerID="",isReviewer="false";
    private String mLocatioID;

    private String mAuditID="";
    private String mTemplateID="";
    private ArrayAdapter mLocationAdapter;
    private String mAuditStartDate="";
    private ArrayList<MultiSelectModel> mMultiSelectModelsList;
    private ArrayList<Integer> mAuditorsIDSelected;
    private String mAuditStartHour="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_audit);
        initView();
        initVar();
        getLocationFilterListFromServer();

    }

    @Override
    protected void initView() {
        super.initView();

        findViewById(R.id.iv_header_left).setOnClickListener(this);
        findViewById(R.id.btn_create).setOnClickListener(this);
        findViewById(R.id.tv_moreoptions).setOnClickListener(this);

        TextView textView=(TextView)findViewById(R.id.tv_header_title);
        textView.setText(getString(R.string.text_create_audit));
        mProgressBarRL=(RelativeLayout)findViewById(R.id.ll_parent_progress);

        mAuditTypeSPN=(Spinner) findViewById(R.id.spn_audittype);
        mLocationSPN=(Spinner) findViewById(R.id.spn_locationtype);
        mTemplateSPN=(Spinner) findViewById(R.id.spn_template);
        mAuditorNameET=(EditText) findViewById(R.id.et_auditor_name);
        mAuditorNameET.setOnClickListener(this);

        mAuditorNameET.setText(AppPreferences.INSTANCE.getUserFname(this)+" "+AppPreferences.INSTANCE.getUserLName(this));
    }

    @Override
    protected void initVar() {
        super.initVar();

        mLocationList=new ArrayList<>();
        mLocationListID=new ArrayList<>();
        mMultiSelectModelsList=new ArrayList<>();
        mAuditorNameList =new ArrayList<>();

        mReviewerList=new ArrayList<>();
        getmReviewerListID=new ArrayList<>();

        mAuditsTypeList=new ArrayList<>();
        mAuditTypeIDList=new ArrayList<>();

        mTemplateTypeIDList=new ArrayList<>();
        mTemplateTypeList=new ArrayList<>();
        mAuditorsIDSelected=new ArrayList<>();

        mLocationSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mLocationListID!=null && mLocationListID.size()>0) {
                    mLocatioID = mLocationListID.get(position);
                    mTemplateID ="";
                    mAuditID ="";
                    getFilterListFromServer(mLocatioID);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mAuditTypeSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mAuditTypeIDList!=null && mAuditTypeIDList.size()>0)
                mAuditID = mAuditTypeIDList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mTemplateSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mTemplateTypeIDList!=null && mTemplateTypeIDList.size()>0)
                    mTemplateID = mTemplateTypeIDList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.iv_header_left:
                finish();
                break;
            case R.id.tv_moreoptions:
                String auditorname1 = mAuditorNameET.getText().toString();
                if (!TextUtils.isEmpty(auditorname1) && !auditorname1.equalsIgnoreCase("All")) {
                    if (mMultiSelectModelsList.size()<=0)
                        populateMultiSelectData();

                    Intent intent = new Intent(this, AuditCreateMoreActivity.class);
                    Bundle args = new Bundle();
                    args.putStringArrayList(AppConstant.REVIEWRLIST, mReviewerList);
                    args.putStringArrayList(AppConstant.REVIEWRLISTID, getmReviewerListID);
                    intent.putExtra("BUNDLE", args);
                    startActivityForResult(intent, 1001);
                }
                else
                    AppUtils.toast(this,getString(R.string.text_selectreviwer));
                break;
            case R.id.btn_create:
                Log.e("USER ID ",""+AppPreferences.INSTANCE.getUserId(this));
                String auditorname = mAuditorNameET.getText().toString();
                String locationName = mAuditorNameET.getText().toString();

                if (!TextUtils.isEmpty(mTemplateID)) {
                    if (mAuditorsIDSelected.size()==0)
                        mAuditorsIDSelected.add(AppPreferences.INSTANCE.getUserId(this));
                    postAuditCreateServerData();
                }
                else
                    AppUtils.toast(this,getString(R.string.text_selecttemplet));
                break;
            case R.id.et_auditor_name:
                populateMultiSelectData();
                getMultiSelectionDialog(mMultiSelectModelsList,getString(R.string.text_assigneeselect));
                break;
        }

    }

    private void populateMultiSelectData()
    {
        mMultiSelectModelsList.clear();
        mReviewerList.clear();
        getmReviewerListID.clear();
        for(int i=0;i<mAuditorNameList.size();i++)
        {
            AditorReviewBean bean=mAuditorNameList.get(i);
            mReviewerList.add(bean.getName());
            getmReviewerListID.add(""+bean.getUser_id());
            MultiSelectModel data1=new MultiSelectModel(bean.getUser_id(),bean.getName());
            mMultiSelectModelsList.add(data1);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK) {
            if (data != null)
            {
                mInstruction=data.getStringExtra(AppConstant.AUDIT_INSTRUCTION);
                mAuditName=data.getStringExtra(AppConstant.AUDIT_NAME);
                mAuditDate=data.getStringExtra(AppConstant.AUDIT_DATE).trim();
                mAuditStartDate=data.getStringExtra(AppConstant.AUDIT_STARTDATE);
                mAuditStartHour=data.getStringExtra(AppConstant.AUDIT_STARTHOUR);
                mReviewerID=data.getStringExtra(AppConstant.AUDIT_REVIEWERID);
                mBenchMark=data.getStringExtra(AppConstant.AUDIT_BENCHMARK);
            }

            Log.e("More DATA==> ",""+mInstruction+" || "+mReviewerID+" || "+mBenchMark+" || "+mAuditDate );        }
    }

    private void getLocationFilterListFromServer()
    {
        if (NetworkStatus.isNetworkConnected(this)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            NetworkService networkService = new NetworkService(NetworkURL.AUDIT_LOCATION_LIST, NetworkConstant.METHOD_GET, this,this);
            networkService.call( new HashMap<String, String>());
        } else
        {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }
    private void getFilterListFromServer(String locationid)
    {
        if (NetworkStatus.isNetworkConnected(this)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            String url=NetworkURL.GET_AUDITCREATEFILTER_URL+locationid;
            Log.e("Filter url==> ",""+url);
            NetworkService networkService = new NetworkService(url, NetworkConstant.METHOD_GET, this,this);
            networkService.call( new HashMap<String, String>());
        } else
        {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }
    private void postAuditCreateServerData()
    {
        if (NetworkStatus.isNetworkConnected(this)) {

            // showAppProgressDialog();
            mProgressBarRL.setVisibility(View.VISIBLE);

            JSONArray jsArray=  new JSONArray(mAuditorsIDSelected);

            ArrayList<String> reviewerList=new ArrayList();
            reviewerList.add(mReviewerID);

            try {
                JSONObject params = new JSONObject();
                params.put(NetworkConstant.REQ_PARAM_MOBILE, "1");
                params.put(NetworkConstant.REQ_PARAM_LOCATIONID, mLocatioID);
                params.put(NetworkConstant.REQ_PARAM_TEMPLATEID, mTemplateID);
                params.put(NetworkConstant.REQ_PARAM_AUDITTYPEID, mAuditID);
                params.put(NetworkConstant.REQ_PARAM_AUDITORID, jsArray);
                if(!TextUtils.isEmpty(mAuditName))
                    params.put(NetworkConstant.REQ_PARAM_AUDITNAME, mAuditName);
                if(!TextUtils.isEmpty(mReviewerID))
                    params.put(NetworkConstant.REQ_PARAM_REVIEWERID, new JSONArray(reviewerList));
                if(!TextUtils.isEmpty(mAuditDate))
                    params.put(NetworkConstant.REQ_PARAM_DUEDATE,mAuditDate);
                if(!TextUtils.isEmpty(mAuditStartDate))
                    params.put(NetworkConstant.REQ_PARAM_AUDITSTARTDATE, mAuditStartDate);
                if(!TextUtils.isEmpty(mAuditStartHour) && !TextUtils.isEmpty(mAuditStartDate))
                    params.put(NetworkConstant.REQ_PARAM_AUDITSTARTHOUR, mAuditStartHour);
                if(!TextUtils.isEmpty(mBenchMark))
                    params.put(NetworkConstant.REQ_PARAM_BENCHMARK,mBenchMark);
                if(!TextUtils.isEmpty(mInstruction))
                    params.put(NetworkConstant.REQ_PARAM_INSTRUCTION,mInstruction);
                NetworkServiceJSON networkService = new NetworkServiceJSON(NetworkURL.POST_AUDIT_ADD_URL, NetworkConstant.METHOD_POST, this, this);
                networkService.call(params);
            }
            catch (Exception e)
            {
                //AppUtils.toast(this, getString(R.string.internet_error));
                e.printStackTrace(); }
        } else
            AppUtils.toast(this, getString(R.string.internet_error));

    }


    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response)
    {
        Log.e("Response==>",""+response);
         if(service.equalsIgnoreCase(NetworkURL.AUDIT_LOCATION_LIST))
         {
            try{
                mLocationListID.clear();
                mLocationList.clear();
                JSONObject jsonObject = new JSONObject(response);
               JSONObject childOBJ = jsonObject.getJSONObject("data");
                JSONArray  jsonArray = childOBJ.optJSONArray("locations");
              for(int i=0;i<jsonArray.length();i++)
              {
                  JSONObject obj=jsonArray.optJSONObject(i);
                  mLocationList.add(obj.optString("location_name"));
                  mLocationListID.add(""+obj.optInt("location_id"));
              }
                mLocationAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mLocationList);
                mLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mLocationSPN.setAdapter(mLocationAdapter);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
         }
       else if(service.equalsIgnoreCase(NetworkURL.POST_AUDIT_ADD_URL)) {

            try {
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                  //  AppUtils.toastDisplayForLong(this, message);
                    if (TextUtils.isEmpty(mAuditStartDate))
                        AppDialogs.messageDialogWithOKButton(this,mAuditName+" "+getString(R.string.text_inspection_hasbeen_created_and_assigned));
                    else
                        AppDialogs.messageDialogWithOKButton(this,mAuditName+" "+getString(R.string.text_inspection_hasbeen_created_and_scheduled));

                } else
                    AppUtils.toastDisplayForLong(this, message);
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.toastDisplayForLong(this, getString(R.string.oops));
            }
        }
        else
        {
            try {
                JSONObject object = new JSONObject(response);

                if (!object.getBoolean(AppConstant.RES_KEY_ERROR))
                {
                    mAuditTypeIDList.clear();
                    mAuditsTypeList.clear();
                    mTemplateTypeIDList.clear();
                    mTemplateTypeList.clear();
                    mAuditorNameList.clear();
                    mMultiSelectModelsList.clear();

                    AuditFilterRootObject teamRootObject = new GsonBuilder().create().fromJson(object.toString(), AuditFilterRootObject.class);
                    if (teamRootObject.getData().getAuditors() != null && teamRootObject.getData().getAuditors().size() > 0) {
                        mAuditorNameList.addAll(teamRootObject.getData().getAuditors());
                        for (int i = 0; i < teamRootObject.getData().getAudit_types().size(); i++) {
                            AuditType atype = teamRootObject.getData().getAudit_types().get(i);
                            mAuditTypeIDList.add("" + atype.getType_id());
                            mAuditsTypeList.add("" + atype.getName());
                        }
                        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mAuditsTypeList);
                        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mAuditTypeSPN.setAdapter(ad);


                        for (int i = 0; i < teamRootObject.getData().getQuestionnaires().size(); i++) {
                            TemplateBean atype = teamRootObject.getData().getQuestionnaires().get(i);
                            mTemplateTypeIDList.add("" + atype.getQuestionnaire_id());
                            mTemplateTypeList.add("" + atype.getQuestionnaire_title());
                        }
                        ArrayAdapter ad1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mTemplateTypeList);
                        ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mTemplateSPN.setAdapter(ad1);

                    } else
                        AppUtils.toast((BaseActivity) this, object.getString(AppConstant.RES_KEY_MESSAGE));
                } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    AppUtils.toast((BaseActivity) this, object.getString(AppConstant.RES_KEY_MESSAGE));
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.toast(this, getString(R.string.oops));
            }
        }

        mProgressBarRL.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage)
    {
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(this, getString(R.string.oops));
        mProgressBarRL.setVisibility(View.GONE);

    }


    @Override
    public void onSelected(ArrayList<Integer> id, ArrayList<String> name, String data) {
        mAuditorsIDSelected.clear();
        Log.e(";;;;;;;;;;;;;;;;;;;   ",name.toString());
        mAuditorNameET.setText(name.toString());
        mAuditorsIDSelected.addAll(id);
    }

    @Override
    public void onCancel()
    {


    }
}
