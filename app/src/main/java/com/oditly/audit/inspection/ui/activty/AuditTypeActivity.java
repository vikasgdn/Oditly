package com.oditly.audit.inspection.ui.activty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.AuditTypeAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.model.audit.AuditType;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuditTypeActivity extends BaseActivity implements AuditTypeAdapter.CustomItemClickListener, INetworkEvent {

    public static final String TAG = AuditTypeActivity.class.getSimpleName();
    private List<AuditType> mAuditTypeList;
    private RecyclerView mAUditRecycle;
    private AuditTypeAdapter mAuditTypeAdapter;
    private RelativeLayout mSpinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_type);
        AppPreferences.INSTANCE.initAppPreferences(this);
        initView();
        initVar();
        getAuditTypeListFromServer();
    }

    @Override
    protected void initView() {
        super.initView();


        mAUditRecycle = (RecyclerView) findViewById(R.id.rv_auditlist);

        GridLayoutManager manager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        mAUditRecycle.setLayoutManager(manager);

       findViewById(R.id.iv_header_left).setOnClickListener(this);
       mSpinKitView=(RelativeLayout)findViewById(R.id.ll_parent_progress);


    }

    @Override
    protected void initVar() {
        super.initVar();
        mAuditTypeList = new ArrayList<AuditType>();
        mAuditTypeAdapter=new AuditTypeAdapter(this,mAuditTypeList,AuditTypeActivity.this);
        mAUditRecycle.setAdapter(mAuditTypeAdapter);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        onBackPressed();
    }

    private void getAuditTypeListFromServer()
    {
        if (NetworkStatus.isNetworkConnected(this)) {
          //  showProgressDialog();
            mSpinKitView.setVisibility(View.VISIBLE);
            NetworkService networkService = new NetworkService(NetworkURL.AUDIT_TYPE_LIST, NetworkConstant.METHOD_GET, this,this);
            networkService.call( new HashMap<String, String>());
        } else
        {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }

    @Override
    public void onItemClick(AuditType type1) {
        // Toast.makeText(getActivity(),"===> "+type.name,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AnimationActivity.class);
        intent.putExtra(AppConstant.AUDIT_TYPE_ID, ""+type1.type_id);
        intent.putExtra(AppConstant.AUDIT_TYPE_NAME, type1.name);
        startActivity(intent);
    }

    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type1, String service, String response) {
        try {
            JSONObject object = new JSONObject(response);

            if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {

                JSONArray jsonArray=object.getJSONArray("data");

                for(int i=0;i<jsonArray.length();i++)
                {
                    AuditType type=new AuditType();
                    type.type_id=jsonArray.getJSONObject(i).optInt("type_id");
                    type.name=jsonArray.getJSONObject(i).optString("name");
                    mAuditTypeList.add(type);

                }
                mAuditTypeAdapter.notifyDataSetChanged();

            } else
            {
                AppUtils.toast(this, object.getString(AppConstant.RES_KEY_MESSAGE));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.toast(this, getString(R.string.oops));
        }
      //  hideProgressDialog();
        mSpinKitView.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(this, getString(R.string.oops));
       // hideProgressDialog();
        mSpinKitView.setVisibility(View.GONE);
    }

   /* @Override
    public void onBackPressed() {
        AppDialogs.exitDialog(this);
    }*/
}
