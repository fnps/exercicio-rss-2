package br.ufpe.cin.if710.rss

import android.content.Context
import android.database.Cursor
import android.database.SQLException

class ItemRSSHelper(val context: Context) {

    fun getItems(): Cursor {
        val helper = SQLiteHelper(context)
        val db = helper.readableDatabase
        return db.query(TABLE, null, "$USED = ?", arrayOf("FALSE"), null, null, null)
    }

    fun markAsRead(link: String): Boolean {
        return try {
            val helper = SQLiteHelper(context)
            val db = helper.writableDatabase
            val str = "UPDATE $TABLE SET $USED = ? WHERE $LINK = ?"
            db.apply {
                beginTransaction()
                compileStatement(str).apply {
                    clearBindings()
                    bindAllArgsAsStrings(arrayOf("TRUE", link))
                    executeInsert()
                }
                setTransactionSuccessful()
                endTransaction()
                close()
            }
            helper.close()
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun markAsUnread(link: String): Boolean {
        return try {
            val helper = SQLiteHelper(context)
            val db = helper.writableDatabase
            val str = "UPDATE $TABLE SET $USED = ? WHERE $LINK = ?"
            db.apply {
                beginTransaction()
                compileStatement(str).apply {
                    clearBindings()
                    bindAllArgsAsStrings(arrayOf("FALSE", link))
                    executeInsert()
                }
                setTransactionSuccessful()
                endTransaction()
                close()
            }
            helper.close()
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun insertItem(itemRSS: ItemRSS): Boolean {
        return insertItem(itemRSS.title, itemRSS.link, itemRSS.pubDate, itemRSS.description)
    }

    private fun insertItem(title: String, link: String, pubDate: String, description: String): Boolean {
        return try {
            val helper = SQLiteHelper(context)
            if (ItemRSSHelper(context).getItem(link) == null) {
                val str = "INSERT INTO $TABLE ($TITLE, $LINK, $DATE, $DESC, $USED) VALUES (?, ?, ?, ?, ?)"
                helper.writableDatabase.apply {
                    beginTransaction()
                    compileStatement(str).apply {
                        clearBindings()
                        bindAllArgsAsStrings(arrayOf("$title", "$link", "$pubDate", "$description", "FALSE"))
                        executeInsert()
                    }
                    setTransactionSuccessful()
                    endTransaction()
                    close()
                }
                helper.close()
                true
            } else {
                false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun getItem(link: String): ItemRSS? {
        return try {
            val helper = SQLiteHelper(context)
            val db = helper.readableDatabase
            val cursor = db.query(TABLE, null, "$LINK = ?", arrayOf("$link"), null, null, null)
            cursor.count
            if (cursor == null || !cursor.moveToFirst()) {
                null
            } else {
                ItemRSS(cursor.getString(cursor.getColumnIndex(TITLE)), cursor.getString(cursor.getColumnIndex(LINK)), cursor.getString(cursor.getColumnIndex(DATE)), cursor.getString(cursor.getColumnIndex(DESC)))
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    fun remove(link: String): Boolean {
        return try {
            val helper = SQLiteHelper(context)
            val db = helper.writableDatabase
            val str = "DELETE FROM $TABLE WHERE $LINK = ?"
            db.apply {
                beginTransaction()
                compileStatement(str).apply {
                    clearBindings()
                    bindAllArgsAsStrings(arrayOf(link))
                    executeUpdateDelete()
                }
                setTransactionSuccessful()
                endTransaction()
                close()
            }
            helper.close()
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun clean(): Boolean {
        return try {
            val helper = SQLiteHelper(context)
            helper.writableDatabase.delete(TABLE, null, null)
            helper.close()
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        private const val TABLE = SQLiteHelper.TABLE
        private const val ID = SQLiteHelper.ID
        private const val TITLE = SQLiteHelper.TITLE
        private const val LINK = SQLiteHelper.LINK
        private const val DATE = SQLiteHelper.DATE
        private const val DESC = SQLiteHelper.DESC
        private const val USED = SQLiteHelper.USED
    }
}