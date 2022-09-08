package com.oditly.audit.inspection.ui.activty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.PointerIconCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.oditly.audit.inspection.BuildConfig;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
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

/* renamed from: com.oditly.audit.inspection.ui.activty.MainActivity */
public class MainActivity extends BaseActivity {
    private ImageView ivNotification;
    private int lastSelectedPosition = 0;
    private String mAuditTypeID = "";
    private TextView mTitleTV;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mAuditTypeID = getIntent().getStringExtra("audit_type_id");
        initView();
        initVar();

    }

    /* access modifiers changed from: protected */
    public void initView() {
        super.initView();
        this.mToolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mTitleTV = (TextView) findViewById(R.id.tv_header_title);
        initToolbar(this.mToolbar);
        ImageView imageView = (ImageView) findViewById(R.id.iv_header_right);
        this.ivNotification = imageView;
        imageView.setVisibility(View.INVISIBLE);
        this.ivNotification.setOnClickListener(this);
        ImageView imgMenu = (ImageView) findViewById(R.id.iv_header_left);
        imgMenu.setImageResource(R.drawable.ic_menu);
        imgMenu.setOnClickListener(this);
        String location = getIntent().getStringExtra(AppConstant.FROMWHERE) == null ? "" : getIntent().getStringExtra(AppConstant.FROMWHERE);
        if (!TextUtils.isEmpty(location) && location.equalsIgnoreCase(AppConstant.TEAM)) {
            this.lastSelectedPosition = 3;
        }
        BottomifyNavigationView navigationView = (BottomifyNavigationView) findViewById(R.id.bottomify_nav);
        navigationView.setActiveNavigationIndex(this.lastSelectedPosition);
        // setPageSelection(this.lastSelectedPosition);
        navigationView.setOnNavigationItemChangedListener(new OnNavigationItemChangeListener() {
            public void onNavigationItemChanged(BottomifyNavigationView.NavigationItem navigationItem) {
                MainActivity.this.setPageSelection(navigationItem.getPosition());
            }
        });
    }

    /* access modifiers changed from: protected */
    public void initVar() {
        super.initVar();
        setPageSelection(this.lastSelectedPosition);
        checkPermissionRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  if (AppPreferences.INSTANCE.getAppMinimumVersionServer(this)> BuildConfig.VERSION_CODE)
        //    AppDialogs.openPlayStoreDialog(this);
    }

    private void checkLocationEnable() {
        if (!new LocationUtils(this).hasLocationEnabled()) {
            startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), PointerIconCompat.TYPE_ALIAS);
        }
    }

    private void checkPermissionRequest() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            checkLocationEnable();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, PointerIconCompat.TYPE_VERTICAL_TEXT);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1009) {
            if (grantResults.length <= 0 || grantResults[0] != 0) {
                AppUtils.toastDisplayForLong(this, "Please enable permission for geo-tagging of images");
            } else {
                checkLocationEnable();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 1010) {
            return;
        }
        if (new LocationUtils(this).hasLocationEnabled()) {
            new LocationUtils(this).beginUpdates(this);
            AppUtils.toast(this, "Location enabled successfully");
            return;
        }
        AppUtils.toast(this, "Please enable location for geo-tagging of images ");
    }

    /* access modifiers changed from: private */
    public void setPageSelection(int position) {
        if (position == 0) {
            this.mTitleTV.setText(getResources().getString(R.string.s_mytask));
            getSupportFragmentManager().beginTransaction().replace(R.id.container, LandingFragment.newInstance(this.mAuditTypeID)).commitAllowingStateLoss();
        } else if (position == 1) {
            this.mTitleTV.setText(getResources().getString(R.string.s_analytics));
            getSupportFragmentManager().beginTransaction().replace(R.id.container, DashboardFragment.newInstance(0)).commitAllowingStateLoss();
        } else if (position == 2) {
            this.mTitleTV.setText(getResources().getString(R.string.s_reports));
            getSupportFragmentManager().beginTransaction().replace(R.id.container, ReportListFragment.newInstance("")).commitAllowingStateLoss();
        } else if (position != 3) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, LandingFragment.newInstance(this.mAuditTypeID)).commitAllowingStateLoss();
        } else {
            this.mTitleTV.setText(getResources().getString(R.string.text_team));
            getSupportFragmentManager().beginTransaction().replace(R.id.container, TeamListFragment.newInstance(4)).commitAllowingStateLoss();
        }
    }


    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.iv_header_left:
                startActivity(new Intent(this, AccountProfileActivity.class));
                return;
            case R.id.iv_header_right:
                AppUtils.toast(this, getString(R.string.text_coming_soon));
                return;
            default:
                return;
        }
    }
}
