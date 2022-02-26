package com.oditly.audit.inspection.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.AuditListAdapter;

import com.oditly.audit.inspection.adapter.AuditListAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.localDB.bsoffline.BsOfflineDBImpl;
import com.oditly.audit.inspection.model.audit.AuditInfo;
import com.oditly.audit.inspection.model.audit.AuditRootObject;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;

import com.oditly.audit.inspection.ui.activty.AuditCreateActivity;
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
    private RelativeLayout mSpinKitView;
    private String mAuditURL="";
    private BsOfflineDBImpl mBsOfflineDB;
    private String mAudityType=AppConstant.SCHEDULE;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 4;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private int mCurrentPage=1;
    private int mTotalPage=1;
    private boolean isPagingData=false;
    //private int row;

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
        mBsOfflineDB= BsOfflineDBImpl.getInstance(mActivity);
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
        view.findViewById(R.id.fb_create).setOnClickListener(this);

        mSheduleTv.setOnClickListener(this);
        mResumeTv.setOnClickListener(this);
        mOverDueTV.setOnClickListener(this);

        mSheduleTv.setSelected(true);
    }

    @Override
    protected void initVar() {
        super.initVar();

        mAuditLisBean=new ArrayList<>();

        mAuditListAdapter=new AuditListAdapter(mActivity,mAuditLisBean,status);
        mAuditListRV.setAdapter(mAuditListAdapter);

        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAuditListRV.setLayoutManager(mLayoutManager);


        mAuditListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mAuditListRV.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                  //  Toast.makeText(getActivity()," END PAGE",Toast.LENGTH_SHORT).show();
                    if (mTotalPage>mCurrentPage) {
                        isPagingData=true;
                        mCurrentPage++;
                        mAuditURL = mAuditURL + "&page=" + mCurrentPage + "";
                        getAuditListFromServer(); //scheduled
                    }
                    loading = true;
                }
            }
        });

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
                mAudityType= AppConstant.SCHEDULE;
                status =1;
                mAuditURL= NetworkURL.AUDIT_LIST+"?filter_brand_std_status%5B%5D=1&assigned=1&skip_overdue=1";
                if(mSheduleTv.isSelected())
                    mSheduleTv.setSelected(false);
                else
                {
                    removeAllSelection();
                    mSheduleTv.setSelected(true);
                }
                isPagingData=false;
                previousTotal=0;
                mCurrentPage=1;
             //   mAuditLisBean.clear();
                getAuditListFromServer(); //scheduled
                break;
            case R.id.tv_progress:
                mAudityType= AppConstant.INPROGRESS;
                status =2;
                mAuditURL= NetworkURL.AUDIT_LIST+"?filter_brand_std_status%5B%5D=2&filter_brand_std_status%5B%5D=3&assigned=1&skip_overdue=1";
                if(mResumeTv.isSelected())
                    mResumeTv.setSelected(false);
                else {
                    removeAllSelection();
                    mResumeTv.setSelected(true);
                }
                isPagingData=false;
                previousTotal=0;
                mCurrentPage=1;
             //   mAuditLisBean.clear();
                getAuditListFromServer(); //scheduled

                break;
            case R.id.tv_overdue:
                mAudityType= AppConstant.OVERDUE;
                status =3;
                mAuditURL= NetworkURL.AUDIT_LIST+"?assigned=1&overdue=1";
                if(mOverDueTV.isSelected())
                    mOverDueTV.setSelected(false);
                else {
                    removeAllSelection();
                    mOverDueTV.setSelected(true);
                }
                isPagingData=false;
                previousTotal=0;
                mCurrentPage=1;
                getAuditListFromServer(); //scheduled
                break;
            case R.id.fb_create:
                Intent  intent =new Intent(mActivity, AuditCreateActivity.class);
                mActivity.startActivity(intent);
                break;
        }
    }
    private void getAuditListFromServer()
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {
            mSpinKitView.setVisibility(View.VISIBLE);
            System.out.println("==> mAuditTYpeID "+mAuditURL);
            NetworkService networkService = new NetworkService(mAuditURL, NetworkConstant.METHOD_GET, this,mActivity);
            networkService.call( new HashMap<String, String>());
        } else
        {
            AppUtils.toast(mActivity, mActivity.getString(R.string.internet_error));
        }
    }

    @Override
    public void onNetworkCallInitiated(String service)
    {

    }
    @Override
    public void onNetworkCallCompleted(String type, String service, String response)
    {
        mBsOfflineDB.deleteAuditListJSONToDB(mAudityType);
        mBsOfflineDB.saveAuditListJSONToDB(mAudityType,response);
        processAuditListResponse(response);

    }
    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("onNetworkCallError","===>"+errorMessage);
        if (mActivity!=null)
            AppUtils.toast(mActivity, mActivity.getString(R.string.oops));
        mSpinKitView.setVisibility(View.GONE);
    }
    private void processAuditListResponse(String response) {
        try {

            JSONObject object = new JSONObject(response);
            mNoDataFoundRL.setVisibility(View.GONE);
           if (!isPagingData) {
                mAuditLisBean.clear();
            }
            if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
             //   row=object.getInt("rows");
                mTotalPage=(object.getInt("rows")/object.getInt("limit"))+1;

                AuditRootObject auditRootObject = new GsonBuilder().create().fromJson(object.toString(), AuditRootObject.class);
                if (auditRootObject.getData() != null && auditRootObject.getData().size() > 0) {

                     mAuditLisBean.addAll(auditRootObject.getData());
                     mAuditListAdapter.updateStatus(status);
                     mAuditListAdapter.notifyDataSetChanged();
                    switch (status)
                    {
                        case 1:
                            mSheduleTv.setText(getString(R.string.s_scheduled_audit)+"("+object.optString("rows")+")");
                            break;
                        case 2:
                            mResumeTv.setText(getString(R.string.s_progress_audit)+"("+object.optString("rows")+")");
                            break;
                        case 3:
                            mOverDueTV.setText(getString(R.string.s_overdue)+"("+object.optString("rows")+")");
                            break;
                    }
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



}
