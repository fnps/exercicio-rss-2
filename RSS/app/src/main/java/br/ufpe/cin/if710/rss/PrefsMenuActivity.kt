package br.ufpe.cin.if710.rss

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment

class PrefsMenuActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prefs_menu)
    }

    class RssPreferenceFragment : PreferenceFragment(){
        private var mListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
        private var mRssUrlPreference: Preference? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Carrega preferences a partir de um XML
            addPreferencesFromResource(R.xml.preferencias)


            // pega a Preference especifica do username
            mRssUrlPreference = preferenceManager.findPreference(MainActivity.rssfeed)
            // Define um listener para atualizar descricao ao modificar preferences
            mListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                mRssUrlPreference!!.summary = sharedPreferences.getString(
                        MainActivity.rssfeed,
                        "Entre com um endereço url válido!"
                )
            }
            // Pega objeto SharedPreferences gerenciado pelo PreferenceManager para este Fragmento
            val prefs = preferenceManager.sharedPreferences
            // Registra listener no objeto SharedPreferences
            prefs.registerOnSharedPreferenceChangeListener(mListener)
            // Invoca callback manualmente para exibir username atual
            mListener!!.onSharedPreferenceChanged(prefs, MainActivity.rssfeed)

        }
    }
}