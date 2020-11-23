package com.oditly.audit.inspection.ui.fragment.actionplan;

import android.Manifest;
import android.app.Activity;
import android.app.MediaRouteButton;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oditly.audit.inspection.BuildConfig;
import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.ActionMediaAdapter;
import com.oditly.audit.inspection.adapter.AddBSMediaAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.CustomDialog;
import com.oditly.audit.inspection.model.actionData.ActionInfo;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.network.NetworkService;
import com.oditly.audit.inspection.network.NetworkStatus;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.AddActionAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.AddBSAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.AddQuestionAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.ui.activty.AddAttachmentActivity;
import com.oditly.audit.inspection.ui.activty.MainActivity;
import com.oditly.audit.inspection.ui.activty.ResetPasswordScreen;
import com.oditly.audit.inspection.ui.activty.SignInPasswordActivity;
import com.oditly.audit.inspection.ui.fragment.BaseFragment;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;
import com.oditly.audit.inspection.util.PermissionUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.balakrishnan.easycam.CameraBundleBuilder;
import in.balakrishnan.easycam.CameraControllerActivity;

public class ActionCompleteFragment extends BaseFragment implements View.OnClickListener , INetworkEvent ,  BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private Button mSubmitButton;
    private EditText mCommentET;
    private TextView mCommentErrorTV;
    private RelativeLayout mSpinKitView;
    private ActionInfo mAuditInfoActionPlanData;
    private  CustomDialog customDialog;
    private CustomDialog imageCustomDialog;
    private boolean isVideoPermission=false;
    private static final int REQUEST_TAKE_PHOTO = 101;
    private static final int REQUEST_TAKE_VDO = 102;
    private static final int REQUEST_FOR_CAMERA = 100;
    private RecyclerView mMediaRecycleView;
    private ArrayList<Uri> mURIimageList;
    private ActionMediaAdapter mMediaAdapter;
    private TextView mMediaTV;
    private int mPosition = 0;
    private ImageView mFabMedia;


    public static ActionCompleteFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ActionCompleteFragment fragment = new ActionCompleteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_action_complete, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mAuditInfoActionPlanData=((OditlyApplication)mActivity.getApplicationContext()).getmActionPlanData();
        AppPreferences.INSTANCE.initAppPreferences(mActivity);
        initView(getView());
        initVar();

    }

    @Override
    protected void initVar() {
        super.initVar();
        mURIimageList=new ArrayList<>();
        mMediaAdapter = new ActionMediaAdapter(mActivity,this, mURIimageList);
        mMediaRecycleView.setAdapter(mMediaAdapter);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mMediaRecycleView =(RecyclerView) view.findViewById(R.id.rv_imagelist);
        mMediaTV =(TextView) view.findViewById(R.id.tv_media);
        mFabMedia =(ImageView) view.findViewById(R.id.fb_media);

        mSubmitButton=(Button)view.findViewById(R.id.btn_submit);
        mCommentET=(EditText)view.findViewById(R.id.et_commentbox);
        mCommentErrorTV=(TextView)view.findViewById(R.id.tv_comment_error);
        mSpinKitView=(RelativeLayout) view.findViewById(R.id.ll_parent_progress);
        mSubmitButton.setOnClickListener(this);
        mFabMedia.setOnClickListener(this);
        Log.e("CAN COMPLETE===> ",""+mAuditInfoActionPlanData.isCan_complete());
        if(!mAuditInfoActionPlanData.isCan_complete()) {
            mCommentErrorTV.setText(getString(R.string.text_this_audit_could));
            mCommentErrorTV.setVisibility(View.VISIBLE);
            mSubmitButton.setVisibility(View.GONE);
            mFabMedia.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.btn_submit:
                String comment = mCommentET.getText().toString();
                if (mURIimageList.size()<=0)
                    postCommentAndCompleteAction(comment);
                else
                    AppUtils.toast(mActivity,"Please click on the media file(s) to upload.");
                break;
            case R.id.fb_media:
                openMediaDialog();
                break;
            case R.id.iv_imageupload:
            case R.id.iv_image:
                mPosition =  Integer.parseInt(view.getTag().toString());
                uploadMediaFileAttachment(AppUtils.convertImageURIToByte(mURIimageList.get(mPosition),mActivity));
                break;
        }

    }

    private void postCommentAndCompleteAction(String comment)
    {
        if (NetworkStatus.isNetworkConnected(mActivity)) {

            mSpinKitView.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(NetworkConstant.REQ_PARAM_MOBILE, "1");
            params.put(NetworkConstant.REQ_PARAM_AUDIT_ID, ""+mAuditInfoActionPlanData.getAudit_id());
            params.put(NetworkConstant.REQ_PARAM_ACTION_PLANID, ""+mAuditInfoActionPlanData.getAction_plan_id());
            params.put(NetworkConstant.REQ_PARAM_COMMENT, mCommentET.getText().toString());

            Log.e("URL==>",""+NetworkURL.ACTION_PLAN_COMPLETE);
            NetworkService networkService = new NetworkService(NetworkURL.ACTION_PLAN_COMPLETE, NetworkConstant.METHOD_POST, this,mActivity);
            networkService.call(params);
        } else

            AppUtils.toast(mActivity, mActivity.getString(R.string.internet_error));

    }


    private void uploadMediaFileAttachment(byte[] imageByteData) {
        mSpinKitView.setVisibility(View.VISIBLE);
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e("TAG", "AddAttachmentResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        mURIimageList.remove(mPosition);
                        mMediaAdapter.notifyDataSetChanged();
                        Toast.makeText(mActivity, getString(R.string.text_updatedsuccessfully), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mActivity,object.getString(AppConstant.RES_KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                mSpinKitView.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSpinKitView.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();
            }
        };
        String fileName = "Oditly-"+System.currentTimeMillis() ;
        AddActionAttachmentRequest addBSAttachmentRequest = new AddActionAttachmentRequest(AppPreferences.INSTANCE.getAccessToken(mActivity), NetworkURL.POST_ACTIONFILE_URL, fileName, imageByteData,""+mAuditInfoActionPlanData.getAudit_id(),""+mAuditInfoActionPlanData.getAction_plan_id(), stringListener, errorListener);
        VolleyNetworkRequest.getInstance(mActivity).addToRequestQueue(addBSAttachmentRequest);

    }

    private void openMediaDialog() {
        imageCustomDialog = new CustomDialog(mActivity, R.layout.upload_image_dailog);
        imageCustomDialog.setCancelable(true);
        imageCustomDialog.findViewById(R.id.tv_gallery_vdo).setVisibility(View.GONE);
        imageCustomDialog.findViewById(R.id.tv_cameravideo).setVisibility(View.GONE);
        imageCustomDialog.findViewById(R.id.tv_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, AppConstant.GALLERY_PERMISSION_REQUEST);
                } else {
                    chooseImagesFromGallery();
                }
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
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, REQUEST_FOR_CAMERA);
        } else {
            System.gc();
            takePhotoFromCamera();
        }
    }
    private void chooseImagesFromGallery() {
        BSImagePicker pickerDialog = new BSImagePicker.Builder(BuildConfig.APPLICATION_ID + ".provider")
                .setMaximumDisplayingImages(200)
                .isMultiSelect()
                .setTag("")
                .setMinimumMultiSelectCount(1)
                .setMaximumMultiSelectCount(10)
                .build();
        pickerDialog.show(getChildFragmentManager(), "picker");
    }

    private void chooseImagesFromGalleryVDO() {
        System.gc();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_TAKE_VDO);
    }


    private void takePhotoFromCamera() {
        Intent intent = new Intent(mActivity, CameraControllerActivity.class);
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
                    AppUtils.toast(mActivity,"Permission Denied");

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.REQUEST_TAKE_PHOTO && resultCode == mActivity.RESULT_OK) {
            try{
                String[] list = data.getStringArrayExtra("resultData");
                if (list!=null && list.length>0) {
                    for (int i = 0; i < list.length; i++)
                        mURIimageList.add(Uri.fromFile(new File(list[i])));

                    mMediaAdapter.notifyDataSetChanged();

                } else {
                    AppUtils.toast(mActivity, "Image Not Attached" );
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.toast(mActivity, "Result Some technical error. Please try again." );
            }



        }
    }


    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response)
    {
        mSpinKitView.setVisibility(View.GONE);

        Log.e("===RESPONSE===> ","==="+response);
        try {
            JSONObject object = new JSONObject(response);
            String message = object.getString(AppConstant.RES_KEY_MESSAGE);
            if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                AppUtils.toastDisplayForLong(mActivity, message);
                mCommentET.setText("");

                Intent intent =new Intent(mActivity, MainActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();

            }else
                AppUtils.toastDisplayForLong(mActivity, message);
        }
        catch (Exception e){
            e.printStackTrace();
            AppUtils.toastDisplayForLong(mActivity, mActivity.getString(R.string.oops));
        }

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage)
    {
        mSpinKitView.setVisibility(View.GONE);
        AppUtils.toastDisplayForLong(mActivity, mActivity.getString(R.string.oops));
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        try {
            if (uri != null) {
                mURIimageList.add(uri);
                mMediaAdapter.notifyDataSetChanged();
            } else
                AppUtils.toast(mActivity, "Image Not Attached" + tag);
        }catch (Exception e){
            AppUtils.toast(mActivity, "Some technical error. Please try again." );

        }

    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        try{
            if (uriList != null)
            {
                mURIimageList.addAll(uriList);
                mMediaAdapter.notifyDataSetChanged();
            } else {
                AppUtils.toast(mActivity, "Image Not Attached" + tag);
            }
        }catch (Exception e){
            AppUtils.toast(mActivity, "Some technical error. Please try again." );
        }
    }
    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {
        Glide.with(mActivity).load(imageUri).into(ivImage);
    }
}
