package com.oditly.audit.inspection.ui.activty;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.network.DownloadPdfTask;
import com.oditly.audit.inspection.network.INetworkEvent;
import com.oditly.audit.inspection.network.NetworkURL;
import com.oditly.audit.inspection.network.apirequest.OktaTokenRefreshRequest;
import com.oditly.audit.inspection.network.apirequest.UpdateQuestionAttachmentRequest;
import com.oditly.audit.inspection.network.apirequest.VolleyNetworkRequest;
import com.oditly.audit.inspection.ui.activty.BaseActivity;
import com.oditly.audit.inspection.photoeditor.EditingToolsAdapter;
import com.oditly.audit.inspection.photoeditor.EmojiBSFragment;
import com.oditly.audit.inspection.photoeditor.FilterListener;
import com.oditly.audit.inspection.photoeditor.FilterViewAdapter;
import com.oditly.audit.inspection.photoeditor.PropertiesBSFragment;
import com.oditly.audit.inspection.photoeditor.StickerBSFragment;
import com.oditly.audit.inspection.photoeditor.TextEditorDialogFragment;
import com.oditly.audit.inspection.photoeditor.ToolType;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;

public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener, View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener, INetworkEvent,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected, FilterListener {
    private static final String TAG = EditImageActivity.class.getSimpleName();
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private TextView mTxtCurrentTool;
    private RecyclerView mRvTools, mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;
    private RelativeLayout mProgressBarRL;
    private String mLocation;
    private String mAuditId,mSectionGroupID,mSectionID,mQuestionFileID,mSectionFileID,mQuestionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_edit_image);
        AppPreferences.INSTANCE.initAppPreferences(this);

        initViews();

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        mProgressBarRL=(RelativeLayout)findViewById(R.id.ll_parent_progress);
        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);


        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

        Intent intent = getIntent();
        if (intent!=null) {
            mLocation = intent.getStringExtra(AppConstant.FROMWHERE) == null ? "" : intent.getStringExtra(AppConstant.FROMWHERE);
            mAuditId= intent.getStringExtra(AppConstant.AUDIT_ID) == null ? "" : intent.getStringExtra(AppConstant.AUDIT_ID);
            mSectionGroupID= intent.getStringExtra(AppConstant.SECTION_GROUPID) == null ? "" : intent.getStringExtra(AppConstant.SECTION_GROUPID);
            mSectionID= intent.getStringExtra(AppConstant.SECTION_ID) == null ? "" : intent.getStringExtra(AppConstant.SECTION_ID);
            mQuestionID= intent.getStringExtra(AppConstant.QUESTION_ID) == null ? "" : intent.getStringExtra(AppConstant.QUESTION_ID);
            mQuestionFileID= intent.getStringExtra(AppConstant.QUESTION_FILEID) == null ? "" : intent.getStringExtra(AppConstant.QUESTION_FILEID);
            mSectionFileID= intent.getStringExtra(AppConstant.SECTION_FILEID) == null ? "" : intent.getStringExtra(AppConstant.SECTION_ID);

        }
        Log.e("","question file id "+mQuestionFileID+"  ||  section file id "+mSectionFileID);
        //Set Image Dynamically
        if (!TextUtils.isEmpty(mLocation) && mLocation.equalsIgnoreCase("EDIT"))
            mPhotoEditorView.getSource().setImageDrawable(EditAttachmentActivity.sDrawable);
        else
            mPhotoEditorView.getSource().setImageURI(Uri.parse(getIntent().getStringExtra("bitmap")));


    }

    private void initViews() {
        ImageView imgUndo;
        ImageView imgRedo;
        ImageView imgCamera;
        ImageView imgGallery;
        Button imgSave;
        ImageView imgClose;

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvFilters = findViewById(R.id.rvFilterView);
        mRootView = findViewById(R.id.rootView);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);

        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);

        imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);

        imgGallery = findViewById(R.id.imgGallery);
        imgGallery.setOnClickListener(this);

        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);

    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(colorCode);

                mPhotoEditor.editText(rootView, inputText, styleBuilder);
                mTxtCurrentTool.setText(R.string.text_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgUndo:
                mPhotoEditor.undo();
                break;

            case R.id.imgRedo:
                mPhotoEditor.redo();
                break;

            case R.id.imgSave:
                if (ActivityCompat.checkSelfPermission(EditImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, AppConstant.GALLERY_PERMISSION_REQUEST);
                } else {
                    saveImage();
                }

                break;

            case R.id.imgClose:
                onBackPressed();
                break;

            case R.id.imgCamera:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;

            case R.id.imgGallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case AppConstant.GALLERY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage();
                }
                else
                    AppUtils.toast(this,"Permission Denied");
                break;
        }
    }
    @SuppressLint("MissingPermission")
    private void saveImage() {
        mProgressBarRL.setVisibility(View.VISIBLE);
        File file = null;
        try {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            file = File.createTempFile(imageFileName,  /* prefix */".jpg",         /* suffix */storageDir      /* directory */);

            //file = File.createTempFile()
            SaveSettings saveSettings = new SaveSettings.Builder()
                    .setClearViewsEnabled(true)
                    .setTransparencyEnabled(true)
                    .build();

            mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String imagePath) {
                    try {
                        if (!TextUtils.isEmpty(mLocation) && mLocation.equalsIgnoreCase("EDIT"))
                        {
                            byte[] byteData = AppUtils.readBytes(Uri.fromFile(new File(imagePath)), EditImageActivity.this);
                            addQuestionFileAttachment(byteData);
                        }
                    }
                    catch (Exception e){e.printStackTrace();}
                    mProgressBarRL.setVisibility(View.GONE);
                    AppUtils.toast(EditImageActivity.this, "Image Saved Successfully");
                    Intent result = new Intent();
                    result.putExtra("path", imagePath);
                    setResult(RESULT_OK, result);
                    finish();
                    //mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    // hideProgressDialog();
                    mProgressBarRL.setVisibility(View.GONE);
                    AppUtils.toast(EditImageActivity.this, "Failed to save Image");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            // hideProgressDialog();
            mProgressBarRL.setVisibility(View.GONE);
            AppUtils.toast(EditImageActivity.this,e.getMessage());
        }

    }

    private void addQuestionFileAttachment(byte[] imageByteData) {
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppLogger.e(TAG, "AddAttachmentResponse: " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.text_updatedsuccessfully), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),object.getString(AppConstant.RES_KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppLogger.e(TAG, "AddAttachmentError: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Server temporary unavailable, Please try again", Toast.LENGTH_SHORT).show();
            }
        };

        String url = NetworkURL.BSATTACHMENT_UPDATE_NEW;
        String fileName = "Oditly-"+AppUtils.getDate(Calendar.getInstance().getTime());

        if (AppPreferences.INSTANCE.getProviderName().equalsIgnoreCase(AppConstant.OKTA))
        {

            if (System.currentTimeMillis()<AppPreferences.INSTANCE.getOktaTokenExpireTime(this))
            {
                UpdateQuestionAttachmentRequest addBSAttachmentRequest = new UpdateQuestionAttachmentRequest(
                        AppPreferences.INSTANCE.getOktaToken(EditImageActivity.this), url, fileName, imageByteData, mAuditId,
                        mSectionGroupID, mSectionID, mQuestionID, mQuestionFileID, EditImageActivity.this, stringListener, errorListener);
                VolleyNetworkRequest.getInstance(EditImageActivity.this).addToRequestQueue(addBSAttachmentRequest);

            }
            else
            {
                Response.Listener<JSONObject> jsonListener = new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        AppLogger.e("TAG", " Token SUCCESS Response: " + response);
                        AppUtils.parseRefreshTokenRespone(response,EditImageActivity.this);
                        UpdateQuestionAttachmentRequest addBSAttachmentRequest = new UpdateQuestionAttachmentRequest(
                                AppPreferences.INSTANCE.getOktaToken(EditImageActivity.this), url, fileName, imageByteData, mAuditId,
                                mSectionGroupID, mSectionID, mQuestionID, mQuestionFileID, EditImageActivity.this, stringListener, errorListener);
                        VolleyNetworkRequest.getInstance(EditImageActivity.this).addToRequestQueue(addBSAttachmentRequest);

                    }
                };
                Response.ErrorListener errListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppLogger.e("TAG", "ERROR Response: " + error);
                    }
                };
                OktaTokenRefreshRequest tokenRequest = new OktaTokenRefreshRequest(AppUtils.getTokenJson(EditImageActivity.this),jsonListener, errListener);
                VolleyNetworkRequest.getInstance(EditImageActivity.this).addToRequestQueue(tokenRequest);
            }

        }
        else {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    String token = task.getResult().getToken();
                                    UpdateQuestionAttachmentRequest addBSAttachmentRequest = new UpdateQuestionAttachmentRequest(
                                            token, url, fileName, imageByteData, mAuditId,
                                            mSectionGroupID, mSectionID, mQuestionID, mQuestionFileID, EditImageActivity.this, stringListener, errorListener);
                                    VolleyNetworkRequest.getInstance(EditImageActivity.this).addToRequestQueue(addBSAttachmentRequest);
                                }
                            }
                        });
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    mPhotoEditor.clearAllViews();
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    mPhotoEditorView.getSource().setImageBitmap(photo);
                    break;
                case PICK_REQUEST:
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.text_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.text_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
        mTxtCurrentTool.setText(R.string.text_brush);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.text_emoji);

    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.text_sticker);
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you want to exit without saving image ?");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();

    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {

        //mPhotoEditor.setFilterEffect(photoFilter)
        if(photoFilter == PhotoFilter.ROTATE){

            float rotate = mPhotoEditorView.getSource().getRotation();
            if(rotate>=360)
                rotate = 0;
            mPhotoEditorView.getSource().setRotation(rotate + 90);
            //mPhotoEditorView.getSource().getImageMatrix().setRotate(rotate+90);
        }else mPhotoEditor.setFilterEffect(photoFilter);

    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case BRUSH:
                mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.text_brush);
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                        styleBuilder.withTextColor(colorCode);

                        mPhotoEditor.addText(inputText, styleBuilder);
                        mTxtCurrentTool.setText(R.string.text_text);
                    }
                });
                break;
            case ERASER:
                mPhotoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.text_eraser);
                break;
            case FILTER:
                mTxtCurrentTool.setText(R.string.text_filter);
                showFilter(true);
                break;
            case EMOJI:
                mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
                break;
            case STICKER:
                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                break;
        }
    }


    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    @Override
    public void onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false);
            mTxtCurrentTool.setText(R.string.app_name);
        } else if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {

    }
}
