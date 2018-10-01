package br.ufpe.cin.if710.rss

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class downloadReceiver(lista: SortedList<ItemRSS>, db: SQLiteRSSHelper) : BroadcastReceiver() {

    private val rv = lista
    private val database = db

    override fun onReceive(p0: Context?, p1: Intent?) {
        doAsync {
            val cItems = database.getItems()
            uiThread {
                if (cItems != null && cItems!!.moveToFirst()) {
                    do {
                        rv.add(ItemRSS(cItems.getString(cItems.getColumnIndex(RssProviderContract.TITLE)),
                                cItems.getString(cItems.getColumnIndex(RssProviderContract.LINK)),
                                cItems.getString(cItems.getColumnIndex(RssProviderContract.DATE)),
                                cItems.getString(cItems.getColumnIndex(RssProviderContract.DESCRIPTION))))
                    } while (cItems.moveToNext())
                }
                else{
                    Toast.makeText(p0,"NADA RECEBIDO",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
