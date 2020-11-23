package com.oditly.audit.inspection.ui.activty;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.team.AddTeamListAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.model.teamData.TeamInfo;
import com.oditly.audit.inspection.model.teamData.TeamRootObject;
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
import java.util.HashSet;
import java.util.List;

public class AddTeamMemberActivity extends BaseActivity implements INetworkEvent {

    private RecyclerView mTeamListRV;
    private RelativeLayout mProgressBarRL;
    private ArrayList<TeamInfo> mTeamListBean;
    private AddTeamListAdapter mAddTeamListAdapter;
    private RelativeLayout mNoDataFoundRL;
    private Button mInviteButton;
    private CheckBox mSelectAllCB;
    private TextView mResetAllTV;
    private EditText mSeachFilterET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team_member);

        initView();
        initVar();
        getTeamListFromServer();

    }

    @Override
    protected void initView() {
        super.initView();

        findViewById(R.id.iv_header_left).setOnClickListener(this);
        TextView textView=(TextView)findViewById(R.id.tv_header_title);
        textView.setText(getString(R.string.text_team));

        mTeamListRV=(RecyclerView)findViewById(R.id.rv_teamlist);
        mProgressBarRL=(RelativeLayout)findViewById(R.id.ll_parent_progress);
        mNoDataFoundRL=(RelativeLayout)findViewById(R.id.rl_nodatafound);
        mInviteButton=(Button)findViewById(R.id.btn_invite);
        mSelectAllCB=(CheckBox)findViewById(R.id.cb_select_all);
        mResetAllTV=(TextView)findViewById(R.id.tv_reset_selection);
        mSeachFilterET=(EditText)findViewById(R.id.et_search);


        mInviteButton.setOnClickListener(this);
        mSelectAllCB.setOnClickListener(this);
        mResetAllTV.setOnClickListener(this);


        mSeachFilterET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });

    }

    @Override
    protected void initVar() {
        super.initVar();
        mTeamListBean=new ArrayList<>();
        mAddTeamListAdapter=new AddTeamListAdapter(this,mTeamListBean);
        mTeamListRV.setAdapter(mAddTeamListAdapter);
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        List<TeamInfo> filterdNames = new ArrayList<>();

        for (TeamInfo s : mTeamListBean) {
            //if the existing elements contains the search input
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getEmail().toLowerCase().contains(text.toLowerCase()) || s.getRole_name().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        mAddTeamListAdapter.filterList(filterdNames);
    }
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.btn_invite:
              HashSet<Integer>  mTeamMember=mAddTeamListAdapter.getInvitedMember();
                if(mTeamMember!=null && mTeamMember.size()>0)
                    AppDialogs.showTeamNameDialog(this,mTeamMember);
                else
                    AppUtils.toast(this, getString(R.string.text_select_member));
                break;
            case R.id.cb_select_all:
                if(mSelectAllCB.isChecked())
                    mAddTeamListAdapter.selectAll();
                else
                    mAddTeamListAdapter.unselectall();
                break;
            case R.id.tv_reset_selection:
                mSelectAllCB.setChecked(false);
                    mAddTeamListAdapter.unselectall();
                break;
            case R.id.iv_header_left:
               finish();
                break;
        }

    }
    private void getTeamListFromServer()
    {
        if (NetworkStatus.isNetworkConnected(this)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            NetworkService networkService = new NetworkService(NetworkURL.GET_TEAM_LIST_ADD, NetworkConstant.METHOD_GET, this,this);
            networkService.call( new HashMap<String, String>());
        } else
        {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }

    public void postTeamMemberToServer(String  teamName,HashSet<Integer> mTeamMember)
    {
        // { "team_name": "", "users": [1, 2]}
        if (NetworkStatus.isNetworkConnected(this))
        {
            JSONArray jsArray=  new JSONArray(mTeamMember);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(AppConstant.TEAM_NAME,teamName);
                jsonObject.put(AppConstant.TEAM_USERS, jsArray);

                mProgressBarRL.setVisibility(View.VISIBLE);
                NetworkServiceJSON networkService = new NetworkServiceJSON(NetworkURL.POST_TEAM_MEMBER, NetworkConstant.METHOD_POST, this, this);
                networkService.call(jsonObject);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        } else
        {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }



    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response)
    {
        Log.e("RESponse==>",""+response);

        if(service.equalsIgnoreCase(NetworkURL.GET_TEAM_LIST_ADD)) {
            try {
                JSONObject object = new JSONObject(response);
                mNoDataFoundRL.setVisibility(View.GONE);
                mTeamListBean.clear();
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {

                    TeamRootObject teamRootObject = new GsonBuilder().create().fromJson(object.toString(), TeamRootObject.class);
                    Log.e("teamRootObject", "=====> size " + teamRootObject.getData().getUsers());
                    if (teamRootObject.getData().getUsers() != null && teamRootObject.getData().getUsers().size() > 0) {

                        mTeamListBean.addAll(teamRootObject.getData().getUsers());
                        mAddTeamListAdapter.notifyDataSetChanged();
                    } else {
                        mNoDataFoundRL.setVisibility(View.VISIBLE);
                    }
                } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    mNoDataFoundRL.setVisibility(View.VISIBLE);
                    AppUtils.toast((BaseActivity) this, object.getString(AppConstant.RES_KEY_MESSAGE));
                }


            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.toast(this, getString(R.string.oops));
            }
        }
        else
        {
            try {
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    AppUtils.toastDisplayForLong(this, message);
                    Intent intent =new Intent(this, MainActivity.class);
                    intent.putExtra(AppConstant.FROMWHERE,AppConstant.TEAM);
                    this.startActivity(intent);
                    this.finish();
                }else
                    AppUtils.toastDisplayForLong(this, message);
            }
            catch (Exception e){
                e.printStackTrace();
                AppUtils.toastDisplayForLong(this, getString(R.string.oops));
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
}
