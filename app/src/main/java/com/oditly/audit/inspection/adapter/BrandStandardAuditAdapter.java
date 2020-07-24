package com.oditly.audit.inspection.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestion;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestionsOption;
import com.oditly.audit.inspection.ui.activty.BrandStandardAuditActivity;
import com.oditly.audit.inspection.ui.activty.ImageViewActivity;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.ArrayList;
import java.util.List;


public class BrandStandardAuditAdapter extends
        RecyclerView.Adapter<BrandStandardAuditAdapter.BrandStandardAuditViewHolder> {

    private Context context;
    private ArrayList<BrandStandardQuestion> data;
    CustomItemClickListener customItemClickListener;
    private String editable;
    private String status;
    private List<String> mImgList;


    public BrandStandardAuditAdapter(Context context, ArrayList<BrandStandardQuestion> data, CustomItemClickListener customItemClickListener, String editable, String status, List<String> imgList) {
        this.context = context;
        this.data = data;
        this.editable = editable;
        this.status = status;
        this.customItemClickListener = customItemClickListener;
        mImgList=imgList;

    }

    @Override
    public BrandStandardAuditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brand_standard_audit_layout, parent, false);

        return new BrandStandardAuditViewHolder(view);
    }

   // public BrandStandardAuditViewHolder holderPub;
    @Override
    public void onBindViewHolder(final BrandStandardAuditViewHolder holder, final int position) {
        //TODO : Static data testing
    //    holderPub=holder;
        Log.e("=========POSITION===>>",""+position);
        holder.mCommentHideShowLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.comment.requestFocus();
                if(holder.comment.isShown())
                    holder.comment.setVisibility(View.GONE);
                else
                    holder.comment.setVisibility(View.VISIBLE);
            }
        });


      /*  if(mImgList.size()>0)
            holder.mRecyclerView.setVisibility(View.VISIBLE);
        else
            holder.mRecyclerView.setVisibility(View.GONE);
*/

      //  AddAttachmentAdapterImage_Vik adapterImage_vik=new AddAttachmentAdapterImage_Vik(context,mImgList);
      //  holder.mRecyclerView.setAdapter(adapterImage_vik);


        final BrandStandardQuestion brandStandardQuestion = data.get(position);
        ((BrandStandardAuditActivity) context).questionCount = ((BrandStandardAuditActivity) context).questionCount + 1;
        holder.questionTitle.setText("" + ((BrandStandardAuditActivity) context).questionCount + ". " + brandStandardQuestion.getQuestion_title());
        holder.brandStandardAddFile.setText(context.getString(R.string.text_photo)+ " (" + brandStandardQuestion.getAudit_question_file_cnt()+")");
       // holder.mCommentHideShow.setText(context.getString(R.string.text_comment));


        if (!AppUtils.isStringEmpty(brandStandardQuestion.getHint())) {
            holder.hintLayout.setVisibility(View.VISIBLE);
            holder.note.setText(brandStandardQuestion.getHint());
        } else {
            holder.hintLayout.setVisibility(View.GONE);
        }

        if (AppUtils.isStringEmpty(brandStandardQuestion.getReviewer_answer_comment())) {
            holder.rejectedComment.setVisibility(View.GONE);

        } else {
            holder.rejectedComment.setVisibility(View.VISIBLE);
            holder.rejectedComment.setText("Reviewer Comment:- " + brandStandardQuestion.getReviewer_answer_comment());
        }

        holder.bsLayout.setFocusable(true);
        holder.bsLayout.setFocusableInTouchMode(true);

        if (editable.equals("0")) {
            enableView(holder);
        } else {
            disableView(holder);
        }

        if (AppUtils.isStringEmpty(brandStandardQuestion.getRef_image_url())) {
            holder.referenceImageTab.setVisibility(View.INVISIBLE);
            holder.referenceImageTab.setEnabled(false);
        } else {
            holder.referenceImageTab.setVisibility(View.VISIBLE);
            holder.referenceImageTab.setEnabled(true);
        }
        holder.referenceImageTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("fileUrl", brandStandardQuestion.getRef_image_url());
                context.startActivity(intent);
           }
        });

        holder.comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!AppUtils.isStringEmpty(brandStandardQuestion.getQuestion_type()) && brandStandardQuestion.getQuestion_type().equals("textarea")) {
                    if (brandStandardQuestion.getAudit_answer_na() == 0) {
                        AppLogger.e("AuditCommment", "" + editable.toString());
                        brandStandardQuestion.setAudit_answer("" + editable.toString());
                    } else {
                        AppLogger.e("AuditCommment", "" + editable.toString());
                        brandStandardQuestion.setAudit_comment("" + editable.toString());
                    }
                } else {
                    AppLogger.e("AuditCommment", "" + editable.toString());
                    brandStandardQuestion.setAudit_comment("" + editable.toString());
                }
                holder.comment.clearFocus();
            }
        });

        addAnswerList(brandStandardQuestion, holder, brandStandardQuestion.getQuestion_type(), brandStandardQuestion.getAudit_option_id());

        holder.brandStandardAddFileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.comment.requestFocus();
                int count = Integer.parseInt(holder.questionTitle.getText().toString().substring(0, holder.questionTitle.getText().toString().indexOf(".")));
                AppLogger.e("Count", "" + count);
                AppLogger.e("position", "" + position);
                AppLogger.e("Count_position", "" + (count - position - 1));
                customItemClickListener.onItemClick(count - 1,
                        BrandStandardAuditAdapter.this, brandStandardQuestion.getQuestion_id(), "bsQuestion", position);

            }
        });

        AppLogger.e("QuestionType:", brandStandardQuestion.getQuestion_type());
        if (!AppUtils.isStringEmpty(brandStandardQuestion.getQuestion_type()) && (brandStandardQuestion.getQuestion_type().equals("textarea") || brandStandardQuestion.getQuestion_type().equals("text"))) {
            if (brandStandardQuestion.getAudit_answer_na() == 0) {
                holder.naBtn.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_unselect_btn));
                holder.naBtn.setTextColor(context.getResources().getColor(R.color.c_dark_gray));
            } else {
                holder.naBtn.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_na_select_btn));
                holder.naBtn.setTextColor(context.getResources().getColor(R.color.c_white));
            }
           // holder.comment.setVisibility(View.VISIBLE);
            if (!AppUtils.isStringEmpty(brandStandardQuestion.getAudit_answer())) {
                holder.comment.setText(brandStandardQuestion.getAudit_answer());
            }else if (!AppUtils.isStringEmpty(brandStandardQuestion.getAudit_comment())) {
                holder.comment.setText(brandStandardQuestion.getAudit_comment());
            }
            holder.naBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    BrandStandardAuditActivity.isAnswerCliked=true;
                    if (brandStandardQuestion.getAudit_answer_na() == 1) {
                        holder.naBtn.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_unselect_btn));
                        holder.naBtn.setTextColor(context.getResources().getColor(R.color.c_dark_gray));
                        brandStandardQuestion.setAudit_answer_na(0);
                    } else {
                        holder.naBtn.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_na_select_btn));
                        holder.naBtn.setTextColor(context.getResources().getColor(R.color.c_white));
                        brandStandardQuestion.setAudit_answer_na(1);
                        //holder.comment.setText("");
                    }

                    ((BrandStandardAuditActivity) context).countNA_Answers();
                }
            });
        } else {
            if (!AppUtils.isStringEmpty(brandStandardQuestion.getAudit_comment())) {
              //  holder.comment.setVisibility(View.VISIBLE);
                holder.comment.setText(brandStandardQuestion.getAudit_comment());
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class BrandStandardAuditViewHolder extends RecyclerView.ViewHolder {

        TextView questionTitle;
        TextView note;
        TextView rejectedComment;
        EditText comment;
        LinearLayout referenceImageTab;
     //   TextView mCommentHideShow;
        LinearLayout mCommentHideShowLL;
        //CheckBox naCheckBox;
        TextView naBtn;
        TextView brandStandardAddFile;
        LinearLayout brandStandardAddFileLayout;
        LinearLayout noteLayout;
        LinearLayout optionListLinearLayout;
        LinearLayout bsLayout;
        RelativeLayout btnLayout;
        LinearLayout hintLayout;
        RecyclerView mRecyclerView;


        public BrandStandardAuditViewHolder(View itemView) {
            super(itemView);
           // mCommentHideShow=itemView.findViewById(R.id.tv_comment);
            mCommentHideShowLL=itemView.findViewById(R.id.ll_comment);
            questionTitle = itemView.findViewById(R.id.tv_bs_title);
            note = itemView.findViewById(R.id.tv_bs_note);
            hintLayout = itemView.findViewById(R.id.ll_note_layout);
            rejectedComment = itemView.findViewById(R.id.tv_bs_rejected_comment);
            comment = itemView.findViewById(R.id.tv_bs_comment);
            referenceImageTab = itemView.findViewById(R.id.bs_reference_image_tab);
            optionListLinearLayout = itemView.findViewById(R.id.rv_brand_standard_answer);
            naBtn = itemView.findViewById(R.id.tv_na_btn);
            brandStandardAddFile = itemView.findViewById(R.id.bs_add_file_btn);
            brandStandardAddFileLayout = itemView.findViewById(R.id.ll_bs_add_file_btn);
            //noteLayout = itemView.findViewById(R.id.ll_note_layout);
            bsLayout = itemView.findViewById(R.id.bs_layout);
            btnLayout = itemView.findViewById(R.id.reference_btn_layout);

            mRecyclerView=(RecyclerView)itemView.findViewById(R.id.rv_imagelist);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);


        }
    }

    private void enableView(BrandStandardAuditViewHolder holder) {
        holder.naBtn.setEnabled(true);
        holder.comment.setEnabled(true);
       // holder.addBtn.setText("+");
    }

    private void disableView(BrandStandardAuditViewHolder holder) {
        holder.naBtn.setEnabled(false);
        holder.comment.setEnabled(false);
      //  holder.addBtn.setText("");
    }

    private void addAnswerList(final BrandStandardQuestion brandStandardQuestion,
                               final BrandStandardAuditViewHolder holder, final String questionType,
                               final ArrayList<Integer> answerOptionId) {

        final ArrayList<BrandStandardQuestionsOption> arrayList = new ArrayList<>();
        arrayList.addAll(brandStandardQuestion.getOptions());
        holder.optionListLinearLayout.removeAllViews();

        for (int i = 0; i < arrayList.size(); i++) {

            final BrandStandardQuestionsOption brandStandardQuestionsOption = arrayList.get(i);
            final View view = ((BrandStandardAuditActivity) context).inflater.inflate(R.layout.brand_standard_audit_rdo_quesn, null);

            final TextView answerText = view.findViewById(R.id.radio_text);

            if (editable.equals("0")) {
                answerText.setEnabled(true);
            } else {
                answerText.setEnabled(false);
            }

            if (brandStandardQuestion.getAudit_answer_na() == 1) {
                holder.naBtn.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_na_select_btn));
                holder.naBtn.setTextColor(context.getResources().getColor(R.color.c_white));
                answerText.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_unselect_btn));
                answerText.setEnabled(false);
                //holder.naCheckBox.setChecked(true);
            } else {
                //holder.naCheckBox.setChecked(false);
                holder.naBtn.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_unselect_btn));
                holder.naBtn.setTextColor(context.getResources().getColor(R.color.c_dark_gray));
                answerText.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_btn_border));
                answerText.setEnabled(true);
            }

            answerText.setText(String.valueOf(brandStandardQuestionsOption.getOption_text()));
           // Toast.makeText(context,"answer==> "+brandStandardQuestionsOption.getOption_text(),Toast.LENGTH_SHORT).show();
            AppLogger.e("questionType:", ":" + questionType);
            if (questionType.equals("radio")) {

                if (brandStandardQuestion.getAudit_answer_na() == 0) {
                    if (brandStandardQuestionsOption.getSelected() == 1) {
                        if (brandStandardQuestionsOption.getOption_mark() != 0) {
                            answerText.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_select_btn));
                            answerText.setTextColor(context.getResources().getColor(R.color.c_white));
                        } else {
                            answerText.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_select_no_btn));
                            answerText.setTextColor(context.getResources().getColor(R.color.c_white));
                        }
                    }
                }

                answerText.setTag(brandStandardQuestionsOption.getOption_id());
                answerText.setId(i);

                answerText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BrandStandardAuditActivity.isAnswerCliked=true;
                        holder.comment.requestFocus();
                    //    Toast.makeText(context,"Click on Answer yes no",Toast.LENGTH_SHORT).show();
                        int optionId = brandStandardQuestionsOption.getOption_id();
                        for (int j = 0; j < arrayList.size(); j++)
                        {
                            TextView radio_text = holder.optionListLinearLayout.findViewById(j);
                            if (radio_text.equals(answerText)) {
                                answerOptionId.clear();
                                answerOptionId.add(optionId);
                                //holder.comment.setText("");
                                if (brandStandardQuestionsOption.getOption_mark() != 0) {
                                    answerText.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_select_btn));
                                    answerText.setTextColor(context.getResources().getColor(R.color.colorWhite));
                                } else {
                                    answerText.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_select_no_btn));
                                    answerText.setTextColor(context.getResources().getColor(R.color.colorWhite));
                                }
                            } else {
                                radio_text.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_btn_border));
                                radio_text.setTextColor(context.getResources().getColor(R.color.c_dark_gray));
                            }
                        }

                        holder.naBtn.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_unselect_btn));
                        holder.naBtn.setTextColor(context.getResources().getColor(R.color.c_dark_gray));
                        brandStandardQuestion.setAudit_answer_na(0);
                        ((BrandStandardAuditActivity) context).countNA_Answers();
                    }
                });

            }
            else {

                int optionId = brandStandardQuestionsOption.getOption_id();
                if (brandStandardQuestion.getAudit_answer_na() == 0)
                {
                    for (int j = 0; j < answerOptionId.size(); j++) {
                        if (optionId == answerOptionId.get(j)) {
                            answerText.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_select_btn));
                            answerText.setTextColor(context.getResources().getColor(R.color.c_white));
                            for (int k = 0; k < arrayList.size(); k++) {
                                if (arrayList.get(k).getOption_id() == optionId) {
                                    arrayList.get(k).setChecked(1);
                                    break;
                                }
                            }

                            break;
                        }
                    }
                }
                answerText.setTag(brandStandardQuestionsOption.getOption_id());
                answerText.setId(i);
               // holder.comment.setVisibility(View.VISIBLE);
                answerText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BrandStandardAuditActivity.isAnswerCliked=true;
                        holder.comment.requestFocus();
                        AppLogger.e("OptionId", "" + brandStandardQuestionsOption.getOption_id());
                        int optionId = brandStandardQuestionsOption.getOption_id();

                        holder.naBtn.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_unselect_btn));
                        holder.naBtn.setTextColor(context.getResources().getColor(R.color.c_dark_gray));
                        brandStandardQuestion.setAudit_answer_na(0);

                        for (int j = 0; j < arrayList.size(); j++) {
                            TextView checkBoxText = holder.optionListLinearLayout.findViewById(j);

                            if (checkBoxText.equals(answerText)) {
                                if (arrayList.get(j).getChecked() == 0) {
                                    answerText.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_select_btn));
                                    answerText.setTextColor(context.getResources().getColor(R.color.c_white));
                                    arrayList.get(j).setChecked(1);
                                    answerOptionId.add(optionId);
                                } else {
                                    answerText.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_btn_border));
                                    answerText.setTextColor(context.getResources().getColor(R.color.c_dark_gray));
                                    arrayList.get(j).setChecked(0);
                                    //answerOptionId.remove(new Integer(optionId));
                                    for (int k = 0; k < answerOptionId.size(); k++) {
                                        if (answerOptionId.get(k) == optionId) {
                                            answerOptionId.remove(k);
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        ((BrandStandardAuditActivity) context).countNA_Answers();
                    }

                });
            }

            holder.naBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BrandStandardAuditActivity.isAnswerCliked=true;
                 //   Toast.makeText(context,"n/A  button click ",Toast.LENGTH_SHORT).show();
                    holder.comment.requestFocus();
                    //holder.comment.setText("");
                    if (brandStandardQuestion.getAudit_answer_na() == 1) {
                      //  holder.comment.setVisibility(View.VISIBLE);
                        //holder.comment.setMinLines(1);
                        //answerOptionId.clear();
                        holder.naBtn.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_unselect_btn));
                        holder.naBtn.setTextColor(context.getResources().getColor(R.color.c_dark_gray));
                        brandStandardQuestion.setAudit_answer_na(0);
                        if (questionType.equals("radio"))
                        {
                            for (int i = 0; i < arrayList.size(); i++)
                            {
                                TextView textRadio = holder.optionListLinearLayout.findViewWithTag(arrayList.get(i).getOption_id());
                                textRadio.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_btn_border));
                                textRadio.setTextColor(context.getResources().getColor(R.color.c_dark_gray));

                                //textRadio.setEnabled(true);
                            }
                        }
                        else {
                            for (int i = 0; i < arrayList.size(); i++) {
                                TextView textCheckBox = holder.optionListLinearLayout.findViewWithTag(arrayList.get(i).getOption_id());
                                textCheckBox.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_btn_border));
                                textCheckBox.setTextColor(context.getResources().getColor(R.color.c_dark_gray));
                                // textCheckBox.setEnabled(true);
                            }
                        }
                    } else {
                        holder.naBtn.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_na_select_btn));
                        holder.naBtn.setTextColor(context.getResources().getColor(R.color.c_white));
                        answerOptionId.clear();
                        brandStandardQuestion.setAudit_answer_na(1);
                        if (questionType.equals("radio")) {
                            for (int i = 0; i < arrayList.size(); i++) {
                                TextView textRadio = holder.optionListLinearLayout.findViewWithTag(arrayList.get(i).getOption_id());
                                textRadio.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_unselect_btn));
                                textRadio.setTextColor(context.getResources().getColor(R.color.c_dark_gray));
                                //textRadio.setEnabled(false);
                            }
                        } else {
                            for (int i = 0; i < arrayList.size(); i++) {
                                TextView textCheckBox = holder.optionListLinearLayout.findViewWithTag(arrayList.get(i).getOption_id());
                                textCheckBox.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_unselect_btn));
                                textCheckBox.setTextColor(context.getResources().getColor(R.color.c_dark_gray));
                                arrayList.get(i).setChecked(0);
                                //textCheckBox.setEnabled(false);
                            }
                        }
                    }

                    ((BrandStandardAuditActivity) context).countNA_Answers();
                }
            });

            if (status.equals("3")) {
                if (AppUtils.isStringEmpty(brandStandardQuestion.getReviewer_answer_comment())) {
                    if (questionType.equals("radio")) {
                        for (int j = 0; j < arrayList.size(); j++) {
                            //RadioButton radioButton = holder.answerList.findViewWithTag(arrayList.get(i).getOption_id());
                            if (holder.optionListLinearLayout.findViewById(j) instanceof RadioButton) {
                                RadioButton radioButton = holder.optionListLinearLayout.findViewById(j);
                                if (radioButton != null)
                                    radioButton.setEnabled(false);
                            }
                        }
                        answerText.setEnabled(false);
                        answerText.setTextColor(context.getResources().getColor(R.color.c_white));
                        holder.comment.setEnabled(false);
                        holder.naBtn.setEnabled(false);
                    } else {
                        for (int j = 0; j < arrayList.size(); j++) {
                            //CheckBox checkBox = holder.answerList.findViewWithTag(arrayList.get(i).getOption_id());
                            CheckBox checkBox = holder.optionListLinearLayout.findViewById(j);
                            checkBox.setEnabled(false);
                        }
                        answerText.setEnabled(false);
                        answerText.setTextColor(context.getResources().getColor(R.color.c_white));
                        holder.comment.setEnabled(false);
                        holder.naBtn.setEnabled(false);
                    }
                }
            }
            holder.optionListLinearLayout.addView(view);
        }
    }

    public ArrayList<BrandStandardQuestion> getArrayList() {
        return data;
    }

    public interface CustomItemClickListener {
        void onItemClick(int count, BrandStandardAuditAdapter brandStandardAuditAdapter, int bsQuestionId, String attachtype, int position);
    }

    public void setattachmentCount(int count, int pos)
    {
        data.get(pos).setAudit_question_file_cnt(count);
        //notifyDataSetChanged();
       //   notifyItemChanged(pos);
       // Toast.makeText(context,"position==>"+pos,Toast.LENGTH_SHORT).show();
        //((BrandStandardAuditActivity) context).questionCount = 0;
    }


}
