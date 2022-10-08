package com.oditly.audit.inspection.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.AuditActionFragmentPagerAdapter;
import com.oditly.audit.inspection.util.AppConstant;

public class LandingFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private String mAuditTYpeID="";
    private AuditActionFragmentPagerAdapter sampleFragmentPagerAdapter;

    public static LandingFragment newInstance(String auditTYpeID) {
        Bundle args = new Bundle();
        args.putString(AppConstant.AUDIT_TYPE_ID, auditTYpeID);
        LandingFragment fragment = new LandingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuditTYpeID = getArguments().getString(AppConstant.AUDIT_TYPE_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landing, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view=getView();

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPagerDynamic);

        sampleFragmentPagerAdapter=new AuditActionFragmentPagerAdapter(getChildFragmentManager(), getActivity(),mAuditTYpeID);
        viewPager.setAdapter(sampleFragmentPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayoutDynamicViewPager);


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
    }
}
