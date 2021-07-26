package com.oditly.audit.inspection.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadPdfTask {

    private String downloadUrl = "";
    private String downloadFileName = "";
  //  private ProgressDialog progressDialog;
    private Context context;
    private static final String TAG = DownloadPdfTask.class.getSimpleName();
    private PDFDownloadFinishedListner pdfDownloadFinishedListner;
    private int mAuditID=0;
    private String mFirebaseToken;


    public DownloadPdfTask(Context context, String downloadUrl,int auditid,String token, PDFDownloadFinishedListner pdfDownloadFinishedListner) {
        this.context = context;
        this.downloadUrl = downloadUrl;
        mAuditID=auditid;
        this.pdfDownloadFinishedListner = pdfDownloadFinishedListner;
        mFirebaseToken=token;
        downloadFileName = downloadUrl.substring(downloadUrl.lastIndexOf( '/' ),downloadUrl.length());//Create file name by picking download file name from URL
        Log.e(TAG, downloadFileName);

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File pdfStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    pdfDownloadFinishedListner.onPDFDownloadFinished(outputFile.getAbsolutePath());
                } else
                        pdfDownloadFinishedListner.onPDFDownloadFinished("");
            } catch (Exception e) {
                e.printStackTrace();
                pdfDownloadFinishedListner.onPDFDownloadFinished("");
            }
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //String encodedAuth = Base64.encode(AppPrefs.getAccessToken(context).getBytes(),Base64.DEFAULT);
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();//Open Url Connection
                httpURLConnection.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                httpURLConnection.setRequestProperty ("access-token", AppPreferences.INSTANCE.getAccessToken(context));
                httpURLConnection.setRequestProperty (NetworkConstant.REQ_FIREBASE_ACCESS_TOKEN,"Bearer "+mFirebaseToken);

                httpURLConnection.setDoOutput(false);//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());

                }

            if(Build.VERSION.SDK_INT >= 29) {
                String downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
                outputFile = new File(downloadDir, mAuditID + ".pdf");
            }
            else
            {
                pdfStorage = new File(Environment.getExternalStorageDirectory() +File.separator+ AppConstant.ODITLY);
                if (!pdfStorage.exists()) {
                  boolean diectoryCreate = pdfStorage.mkdirs();
                    Log.e(TAG, "Directory Created."+diectoryCreate);
                }
                outputFile = new File(pdfStorage, mAuditID+".pdf");//Create Output file in Main File
                //Create New File if not presentCrea
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }
            }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location
                InputStream is = httpURLConnection.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {
                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }
            return null;
        }
    }

    public interface PDFDownloadFinishedListner {
        void onPDFDownloadFinished(String file);
    }
}
