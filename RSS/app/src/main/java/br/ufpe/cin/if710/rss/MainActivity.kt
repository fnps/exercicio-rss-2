package br.ufpe.cin.if710.rss

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.util.SortedList
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.notificationManager
import java.io.IOException

open class MainActivity : Activity() {

    private var conteudoRSS: RecyclerView? = null
    private var viewAdapter: RssAdapter? = null
    private var sortedList: SortedList<ItemRSS>? = null
    private var receiver: DownloadReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationRSSDownloadedChannel()

        //iniciando lista
        sortedList = SortedList(ItemRSS::class.java, metodosCallback)
        //iniciando adapter
        viewAdapter = RssAdapter(sortedList)
        //iniciando view
        conteudoRSS = RecyclerView(this).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = viewAdapter
        }
        setContentView(conteudoRSS)
        receiver = DownloadReceiver(sortedList!!)
    }

    private fun createNotificationRSSDownloadedChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                    NotificationChannel(
                            getString(R.string.channel_id),
                            getString(R.string.channel_name),
                            NotificationManager.IMPORTANCE_DEFAULT
                    ).apply {
                        description = getString(R.string.channel_description)
                    }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            //Desativando o NotificationReceiver estatico, com a MainActivity aberta não é necessario mostrar notificações
            setNotificationReceiverState(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
            //Registrando o broadcastreceiver dinamico para atualizar o recyclerview
            registerReceiver(receiver, IntentFilter(getString(R.string.downloadCompleted)))
            //iniciando o serviço que efetua o download e povoamento do banco de dados
            startService(
                    Intent(applicationContext, RssDownloaderService::class.java).apply {
                        putExtra("uri", defaultSharedPreferences.getString(MainActivity.rssfeed, getString(R.string.rssfeed)))
                    }
            )

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //Metodo que altera o stado do receiver estatico NotificationReceiver
    private fun setNotificationReceiverState(state: Int) {
        packageManager.apply {
            setComponentEnabledSetting(
                    ComponentName(applicationContext, NotificationReceiver::class.java),
                    state,
                    PackageManager.DONT_KILL_APP
            )
        }
    }

    override fun onPause() {
        setNotificationReceiverState(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        // retirando o registro do receiver dinamico
        unregisterReceiver(receiver)
        //ativando novamente o receiver estatico para exibir notificacao caso o download nao tenha terminado ainda
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item!!.itemId) {
        R.id.action_change_url -> {
            startActivity(Intent(applicationContext, PrefsMenuActivity::class.java))
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    //classe padrao do adapter
    private inner class RssAdapter(private val list: SortedList<ItemRSS>?) : RecyclerView.Adapter<MyHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            return MyHolder(layoutInflater.inflate(R.layout.itemlista, parent, false))
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.bind(list?.get(position))
        }

        override fun getItemCount(): Int {
            return list!!.size()
        }
    }
    ///////////////

    //classe padrao do holder
    private inner class MyHolder(item: View) : RecyclerView.ViewHolder(item), View.OnClickListener {

        var title: TextView? = null
        var pubDate: TextView? = null

        init {

            title = item.findViewById(R.id.item_titulo)
            pubDate = item.findViewById(R.id.item_data)

            title!!.setOnClickListener(this)
        }

        fun bind(p: ItemRSS?) {
            title?.text = p?.title
            pubDate?.text = p?.pubDate
        }

        //quando for realizado um clique em algum elemento da lista o link referente ao elemento
        //é iniciado no navegador
        override fun onClick(v: View) {
            val position = this.adapterPosition
            val str = sortedList?.get(position)?.link
            val link = Uri.parse(str)
            ItemRSSHelper(applicationContext).markAsRead(str!!)
            sortedList?.removeItemAt(position)
            val intent = Intent(Intent.ACTION_VIEW, link)
            when {
                intent.resolveActivity(packageManager) != null -> startActivity(intent)
            }
        }
    }

    //metodos disponibilizados em sala foram usados por conveniencia
    private var metodosCallback: SortedList.Callback<ItemRSS> = object : SortedList.Callback<ItemRSS>() {
        override fun compare(o1: ItemRSS, o2: ItemRSS): Int {
            return o1.title.compareTo(o2.title)
        }

        override fun onInserted(position: Int, count: Int) {
            viewAdapter?.notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            viewAdapter?.notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            viewAdapter?.notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int) {
            viewAdapter?.notifyItemRangeChanged(position, count)
        }

        override fun areContentsTheSame(oldItem: ItemRSS, newItem: ItemRSS): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }

        override fun areItemsTheSame(item1: ItemRSS, item2: ItemRSS): Boolean {
            return compare(item1, item2) == 0
        }
    }

    companion object {
        const val rssfeed = "rss_url"
    }
}
