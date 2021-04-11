package com.oditly.audit.inspection.ui.activty;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.ActionPlanPagerAdapter;


public class ActionPlanLandingActivity extends BaseActivity {

    private ActionPlanPagerAdapter sampleFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actionplanlanding);
        initView();
        initVar();
    }
    @Override
    protected void initView() {
        super.initView();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerDynamic);

        sampleFragmentPagerAdapter=new ActionPlanPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(sampleFragmentPagerAdapter);


        findViewById(R.id.iv_header_left).setOnClickListener(this);
        findViewById(R.id.iv_header_right).setVisibility(View.GONE);
        TextView title =(TextView)findViewById(R.id.tv_header_title);
        title.setText(R.string.text_action);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayoutDynamicViewPager);


        tabLayout.setupWithViewPager(viewPager);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position)
            {
                //Toast.makeText(ActionPlanLandingActivity.this, "Selected page position: " + position, Toast.LENGTH_SHORT).show();
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

    }
    @Override
    protected void initVar() {
        super.initVar();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        finish();
    }
}
