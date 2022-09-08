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
        // int totalQuestionCount = questionCount(brandStandardSection);
        int totalQuestionCount = brandStandardSection.getQuestion_count();
        int answerdCount = brandStandardSection.getAnswered_question_count();
        if (brandStandardSection.getTotal_obtained_mark()>0) {
            float sectionScore = ((brandStandardSection.getTotal_obtained_mark() / brandStandardSection.getTotal_max_mark()) * 100);
            holder.tvScore.setText(context.getResources().getString(R.string.text_score)+": " + String.format("%.1f", sectionScore) + "%");
        }
        else
            holder.tvScore.setText(context.getResources().getString(R.string.text_score)+": 0%");


        holder.tvSubSectionTitle.setText(brandStandardSection.getSection_title());
        holder.tvSectionTitle.setText(brandStandardSection.getSection_group_title());
        holder.tvSubSectionTitle.setSelected(true);
        holder.tvSectionTitle.setSelected(true);

        if (answerdCount>totalQuestionCount)
            answerdCount=totalQuestionCount;

        holder.tvQuestionCount.setText(context.getResources().getString(R.string.text_question)+" " + answerdCount + "/" + totalQuestionCount);
        if (answerdCount == 0) {
            holder.tvSubSectionStatus.setText(context.getResources().getString(R.string.text_start));
            holder.tvSubSectionIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.start_status1));
            holder.tvSubSectionStatus.setTextColor(context.getResources().getColor(R.color.c_red));
        } else if (answerdCount < totalQuestionCount) {
            holder.tvSubSectionStatus.setText(context.getResources().getString(R.string.text_pending));
            holder.tvSubSectionIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.pending_status1));
            holder.tvSubSectionStatus.setTextColor(context.getResources().getColor(R.color.c_orange));
        } else {
            holder.tvSubSectionStatus.setText(context.getResources().getString(R.string.text_complete));
            holder.tvSubSectionIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.complete_status1));
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
        TextView tvScore;
        TextView tvSectionTitle;

        LinearLayout llSubSectionBorder;


        //  CheckBox naCheckBox;

        public SubSectionTabViewHolder(View itemView) {
            super(itemView);

            tvSectionTitle = itemView.findViewById(R.id.tv_section_title);
            tvSubSectionTitle = itemView.findViewById(R.id.tv_sub_section_title);
            tvSubSectionStatus = itemView.findViewById(R.id.tv_sub_section_status);
            tvSubSectionIcon = itemView.findViewById(R.id.iv_sub_section_icon);
            tvQuestionCount = itemView.findViewById(R.id.tv_question_count);
            llSubSectionBorder = itemView.findViewById(R.id.ll_sub_section_border);
            tvScore = itemView.findViewById(R.id.tv_score);
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
