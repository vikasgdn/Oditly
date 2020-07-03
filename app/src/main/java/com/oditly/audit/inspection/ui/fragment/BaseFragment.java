package com.oditly.audit.inspection.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

   protected Activity mActivity;

    private void toggleVisibility(ViewGroup listView, View noResultRl) {
        if (listView.getVisibility() == View.VISIBLE) {
            listView.setVisibility(View.GONE);
            noResultRl.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            noResultRl.setVisibility(View.GONE);
        }
    }

    public void showNoResultFoundView(ViewGroup listView, View noResultRl) {
        listView.setVisibility(View.GONE);
        noResultRl.setVisibility(View.VISIBLE);
    }

    public void hideNoResultFoundView(ViewGroup listView, View noResultRl) {
        listView.setVisibility(View.VISIBLE);
        noResultRl.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity=getActivity();
    }

    protected void initView(View view)
    {

    }
    protected void initVar()
    {

    }
}
