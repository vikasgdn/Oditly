package com.oditly.audit.inspection.ui.fragment.actionplan;

import android.app.MediaRouteButton;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.github.ybq.android.spinkit.SpinKitView;
import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.model.actionData.ActionInfo;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.ui.activty.MainActivity;
import com.oditly.audit.inspection.ui.activty.ResetPasswordScreen;
import com.oditly.audit.inspection.ui.activty.SignInPasswordActivity;
import com.oditly.audit.inspection.ui.fragment.BaseFragment;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActionCompleteFragment extends BaseFragment implements View.OnClickListener , INetworkEvent {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private Button mSubmitButton;
    private EditText mCommentET;
    private TextView mCommentErrorTV;
    private RelativeLayout mSpinKitView;
    private ActionInfo mAuditInfoActionPlanData;

    public static ActionCompleteFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ActionCompleteFragment fragment = new ActionCompleteFragment();
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

        return inflater.inflate(R.layout.fragment_action_complete, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mAuditInfoActionPlanData=((OditlyApplication)mActivity.getApplicationContext()).getmActionPlanData();
        AppPreferences.INSTANCE.initAppPreferences(mActivity);
        initVar();
        initView(getView());

    }

    @Override
    protected void initVar() {
        super.initVar();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mSubmitButton=(Button)view.findViewById(R.id.btn_submit);
        mCommentET=(EditText)view.findViewById(R.id.et_commentbox);
        mCommentErrorTV=(TextView)view.findViewById(R.id.tv_comment_error);
        mSpinKitView=(RelativeLayout) view.findViewById(R.id.ll_parent_progress);
        mSubmitButton.setOnClickListener(this);
        Log.e("CAN COMPLETE===> ",""+mAuditInfoActionPlanData.isCan_complete());
        if(!mAuditInfoActionPlanData.isCan_complete()) {
            mCommentErrorTV.setText(getString(R.string.text_this_audit_could));
            mCommentErrorTV.setVisibility(View.VISIBLE);
            mSubmitButton.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View view)
    {
       String comment=mCommentET.getText().toString();

      // if(TextUtils.isEmpty(comment))
      //     mCommentErrorTV.setVisibility(View.VISIBLE);
      // else {
           postCommentAndCompleteAction(comment);
           // AppUtils.toast(mActivity,"Comment Submitted");
       //}

    }

    private void postCommentAndCompleteAction(String comment)
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {

           mSpinKitView.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(NetworkConstant.REQ_PARAM_MOBILE, "1");
            params.put(NetworkConstant.REQ_PARAM_AUDIT_ID, ""+mAuditInfoActionPlanData.getAudit_id());
            params.put(NetworkConstant.REQ_PARAM_ACTION_PLANID, ""+mAuditInfoActionPlanData.getAction_plan_id());
            params.put(NetworkConstant.REQ_PARAM_COMMENT, mCommentET.getText().toString());

            Log.e("URL==>",""+NetworkURL.ACTION_PLAN_COMPLETE);
            NetworkService networkService = new NetworkService(NetworkURL.ACTION_PLAN_COMPLETE, NetworkConstant.METHOD_POST, this,mActivity);
            networkService.call(params);
        } else

            AppUtils.toast(mActivity, getString(R.string.internet_error));

    }


    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response)
    {
        mSpinKitView.setVisibility(View.GONE);

        Log.e("===RESPONSE===> ","==="+response);
        try {
            JSONObject object = new JSONObject(response);
            String message = object.getString(AppConstant.RES_KEY_MESSAGE);
            if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                AppUtils.toastDisplayForLong(mActivity, message);
                mCommentET.setText("");

                Intent intent =new Intent(mActivity, MainActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();

            }else
                AppUtils.toastDisplayForLong(mActivity, message);
        }
        catch (Exception e){
            e.printStackTrace();
            AppUtils.toastDisplayForLong(mActivity, getString(R.string.oops));
        }

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage)
    {
        mSpinKitView.setVisibility(View.GONE);
        AppUtils.toastDisplayForLong(mActivity, getString(R.string.oops));
    }
}
