package com.oditly.audit.inspection.ui.fragment.actionplan;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.ActionMediaAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.AppDialogs;
import com.oditly.audit.inspection.dialog.CustomDialog;
import com.oditly.audit.inspection.model.actionData.ActionInfo;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkServiceMultipartActionComplete;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.model.actionData.ActionCompleteRequestBean;
import com.oditly.audit.inspection.network.apirequest.OktaTokenRefreshRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.ui.activty.ActionCreateActivity;
import com.oditly.audit.inspection.ui.activty.MainActivity;
import com.oditly.audit.inspection.ui.fragment.BaseFragment;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import in.balakrishnan.easycam.CameraBundleBuilder;
import in.balakrishnan.easycam.CameraControllerActivity;

public class ActionCompleteFragment extends BaseFragment implements View.OnClickListener, INetworkEvent, BSImagePicker.OnSingleImageSelectedListener, BSImagePicker.OnMultiImageSelectedListener, BSImagePicker.ImageLoaderDelegate {
    public static final String ARG_PAGE = "ARG_PAGE";
    private static final int REQUEST_FOR_CAMERA = 100;
    private static final int REQUEST_TAKE_PHOTO = 101;
    private static final int REQUEST_TAKE_VDO = 102;
    private CustomDialog customDialog;
    /* access modifiers changed from: private */
    public CustomDialog imageCustomDialog;
    private boolean isVideoPermission = false;
    private ActionInfo mAuditInfoActionPlanData;
    private EditText mCommentET;
    private EditText mRootCauseET;
    private EditText mNotifyUserET;
    private TextView mCommentErrorTV;
    private ImageView mFabMedia;
    private ArrayList<File> mFileimageList;
    /* access modifiers changed from: private */
    public ActionMediaAdapter mMediaAdapter;
    private TextView mMediaCountTV;
    private RecyclerView mMediaRecycleView;
    private int mPage;
    /* access modifiers changed from: private */
    public int mPosition = 0;
    /* access modifiers changed from: private */
    public RelativeLayout mSpinKitView;
    private Button mSubmitButton;
    /* access modifiers changed from: private */
    public ArrayList<Uri> mURIimageList;

