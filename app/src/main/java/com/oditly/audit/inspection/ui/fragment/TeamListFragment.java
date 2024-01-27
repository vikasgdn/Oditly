package com.oditly.audit.inspection.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.team.TeamListAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.interfaces.OnRecyclerViewItemClickListener;
import com.oditly.audit.inspection.model.teamData.TeamList;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.ui.activty.AddTeamMemberActivity;
import com.oditly.audit.inspection.ui.activty.TeamMemberDisplayActivity;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TeamListFragment extends BaseFragment implements View.OnClickListener, OnRecyclerViewItemClickListener, INetworkEvent {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private RecyclerView mTeamListRV;
    private RelativeLayout mProgressBarRL;
    private ArrayList<TeamList> mTeamListBean;
    private TeamListAdapter mAddTeamListAdapter;
    private RelativeLayout mNoDataFoundRL;


    public static TeamListFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
            TeamListFragment fragment = new TeamListFragment();
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
        mAddTeamListAdapter=new TeamListAdapter(mActivity,mTeamListBean,this);
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
            NetworkService networkService = new NetworkService(NetworkURL.GET_TEAM_LIST, NetworkConstant.METHOD_GET, this,mActivity);
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
        Log.e("RESponse==>",""+response);

        try {
            mNoDataFoundRL.setVisibility(View.GONE);
            JSONObject object = new JSONObject(response);
            String message = object.getString(AppConstant.RES_KEY_MESSAGE);
            if (!object.getBoolean(AppConstant.RES_KEY_ERROR))
            {
                JSONArray array=object.optJSONArray("data");
                if(array!=null && array.length()>0)
                {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object1 = array.getJSONObject(i);
                        TeamList teamList = new TeamList();
                        teamList.setTeam_name(object1.optString("team_name"));
                        teamList.setTeam_users_count(object1.optInt("team_users_count"));
                        teamList.setTeam_id(object1.optInt("team_id"));
                        teamList.setCreated_on(object1.optString("created_on"));
                        mTeamListBean.add(teamList);
                    }
                    mAddTeamListAdapter.notifyDataSetChanged();
                }
                else
                    mNoDataFoundRL.setVisibility(View.VISIBLE);


            }else
                AppUtils.toast(mActivity, message);

        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.toast(mActivity, mActivity.getString(R.string.oops));
        }catch (Exception e){
            e.printStackTrace();
            AppUtils.toast(mActivity, mActivity.getString(R.string.oops));
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
        Log.e("Team Member",""+mTeamListBean.get(position).getTeam_users_count());
        Intent intent=new Intent(mActivity, TeamMemberDisplayActivity.class);
        intent.putExtra(AppConstant.TEAM_ID,""+mTeamListBean.get(position).getTeam_id());
        intent.putExtra(AppConstant.TEAM_NAME,""+mTeamListBean.get(position).getTeam_name());
        intent.putExtra(AppConstant.TEAM_MEMBER,mTeamListBean.get(position).getTeam_users_count());
        startActivity(intent);
    }
}


