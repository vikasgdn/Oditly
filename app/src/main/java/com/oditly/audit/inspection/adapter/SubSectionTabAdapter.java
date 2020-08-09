package com.oditly.audit.inspection.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestion;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSection;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSubSection;
import com.oditly.audit.inspection.ui.activty.SubSectionsActivity;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubSectionTabAdapter extends RecyclerView.Adapter<SubSectionTabAdapter.SubSectionTabViewHolder> {
    private Context context;
    private ArrayList<BrandStandardSection> data;
    CustomItemClickListener customItemClickListener;
    private String editable = "";

    public SubSectionTabAdapter(Context context, ArrayList<BrandStandardSection> data, String editable, CustomItemClickListener customItemClickListener) {
        this.context = context;
        this.data = data;
        this.editable = editable;
        this.customItemClickListener = customItemClickListener;
    }

    @Override
    public SubSectionTabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_section_tab_layout, parent, false);

        return new SubSectionTabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SubSectionTabViewHolder holder, final int position) {
        //TODO : Static data testing

        final BrandStandardSection brandStandardSection = data.get(position);
        int[] result = questionCount(brandStandardSection);
        int totalCount = result[0];
        int count = result[1];
        int isPartiallyFilled = result[2];
        int naFilled = result[3];
        holder.tvSubSectionTitle.setText(brandStandardSection.getSection_title());
        holder.tvSubSectionTitle.setSelected(true);
        holder.tvQuestionCount.setText("Question: " + count + "/" + totalCount);

        if (editable.equals("0")) {
            holder.naCheckBox.setEnabled(true);
        } else {
            holder.naCheckBox.setEnabled(false);
        }

        if (isPartiallyFilled == 1 || count < totalCount && count != 0) {
            holder.tvSubSectionStatus.setText("Pending");
            holder.tvSubSectionIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.pending_status));
            holder.tvSubSectionStatus.setTextColor(context.getResources().getColor(R.color.c_orange));

        } else if (count == 0) {
            holder.tvSubSectionStatus.setText("Start");
            holder.tvSubSectionIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.start_status));
            holder.tvSubSectionStatus.setTextColor(context.getResources().getColor(R.color.c_red));
        } else {
            holder.tvSubSectionStatus.setText("Completed");
            holder.tvSubSectionIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.complete_status));
            holder.tvSubSectionStatus.setTextColor(context.getResources().getColor(R.color.c_green));
        }

        if (naFilled == totalCount) {
            holder.naCheckBox.setChecked(true);
        }

        holder.llSubSectionBorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customItemClickListener.onItemClick(data, brandStandardSection.getAudit_section_file_cnt(), position);
            }
        });


        holder.naCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.naCheckBox.isChecked()) {
                    AppDialogs.messageDialogWithNA(holder,brandStandardSection,SubSectionTabAdapter.this,(SubSectionsActivity)context);
                } else {
                 //   Toast.makeText(context,"IN SIDE NO CHECKED",Toast.LENGTH_SHORT).show();
                    holder.tvSubSectionStatus.setText("Start");
                    holder.tvSubSectionIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.start_status));
                    holder.tvSubSectionStatus.setTextColor(context.getResources().getColor(R.color.c_red));
                    if (brandStandardSection.getQuestions() != null && brandStandardSection.getQuestions().size() > 0) {
                        for (int i = 0; i < brandStandardSection.getQuestions().size(); i++) {
                            BrandStandardQuestion question = brandStandardSection.getQuestions().get(i);
                            question.setAudit_answer_na(0);
                        }
                    }
                    if (brandStandardSection.getSub_sections() != null && brandStandardSection.getSub_sections().size() > 0) {
                        for (int i = 0; i < brandStandardSection.getSub_sections().size(); i++) {
                            BrandStandardSubSection subSection = brandStandardSection.getSub_sections().get(i);
                            if (subSection.getQuestions() != null && subSection.getQuestions().size() > 0) {
                                for (int j = 0; j < subSection.getQuestions().size(); j++) {
                                    BrandStandardQuestion question = subSection.getQuestions().get(j);
                                    question.setAudit_answer_na(0);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
    public void markSectionAsNotApplicableCancel(SubSectionTabViewHolder holder)
    {
        holder.naCheckBox.setChecked(false);
    }

        public void markSectionAsNotApplicable(SubSectionTabViewHolder holder, BrandStandardSection brandStandardSection) {
        JSONArray jsonArray =new JSONArray();
        holder.tvSubSectionStatus.setText("Completed");
        holder.tvSubSectionIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.complete_status));
        holder.tvSubSectionStatus.setTextColor(context.getResources().getColor(R.color.c_green));
        if (brandStandardSection.getQuestions() != null && brandStandardSection.getQuestions().size() > 0) {
            for (int i = 0; i < brandStandardSection.getQuestions().size(); i++) {
                BrandStandardQuestion question = brandStandardSection.getQuestions().get(i);
                question.setAudit_option_id(new ArrayList<Integer>());
                question.setAudit_answer_na(1);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("question_id", question.getQuestion_id());
                    jsonObject.put("audit_answer_na",question.getAudit_answer_na());
                    jsonObject.put("audit_comment", question.getAudit_comment());
                    jsonObject.put("audit_option_id", new JSONArray());
                    jsonObject.put("audit_answer", question.getAudit_answer());
                    jsonArray.put(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (brandStandardSection.getSub_sections() != null && brandStandardSection.getSub_sections().size() > 0) {
            for (int i = 0; i < brandStandardSection.getSub_sections().size(); i++) {
                BrandStandardSubSection subSection = brandStandardSection.getSub_sections().get(i);
                if (subSection.getQuestions() != null && subSection.getQuestions().size() > 0) {
                    for (int j = 0; j < subSection.getQuestions().size(); j++) {
                        BrandStandardQuestion question = subSection.getQuestions().get(j);
                        question.setAudit_option_id(new ArrayList<Integer>());
                        question.setAudit_answer_na(1);

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("question_id", question.getQuestion_id());
                            jsonObject.put("audit_answer_na", question.getAudit_answer_na());
                            jsonObject.put("audit_comment", question.getAudit_comment());
                            jsonObject.put("audit_option_id", question.getAudit_option_id());
                            jsonObject.put("audit_answer", question.getAudit_answer());
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        // saving N/A to the server
        ((SubSectionsActivity)context).saveBrandStandardQuestionForNA(jsonArray);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SubSectionTabViewHolder extends RecyclerView.ViewHolder {


        TextView tvSubSectionTitle;
        TextView tvQuestionText;
        TextView tvSubSectionStatus;
        ImageView tvSubSectionIcon;
        TextView tvQuestionCount;
        LinearLayout llSubSectionBorder;
        CheckBox naCheckBox;

        public SubSectionTabViewHolder(View itemView) {
            super(itemView);

            tvSubSectionTitle = itemView.findViewById(R.id.tv_sub_section_title);
            tvQuestionText = itemView.findViewById(R.id.tv_question_text);
            tvSubSectionStatus = itemView.findViewById(R.id.tv_sub_section_status);
            tvSubSectionIcon = itemView.findViewById(R.id.iv_sub_section_icon);
            tvQuestionCount = itemView.findViewById(R.id.tv_question_count);
            llSubSectionBorder = itemView.findViewById(R.id.ll_sub_section_border);
            naCheckBox = itemView.findViewById(R.id.cb_brand_standard_na);
        }
    }

    private int[] questionCount(BrandStandardSection brandStandardSection) {
        int totalCount = 0;
        int count = 0;
        int isPartiallyFilled = 0;
        int naFilled = 0;

        //totalCount = brandStandardSection.getQuestions().size();

        for (int i = 0; i < brandStandardSection.getQuestions().size(); i++) {
            if (brandStandardSection.getQuestions().get(i).getAudit_option_id().size() != 0
                    || brandStandardSection.getQuestions().get(i).getAudit_answer_na() == 1
                    || !AppUtils.isStringEmpty(brandStandardSection.getQuestions().get(i).getAudit_answer())) {
                count += 1;
            }
            if (brandStandardSection.getQuestions().get(i).getAudit_answer_na() == 1) {
                naFilled += 1;
            }
            if (brandStandardSection.getQuestions().get(i).getAnswer_status() == 3) {
                isPartiallyFilled = 1;
            }
            /*if (brandStandardSection.getQuestions().get(i).getQuestion_type().equals("textarea")){
                if (!AppUtils.isStringEmpty(brandStandardSection.getQuestions().get(i).getAudit_answer())){
                    count+=1;
                }
                if (brandStandardSection.getQuestions().get(i).getAudit_answer_na() == 1
                        && !AppUtils.isStringEmpty(brandStandardSection.getQuestions().get(i).getAudit_comment())){
                    count-=1;
                }
            }*/
            totalCount += 1;

        }

        for (int i = 0; i < brandStandardSection.getSub_sections().size(); i++) {
            for (int j = 0; j < brandStandardSection.getSub_sections().get(i).getQuestions().size(); j++) {
                if (brandStandardSection.getSub_sections().get(i).getQuestions().get(j).getAudit_option_id().size() != 0
                        || brandStandardSection.getSub_sections().get(i).getQuestions().get(j).getAudit_answer_na() == 1
                        || !AppUtils.isStringEmpty(brandStandardSection.getSub_sections().get(i).getQuestions().get(j).getAudit_answer())) {
                    count += 1;

                }
                if (brandStandardSection.getSub_sections().get(i).getQuestions().get(j).getAudit_answer_na() == 1) {
                    naFilled += 1;
                }
                if (brandStandardSection.getSub_sections().get(i).getQuestions().get(j).getAnswer_status() == 3) {
                    isPartiallyFilled = 1;
                }
                /*if (brandStandardSection.getSub_sections().get(i).getQuestions().get(j).getQuestion_type().equals("textarea")){
                    if (!AppUtils.isStringEmpty(brandStandardSection.getSub_sections().get(i).getQuestions().get(j).getAudit_answer())){
                        count+=1;
                    }
                }*/
                totalCount += 1;

            }
        }

        return new int[]{totalCount, count, isPartiallyFilled, naFilled};
    }

    public interface CustomItemClickListener {
        void onItemClick(ArrayList<BrandStandardSection> brandStandardSections, int fileCount, int position);
    }

}
