/*
 * This file is part of Breezy Weather.
 *
 * Breezy Weather is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, version 3 of the License.
 *
 * Breezy Weather is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Breezy Weather. If not, see <https://www.gnu.org/licenses/>.
 */

package org.breezyweather.background.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import org.breezyweather.background.weather.WeatherUpdateJob
import org.breezyweather.common.extensions.cancelNotification
import org.breezyweather.common.extensions.notificationManager
import org.breezyweather.BuildConfig.APPLICATION_ID as ID

/*
 * Taken partially from Mihon
 * License Apache, Version 2.0
 * https://github.com/mihonapp/mihon/blob/aa498360db90350f2642e6320dc55e7d474df1fd/app/src/main/java/eu/kanade/tachiyomi/data/notification/NotificationReceiver.kt
 */

/**
 * Global [BroadcastReceiver] that runs on UI thread
 * Pending Broadcasts should be made from here.
 * NOTE: Use local broadcasts if possible.
 */
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            // Cancel weather update and dismiss notification
            ACTION_CANCEL_WEATHER_UPDATE -> cancelWeatherUpdate(context)
            // Download app update
            ACTION_DOWNLOAD_APP_UPDATE -> downloadAppUpdate(context, intent)
        }
    }

    /**
     * Dismiss the notification
     *
     * @param notificationId the id of the notification
     */
    private fun dismissNotification(context: Context, notificationId: Int) {
        context.cancelNotification(notificationId)
    }

    /**
     * Method called when user wants to stop a weather update
     *
     * @param context context of application
     */
    private fun cancelWeatherUpdate(context: Context) {
        WeatherUpdateJob.stop(context)
    }

    /**
     * Method called when user wants to download an app update
     *
     * @param context context of application
     * @param intent intent containing download URL
     */
    private fun downloadAppUpdate(context: Context, intent: Intent) {
        val url = intent.getStringExtra(EXTRA_DOWNLOAD_URL) ?: return
        val version = intent.getStringExtra(EXTRA_VERSION) ?: "update"

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
        val downloadUri = Uri.parse(url)

        val request = android.app.DownloadManager.Request(downloadUri).apply {
            setTitle("BrainOps Weather $version")
            setDescription("Downloading update...")
            setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, "BrainOps-Weather-$version.apk")
            setMimeType("application/vnd.android.package-archive")
        }

        downloadManager.enqueue(request)
    }

    companion object {
        private const val NAME = "NotificationReceiver"

        private const val ACTION_CANCEL_WEATHER_UPDATE = "$ID.$NAME.CANCEL_WEATHER_UPDATE"
        private const val ACTION_DOWNLOAD_APP_UPDATE = "$ID.$NAME.DOWNLOAD_APP_UPDATE"
        private const val EXTRA_DOWNLOAD_URL = "$ID.$NAME.DOWNLOAD_URL"
        private const val EXTRA_VERSION = "$ID.$NAME.VERSION"

        /**
         * Returns [PendingIntent] that starts a service which stops the weather update
         *
         * @param context context of application
         * @return [PendingIntent]
         */
        internal fun cancelWeatherUpdatePendingBroadcast(context: Context): PendingIntent {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = ACTION_CANCEL_WEATHER_UPDATE
            }
            return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        /**
         * Returns [PendingIntent] that starts a service which dismissed the notification
         *
         * @param context context of application
         * @param notificationId id of notification
         * @return [PendingIntent]
         */
        internal fun dismissNotification(context: Context, notificationId: Int, groupId: Int? = null) {
            /*
            Group notifications always have at least 2 notifications:
            - Group summary notification
            - Single manga notification

            If the single notification is dismissed by the system, ie by a user swipe or tapping on the notification,
            it will auto dismiss the group notification if there's no other single updates.

            When programmatically dismissing this notification, the group notification is not automatically dismissed.
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val groupKey = context.notificationManager.activeNotifications.find {
                    it.id == notificationId
                }?.groupKey

                if (groupId != null && groupId != 0 && !groupKey.isNullOrEmpty()) {
                    val notifications = context.notificationManager.activeNotifications.filter {
                        it.groupKey == groupKey
                    }

                    if (notifications.size == 2) {
                        context.cancelNotification(groupId)
                        return
                    }
                }
            }

            context.cancelNotification(notificationId)
        }

        /**
         * Returns [PendingIntent] that starts downloading an app update
         *
         * @param context context of application
         * @param url download URL of the APK
         * @param version version string
         * @return [PendingIntent]
         */
        internal fun downloadAppUpdatePendingBroadcast(context: Context, url: String, version: String): PendingIntent {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = ACTION_DOWNLOAD_APP_UPDATE
                putExtra(EXTRA_DOWNLOAD_URL, url)
                putExtra(EXTRA_VERSION, version)
            }
            return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        /**
         * Returns [PendingIntent] that opens the error log file in an external viewer
         *
         * @param context context of application
         * @param uri uri of error log file
         * @return [PendingIntent]
         */
        internal fun openErrorLogPendingActivity(context: Context, uri: Uri): PendingIntent {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                setDataAndType(uri, "text/plain")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
