package br.ufpe.cin.if710.rss

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.util.SortedList
import android.support.v7.util.SortedList.INVALID_POSITION
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DownloadReceiver(private val lista: SortedList<ItemRSS>) : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        doAsync {
            val cItems = ItemRSSHelper(p0!!).getItems()
            uiThread {
                Toast.makeText(p0, cItems?.count.toString(),Toast.LENGTH_SHORT).show()
                if (cItems != null && cItems!!.moveToFirst()) {
                    do {
                        val umItem = ItemRSS(
                                cItems.getString(cItems.getColumnIndex(SQLiteHelper.TITLE)),
                                cItems.getString(cItems.getColumnIndex(SQLiteHelper.LINK)),
                                cItems.getString(cItems.getColumnIndex(SQLiteHelper.DATE)),
                                cItems.getString(cItems.getColumnIndex(SQLiteHelper.DESC))
                        )
                        if (INVALID_POSITION == lista.indexOf(umItem)) {
                            lista.add(umItem)
                        }
                    } while (cItems.moveToNext())
                }
                else{
                    Toast.makeText(p0,"NADA RECEBIDO",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
