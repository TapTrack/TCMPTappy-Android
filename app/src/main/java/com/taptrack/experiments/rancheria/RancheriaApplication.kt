package com.taptrack.experiments.rancheria

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatDelegate
import com.f2prateek.rx.preferences2.RxSharedPreferences
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber

class RancheriaApplication: Application() {
    lateinit private var prefs: SharedPreferences
    lateinit private var rxPrefs: RxSharedPreferences

    override fun onCreate() {
        super.onCreate()

        prefs = getSharedPreferences(PREFS_GLOBAL,Context.MODE_PRIVATE)
        rxPrefs = RxSharedPreferences.create(prefs)

        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .schemaVersion(2)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)

        getNightModeEnabled()
                .subscribe {
                    if (it) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
    }

    fun getAutolaunchEnabled() : Observable<Boolean> = rxPrefs.getBoolean(KEY_AUTOLAUNCH,false).asObservable()


    fun setAutolaunchEnabled(shouldLaunch: Boolean) {
        prefs.edit().putBoolean(KEY_AUTOLAUNCH,shouldLaunch).apply()
    }

    fun getHeartbeatEnabled() : Observable<Boolean> = rxPrefs.getBoolean(KEY_HEARTBEAT,false).asObservable()

    fun setHeartbeatEnabled(shouldHeartbeat: Boolean) {
        prefs.edit().putBoolean(KEY_HEARTBEAT,shouldHeartbeat).apply()
    }

    fun getNightModeEnabled() : Observable<Boolean> = rxPrefs.getBoolean(KEY_NIGHT_MODE,true).asObservable()

    fun setNightModeEnabled(shouldEnableHeartbeat: Boolean) {
        prefs.edit().putBoolean(KEY_NIGHT_MODE,shouldEnableHeartbeat).apply()
    }

    companion object {
        private val PREFS_GLOBAL = Application::class.java.name+".PREFS_GLOBAL"
        private val KEY_AUTOLAUNCH = Application::class.java.name+".KEY_AUTOLAUNCH"
        private val KEY_HEARTBEAT = Application::class.java.name+".KEY_HEARTBEAT"
        private val KEY_NIGHT_MODE = Application::class.java.name+".KEY_NIGHT_MODE"
    }

}

inline fun Context.getRancheriaApplication(): RancheriaApplication = this.applicationContext as RancheriaApplication