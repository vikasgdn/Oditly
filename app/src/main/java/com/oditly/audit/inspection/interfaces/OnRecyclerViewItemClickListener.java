package com.oditly.audit.inspection.interfaces;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Vikas on 23/06/2020.
 */

public interface OnRecyclerViewItemClickListener {
    void onItemClick(RecyclerView.Adapter adapter, View v, int position);
}
