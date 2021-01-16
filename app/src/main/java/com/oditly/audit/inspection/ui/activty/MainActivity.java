package com.oditly.audit.inspection.ui.activty;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.ui.fragment.DashboardFragment;
import com.oditly.audit.inspection.ui.fragment.LandingFragment;
import com.oditly.audit.inspection.ui.fragment.ReportListFragment;
import com.oditly.audit.inspection.ui.fragment.TeamListFragment;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;
import com.oditly.audit.inspection.util.LocationUtils;
import com.volcaniccoder.bottomify.BottomifyNavigationView;
import com.volcaniccoder.bottomify.OnNavigationItemChangeListener;


public class MainActivity extends BaseActivity
{
    private   int lastSelectedPosition = 0;
    private TextView mTitleTV;
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

        ivNotification=(ImageView)findViewById(R.id.iv_header_right);
        ivNotification.setVisibility(View.INVISIBLE);
        ivNotification.setOnClickListener(this);

        ImageView imgMenu=(ImageView)findViewById(R.id.iv_header_left);
        imgMenu.setImageResource(R.drawable.ic_menu);
        imgMenu.setOnClickListener(this);

       String location= getIntent().getStringExtra(AppConstant.FROMWHERE)==null?"":getIntent().getStringExtra(AppConstant.FROMWHERE);
       if(!TextUtils.isEmpty(location) && location.equalsIgnoreCase(AppConstant.TEAM))
           lastSelectedPosition=4;
        BottomifyNavigationView navigationView=(BottomifyNavigationView)findViewById(R.id.bottomify_nav);
        navigationView.setActiveNavigationIndex(lastSelectedPosition);

        setScrollableText(lastSelectedPosition);
        navigationView.setOnNavigationItemChangedListener(new OnNavigationItemChangeListener() {
            @Override
            public void onNavigationItemChanged(BottomifyNavigationView.NavigationItem navigationItem) {
               // navigationView.setActiveNavigationIndex(lastSelectedPosition);
                setScrollableText(navigationItem.getPosition());
            }
        });

    }


    @Override
    protected void initVar() {
        super.initVar();
        setScrollableText(lastSelectedPosition);
        checkLocationEnable();
    }

    private void checkLocationEnable()
    {
        if (!new LocationUtils(this).hasLocationEnabled()) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent,1010);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1010)
        {
            if (new LocationUtils(this).hasLocationEnabled())
                AppUtils.toastDisplayForLong(this, "Location enabled successfully");
            else
                AppUtils.toastDisplayForLong(this, "Location not enabled ");
        }

    }

  /*  @Override
    protected void onResume() {
        super.onResume();
        setScrollableText(lastSelectedPosition);
    }*/
    private void setScrollableText(int position) {
        switch (position) {
            case 0:
                mTitleTV.setText(getResources().getString(R.string.s_schedule));
                getSupportFragmentManager().beginTransaction().replace(R.id.container,LandingFragment.newInstance(mAuditTypeID)).commitAllowingStateLoss();
                break;
            case 1:
                mTitleTV.setText(getResources().getString(R.string.s_analytics));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, DashboardFragment.newInstance(0)).commitAllowingStateLoss();
                break;
            case 2:
              //  mTitleTV.setText(getResources().getString(R.string.text_goto));
               // getSupportFragmentManager().beginTransaction().replace(R.id.container, GoToFragment.newInstance(4)).commitAllowingStateLoss();
                AppDialogs.showOtpValidateDialog(this);
                break;
            case 3:
                mTitleTV.setText(getResources().getString(R.string.s_reports));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, ReportListFragment.newInstance("")).commitAllowingStateLoss();
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
