package br.ufpe.cin.if710.rss

import android.app.IntentService
import android.content.Intent

class ClearDatabaseService : IntentService("ClearDatabaseService") {

    override fun onHandleIntent(intent: Intent?) {
        ItemRSSHelper(applicationContext).clean()
    }
}
