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
            val cursor = ItemRSSHelper(context).getItems()
            if (cursor != null && cursor.moveToFirst()) {
                val notificationBuilder = NotificationCompat.Builder(context, context.getString(R.string.channel_id)).apply {
                    setAutoCancel(true)
                    setSmallIcon(R.drawable.notification_bg)
                    setContentTitle(mContext.getString(R.string.rss_notification_title))
                    setContentText(mContext.getString(R.string.rss_notification_text) + " " + cursor.count.toString() + " items não lidos!")
                    priority = NotificationCompat.PRIORITY_DEFAULT
                    setContentIntent(PendingIntent.getActivity(
                            mContext,
                            0,
                            Intent(mContext, MainActivity::class.java),
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
}
