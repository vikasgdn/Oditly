package com.oditly.audit.inspection.ui.activty

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.volley.Response
import com.android.volley.VolleyError
import com.asksira.bsimagepicker.BSImagePicker.*
import com.bumptech.glide.Glide
import com.creativechintak.multiimagepicker.builder.MultiImagePicker
import com.github.file_picker.ListDirection
import com.github.file_picker.adapter.FilePickerAdapter
import com.github.file_picker.data.model.Media
import com.github.file_picker.extension.showFilePicker
import com.github.file_picker.listener.OnItemClickListener
import com.github.file_picker.listener.OnSubmitClickListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.gson.GsonBuilder
import com.oditly.audit.inspection.OditlyApplication
import com.oditly.audit.inspection.R
import com.oditly.audit.inspection.adapter.AddAttachmentAdapter
import com.oditly.audit.inspection.apppreferences.AppPreferences
import com.oditly.audit.inspection.dialog.CustomDialog
import com.oditly.audit.inspection.localDB.media.MediaDBImpl
import com.oditly.audit.inspection.model.audit.AddAttachment.AddAttachmentInfo
import com.oditly.audit.inspection.model.audit.AddAttachment.AddAttachmentRootObject
import com.oditly.audit.inspection.network.NetworkURL
import com.oditly.audit.inspection.network.apirequest.*
import com.oditly.audit.inspection.ui.activty.AddAttachmentActivity.AddAttachmentListAdapter.AddAttachmentListViewHolder
import com.oditly.audit.inspection.util.*
import com.thorny.photoeasy.PhotoEasy
import `in`.balakrishnan.easycam.CameraBundleBuilder
import `in`.balakrishnan.easycam.CameraControllerActivity
import `in`.balakrishnan.easycam.FileUtils
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddAttachmentActivity() : BaseActivity(), View.OnClickListener, OnSingleImageSelectedListener,
    OnMultiImageSelectedListener, ImageLoaderDelegate {
    @BindView(R.id.tv_header_title)
    var mTitleTV: TextView? = null

    @BindView(R.id.tv_attachment_count)
    var tvAttachmentCount: TextView? = null

    @BindView(R.id.recycler_view_attachment_list)
    var attachmentList: RecyclerView? = null

    @BindView(R.id.add_attachment_layout)
    var addAttachmentLayout: LinearLayout? = null

    @BindView(R.id.floating_btn_add_attachment)
    var addAttachmentBtn: ImageView? = null

    @BindView(R.id.add_attachment_text)
    var add_attachment_text: TextView? = null

    @BindView(R.id.ll_parent_progress)
    var mProgressLayoutLL: RelativeLayout? = null
    private var mLocationUtils: LocationUtils? = null
    private var isVideoPermission = false
    private var isGalleryDisable = 1
    var EDIT_IMAGE_POS = 0
    private var auditId: String? = ""
    private var sectionGroupId: String? = ""
    private var sectionId: String? = ""
    private var questionId: String? = ""
    private var attachType: String? = ""
    private var longitude = ""
    private var latitude = ""
    private var date = ""
    private var attachmentCount = 0
    private var context: Context? = null
    private var customDialog: CustomDialog? = null
    private var imageCustomDialog: CustomDialog? = null
    private var viewPagerAdapter: AddAttachmentListAdapter? = null
    private var mImageCounter = 0
    private var mURIimageList: ArrayList<Uri?>? = null
    private var mMediaDB: MediaDBImpl? = null
    private var mCurrentPhotoPath = ""
    private val photoEasy: PhotoEasy? = null
    private lateinit var mRightImageIV:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_attachment)
        mLocationUtils = LocationUtils(this)
        AppPreferences.INSTANCE.initAppPreferences(this)
        context = this
        ButterKnife.bind(this@AddAttachmentActivity)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                10
            )
        }
        /* else
            mLocationUtils.beginUpdates(this);*/

        //appLocationService = new AppLocationService(context);
        mMediaDB = MediaDBImpl.getInstance(context)
        initView()
        initVar()
    }

    override fun onBackPressed() {
        val result = Intent()
        result.putExtra("attachmentCount", "" + attachmentCount)
        setResult(RESULT_CANCELED, result) //set cancell by vikas
        finish()
    }

    override fun initView() {
        super.initView()
        mTitleTV = findViewById(R.id.tv_header_title) as TextView
        mTitleTV!!.setText(R.string.text_attachment)
        tvAttachmentCount = findViewById(R.id.tv_attachment_count)
        attachmentList = findViewById(R.id.recycler_view_attachment_list)
        addAttachmentLayout = findViewById(R.id.add_attachment_layout)
        add_attachment_text = findViewById(R.id.add_attachment_text)
        addAttachmentBtn = findViewById(R.id.floating_btn_add_attachment) as ImageView
        mProgressLayoutLL = findViewById(R.id.ll_parent_progress)
        findViewById<View>(R.id.iv_header_left).setOnClickListener(this)

        mRightImageIV = findViewById(R.id.iv_header_right)
        mRightImageIV.visibility=View.VISIBLE
        mRightImageIV.setImageResource(R.drawable.baseline_refresh_24)
        mRightImageIV.setOnClickListener(this)

        addAttachmentBtn!!.setOnClickListener(this)
    }

    override fun initVar() {
        super.initVar()
        date = AppUtils.getDate(Calendar.getInstance().time)
        auditId = intent.getStringExtra("auditId")
        attachType = intent.getStringExtra("attachType")
        sectionGroupId = intent.getStringExtra("sectionGroupId")
        sectionId = intent.getStringExtra("sectionId")
        questionId = intent.getStringExtra("questionId")
        isGalleryDisable = intent.getIntExtra(AppConstant.GALLERY_DISABLE, 1)
        mURIimageList = ArrayList()
        getOldMediaAttachmentList(attachType)
        latLong
        addAttachmentBtn!!.performClick() // new changes
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.floating_btn_add_attachment -> openDCRDialog()
            R.id.iv_header_left -> onBackPressed()
            R.id.iv_header_right -> getOldMediaAttachmentList(attachType)
        }
    }

    private val latLong: Unit
        private get() {
            latitude = String.format("%.3f", mLocationUtils!!.latitude)
            longitude = String.format("%.3f", mLocationUtils!!.longitude)
        }

    private fun openDCRDialog() {
        imageCustomDialog = CustomDialog(context, R.layout.upload_image_dailog)
        imageCustomDialog!!.setCancelable(true)
        if (isGalleryDisable == 0) {
            imageCustomDialog!!.findViewById<View>(R.id.tv_gallery).visibility = View.GONE
            imageCustomDialog!!.findViewById<View>(R.id.tv_gallery_vdo).visibility = View.GONE
        }
        imageCustomDialog!!.findViewById<View>(R.id.tv_gallery).setOnClickListener(
            View.OnClickListener {
                isVideoPermission = false
               /* if (ActivityCompat.checkSelfPermission(
                        this@AddAttachmentActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@AddAttachmentActivity,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        AppConstant.GALLERY_PERMISSION_REQUEST
                    )
                } else {*/
                    chooseImagesFromGallery()
               // }
                imageCustomDialog!!.dismiss()
            })
        imageCustomDialog!!.findViewById<View>(R.id.tv_gallery_vdo)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    isVideoPermission = true

                    chooseImagesFromGalleryVDO()

                    imageCustomDialog!!.dismiss()
                }
            })
        imageCustomDialog!!.findViewById<View>(R.id.tv_camera)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    isVideoPermission = false
                    cameraPermission()
                    imageCustomDialog!!.dismiss()
                }
            })
        imageCustomDialog!!.findViewById<View>(R.id.tv_cameravideo)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    isVideoPermission = true
                    cameraPermission()
                    //Intent intent=new Intent(context,AudioRecordActivity.class);
                    //context.startActivity(intent);
                    imageCustomDialog!!.dismiss()
                }
            })
        imageCustomDialog!!.findViewById<View>(R.id.tv_cancel)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    imageCustomDialog!!.dismiss()
                }
            })
        imageCustomDialog!!.show()
    }

    private fun chooseImagesFromGallery() {
        /*   BSImagePicker pickerDialog = new BSImagePicker.Builder(BuildConfig.APPLICATION_ID + ".provider")
                .setMaximumDisplayingImages(5000)
                .isMultiSelect()
                .setTag("")
                .setMinimumMultiSelectCount(1)
                .setMaximumMultiSelectCount(10)
                .build();
        pickerDialog.show(getSupportFragmentManager(), "picker");*/
      /*  val photoPickerIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(photoPickerIntent, AppConstant.REQUEST_TAKE_PHOTO_GALLERY)*/

    /*    MultiImagePicker.with(this) // `this` refers to activity or fragment
            .setSelectionLimit(10)  // The number of max image selection you want from user at a time, MAX is 30
            .open() // This will open image selection activity to select images
*/
        showFilePicker(
            limitItemSelection = 10,
            listDirection = ListDirection.RTL,
            accentColor = ContextCompat.getColor(this@AddAttachmentActivity, R.color.colorAccent),
            titleTextColor = ContextCompat.getColor(this@AddAttachmentActivity, R.color.white),

            onSubmitClickListener = object : OnSubmitClickListener {
                override fun onClick(files: List<Media>) {
                    // Do something here with selected files
                      for (file in files)
                      {
                          mURIimageList?.add(file.file.toUri())
                      }
                    addDescriptionDialog()
                }
            },
            onItemClickListener = object : OnItemClickListener {
                override fun onClick(media: Media, position: Int, adapter: FilePickerAdapter) {
                    if (!media.file.isDirectory) {
                        adapter.setSelected(position)
                    }
                }
            }
        )
    }

    private fun chooseImagesFromGalleryVDO() {

        System.gc()
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, AppConstant.REQUEST_TAKE_VDO)
    }

    private fun setAttachmentList(arrayList: ArrayList<AddAttachmentInfo>) {
        val attachmentInfoArrayList = ArrayList<AddAttachmentInfo>()
        for (i in arrayList.indices) {
            val info = arrayList[i]
            val fileType = info.file_type
            if (fileType.contains("image/") || fileType.contains("video/") || fileType.contains("octet-stream")) {
                attachmentInfoArrayList.add(info)
            }
        }
        val addAttachmentAdapter = AddAttachmentAdapter(
            context,
            attachmentInfoArrayList,
            attachType,
            auditId,
            sectionGroupId,
            sectionId,
            questionId
        )
        attachmentList!!.layoutManager = LinearLayoutManager(context)
        attachmentList!!.adapter = addAttachmentAdapter
    }

    private fun cameraPermission() {
        if (ContextCompat.checkSelfPermission(
                (context)!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@AddAttachmentActivity,
                arrayOf(Manifest.permission.CAMERA),
                AppConstant.REQUEST_FOR_CAMERA
            )
        } else {
            System.gc()
            if (isVideoPermission) takeVideoFromCamera() else {
                takePhotoFromCamera()
            }
        }
    }

    private fun takeVideoFromCamera() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 8)
        //  takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10);
        // if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
        startActivityForResult(takeVideoIntent, AppConstant.REQUEST_TAKE_VDO)
        // }
    }

    private fun takePhotoFromCamera() {
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
        val intent = Intent(this, CameraControllerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(
            "inputData", CameraBundleBuilder()
                .setFullscreenMode(true)
                .setDoneButtonString("Add")
                .setDoneButtonDrawable(R.drawable.circle_color_green)
                .setSinglePhotoMode(true)
                .setMax_photo(1)
                .setManualFocus(true)
                .setPreviewEnableCount(false)
                .setPreviewIconVisiblity(false)
                .setPreviewPageRedirection(false)
                .setEnableDone(false)
                .setClearBucket(true)
                .createCameraBundle()
        )
        startActivityForResult(intent, AppConstant.REQUEST_TAKE_PHOTO)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstant.REQUEST_TAKE_LOCATION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationUtils!!.beginUpdates(this)
            }
            AppConstant.REQUEST_FOR_CAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isVideoPermission) takeVideoFromCamera() else takePhotoFromCamera()
            }
            AppConstant.GALLERY_PERMISSION_REQUEST -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isVideoPermission) chooseImagesFromGalleryVDO() else chooseImagesFromGallery()
            } else AppUtils.toast(this, "Permission Denied")
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                val dataPath = data!!.getStringExtra("camerax")
                // String dataURI= data.getStringExtra("cameraURI");
                // AppLogger.e("Addattachment uri", "  "+dataURI);
                AppLogger.e("Addattachment path", "  $dataPath")
                /*  Uri uri = Uri.fromFile(new File(dataPath));
                AppLogger.e("Addattachment uri", " size "+uri);
                if (uri != null && uri.toString().length()>10) {
                    mURIimageList.add(uri);
                    addDescriptionDialog();
                }*/
                val list = data.getStringArrayExtra("resultData")
                if (list != null && list.size > 0) {
                    for (i in list.indices) mURIimageList!!.add(
                        Uri.fromFile(
                            File(
                                list[i]
                            )
                        )
                    )
                    addDescriptionDialog()
                } else {
                    AppUtils.toast(this@AddAttachmentActivity, "Image Not Attached")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                AppUtils.toast(
                    this@AddAttachmentActivity,
                    "Result Some technical error. Please try again."
                )
            }
        }
        if (requestCode == MultiImagePicker.REQUEST_PICK_MULTI_IMAGES && resultCode == RESULT_OK) {
            val result = MultiImagePicker.Result(data)
            if (result.isSuccess()) {
                val imageListInUri = result.getImageList() // List os selected images as content Uri format
                //You can also request list as absolute filepath instead of Uri as below
                Log.e("IMAGE LIST==> ",""+imageListInUri.size)

            }
        }
        else if (requestCode == AppConstant.REQUEST_TAKE_PHOTO_GALLERY && resultCode == RESULT_OK) {
            val selectedImage = data!!.data
            mURIimageList!!.add(selectedImage)
            addDescriptionDialog()
        } else if (requestCode == AppConstant.EDIT_IMAGE && resultCode == RESULT_OK) {
            try {
                val uri = Uri.fromFile(File(data!!.getStringExtra("path")))
                viewPagerAdapter!!.updateImage(EDIT_IMAGE_POS, uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            if (requestCode == AppConstant.REQUEST_TAKE_VDO && resultCode == RESULT_OK) {
                try {
                    val selectedImageUri = data!!.data
                    if (selectedImageUri != null) {
                        val imageByteData = AppUtils.readBytes(selectedImageUri, context)
                        mProgressLayoutLL!!.visibility = View.VISIBLE
                        uploadMediaFileAttachment(
                            selectedImageUri.toString(),
                            imageByteData,
                            "Description BS Section video",
                            "video"
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private val listOfImageString: MutableList<Uri?> = ArrayList<Uri?>()
    private fun addDescriptionDialog() {
        listOfImageString.addAll((mURIimageList)!!)
        (applicationContext as OditlyApplication).setmAttachImageList(listOfImageString)
        customDialog = CustomDialog(context, R.layout.add_attachment_dailog)
        customDialog!!.setCancelable(false)
        val attach_List_recycler_view =
            customDialog!!.findViewById<RecyclerView>(R.id.rv_image_list)
        viewPagerAdapter = AddAttachmentListAdapter(context, mURIimageList)
        attach_List_recycler_view.addItemDecoration(CirclePagerIndicatorDecoration())
        attach_List_recycler_view.adapter = viewPagerAdapter
        customDialog!!.show()
    }

    override fun onSingleImageSelected(uri: Uri, tag: String) {
        try {
            if (uri != null) {
                mURIimageList!!.add(uri)
                addDescriptionDialog()
            } else {
                AppUtils.toast(this@AddAttachmentActivity, "Image Not Attached$tag")
            }
        } catch (e: Exception) {
            AppUtils.toast(this@AddAttachmentActivity, "Some technical error. Please try again.")
        }
    }

    override fun onMultiImageSelected(uriList: List<Uri>, tag: String) {
        try {
            if (uriList != null) {
                mURIimageList!!.addAll(uriList)
                addDescriptionDialog()
            } else {
                AppUtils.toast(this@AddAttachmentActivity, "Image Not Attached$tag")
            }
        } catch (e: Exception) {
            AppUtils.toast(this@AddAttachmentActivity, "Some technical error. Please try again.")
        }
    }

    override fun loadImage(imageUri: Uri, ivImage: ImageView) {
        Glide.with(this@AddAttachmentActivity).load(imageUri).into(ivImage)
    }

    var url = ""
    fun getOldMediaAttachmentList(attachType: String?) {
        val stringListener: Response.Listener<String> = object : Response.Listener<String> {
            override fun onResponse(response: String) {
                AppLogger.e(TAG, "GetAttachmentResponse: $response")
                try {
                    val `object` = JSONObject(response)
                    if (!`object`.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        val addAttachmentRootObject = GsonBuilder().create()
                            .fromJson(`object`.toString(), AddAttachmentRootObject::class.java)
                        if (addAttachmentRootObject.data != null && addAttachmentRootObject.data.toString().length > 0) {
                            setAttachmentList(addAttachmentRootObject.data)
                            val size = addAttachmentRootObject.data.size
                            attachmentCount = size
                            tvAttachmentCount!!.visibility = View.VISIBLE
                            tvAttachmentCount!!.text = "$size/20 Uploaded"
                            if (size >= 20) {
                                addAttachmentLayout!!.visibility = View.GONE
                            } else {
                                addAttachmentLayout!!.visibility = View.VISIBLE
                            }
                        } else {
                            tvAttachmentCount!!.visibility = View.VISIBLE
                        }
                    } else if (`object`.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        AppUtils.toast(
                            context as BaseActivity?,
                            `object`.getString(AppConstant.RES_KEY_MESSAGE)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        val errorListener: Response.ErrorListener = object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                AppLogger.e(TAG, "GetAttachmentError: " + error.message)
                Toast.makeText(
                    applicationContext,
                    "Server temporary unavailable, Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (attachType.equals("bsSection", ignoreCase = true))
            url = NetworkURL.BS_FILE_UPLOAD_LISTGET_NEW + "?audit_id=" + auditId
        else
            url = NetworkURL.BS_FILE_UPLOAD_LISTGET_NEW + "?audit_id=" + auditId + "&question_id=" + questionId
        if (AppPreferences.INSTANCE.providerName.equals(AppConstant.OKTA, ignoreCase = true)) {
            val getReportRequest = GetReportRequest(
                AppPreferences.INSTANCE.getAccessToken(context),
                AppPreferences.INSTANCE.getOktaToken(context),
                context,
                url,
                stringListener,
                errorListener
            )
            VolleyNetworkRequest.getInstance(context).addToRequestQueue(getReportRequest)
        } else {
            if (FirebaseAuth.getInstance().currentUser != null) {
                FirebaseAuth.getInstance().currentUser!!.getIdToken(true)
                    .addOnCompleteListener(object : OnCompleteListener<GetTokenResult> {
                        override fun onComplete(task: Task<GetTokenResult>) {
                            if (task.isSuccessful) {
                                val token = task.result.token
                                val getReportRequest = GetReportRequest(
                                    AppPreferences.INSTANCE.getAccessToken(context),
                                    token,
                                    context,
                                    url,
                                    stringListener,
                                    errorListener
                                )
                                VolleyNetworkRequest.getInstance(context)
                                    .addToRequestQueue(getReportRequest)
                            }
                        }
                    })
            }
        }
    }

    private fun uploadMediaFileAttachment(
        media: String,
        imageByteData: ByteArray,
        description: String,
        type: String
    ) {
        val stringListener: Response.Listener<String> = object : Response.Listener<String> {
            override fun onResponse(response: String) {
                mMediaDB!!.addMediaToDB(auditId, sectionId, sectionGroupId, questionId, media)
                AppLogger.e(TAG, "AddAttachmentResponse: $response")
                try {
                    val `object` = JSONObject(response)
                    if (!`object`.getBoolean(AppConstant.RES_KEY_ERROR)) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.text_updatedsuccessfully),
                            Toast.LENGTH_SHORT
                        ).show()
                        getOldMediaAttachmentList(attachType)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            `object`.getString(AppConstant.RES_KEY_MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mProgressLayoutLL!!.visibility = View.GONE
            }
        }
        val errorListener: Response.ErrorListener = object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                mProgressLayoutLL!!.visibility = View.GONE
                AppLogger.e(TAG, "AddAttachmentError: " + error.message)
                Toast.makeText(
                    applicationContext,
                    "Server temporary unavailable, Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val fileName = "Oditly-$date"
        if (AppPreferences.INSTANCE.providerName.equals(AppConstant.OKTA, ignoreCase = true)) {
            if (System.currentTimeMillis() < AppPreferences.INSTANCE.getOktaTokenExpireTime(this)) {
                if (attachType.equals("bsQuestion", ignoreCase = true)) {
                    val addBSAttachmentRequest = AddQuestionAttachmentRequest(
                        AppPreferences.INSTANCE.getAccessToken(context),
                        NetworkURL.BS_FILE_UPLOAD_LISTGET_NEW,
                        fileName,
                        imageByteData,
                        auditId,
                        sectionGroupId,
                        sectionId,
                        questionId,
                        description,
                        "0",
                        latitude,
                        longitude,
                        type,
                        AppPreferences.INSTANCE.getOktaToken(context),
                        context,
                        stringListener,
                        errorListener
                    )
                    VolleyNetworkRequest.getInstance(context)
                        .addToRequestQueue(addBSAttachmentRequest)
                } else {
                    val addBSAttachmentRequest = AddBSAttachmentRequest(
                        AppPreferences.INSTANCE.getAccessToken(context),
                        NetworkURL.BS_FILE_UPLOAD_LISTGET_NEW,
                        fileName,
                        imageByteData,
                        auditId,
                        sectionGroupId,
                        sectionId,
                        description,
                        "0",
                        latitude,
                        longitude,
                        type,
                        AppPreferences.INSTANCE.getOktaToken(context),
                        context,
                        stringListener,
                        errorListener
                    )
                    VolleyNetworkRequest.getInstance(context)
                        .addToRequestQueue(addBSAttachmentRequest)
                }
            } else {
                val jsonListener: Response.Listener<JSONObject> =
                    object : Response.Listener<JSONObject> {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        override fun onResponse(response: JSONObject) {
                            AppLogger.e("TAG", " Token SUCCESS Response: $response")
                            AppUtils.parseRefreshTokenRespone(response, this@AddAttachmentActivity)
                            if (attachType.equals("bsQuestion", ignoreCase = true)) {
                                val addBSAttachmentRequest = AddQuestionAttachmentRequest(
                                    AppPreferences.INSTANCE.getAccessToken(context),
                                    NetworkURL.BS_FILE_UPLOAD_LISTGET_NEW,
                                    fileName,
                                    imageByteData,
                                    auditId,
                                    sectionGroupId,
                                    sectionId,
                                    questionId,
                                    description,
                                    "0",
                                    latitude,
                                    longitude,
                                    type,
                                    AppPreferences.INSTANCE.getOktaToken(context),
                                    context,
                                    stringListener,
                                    errorListener
                                )
                                VolleyNetworkRequest.getInstance(context)
                                    .addToRequestQueue(addBSAttachmentRequest)
                            } else {
                                val addBSAttachmentRequest = AddBSAttachmentRequest(
                                    AppPreferences.INSTANCE.getAccessToken(context),
                                    NetworkURL.BS_FILE_UPLOAD_LISTGET_NEW,
                                    fileName,
                                    imageByteData,
                                    auditId,
                                    sectionGroupId,
                                    sectionId,
                                    description,
                                    "0",
                                    latitude,
                                    longitude,
                                    type,
                                    AppPreferences.INSTANCE.getOktaToken(context),
                                    context,
                                    stringListener,
                                    errorListener
                                )
                                VolleyNetworkRequest.getInstance(context)
                                    .addToRequestQueue(addBSAttachmentRequest)
                            }
                        }
                    }
                val errListener: Response.ErrorListener = object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        AppLogger.e("TAG", "ERROR Response: $error")
                    }
                }
                val tokenRequest = OktaTokenRefreshRequest(
                    AppUtils.getTokenJson(this@AddAttachmentActivity),
                    jsonListener,
                    errListener
                )
                VolleyNetworkRequest.getInstance(this@AddAttachmentActivity)
                    .addToRequestQueue(tokenRequest)
            }
        } else {
            if (FirebaseAuth.getInstance().currentUser != null) {
                FirebaseAuth.getInstance().currentUser!!.getIdToken(true)
                    .addOnCompleteListener(object : OnCompleteListener<GetTokenResult> {
                        override fun onComplete(task: Task<GetTokenResult>) {
                            if (task.isSuccessful) {
                                val token = task.result.token
                                if (attachType.equals("bsQuestion", ignoreCase = true)) {
                                    val addBSAttachmentRequest = AddQuestionAttachmentRequest(
                                        AppPreferences.INSTANCE.getAccessToken(context),
                                        NetworkURL.BS_FILE_UPLOAD_LISTGET_NEW,
                                        fileName,
                                        imageByteData,
                                        auditId,
                                        sectionGroupId,
                                        sectionId,
                                        questionId,
                                        description,
                                        "0",
                                        latitude,
                                        longitude,
                                        type,
                                        token,
                                        context,
                                        stringListener,
                                        errorListener
                                    )
                                    VolleyNetworkRequest.getInstance(context)
                                        .addToRequestQueue(addBSAttachmentRequest)
                                } else {
                                    val addBSAttachmentRequest = AddBSAttachmentRequest(
                                        AppPreferences.INSTANCE.getAccessToken(context),
                                        NetworkURL.BS_FILE_UPLOAD_LISTGET_NEW,
                                        fileName,
                                        imageByteData,
                                        auditId,
                                        sectionGroupId,
                                        sectionId,
                                        description,
                                        "0",
                                        latitude,
                                        longitude,
                                        type,
                                        token,
                                        context,
                                        stringListener,
                                        errorListener
                                    )
                                    VolleyNetworkRequest.getInstance(context)
                                        .addToRequestQueue(addBSAttachmentRequest)
                                }
                            }
                        }
                    })
            }
        }
    }

    inner class AddAttachmentListAdapter(
        var context: Context?,
        private val imageURI: ArrayList<Uri?>?
    ) : RecyclerView.Adapter<AddAttachmentListViewHolder>() {
        var imageByteData = ByteArray(0)
        fun updateImage(pos: Int, uri: Uri?) {
            imageURI!![pos] = uri
            notifyItemChanged(pos)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): AddAttachmentListViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_add_attachment, parent, false)
            return AddAttachmentListViewHolder(view)
        }

        override fun onBindViewHolder(holder: AddAttachmentListViewHolder, position: Int) {
            try {
                val bitmapPlain = MediaStore.Images.Media.getBitmap(
                    context!!.contentResolver,
                    mURIimageList!![position]
                )
                if (bitmapPlain != null) {
                    latLong
                    var imageText: String = ""
                    if (!LocationUtils(this@AddAttachmentActivity).hasLocationEnabled()) imageText =
                        "location disabled, " + AppUtils.getCurrentDateImage() else imageText =
                        "" + latitude + "," + longitude + "," + AppUtils.getCurrentDateImage()
                    val bitmap =
                        AppUtils.resizeImage(mURIimageList!![position], bitmapPlain, 1400, 1400)
                    val drawBitmap = AppUtils().drawTextToBitmap(context, bitmap, imageText)
                    holder.imageView.setImageBitmap(drawBitmap)
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    drawBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
                    imageByteData = byteArrayOutputStream.toByteArray()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                AppUtils.toast(
                    context as AddAttachmentActivity?,
                    "Some technical error. Please try again."
                )
            }
            holder.description.setText("")
            holder.submitButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    mImageCounter++
                    if (imageURI!!.size == 1) {
                        AppUtils.hideKeyboard(context, view)
                    }
                    /* String text = "";
                    if (holder.description.getText().toString().length() > 0) {
                        text = holder.description.getText().toString();
                    }*/if (attachType.equals("bsQuestion", ignoreCase = true)) attachmentCount++
                    uploadMediaFileAttachment(
                        mURIimageList!![position].toString(),
                        imageByteData,
                        holder.description.text.toString(),
                        "image"
                    )
                    imageURI.removeAt(position)
                    //  int size = imageURI.size();
                    if (imageURI.size > 0) AppUtils.toast(
                        context as AddAttachmentActivity?,
                        "" + imageURI.size + " images left"
                    )
                    notifyDataSetChanged()
                    if (imageURI.size == 0) {
                        customDialog!!.dismiss()
                        val result = Intent()
                        result.putExtra("attachmentCount", "" + attachmentCount)
                        setResult(RESULT_OK, result)
                        finish()
                    }
                }
            })
            holder.cancelButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    // mImageCounter
                    listOfImageString.removeAt(mImageCounter)
                    if (imageURI!!.size <= 1) {
                        val result = Intent()
                        result.putExtra("attachmentCount", "" + attachmentCount)
                        setResult(RESULT_OK, result)
                        finish()
                        customDialog!!.dismiss()
                    } else {
                        imageURI.removeAt(position)
                        val size = imageURI.size
                        if (size > 0) {
                            AppUtils.toast(context as AddAttachmentActivity?, "$size images left")
                        }
                        notifyDataSetChanged()
                    }
                }
            })
            holder.editImage.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    EDIT_IMAGE_POS = position
                    val intent = Intent(context, EditImageActivity::class.java)
                    intent.putExtra("bitmap", imageURI!![position].toString())
                    startActivityForResult(intent, AppConstant.EDIT_IMAGE)
                }
            })
        }

        override fun getItemCount(): Int {
            return imageURI!!.size
        }

        inner class AddAttachmentListViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var imageView: ImageView
            var description: EditText
            var submitButton: TextView
            var cancelButton: TextView
            var editImage: ImageButton

            init {
                imageView = itemView.findViewById(R.id.iv_attached_image)
                description = itemView.findViewById(R.id.et_description)
                submitButton = itemView.findViewById(R.id.tv_submit_btn)
                cancelButton = itemView.findViewById(R.id.tv_cancel_btn)
                editImage = itemView.findViewById(R.id.edit_image)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.e("Add onStop", ";;;;;onStop")
    }

    override fun onPause() {
        super.onPause()
        Log.e("ADD onPause", ";;;;;onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        FileUtils.clearAllFiles(this, javaClass.name) // for image clear
        Log.e("ADD onDestroy", ";;;;;onDestroy")
    }

    companion object {
        //private AppLocationService appLocationService;
        private val TAG = AddAttachmentActivity::class.java.simpleName
    }
}