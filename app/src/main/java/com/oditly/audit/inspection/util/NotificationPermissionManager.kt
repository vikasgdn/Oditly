package com.oditly.audit.inspection.util

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.oditly.audit.inspection.ui.activty.MainActivity


class NotificationPermissionManager {
    companion object {
        var permissionsRequired = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        val NOTIFICATION_PERMISSION_CODE = 1000

        @JvmStatic
        fun areNotificationsEnabled(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.areNotificationsEnabled()
            } else {
                true
            }
        }

        @JvmStatic
        fun permissionGranted(context: Context): Boolean {
            return ActivityCompat.checkSelfPermission(context, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
        }

      /*  @JvmStatic
        fun showSettingDialog(activity: Activity) {
            if (activity is MainActivity) {
                val dialog = Dialog(activity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                val bindingObject: DialogNotificationPermissionBinding = DialogNotificationPermissionBinding.inflate(LayoutInflater.from(activity))
                dialog.setContentView(bindingObject.root)
                dialog.setCancelable(false)

                bindingObject.btnDeny.setOnClickListener { dialog.dismiss() }

                bindingObject.btnAllow.setOnClickListener {
                    dialog.dismiss()
                    activity.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.applicationContext.packageName)))
                }

                dialog.show()
            }
        }
*/
        @JvmStatic
        fun showNotificationPermissionRationale(activity: Activity) {
            if (activity is MainActivity) {
                ActivityCompat.requestPermissions(activity, permissionsRequired, NOTIFICATION_PERMISSION_CODE)
            }
        }
    }
}