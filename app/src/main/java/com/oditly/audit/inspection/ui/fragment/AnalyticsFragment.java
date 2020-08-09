package com.oditly.audit.inspection.ui.fragment;

import android.app.MediaRouteButton;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.ActionDashboard;
import com.oditly.audit.inspection.model.InspectionDashboard;
import com.oditly.audit.inspection.model.audit.AuditRootObject;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.ui.activty.BaseActivity;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnalyticsFragment extends BaseFragment implements INetworkEvent {
    public static final String ARG_PAGE = "ARG_PAGE";
    private PieChart mChartActionplan;
    private PieChart mChartAuditbenchmark;
    private List<InspectionDashboard> mInspectionList;
    private List<ActionDashboard> mActionList;

    private TextView mNoOfAuditTV,mNoOfNonComplianceTV,mNoOfLocationTV,mNoOfAverageNonCompTV;
    private TextView mFailedTV,mTotalTV;

    private TextView moverDueByTV,moverDuePerTV;
    private TextView mInProressByTV,mInprogressPerTV;
    private TextView mCompleteByTV,mCompletePerTV;
    private TextView mScheduleByTV,mSchedulePerTV;

    private TextView mPendingApprovalCTV,mApprovedCTV,mCompleteCTV,mOverDueCTV,mRejectedCTV,mTotalActionTV;



    private int[] yValueseBench = {80, 20};
    private String[] xValuesBench = {"Total", "failed"};
    // colors for different sections in pieChart
    public static  final int[] MY_COLORS =
            {       Color.rgb(118, 165, 175),
                    Color.rgb(131, 89, 163),
                    Color.rgb(11, 135, 228),
                    Color.rgb(198, 0, 34),
                    Color.rgb(19, 133, 95)
            };
    //1:PA,purple,Blue,red,green
    public static  final int[] MY_COLORS_BENCH= {Color.rgb(19, 133, 95), Color.rgb(255,63,52)};
    private RelativeLayout mSpinKitView;

    public static AnalyticsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        AnalyticsFragment fragment = new AnalyticsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
        initVar();
        getDashBoardDataFromServer();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mSpinKitView= (RelativeLayout) view.findViewById(R.id.ll_parent_progress);
        mChartActionplan = (PieChart) view.findViewById(R.id.pichart_actionplan);
        mChartAuditbenchmark = (PieChart) view.findViewById(R.id.pichart_auditbenchmark);

        mNoOfAuditTV=(TextView)view.findViewById(R.id.tv_noofaudits);
        mNoOfLocationTV=(TextView)view.findViewById(R.id.tv_nooflocations);
        mNoOfNonComplianceTV=(TextView)view.findViewById(R.id.tv_noofnoncompliances);
        mNoOfAverageNonCompTV=(TextView)view.findViewById(R.id.tv_average_non_compliance);

        mTotalTV=(TextView)view.findViewById(R.id.tv_total);
        mTotalActionTV=(TextView)view.findViewById(R.id.tv_totalaction);
        mFailedTV=(TextView)view.findViewById(R.id.tv_failed);


        mPendingApprovalCTV=(TextView)view.findViewById(R.id.tv_pendingapr);
        mCompleteCTV=(TextView)view.findViewById(R.id.tv_complete);
        mApprovedCTV=(TextView)view.findViewById(R.id.tv_approved);
        mOverDueCTV=(TextView)view.findViewById(R.id.tv_overdue);
        mRejectedCTV=(TextView)view.findViewById(R.id.tv_rejected);


        moverDueByTV=(TextView)view.findViewById(R.id.tv_overdueby);
        moverDuePerTV=(TextView)view.findViewById(R.id.tv_overdueper);
        mInProressByTV=(TextView)view.findViewById(R.id.tv_inprogressby);
        mInprogressPerTV=(TextView)view.findViewById(R.id.tv_inprogressper);
        mCompleteByTV=(TextView)view.findViewById(R.id.tv_completedby);
        mCompletePerTV=(TextView)view.findViewById(R.id.tv_complete_per);
        mScheduleByTV=(TextView)view.findViewById(R.id.tv_scheduleby);
        mSchedulePerTV=(TextView)view.findViewById(R.id.tv_scheduledper);

        //  mChartActionplan.setUsePercentValues(true);
        //mChartActionplan.setCenterText("");
        //mChartActionplan.setRotationEnabled(true);
        mChartActionplan.getDescription().setEnabled(false);
        mChartAuditbenchmark.getDescription().setEnabled(false);
        mChartAuditbenchmark.getLegend().setEnabled(false);
        mChartActionplan.getLegend().setEnabled(false);

        mChartActionplan.setRotationEnabled(false);
        mChartAuditbenchmark.setRotationEnabled(false);



    }

    @Override
    protected void initVar() {
        super.initVar();
        mInspectionList =new ArrayList<>();
        mActionList =new ArrayList<>();
    }
    ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();
    ArrayList<String> xVals = new ArrayList<String>();
    public void setDataForPieChart()
    {
        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(0);
        dataSet.setDrawValues(false);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : MY_COLORS) colors.add(c);
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new DefaultValueFormatter(0));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        mChartActionplan.setData(data);
        mChartActionplan.animateXY(1400, 1400);
    }

    public void setDataForPieChartBenchmark()
    {
        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();
        for (int i = 0; i < yValueseBench.length; i++)
            yVals1.add(new PieEntry(yValueseBench[i], i));
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < xValuesBench.length; i++)
            xVals.add(xValuesBench[i]);
        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(0);
        dataSet.setDrawValues(false);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : MY_COLORS_BENCH) colors.add(c);
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new DefaultValueFormatter(0));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        mChartAuditbenchmark.setData(data);
        mChartAuditbenchmark.animateXY(1400, 1400);

    }
    private void getDashBoardDataFromServer()
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {
            mSpinKitView.setVisibility(View.VISIBLE);
            System.out.println("==> Report URL==>  "+NetworkURL.GET_DASHBOARD_URL);
            NetworkService networkService = new NetworkService(NetworkURL.GET_DASHBOARD_URL, NetworkConstant.METHOD_GET, this,mActivity);
            networkService.call( new HashMap<String, String>());
        } else
        {
            AppUtils.toast(mActivity, getString(R.string.internet_error));

        }
    }
    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response)
    {
        try {
            JSONObject object = new JSONObject(response);
            Log.e("Response ","=====>   "+response);
            if (!object.getBoolean(AppConstant.RES_KEY_ERROR))
            {
                JSONObject dataObj=object.optJSONObject("data");

                JSONObject reportObj= dataObj.optJSONObject("report");

                int auditCount = dataObj.optInt("audit_count");
                mNoOfAuditTV.setText(""+dataObj.optInt("auditor_count"));
                mNoOfLocationTV.setText(""+dataObj.optInt("location_count"));
                String first = ""+reportObj.optInt("failed_question_count");
                String second = "/"+reportObj.optInt("question_count");
                String next = "<font color='#616161'>"+second+"</font>";
                mNoOfNonComplianceTV.setText(Html.fromHtml(first + next));
                mNoOfAverageNonCompTV.setText(""+reportObj.optInt("avg_failed_question_count"));

                yValueseBench[0]=reportObj.optInt("audit_count");
                yValueseBench[1]=reportObj.optInt("failed_audit_count");
                mTotalTV.setText(getString(R.string.text_total)+" "+reportObj.optInt("audit_count"));
                mFailedTV.setText(getString(R.string.text_failed)+" "+reportObj.optInt("failed_audit_count"));
                mTotalActionTV.setText(getString(R.string.text_total)+" "+dataObj.optInt("action_plan_count"));

                JSONArray arrayInspection=dataObj.optJSONArray("brand_std_status_count");
                JSONArray arrayAction=dataObj.optJSONArray("action_plan_status_count");

                for (int i=0;i<arrayInspection.length();i++)
                {
                    JSONObject obj=arrayInspection.optJSONObject(i);
                    InspectionDashboard  dashboard=new InspectionDashboard();
                    dashboard.setStatus_count(obj.optInt("status_count"));
                    dashboard.setStatus_id(obj.optInt("status_id"));
                    dashboard.setStatus_name(obj.optString("status_name"));
                    mInspectionList.add(dashboard);
                    if(obj.optInt("status_id")==1)
                    {
                        mScheduleByTV.setText(""+obj.optInt("status_count")+"/"+auditCount);
                        mSchedulePerTV.setText(""+(obj.optInt("status_count")*100)/auditCount+"%");
                    }
                    if(obj.optInt("status_id")==2)
                    {
                        mInProressByTV.setText(""+obj.optInt("status_count")+"/"+auditCount);
                        mInprogressPerTV.setText(""+(obj.optInt("status_count")*100)/auditCount+"%");
                    }
                    if(obj.optInt("status_id")==4)
                    {
                        mCompleteByTV.setText(""+obj.optInt("status_count")+"/"+auditCount);
                        mCompletePerTV.setText(""+(obj.optInt("status_count")*100)/auditCount+"%");
                    }
                    if(obj.optInt("status_id")<0)
                    {
                        moverDueByTV.setText(""+obj.optInt("status_count")+"/"+auditCount);
                        moverDuePerTV.setText(""+(obj.optInt("status_count")*100)/auditCount+"%");
                    }
                }

                for (int i=0;i<arrayAction.length();i++)
                {
                    JSONObject obj=arrayAction.optJSONObject(i);
                    ActionDashboard  dashboard=new ActionDashboard();
                    dashboard.setStatus_count(obj.optInt("status_count"));
                    dashboard.setStatus_id(obj.optInt("status_id"));
                    dashboard.setStatus_name(obj.optString("status_name"));
                    yVals1.add(new PieEntry(obj.optInt("status_count"), i));
                    xVals.add(obj.optString("status_name"));
                    mActionList.add(dashboard);

                    if(obj.optInt("status_id")==1) {
                        mPendingApprovalCTV.setText(obj.optString("status_name")+" " + obj.optInt("status_count"));
                    }
                    if(obj.optInt("status_id")==2) {
                        mRejectedCTV.setText(obj.optString("status_name")+" " + obj.optInt("status_count"));
                    }
                    if(obj.optInt("status_id")==3) {
                        mApprovedCTV.setText(obj.optString("status_name")+" "  + obj.optInt("status_count"));
                    }
                    if(obj.optInt("status_id")==4) {
                        mOverDueCTV.setText(obj.optString("status_name")+" "  + obj.optInt("status_count"));
                    }
                    if(obj.optInt("status_id")==5) {
                        mCompleteCTV.setText(obj.optString("status_name")+" "  + obj.optInt("status_count"));
                    }


                }

                setDataForPieChart();
                setDataForPieChartBenchmark();

            } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {

                AppUtils.toast((BaseActivity) mActivity, object.getString(AppConstant.RES_KEY_MESSAGE));
            }


        }
        catch (Exception e)
        {
            if(mActivity!=null)
                AppUtils.toast(mActivity, getString(R.string.oops));
        }
        mSpinKitView.setVisibility(View.GONE);

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("onNetworkCallError","===>"+errorMessage);
        AppUtils.toast(mActivity, getString(R.string.oops));
        mSpinKitView.setVisibility(View.GONE);
    }

}