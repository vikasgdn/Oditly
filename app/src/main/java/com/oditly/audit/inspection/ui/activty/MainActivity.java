package com.oditly.audit.inspection.ui.activty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.ui.fragment.AnalyticsFragment;
import com.oditly.audit.inspection.ui.fragment.GoToFragment;
import com.oditly.audit.inspection.ui.fragment.LandingFragment;
import com.oditly.audit.inspection.ui.fragment.ReportFragment;
import com.oditly.audit.inspection.ui.fragment.TeamListFragment;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener
{
    private   int lastSelectedPosition = 0;
    private BottomNavigationBar bottomNavigationBar;
    private TextView mTitleTV;
    private String mAuditTypeName="";
    private String mAuditTypeID="";
    private ImageView ivNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAuditTypeID=getIntent().getStringExtra(AppConstant.AUDIT_TYPE_ID);
        initView();
        initVar();

    }

    @Override
    protected void initView() {
        super.initView();

        mToolbar = findViewById(R.id.toolbar);
        mTitleTV=(TextView)findViewById(R.id.tv_header_title);

        initToolbar(mToolbar);

        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        ivNotification=(ImageView)findViewById(R.id.iv_header_right);
        ivNotification.setVisibility(View.INVISIBLE);
        ivNotification.setOnClickListener(this);

        ImageView imgMenu=(ImageView)findViewById(R.id.iv_header_left);
        imgMenu.setImageResource(R.drawable.ic_menu);
        imgMenu.setOnClickListener(this);

       String location= getIntent().getStringExtra(AppConstant.FROMWHERE)==null?"":getIntent().getStringExtra(AppConstant.FROMWHERE);
       if(!TextUtils.isEmpty(location) && location.equalsIgnoreCase(AppConstant.TEAM))
           lastSelectedPosition=4;

        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED_NO_TITLE);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setBarBackgroundColor("#F4F1F1");

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_schedule_png,"Schedule").setActiveColorResource(R.color.c_blue))
                .addItem(new BottomNavigationItem(R.drawable.ic_analytics_png, "Analytics").setActiveColorResource(R.color.c_blue))
                .addItem(new BottomNavigationItem(R.drawable.ic_goto_png, "Go To").setActiveColorResource(R.color.c_blue))
                .addItem(new BottomNavigationItem(R.drawable.ic_report_png, "Report").setActiveColorResource(R.color.c_blue))
                .addItem(new BottomNavigationItem(R.drawable.ic_adduser_png, "Team").setActiveColorResource(R.color.c_blue))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(this);

    }

    @Override
    protected void initVar() {
        super.initVar();
        setScrollableText(lastSelectedPosition);
    }

  /*  @Override
    protected void onResume() {
        super.onResume();
        setScrollableText(lastSelectedPosition);
    }*/

    @Override
    public void onTabSelected(int position) {
        lastSelectedPosition = position;

        setScrollableText(position);
        Log.e("========>","onTabSelected");
    }

    @Override
    public void onTabUnselected(int position) {
        Log.e("=======>","onTabSelected");
    }

    @Override
    public void onTabReselected(int position)
    {
        setScrollableText(position);
        Log.e("======> ","onTabReselect");
    }


    private void setScrollableText(int position) {
        switch (position) {
            case 0:
                mTitleTV.setText(getResources().getString(R.string.s_schedule));
                getSupportFragmentManager().beginTransaction().replace(R.id.container,LandingFragment.newInstance(mAuditTypeID)).commitAllowingStateLoss();
                break;
            case 1:
                mTitleTV.setText(getResources().getString(R.string.s_analytics));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, AnalyticsFragment.newInstance(0)).commitAllowingStateLoss();
                break;
            case 2:
              //  mTitleTV.setText(getResources().getString(R.string.text_goto));
               // getSupportFragmentManager().beginTransaction().replace(R.id.container, GoToFragment.newInstance(4)).commitAllowingStateLoss();
                AppDialogs.showOtpValidateDialog(this);
                break;
            case 3:
                mTitleTV.setText(getResources().getString(R.string.s_reports));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, ReportFragment.newInstance("")).commitAllowingStateLoss();
                break;
            case 4:
                mTitleTV.setText(getResources().getString(R.string.text_team));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, TeamListFragment.newInstance(4)).commitAllowingStateLoss();
                break;
            default:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, LandingFragment.newInstance(mAuditTypeID)).commitAllowingStateLoss();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.iv_header_left:
                Intent account =new Intent(this, AccountProfileActivity.class);
                startActivity(account);
                break;
            case R.id.iv_header_right:
                AppUtils.toast(this, getString(R.string.text_coming_soon));
                break;        }
    }

  /*  @Override
    public void onBackPressed() {
        AppDialogs.exitDialog(this);
    }*/
}