    public static ActionCompleteFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE", page);
        ActionCompleteFragment fragment = new ActionCompleteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPage = getArguments().getInt("ARG_PAGE");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_action_complete, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(2);
        this.mAuditInfoActionPlanData = ((OditlyApplication) this.mActivity.getApplicationContext()).getmActionPlanData();
        AppPreferences.INSTANCE.initAppPreferences(this.mActivity);
        initView(getView());
        initVar();
    }

    /* access modifiers changed from: protected */
    public void initVar() {
        super.initVar();
        this.mURIimageList = new ArrayList<>();
        this.mFileimageList = new ArrayList<>();
        ActionMediaAdapter actionMediaAdapter = new ActionMediaAdapter(this.mActivity, this, this.mURIimageList);
        this.mMediaAdapter = actionMediaAdapter;
        this.mMediaRecycleView.setAdapter(actionMediaAdapter);
        TextView textView = this.mMediaCountTV;
        textView.setText(getString(R.string.text_media)+" (0/" + this.mAuditInfoActionPlanData.getMedia_count() + ")");
    }

    /* access modifiers changed from: protected */
    public void initView(View view) {
        super.initView(view);
        this.mMediaRecycleView = (RecyclerView) view.findViewById(R.id.rv_imagelist);
        this.mMediaCountTV = (TextView) view.findViewById(R.id.tv_media_count);
        this.mFabMedia = (ImageView) view.findViewById(R.id.fb_media);
        this.mSubmitButton = (Button) view.findViewById(R.id.btn_submit);
        this.mCommentET = (EditText) view.findViewById(R.id.et_commentbox);
        this.mCommentErrorTV = (TextView) view.findViewById(R.id.tv_comment_error);
        mRootCauseET= (EditText) view.findViewById(R.id.et_rootcause_analysis);
        mNotifyUserET= (EditText) view.findViewById(R.id.et_notifyuser);


        this.mSpinKitView = (RelativeLayout) view.findViewById(R.id.ll_parent_progress);
        this.mSubmitButton.setOnClickListener(this);
        this.mFabMedia.setOnClickListener(this);
        Log.e("CAN COMPLETE===> ", "" + this.mAuditInfoActionPlanData.isCan_complete());
        if (!this.mAuditInfoActionPlanData.isCan_complete()) {
            this.mCommentErrorTV.setText(getString(R.string.text_this_audit_could));
            this.mCommentErrorTV.setVisibility(View.VISIBLE);
            this.mSubmitButton.setVisibility(View.GONE);
            this.mFabMedia.setVisibility(View.GONE);
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_submit) {
            String obj = this.mCommentET.getText().toString();
            if (this.mURIimageList.size() < this.mAuditInfoActionPlanData.getMedia_count()) {
                int remainCount = this.mAuditInfoActionPlanData.getMedia_count() - this.mURIimageList.size();
                AppUtils.toast(mActivity, getString(R.string.text_submit_requredmedia_action).replace("MMM",""+remainCount));
            }
            else {
                if (AppPreferences.INSTANCE.getProviderName().equalsIgnoreCase(AppConstant.OKTA)) {

                    if (System.currentTimeMillis() < AppPreferences.INSTANCE.getOktaTokenExpireTime(mActivity)) {
                        postActionCreateServerData(AppPreferences.INSTANCE.getOktaToken(mActivity));
                    } else {
                        Response.Listener<JSONObject> jsonListener = new Response.Listener<JSONObject>() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onResponse(JSONObject response) {
                                AppLogger.e("TAG", " Token SUCCESS Response: " + response);
                                AppUtils.parseRefreshTokenRespone(response, mActivity);
                                postActionCreateServerData(AppPreferences.INSTANCE.getOktaToken(mActivity));
                            }
                        };
                        Response.ErrorListener errListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                AppLogger.e("TAG", "ERROR Response: " + error);
                            }
                        };
                        OktaTokenRefreshRequest tokenRequest = new OktaTokenRefreshRequest(AppUtils.getTokenJson(mActivity), jsonListener, errListener);
                        VolleyNetworkRequest.getInstance(mActivity).addToRequestQueue(tokenRequest);
                    }
                }
                else {
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
        } else if (id == R.id.fb_media) {
            openMediaDialog();
        }
    }

    private void postActionCreateServerData(String tokenFirebase) {
        if (NetworkStatus.isNetworkConnected(this.mActivity)) {
            this.mSpinKitView.setVisibility(View.VISIBLE);
            ActionCompleteRequestBean bean = new ActionCompleteRequestBean();
            bean.setMobile("1");
            bean.setAudit_id("" + this.mAuditInfoActionPlanData.getAudit_id());
            bean.setAction_plan_id("" + this.mAuditInfoActionPlanData.getAction_plan_id());
            bean.setComplete_comment(this.mCommentET.getText().toString());
            bean.setCc_emails(this.mNotifyUserET.getText().toString());
            bean.setIssue_fix_comment(this.mRootCauseET.getText().toString());

            NetworkServiceMultipartActionComplete actionComplete=new NetworkServiceMultipartActionComplete(NetworkURL.ACTION_PLAN_COMPLETE, bean, mFileimageList,tokenFirebase, this, this.mActivity);
            actionComplete.call(null);
        }
        else
            AppUtils.toast(this.mActivity, getString(R.string.internet_error));
    }


    private void openMediaDialog() {
        CustomDialog customDialog2 = new CustomDialog(this.mActivity,R.layout.upload_image_dailog);
        this.imageCustomDialog = customDialog2;
        customDialog2.setCancelable(true);
        this.imageCustomDialog.findViewById(R.id.tv_gallery_vdo).setVisibility(View.GONE);
        this.imageCustomDialog.findViewById(R.id.tv_cameravideo).setVisibility(View.GONE);
        this.imageCustomDialog.findViewById(R.id.tv_gallery).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ActionCompleteFragment.this.mActivity, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                    ActionCompleteFragment.this.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 103);
                } else {
                    ActionCompleteFragment.this.chooseImagesFromGallery();
                }
                ActionCompleteFragment.this.imageCustomDialog.dismiss();
            }
        });
        this.imageCustomDialog.findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ActionCompleteFragment.this.cameraPermission();
                ActionCompleteFragment.this.imageCustomDialog.dismiss();
            }
        });
        this.imageCustomDialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ActionCompleteFragment.this.imageCustomDialog.dismiss();
            }
        });
        this.imageCustomDialog.show();
    }

    /* access modifiers changed from: private */
    public void cameraPermission() {
        if (ContextCompat.checkSelfPermission(this.mActivity, "android.permission.CAMERA") != 0) {
            requestPermissions(new String[]{"android.permission.CAMERA"}, 100);
            return;
        }
        System.gc();
        takePhotoFromCamera();
    }

    /* access modifiers changed from: private */
    public void chooseImagesFromGallery() {
        new BSImagePicker.Builder("com.oditly.audit.inspection.provider").setMaximumDisplayingImages(200).isMultiSelect().setTag("").setMinimumMultiSelectCount(1).setMaximumMultiSelectCount(10).build().show(getChildFragmentManager(), "picker");
    }

    private void chooseImagesFromGalleryVDO() {
        System.gc();
        startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Video.Media.EXTERNAL_CONTENT_URI), 102);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(this.mActivity, CameraControllerActivity.class);
        intent.setFlags(603979776);
        CameraBundleBuilder manualFocus = new CameraBundleBuilder().setFullscreenMode(false).setDoneButtonString("Add").setDoneButtonDrawable(R.drawable.circle_color_green).setSinglePhotoMode(true).setMax_photo(3).setManualFocus(true);
        intent.putExtra("inputData", manualFocus.setBucketName(getClass().getName() + "" + System.currentTimeMillis()).setPreviewEnableCount(false).setPreviewIconVisiblity(false).setPreviewPageRedirection(false).setEnableDone(false).setClearBucket(false).createCameraBundle());
        startActivityForResult(intent, 101);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 100) {
            if (requestCode == 103) {
                if (grantResults.length <= 0 || grantResults[0] != 0) {
                    AppUtils.toast(this.mActivity, "Permission Denied");
                } else {
                    chooseImagesFromGallery();
                }
            }
        } else if (grantResults.length > 0 && grantResults[0] == 0) {
            takePhotoFromCamera();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == -1) {
                try {
                    String[] list = data.getStringArrayExtra("resultData");
                    if (list == null || list.length <= 0) {
                        AppUtils.toast(this.mActivity, getString(R.string.text_image_not_attched));
                        return;
                    }
                    for (int i = 0; i < list.length; i++) {
                        this.mURIimageList.add(Uri.fromFile(new File(list[i])));
                        this.mFileimageList.add(new File(list[i]));
                    }
                    TextView textView = this.mMediaCountTV;
                    textView.setText(getResources().getString(R.string.text_comment_comp)+" (" + this.mURIimageList.size() + "/" + this.mAuditInfoActionPlanData.getMedia_count() + ")");
                    this.mMediaAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    AppUtils.toast(this.mActivity, getString(R.string.oops));
                }
            }
        }
    }

    public void onNetworkCallInitiated(String service) {
    }

    public void onNetworkCallCompleted(String type, String service, String response) {
        this.mSpinKitView.setVisibility(View.GONE);
        Log.e("===RESPONSE===> ", "===" + response);
        try {
            JSONObject object = new JSONObject(response);
            String message = object.getString(AppConstant.RES_KEY_MESSAGE);
            if (!object.getBoolean("error"))
            {
                this.mCommentET.setText("");
                AppDialogs.messageDialogWithOKButton(mActivity,message);
            }
            else
                AppDialogs.messageDialogWithOKButton(mActivity,message);

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.toastDisplayForLong(this.mActivity, this.mActivity.getString(R.string.oops));
        }
    }

    public void onNetworkCallError(String service, String errorMessage) {
        this.mSpinKitView.setVisibility(View.GONE);
        AppUtils.toastDisplayForLong(this.mActivity, this.mActivity.getString(R.string.oops));
    }

    public void onSingleImageSelected(Uri uri, String tag) {
        if (uri != null) {
            try {
                this.mURIimageList.add(uri);
                this.mFileimageList.add(new File(getPath(uri)));
                TextView textView = this.mMediaCountTV;
                textView.setText(getString(R.string.text_comment_comp)+" (" + this.mURIimageList.size() + "/" + this.mAuditInfoActionPlanData.getMedia_count() + ")");
                this.mMediaAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                AppUtils.toast(this.mActivity, getString(R.string.oops));
            }
        } else {
            Activity activity = this.mActivity;
            AppUtils.toast(activity, getString(R.string.text_image_not_attched) + tag);
        }
    }

    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        if (uriList != null) {
            try {
                this.mURIimageList.addAll(uriList);
                for (int i = 0; i < uriList.size(); i++) {
                    this.mFileimageList.add(new File(getPath(uriList.get(i))));
                }
                TextView textView = this.mMediaCountTV;
                textView.setText(getString(R.string.text_comment_comp)+" (" + this.mURIimageList.size() + "/" + this.mAuditInfoActionPlanData.getMedia_count() + ")");
                this.mMediaAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                AppUtils.toast(this.mActivity, getString(R.string.oops));
            }
        } else {
            Activity activity = this.mActivity;
            AppUtils.toast(activity, getString(R.string.text_image_not_attched) + tag);
        }
    }

    public void loadImage(Uri imageUri, ImageView ivImage) {
        Glide.with(this.mActivity).load(imageUri).into(ivImage);
    }

    public String getPath(Uri uri) {
        Cursor cursor = this.mActivity.getContentResolver().query(uri, new String[]{"_data"}, (String) null, (String[]) null, (String) null);
        if (cursor == null) {
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow("_data");
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }
}
