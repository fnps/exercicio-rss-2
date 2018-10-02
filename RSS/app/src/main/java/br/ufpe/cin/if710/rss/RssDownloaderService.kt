package br.ufpe.cin.if710.rss

import android.app.IntentService
import android.content.Context
import android.content.Intent
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class RssDownloaderService : IntentService("RssDownloaderService") {

    private val parser = ParserRSS
//    private val myHelper = SQLiteRSSHelper()

    override fun onHandleIntent(linkAddress: Intent?) {
        val str = getRssFeed(linkAddress!!.getStringExtra("uri"))
        val list = parser.parse(str)
        list.forEach {
            ItemRSSHelper(applicationContext).insertItem(ItemRSS(
                    it.title,
                    it.link,
                    it.pubDate,
                    it.description
            ))
        }
        sendBroadcast(Intent(getString(R.string.downloadCompleted)))
    }

    //classe de download usada como a disponibilizada por padrao, possivel implementacao
    // com uso do downloadmanager será feita
    @Throws(IOException::class)
    private fun getRssFeed(feed: String): String {
        var input: InputStream? = null
        var rssFeed = ""
        try {
            val url = URL(feed)
            val conn = url.openConnection() as HttpURLConnection
            input = conn.inputStream
            val out = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var count = 0
            while (count != -1) {
                count = input!!.read(buffer)
                if (count != -1) out.write(buffer, 0, count)
            }
            val response = out.toByteArray()
            rssFeed = String(response, charset("UTF-8"))
        } finally {
            input?.close()
        }
        return rssFeed
    }

}
