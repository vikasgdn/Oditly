package com.oditly.audit.inspection.adapter;

import android.annotation.SuppressLint;
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
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardActionPlan;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestion;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestionsOption;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSlider;
import com.oditly.audit.inspection.ui.activty.BrandStandardAuditActivityPagingnation;
import com.oditly.audit.inspection.ui.activty.BrandStandardOptionsBasedQuestionActivity;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.ArrayList;


public class BrandStandardOptionBasedQuestionsAdapter extends RecyclerView.Adapter<BrandStandardOptionBasedQuestionsAdapter.BrandStandardAuditViewHolder> {

    private BrandStandardOptionsBasedQuestionActivity context;
    private ArrayList<BrandStandardQuestion> data;
    CustomItemClickListener customItemClickListener;



    public BrandStandardOptionBasedQuestionsAdapter(BrandStandardOptionsBasedQuestionActivity context, ArrayList<BrandStandardQuestion> data, CustomItemClickListener customItemClickListener) {
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
    public void onBindViewHolder(final BrandStandardAuditViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final BrandStandardQuestion brandStandardQuestion = data.get(position);
        holder.parentLayout.clearFocus();

        holder.questionTitle.setText(""+(position+1) +". " + brandStandardQuestion.getQuestion_title());
        brandStandardQuestion.setQuestionCount(position); // for refresing that  position

        if (brandStandardQuestion.getIs_required()==1)
            holder.questionTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_astrisk12 , 0);
        else
            holder.questionTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        if (brandStandardQuestion.getMedia_count()>0) {
            holder.mMediaLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0 , R.drawable.ic_astrisk12);
            holder.mMediaLabelTV.setText(context.getString(R.string.text_photo) + " (" + brandStandardQuestion.getAudit_question_file_cnt() + "/" + brandStandardQuestion.getMedia_count() + ")");
        }
        else {
            holder.mMediaLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
            holder.mMediaLabelTV.setText(context.getString(R.string.text_photo) + " (" + brandStandardQuestion.getAudit_question_file_cnt() + ")");
        }
        if (brandStandardQuestion.getHas_comment()>0) {
            holder.mCommentLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0 , R.drawable.ic_astrisk12);
            holder.mCommentLenthTV.setText("Please enter minimum "+brandStandardQuestion.getHas_comment()+" characters");
        }
        else {
            holder.mCommentLenthTV.setVisibility(View.GONE);
            holder.mCommentLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        if(brandStandardQuestion.getmImageList()!=null && brandStandardQuestion.getmImageList().size()>0) {
            holder.mRecyclerView.setVisibility(View.VISIBLE);
            AddBSMediaAdapter adapterImage = new AddBSMediaAdapter(context, brandStandardQuestion.getmImageList());
            holder.mRecyclerView.setAdapter(adapterImage);
        }
        else
            holder.mRecyclerView.setVisibility(View.GONE);



        String questionType = brandStandardQuestion.getQuestion_type();
        AppLogger.e("QuestionType:", ""+questionType +" || "+ brandStandardQuestion.getQuestion_title());


        if(questionType.equalsIgnoreCase("radio"))
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
        else  if(questionType.equalsIgnoreCase("textarea") || questionType.equalsIgnoreCase("text") )
        {
            if (!TextUtils.isEmpty(brandStandardQuestion.getAudit_answer())) {
                holder.mTextAnswerET.setText(brandStandardQuestion.getAudit_answer());
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
            }
            setOtherViewHide(holder);
         /*   if (questionType.equalsIgnoreCase("textarea"))
                holder.mTextAnswerET.setMinLines(4);
            else
                holder.mTextAnswerET.setMinLines(2);
*/
            holder.mTextAnswerET.setVisibility(View.VISIBLE);
            holder.mTextAnswerET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppDialogs.showeTexTypeAnswerForQuestionBS(brandStandardQuestion, context.getString(R.string.text_type_your_answer), (BrandStandardOptionsBasedQuestionActivity)context);
                }
            });
       /*     holder.mTextAnswerET.addTextChangedListener(new TextWatcher() {
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
*/
        }
        else  if(questionType.equalsIgnoreCase("number") )
        {
            if (!TextUtils.isEmpty(brandStandardQuestion.getAudit_answer()) && !brandStandardQuestion.getAudit_answer().equalsIgnoreCase("0")) {
                holder.mNumberDecAnsweET.setText(brandStandardQuestion.getAudit_answer());
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
            }
            setOtherViewHide(holder);
            holder.mNumberDecAnsweET.setVisibility(View.VISIBLE);
            holder.mNumberDecAnsweET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppDialogs.showeTexTypeAnswerForQuestionBS(brandStandardQuestion, context.getString(R.string.text_type_your_answer), (BrandStandardOptionsBasedQuestionActivity)context);
                }
            });

        /*    holder.mNumberDecAnsweET.addTextChangedListener(new TextWatcher() {
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
*/
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
                    ((BrandStandardOptionsBasedQuestionActivity) context).saveSingleBrandStandardQuestionEveryClick(brandStandardQuestion);

                }
            });

        }
        else if(questionType.equalsIgnoreCase("media"))
        {
            setOtherViewHide(holder);
        }
        else if(questionType.equalsIgnoreCase("temperature"))
        {
            if (!TextUtils.isEmpty(brandStandardQuestion.getAudit_answer())) {
                holder.mNumberDecAnsweET.setText(brandStandardQuestion.getAudit_answer());
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
            }
            holder.mNumberDecAnsweET.setHint("Please enter value in "+brandStandardQuestion.getUnit().getUnit_name());
            setOtherViewHide(holder);
            holder.mNumberDecAnsweET.setVisibility(View.VISIBLE);
            holder.mNumberDecAnsweET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppDialogs.showeTexTypeAnswerForQuestionBS(brandStandardQuestion,context.getResources().getString(R.string.please_enter_valuein)+" "+brandStandardQuestion.getUnit().getUnit_name(), (BrandStandardOptionsBasedQuestionActivity)context);
                }
            });

        /*    holder.mNumberDecAnsweET.addTextChangedListener(new TextWatcher() {
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
*/
        }
        else if(questionType.equalsIgnoreCase("measurement"))
        {
            if (!TextUtils.isEmpty(brandStandardQuestion.getAudit_answer())) {
                holder.mNumberDecAnsweET.setText(brandStandardQuestion.getAudit_answer());
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
            }
            setOtherViewHide(holder);
            holder.mNumberDecAnsweET.setHint("Please enter value in "+brandStandardQuestion.getUnit().getUnit_name());
            holder.mNumberDecAnsweET.setVisibility(View.VISIBLE);
            holder.mNumberDecAnsweET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppDialogs.showeTexTypeAnswerForQuestionBS(brandStandardQuestion,context.getResources().getString(R.string.please_enter_valuein)+" "+brandStandardQuestion.getUnit().getUnit_name(), (BrandStandardOptionsBasedQuestionActivity)context);
                }
            });

          /*  holder.mNumberDecAnsweET.addTextChangedListener(new TextWatcher() {
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
            });*/
        }
        else if(questionType.equalsIgnoreCase("target"))
        {
            if (!TextUtils.isEmpty(brandStandardQuestion.getAudit_answer())) {
                holder.mNumberDecAnsweET.setText(brandStandardQuestion.getAudit_answer());
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
            }
            setOtherViewHide(holder);
            holder.mNumberDecAnsweET.setHint("Target: "+brandStandardQuestion.getMax_mark());
            holder.mNumberDecAnsweET.setVisibility(View.VISIBLE);
            holder.mNumberDecAnsweET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppDialogs.showeTexTypeAnswerForQuestionBS(brandStandardQuestion,"Target: "+brandStandardQuestion.getMax_mark(), (BrandStandardOptionsBasedQuestionActivity)context);

                }
            });

          /*  holder.mNumberDecAnsweET.addTextChangedListener(new TextWatcher() {
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
                    // ((BrandStandardAuditActivityPagingnation) context).countNA_Answers();
                }
            });*/
        }
        else if(questionType.equalsIgnoreCase("datetime"))
        {
            if (!TextUtils.isEmpty(brandStandardQuestion.getAudit_answer())) {
                holder.mDateTimePickerTV.setText(brandStandardQuestion.getAudit_answer());
                holder.parentLayout.setBackgroundResource(R.drawable.brandstandard_question_answeredbg);
            }
            setOtherViewHide(holder);
            holder.mDateTimePickerTV.setVisibility(View.VISIBLE);
            //  holder.mCommentMediaShowLayout.setVisibility(View.GONE);
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
            // holder.mCommentMediaShowLayout.setVisibility(View.GONE);
            holder.mDateTimePickerTV.setVisibility(View.VISIBLE);
            holder.mDateTimePickerTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedOnAnswerTpye();
                    AppUtils.datePicker(context,holder.mDateTimePickerTV,false,brandStandardQuestion);
                }
            });

        }
        else
        {
            setOtherViewHide(holder);
            holder.mRadioSectionLL.setVisibility(View.VISIBLE);
            handleRadioTypeQuestion(brandStandardQuestion, holder,brandStandardQuestion.getAudit_option_id());
        }

        if (!AppUtils.isStringEmpty(brandStandardQuestion.getAudit_comment()))
            holder.mCommentET.setText(brandStandardQuestion.getAudit_comment());
        else
            holder.mCommentET.setText("");


        holder.mCommentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                clickedOnAnswerTpye();
                brandStandardQuestion.setAudit_comment("" + editable.toString());
            }
        });
        holder.mCommentHideShowLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCurrentFocus();
                AppDialogs.showEnterCommentForQuestionBS(brandStandardQuestion,(BrandStandardOptionsBasedQuestionActivity)context);

              /*  if(holder.mCommentET.isShown()) {
                    holder.mCommentET.setVisibility(View.GONE);
                    holder.mCommentLenthTV.setVisibility(View.GONE);
                }
                else {
                    holder.mCommentET.setVisibility(View.VISIBLE);
                    holder.mCommentLenthTV.setVisibility(View.VISIBLE);
                }*/
            }
        });

        holder.brandStandardAddFileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCurrentFocus();
                int count = Integer.parseInt(holder.questionTitle.getText().toString().substring(0, holder.questionTitle.getText().toString().indexOf(".")));
                customItemClickListener.onItemClick(count - 1, BrandStandardOptionBasedQuestionsAdapter.this, brandStandardQuestion.getQuestion_id(), "bsQuestion", position);
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

        brandStandardQuestion.setmClickPosition(position); //newly added for question refresh

        if (brandStandardQuestion.getRef_file()!=null && !AppUtils.isStringEmpty(brandStandardQuestion.getRef_file().getFile_url())) {
            holder.mShowHowLL.setVisibility(View.VISIBLE);
            holder.mShowHowLL.setEnabled(true);
            holder.mShowHowLL.setTag(brandStandardQuestion.getRef_file());
            holder.mShowHowLL.setOnClickListener(context);
        } else {
            holder.mShowHowLL.setVisibility(View.GONE);
            holder.mShowHowLL.setEnabled(false);
        }
        if (brandStandardQuestion.isCan_create_action_plan()) {
            clickedOnAnswerTpye();
          //  brandStandardQuestion.setmClickPosition(position);
            holder.mActionCreateLL.setVisibility(View.VISIBLE);
            holder.mActionCreateLL.setTag(brandStandardQuestion);
            holder.mActionCreateLL.setOnClickListener(context);
        } else if (brandStandardQuestion.getAction_plan() != null) {
            holder.mActionCreateLL.setAlpha(0.5f);
            holder.mActionCreateLL.setVisibility(View.VISIBLE);
            holder.mActionCreateLL.setTag(brandStandardQuestion);
            holder.mActionCreateLL.setOnClickListener(context);
        } else {
            holder.mActionCreateLL.setVisibility(View.GONE);
        }
    }

    private void clearCurrentFocus() {
        View currentFocus = context.getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    private void clickedOnAnswerTpye() {
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public class BrandStandardAuditViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public TextView mActionPlanLabelTV;
        TextView questionTitle;
        TextView note;
        TextView mCommentLabelTV;
        TextView mMediaLabelTV;
        EditText mCommentET;
        TextView mCommentLenthTV;
        LinearLayout mShowHowLL;
        LinearLayout mCommentHideShowLL;
        LinearLayout brandStandardAddFileLayout;
        LinearLayout mCommentMediaShowLayout;
        LinearLayout optionListLinearLayout;
        LinearLayout parentLayout;
        LinearLayout hintLayout;
        LinearLayout mActionCreateLL;
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
            mCommentET = itemView.findViewById(R.id.et_comment);
            mCommentLenthTV = itemView.findViewById(R.id.tv_commentlenth);

            mShowHowLL = itemView.findViewById(R.id.ll_showhow);
            mActionCreateLL = itemView.findViewById(R.id.ll_actioncreate);
            optionListLinearLayout = itemView.findViewById(R.id.rv_brand_standard_answer);
            mMediaLabelTV = itemView.findViewById(R.id.tv_media);
            mActionPlanLabelTV= itemView.findViewById(R.id.tv_actionplan);
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
            final View view = ((BrandStandardOptionsBasedQuestionActivity) context).inflater.inflate(R.layout.brand_standard_audit_rdo_cbx_quesn, null,false);
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
                    if (brandStandardQuestionsOption.getMedia_count()>0) {
                        holder.mMediaLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0 , R.drawable.ic_astrisk12);
                        holder.mMediaLabelTV.setText(context.getString(R.string.text_photo) + " (" + brandStandardQuestion.getAudit_question_file_cnt() + "/" + brandStandardQuestionsOption.getMedia_count() + ")");
                    }
                    else {
                        holder.mMediaLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                        holder.mMediaLabelTV.setText(context.getString(R.string.text_photo) + " (" + brandStandardQuestion.getAudit_question_file_cnt() + ")");
                    }
                    if (brandStandardQuestionsOption.getCommentCount()>0) {
                        holder.mCommentLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0 , R.drawable.ic_astrisk12);
                        holder.mCommentLenthTV.setText("Please enter minimum "+brandStandardQuestionsOption.getCommentCount()+" characters");
                    }
                    else {
                        holder.mCommentLenthTV.setVisibility(View.GONE);
                        holder.mCommentLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                    if (brandStandardQuestionsOption.getAction_plan_required()==1) {
                        holder.mActionPlanLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0 , R.drawable.ic_astrisk12);
                    }
                    else {
                        holder.mActionPlanLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                    }
                    // as one option selected
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
                    ((BrandStandardOptionsBasedQuestionActivity) context).saveSingleBrandStandardQuestionEveryClick(brandStandardQuestion);
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
        final View view = context.inflater.inflate(R.layout.brand_standard_drpdwn_qstn, null,false);
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

                    if (arrayList.get(position).getMedia_count()>0) {
                        holder.mMediaLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0 , R.drawable.ic_astrisk12);
                        holder.mMediaLabelTV.setText(context.getString(R.string.text_photo) + " (" + brandStandardQuestion.getAudit_question_file_cnt() + "/" + arrayList.get(position).getMedia_count() + ")");
                    }
                    else {
                        holder.mMediaLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                        holder.mMediaLabelTV.setText(context.getString(R.string.text_photo) + " (" + brandStandardQuestion.getAudit_question_file_cnt() + ")");
                    }
                    if (arrayList.get(position).getCommentCount()>0) {
                        holder.mCommentLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0 , R.drawable.ic_astrisk12);
                        holder.mCommentLenthTV.setText("Please enter minimum "+arrayList.get(position).getCommentCount()+" characters");
                    }
                    else {
                        holder.mCommentLenthTV.setVisibility(View.GONE);
                        holder.mCommentLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                    if (arrayList.get(position).getAction_plan_required()==1) {
                        holder.mActionPlanLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0 , R.drawable.ic_astrisk12);
                    }
                    else {
                        holder.mActionPlanLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                    }

                    answerOptionId.add(arrayList.get(position).getOption_id());
                    if (arrayList.get(position).getOption_text().equalsIgnoreCase("N/A") || arrayList.get(position).getOption_text().equalsIgnoreCase("NA"))
                        brandStandardQuestion.setAudit_answer_na(1);
                    else
                        brandStandardQuestion.setAudit_answer_na(0);

                    ((BrandStandardOptionsBasedQuestionActivity) context).saveSingleBrandStandardQuestionEveryClick(brandStandardQuestion);

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
            final View view = context.inflater.inflate(R.layout.brand_standard_audit_rdo_cbx_quesn, null,false);
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

                    if (brandStandardQuestionsOption.getMedia_count()>0) {
                        holder.mMediaLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0 , R.drawable.ic_astrisk12);
                        holder.mMediaLabelTV.setText(context.getString(R.string.text_photo) + " (" + brandStandardQuestion.getAudit_question_file_cnt() + "/" + brandStandardQuestionsOption.getMedia_count() + ")");
                    }
                    else {
                        holder.mMediaLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                        holder.mMediaLabelTV.setText(context.getString(R.string.text_photo) + " (" + brandStandardQuestion.getAudit_question_file_cnt() + ")");
                    }
                    if (brandStandardQuestionsOption.getCommentCount()>0) {
                        holder.mCommentLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0 , R.drawable.ic_astrisk12);
                        holder.mCommentLenthTV.setText("Please enter minimum "+brandStandardQuestionsOption.getCommentCount()+" characters");
                    }
                    else {
                        holder.mCommentLenthTV.setVisibility(View.GONE);
                        holder.mCommentLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                    if (brandStandardQuestionsOption.getAction_plan_required()==1) {
                        holder.mActionPlanLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0 , R.drawable.ic_astrisk12);
                    }
                    else {
                        holder.mActionPlanLabelTV.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                    }

                    TextView tvSelected =((TextView)view);
                    if (tvSelected.getText().toString().equalsIgnoreCase("None of the above") || tvSelected.getText().toString().equalsIgnoreCase("NA")|| tvSelected.getText().toString().equalsIgnoreCase("N/A"))
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
                            if (radio_text.getText().toString().equalsIgnoreCase("None of the above") || radio_text.getText().toString().equalsIgnoreCase("NA")|| radio_text.getText().toString().equalsIgnoreCase("N/A"))
                                backToNormalState(radio_text, answerOptionId);
                        }
                        if (answerOptionId.contains(new Integer(optionId))) {
                            backToNormalState(answerText, answerOptionId);
                        }
                        else
                        {   answerOptionId.add(optionId);
                            setSelectionProcess(answerText, brandStandardQuestion, brandStandardQuestionsOption);
                        }
                    }
                    ((BrandStandardOptionsBasedQuestionActivity) context).saveSingleBrandStandardQuestionEveryClick(brandStandardQuestion);
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
        if (brandStandardQuestionsOption.getOption_text().equalsIgnoreCase("NA") || brandStandardQuestionsOption.getOption_text().equalsIgnoreCase("N/A")) {
            brandStandardQuestion.setAudit_answer_na(1);
            brandStandardQuestion.setObtainMarksForQuestion(null);
        }
        else {
            brandStandardQuestion.setAudit_answer_na(0);
            brandStandardQuestion.setObtainMarksForQuestion(""+brandStandardQuestionsOption.getOption_mark());
        }

        brandStandardQuestion.setHas_comment(brandStandardQuestionsOption.getCommentCount());
        brandStandardQuestion.setMedia_count(brandStandardQuestionsOption.getMedia_count());

        Log.e("OPTION && TEXT COLOR "," "+brandStandardQuestionsOption.getOption_color()+" TEXTCOLOR  "+brandStandardQuestionsOption.getOption_text_color());
        answerText.setBackgroundColor(Color.parseColor(brandStandardQuestionsOption.getOption_color()));
        answerText.setTextColor(Color.parseColor(brandStandardQuestionsOption.getOption_text_color()));


    }

    public ArrayList<BrandStandardQuestion> getArrayList() {
        return data;
    }

    public interface CustomItemClickListener {
        void onItemClick(int count, BrandStandardOptionBasedQuestionsAdapter brandStandardAuditAdapter, int bsQuestionId, String attachtype, int position);
    }

    public void setattachmentCount(int count, int pos)
    {
        data.get(pos).setAudit_question_file_cnt(count);
        notifyItemChanged(pos);
    }

    public void setActionCreatedFlag(int pos) {
        this.data.get(pos).setCan_create_action_plan(false);
        this.data.get(pos).setAction_plan(new BrandStandardActionPlan());
        notifyItemChanged(pos);
    }
    public void updatehParticularPosition(int pos)
    {
        notifyItemChanged(pos);
    }
}