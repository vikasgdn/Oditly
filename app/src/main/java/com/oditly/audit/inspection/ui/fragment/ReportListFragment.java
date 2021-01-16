package com.oditly.audit.inspection.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.ReportListAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.interfaces.OnRecyclerViewItemClickListener;
import com.oditly.audit.inspection.model.audit.AuditInfo;
import com.oditly.audit.inspection.model.audit.AuditRootObject;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkServiceJSON;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.ui.activty.BaseActivity;
import com.oditly.audit.inspection.ui.activty.ReportDetailsActivity;
import com.oditly.audit.inspection.ui.activty.ReportFilterActivity;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportListFragment extends BaseFragment implements View.OnClickListener , OnRecyclerViewItemClickListener, INetworkEvent {
    private static final int FILTER_CODE = 1001;
    private ReportListAdapter mReportListAdapter;
    private List<AuditInfo> mAuditLisBean;

    private RecyclerView mAuditListRV;
    private ImageView mFilterIV,mSearchIV;
    private EditText mSearchET;
    private int status=1;
    private RelativeLayout mNoDataFoundRL;
    private RelativeLayout mSpinKitView;
    private String mSerachValue="";

    public static ReportListFragment newInstance(String auditType) {
        Bundle args = new Bundle();
        args.putString(AppConstant.AUDIT_TYPE_ID, auditType);
        ReportListFragment fragment = new ReportListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppPreferences.INSTANCE.initAppPreferences(mActivity);

        initView(getView());
        initVar();
        getAuditListFromServer(); //scheduled
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mNoDataFoundRL=(RelativeLayout)view.findViewById(R.id.rl_nodatafound);
        mAuditListRV=(RecyclerView)view.findViewById(R.id.rv_auditlist);
        mSearchET=(EditText) view.findViewById(R.id.et_search);
        mFilterIV=(ImageView) view.findViewById(R.id.iv_filter);
        mSearchIV=(ImageView) view.findViewById(R.id.iv_search);
        mSpinKitView=(RelativeLayout) view.findViewById(R.id.ll_parent_progress);

        mFilterIV.setOnClickListener(this);
        mSearchIV.setOnClickListener(this);
        view.findViewById(R.id.fb_filter).setOnClickListener(this);

    }

    @Override
    protected void initVar() {
        super.initVar();

        mAuditLisBean=new ArrayList<>();

        mReportListAdapter=new ReportListAdapter(mActivity,mAuditLisBean,this);
        mAuditListRV.setAdapter(mReportListAdapter);

        mSearchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchData(v);
                    //  Toast.makeText(mActivity, mSearchET.getText(), Toast.LENGTH_SHORT).show();
                    handled = true;
                }
                return handled;
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.iv_search:
                searchData(view);
                break;
            case R.id.fb_filter:
                Intent intent=new Intent(mActivity, ReportFilterActivity.class);
                this.startActivityForResult(intent,FILTER_CODE);
                break;
        }
    }

    private void searchData(View view) {
          AppUtils.hideKeyboard(mActivity,view);
      //  if(!TextUtils.isEmpty(mSearchET.getText().toString())) {
            mSerachValue= mSearchET.getText().toString();
            getAuditListFromServer(); //scheduled
       /* }
        else
            AppUtils.toast(mActivity, getString(R.string.text_entor_data));*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
        {
            if(data!=null)
            {
                try
                {
                    getAuditListFromServerPostForFilter(new JSONObject(data.getStringExtra(AppConstant.JSON_DATA)));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            getAuditListFromServer();
        }

    }


    private void getAuditListFromServerPostForFilter(JSONObject params)
    {
        Log.e("IN SIDE ON ACTIVITY ","result"+params);
        //   audit_type_id,location_id,custom_role_id(designation),questionnaire_id(template),auditor_id,audit_to_date,audit_from_date

        if (NetworkStatus.isNetworkConnected(mActivity)) {
            mSpinKitView.setVisibility(View.VISIBLE);
            NetworkServiceJSON networkService = new NetworkServiceJSON(NetworkURL.AUDIT_LIST, NetworkConstant.METHOD_POST, this,mActivity);
            networkService.call(params);
        } else
        {
            AppUtils.toast(mActivity, getString(R.string.internet_error));

        }
    }
    private void getAuditListFromServer()
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {
            mSpinKitView.setVisibility(View.VISIBLE);
            String auditUrl = NetworkURL.AUDIT_LIST + "?" + "filter_report_status=1&" + "page=" + "1" + "&assigned=0&search="+mSerachValue;
            System.out.println("==> Report URL==>  "+auditUrl);
            NetworkService networkService = new NetworkService(auditUrl, NetworkConstant.METHOD_GET, this,mActivity);
            networkService.call( new HashMap<String, String>());
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
        try {
            JSONObject object = new JSONObject(response);
            mNoDataFoundRL.setVisibility(View.GONE);
            mAuditLisBean.clear();
            if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {


                AuditRootObject auditRootObject = new GsonBuilder().create().fromJson(object.toString(), AuditRootObject.class);
                Log.e("auditRootObject","=====> size "+auditRootObject.getData().size());
                if (auditRootObject.getData() != null && auditRootObject.getData().size() > 0) {
                    mAuditLisBean.addAll(auditRootObject.getData());
                    mReportListAdapter.notifyDataSetChanged();
                }else {
                    mNoDataFoundRL.setVisibility(View.VISIBLE);
                }
            } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                Log.e("auditRootObject","=====> ELSE IF ");
                mNoDataFoundRL.setVisibility(View.VISIBLE);
                AppUtils.toast((BaseActivity) mActivity, object.getString(AppConstant.RES_KEY_MESSAGE));
            }


        }
        catch (Exception e)
        {
            AppUtils.toast(mActivity, mActivity.getString(R.string.oops));
        }
        mSpinKitView.setVisibility(View.GONE);

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(mActivity, mActivity.getString(R.string.oops));
        mSpinKitView.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, View v, int position)
    {
        AuditInfo auditInfo= mAuditLisBean.get(position);

        Log.e("AUDIT ID","===> "+auditInfo.getAudit_id());

        Intent intent=new Intent(mActivity, ReportDetailsActivity.class);
        intent.putExtra(AppConstant.AUDIT_NAME,auditInfo.getAudit_name());
        intent.putExtra(AppConstant.AUDIT_ID,auditInfo.getAudit_id());
        intent.putExtra(AppConstant.AUDIT_TYPE_NAME,auditInfo.getAudit_type());
        intent.putExtra(AppConstant.AUDIT_CHECKLIST,auditInfo.getQuestionnaire_title());
        intent.putExtra(AppConstant.AUDIT_LOCATION,auditInfo.getLocation_title());
        intent.putExtra(AppConstant.AUDIT_AUDITOR,auditInfo.getAuditor_fname()+" "+auditInfo.getAuditor_lname());
        intent.putExtra(AppConstant.AUDIT_ASSIGNED_BY,auditInfo.getCreator_name());
        intent.putExtra(AppConstant.AUDIT_STARTDATE,auditInfo.getStart_date());
        intent.putExtra(AppConstant.AUDIT_COMPLETION_DATE,auditInfo.getEnd_date());
        intent.putExtra(AppConstant.AUDIT_SCORE,auditInfo.getScore_text());
        intent.putExtra(AppConstant.AUDIT_NON_COMPLIANCE,"N/A");
        intent.putExtra(AppConstant.AUDIT_DWNLD_URL,"N/A");
        startActivity(intent);

    }
}
