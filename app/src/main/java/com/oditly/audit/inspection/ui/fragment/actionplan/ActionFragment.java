package com.oditly.audit.inspection.ui.fragment.actionplan;

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
import com.oditly.audit.inspection.adapter.ActionPlanListAdapter;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.model.actionData.ActionInfo;
import com.oditly.audit.inspection.model.actionData.ActionRootObject;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.ui.activty.BaseActivity;
import com.oditly.audit.inspection.ui.fragment.BaseFragment;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActionFragment extends BaseFragment implements View.OnClickListener, INetworkEvent {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private RecyclerView mAuditListRV;
    private ActionPlanListAdapter mAuditListAdapter;
    private TextView mSheduleTv,mResumeTv,mOverDueTV;
    private List<ActionInfo> mAuditLisBean;
    private int status=1;
    private RelativeLayout mSpinKitView;
    private RelativeLayout mNoDataFoundRL;
    private String mURL="";


    public static ActionFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ActionFragment fragment = new ActionFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_action, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView(getView());
        initVar();
        mURL= NetworkURL.ACTION_PLAN+"?action_plan_status%5B%5D=1";

        getAuditListFromServer(status); //scheduled

    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mAuditListRV=(RecyclerView)view.findViewById(R.id.rv_auditlist);
        mSheduleTv=(TextView)view.findViewById(R.id.tv_schedule);
        mResumeTv=(TextView)view.findViewById(R.id.tv_progress);
        mOverDueTV=(TextView)view.findViewById(R.id.tv_overdue);
        mSpinKitView=(RelativeLayout) view.findViewById(R.id.ll_parent_progress);
        mNoDataFoundRL=(RelativeLayout) view.findViewById(R.id.rl_nodatafound);


        mSheduleTv.setOnClickListener(this);
        mResumeTv.setOnClickListener(this);
        mOverDueTV.setOnClickListener(this);

        mSheduleTv.setSelected(true);
    }

    @Override
    protected void initVar() {
        super.initVar();
        mAuditLisBean=new ArrayList<>();
        //mAuditListAdapter=new ActionPlanListAdapter(mActivity,mAuditLisBean,status);
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
                if(mSheduleTv.isSelected())
                    mSheduleTv.setSelected(false);
                else
                {
                    removeAllSelection();
                    mSheduleTv.setSelected(true);
                }
                status=1;
                mURL=NetworkURL.ACTION_PLAN+"?action_plan_status%5B%5D=1";
                getAuditListFromServer(status); //scheduled
                break;
            case R.id.tv_progress:
                if(mResumeTv.isSelected())
                    mResumeTv.setSelected(false);
                else {
                    removeAllSelection();
                    mResumeTv.setSelected(true);
                }
                status=2;
                mURL= NetworkURL.ACTION_PLAN+"?action_plan_status%5B%5D=2&action_plan_status%5B%5D=3";

                getAuditListFromServer(status); //
                break;
            case R.id.tv_overdue:
                if(mOverDueTV.isSelected())
                    mOverDueTV.setSelected(false);
                else {
                    removeAllSelection();
                    mOverDueTV.setSelected(true);
                }
                status=4;
                mURL=NetworkURL.ACTION_PLAN+"?action_plan_status%5B%5D=4";
                getAuditListFromServer(status); //scheduled
                break;

        }
    }


    private void getAuditListFromServer(int status)
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {
            mSpinKitView.setVisibility(View.VISIBLE);
            Log.e("URL",""+mURL);
            NetworkService networkService = new NetworkService(mURL, NetworkConstant.METHOD_GET, this, mActivity);
            networkService.call(new HashMap<String, String>());
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

                ActionRootObject auditRootObject = new GsonBuilder().create().fromJson(object.toString(), ActionRootObject.class);
                Log.e(" ACTION","=====> size "+auditRootObject.getData().size());
                if (auditRootObject.getData() != null && auditRootObject.getData().size() > 0) {

                    mAuditLisBean.addAll(auditRootObject.getData());
                    //  mAuditListAdapter.notifyDataSetChanged();
                    mAuditListAdapter=new ActionPlanListAdapter(mActivity,mAuditLisBean,status);
                    mAuditListRV.setAdapter(mAuditListAdapter);

                    switch (status)
                    {
                        case 1:
                            mSheduleTv.setText(getString(R.string.s_scheduled)+"("+mAuditLisBean.size()+")");
                            break;
                        case 2:
                            mResumeTv.setText(getString(R.string.s_progress)+"("+mAuditLisBean.size()+")");
                            break;
                        case 4:
                            mOverDueTV.setText(getString(R.string.s_overdue)+"("+mAuditLisBean.size()+")");
                            break;

                    }

                }else {
                    mNoDataFoundRL.setVisibility(View.VISIBLE);
                }
            } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                Log.e("ACTION","=====> ELSE IF ");
                mNoDataFoundRL.setVisibility(View.VISIBLE);
                AppUtils.toast((BaseActivity) mActivity, object.getString(AppConstant.RES_KEY_MESSAGE));
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
          //  AppUtils.toast(mActivity, mActivity.getString(R.string.oops));
        }
        mSpinKitView.setVisibility(View.GONE);

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(mActivity, mActivity.getString(R.string.oops));
        mSpinKitView.setVisibility(View.GONE);
    }
}
