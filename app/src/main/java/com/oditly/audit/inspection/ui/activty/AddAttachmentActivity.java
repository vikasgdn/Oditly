package com.oditly.audit.inspection.ui.activty;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.AddAttachmentAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.dialog.CustomDialog;
import com.oditly.audit.inspection.localDB.media.MediaDBImpl;
import com.oditly.audit.inspection.model.audit.AddAttachment.AddAttachmentInfo;
import com.oditly.audit.inspection.model.audit.AddAttachment.AddAttachmentRootObject;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.AddAuditSignatureRequest;
import com.oditly.audit.inspection.network.apirequest.AddBSAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.AddQuestionAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.GetReportRequest;
import com.oditly.audit.inspection.network.apirequest.OktaTokenRefreshRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;
import com.oditly.audit.inspection.util.CirclePagerIndicatorDecoration;
import com.oditly.audit.inspection.util.LocationUtils;
import com.thorny.photoeasy.PhotoEasy;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.balakrishnan.easycam.CameraBundleBuilder;
import in.balakrishnan.easycam.CameraControllerActivity;

public class AddAttachmentActivity extends BaseActivity implements View.OnClickListener,
        BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate {

    @BindView(R.id.tv_header_title)
    TextView mTitleTV;
    @BindView(R.id.tv_attachment_count)
    TextView tvAttachmentCount;
    @BindView(R.id.recycler_view_attachment_list)
    RecyclerView attachmentList;
    @BindView(R.id.add_attachment_layout)
    LinearLayout addAttachmentLayout;
    @BindView(R.id.floating_btn_add_attachment)
    ImageView addAttachmentBtn;
    @BindView(R.id.add_attachment_text)
    TextView add_attachment_text;
    @BindView(R.id.ll_parent_progress)
    RelativeLayout mProgressLayoutLL;
    private LocationUtils mLocationUtils;
    private boolean isVideoPermission=false;
    private int isGalleryDisable=1;

    public int EDIT_IMAGE_POS=0;
    private  String auditId = "",sectionGroupId = "",sectionId = "", questionId = "",attachType = "", longitude = "",latitude = "",date = "";
    private int attachmentCount = 0;
    private  Context context;
    private  CustomDialog customDialog;
    private CustomDialog imageCustomDialog;
    //private AppLocationService appLocationService;
    private static final String TAG = AddAttachmentActivity.class.getSimpleName();
    private AddAttachmentListAdapter viewPagerAdapter;
    private int mImageCounter=0;
    private ArrayList<Uri> mURIimageList;
    private MediaDBImpl mMediaDB;
    private String mCurrentPhotoPath="";
    private PhotoEasy photoEasy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attachment);
        mLocationUtils=new LocationUtils(this);
        AppPreferences.INSTANCE.initAppPreferences(this);
        context = this;
        ButterKnife.bind(AddAttachmentActivity.this);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }
       /* else
            mLocationUtils.beginUpdates(this);*/

        //appLocationService = new AppLocationService(context);
        mMediaDB = MediaDBImpl.getInstance(context);

        initView();
        initVar();
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra("attachmentCount", ""+attachmentCount);
        setResult(RESULT_CANCELED, result);    //set cancell by vikas
        finish();
    }

    @Override
    protected void initView() {
        super.initView();
        mTitleTV = findViewById(R.id.tv_header_title);
        mTitleTV.setText(R.string.text_attachment);
        tvAttachmentCount = findViewById(R.id.tv_attachment_count);
        attachmentList = findViewById(R.id.recycler_view_attachment_list);
        addAttachmentLayout = findViewById(R.id.add_attachment_layout);
        add_attachment_text = findViewById(R.id.add_attachment_text);
        addAttachmentBtn = findViewById(R.id.floating_btn_add_attachment);
        mProgressLayoutLL=findViewById(R.id.ll_parent_progress);
        findViewById(R.id.iv_header_left).setOnClickListener(this);
        addAttachmentBtn.setOnClickListener(this);

    }

    @Override
    protected void initVar() {
        super.initVar();
        date = AppUtils.getDate(Calendar.getInstance().getTime());
        auditId = getIntent().getStringExtra("auditId");
        attachType = getIntent().getStringExtra("attachType");
        sectionGroupId = getIntent().getStringExtra("sectionGroupId");
        sectionId = getIntent().getStringExtra("sectionId");
        questionId = getIntent().getStringExtra("questionId");
        isGalleryDisable = getIntent().getIntExtra(AppConstant.GALLERY_DISABLE,1);
        mURIimageList=new ArrayList<>();

        getOldMediaAttachmentList(attachType);
        getLatLong();

        addAttachmentBtn.performClick(); // new changes
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floating_btn_add_attachment:
                openDCRDialog();
                break;
            case R.id.iv_header_left:
                onBackPressed();
                break;
        }
    }

    private void getLatLong() {
        latitude = String.format("%.3f", mLocationUtils.getLatitude());
        longitude = String.format("%.3f", mLocationUtils.getLongitude());
    }

    private void openDCRDialog() {
        imageCustomDialog = new CustomDialog(context, R.layout.upload_image_dailog);
        imageCustomDialog.setCancelable(true);
        if (isGalleryDisable==0)
        {
            imageCustomDialog.findViewById(R.id.tv_gallery).setVisibility(View.GONE);
            imageCustomDialog.findViewById(R.id.tv_gallery_vdo).setVisibility(View.GONE);
        }

        imageCustomDialog.findViewById(R.id.tv_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideoPermission=false;
                if (ActivityCompat.checkSelfPermission(AddAttachmentActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddAttachmentActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, AppConstant.GALLERY_PERMISSION_REQUEST);
                } else {
                    chooseImagesFromGallery();
                }
                imageCustomDialog.dismiss();
            }
        });
        imageCustomDialog.findViewById(R.id.tv_gallery_vdo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                isVideoPermission=true;
                if (ActivityCompat.checkSelfPermission(AddAttachmentActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddAttachmentActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, AppConstant.GALLERY_PERMISSION_REQUEST);
                } else {
                    chooseImagesFromGalleryVDO();
                }
                imageCustomDialog.dismiss();
            }
        });
        imageCustomDialog.findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideoPermission=false;
                cameraPermission();
                imageCustomDialog.dismiss();
            }
        });
        imageCustomDialog.findViewById(R.id.tv_cameravideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideoPermission=true;
                cameraPermission();
                //Intent intent=new Intent(context,AudioRecordActivity.class);
                //context.startActivity(intent);
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



    private void chooseImagesFromGallery() {
        BSImagePicker pickerDialog = new BSImagePicker.Builder(BuildConfig.APPLICATION_ID + ".provider")
                .setMaximumDisplayingImages(200)
                .isMultiSelect()
                .setTag("")
                .setMinimumMultiSelectCount(1)
                .setMaximumMultiSelectCount(10)
                .build();
        pickerDialog.show(getSupportFragmentManager(), "picker");
    }

    private void chooseImagesFromGalleryVDO() {
        System.gc();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, AppConstant.REQUEST_TAKE_VDO);
    }


    private void setAttachmentList(ArrayList<AddAttachmentInfo> arrayList) {
        ArrayList<AddAttachmentInfo> attachmentInfoArrayList = new ArrayList<>();

        for (int i = 0; i < arrayList.size(); i++) {
            AddAttachmentInfo info = arrayList.get(i);
            String fileType = info.getFile_type();
            if (fileType.contains("image/") || fileType.contains("video/")  || fileType.contains("octet-stream") ) {
                attachmentInfoArrayList.add(info);
            }
        }
        AddAttachmentAdapter addAttachmentAdapter = new AddAttachmentAdapter(context, attachmentInfoArrayList, attachType, auditId, sectionGroupId, sectionId, questionId);
        attachmentList.setLayoutManager(new LinearLayoutManager(context));
        attachmentList.setAdapter(addAttachmentAdapter);
    }

    private void cameraPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddAttachmentActivity.this, new String[]{Manifest.permission.CAMERA}, AppConstant.REQUEST_FOR_CAMERA);
        } else {
            System.gc();
            if (isVideoPermission)
                takeVideoFromCamera();
            else {
                takePhotoFromCamera();
            }

        }
    }
    private void takeVideoFromCamera()
    {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 8);
        //  takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10);

        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, AppConstant.REQUEST_TAKE_VDO);
        }
    }
    private void takePhotoFromCamera() {
      /*  Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, AppConstant.REQUEST_TAKE_PHOTO);
                }
            }
            catch (Exception e){e.printStackTrace();}
        }*/

    /*    Intent intent=new Intent(AddAttachmentActivity.this,CameraActivity.class);
        startActivityForResult(intent,AppConstant.REQUEST_TAKE_PHOTO);
        overridePendingTransition(R.anim.pull_in_left,R.anim.pull_from_right);*/

        Intent intent = new Intent(this, CameraControllerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("inputData", new CameraBundleBuilder()
                .setFullscreenMode(false)
                .setDoneButtonString("Add")
                .setDoneButtonDrawable(R.drawable.circle_color_green)
                .setSinglePhotoMode(true)
                .setMax_photo(1)
                .setManualFocus(true)
                .setBucketName(getClass().getName()+""+System.currentTimeMillis())
                .setPreviewEnableCount(false)
                .setPreviewIconVisiblity(false)
                .setPreviewPageRedirection(false)
                .setEnableDone(false)
                .setClearBucket(false)
                .createCameraBundle());
        startActivityForResult(intent, AppConstant.REQUEST_TAKE_PHOTO);

        // photoEasy = PhotoEasy.builder().setActivity(this).build();

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConstant.REQUEST_TAKE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationUtils.beginUpdates(this);
                }
                break;
            case AppConstant.REQUEST_FOR_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isVideoPermission)
                        takeVideoFromCamera();
                    else
                        takePhotoFromCamera();
                }
                break;
            case AppConstant.GALLERY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(isVideoPermission)
                        chooseImagesFromGalleryVDO();
                    else
                        chooseImagesFromGallery();
                }
                else
                    AppUtils.toast(this,"Permission Denied");
                break;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,  /* prefix */".jpg",         /* suffix */storageDir      /* directory */);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppConstant.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try{
                String dataPath= data.getStringExtra("camerax");
                // String dataURI= data.getStringExtra("cameraURI");
                // AppLogger.e("Addattachment uri", "  "+dataURI);
                AppLogger.e("Addattachment path", "  "+dataPath);
              /*  Uri uri = Uri.fromFile(new File(dataPath));
                AppLogger.e("Addattachment uri", " size "+uri);
                if (uri != null && uri.toString().length()>10) {
                    mURIimageList.add(uri);
                    addDescriptionDialog();
                }*/
                String[] list = data.getStringArrayExtra("resultData");
                if (list!=null && list.length>0) {
                    for (int i = 0; i < list.length; i++)
                        mURIimageList.add(Uri.fromFile(new File(list[i])));
                    addDescriptionDialog();
                } else {
                    AppUtils.toast(AddAttachmentActivity.this, "Image Not Attached" );
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.toast(AddAttachmentActivity.this, "Result Some technical error. Please try again." );
            }

        }
        else if(requestCode == AppConstant.EDIT_IMAGE && resultCode == RESULT_OK){
            try {
                Uri uri = Uri.fromFile(new File(data.getStringExtra("path")));
                viewPagerAdapter.updateImage(EDIT_IMAGE_POS,uri);
            }
            catch (Exception e)
            {e.printStackTrace();}
        }
        else
        {
            if (requestCode == AppConstant.REQUEST_TAKE_VDO && resultCode == RESULT_OK) {
                try {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri!=null)
                    {
                        byte[] imageByteData = AppUtils.readBytes(selectedImageUri, context);
                        mProgressLayoutLL.setVisibility(View.VISIBLE);
                        uploadMediaFileAttachment(selectedImageUri.toString(), imageByteData, "Description BS Section video", "video");

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private List<Uri> listOfImageString=new ArrayList();
    private void addDescriptionDialog()
    {
        listOfImageString.addAll(mURIimageList);
        ((OditlyApplication)getApplicationContext()).setmAttachImageList(listOfImageString);
        customDialog = new CustomDialog(context, R.layout.add_attachment_dailog);
        customDialog.setCancelable(false);

        RecyclerView attach_List_recycler_view = customDialog.findViewById(R.id.rv_image_list);
        viewPagerAdapter = new AddAttachmentListAdapter(context, mURIimageList);
        attach_List_recycler_view.addItemDecoration(new CirclePagerIndicatorDecoration());
        attach_List_recycler_view.setAdapter(viewPagerAdapter);
        customDialog.show();
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        try {
            if (uri != null) {
                mURIimageList.add(uri);
                addDescriptionDialog();
            } else {
                AppUtils.toast(AddAttachmentActivity.this, "Image Not Attached" + tag);
            }
        }catch (Exception e){
            AppUtils.toast(AddAttachmentActivity.this, "Some technical error. Please try again." );
        }

    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        try{
            if (uriList != null)
            {
                mURIimageList.addAll(uriList);
                addDescriptionDialog();
            } else {
                AppUtils.toast(AddAttachmentActivity.this, "Image Not Attached" + tag);
            }
        }catch (Exception e){
            AppUtils.toast(AddAttachmentActivity.this, "Some technical error. Please try again." );
        }

    }

    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {
        Glide.with(AddAttachmentActivity.this).load(imageUri).into(ivImage);
    }

    String url="";
    public void getOldMediaAttachmentList(String attachType)
    {
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "GetAttachmentResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AddAttachmentRootObject addAttachmentRootObject = new GsonBuilder().create().fromJson(object.toString(), AddAttachmentRootObject.class);
                        if (addAttachmentRootObject.getData() != null && addAttachmentRootObject.getData().toString().length() > 0) {
                            setAttachmentList(addAttachmentRootObject.getData());
                            int size = addAttachmentRootObject.getData().size();
                            attachmentCount =  size;
                            tvAttachmentCount.setVisibility(View.VISIBLE);
                            tvAttachmentCount.setText("" + size + "/20 Uploaded");
                            if (size >= 20) {
                                addAttachmentLayout.setVisibility(View.GONE);
                            } else {
                                addAttachmentLayout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            tvAttachmentCount.setVisibility(View.VISIBLE);
                        }
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppLogger.e(TAG, "GetAttachmentError: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();
            }
        };

        if (attachType.equalsIgnoreCase("bsSection"))
            url = NetworkURL.BSATTACHMENT + "?audit_id=" + auditId + "&section_group_id=" + sectionGroupId + "&section_id=" + sectionId;
        else
            url = NetworkURL.BSATTACHMENT + "?audit_id=" + auditId + "&section_group_id=" + sectionGroupId + "&section_id=" + sectionId + "&question_id=" + questionId;

         if (AppPreferences.INSTANCE.getProviderName().equalsIgnoreCase(AppConstant.OKTA))
         {
             GetReportRequest getReportRequest = new GetReportRequest(AppPreferences.INSTANCE.getAccessToken(context), AppPreferences.INSTANCE.getOktaToken(context), context, url, stringListener, errorListener);
             VolleyNetworkRequest.getInstance(context).addToRequestQueue(getReportRequest);
         }
         else {
             if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                 FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                         .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                             public void onComplete(@NonNull Task<GetTokenResult> task) {
                                 if (task.isSuccessful()) {
                                     String token = task.getResult().getToken();
                                     GetReportRequest getReportRequest = new GetReportRequest(AppPreferences.INSTANCE.getAccessToken(context), token, context, url, stringListener, errorListener);
                                     VolleyNetworkRequest.getInstance(context).addToRequestQueue(getReportRequest);
                                 }
                             }
                         });
             }
         }

    }



    private void uploadMediaFileAttachment(String media,byte[] imageByteData, String description, String type) {
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mMediaDB.addMediaToDB(auditId,sectionId,sectionGroupId,questionId,media);
                AppLogger.e(TAG, "AddAttachmentResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.text_updatedsuccessfully), Toast.LENGTH_SHORT).show();
                        getOldMediaAttachmentList(attachType);
                    } else {
                        Toast.makeText(getApplicationContext(),object.getString(AppConstant.RES_KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                mProgressLayoutLL.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressLayoutLL.setVisibility(View.GONE);
                AppLogger.e(TAG, "AddAttachmentError: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();
            }
        };

        String fileName = "Oditly-" + date ;

        if (AppPreferences.INSTANCE.getProviderName().equalsIgnoreCase(AppConstant.OKTA))
        {
            if (System.currentTimeMillis()<AppPreferences.INSTANCE.getOktaTokenExpireTime(this))
            {
                if (attachType.equalsIgnoreCase("bsQuestion")) {
                    AddQuestionAttachmentRequest addBSAttachmentRequest = new AddQuestionAttachmentRequest(
                            AppPreferences.INSTANCE.getAccessToken(context), NetworkURL.BSATTACHMENT, fileName, imageByteData, auditId,
                            sectionGroupId, sectionId, questionId, description, "0", latitude, longitude, type, AppPreferences.INSTANCE.getOktaToken(context), context, stringListener, errorListener);
                    VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
                } else {
                    AddBSAttachmentRequest addBSAttachmentRequest = new AddBSAttachmentRequest(
                            AppPreferences.INSTANCE.getAccessToken(context), NetworkURL.BSATTACHMENT, fileName, imageByteData, auditId,
                            sectionGroupId, sectionId, description, "0", latitude, longitude, type, AppPreferences.INSTANCE.getOktaToken(context), context,
                            stringListener, errorListener);
                    VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
                }         }
            else
            {
                Response.Listener<JSONObject> jsonListener = new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        AppLogger.e("TAG", " Token SUCCESS Response: " + response);
                        AppUtils.parseRefreshTokenRespone(response,AddAttachmentActivity.this);
                        if (attachType.equalsIgnoreCase("bsQuestion")) {
                            AddQuestionAttachmentRequest addBSAttachmentRequest = new AddQuestionAttachmentRequest(
                                    AppPreferences.INSTANCE.getAccessToken(context), NetworkURL.BSATTACHMENT, fileName, imageByteData, auditId,
                                    sectionGroupId, sectionId, questionId, description, "0", latitude, longitude, type, AppPreferences.INSTANCE.getOktaToken(context), context, stringListener, errorListener);
                            VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
                        } else {
                            AddBSAttachmentRequest addBSAttachmentRequest = new AddBSAttachmentRequest(
                                    AppPreferences.INSTANCE.getAccessToken(context), NetworkURL.BSATTACHMENT, fileName, imageByteData, auditId,
                                    sectionGroupId, sectionId, description, "0", latitude, longitude, type, AppPreferences.INSTANCE.getOktaToken(context), context,
                                    stringListener, errorListener);
                            VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
                        }         }
                };
                Response.ErrorListener errListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppLogger.e("TAG", "ERROR Response: " + error);
                    }
                };
                OktaTokenRefreshRequest tokenRequest = new OktaTokenRefreshRequest(AppUtils.getTokenJson(AddAttachmentActivity.this),jsonListener, errListener);
                VolleyNetworkRequest.getInstance(AddAttachmentActivity.this).addToRequestQueue(tokenRequest);
            }


        }
        else {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    String token = task.getResult().getToken();
                                    if (attachType.equalsIgnoreCase("bsQuestion")) {
                                        AddQuestionAttachmentRequest addBSAttachmentRequest = new AddQuestionAttachmentRequest(
                                                AppPreferences.INSTANCE.getAccessToken(context), NetworkURL.BSATTACHMENT, fileName, imageByteData, auditId,
                                                sectionGroupId, sectionId, questionId, description, "0", latitude, longitude, type, token, context, stringListener, errorListener);
                                        VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
                                    } else {
                                        AddBSAttachmentRequest addBSAttachmentRequest = new AddBSAttachmentRequest(
                                                AppPreferences.INSTANCE.getAccessToken(context), NetworkURL.BSATTACHMENT, fileName, imageByteData, auditId,
                                                sectionGroupId, sectionId, description, "0", latitude, longitude, type, token, context,
                                                stringListener, errorListener);
                                        VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
                                    }
                                }
                            }
                        });
            }
        }

    }


    public class AddAttachmentListAdapter extends RecyclerView.Adapter<AddAttachmentListAdapter.AddAttachmentListViewHolder> {
        private ArrayList<Uri> imageURI;
        Context context;
        byte[] imageByteData = new byte[0];
        public AddAttachmentListAdapter(Context context, ArrayList<Uri> imageListURI) {
            this.imageURI = imageListURI;
            this.context = context;
        }
        public void updateImage(int pos, Uri uri){
            imageURI.set(pos,uri);
            notifyItemChanged(pos);
        }

        @NonNull
        @Override
        public AddAttachmentListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_add_attachment, parent, false);

            return new AddAttachmentListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final AddAttachmentListViewHolder holder, final int position) {
            try
            {
                Bitmap bitmapPlain  = MediaStore.Images.Media.getBitmap(context.getContentResolver(),mURIimageList.get(position));
                if (bitmapPlain!=null)
                {
                    getLatLong();
                    String imageText="";
                    if(!new LocationUtils(AddAttachmentActivity.this).hasLocationEnabled())
                        imageText="location disabled, "+AppUtils.getCurrentDateImage();
                    else
                        imageText=""+latitude+","+longitude+","+AppUtils.getCurrentDateImage();

                    Bitmap bitmap = AppUtils.resizeImage(mURIimageList.get(position),bitmapPlain, 1400, 1400);
                    Bitmap drawBitmap=new AppUtils().drawTextToBitmap(context,bitmap,imageText);
                    holder.imageView.setImageBitmap(drawBitmap);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    drawBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    imageByteData = byteArrayOutputStream.toByteArray();
                }

            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.toast((AddAttachmentActivity)context,"Some technical error. Please try again.");
            }

            holder.description.setText("");

            holder.submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mImageCounter++;
                    if (imageURI.size() == 1) {
                        AppUtils.hideKeyboard(context, view);
                    }
                   /* String text = "";
                    if (holder.description.getText().toString().length() > 0) {
                        text = holder.description.getText().toString();
                    }*/

                    if (attachType.equalsIgnoreCase("bsQuestion"))
                        attachmentCount++;

                    uploadMediaFileAttachment(mURIimageList.get(position).toString(),imageByteData, holder.description.getText().toString(),"image");
                    imageURI.remove(position);
                    //  int size = imageURI.size();
                    if (imageURI.size() > 0)
                        AppUtils.toast((AddAttachmentActivity) context, "" + imageURI.size() + " images left");

                    notifyDataSetChanged();

                    if(imageURI.size()==0){
                        customDialog.dismiss();
                        Intent result = new Intent();
                        result.putExtra("attachmentCount", ""+attachmentCount);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                }
            });
            holder.cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // mImageCounter
                    listOfImageString.remove(mImageCounter);
                    if (imageURI.size() <= 1) {
                        Intent result = new Intent();
                        result.putExtra("attachmentCount", ""+attachmentCount);
                        setResult(RESULT_OK, result);
                        finish();
                        customDialog.dismiss();
                    } else {
                        imageURI.remove(position);
                        int size = imageURI.size();
                        if (size > 0) {
                            AppUtils.toast((AddAttachmentActivity) context, "" + size + " images left");
                        }
                        notifyDataSetChanged();
                    }
                }
            });

            holder.editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EDIT_IMAGE_POS = position;
                    Intent intent = new Intent(context, EditImageActivity.class);
                    intent.putExtra("bitmap", imageURI.get(position).toString());
                    startActivityForResult(intent,AppConstant.EDIT_IMAGE);
                }
            });


        }

        @Override
        public int getItemCount() {
            return imageURI.size();
        }

        public class AddAttachmentListViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            EditText description;
            TextView submitButton;
            TextView cancelButton;
            ImageButton editImage;

            public AddAttachmentListViewHolder(View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.iv_attached_image);
                description = itemView.findViewById(R.id.et_description);
                submitButton = itemView.findViewById(R.id.tv_submit_btn);
                cancelButton = itemView.findViewById(R.id.tv_cancel_btn);
                editImage = itemView.findViewById(R.id.edit_image);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Add onStop",";;;;;onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("ADD onPause",";;;;;onPause");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("ADD onDestroy",";;;;;onDestroy");

    }
}
