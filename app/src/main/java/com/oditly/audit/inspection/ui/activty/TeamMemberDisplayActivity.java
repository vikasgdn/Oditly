package com.oditly.audit.inspection.ui.activty;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.team.TeamMemberListAdapter;
import com.oditly.audit.inspection.model.teamData.TeamInfo;
import com.oditly.audit.inspection.model.teamData.TeamMemberRootObject;
import com.oditly.audit.inspection.model.teamData.TeamMembers;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamMemberDisplayActivity extends BaseActivity implements INetworkEvent {

    private RecyclerView mTeamListRV;
    private RelativeLayout mProgressBarRL;
    private ArrayList<TeamMembers> mTeamListBean;
    private TeamMemberListAdapter mAddTeamListAdapter;
    private RelativeLayout mNoDataFoundRL;
    private EditText mSeachFilterET;
    private TextView mTeamNameAndSizeTV;
    private String mMembeText="Member";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_member);
        String teamId=getIntent().getStringExtra(AppConstant.TEAM_ID);
        initView();
        initVar();
        getTeamListFromServer(teamId);

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
        mSeachFilterET=(EditText)findViewById(R.id.et_search);
        mTeamNameAndSizeTV=(TextView)findViewById(R.id.tv_teamname);

       int member= getIntent().getIntExtra(AppConstant.TEAM_MEMBER,0);
       if (member>1)
           mMembeText=""+member+" "+getString(R.string.text_members);
       else
           mMembeText=""+member+" "+getString(R.string.text_member);

        if(getIntent()!=null)
            mTeamNameAndSizeTV.setText(""+getIntent().getStringExtra(AppConstant.TEAM_NAME)+"'s"+getString(R.string.text_team)+" ( "+mMembeText+" )");



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
        mAddTeamListAdapter=new TeamMemberListAdapter(this,mTeamListBean);
        mTeamListRV.setAdapter(mAddTeamListAdapter);
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        List<TeamMembers> filterdNames = new ArrayList<>();

        for (TeamMembers s : mTeamListBean) {
            //if the existing elements contains the search input
            String rollName=s.getCustom_role_name()==null?"":s.getCustom_role_name();
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getEmail().toLowerCase().contains(text.toLowerCase()) || rollName.toLowerCase().contains(text.toLowerCase())) {
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
            case R.id.iv_header_left:
                finish();
                break;
        }

    }
    private void getTeamListFromServer(String teamid)
    {
        if (NetworkStatus.isNetworkConnected(this)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            String url=NetworkURL.GET_TEAM_MEMBER+"team_id="+teamid;
            Log.e("team url==> ",""+url);
            NetworkService networkService = new NetworkService(url, NetworkConstant.METHOD_GET, this,this);
            networkService.call( new HashMap<String, String>());
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
        Log.e("Response==>",""+response);

        try {
            JSONObject object = new JSONObject(response);
            mNoDataFoundRL.setVisibility(View.GONE);
            mTeamListBean.clear();
            if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {

                TeamMemberRootObject teamRootObject = new GsonBuilder().create().fromJson(object.toString(), TeamMemberRootObject.class);
                Log.e("teamRootObject", "=====> size " + teamRootObject.getData().get(0).getUsers());
                if (teamRootObject.getData().get(0).getUsers() != null && teamRootObject.getData().get(0).getUsers().size() > 0) {
                    mTeamListBean.addAll(teamRootObject.getData().get(0).getUsers());
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
