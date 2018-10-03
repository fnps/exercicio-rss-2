package br.ufpe.cin.if710.rss

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        doAsync {
            val notificationBuilder = NotificationCompat.Builder(context, context.getString(R.string.channel_id)).apply {
                setAutoCancel(true)
                setSmallIcon(R.drawable.notification_icon_background)
                setContentTitle(context.getString(R.string.rss_notification_title))
                setContentText(context.getString(R.string.rss_notification_text))
                priority = NotificationCompat.PRIORITY_DEFAULT
                setContentIntent(PendingIntent.getActivity(
                        context,
                        0,
                        Intent(context, MainActivity::class.java).apply { Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK },
                        0
                ))
            }
            uiThread {
                with(NotificationManagerCompat.from(context)) {
                    notify(R.integer.rss_notification_id, notificationBuilder.build())
                }

            }
        }

    }
}
