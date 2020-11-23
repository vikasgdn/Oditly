package com.oditly.audit.inspection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestion;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSection;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSubSection;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.ArrayList;

public class SubSectionAdapter extends RecyclerView.Adapter<SubSectionAdapter.SubSectionTabViewHolder> {
    private Context context;
    private ArrayList<BrandStandardSection> data;
    CustomItemClickListener customItemClickListener;

    public SubSectionAdapter(Context context, ArrayList<BrandStandardSection> data, CustomItemClickListener customItemClickListener) {
        this.context = context;
        this.data = data;
        this.customItemClickListener = customItemClickListener;
    }

    @Override
    public SubSectionTabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_section_tab_layout, parent, false);

        return new SubSectionTabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SubSectionTabViewHolder holder, final int position) {
        final BrandStandardSection brandStandardSection = data.get(position);
        int totalQuestionCount = questionCount(brandStandardSection);
        int answerdCount = brandStandardSection.getAnswered_question_count();

        holder.tvSubSectionTitle.setText(brandStandardSection.getSection_title());
        holder.tvSubSectionTitle.setSelected(true);
        holder.tvQuestionCount.setText("Question: " + answerdCount + "/" + totalQuestionCount);
        if (answerdCount == 0) {
            holder.tvSubSectionStatus.setText("Start");
            holder.tvSubSectionIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.start_status));
            holder.tvSubSectionStatus.setTextColor(context.getResources().getColor(R.color.c_red));
        } else if (answerdCount < totalQuestionCount) {
            holder.tvSubSectionStatus.setText("Pending");
            holder.tvSubSectionIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.pending_status));
            holder.tvSubSectionStatus.setTextColor(context.getResources().getColor(R.color.c_orange));
        } else {
            holder.tvSubSectionStatus.setText("Completed");
            holder.tvSubSectionIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.complete_status));
            holder.tvSubSectionStatus.setTextColor(context.getResources().getColor(R.color.c_green));
          //  holder.naCheckBox.setChecked(true);
        }
        holder.llSubSectionBorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customItemClickListener.onItemClick(data, brandStandardSection.getAudit_section_file_cnt(), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SubSectionTabViewHolder extends RecyclerView.ViewHolder {


        TextView tvSubSectionTitle;
        TextView tvSubSectionStatus;
        ImageView tvSubSectionIcon;
        TextView tvQuestionCount;
        LinearLayout llSubSectionBorder;
      //  CheckBox naCheckBox;

        public SubSectionTabViewHolder(View itemView) {
            super(itemView);

            tvSubSectionTitle = itemView.findViewById(R.id.tv_sub_section_title);
            tvSubSectionStatus = itemView.findViewById(R.id.tv_sub_section_status);
            tvSubSectionIcon = itemView.findViewById(R.id.iv_sub_section_icon);
            tvQuestionCount = itemView.findViewById(R.id.tv_question_count);
            llSubSectionBorder = itemView.findViewById(R.id.ll_sub_section_border);
         //   naCheckBox = itemView.findViewById(R.id.cb_brand_standard_na);
        }
    }

    private int questionCount(BrandStandardSection brandStandardSection) {
        int totalCount = 0;
        totalCount = brandStandardSection.getQuestions().size();
        for (int i = 0; i < brandStandardSection.getSub_sections().size(); i++)
        {
           BrandStandardSubSection subSection = brandStandardSection.getSub_sections().get(i);
           totalCount = totalCount + subSection.getQuestions().size();
        }
        return totalCount;
    }

    public interface CustomItemClickListener {
        void onItemClick(ArrayList<BrandStandardSection> brandStandardSections, int fileCount, int position);
    }

}
