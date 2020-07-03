package com.oditly.audit.inspection.adapter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.recyclerview.widget.RecyclerView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.audit.DetailedSummary.DetailedSummaryInfo;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class DetailedSummaryAuditAdapter extends RecyclerView.Adapter<DetailedSummaryAuditAdapter.DetailedSummaryAuditViewHolder> {

    private Context context;
    private ArrayList<DetailedSummaryInfo> data;
    private CustomItemClickListener customItemClickListener;
    private String editable;

    public DetailedSummaryAuditAdapter(Context context, ArrayList<DetailedSummaryInfo> data, String editable, CustomItemClickListener customItemClickListener) {
        this.context = context;
        this.data = data;
        this.editable = editable;
        this.customItemClickListener = customItemClickListener;
    }

    @Override
    public DetailedSummaryAuditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detailed_summary_audit_layout, parent, false);

        return new DetailedSummaryAuditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DetailedSummaryAuditViewHolder holder, final int position) {
        final DetailedSummaryInfo detailedSummaryInfo = data.get(position);
        showViewAccStatus(holder, detailedSummaryInfo);
        setDSDetail(holder, detailedSummaryInfo);
        if (detailedSummaryInfo.getIs_na() == 1){
            holder.detailedSummaryNA.setChecked(true);
            holder.detailedSummarySubLayout.setVisibility(View.GONE);
            holder.ivExpandIcon.setImageResource(R.drawable.expand_icon);
        }else {
            holder.detailedSummaryNA.setChecked(false);
            holder.tvDetailedSummaryTitle.setText(detailedSummaryInfo.getSection_title());
            if(!data.get(position).isExpand()){
                holder.detailedSummarySubLayout.setVisibility(View.GONE);
                holder.ivExpandIcon.setImageResource(R.drawable.expand_icon);
                data.get(position).setExpand(false);
            }else {
                holder.detailedSummarySubLayout.setVisibility(View.VISIBLE);
                holder.ivExpandIcon.setImageResource(R.drawable.compress_icon);
                data.get(position).setExpand(true);
            }
            holder.rlDetailedSummaryExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(data.get(position).isExpand()){
                        holder.detailedSummarySubLayout.setVisibility(View.GONE);
                        holder.ivExpandIcon.setImageResource(R.drawable.expand_icon);
                        data.get(position).setExpand(false);
                    }else {
                        holder.detailedSummarySubLayout.setVisibility(View.VISIBLE);
                        holder.ivExpandIcon.setImageResource(R.drawable.compress_icon);
                        data.get(position).setExpand(true);
                    }
                }
            });

        }



        holder.detailedSummaryNA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detailedSummaryInfo.getIs_na() == 1){
                    detailedSummaryInfo.setIs_na(0);
                    holder.rlDetailedSummaryExpand.setEnabled(false);
                    holder.detailedSummarySubLayout.setVisibility(View.VISIBLE);
                    holder.ivExpandIcon.setImageResource(R.drawable.compress_icon);
                }else {
                    detailedSummaryInfo.setIs_na(1);
                    holder.rlDetailedSummaryExpand.setEnabled(true);
                    holder.detailedSummarySubLayout.setVisibility(View.GONE);
                    holder.ivExpandIcon.setImageResource(R.drawable.expand_icon);
                }
            }
        });

        holder.tvDSDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar auditMonthCal = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                Calendar cal = Calendar.getInstance();
                                cal.set(year, monthOfYear, dayOfMonth);
                                holder.tvDSDate.setText(AppUtils.getDSAuditDate(cal.getTime()));

                            }
                        }, auditMonthCal.get(Calendar.YEAR), auditMonthCal.get(Calendar.MONTH),
                        auditMonthCal.get(Calendar.YEAR));
                datePickerDialog.show();
            }
        });

        holder.tvDSTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar calendar = Calendar.getInstance();
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String AM_PM ;
                                if(hourOfDay < 12) {
                                    AM_PM = " AM";
                                } else {
                                    AM_PM = " PM";
                                }

                                calendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DATE, hourOfDay, minute);
                                String time = AppUtils.getDSAuditTime(calendar.getTime());

                                holder.tvDSTime.setText(time);

                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });

        holder.detailedSummaryFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customItemClickListener.onItemClick( detailedSummaryInfo.getSection_group_id(),detailedSummaryInfo.getSection_id(),"dsSection", position);
            }
        });

        setDSData(holder, detailedSummaryInfo);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DetailedSummaryAuditViewHolder extends RecyclerView.ViewHolder {

        TextView tvDetailedSummaryTitle;
        RelativeLayout rlDetailedSummaryExpand;
        ImageView ivExpandIcon;
        LinearLayout detailedSummarySubLayout;
        EditText etDSStaffName;
        EditText etDSSummary;
        EditText etDSKeyNegative;
        EditText etDSKeyPositive;
        EditText etDSRecommendation;
        TextView tvDSTime;
        TextView tvDSDate;
        Button detailedSummaryFileBtn;
        TextView detailedSummaryAttachCount;
        Button detailedSummaryAddFile;
        CheckBox detailedSummaryNA;


        public DetailedSummaryAuditViewHolder (View itemView) {
            super(itemView);

            tvDetailedSummaryTitle = itemView.findViewById(R.id.tv_detailed_summary_title);
            rlDetailedSummaryExpand = itemView.findViewById(R.id.rl_detailed_summary_expand);
            ivExpandIcon = itemView.findViewById(R.id.iv_expand_icon);
            detailedSummarySubLayout = itemView.findViewById(R.id.detailed_summary_sub_layout);
            tvDSTime = itemView.findViewById(R.id.tv_detailed_summary_time);
            tvDSDate = itemView.findViewById(R.id.tv_detailed_summary_date);
            etDSStaffName = itemView.findViewById(R.id.et_detailed_summary_staff_name);
            etDSSummary = itemView.findViewById(R.id.et_detailed_summary_summary);
            etDSKeyNegative = itemView.findViewById(R.id.et_detailed_summary_key_negative);
            etDSKeyPositive = itemView.findViewById(R.id.et_detailed_summary_key_positive);
            etDSRecommendation = itemView.findViewById(R.id.et_detailed_summary_recommendation);
            detailedSummaryAddFile = itemView.findViewById(R.id.ds_add_btn);
            detailedSummaryAttachCount = itemView.findViewById(R.id.ds_attachment_count);
            detailedSummaryFileBtn = itemView.findViewById(R.id.ds_add_file_btn);
            detailedSummaryNA = itemView.findViewById(R.id.cb_detailed_summary_na);

        }
    }

    private void showViewAccStatus(DetailedSummaryAuditViewHolder holder, DetailedSummaryInfo detailedSummaryInfo){
        switch (detailedSummaryInfo.getDetailed_sum_status()){
            case 0:
                break;
            case 1:
                enableView(holder);
                holder.detailedSummaryAddFile.setText("+");
                holder.detailedSummaryNA.setEnabled(true);
                break;
            case 2:
                enableView(holder);
                holder.detailedSummaryAddFile.setText("+");
                holder.detailedSummaryNA.setEnabled(true);
                break;
            case 3:
                disableView(holder);
                holder.detailedSummaryAddFile.setText("+");
                holder.detailedSummaryNA.setEnabled(true);
                break;
            case 4:
                disableView(holder);
                holder.detailedSummaryAddFile.setText("");
                holder.detailedSummaryNA.setEnabled(false);
                break;
            case 5:
                disableView(holder);
                holder.detailedSummaryAddFile.setText("");
                holder.detailedSummaryNA.setEnabled(false);
                break;
        }
    }

    private void enableView(DetailedSummaryAuditViewHolder holder){
        holder.tvDSTime.setEnabled(true);
        holder.tvDSDate.setEnabled(true);
        holder.etDSStaffName.setEnabled(true);
        holder.etDSSummary.setEnabled(true);
        holder.etDSKeyNegative.setEnabled(true);
        holder.etDSKeyPositive.setEnabled(true);
        holder.etDSRecommendation.setEnabled(true);
    }

    private void disableView(DetailedSummaryAuditViewHolder holder){
        holder.tvDSTime.setEnabled(false);
        holder.tvDSDate.setEnabled(false);
        holder.etDSStaffName.setEnabled(false);
        holder.etDSSummary.setEnabled(false);
        holder.etDSKeyNegative.setEnabled(false);
        holder.etDSKeyPositive.setEnabled(false);
        holder.etDSRecommendation.setEnabled(false);
    }



    public void setDSDetail (DetailedSummaryAuditViewHolder holder, DetailedSummaryInfo detailedSummaryInfo) {
        if (!AppUtils.isStringEmpty(detailedSummaryInfo.getStaff_name())){
            holder.etDSStaffName.setText(detailedSummaryInfo.getStaff_name());
        }
        if (!AppUtils.isStringEmpty(detailedSummaryInfo.getDate_time())){
            String date = AppUtils.getDSAuditDate(detailedSummaryInfo.getDate_time());
            holder.tvDSDate.setText(date);
        }
        if (!AppUtils.isStringEmpty(detailedSummaryInfo.getDate_time())){
            String time = AppUtils.getDSAuditTime(detailedSummaryInfo.getDate_time());
            holder.tvDSTime.setText(time);
        }
        if (!AppUtils.isStringEmpty(detailedSummaryInfo.getSummary())){
            holder.etDSSummary.setText(detailedSummaryInfo.getSummary());
        }
        if (!AppUtils.isStringEmpty(detailedSummaryInfo.getKey_positive())){
            holder.etDSKeyPositive.setText(detailedSummaryInfo.getKey_positive());
        }
        if (!AppUtils.isStringEmpty(detailedSummaryInfo.getKey_negative())){
            holder.etDSKeyNegative.setText(detailedSummaryInfo.getKey_negative());
        }
        if (!AppUtils.isStringEmpty(detailedSummaryInfo.getRecommendation())){
            holder.etDSRecommendation.setText(detailedSummaryInfo.getRecommendation());
        }
        if (!AppUtils.isStringEmpty("" + detailedSummaryInfo.getFile_count())){
            holder.detailedSummaryAttachCount.setText("" + detailedSummaryInfo.getFile_count());
        }
    }

    public void setDSData(DetailedSummaryAuditViewHolder holder, final DetailedSummaryInfo detailedSummaryInfo) {
        holder.etDSStaffName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                detailedSummaryInfo.setStaff_name(""+editable.toString());
                AppLogger.e("Staff Name", ""+editable);

                /*if (AppUtils.isStringEmpty(holder.etDSStaffName.toString())) {
                    detailedSummaryInfo.setStaff_name(holder.etDSStaffName.toString());
                }*/
            }
        });

        holder.tvDSDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                detailedSummaryInfo.setDate(editable.toString());
            }
        });

        holder.tvDSTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                detailedSummaryInfo.setTime(editable.toString());
            }
        });

        holder.etDSSummary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                detailedSummaryInfo.setSummary(editable.toString());
            }
        });

        holder.etDSKeyPositive.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                detailedSummaryInfo.setKey_positive(editable.toString());
            }
        });

        holder.etDSKeyNegative.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                detailedSummaryInfo.setKey_negative(editable.toString());
            }
        });

        holder.etDSRecommendation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                detailedSummaryInfo.setRecommendation(editable.toString());
            }
        });

    }

    public boolean validateInput(DetailedSummaryAuditViewHolder holder, DetailedSummaryInfo detailedSummaryInfo){
        boolean validate = true;
        if (holder.etDSStaffName.getText().toString().length() <= 0){
            //AppUtils.toast();
            validate = false;
        }else if (holder.tvDSDate.getText().toString().length() <= 0){
            validate = false;
        }else if (holder.tvDSTime.getText().toString().length() <= 0){
            validate = false;
        }else if (holder.etDSSummary.getText().toString().length() < 100){
            validate = false;
        }
        /*if (validate){
            detailedSummaryInfo.setStaff_name(holder.etDetailedSummaryStaffName.getText().toString());
            detailedSummaryInfo.setDate_time(holder.tvDetailedSummaryDate.getText().toString());
            detailedSummaryInfo.setSummary(holder.etDetailedSummarySummary.getText().toString());
            if (!AppUtils.isStringEmpty(holder.etDetailedSummaryKeyPositive.getText().toString())){
                detailedSummaryInfo.setStaff_name(holder.etDetailedSummaryKeyPositive.getText().toString());
            }
            if (!AppUtils.isStringEmpty(holder.etDetailedSummaryKeyNegative.getText().toString())){
                detailedSummaryInfo.setStaff_name(holder.etDetailedSummaryKeyNegative.getText().toString());
            }
            if (!AppUtils.isStringEmpty(holder.etDetailedSummaryRecommendation.getText().toString())){
                detailedSummaryInfo.setStaff_name(holder.etDetailedSummaryRecommendation.getText().toString());
            }
        }*/
        return validate;
    }

    public ArrayList<DetailedSummaryInfo> getArrayList() {
        return data;
    }

    public interface CustomItemClickListener {
        void onItemClick(int sectionGroupId, int sectionId, String attachtype, int position);
    }

    public void setattachmentCount(int count, int pos){

        data.get(pos).setFile_count(count);
        notifyDataSetChanged();
    }

}
