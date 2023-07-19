package com.oditly.audit.inspection.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.model.actionData.AddAdHocActionPlan;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NetworkServiceMultipart {
    private String service;
    private INetworkEvent networkEvent;
    private Context mContext;
    private String mMediaType = "image/jpeg";
    private AddAdHocActionPlan mRequestBean;
    private ArrayList<File> mMediaFileList;
    private String mFirebaseToken="";



    public NetworkServiceMultipart(String serviceName, AddAdHocActionPlan bean,ArrayList<File> file,String token, INetworkEvent networkEvent, Context context) {
        service = serviceName;
        this.networkEvent = networkEvent;
        this.mContext = context;
        mRequestBean=bean;
        mMediaFileList=file;
        mFirebaseToken=token;


    }

    public void call(NetworkModel input) {
        new NetworkTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, input);
    }

    private class NetworkTask extends AsyncTask<NetworkModel, Void, String> {
        boolean isError = false;
        String message = "";

        @Override
        protected void onPreExecute() {

            if (networkEvent != null) {
                networkEvent.onNetworkCallInitiated(service);
            }
        }

        @Override
        protected String doInBackground(NetworkModel... networkModels) {
            int responseCode = -1;
            try {

                OkHttpClient client = new OkHttpClient().newBuilder().build();
                final MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                // RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                builder.addFormDataPart("location_id",mRequestBean.getLocation_id());
                builder.addFormDataPart("section_id",mRequestBean.getSection_id());
                builder.addFormDataPart("priority_id",mRequestBean.getPriority_id());
                builder.addFormDataPart("title",mRequestBean.getTitle());
                builder.addFormDataPart("planned_date",mRequestBean.getPlanned_date());
                builder.addFormDataPart("action_details",mRequestBean.getAction_details());
                builder.addFormDataPart("media_count",mRequestBean.getMedia_count());
                builder.addFormDataPart("cc_emails","");
                for (int i=0;i<mRequestBean.getAssigned_user_id().length();i++) {
                    builder.addFormDataPart("assigned_user_id[]", mRequestBean.getAssigned_user_id().get(i).toString());
                }
                for (int j=0;j<mMediaFileList.size();j++) {
                    String fileName="Oditly_"+System.currentTimeMillis()+".jpg";
                    builder.addFormDataPart("files[]",fileName, RequestBody.create(MediaType.parse("application/octet-stream"),mMediaFileList.get(j)));
                }
              //  builder.addFormDataPart("files[]","1.jpg", RequestBody.create(MediaType.parse("application/octet-stream"),mMediaFileList.get(0)));
                //builder.addFormDataPart("files[]","2.jpg", RequestBody.create(MediaType.parse("application/octet-stream"),mMediaFileList.get(1)));
                //  .addFormDataPart("files[]","graphics.jpg", RequestBody.create(MediaType.parse("application/octet-stream"),new File("/home/vikas/Desktop/graphics.jpg")))


                RequestBody body=builder.build();
                Request request = new Request.Builder()
                        .url(NetworkURL.ACTION_PLAN_ADD_NEW)
                        .method("POST", body)
                        .addHeader("access-token", AppPreferences.INSTANCE.getAccessToken(mContext))
                        .addHeader("Authorization","Bearer "+mFirebaseToken)
                        .build();

                Log.e("MULTIPART=====> ",""+NetworkURL.ACTION_PLAN_ADD_NEW);

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    isError = false;
                    return response.body().string();
                } else {
                    isError = true;
                    message = "response error";
                    return responseCode + " " + message;
                }
            } catch (Exception e) {
                isError = true;
                message = e.getMessage();
                e.printStackTrace();
                return responseCode + " " + message;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (networkEvent != null) {
                if (isError) {
                    networkEvent.onNetworkCallError(service, message);
                } else {
                    networkEvent.onNetworkCallCompleted("",service, s);
                }
            }
        }
    }



}
