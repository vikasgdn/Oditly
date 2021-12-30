package com.oditly.audit.inspection.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.ui.fragment.AuditFragment;
import com.oditly.audit.inspection.ui.fragment.actionplan.ActionFragment;

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Inspection", "Action"};
    private Context context;
    private String mAuditType="";

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context,String auditType) {
        super(fm);
        this.context = context;
        mAuditType=auditType;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment=null;
        switch (position) {
            case 0:
                fragment= AuditFragment.newInstance(mAuditType);
                break;
            case 1:
                fragment= ActionFragment.newInstance(position);
                break;
            default:
                fragment= AuditFragment.newInstance(mAuditType);
                break;

        }
        return  fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        String title=context.getString(R.string.text_action);
        if (position==0)
            title=context.getString(R.string.text_inspection);

        return title;
    }
}
