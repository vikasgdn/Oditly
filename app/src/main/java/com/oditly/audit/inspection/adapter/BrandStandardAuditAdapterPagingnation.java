package com.oditly.audit.inspection.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammedalaa.seekbar.OnRangeSeekBarChangeListener;
import com.mohammedalaa.seekbar.RangeSeekBarView;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestion;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestionsOption;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSlider;
import com.oditly.audit.inspection.ui.activty.BrandStandardAuditActivityPagingnation;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.ArrayList;


public class BrandStandardAuditAdapterPagingnation extends RecyclerView.Adapter<BrandStandardAuditAdapterPagingnation.BrandStandardAuditViewHolder> {

    private Context context;
    private ArrayList<BrandStandardQuestion> data;
    CustomItemClickListener customItemClickListener;

    public BrandStandardAuditAdapterPagingnation(Context context, ArrayList<BrandStandardQuestion> data, CustomItemClickListener customItemClickListener) {
        this.context = context;
        this.data = data;
        this.customItemClickListener = customItemClickListener;
    }

    @Override
    public BrandStandardAuditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brand_standard_audit_layout, parent, false);

        return new BrandStandardAuditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BrandStandardAuditViewHolder holder, final int position) {

        final BrandStandardQuestion brandStandardQuestion = data.get(position);
        holder.parentLayout.clearFocus();

        ((BrandStandardAuditActivityPagingnation) context).questionCount = ((BrandStandardAuditActivityPagingnation) context).questionCount + 1;
        holder.questionTitle.setText("" + ((BrandStandardAuditActivityPagingnation) context).questionCount + ". " + brandStandardQuestion.getQuestion_title());
        holder.mMediaLabelTV.setText(context.getString(R.string.text_photo)+ " (" + brandStandardQuestion.getAudit_question_file_cnt()+")");

        if (brandStandardQuestion.getMedia_count()>0)
            holder.mMediaLabelTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_astrisk12, 0,0 , 0);
        else
            holder.mMediaLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);

        if (brandStandardQuestion.getHas_comment()>0) {
            holder.mCommentLabelTV.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_astrisk12, 0,0 , 0);
            // holder.mCommentET.setHint("");
        }
        else
            holder.mCommentLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);


        if(brandStandardQuestion.getmImageList()!=null && brandStandardQuestion.getmImageList().size()>0) {
            holder.mRecyclerView.setVisibility(View.VISIBLE);
            AddBSMediaAdapter adapterImage = new AddBSMediaAdapter(context, brandStandardQuestion.getmImageList());
            holder.mRecyclerView.setAdapter(adapterImage);
        }
        else
            holder.mRecyclerView.setVisibility(View.GONE);



        String questionType = brandStandardQuestion.getQuestion_type();
          AppLogger.e("QuestionType:", ""+questionType +" || "+holder.questionTitle.getText());

        if(questionType.equalsIgnoreCase("datetime"))
        {
            if (!TextUtils.isEmpty(brandStandardQuestion.getAudit_answer())) {
                holder.mDateTimePickerTV.setText(brandStandardQuestion.getAudit_answer());
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
            }
            setOtherViewHide(holder);
            holder.mDateTimePickerTV.setVisibility(View.VISIBLE);
            holder.mCommentMediaShowLayout.setVisibility(View.GONE);
            holder.mDateTimePickerTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedOnAnswerTpye();
                    AppUtils.datePicker(context,holder.mDateTimePickerTV,true,brandStandardQuestion);
                }
            });

        }
        else if(questionType.equalsIgnoreCase("date"))
        {
            holder.mDateTimePickerTV.setText("Select Date");
            if (!TextUtils.isEmpty(brandStandardQuestion.getAudit_answer())) {
                holder.mDateTimePickerTV.setText(brandStandardQuestion.getAudit_answer());
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
            }
            setOtherViewHide(holder);
            holder.mCommentMediaShowLayout.setVisibility(View.GONE);
            holder.mDateTimePickerTV.setVisibility(View.VISIBLE);
            holder.mDateTimePickerTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedOnAnswerTpye();
                    AppUtils.datePicker(context,holder.mDateTimePickerTV,false,brandStandardQuestion);
                }
            });

        }
        else  if(questionType.equalsIgnoreCase("textarea") || questionType.equalsIgnoreCase("text") )
        {
            if (!TextUtils.isEmpty(brandStandardQuestion.getAudit_answer())) {
                holder.mTextAnswerET.setText(brandStandardQuestion.getAudit_answer());
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
            }
            setOtherViewHide(holder);
            if (questionType.equalsIgnoreCase("textarea"))
                holder.mTextAnswerET.setMinLines(4);
            else
                holder.mTextAnswerET.setMinLines(2);

            holder.mTextAnswerET.setVisibility(View.VISIBLE);
            holder.mTextAnswerET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void afterTextChanged(Editable editable)
                {
                    clickedOnAnswerTpye();
                    brandStandardQuestion.setAudit_answer("" + editable.toString());

                }
            });

        }
        else  if(questionType.equalsIgnoreCase("number") )
        {
            if (!TextUtils.isEmpty(brandStandardQuestion.getAudit_answer())) {
                holder.mNumberDecAnsweET.setText(brandStandardQuestion.getAudit_answer());
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
            }
            setOtherViewHide(holder);
            holder.mNumberDecAnsweET.setVisibility(View.VISIBLE);
            holder.mNumberDecAnsweET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void afterTextChanged(Editable editable)
                {
                    clickedOnAnswerTpye();
                    brandStandardQuestion.setAudit_answer("" + editable.toString());

                }
            });

        }
        else  if(questionType.equalsIgnoreCase("slider") )
        {
            if (!TextUtils.isEmpty(brandStandardQuestion.getAudit_answer()) && !brandStandardQuestion.getAudit_answer().equalsIgnoreCase("0") )
            {
                holder.mFinalValueSliderTV.setText(brandStandardQuestion.getAudit_answer());
                holder.mRangeSeekBarSlider.setCurrentValue(Integer.parseInt(brandStandardQuestion.getAudit_answer()));
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
            }
            setOtherViewHide(holder);
            holder.mSliderRL.setVisibility(View.VISIBLE);

            BrandStandardSlider sliderOBJ = brandStandardQuestion.getSlider();
            if (sliderOBJ!=null)
            {
                holder.mMinValueSliderTV.setText(""+sliderOBJ.getFrom());
                holder.mMaxValueSliderTV.setText(""+sliderOBJ.getTo());
                holder.mRangeSeekBarSlider.setMaxValue(sliderOBJ.getTo());
                holder.mRangeSeekBarSlider.setMinValue(sliderOBJ.getFrom());
                holder.mRangeSeekBarSlider.setStep(sliderOBJ.getStep());
            }
            holder.mRangeSeekBarSlider.setOnRangeSeekBarViewChangeListener(new OnRangeSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(RangeSeekBarView rangeSeekBarView, int i) {
                    //  holder.mFinalValueSliderTV.setText(""+rangeSeekBarView.getCurrentValue());
                    // brandStandardQuestion.setAudit_answer(String.valueOf(rangeSeekBarView.getCurrentValue()));
                }
                @Override
                public void onStartTrackingTouch(RangeSeekBarView rangeSeekBarView, int i) {

                }
                @Override
                public void onProgressChanged(@Nullable RangeSeekBarView seekBar, int progress, boolean fromUser) {
                    clearCurrentFocus();
                    clickedOnAnswerTpye();
                    holder.mFinalValueSliderTV.setText(""+progress);
                    brandStandardQuestion.setAudit_answer(String.valueOf(progress));
                }
            });

        }
        else if(questionType.equalsIgnoreCase("radio"))
        {
            // for radio type question
            setOtherViewHide(holder);
            holder.mRadioSectionLL.setVisibility(View.VISIBLE);
            handleRadioTypeQuestion(brandStandardQuestion, holder,brandStandardQuestion.getAudit_option_id());

        }
        else if(questionType.equalsIgnoreCase("checkbox"))
        {
            // for MultiSelection type question
            setOtherViewHide(holder);
            holder.mRadioSectionLL.setVisibility(View.VISIBLE);
            handleMultiSelectionCheckBoxTypeQuestion(brandStandardQuestion, holder,brandStandardQuestion.getAudit_option_id());

        }
        else if(questionType.equalsIgnoreCase("dropdown"))
        {
            setOtherViewHide(holder);
            holder.mRadioSectionLL.setVisibility(View.VISIBLE);
            handleDropDownTypeQuestion(brandStandardQuestion, holder,brandStandardQuestion.getAudit_option_id());
        }
        else
        {
            setOtherViewHide(holder);
            holder.mRadioSectionLL.setVisibility(View.VISIBLE);
            handleRadioTypeQuestion(brandStandardQuestion, holder,brandStandardQuestion.getAudit_option_id());
        }

        if (!AppUtils.isStringEmpty(brandStandardQuestion.getAudit_comment()))
            holder.mCommentET.setText(brandStandardQuestion.getAudit_comment());

        holder.mCommentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                brandStandardQuestion.setAudit_comment("" + editable.toString());
            }
        });
        holder.mCommentHideShowLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCurrentFocus();
                if(holder.mCommentET.isShown())
                    holder.mCommentET.setVisibility(View.GONE);
                else
                    holder.mCommentET.setVisibility(View.VISIBLE);
            }
        });

        holder.brandStandardAddFileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCurrentFocus();
                int count = Integer.parseInt(holder.questionTitle.getText().toString().substring(0, holder.questionTitle.getText().toString().indexOf(".")));
                customItemClickListener.onItemClick(count - 1, BrandStandardAuditAdapterPagingnation.this, brandStandardQuestion.getQuestion_id(), "bsQuestion", position);
            }
        });

        if (!AppUtils.isStringEmpty(brandStandardQuestion.getHint()))
        {
            holder.hintLayout.setVisibility(View.VISIBLE);
            holder.note.setText("Instruction: "+brandStandardQuestion.getHint());
            holder.mCollapseExpandIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    clearCurrentFocus();
                    if (holder.note.getMaxLines()>10)
                    {
                        holder.note.setMaxLines(1);
                        holder.note.setText("Instruction: "+brandStandardQuestion.getHint());
                    }
                    else
                    {
                        holder.note.setMaxLines(12);
                        holder.note.setText("Instruction: "+brandStandardQuestion.getHint());
                    }
                }
            });
        } else
            holder.hintLayout.setVisibility(View.GONE);


        if (AppUtils.isStringEmpty(brandStandardQuestion.getReviewer_answer_comment()))
            holder.rejectedComment.setVisibility(View.GONE);
        else {
            holder.rejectedComment.setVisibility(View.VISIBLE);
            holder.rejectedComment.setText("Reviewer Comment:- " + brandStandardQuestion.getReviewer_answer_comment());
        }

        if (brandStandardQuestion.getRef_file()!=null && !AppUtils.isStringEmpty(brandStandardQuestion.getRef_file().getFile_url())) {
            holder.mShowHowLL.setVisibility(View.VISIBLE);
            holder.mShowHowLL.setEnabled(true);
            holder.mShowHowLL.setTag(brandStandardQuestion.getRef_file());
            holder.mShowHowLL.setOnClickListener((BrandStandardAuditActivityPagingnation)context);
        /*    holder.mShowHowLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AudioPlayerActivity.class);
                    intent.putExtra(AppConstant.FILE_URL, brandStandardQuestion.getRef_file().getFile_url());
                    context.startActivity(intent);
                }
            });
     */   } else {
            holder.mShowHowLL.setVisibility(View.INVISIBLE);
            holder.mShowHowLL.setEnabled(false);
        }
    }

    private void clearCurrentFocus() {
        View currentFocus = ((BrandStandardAuditActivityPagingnation)context).getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    private void clickedOnAnswerTpye() {
        BrandStandardAuditActivityPagingnation.isAnswerCliked=true;
    }

    private void setOtherViewHide(BrandStandardAuditViewHolder holder) {
        holder.mRadioSectionLL.setVisibility(View.GONE);
        holder.mTextAnswerET.setVisibility(View.GONE);
        holder.mNumberDecAnsweET.setVisibility(View.GONE);
        holder.mSliderRL.setVisibility(View.GONE);
        holder.mDateTimePickerTV.setVisibility(View.GONE);
        holder.mCommentMediaShowLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class BrandStandardAuditViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView questionTitle;
        TextView note;
        TextView rejectedComment;
        TextView mCommentLabelTV;
        TextView mMediaLabelTV;
        EditText mCommentET;
        LinearLayout mShowHowLL;
        LinearLayout mCommentHideShowLL;
        LinearLayout brandStandardAddFileLayout;
        LinearLayout mCommentMediaShowLayout;
        LinearLayout optionListLinearLayout;
        LinearLayout parentLayout;
        LinearLayout hintLayout;
        RecyclerView mRecyclerView;

        TextView mDateTimePickerTV;
        EditText mTextAnswerET;
        EditText mNumberDecAnsweET;
        LinearLayout mRadioSectionLL;
        RangeSeekBarView mRangeSeekBarSlider;
        RelativeLayout mSliderRL;
        TextView mMinValueSliderTV;
        TextView mMaxValueSliderTV;
        TextView mFinalValueSliderTV;
        ImageView mCollapseExpandIV;


        public BrandStandardAuditViewHolder(View itemView) {
            super(itemView);
            mRadioSectionLL=itemView.findViewById(R.id.ll_radiosetion);
            mTextAnswerET=itemView.findViewById(R.id.et_textanswer);
            mNumberDecAnsweET=itemView.findViewById(R.id.et_numberanswer);
            mDateTimePickerTV=itemView.findViewById(R.id.tv_datetimepicker);
            mCommentHideShowLL=itemView.findViewById(R.id.ll_comment);
            mCommentMediaShowLayout=itemView.findViewById(R.id.ll_comment_media_showhow);
            questionTitle = itemView.findViewById(R.id.tv_bs_title);
            note = (TextView) itemView.findViewById(R.id.tv_bs_note);
            hintLayout = itemView.findViewById(R.id.ll_note_layout);
            rejectedComment = itemView.findViewById(R.id.tv_bs_rejected_comment);
            mCommentET = itemView.findViewById(R.id.et_comment);
            mShowHowLL = itemView.findViewById(R.id.ll_showhow);
            optionListLinearLayout = itemView.findViewById(R.id.rv_brand_standard_answer);
            mMediaLabelTV = itemView.findViewById(R.id.tv_media);
            brandStandardAddFileLayout = itemView.findViewById(R.id.ll_bs_add_file_btn);
            parentLayout = itemView.findViewById(R.id.bs_layout);
            mCommentLabelTV = itemView.findViewById(R.id.tv_comment);
            mRangeSeekBarSlider = (RangeSeekBarView) itemView.findViewById(R.id.range_seekbar);
            mRecyclerView=(RecyclerView)itemView.findViewById(R.id.rv_imagelist);


            mMaxValueSliderTV= itemView.findViewById(R.id.tv_maxvalue);
            mMinValueSliderTV= itemView.findViewById(R.id.tv_minvalue);
            mFinalValueSliderTV= itemView.findViewById(R.id.tv_finalvalue);
            mSliderRL= itemView.findViewById(R.id.rl_slider_layout);
            mCollapseExpandIV= itemView.findViewById(R.id.iv_collapseexpand);
        }

        @Override
        public void onClick(View v) {
            clearCurrentFocus();
        }
    }

    private void handleRadioTypeQuestion(final BrandStandardQuestion brandStandardQuestion, final BrandStandardAuditViewHolder holder,final ArrayList<Integer> answerOptionId) {

        final ArrayList<BrandStandardQuestionsOption> arrayList = new ArrayList<>();
        arrayList.addAll(brandStandardQuestion.getOptions());
        holder.optionListLinearLayout.removeAllViews();

        for (int i = 0; i < arrayList.size(); i++) {

            final BrandStandardQuestionsOption brandStandardQuestionsOption = arrayList.get(i);
            final View view = ((BrandStandardAuditActivityPagingnation) context).inflater.inflate(R.layout.brand_standard_audit_rdo_cbx_quesn, null,false);
            final TextView answerText = view.findViewById(R.id.radio_text);
            answerText.setText(String.valueOf(brandStandardQuestionsOption.getOption_text()));
            if (brandStandardQuestion.getAudit_option_id()!= null && brandStandardQuestion.getAudit_option_id().contains(new Integer(brandStandardQuestionsOption.getOption_id())))
            {   setSelectionProcess(answerText,brandStandardQuestion,brandStandardQuestionsOption);
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
            }

            answerText.setTag(brandStandardQuestionsOption.getOption_id());
            answerText.setId(i);
            // brandStandardQuestionsOption.setSelected(0);
            answerText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    // as one option selected
                    BrandStandardAuditActivityPagingnation.isAnswerCliked=true;
                    holder.mCommentET.requestFocus();
                    int optionId = brandStandardQuestionsOption.getOption_id();
                    for (int j = 0; j < arrayList.size(); j++)
                    {
                        TextView radio_text = holder.optionListLinearLayout.findViewById(j);
                        if (radio_text.equals(answerText))
                        {
                            answerOptionId.clear();
                            answerOptionId.add(optionId);
                            brandStandardQuestionsOption.setSelected(1);
                            setSelectionProcess(answerText,brandStandardQuestion,brandStandardQuestionsOption);
                        } else {
                            radio_text.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_btn_border));
                            radio_text.setTextColor(context.getResources().getColor(R.color.c_dark_gray));
                        }
                    }
                  //  ((BrandStandardAuditActivity) context).countNA_Answers();
                }
            });
            holder.optionListLinearLayout.addView(view);
        }
    }

    private void handleDropDownTypeQuestion(final BrandStandardQuestion brandStandardQuestion, final BrandStandardAuditViewHolder holder,final ArrayList<Integer> answerOptionId) {

        final ArrayList<BrandStandardQuestionsOption> arrayList = new ArrayList<>();
        final ArrayList<String> arrayListStr = new ArrayList<>();
        arrayList.addAll(brandStandardQuestion.getOptions());
        holder.optionListLinearLayout.removeAllViews();

        for (int j=0;j<arrayList.size();j++)
            arrayListStr.add(arrayList.get(j).getOption_text());
        arrayListStr.add(0,"Select Option");
        final View view = ((BrandStandardAuditActivityPagingnation) context).inflater.inflate(R.layout.brand_standard_drpdwn_qstn, null,false);
        final Spinner answerSpinner = view.findViewById(R.id.spn_dropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_text,arrayListStr);
        answerSpinner.setAdapter(adapter);
        answerSpinner.setSelection(0);
        for (int i = 0; i < arrayList.size(); i++)
        {
            final BrandStandardQuestionsOption brandStandardQuestionsOption = arrayList.get(i);
            if (brandStandardQuestion.getAudit_option_id()!= null && brandStandardQuestion.getAudit_option_id().contains(new Integer(brandStandardQuestionsOption.getOption_id())))
            {
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
                answerSpinner.setSelection(i+1);
            }
        }
        // only for removing focus from other edittext
        answerSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clearCurrentFocus();
                return false;
            }
        });
        answerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                clearCurrentFocus();
                answerOptionId.clear();
                if (position>0)
                {
                    position= position-1;    //because we have set select option static
                    clickedOnAnswerTpye();
                    answerOptionId.add(arrayList.get(position).getOption_id());
                    if (arrayList.get(position).getOption_text().equalsIgnoreCase("N/A") || arrayList.get(position).getOption_text().equalsIgnoreCase("NA"))
                        brandStandardQuestion.setAudit_answer_na(1);
                    else
                        brandStandardQuestion.setAudit_answer_na(0);

                   // ((BrandStandardAuditActivityPagingnation) context).countNA_Answers();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        holder.optionListLinearLayout.addView(view);

    }


    private void handleMultiSelectionCheckBoxTypeQuestion(final BrandStandardQuestion brandStandardQuestion, final BrandStandardAuditViewHolder holder,final ArrayList<Integer> answerOptionId) {

        final ArrayList<BrandStandardQuestionsOption> arrayList = new ArrayList<>();
        arrayList.addAll(brandStandardQuestion.getOptions());
        holder.optionListLinearLayout.removeAllViews();

        for (int i = 0; i < arrayList.size(); i++) {

            final BrandStandardQuestionsOption brandStandardQuestionsOption = arrayList.get(i);
            final View view = ((BrandStandardAuditActivityPagingnation) context).inflater.inflate(R.layout.brand_standard_audit_rdo_cbx_quesn, null,false);
            final TextView answerText = view.findViewById(R.id.radio_text);
            answerText.setText(String.valueOf(brandStandardQuestionsOption.getOption_text()));

            if (brandStandardQuestion.getAudit_option_id()!= null && brandStandardQuestion.getAudit_option_id().contains(new Integer(brandStandardQuestionsOption.getOption_id())))
            {
                setSelectionProcess(answerText,brandStandardQuestion,brandStandardQuestionsOption);
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
            }

            answerText.setTag(brandStandardQuestionsOption.getOption_id());
            answerText.setId(i);
            answerText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedOnAnswerTpye();
                    int optionId = brandStandardQuestionsOption.getOption_id();
                    TextView tvSelected =((TextView)view);
                    if (tvSelected.getText().toString().equalsIgnoreCase("None of the above") || tvSelected.getText().toString().equalsIgnoreCase("No")|| tvSelected.getText().toString().equalsIgnoreCase("NA")|| tvSelected.getText().toString().equalsIgnoreCase("N/A"))
                    {
                        for (int j = 0; j < arrayList.size(); j++)
                        {
                            TextView radio_text = holder.optionListLinearLayout.findViewById(j);
                            if (radio_text.equals(answerText))
                            {
                                answerOptionId.clear();
                                answerOptionId.add(optionId);
                                setSelectionProcess(answerText,brandStandardQuestion,brandStandardQuestionsOption);
                            } else {
                                radio_text.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_btn_border));
                                radio_text.setTextColor(context.getResources().getColor(R.color.c_dark_gray));
                            }
                        }
                    }
                    else
                    {
                        for (int j = 0; j < arrayList.size(); j++)
                        {
                            TextView radio_text = holder.optionListLinearLayout.findViewById(j);
                            if (radio_text.getText().toString().equalsIgnoreCase("None of the above") || radio_text.getText().toString().equalsIgnoreCase("No")|| radio_text.getText().toString().equalsIgnoreCase("NA")|| radio_text.getText().toString().equalsIgnoreCase("N/A"))
                                backToNormalState(radio_text, answerOptionId);
                        }

                        if (!answerOptionId.contains(new Integer(optionId))) {
                            answerOptionId.add(optionId);
                            setSelectionProcess(answerText, brandStandardQuestion, brandStandardQuestionsOption);
                        }
                    }
                  //  ((BrandStandardAuditActivityPagingnation) context).countNA_Answers();
                }
            });
            holder.optionListLinearLayout.addView(view);
        }
    }

    private void backToNormalState(TextView view, ArrayList<Integer> answerOptionId)
    {

        int id=Integer.parseInt(view.getTag().toString());
        answerOptionId.remove(new Integer(id));
        view.setBackground(context.getResources().getDrawable(R.drawable.brand_standard_btn_border));
        view.setTextColor(context.getResources().getColor(R.color.c_dark_gray));

    }

    private void setSelectionProcess(TextView answerText,BrandStandardQuestion brandStandardQuestion, BrandStandardQuestionsOption brandStandardQuestionsOption) {
        if (brandStandardQuestionsOption.getOption_text().equalsIgnoreCase("NA") || brandStandardQuestionsOption.getOption_text().equalsIgnoreCase("N/A"))
            brandStandardQuestion.setAudit_answer_na(1);
        else
            brandStandardQuestion.setAudit_answer_na(0);

        Log.e("OPTION && TEXT COLOR "," "+brandStandardQuestionsOption.getOption_color()+" TEXTCOLOR  "+brandStandardQuestionsOption.getOption_text_color());
        answerText.setBackgroundColor(Color.parseColor(brandStandardQuestionsOption.getOption_color()));
        answerText.setTextColor(Color.parseColor(brandStandardQuestionsOption.getOption_text_color()));

    }

    public ArrayList<BrandStandardQuestion> getArrayList() {
        return data;
    }

    public interface CustomItemClickListener {
        void onItemClick(int count, BrandStandardAuditAdapterPagingnation brandStandardAuditAdapter, int bsQuestionId, String attachtype, int position);
    }

    public void setattachmentCount(int count, int pos)
    {
        data.get(pos).setAudit_question_file_cnt(count);
        notifyItemChanged(pos);
    }


}
