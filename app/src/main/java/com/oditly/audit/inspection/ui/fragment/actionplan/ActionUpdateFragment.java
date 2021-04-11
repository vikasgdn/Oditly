package com.oditly.audit.inspection.ui.fragment.actionplan;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.ActionCommentListAdapter;
import com.oditly.audit.inspection.interfaces.OnRecyclerViewItemClickListener;
import com.oditly.audit.inspection.model.actionData.ActionCommentData;
import com.oditly.audit.inspection.model.actionData.ActionCommentRootObject;
import com.oditly.audit.inspection.model.actionData.ActionInfo;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.ui.fragment.BaseFragment;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActionUpdateFragment extends BaseFragment implements View.OnClickListener , INetworkEvent, OnRecyclerViewItemClickListener {
    public static final String ARG_PAGE = "ARG_PAGE";


    private RelativeLayout mSpinKitView;

    private RecyclerView mCommentRecycleView;
    private ArrayList<ActionCommentData> mCommentListBean;
    private ActionCommentListAdapter mCommentListAdapter;
    private EditText mCommentET;
    private ActionInfo mAuditInfoActionPlanData;
    private String mCommentListURL="";


    public static ActionUpdateFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ActionUpdateFragment fragment = new ActionUpdateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_action_update, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initView(getView());
        initVar();
        if(!mAuditInfoActionPlanData.isCan_complete())
            mCommentET.setEnabled(false);
        getActionCommentListServer();
    }

    @Override
    protected void initVar() {
        super.initVar();
        mAuditInfoActionPlanData=((OditlyApplication)mActivity.getApplicationContext()).getmActionPlanData();
        mCommentListBean=new ArrayList<>();
          mCommentListAdapter = new ActionCommentListAdapter(mActivity,mCommentListBean, this);
         mCommentRecycleView.setAdapter(mCommentListAdapter);

    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mCommentRecycleView =(RecyclerView) view.findViewById(R.id.rv_commentlist);
        mCommentET=(EditText)view.findViewById(R.id.et_comment);
        mSpinKitView=(RelativeLayout)view.findViewById(R.id.ll_parent_progress);
        view.findViewById(R.id.iv_send).setOnClickListener(this);


    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.iv_send:
                if (!TextUtils.isEmpty(mCommentET.getText().toString()))
                    postCommentAndUpdateAction(mCommentET.getText().toString());
                else
                    AppUtils.toast(getActivity(),"Please enter Comment ");
                break;

        }

    }
    private void getActionCommentListServer()
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {
            mSpinKitView.setVisibility(View.VISIBLE);
            mCommentListURL=NetworkURL.ACTION_PLAN_COMMENT_LIST+mAuditInfoActionPlanData.getAction_plan_id();
            NetworkService networkService = new NetworkService(mCommentListURL, NetworkConstant.METHOD_GET, this,mActivity);
            networkService.call( new HashMap<String, String>());
        } else
            AppUtils.toast(mActivity, mActivity.getString(R.string.internet_error));

    }
    private void postCommentAndUpdateAction(String comment)
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {

            mSpinKitView.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(NetworkConstant.REQ_PARAM_MOBILE, "1");
            params.put(NetworkConstant.REQ_PARAM_ACTION_PLANID, ""+mAuditInfoActionPlanData.getAction_plan_id());
            params.put(NetworkConstant.REQ_PARAM_COMMENT_ACTION,comment);

            Log.e("URL==>",""+NetworkURL.ACTION_PLAN_ADD_COMMENT);
            NetworkService networkService = new NetworkService(NetworkURL.ACTION_PLAN_ADD_COMMENT, NetworkConstant.METHOD_POST, this,mActivity);
            networkService.call(params);
        } else

            AppUtils.toast(mActivity, mActivity.getString(R.string.internet_error));

    }


    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response)
    {
        mSpinKitView.setVisibility(View.GONE);

        Log.e("===RESPONSE===> ","==="+response);
        if (service.equalsIgnoreCase(mCommentListURL)) {
            try {
                JSONObject object = new JSONObject(response);
                mCommentListBean.clear();
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {

                    ActionCommentRootObject auditRootObject = new GsonBuilder().create().fromJson(object.toString(), ActionCommentRootObject.class);
                    Log.e(" ACTION", "=====> size " + auditRootObject.getData().size());
                    if (auditRootObject.getData() != null && auditRootObject.getData().size() > 0)
                    {
                        mCommentListBean.addAll(auditRootObject.getData());
                        mCommentListAdapter.notifyDataSetChanged();
                    }

                }
            }
            catch (Exception e){e.printStackTrace();}
        }
        else
        {
            try {
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    mCommentET.setText("");
                    getActionCommentListServer();
                  //  AppUtils.toastDisplayForLong(mActivity, message);

                } else
                    AppUtils.toastDisplayForLong(mActivity, message);
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.toastDisplayForLong(mActivity, mActivity.getString(R.string.oops));
            }
        }

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage)
    {
        mSpinKitView.setVisibility(View.GONE);
        AppUtils.toastDisplayForLong(mActivity, mActivity.getString(R.string.oops));
    }



    @Override
    public void onItemClick(RecyclerView.Adapter adapter, View v, int position) {

    }
}
