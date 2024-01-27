package com.oditly.audit.inspection.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.team.TeamListAdapter;
import com.oditly.audit.inspection.adapter.team.TemplateListAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.interfaces.OnRecyclerViewItemClickListener;
import com.oditly.audit.inspection.model.audit.createaudit.AditorReviewBean;
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

public class TemplateListFragment extends BaseFragment implements View.OnClickListener, OnRecyclerViewItemClickListener, INetworkEvent {
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
        View view = inflater.inflate(R.layout.fragment_teamlist, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
        initVar();
        getTeamListFromServer();
    }



    @Override
    protected void initView(View view) {
        super.initView(view);

        mTeamListRV=(RecyclerView)view.findViewById(R.id.rv_teamlist);
        mProgressBarRL=(RelativeLayout)view.findViewById(R.id.ll_parent_progress);
        mNoDataFoundRL=(RelativeLayout)view.findViewById(R.id.rl_nodatafound);
        view.findViewById(R.id.iv_add).setOnClickListener(this);
    }
    @Override
    protected void initVar() {
        super.initVar();
        mTeamListBean=new ArrayList<>();

        mLocationList=new ArrayList<>();
        mLocationListID=new ArrayList<>();

        mAuditsTypeList=new ArrayList<>();
        mAuditTypeIDList=new ArrayList<>();

        mAddTeamListAdapter=new TemplateListAdapter(mActivity,mTeamListBean,this);
        mTeamListRV.setAdapter(mAddTeamListAdapter);
    }
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_add:
                int roleId= AppPreferences.INSTANCE.getUserRole(mActivity);
                Log.e("role Id===>",""+roleId);
                if (roleId==200 || roleId==250 || roleId==260 ||roleId ==350) {
                    Intent intent = new Intent(mActivity, AddTeamMemberActivity.class);
                    startActivity(intent);
                }
                else
                    AppUtils.toast(mActivity,getString(R.string.text_not_allowed_team));
                break;

        }

    }
    private void getTeamListFromServer()
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            NetworkService networkService = new NetworkService(NetworkURL.GET_TEMPLATE_LIST, NetworkConstant.METHOD_GET, this,mActivity);
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

                JSONObject params = new JSONObject();
                params.put("audit_type_id", Integer.parseInt(mAuditID));
                params.put("auditor_id", AppPreferences.INSTANCE.getUserId(mActivity));
                params.put("location_id", Integer.parseInt(mLocationID));
                params.put("questionnaire_id", mQuestioneriesID);
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
        Log.e("TEMPLATE Response==>",""+response);

        if (service.equalsIgnoreCase(NetworkURL.GET_TEMPLATE_LIST)) {

            try {
                mNoDataFoundRL.setVisibility(View.GONE);
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
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


                } else
                    AppUtils.toast(mActivity, message);

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
                   // startAudit.putExtra(AppConstant.AUDIT_NAME, auditInfo.getAudit_name());
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
        else {

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

        mProgressBarRL.setVisibility(View.GONE);
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
                if (mLocationListID!=null && mLocationListID.size()>0)
                    mLocationID = mLocationListID.get(position);
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

}


