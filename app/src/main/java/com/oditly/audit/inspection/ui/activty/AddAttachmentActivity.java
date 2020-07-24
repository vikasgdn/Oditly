package com.oditly.audit.inspection.ui.activty;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.GsonBuilder;
import com.oditly.audit.inspection.BuildConfig;
import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.adapter.AddAttachmentAdapter;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.dialog.CustomDialog;
import com.oditly.audit.inspection.model.audit.AddAttachment.AddAttachmentInfo;
import com.oditly.audit.inspection.model.audit.AddAttachment.AddAttachmentRootObject;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.AddBSAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.AddDSAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.AddESAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.AddQuestionAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.GetReportRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.services.AppLocationService;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;
import com.oditly.audit.inspection.util.CirclePagerIndicatorDecoration;
import com.oditly.audit.inspection.util.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private boolean isVideoPermission=false;

    private static final int REQUEST_FOR_CAMERA = 100;
    private static final int REQUEST_TAKE_PHOTO = 101;
    private static final int REQUEST_TAKE_VDO = 102;
    private static final int GALLERY_PERMISSION_REQUEST = 103;
    public int EDIT_IMAGE_POS=0;
    private static final int EDIT_IMAGE = 123;
    String mCurrentPhotoPath = "";
    String auditId = "";
    String sectionGroupId = "";
    String sectionId = "";
    String questionId = "";
    String attachType = "";
    String attachmentCount = "0";
    String longitude = "";
    String latitude = "";
    private String editable = "";
    String date = "";
    Context context;
    CustomDialog customDialog;
    private CustomDialog imageCustomDialog;
    AppLocationService appLocationService;
    private static final String TAG = AddAttachmentActivity.class.getSimpleName();
    AddAttachmentListAdapter viewPagerAdapter;
    //private List listSTR=new ArrayList();
    private int mImageCounter=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attachment);
        //  setStatusBarColor("#0B87E4");
        AppPreferences.INSTANCE.initAppPreferences(this);
        context = this;
        ButterKnife.bind(AddAttachmentActivity.this);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, 10);
        }
        appLocationService = new AppLocationService(context);
        initView();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent result = new Intent();
        result.putExtra("attachmentCount", attachmentCount);
        //  result.putParcelableArrayListExtra("IMAGE_URI",imageURI);
        // setResult(RESULT_OK, result);
        setResult(RESULT_CANCELED, result);    //set cancell by vikas
        finish();
    }

    @Override
    protected void initView() {
        super.initView();
        mTitleTV = findViewById(R.id.tv_header_title);
        mTitleTV.setText(R.string.text_attachment);
        //  setActionBar();
        date = AppUtils.getDate(Calendar.getInstance().getTime());
        //customDialog = new CustomDialog(context, R.layout.add_attachment_layout);
        tvAttachmentCount = findViewById(R.id.tv_attachment_count);
        attachmentList = findViewById(R.id.recycler_view_attachment_list);
        addAttachmentLayout = findViewById(R.id.add_attachment_layout);
        add_attachment_text = findViewById(R.id.add_attachment_text);
        addAttachmentBtn = findViewById(R.id.floating_btn_add_attachment);
        mProgressLayoutLL=findViewById(R.id.ll_parent_progress);

        findViewById(R.id.iv_header_left).setOnClickListener(this);

        addAttachmentBtn.setOnClickListener(this);
        auditId = getIntent().getStringExtra("auditId");
        attachType = getIntent().getStringExtra("attachType");
        sectionGroupId = getIntent().getStringExtra("sectionGroupId");
        sectionId = getIntent().getStringExtra("sectionId");
        questionId = getIntent().getStringExtra("questionId");
        editable = getIntent().getStringExtra("editable");
        AppLogger.e(TAG, "AttachType : " + attachType);

        if (attachType.equals("bsSection")) {
            getBsAttachmentList();
        } else if (attachType.equals("bsQuestion")) {
            getQuestionAttachmentList();
        } else if (attachType.equals("dsSection")) {
            getDsAttachmentList();
        } else if (attachType.equals("esSection")) {
            getEsAttachmentList();
        }

        if (editable.equals("1")) {
            addAttachmentBtn.setVisibility(View.GONE);
            add_attachment_text.setVisibility(View.GONE);
        } else {
            addAttachmentBtn.setVisibility(View.VISIBLE);
            add_attachment_text.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floating_btn_add_attachment:
                openDCRDialog();//TODO open dialog box to choose gallery or camera
                break;
            case R.id.iv_header_left:
                onBackPressed();
                break;
        }
    }

    private void getLatLong() {
        Location location = appLocationService.getLocation(LocationManager.GPS_PROVIDER, context);

        if (location != null) {
            double lat = location.getLatitude();
            double log = location.getLongitude();
            /*latitude = "" + location.getLatitude();
            longitude = "" + location.getLongitude();*/
            latitude = String.format("%.6f", lat);
            longitude = String.format("%.6f", log);
            Log.e("latitude", "" + lat);
            Log.e("longitude", "" + log);
            Log.e("latitude", "" + latitude);
            Log.e("longitude", "" + longitude);

        } else {
            //showSettingsAlert();
            latitude = "";
            longitude = "";
        }
    }

    private void openDCRDialog() {
        imageCustomDialog = new CustomDialog(context, R.layout.upload_image_dailog);
        imageCustomDialog.setCancelable(true);TextView tvGallery = imageCustomDialog.findViewById(R.id.tv_gallery);
        TextView tvGalleryVDO = imageCustomDialog.findViewById(R.id.tv_gallery_vdo);
        TextView tvCamera = imageCustomDialog.findViewById(R.id.tv_camera);
        TextView tvCancel = imageCustomDialog.findViewById(R.id.tv_cancel);

        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestGalleryPermissions()) {
                    chooseImagesFromGallery();
                }
                imageCustomDialog.dismiss();
            }
        });
        tvGalleryVDO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                isVideoPermission=true;
                if (checkAndRequestGalleryPermissions()) {
                    chooseImagesFromGalleryVDO();
                }
                imageCustomDialog.dismiss();
            }
        });
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPermission();
                imageCustomDialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                imageCustomDialog.dismiss();
            }
        });
        imageCustomDialog.show();

    }

    private boolean checkAndRequestGalleryPermissions() {

        int permissionStorageWrite = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionStorageRead = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionStorageWrite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionStorageRead != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            PermissionUtils.requestPermission(this, listPermissionsNeeded, GALLERY_PERMISSION_REQUEST);
            return false;
        }
        return true;
    }


    private void chooseImagesFromGallery() {
        System.gc();

        //Select from Gallery with multi selection
        BSImagePicker pickerDialog = new BSImagePicker.Builder(BuildConfig.APPLICATION_ID + ".provider")
                .setMaximumDisplayingImages(Integer.MAX_VALUE)
                .isMultiSelect()
                .setTag("")
                .setMinimumMultiSelectCount(1)
                .setMaximumMultiSelectCount(5)
                .build();
        pickerDialog.show(getSupportFragmentManager(), "picker");


    }

    private void chooseImagesFromGalleryVDO() {
        System.gc();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_TAKE_VDO);
    }


    private void setAttachmentList(ArrayList<AddAttachmentInfo> arrayList) {
        ArrayList<AddAttachmentInfo> attachmentInfoArrayList = new ArrayList<>();

        for (int i = 0; i < arrayList.size(); i++) {
            AddAttachmentInfo info = arrayList.get(i);
            String fileType = info.getFile_type();
            if (fileType.contains("image/") || fileType.contains("video/")  ) {
                attachmentInfoArrayList.add(info);
            }
        }
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(context,2, LinearLayoutManager.VERTICAL,false);
        AddAttachmentAdapter addAttachmentAdapter = new AddAttachmentAdapter(context, attachmentInfoArrayList, attachType, auditId, sectionGroupId, sectionId, questionId, editable);
        attachmentList.setLayoutManager(new LinearLayoutManager(context));
        attachmentList.setAdapter(addAttachmentAdapter);
    }

    private void cameraPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(AddAttachmentActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_FOR_CAMERA);

        } else {
            System.gc();
            takePhotoFromCamera();

        }
    }

    private void takePhotoFromCamera() {

        // Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(intent, REQUEST_TAKE_PHOTO);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,  /* prefix */".jpg",         /* suffix */storageDir      /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FOR_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhotoFromCamera();
                }
                break;
            case GALLERY_PERMISSION_REQUEST:
                boolean isGalleryPermissionGranted = PermissionUtils.checkPermissionGrantedOrNot(AddAttachmentActivity.this, permissions, grantResults);
                if (isGalleryPermissionGranted)
                {   if(isVideoPermission)
                    chooseImagesFromGalleryVDO();
                    else
                    chooseImagesFromGallery();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try{
                Uri uri = Uri.fromFile(new File(mCurrentPhotoPath));

                if (uri != null) {
                    ArrayList<Uri> uriList = new ArrayList<>();
                    uriList.add(uri);
                    addDescriptionDialog(uriList);
                } else {
                    AppUtils.toast(AddAttachmentActivity.this, "Image Not Attached" );
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.toast(AddAttachmentActivity.this, "Result Some technical error. Please try again." );
            }

        }else if(requestCode == EDIT_IMAGE && resultCode == RESULT_OK){
            Uri uri = Uri.fromFile(new File(data.getStringExtra("path")));
            viewPagerAdapter.updateImage(EDIT_IMAGE_POS,uri);
        }
        else
        {
            //  Toast.makeText(this,"Getting VDO",Toast.LENGTH_SHORT).show();
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_TAKE_VDO) {
                    Uri selectedImageUri = data.getData();
                    Log.e("URIIIIIII===>",""+selectedImageUri);
                    try {
                        byte[] imageByteData = readBytes(selectedImageUri);

                        //   showAppProgressDialog();
                        mProgressLayoutLL.setVisibility(View.VISIBLE);

                        if(attachType.equalsIgnoreCase("bsSection"))
                            addBsFileAttachment(imageByteData, "Descripption BS Section video","video");
                        else if(attachType.equalsIgnoreCase("bsQuestion"))
                            addQuestionFileAttachment(imageByteData, "Descripption Question video","video");
                        else if(attachType.equalsIgnoreCase("dsSection"))
                            addDsFileAttachment(imageByteData, "Descripption DS file video","video");
                        else
                            addEsFileAttachment(imageByteData, "Descripption ES File video","video");



                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

        }


    }

    public byte[] readBytes(Uri uri) throws IOException {
        // this dynamically extends to take the bytes you read
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    private List listOfImageString=new ArrayList();
    private void addDescriptionDialog(ArrayList<Uri> uriList)
    {

        for(int i=0;i<uriList.size();i++)
            listOfImageString.add(uriList.get(i).toString());

        ((OditlyApplication)getApplicationContext()).setmAttachImageList(listOfImageString);

        customDialog = new CustomDialog(context, R.layout.add_attachment_dailog);
        customDialog.setCancelable(false);

        getLatLong();

        //final ViewPager attachment_listing_viewpager = customDialog.findViewById(R.id.vp_attachment_listing);
        RecyclerView attach_List_recycler_view = customDialog.findViewById(R.id.rv_image_list);

        viewPagerAdapter = new AddAttachmentListAdapter(context, uriList);
        attach_List_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        attach_List_recycler_view.addItemDecoration(new CirclePagerIndicatorDecoration());
        attach_List_recycler_view.setAdapter(viewPagerAdapter);

        customDialog.show();
    }


    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        //Uri contentURI = data.getData();
        //File file = new File(String.valueOf(contentURI));
        try {
            if (uri != null) {
                ArrayList<Uri> uriList = new ArrayList<>();
                uriList.add(uri);
                addDescriptionDialog(uriList);
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
            if (uriList != null) {
                ArrayList<Uri> uris = new ArrayList<>();
                uris.addAll(uriList);
                addDescriptionDialog(uris);
            } else {
                AppUtils.toast(AddAttachmentActivity.this, "Image Not Attached" + tag);
            }
        }catch (Exception e){
            AppUtils.toast(AddAttachmentActivity.this, "Some technical error. Please try again." );
        }

    }

    @Override
    public void loadImage(File imageFile, ImageView ivImage) {
        Glide.with(AddAttachmentActivity.this).load(imageFile).into(ivImage);
    }

  /*  private void setActionBar() {
        initToolbar(toolbar);
        setTitle(getString(R.string.text_attachment));
        enableBack(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }*/

    public void getBsAttachmentList() {
        //showAppProgressDialog();
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
                            attachmentCount = "" + size;
                            tvAttachmentCount.setVisibility(View.VISIBLE);
                            tvAttachmentCount.setText("" + size + "/20 Uploaded");
                            if (size >= 20) {
                                addAttachmentLayout.setVisibility(View.GONE);
                            } else {
                                addAttachmentLayout.setVisibility(View.VISIBLE);
                            }
                            //brandStandardAuditAdapter.notifyDataSetChanged();
                        } else {
                            tvAttachmentCount.setVisibility(View.VISIBLE);
                        }
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //hideProgressDialog();
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hideProgressDialog();
                AppLogger.e(TAG, "GetAttachmentError: " + error.getMessage());
                //AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");
                Toast.makeText(getApplicationContext(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();

            }
        };

        String url = NetworkURL.BSATTACHMENT + "?"
                + "audit_id=" + auditId + "&"
                + "section_group_id=" + sectionGroupId + "&"
                + "section_id=" + sectionId;
        GetReportRequest getReportRequest = new GetReportRequest(AppPreferences.INSTANCE.getAccessToken(context), url, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(getReportRequest);
    }

    public void getQuestionAttachmentList() {
        //showAppProgressDialog();
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "GetAttachmentResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AddAttachmentRootObject addAttachmentRootObject = new GsonBuilder().create()
                                .fromJson(object.toString(), AddAttachmentRootObject.class);
                        if (addAttachmentRootObject.getData() != null &&
                                addAttachmentRootObject.getData().toString().length() > 0) {
                            setAttachmentList(addAttachmentRootObject.getData());
                            int size = addAttachmentRootObject.getData().size();
                            attachmentCount = "" + size;
                            tvAttachmentCount.setVisibility(View.VISIBLE);
                            tvAttachmentCount.setText("" + size + "/20 Uploaded");
                            if (size >= 20) {
                                addAttachmentLayout.setVisibility(View.GONE);
                            } else {
                                addAttachmentLayout.setVisibility(View.VISIBLE);
                            }
                            //brandStandardAuditAdapter.notifyDataSetChanged();
                        } else {
                            tvAttachmentCount.setVisibility(View.VISIBLE);
                        }
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context,
                                object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //hideProgressDialog();
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hideProgressDialog();
                AppLogger.e(TAG, "GetAttachmentError: " + error.getMessage());
                //AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");
                Toast.makeText(getApplicationContext(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();
            }
        };

        String url = NetworkURL.BSATTACHMENT + "?"
                + "audit_id=" + auditId + "&"
                + "section_group_id=" + sectionGroupId + "&"
                + "section_id=" + sectionId + "&"
                + "question_id=" + questionId;
        GetReportRequest getReportRequest = new GetReportRequest(AppPreferences.INSTANCE.getAccessToken(context),
                url, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(getReportRequest);
    }

    public void getDsAttachmentList() {
        //showAppProgressDialog();
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "GetAttachmentResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AddAttachmentRootObject addAttachmentRootObject = new GsonBuilder().create()
                                .fromJson(object.toString(), AddAttachmentRootObject.class);
                        if (addAttachmentRootObject.getData() != null &&
                                addAttachmentRootObject.getData().toString().length() > 0) {
                            setAttachmentList(addAttachmentRootObject.getData());
                            int size = addAttachmentRootObject.getData().size();
                            attachmentCount = "" + size;
                            tvAttachmentCount.setVisibility(View.VISIBLE);
                            tvAttachmentCount.setText("" + size + "/20 Uploaded");
                            if (size >= 20) {
                                addAttachmentLayout.setVisibility(View.GONE);
                            } else {
                                addAttachmentLayout.setVisibility(View.VISIBLE);
                            }
                            //brandStandardAuditAdapter.notifyDataSetChanged();
                        } else {
                            tvAttachmentCount.setVisibility(View.VISIBLE);
                        }
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context,
                                object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //hideProgressDialog();
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hideProgressDialog();
                AppLogger.e(TAG, "GetAttachmentError: " + error.getMessage());
                //AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");
                Toast.makeText(getApplicationContext(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();

            }
        };

        String url = NetworkURL.DSATTACHMENT + "?"
                + "audit_id=" + auditId + "&"
                + "section_group_id=" + sectionGroupId + "&"
                + "section_id=" + sectionId;
        GetReportRequest getReportRequest = new GetReportRequest(AppPreferences.INSTANCE.getAccessToken(context),
                url, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(getReportRequest);
    }

    public void getEsAttachmentList() {
        //showAppProgressDialog();
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "GetAttachmentResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AddAttachmentRootObject addAttachmentRootObject = new GsonBuilder().create()
                                .fromJson(object.toString(), AddAttachmentRootObject.class);
                        if (addAttachmentRootObject.getData() != null &&
                                addAttachmentRootObject.getData().toString().length() > 0) {
                            setAttachmentList(addAttachmentRootObject.getData());
                            int size = addAttachmentRootObject.getData().size();
                            attachmentCount = "" + size;
                            tvAttachmentCount.setVisibility(View.VISIBLE);
                            tvAttachmentCount.setText("" + size + "/20 Uploaded");
                            if (size >= 20) {
                                addAttachmentLayout.setVisibility(View.GONE);
                            } else {
                                addAttachmentLayout.setVisibility(View.VISIBLE);
                            }
                            //brandStandardAuditAdapter.notifyDataSetChanged();
                        } else {
                            tvAttachmentCount.setVisibility(View.VISIBLE);
                        }
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context,
                                object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //hideProgressDialog();
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hideProgressDialog();
                mProgressLayoutLL.setVisibility(View.GONE);
                AppLogger.e(TAG, "GetAttachmentError: " + error.getMessage());
                //AppUtils.toast((BaseActivity) context,"Server temporary unavailable, Please try again");
                Toast.makeText(getApplicationContext(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();

            }
        };

        String url = NetworkURL.ESATTACHMENT + "?"
                + "audit_id=" + auditId;
        GetReportRequest getReportRequest = new GetReportRequest(AppPreferences.INSTANCE.getAccessToken(context), url, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(getReportRequest);
    }

    private void addBsFileAttachment(byte[] imageByteData, String description, String type) {
        //showProgressDialog();
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "AddAttachmentResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        Toast.makeText(getApplicationContext(), " Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        getBsAttachmentList();
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // hideProgressDialog();
                mProgressLayoutLL.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  hideProgressDialog();
                mProgressLayoutLL.setVisibility(View.GONE);
                AppLogger.e(TAG, "AddAttachmentError: " + error.getMessage());
                //AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");
                Toast.makeText(getApplicationContext(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();

            }
        };

        String url = NetworkURL.BSATTACHMENT;
        String fileName = "Oditly-" + date;
        AddBSAttachmentRequest addBSAttachmentRequest = new AddBSAttachmentRequest(
                AppPreferences.INSTANCE.getAccessToken(context), url, fileName, imageByteData, auditId,
                sectionGroupId, sectionId, description, "0", latitude, longitude,type,
                stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
    }

    private void addQuestionFileAttachment(byte[] imageByteData, String description, String type) {
        //showProgressDialog();
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "AddAttachmentResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        Toast.makeText(getApplicationContext(), " Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        getQuestionAttachmentList();
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context,
                                object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // hideProgressDialog();
                mProgressLayoutLL.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // hideProgressDialog();
                mProgressLayoutLL.setVisibility(View.GONE);
                AppLogger.e(TAG, "AddAttachmentError: " + error.getMessage());
                //AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");
                Toast.makeText(getApplicationContext(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();

            }
        };

        String url = NetworkURL.BSATTACHMENT;
        String fileName = "Oditly-" + date ;
        AddQuestionAttachmentRequest addBSAttachmentRequest = new AddQuestionAttachmentRequest(
                AppPreferences.INSTANCE.getAccessToken(context), url, fileName, imageByteData, auditId,
                sectionGroupId, sectionId, questionId, description, "0", latitude, longitude,type, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
    }

    private void addDsFileAttachment(byte[] imageByteData, String description, String type) {
        //showProgressDialog();
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "AddAttachmentResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        Toast.makeText(getApplicationContext(), " Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        getDsAttachmentList();
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // hideProgressDialog();
                mProgressLayoutLL.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // hideProgressDialog();
                mProgressLayoutLL.setVisibility(View.GONE);
                AppLogger.e(TAG, "AddAttachmentError: " + error.getMessage());
                //AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");
                Toast.makeText(getApplicationContext(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();

            }
        };

        String url = NetworkURL.DSATTACHMENT;
        String fileName = "Oditly-" + date;
        AddDSAttachmentRequest addBSAttachmentRequest = new AddDSAttachmentRequest(
                AppPreferences.INSTANCE.getAccessToken(context), url, fileName, imageByteData, auditId,
                sectionGroupId, sectionId, description, latitude, longitude,type, stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
    }

    private void addEsFileAttachment(byte[] imageByteData, String description, String type) {
        //showProgressDialog();
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "AddAttachmentResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        Toast.makeText(getApplicationContext(), " Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        getEsAttachmentList();
                    } else if (object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast((BaseActivity) context, object.getString(AppConstant.RES_KEY_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  hideProgressDialog();
                mProgressLayoutLL.setVisibility(View.GONE);
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  hideProgressDialog();
                mProgressLayoutLL.setVisibility(View.GONE);
                AppLogger.e(TAG, "AddAttachmentError: " + error.getMessage());
                //AppUtils.toast((BaseActivity) context, "Server temporary unavailable, Please try again");
                Toast.makeText(getApplicationContext(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();

            }
        };

        String url = NetworkURL.ESATTACHMENT;
        String fileName = "Oditly-" + date;
        AddESAttachmentRequest addBSAttachmentRequest = new AddESAttachmentRequest(
                AppPreferences.INSTANCE.getAccessToken(context), url, fileName, imageByteData, auditId,
                description, latitude, longitude, type,stringListener, errorListener);
        VolleyNetworkRequest.getInstance(context).addToRequestQueue(addBSAttachmentRequest);
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
            Uri uri = imageURI.get(position);
            Bitmap bitmap = null;

            try {
                //bitmap = decodeSampledBitmapFromResource(uri.getPath(),100,100);

                //bitmap = convertToMutable(MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri));
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                // bitmap= ImageUtils.resize(bitmap,800,600);
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);


                if (bitmap != null) {
                    Canvas canvas = new Canvas(bitmap);
/*
                    String s=AppUtils.getCurrentDate();
                    Paint mpaint = new Paint();
                    Paint paint2 = new Paint();
                    mpaint= new Paint();
                    mpaint.setColor(Color.BLACK);
                    mpaint.setStyle(Paint.Style.FILL);
                    paint2= new Paint();
                    paint2.setColor(Color.WHITE);
                    paint2.setTextSize(50);  //set text size
                    float w = paint2.measureText(s)/2;
                    float textSize = paint2.getTextSize();

                    paint2.setTextAlign(Paint.Align.CENTER);
                    canvas.drawRect(bitmap.getWidth()-w, bitmap.getWidth() - textSize, bitmap.getWidth() + w, bitmap.getWidth(), mpaint);
                    canvas.drawText(s, bitmap.getWidth(), bitmap.getWidth() ,paint2); //x=300,y=300

                    Log.e("IMAGE GET WIDTH==> ",""+bitmap.getWidth());*/


                    Paint paint = new Paint();
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(getResources().getColor(android.R.color.white)); // Text Color
                    //paint.setTextSize(50); //Text Size
                    final float testTextSize = 50f;

                    // Get the bounds of the text, using our testTextSize.
                    paint.setTextSize(testTextSize);
                   /* Rect bounds = new Rect();
                    paint.getTextBounds(AppUtils.getCurrentDate(), 0, AppUtils.getCurrentDate().length(), bounds);
                    // Calculate the desired size as a proportion of our testTextSize.
                    float desiredTextSize = testTextSize * 600 / bounds.width();*/
                    // Set the paint for that size.
                    // paint.setTextSize(desiredTextSize);
                    canvas.drawText(AppUtils.getCurrentDate(), 20, bitmap.getHeight()-100, paint);
                    //canvas.drawText("Created by AndroidClarified",10,10,0,0,paint);
                    holder.imageView.setImageBitmap(bitmap);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                    imageByteData = byteArrayOutputStream.toByteArray();

                }


            } catch (Exception e) {
                AppUtils.toast((AddAttachmentActivity)context,"Some technical error. Please try again.");
                e.printStackTrace();
            }

            holder.description.setText("");

            holder.submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mImageCounter++;
                    if (imageURI.size() == 1) {
                        AppUtils.hideKeyboard(context, view);
                    }
                    String text = "";
                    if (holder.description.getText().toString().length() > 0) {
                        text = holder.description.getText().toString();
                    }
                    switch (attachType) {
                        case "bsSection":
                            addBsFileAttachment(imageByteData, text,"image");
                            imageURI.remove(position);
                            int size = imageURI.size();
                            if (size > 0) {
                                AppUtils.toast((AddAttachmentActivity) context, "" + size + " images left");
                            }
                            notifyDataSetChanged();
                            break;
                        case "bsQuestion":
                            addQuestionFileAttachment(imageByteData, text,"image");
                            imageURI.remove(position);
                            int bsSize = imageURI.size();
                            if (bsSize > 0) {
                                AppUtils.toast((AddAttachmentActivity) context, "" + bsSize + " images left");
                            }
                            notifyDataSetChanged();
                            break;
                        case "dsSection":
                            addDsFileAttachment(imageByteData, text,"image");
                            imageURI.remove(position);
                            int dsSize = imageURI.size();
                            if (dsSize > 0) {
                                AppUtils.toast((AddAttachmentActivity) context, "" + dsSize + " images left");
                            }
                            notifyDataSetChanged();
                            break;
                        case "esSection":
                            addEsFileAttachment(imageByteData, text,"image");
                            imageURI.remove(position);
                            int esSize = imageURI.size();
                            if (esSize > 0) {
                                AppUtils.toast((AddAttachmentActivity) context, "" + esSize + " images left");
                            }
                            notifyDataSetChanged();
                            break;
                    }

                    if(imageURI.size()==0){
                        customDialog.dismiss();
                        Intent result = new Intent();
                        result.putExtra("attachmentCount", attachmentCount);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                }
            });
            holder.cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listOfImageString.remove(mImageCounter);
                    if (imageURI.size() <= 1) {
                        Intent result = new Intent();
                        result.putExtra("attachmentCount", attachmentCount);
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

            final Bitmap finalBitmap = bitmap;
            holder.editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EDIT_IMAGE_POS = position;
                    Intent intent = new Intent(context, EditImageActivity.class);
                    intent.putExtra("bitmap", imageURI.get(position).toString());
                    startActivityForResult(intent,EDIT_IMAGE);
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









}
