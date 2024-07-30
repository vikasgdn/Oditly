package com.oditly.audit.inspection.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.team.TeamListAdapter;
import com.oditly.audit.inspection.adapter.team.TemplateListAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.interfaces.OnRecyclerViewItemClickListener;
import com.oditly.audit.inspection.model.audit.createaudit.AditorReviewBean;
import com.oditly.audit.inspection.model.audit.createaudit.AuditFilterRootObject;
import com.oditly.audit.inspection.model.filterData.AuditType;
import com.oditly.audit.inspection.model.filterData.TemplateBean;
import com.oditly.audit.inspection.model.teamData.TeamList;
import com.oditly.audit.inspection.model.template.TemplateList;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkServiceJSON;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.ui.activty.AddTeamMemberActivity;
import com.oditly.audit.inspection.ui.activty.AuditSubSectionsActivity;
import com.oditly.audit.inspection.ui.activty.BaseActivity;
import com.oditly.audit.inspection.ui.activty.BrandStandardAuditActivity;
import com.oditly.audit.inspection.ui.activty.BrandStandardAuditActivityPagingnation;
import com.oditly.audit.inspection.ui.activty.BrandStandardOptionsBasedQuestionActivity;
import com.oditly.audit.inspection.ui.activty.TeamMemberDisplayActivity;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TemplateListFragment extends BaseFragment implements View.OnClickListener, OnRecyclerViewItemClickListener, INetworkEvent,MultiSelectDialog.SubmitCallbackListener {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private RecyclerView mTeamListRV;
    private RelativeLayout mProgressBarRL;
    private ArrayList<TemplateList> mTeamListBean;
    private TemplateListAdapter mAddTeamListAdapter;
    private RelativeLayout mNoDataFoundRL;

    private List<String> mAuditsTypeList,mAuditTypeIDList;
    private List<String> mLocationList,mLocationListID;

    private ArrayAdapter mLocationAdapter;
    private ArrayAdapter mAudittTypeAdapter;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 4;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private int mCurrentPage=1;
    private int mTotalPage=1;
    private String mTemplateListURL="";

    private List<AditorReviewBean> mAuditorNameList;
    private ArrayList<String> mReviewerList,getmReviewerListID;
    private EditText mAuditorNameET;
    private ArrayList<Integer> mAuditorsIDSelected;
    private ArrayList<MultiSelectModel> mMultiSelectModelsList;


    public static TemplateListFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TemplateListFragment fragment = new TemplateListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_template_list, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
        initVar();
        int roleId= AppPreferences.INSTANCE.getUserRole(mActivity);
        if (roleId==280)
            AppUtils.toast(mActivity,getString(R.string.text_youdonthaveaccess_template));
        else
            getTeamListFromServer();


    }



    @Override
    protected void initView(View view) {
        super.initView(view);

        mTeamListRV=(RecyclerView)view.findViewById(R.id.rv_teamlist);
        mProgressBarRL=(RelativeLayout)view.findViewById(R.id.ll_parent_progress);
        mNoDataFoundRL=(RelativeLayout)view.findViewById(R.id.rl_nodatafound);

    }
    @Override
    protected void initVar() {
        super.initVar();
        mTemplateListURL=NetworkURL.GET_TEMPLATE_LIST;
        mTeamListBean=new ArrayList<>();

        mLocationList=new ArrayList<>();
        mLocationListID=new ArrayList<>();

        mAuditsTypeList=new ArrayList<>();
        mAuditTypeIDList=new ArrayList<>();


        mMultiSelectModelsList=new ArrayList<>();
        mAuditorNameList =new ArrayList<>();

        mReviewerList=new ArrayList<>();
        getmReviewerListID=new ArrayList<>();
        mAuditorsIDSelected=new ArrayList<>();


        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTeamListRV.setLayoutManager(mLayoutManager);


        mAddTeamListAdapter=new TemplateListAdapter(mActivity,mTeamListBean,this);
        mTeamListRV.setAdapter(mAddTeamListAdapter);
        mTeamListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mTeamListRV.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // Toast.makeText(getActivity()," END PAGE",Toast.LENGTH_SHORT).show();
                    if (mTotalPage>mCurrentPage) {
                        mCurrentPage++;
                         mTemplateListURL = NetworkURL.GET_TEMPLATE_LIST + "&page=" + mCurrentPage + "";
                        getTeamListFromServer(); //scheduled
                    }
                    loading = true;
                }
            }
        });
    }
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            /*case R.id.et_auditor_name:
                populateMultiSelectData();
                getMultiSelectionDialog(mMultiSelectModelsList,getString(R.string.text_assigneeselect));
                break;*/

        }

    }
    private void getTeamListFromServer()
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            System.out.println("==> mTemplateListURL==>  "+mTemplateListURL);
            NetworkService networkService = new NetworkService(mTemplateListURL, NetworkConstant.METHOD_GET, this,mActivity);
            networkService.call( new HashMap<String, String>());
        } else
        {
            AppUtils.toast(mActivity, mActivity.getString(R.string.internet_error));

        }
    }

    private void getTemplateCreateListFromServer()
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            String listURL=NetworkURL.GET_TEMPLATE_CREATELIST+""+mQuestioneriesID;
            NetworkService networkService = new NetworkService(listURL, NetworkConstant.METHOD_GET, this,mActivity);
            networkService.call( new HashMap<String, String>());
        } else
        {
            AppUtils.toast(mActivity, mActivity.getString(R.string.internet_error));

        }
    }
    //Payload: {audit_type_id, auditor_id, location_id, questionnaire_id}

    private void postCreateTemplate()
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            try {
                JSONArray jsArray=  new JSONArray(mAuditorsIDSelected);

                JSONObject params = new JSONObject();
                params.put("audit_type_id", Integer.parseInt(mAuditID));
                params.put("auditor_id", AppPreferences.INSTANCE.getUserId(mActivity));
                params.put("location_id", Integer.parseInt(mLocationID));
                params.put("questionnaire_id", mQuestioneriesID);
                params.put("completion_notify_user_ids", jsArray);
                NetworkServiceJSON networkService = new NetworkServiceJSON(NetworkURL.POST_TEMPLATE_CREATE, NetworkConstant.METHOD_POST, this, mActivity);
                networkService.call(params);
            }
            catch (Exception e){}
        } else
        {
            AppUtils.toast(mActivity, mActivity.getString(R.string.internet_error));

        }
    }



    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response)
    {
        Log.e("===> Response==>"+service,""+response);
        mProgressBarRL.setVisibility(View.GONE);

        if (service.equalsIgnoreCase(mTemplateListURL)) {

            try {
                mNoDataFoundRL.setVisibility(View.GONE);
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {

                    mTotalPage=(object.getInt("rows")/object.getInt("limit"))+1;

                    JSONArray array = object.optJSONArray("data");
                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object1 = array.getJSONObject(i);
                            TemplateList teamList = new TemplateList();
                            teamList.setQuestionnaire_id(object1.optInt("questionnaire_id"));
                            teamList.setClient_id(object1.optInt("client_id"));
                            teamList.setQuestionnaire_title(object1.optString("questionnaire_title"));
                            teamList.setUpdated_on(object1.optString("updated_on"));
                            teamList.setCreated_by_name(object1.optString("created_by_name"));
                            mTeamListBean.add(teamList);
                        }
                        mAddTeamListAdapter.notifyDataSetChanged();
                    } else
                        mNoDataFoundRL.setVisibility(View.VISIBLE);


                } else {
                    AppUtils.toast(mActivity, message);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                AppUtils.toast(mActivity, mActivity.getString(R.string.oops));
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.toast(mActivity, mActivity.getString(R.string.oops));
            }
        }
        else if (service.equalsIgnoreCase(NetworkURL.POST_TEMPLATE_CREATE))
        {
          //{"error":false,"data":{"audit_id":2688181},"message":"Inspection created"}
            try {
                JSONObject object = new JSONObject(response);

                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR))
                {
                    JSONObject cheildData = object.getJSONObject("data");

                    Intent startAudit = new Intent(mActivity, AuditSubSectionsActivity.class);
                   // startAudit.putExtra(AppConstant.BRAND_NAME, auditInfo.getBrand_name());
                   // startAudit.putExtra(AppConstant.LOCATION_NAME, auditInfo.getLocation_title());
                    startAudit.putExtra(AppConstant.AUDIT_NAME, cheildData.optString("audit_name"));
                    startAudit.putExtra(AppConstant.AUDIT_ID, "" + cheildData.optString("audit_id"));
                    //startAudit.putExtra(AppConstant.BS_STATUS, "" + auditInfo.getBrand_std_status());
                    mActivity.startActivity(startAudit);
                }
                else {
                    AppUtils.toast(mActivity, message);
                }

            }
            catch (Exception e){}

        }
        else  if (service.contains(NetworkURL.GET_TEMPLATE_CREATELIST))
        {

            try{
                mLocationListID.clear();
                mLocationList.clear();

                mAuditTypeIDList.clear();
                mAuditsTypeList.clear();

                JSONObject jsonObject = new JSONObject(response);
                JSONObject childOBJ = jsonObject.getJSONObject("data");
                JSONArray  jsonArrayLocations = childOBJ.optJSONArray("locations");
                JSONArray  jsonArrayAuditType = childOBJ.optJSONArray("audit_types");
                for(int i=0;i<jsonArrayLocations.length();i++)
                {
                    JSONObject obj=jsonArrayLocations.optJSONObject(i);
                    mLocationList.add(obj.optString("location_title"));
                    mLocationListID.add(""+obj.optInt("location_id"));
                }

                for(int i=0;i<jsonArrayAuditType.length();i++)
                {
                    JSONObject obj=jsonArrayAuditType.optJSONObject(i);
                    mAuditsTypeList.add(obj.optString("type_name"));
                    mAuditTypeIDList.add(""+obj.optString("type_id"));
                }

              showTemplateCreateDialog();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        else {
            try {
                JSONObject object = new JSONObject(response);

                if (!object.getBoolean(AppConstant.RES_KEY_ERROR))
                {

                    AuditFilterRootObject teamRootObject = new GsonBuilder().create().fromJson(object.toString(), AuditFilterRootObject.class);
                    if (teamRootObject.getData().getAuditors() != null && teamRootObject.getData().getAuditors().size() > 0) {
                        mAuditorNameList.clear();
                        mAuditorNameList.addAll(teamRootObject.getData().getAuditors());

                    } else
                        AppUtils.toast(mActivity, object.getString(AppConstant.RES_KEY_MESSAGE));
                } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    AppUtils.toast(mActivity, object.getString(AppConstant.RES_KEY_MESSAGE));
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.toast(mActivity, getString(R.string.oops));
            }
        }


    }

    @Override
    public void onNetworkCallError(String service, String errorMessage)
    {
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(mActivity, mActivity.getString(R.string.oops));
        mProgressBarRL.setVisibility(View.GONE);

    }


    @Override
    public void onItemClick(RecyclerView.Adapter adapter, View v, int position)
    {
        mQuestioneriesID=mTeamListBean.get(position).getQuestionnaire_id();
        getTemplateCreateListFromServer();

    }



    String mLocationID="",mAuditID="";
    int mQuestioneriesID=0;

    public void showTemplateCreateDialog() {
        final Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_template);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (mActivity.getResources().getDisplayMetrics().widthPixels - mActivity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Spinner mLocationSPN=dialog.findViewById(R.id.spn_locationtype);
        Spinner mAuditTypeSPN=dialog.findViewById(R.id.spn_audittype);

        mAuditorNameET=(EditText) dialog.findViewById(R.id.et_auditor_name);
        mAuditorNameET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        populateMultiSelectData();
                    }
                        },300);
            }
        });



        mLocationAdapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_item, mLocationList);
        mLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationSPN.setAdapter(mLocationAdapter);


        mAudittTypeAdapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_item, mAuditsTypeList);
        mAudittTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAuditTypeSPN.setAdapter(mAudittTypeAdapter);

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

        mLocationSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mLocationListID!=null && mLocationListID.size()>0) {
                    mLocationID = mLocationListID.get(position);
                    getFilterListFromServer(mLocationID);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        try {
            dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_create).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    postCreateTemplate();
                    dialog.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }

    private void getFilterListFromServer(String locationid)
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            String url=NetworkURL.GET_AUDITCREATEFILTER_URL+locationid;
            Log.e("Filter url==> ",""+url);
            NetworkService networkService = new NetworkService(url, NetworkConstant.METHOD_GET, this,mActivity);
            networkService.call( new HashMap<String, String>());
        } else
        {
            AppUtils.toast(mActivity, getString(R.string.internet_error));

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
            String name=(bean.getName()+"\n"+bean.getEmail()+"\n"+(bean.getCustom_role_name()==null?"":bean.getCustom_role_name()));
            mReviewerList.add(name);
            getmReviewerListID.add(""+bean.getUser_id());
            MultiSelectModel data1=new MultiSelectModel(bean.getUser_id(),name);
            mMultiSelectModelsList.add(data1);
        }
        Log.e("list size===>","=====> "+mMultiSelectModelsList.size());
        getMultiSelectionDialog(mMultiSelectModelsList,getString(R.string.text_assigneeselect));
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
                //.preSelectIDsList(alreadySelectedCountries) //List of ids that you need to be selected
                .multiSelectList(model) // the multi select model list with ids and name
                .onSubmit(this);

        multiSelectDialog.show(getChildFragmentManager(), "multiSelectDialog");

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


