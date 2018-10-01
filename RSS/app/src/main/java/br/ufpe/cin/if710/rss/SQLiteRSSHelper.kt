package br.ufpe.cin.if710.rss

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class SQLiteRSSHelper constructor(//alternativa
        internal var c: Context) : SQLiteOpenHelper(c, DATABASE_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        //Executa o comando de criação de tabela
        db.execSQL(CREATE_DB_COMMAND)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //estamos ignorando esta possibilidade no momento
        throw RuntimeException("nao se aplica")
    }

    //IMPLEMENTAR ABAIXO
    //Implemente a manipulação de dados nos métodos auxiliares para não ficar criando consultas manualmente
    fun insertItem(item: ItemRSS): Long {
        return insertItem(item.title, item.pubDate, item.description, item.link)
    }

    fun insertItem(title: String, pubDate: String, description: String, link: String): Long {
        doAsync {
            val contentValues = ContentValues()
            contentValues.apply {
                put(ITEM_TITLE, title)
                put(ITEM_DATE, pubDate)
                put(ITEM_DESC, description)
                put(ITEM_LINK, link)
                put(ITEM_UNREAD, "true")
            }
            getInstance(c).writableDatabase.insert(DATABASE_TABLE, null, contentValues)
        }
        return 0
    }

    @Throws(SQLException::class)
    fun getItemRSS(link: String): ItemRSS {
        var item: ItemRSS ?= null
        doAsync {
            val query = getInstance(c).readableDatabase.query(
                    DATABASE_TABLE,
                    RssProviderContract.ALL_COLUMNS,
                    "$ITEM_LINK = ? AND $ITEM_UNREAD = ?",
                    arrayOf(link, "true"),
                    null,
                    null,
                    null
            )
            uiThread {
                item = ItemRSS(query.getString(query.getColumnIndex(ITEM_TITLE)), query.getString(query.getColumnIndex(ITEM_LINK)), query.getString(query.getColumnIndex(ITEM_DATE)), query.getString(query.getColumnIndex(ITEM_DESC)))
            }
        }

        return item!!
    }


    @Throws(SQLException::class)
    fun getItems(): Cursor? {
        var itens : Cursor? = null
        val bool = true
        doAsync {
            val result = getInstance(c).readableDatabase.query(
                    DATABASE_TABLE,
                    columns,
                    "$ITEM_UNREAD = ?",
                    arrayOf(bool.toString()),
                    null,
                    null,
                    null
            )
            itens = if (result != null && result.moveToFirst()){
                result
            } else{
                Toast.makeText(c, result.count, Toast.LENGTH_SHORT).show()
                null
            }
        }
        return itens
    }

    fun markAsUnread(link: String): Boolean {
        doAsync {
            val cv = ContentValues()
            cv.put(ITEM_UNREAD, "true")
            getInstance(c).writableDatabase.update(DATABASE_TABLE,
                    cv,
                    "$ITEM_LINK = ?",
                    arrayOf(link))
        }
        return true
    }

    fun markAsRead(link: String): Boolean {
        doAsync {
            val cv = ContentValues()
            cv.put(ITEM_UNREAD, "false")
            getInstance(c).writableDatabase.update(DATABASE_TABLE,
                    cv,
                    "$ITEM_LINK = ?",
                    arrayOf(link))
        }
        return true
    }

    companion object {
        //Nome do Banco de Dados
        private val DATABASE_NAME = "rss"
        //Nome da tabela do Banco a ser usada
        val DATABASE_TABLE = "items"
        //Versão atual do banco
        private val DB_VERSION = 1

        private var db: SQLiteRSSHelper? = null

        //Definindo Singleton
        fun getInstance(c: Context): SQLiteRSSHelper {
            if (db == null) {
                db = SQLiteRSSHelper(c.applicationContext)
            }
            return db as SQLiteRSSHelper
        }

        //Definindo constantes que representam os campos do banco de dados
        val ITEM_ROWID = RssProviderContract._ID
        val ITEM_TITLE = RssProviderContract.TITLE
        val ITEM_DATE = RssProviderContract.DATE
        val ITEM_DESC = RssProviderContract.DESCRIPTION
        val ITEM_LINK = RssProviderContract.LINK
        val ITEM_UNREAD = RssProviderContract.UNREAD

        //Definindo constante que representa um array com todos os campos
        val columns = arrayOf(ITEM_ROWID, ITEM_TITLE, ITEM_DATE, ITEM_DESC, ITEM_LINK, ITEM_UNREAD)

        //Definindo constante que representa o comando de criação da tabela no banco de dados
        private val CREATE_DB_COMMAND = "CREATE TABLE " + DATABASE_TABLE + " (" +
                ITEM_ROWID + " integer primary key autoincrement, " +
                ITEM_TITLE + " text not null, " +
                ITEM_DATE + " text not null, " +
                ITEM_DESC + " text not null, " +
                ITEM_LINK + " text not null, " +
                ITEM_UNREAD + " boolean not null);"
    }

}