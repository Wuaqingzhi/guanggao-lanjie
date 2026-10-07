package com.lanjie.app

import android.app.Application
import com.lanjie.app.data.datastore.AppPreferences
import com.lanjie.app.di.appModule
import com.lanjie.app.worker.DailySummaryScheduler
import com.lanjie.app.worker.FilterUpdateScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree
import com.lanjie.app.utils.CrashReportingManager
import com.lanjie.app.utils.FileLoggingTree


class BlockAdsApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BlockAdsApplication)
            modules(appModule)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        
        // Plant File logging tree for all builds to allow log export
        Timber.plant(FileLoggingTree(this))

        // Schedule auto-update for filter lists after Koin is initialized
        val appPreferences: AppPreferences by inject()
        applicationScope.launch {
            // Restore Crash Reporting state dynamically
            val isCrashReportingEnabled = appPreferences.crashReportingEnabled.first()
            CrashReportingManager.toggleSentry(this@BlockAdsApplication, isCrashReportingEnabled)

            // Move v6.3.0 single-config users onto the multi-profile schema.
            appPreferences.migrateLegacyWgConfigIfNeeded()

            FilterUpdateScheduler.scheduleFilterUpdate(this@BlockAdsApplication, appPreferences)
            com.lanjie.app.ui.browser.rules.BrowserRuleUpdateWorker.schedule(this@BlockAdsApplication)

            // Schedule daily summary only if enabled
            if (appPreferences.dailySummaryEnabled.first()) {
                DailySummaryScheduler.scheduleDailySummary(this@BlockAdsApplication)
            }

            // Warm up GeoIP database on IO thread
            com.lanjie.app.data.geoip.GeoIpLookup.init(this@BlockAdsApplication)
        }

        // Trusted Wi-Fi networks (#197): auto-pause/resume on SSID change.
        com.lanjie.app.service.TrustedNetworkManager(this, appPreferences).start()
    }
}
