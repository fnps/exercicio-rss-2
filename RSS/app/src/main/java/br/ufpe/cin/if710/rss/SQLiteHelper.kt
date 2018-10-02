package br.ufpe.cin.if710.rss

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, NOME, null, DB_VERSION) {

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE [$TABLE] ([$ID] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, [$TITLE] TEXT NOT NULL, [$LINK] TEXT NOT NULL, [$DATE] TEXT NOT NULL, [$DESC] TEXT NOT NULL, [$USED] BOOLEAN NOT NULL)")
    }

    companion object {
        const val NOME = "items.db"
        const val DB_VERSION = 1
        const val TABLE = "items"
        const val ID = BaseColumns._ID
        const val TITLE = "title"
        const val LINK = "link"
        const val DATE = "pubdate"
        const val DESC = "des"
        const val USED = "used"
    }


}