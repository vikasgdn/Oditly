package com.oditly.audit.inspection.ui.activty;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.BuildConfig;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.ActionUploadMediaAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.dialog.CustomDialog;
import com.oditly.audit.inspection.model.audit.createaudit.ActionFilterRootObject;
import com.oditly.audit.inspection.model.audit.createaudit.AditorReviewBean;
import com.oditly.audit.inspection.model.audit.createaudit.PriorityBean;
import com.oditly.audit.inspection.model.audit.createaudit.SectionBean;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkServiceJSON;
import com.oditly.audit.inspection.network.NetworkServiceMultipart;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.model.actionData.AddAdHocActionPlan;
import com.oditly.audit.inspection.network.apirequest.ApiRequest;
import com.oditly.audit.inspection.network.apirequest.OktaTokenRefreshRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import in.balakrishnan.easycam.CameraBundleBuilder;
import in.balakrishnan.easycam.CameraControllerActivity;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class ActionCreateActivity extends BaseActivity implements INetworkEvent, MultiSelectDialog.SubmitCallbackListener,BSImagePicker.OnSingleImageSelectedListener,BSImagePicker.OnMultiImageSelectedListener, BSImagePicker.ImageLoaderDelegate {

    private Calendar cal = Calendar.getInstance();
    int startYear = cal.get(Calendar.YEAR);
    int startMonth = cal.get(Calendar.MONTH);
    int startDay = cal.get(Calendar.DAY_OF_MONTH);

    private RelativeLayout mProgressBarRL;
    private EditText mTitleET;
    private EditText mCorrectiveActionET;
    private TextView mDueDateET;
    private Spinner mPriorityTypeSPN;
    private Spinner mLocationTypeSPN;
    private Spinner mSectionTypeSPN;
    private List<String> mLocationList, mLocationListID;
    private List<String> mPriorityList, mPriorityListID;
    private List<String> mSectionList, mSectionListID;
    private CustomDialog imageCustomDialog;
    private boolean isVideoPermission=false;
    private static final int REQUEST_TAKE_PHOTO = 101;
    private static final int REQUEST_FOR_CAMERA = 100;
    private RecyclerView mMediaRecycleView;
    private ArrayList<Uri> mURIimageList;
    private ActionUploadMediaAdapter mMediaAdapter;


    private String mLocatioID = "";


    private ArrayAdapter mLocationAdapter;
    private ArrayList<MultiSelectModel> mMultiSelectModelsList;
    private List<AditorReviewBean> mAuditorNameList;
    private ArrayList<Integer> mAuditorsIDSelected;
    private ArrayList<File> mMediaFileList;
    private EditText mAuditorNameET;
    private EditText mMeidaCountET;
    private ArrayList<String> mReviewerList, getmReviewerListID;
    private String mPriorityID = "";
    private String mSectionID = "0", mSectionGroupID, mQuestionID, mAuditID = "";
    private TextView mTitleErrorTV, mDetailsErrorTV, mDueDateErrorTV, mAssigneeErrorTV;
    private String mActionCreateUsingLocationURL = "", mActionCreateUsingAuditURL = "";
    private String mFromWhere = "";
    private RelativeLayout mLocationRL;
    private RelativeLayout mSectionRL;
    private TextView mLocationTv,mSectionTV;
    private TextView tvClosureProofTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_action);
        initView();
        initVar();

    }

    @Override
    protected void initView() {
        super.initView();

        findViewById(R.id.iv_header_left).setOnClickListener(this);
        findViewById(R.id.btn_create).setOnClickListener(this);

        tvClosureProofTV= findViewById(R.id.tv_closureproof);
        tvClosureProofTV.setOnClickListener(this);

        TextView textView = (TextView) findViewById(R.id.tv_header_title);
        textView.setText(getString(R.string.text_create_action));
        mProgressBarRL = (RelativeLayout) findViewById(R.id.ll_parent_progress);

        mDueDateET = (TextView) findViewById(R.id.tv_duedate);
        mTitleET = (EditText) findViewById(R.id.et_title);
        mCorrectiveActionET = (EditText) findViewById(R.id.et_correctiveaction);
        mMeidaCountET=(EditText) findViewById(R.id.et_media_count);
        mLocationRL = (RelativeLayout) findViewById(R.id.rl_location);
        mSectionRL = (RelativeLayout) findViewById(R.id.rl_section);

        mPriorityTypeSPN = findViewById(R.id.spn_priority);
        mLocationTypeSPN = findViewById(R.id.spn_locationtype);

        mSectionTypeSPN = findViewById(R.id.spn_section);
        mAuditorNameET = findViewById(R.id.et_assignee);

        mDueDateErrorTV = findViewById(R.id.tv_plandateerror);
        mTitleErrorTV = findViewById(R.id.tv_titleerror);
        mDetailsErrorTV = findViewById(R.id.tv_correctiveactioerror);
        mAssigneeErrorTV = findViewById(R.id.tv_assigneeerror);

        mLocationTv=findViewById(R.id.tv_location);
        mSectionTV=findViewById(R.id.tv_section);

        findViewById(R.id.fb_media).setOnClickListener(this);
        mMediaRecycleView=findViewById(R.id.rv_imagelist);

        mAuditorNameET.setOnClickListener(this);
        mDueDateET.setOnClickListener(this);

        Intent intent = getIntent();
        mFromWhere = intent.getStringExtra(AppConstant.FROMWHERE) == null ? "" : intent.getStringExtra(AppConstant.FROMWHERE);
        if (mFromWhere.equalsIgnoreCase("Audit")) {
            mAuditID = intent.getStringExtra(AppConstant.AUDIT_ID);
            mSectionGroupID = intent.getStringExtra(AppConstant.SECTION_GROUPID);
            mSectionID = intent.getStringExtra(AppConstant.SECTION_ID);
            mQuestionID = intent.getStringExtra(AppConstant.QUESTION_ID);

            mLocationRL.setVisibility(View.GONE);
            mSectionRL.setVisibility(View.GONE);
            mLocationTv.setVisibility(View.GONE);
            mSectionTV.setVisibility(View.GONE);
            mMediaRecycleView.setVisibility(View.GONE);
            findViewById(R.id.fb_media).setVisibility(View.GONE);
            getActionCreateDataFromServerUsingAudit();
        } else {
            mSectionID="0";
            getLocationFilterListFromServer();
        }

        mLocationTypeSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mLocationListID != null && mLocationListID.size() > 0) {
                    mLocatioID = mLocationListID.get(position);
                    getActionCreateDataFromServer(mLocatioID);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mPriorityTypeSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mPriorityListID != null && mPriorityListID.size() > 0)
                    mPriorityID = mPriorityListID.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSectionTypeSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSectionListID != null && mSectionListID.size() > 0)
                    mSectionID = mSectionListID.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void initVar() {
        super.initVar();

        mLocationList = new ArrayList<>();
        mLocationListID = new ArrayList<>();

        mPriorityList = new ArrayList<>();
        mPriorityListID = new ArrayList<>();

        mSectionListID = new ArrayList<>();
        mSectionList = new ArrayList<>();


        mMultiSelectModelsList = new ArrayList<>();
        mAuditorNameList = new ArrayList<>();

        mReviewerList = new ArrayList<>();
        getmReviewerListID = new ArrayList<>();

        mAuditorsIDSelected = new ArrayList<>();

        mURIimageList=new ArrayList<>();
        mMediaFileList=new ArrayList<>();

        mMediaAdapter = new ActionUploadMediaAdapter(this,this, mURIimageList);
        mMediaRecycleView.setAdapter(mMediaAdapter);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.iv_header_left:
                finish();
                break;
            case R.id.tv_closureproof:
                new SimpleTooltip.Builder(this)
                        .anchorView(mMeidaCountET)
                        .text("This is the minimum number of attachments required as evidence of completion of the task")
                        .gravity(Gravity.CENTER)
                        .animated(true)
                        .transparentOverlay(true)
                        .build()
                        .show();
                break;
            case R.id.tv_duedate:
                DatePickerDialog datePickerDialog1 = new DatePickerDialog(this, (datePicker, i, i1, i2) -> ((TextView) view).setText(datePicker.getYear() + "-" + String.format("%02d-%02d", (datePicker.getMonth() + 1), i2)), startYear, startMonth, startDay);
                datePickerDialog1.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog1.show();
                break;
            case R.id.tv_moreoptions:
                Intent intent = new Intent(this, ActionCreateMoreActivity.class);
                startActivityForResult(intent, 1001);
                break;
            case R.id.fb_media:
                openMediaDialog();
                break;
            case R.id.iv_image:
                break;
            case R.id.btn_create:
                String title = mTitleET.getText().toString();
                String details = mCorrectiveActionET.getText().toString();
                String dueDate = mDueDateET.getText().toString();
                String all = mAuditorNameET.getText().toString();
                 if (TextUtils.isEmpty(title))
                    mTitleErrorTV.setVisibility(View.VISIBLE);
               else if (TextUtils.isEmpty(dueDate))
                    mDueDateErrorTV.setVisibility(View.VISIBLE);
                else if (all.equalsIgnoreCase("All"))
                    mAssigneeErrorTV.setVisibility(View.VISIBLE);
                else {
                    if (mFromWhere.equalsIgnoreCase("Audit"))
                        postActionCreateServerDataUsingAuditID();
                    else {
                        if (AppPreferences.INSTANCE.getProviderName().equalsIgnoreCase(AppConstant.OKTA))
                        {
                            if (System.currentTimeMillis()<AppPreferences.INSTANCE.getOktaTokenExpireTime(this))
                            {
                                postActionCreateServerData(AppPreferences.INSTANCE.getOktaToken(this));
                            }
                            else
                            {
                                Response.Listener<JSONObject> jsonListener = new Response.Listener<JSONObject>() {
                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        AppLogger.e("TAG", " Token SUCCESS Response: " + response);
                                        AppUtils.parseRefreshTokenRespone(response,ActionCreateActivity.this);
                                        postActionCreateServerData(AppPreferences.INSTANCE.getOktaToken(ActionCreateActivity.this));
                                    }
                                };
                                Response.ErrorListener errListener = new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        AppLogger.e("TAG", "ERROR Response: " + error);
                                    }
                                };
                                OktaTokenRefreshRequest tokenRequest = new OktaTokenRefreshRequest(AppUtils.getTokenJson(ActionCreateActivity.this),jsonListener, errListener);
                                VolleyNetworkRequest.getInstance(ActionCreateActivity.this).addToRequestQueue(tokenRequest);
                            }
                        } else {
                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                if (task.isSuccessful()) {
                                                    postActionCreateServerData(task.getResult().getToken());
                                                }
                                            }
                                        });
                            }
                        }
                    }
                }
                break;

            case R.id.et_assignee:
                populateMultiSelectData();
                getMultiSelectionDialog(mMultiSelectModelsList, getString(R.string.text_assigneeselect));
                break;
        }

    }

    private void getLocationFilterListFromServer() {
        if (NetworkStatus.isNetworkConnected(this)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            NetworkService networkService = new NetworkService(NetworkURL.AUDIT_LOCATION_LIST, NetworkConstant.METHOD_GET, this, this);
            networkService.call(new HashMap<String, String>());
        } else {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }

    private void getActionCreateDataFromServer(String locationid) {
        if (NetworkStatus.isNetworkConnected(this)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            mActionCreateUsingLocationURL = NetworkURL.GET_ACTIONCREATE_USING_LOCATION_API + locationid;
            Log.e("Filter url==> ", "" + mActionCreateUsingLocationURL);
            NetworkService networkService = new NetworkService(mActionCreateUsingLocationURL, NetworkConstant.METHOD_GET, this, this);
            networkService.call(new HashMap<String, String>());
        } else {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }

    private void getActionCreateDataFromServerUsingAudit() {
        if (NetworkStatus.isNetworkConnected(this)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            mActionCreateUsingAuditURL = NetworkURL.GET_ACTIONCREATE_USING_AUDIT_API + mAuditID;
            Log.e("Filter url==> ", "" + mActionCreateUsingAuditURL);
            NetworkService networkService = new NetworkService(mActionCreateUsingAuditURL, NetworkConstant.METHOD_GET, this, this);
            networkService.call(new HashMap<String, String>());
        } else {
            AppUtils.toast(this, getString(R.string.internet_error));

        }
    }

    private void postActionCreateServerData(String firebaseToken) {
        if (NetworkStatus.isNetworkConnected(this)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            JSONArray jsArray = new JSONArray(mAuditorsIDSelected);
            try {
                AddAdHocActionPlan bean=new AddAdHocActionPlan();
                bean.setLocation_id(mLocatioID);
                bean.setPriority_id(mPriorityID);
                bean.setPlanned_date(mDueDateET.getText().toString());
                bean.setTitle(mTitleET.getText().toString());
                bean.setAction_details(mCorrectiveActionET.getText().toString());
                bean.setAssigned_user_id(jsArray);
                if (mMeidaCountET.getText().toString().length()==0)
                    bean.setMedia_count("0");
                else
                    bean.setMedia_count(mMeidaCountET.getText().toString());

                bean.setSection_id(mSectionID);


                NetworkServiceMultipart networkService = new NetworkServiceMultipart(NetworkURL.ACTION_PLAN_ADD_ADHOC,bean,mMediaFileList,firebaseToken, this, this);
                networkService.call(null);
            } catch (Exception e) {
                AppUtils.toast(this, getString(R.string.internet_error));
                e.printStackTrace();
            }
        } else
            AppUtils.toast(this, getString(R.string.internet_error));

    }

    private void postActionCreateServerDataUsingAuditID() {
        if (NetworkStatus.isNetworkConnected(this)) {
            mProgressBarRL.setVisibility(View.VISIBLE);
            JSONArray jsArray = new JSONArray(mAuditorsIDSelected);
            try {
                JSONObject params = new JSONObject();
              //  params.put(NetworkConstant.REQ_PARAM_MOBILE, "1");
               //  params.put(NetworkConstant.REQ_PARAM_SECTION_GROUP_ID, mSectionGroupID);
               // params.put(NetworkConstant.REQ_PARAM_SECTIONID, mSectionID);
                params.put(NetworkConstant.REQ_PARAM_AUDIT_ID, mAuditID);
                params.put(NetworkConstant.REQ_PARAM_QUSITION_ID, mQuestionID);
                params.put(NetworkConstant.REQ_PARAM_TITLE, mTitleET.getText().toString());
                params.put(NetworkConstant.REQ_PARAM_PRIORITYID, mPriorityID);
                params.put(NetworkConstant.REQ_PARAM_COMPLETE_MEDIA_COUNT,mMeidaCountET.getText().toString());
                params.put(NetworkConstant.REQ_PARAM_ACTION_DETAILS, mCorrectiveActionET.getText().toString());
                params.put(NetworkConstant.REQ_PARAM_PLANNED_DATE, mDueDateET.getText().toString());
                params.put(NetworkConstant.REQ_PARAM_ASSIGNED_USERID, jsArray);
                NetworkServiceJSON networkService = new NetworkServiceJSON(NetworkURL.ACTION_PLAN_ADD_NEW, NetworkConstant.METHOD_POST, this, this);
                networkService.call(params);
            } catch (Exception e) {
                AppUtils.toast(this, getString(R.string.internet_error));
                e.printStackTrace();
            }
        } else
            AppUtils.toast(this, getString(R.string.internet_error));

    }


    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {
        Log.e("Response==>", "" + response);
        if (service.equalsIgnoreCase(NetworkURL.AUDIT_LOCATION_LIST)) {
            try {
                mLocationListID.clear();
                mLocationList.clear();
                JSONObject jsonObject = new JSONObject(response);
                JSONObject childOBJ = jsonObject.getJSONObject("data");
                JSONArray jsonArray = childOBJ.optJSONArray("locations");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.optJSONObject(i);
                    mLocationList.add(obj.optString("location_name"));
                    mLocationListID.add("" + obj.optInt("location_id"));
                }
                mLocationAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mLocationList);
                mLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mLocationTypeSPN.setAdapter(mLocationAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (service.equalsIgnoreCase(mActionCreateUsingLocationURL)) {
            try {
                JSONObject object = new JSONObject(response);

                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    mAuditorNameList.clear();
                    mMultiSelectModelsList.clear();

                    mSectionListID.clear();
                    mSectionList.clear();

                    mPriorityListID.clear();
                    mPriorityList.clear();

                    ActionFilterRootObject teamRootObject = new GsonBuilder().create().fromJson(object.toString(), ActionFilterRootObject.class);
                    if (teamRootObject.getData().getUsers() != null && teamRootObject.getData().getUsers().size() > 0) {
                        mAuditorNameList.addAll(teamRootObject.getData().getUsers());
                    } else
                        AppUtils.toast((BaseActivity) this, object.getString(AppConstant.RES_KEY_MESSAGE));

                    if (teamRootObject.getData().getPriorities() != null && teamRootObject.getData().getPriorities().size() > 0) {
                        for (int i = 0; i < teamRootObject.getData().getPriorities().size(); i++) {
                            PriorityBean atype = teamRootObject.getData().getPriorities().get(i);
                            mPriorityListID.add("" + atype.getPriority_id());
                            mPriorityList.add("" + atype.getPriority_name());
                        }
                        ArrayAdapter priorityAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mPriorityList);
                        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mPriorityTypeSPN.setAdapter(priorityAdapter);

                    } else
                        AppUtils.toast((BaseActivity) this, object.getString(AppConstant.RES_KEY_MESSAGE));

                    if (teamRootObject.getData().getSections() != null && teamRootObject.getData().getSections().size() > 0) {
                        for (int i = 0; i < teamRootObject.getData().getSections().size(); i++) {
                            SectionBean atype = teamRootObject.getData().getSections().get(i);
                            mSectionListID.add("" + atype.getSection_id());
                            mSectionList.add("" + atype.getSection_title());
                        }
                        mSectionListID.add(0,"0");
                        mSectionList.add(0,"Please Select Section");
                        ArrayAdapter sectionAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mSectionList);
                        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSectionTypeSPN.setAdapter(sectionAdapter);

                    }


                } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    AppUtils.toast((BaseActivity) this, object.getString(AppConstant.RES_KEY_MESSAGE));
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.toast(this, getString(R.string.oops));
            }
        } else if (service.equalsIgnoreCase(mActionCreateUsingAuditURL)) {
            try {
                JSONObject object = new JSONObject(response);

                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    mAuditorNameList.clear();
                    mMultiSelectModelsList.clear();

                    ActionFilterRootObject teamRootObject = new GsonBuilder().create().fromJson(object.toString(), ActionFilterRootObject.class);
                    if (teamRootObject.getData().getUsers() != null && teamRootObject.getData().getUsers().size() > 0) {
                        mAuditorNameList.addAll(teamRootObject.getData().getUsers());
                    } else
                        AppUtils.toast((BaseActivity) this, object.getString(AppConstant.RES_KEY_MESSAGE));

                    if (teamRootObject.getData().getPriorities() != null && teamRootObject.getData().getPriorities().size() > 0) {
                        for (int i = 0; i < teamRootObject.getData().getPriorities().size(); i++) {
                            PriorityBean atype = teamRootObject.getData().getPriorities().get(i);
                            mPriorityListID.add("" + atype.getPriority_id());
                            mPriorityList.add("" + atype.getPriority_name());
                        }
                        ArrayAdapter ad1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mPriorityList);
                        ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mPriorityTypeSPN.setAdapter(ad1);

                    } else
                        AppUtils.toast((BaseActivity) this, object.getString(AppConstant.RES_KEY_MESSAGE));


                } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    AppUtils.toast((BaseActivity) this, object.getString(AppConstant.RES_KEY_MESSAGE));
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.toast(this, getString(R.string.oops));
            }
        } else {
            try {
                JSONObject object = new JSONObject(response);
                String message = object.getString(AppConstant.RES_KEY_MESSAGE);
                if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                    if (this.mFromWhere.equalsIgnoreCase("Audit")) {
                        AppUtils.toast(this, getString(R.string.text_action_has_been_created));
                        setResult(RESULT_OK, new Intent());
                        finish();
                    } else {
                        AppDialogs.messageDialogWithOKButton(this, getString(R.string.text_action_has_been_created));
                    }
                } else
                    AppUtils.toastDisplayForLong(this, message);
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.toastDisplayForLong(this, getString(R.string.oops));
            }
        }


        mProgressBarRL.setVisibility(View.GONE);
    }

    private void populateMultiSelectData() {
        mMultiSelectModelsList.clear();
        mReviewerList.clear();
        getmReviewerListID.clear();
        for (int i = 0; i < mAuditorNameList.size(); i++) {
            AditorReviewBean bean = mAuditorNameList.get(i);
            mReviewerList.add(bean.getName());
            getmReviewerListID.add("" + bean.getUser_id());
            MultiSelectModel data1 = new MultiSelectModel(bean.getUser_id(), bean.getName());
            mMultiSelectModelsList.add(data1);
        }
    }

    private void getMultiSelectionDialog(ArrayList<MultiSelectModel> model, String filterName) {
        MultiSelectDialog multiSelectDialog = new MultiSelectDialog()
                .title(filterName) //setting title for dialog
                .titleSize(20)
                .positiveText("Done")
                .negativeText("Cancel")
                .setMinSelectionLimit(1) //you can set minimum checkbox selection limit (Optional)
                .setMaxSelectionLimit(model.size()) //you can set maximum checkbox selection limit (Optional)
                // .preSelectIDsList(alreadySelectedCountries) //List of ids that you need to be selected
                .multiSelectList(model) // the multi select model list with ids and name
                .onSubmit(this);

        multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");

    }


    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("onNetworkCallError", "===>" + errorMessage);
        AppUtils.toast(this, getString(R.string.oops));
        mProgressBarRL.setVisibility(View.GONE);

    }


    @Override
    public void onSelected(ArrayList<Integer> id, ArrayList<String> name, String data) {
        mAuditorsIDSelected.clear();
        Log.e(";;;;;;;;;;;;;;;;;;;   ", name.toString());
        mAuditorNameET.setText(name.toString());
        mAuditorsIDSelected.addAll(id);
    }

    @Override
    public void onCancel() {

    }
    private void openMediaDialog() {
        imageCustomDialog = new CustomDialog(this, R.layout.upload_image_dailog);
        imageCustomDialog.setCancelable(true);
        imageCustomDialog.findViewById(R.id.tv_gallery_vdo).setVisibility(View.GONE);
        imageCustomDialog.findViewById(R.id.tv_cameravideo).setVisibility(View.GONE);
        imageCustomDialog.findViewById(R.id.tv_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    chooseImagesFromGallery();
                imageCustomDialog.dismiss();
            }
        });
        imageCustomDialog.findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPermission();
                imageCustomDialog.dismiss();
            }
        });
        imageCustomDialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                imageCustomDialog.dismiss();
            }
        });
        imageCustomDialog.show();

    }

    private void cameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_FOR_CAMERA);
        } else {
            System.gc();
            takePhotoFromCamera();
        }
    }
    private void chooseImagesFromGallery() {
    /*    BSImagePicker pickerDialog = new BSImagePicker.Builder(BuildConfig.APPLICATION_ID + ".provider")
                .setMaximumDisplayingImages(200)
                .isMultiSelect()
                .setTag("")
                .setMinimumMultiSelectCount(1)
                .setMaximumMultiSelectCount(10)
                .build();
        pickerDialog.show(getSupportFragmentManager(),"Picker");*/


        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, AppConstant.REQUEST_TAKE_PHOTO_GALLERY);


    }


    private void takePhotoFromCamera() {
        Intent intent = new Intent(this, CameraControllerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("inputData", new CameraBundleBuilder()
                .setFullscreenMode(false)
                .setDoneButtonString("Add")
                .setDoneButtonDrawable(R.drawable.circle_color_green)
                .setSinglePhotoMode(true)
                .setMax_photo(3)
                .setManualFocus(true)
                .setBucketName(getClass().getName()+""+System.currentTimeMillis())
                .setPreviewEnableCount(false)
                .setPreviewIconVisiblity(false)
                .setPreviewPageRedirection(false)
                .setEnableDone(false)
                .setClearBucket(false)
                .createCameraBundle());
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FOR_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhotoFromCamera();
                }
                break;
            case AppConstant.GALLERY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    chooseImagesFromGallery();
                else
                    AppUtils.toast(this,"Permission Denied");

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.REQUEST_TAKE_PHOTO && resultCode == this.RESULT_OK) {
            try{
                String[] list = data.getStringArrayExtra("resultData");
                if (list!=null && list.length>0) {
                    for (int i = 0; i < list.length; i++) {
                        mURIimageList.add(Uri.fromFile(new File(list[i])));
                        mMediaFileList.add(new File(list[i]));
                    }
                    mMediaAdapter.notifyDataSetChanged();

                } else {
                    AppUtils.toast(this, "Image Not Attached" );
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.toast(this, " Some technical error Camera. Please try again." );
            }
        }
        else{
            Uri selectedImage = data.getData();
            mMediaFileList.add(new File(getPath(selectedImage)));
            mURIimageList.add(selectedImage);
            mMediaAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        try{
            if (uriList != null)
            {
                mURIimageList.addAll(uriList);
                for (int i=0;i<mURIimageList.size();i++)
                    if (!mURIimageList.get(i).toString().contains("file"))
                        mMediaFileList.add(new File(getPath(mURIimageList.get(i))));

                mMediaAdapter.notifyDataSetChanged();
            } else {
                AppUtils.toast(this, "Image Not Attached" + tag);
            }
        }catch (Exception e){
            AppUtils.toast(this, "Some technical error. Please try again." );
        }
    }
    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        try {
            if (uri != null) {
                mMediaFileList.add(new File(getPath(uri)));
                mURIimageList.add(uri);
                mMediaAdapter.notifyDataSetChanged();
            } else
                AppUtils.toast(this, "Image Not Attached" + tag);
        }catch (Exception e){
            AppUtils.toast(this, "Some technical error. Please try again." );

        }

    }

    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {
        Glide.with(this).load(imageUri).into(ivImage);
    }



    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
}