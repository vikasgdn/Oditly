package com.oditly.audit.inspection.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.ui.activty.MainActivity;

import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private static final String NOTIFICATION_CHANNEL_ID = "100";
    private NotificationManager mNotificationManager;
    @Override
    public void onNewToken(@NonNull String token) {
        //  super.onNewToken(s);
        AppPreferences.INSTANCE.setFCMToken(token);
        Log.e("TAG", "FCM TOKEN : " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e("TAG", "From: " + remoteMessage.getNotification());
       /* {
            "notification": {
            "title": "title",
                    "body": "body",
                    "image": "icon"
        }
        }*/
        // Check if message contains a data payload.
        if (remoteMessage.getNotification()!=null)
        {
            String notification=     remoteMessage.getData().get("notification");
          //  String title=     remoteMessage.getData().get("title");
           // String message=    remoteMessage.getData().get("message");
            Log.d("TAG", "Message data payload: " + remoteMessage.getData());
            //Log.d("TAG", "Message data NOTIFICATION : " + notification);
            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            showNotificationBuilder(getApplicationContext(),remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle(),NOTIFICATION_CHANNEL_ID);

        }
        // {icon=https://api.dev.account.oditly.com/assets/report/image/company-logo1.png, title=Oditly, message=You have an audit assigned to you. ID: 1586. Name: 17th Mar 2021/Vikas}
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("TAG", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    private void showNotificationBuilder(Context context, String msg, String title, String CHANNEL_ID) {

        try {
            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);


            int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
            PendingIntent contentIntent = PendingIntent.getActivity(context, iUniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Bitmap bigPicture = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);


            NotificationCompat.Builder mBuilder;
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
            mBuilder.setColor(context.getResources().getColor(R.color.colorAccent));
            mBuilder.setSmallIcon(R.mipmap.ic_notification_push);
            mBuilder.setLargeIcon(bigPicture);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(msg);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
            mBuilder.setContentIntent(contentIntent);
            mBuilder.setDefaults(Notification.DEFAULT_ALL);
            mBuilder.setAutoCancel(true);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                createNotificationChanel(mBuilder);
            }
            mNotificationManager.notify(iUniqueId, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void createNotificationChanel(NotificationCompat.Builder mBuilder) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }


}
