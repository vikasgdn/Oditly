package com.oditly.audit.inspection.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.AuditListAdapter;

import com.oditly.audit.inspection.adapter.AuditListAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.model.audit.AuditInfo;
import com.oditly.audit.inspection.model.audit.AuditRootObject;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.ui.activty.BaseActivity;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuditFragment extends BaseFragment implements View.OnClickListener , INetworkEvent {
    public static final String ARG_PAGE = "ARG_PAGE";
    private AuditListAdapter mAuditListAdapter;
    private List<AuditInfo> mAuditLisBean;

    private RecyclerView mAuditListRV;
    private TextView mSheduleTv,mResumeTv,mOverDueTV;
    private int status=1;
    private RelativeLayout mNoDataFoundRL;
    private String mAuditTYpeID="";
    private RelativeLayout mSpinKitView;
   // private String mOverDue="";
   // private int mSkipOverDue=1;

     private String mAuditURL="";

    public static AuditFragment newInstance(String auditType) {
        Bundle args = new Bundle();
        args.putString(AppConstant.AUDIT_TYPE_ID, auditType);
        AuditFragment fragment = new AuditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuditTYpeID = getArguments().getString(AppConstant.AUDIT_TYPE_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audit, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppPreferences.INSTANCE.initAppPreferences(mActivity);

        initView(getView());
        initVar();
        mAuditURL= NetworkURL.AUDIT_LIST+"?filter_brand_std_status%5B%5D=1&assigned=1&page=1&skip_overdue=1";
        getAuditListFromServer(); //scheduled
    }



    @Override
    protected void initView(View view) {
        super.initView(view);

        mNoDataFoundRL=(RelativeLayout)view.findViewById(R.id.rl_nodatafound);
        mAuditListRV=(RecyclerView)view.findViewById(R.id.rv_auditlist);
        mSheduleTv=(TextView)view.findViewById(R.id.tv_schedule);
        mResumeTv=(TextView)view.findViewById(R.id.tv_progress);
        mOverDueTV=(TextView)view.findViewById(R.id.tv_overdue);
        mSpinKitView=(RelativeLayout) view.findViewById(R.id.ll_parent_progress);


        mSheduleTv.setOnClickListener(this);
        mResumeTv.setOnClickListener(this);
        mOverDueTV.setOnClickListener(this);

        mSheduleTv.setSelected(true);
    }

    @Override
    protected void initVar() {
        super.initVar();

        mAuditLisBean=new ArrayList<>();

       // mAuditListAdapter=new AuditActionAdapter(mActivity,mAuditLisBean,status);
       // mAuditListRV.setAdapter(mAuditListAdapter);

    }

    private void removeAllSelection()
    {
        mSheduleTv.setSelected(false);
        mResumeTv.setSelected(false);
        mOverDueTV.setSelected(false);
    }






    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tv_schedule:
                status =1;
                mAuditURL= NetworkURL.AUDIT_LIST+"?filter_brand_std_status%5B%5D=1&assigned=1&page=1&skip_overdue=1";
                if(mSheduleTv.isSelected())
                    mSheduleTv.setSelected(false);
                else
                {
                    removeAllSelection();
                    mSheduleTv.setSelected(true);
                }
                getAuditListFromServer(); //scheduled
                break;
            case R.id.tv_progress:
                status =2;
                mAuditURL= NetworkURL.AUDIT_LIST+"?filter_brand_std_status%5B%5D=2&filter_brand_std_status%5B%5D=3&assigned=1&page=1&skip_overdue=1";
                if(mResumeTv.isSelected())
                    mResumeTv.setSelected(false);
                else {
                    removeAllSelection();
                    mResumeTv.setSelected(true);
                }
                getAuditListFromServer(); //scheduled

                break;
            case R.id.tv_overdue:
                status =3;
                mAuditURL= NetworkURL.AUDIT_LIST+"?assigned=1&page=1&overdue=1";
                if(mOverDueTV.isSelected())
                    mOverDueTV.setSelected(false);
                else {
                    removeAllSelection();
                    mOverDueTV.setSelected(true);
                }
                getAuditListFromServer(); //scheduled
                break;
        }
    }

   /* Scheduled:
    filter_brand_std_status: [1]
    skip_overdue: 1

    In Progress:
    filter_brand_std_status: [2, 3]
    skip_overdue: 1

    Overdue:
    overdue: 1*/

    private void getAuditListFromServer()
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {
            mSpinKitView.setVisibility(View.VISIBLE);
            System.out.println("==> mAuditTYpeID "+mAuditURL);
            NetworkService networkService = new NetworkService(mAuditURL, NetworkConstant.METHOD_GET, this,mActivity);
            networkService.call( new HashMap<String, String>());
        } else
        {
            AppUtils.toast(mActivity, getString(R.string.internet_error));

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
                   // mAuditListAdapter.notifyDataSetChanged();
                    mAuditListAdapter=new AuditListAdapter(mActivity,mAuditLisBean,status);
                    mAuditListRV.setAdapter(mAuditListAdapter);
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
            AppUtils.toast(mActivity, getString(R.string.oops));
        }
       // ((BaseActivity)mActivity).hideProgressDialog();
        mSpinKitView.setVisibility(View.GONE);

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(mActivity, getString(R.string.oops));
       // ((BaseActivity)mActivity).hideProgressDialog();
        mSpinKitView.setVisibility(View.GONE);
    }
}
