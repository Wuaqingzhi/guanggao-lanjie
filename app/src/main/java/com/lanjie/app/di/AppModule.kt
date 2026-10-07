package com.lanjie.app.di

import com.lanjie.app.BuildConfig
import com.lanjie.app.data.AppDatabase
import com.lanjie.app.data.dao.FirewallRuleDao
import com.lanjie.app.data.datastore.AppPreferences
import com.lanjie.app.data.entities.ProfileManager
import com.lanjie.app.data.remote.FilterDownloadManager
import com.lanjie.app.data.remote.api.CustomFilterApi
import com.lanjie.app.data.repository.CustomFilterManager
import com.lanjie.app.data.repository.FilterListRepository
import com.lanjie.app.ui.dnsprovider.DnsProviderViewModel
import com.lanjie.app.ui.filter.detail.FilterDetailViewModel
import com.lanjie.app.ui.filter.FilterSetupViewModel
import com.lanjie.app.ui.home.HomeViewModel
import com.lanjie.app.ui.logs.LogViewModel
import com.lanjie.app.ui.onboarding.OnboardingViewModel
import com.lanjie.app.ui.profile.ProfileViewModel
import com.lanjie.app.ui.appearance.AppearanceViewModel
import com.lanjie.app.ui.settings.SettingsViewModel
import com.lanjie.app.ui.statistics.StatisticsViewModel
import com.lanjie.app.ui.whitelist.AppWhitelistViewModel
import com.lanjie.app.ui.appmanagement.AppManagementViewModel
import com.lanjie.app.ui.customrules.CustomRulesViewModel
import com.lanjie.app.ui.domainrules.DomainRulesViewModel
import com.lanjie.app.ui.firewall.FirewallViewModel
import com.lanjie.app.ui.splash.SplashViewModel
import com.lanjie.app.ui.wireguard.WireGuardEditViewModel
import com.lanjie.app.ui.wireguard.WireGuardImportViewModel
import com.lanjie.app.ui.httpsfiltering.HttpsFilteringViewModel
import com.lanjie.app.ui.httpsfiltering.wizard.CertInstallationWizardViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber

val appModule = module {

    // HTTP Client
    single {
        HttpClient(CIO) {
            engine {
                requestTimeout = 60_000
                endpoint {
                    connectTimeout = 30_000
                }
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
                val logLevel = if (BuildConfig.DEBUG) LogLevel.INFO else LogLevel.NONE
                level = logLevel
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 30_000
            }
        }
    }

    // DNS Clients (Removed - now handled by Go tunnel)

    // Database
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().dnsLogDao() }
    single { get<AppDatabase>().filterListDao() }
    single { get<AppDatabase>().whitelistDomainDao() }
    single { get<AppDatabase>().dnsErrorDao() }
    single { get<AppDatabase>().customDnsRuleDao() }
    single { get<AppDatabase>().protectionProfileDao() }
    single { get<AppDatabase>().firewallRuleDao() }
    single { get<AppDatabase>().elementRuleDao() }

    // Preferences
    single { AppPreferences(androidContext()) }

    // Repository
    single { FilterDownloadManager(androidContext(), get()) }
    single {
        FilterListRepository(
            context = androidContext(),
            filterListDao = get(),
            whitelistDomainDao = get(),
            customDnsRuleDao = get(),
            client = get(),
            downloadManager = get()
        )
    }
    single { CustomFilterApi(get()) }
    single {
        CustomFilterManager(
            context = androidContext(),
            client = get(),
            filterListDao = get(),
            customFilterApi = get()
        )
    }

    // Browser Dynamic Rules & Search Suggestions
    single { com.lanjie.app.ui.browser.rules.BrowserRuleStorage(androidContext()) }
    single<com.lanjie.app.ui.browser.rules.BrowserRuleRepository> {
        com.lanjie.app.ui.browser.rules.BrowserRuleRepositoryImpl(
            storage = get(),
            client = get()
        )
    }
    single<com.lanjie.app.ui.browser.data.SearchSuggestionRepository> {
        com.lanjie.app.ui.browser.data.SearchSuggestionRepositoryImpl(
            client = get()
        )
    }

    // Profile Manager
    single {
        ProfileManager(
            profileDao = get(),
            filterListDao = get(),
            appPrefs = get(),
            filterRepo = get()
        )
    }

    // ViewModels
    viewModel {
        HomeViewModel(
            appPrefs = get(),
            dnsLogDao = get(),
            filterRepo = get(),
            profileDao = get(),
            filterListDao = get(),
            whitelistDomainDao = get(),
            customDnsRuleDao = get()
        )
    }
    viewModel { StatisticsViewModel(dnsLogDao = get(), filterListDao = get()) }
    viewModel {
        LogViewModel(
            dnsLogDao = get(),
            filterListDao = get(),
            whitelistDomainDao = get(),
            customDnsRuleDao = get(),
            filterListRepository = get(),
            appPrefs = get(),
            application = androidApplication(),
            firewallRuleDao = get()
        )
    }
    viewModel {
        SettingsViewModel(
            appPrefs = get(),
            filterRepo = get(),
            dnsLogDao = get(),
            whitelistDomainDao = get(),
            filterListDao = get(),
            customDnsRuleDao = get(),
            profileDao = get(),
            profileManager = get(),
            firewallRuleDao = get(),
            application = androidApplication()
        )
    }
    viewModel {
        FilterSetupViewModel(
            filterRepo = get(),
            filterListDao = get(),
            customFilterManager = get(),
            profileManager = get(),
            application = androidApplication(),
            appPreferences = get()
        )
    }
    viewModel { (filterId: Long) ->
        FilterDetailViewModel(
            filterId = filterId,
            filterListDao = get(),
            dnsLogDao = get(),
            filterRepo = get(),
            profileManager = get(),
            application = androidApplication(),
            customFilterManager = get()
        )
    }
    viewModel {
        AppWhitelistViewModel(
            appPrefs = get(),
            application = androidApplication()
        )
    }
    viewModel {
        com.lanjie.app.ui.trustednetworks.TrustedNetworksViewModel(
            appPrefs = get(),
            application = androidApplication()
        )
    }
    viewModel {
        CustomRulesViewModel(
            customDnsRuleDao = get(),
            filterListRepository = get(),
            application = androidApplication()
        )
    }
    viewModel {
        DnsProviderViewModel(
            appPrefs = get(),
            application = androidApplication()
        )
    }
    viewModel {
        AppManagementViewModel(
            appPrefs = get(),
            dnsLogDao = get(),
            application = androidApplication(),
        )
    }
    viewModel {
        OnboardingViewModel(
            appPrefs = get(),
            application = androidApplication()
        )
    }
    viewModel {
        ProfileViewModel(
            profileManager = get(),
            profileDao = get(),
            filterListDao = get(),
            application = androidApplication()
        )
    }
    viewModel {
        FirewallViewModel(
            appPrefs = get(),
            firewallRuleDao = get(),
            application = androidApplication()
        )
    }
    viewModel {
        AppearanceViewModel(
            appPrefs = get(),
            application = androidApplication()
        )
    }
    viewModel {
        SplashViewModel(
            appPrefs = get(),
        )
    }
    viewModel {
        DomainRulesViewModel(
            whitelistDomainDao = get(),
            customDnsRuleDao = get(),
            application = androidApplication()
        )
    }
    viewModel {
        WireGuardImportViewModel(
            application = androidApplication()
        )
    }
    viewModel {
        WireGuardEditViewModel(
            application = androidApplication()
        )
    }
    viewModel {
        HttpsFilteringViewModel(
            application = androidApplication()
        )
    }
    viewModel {
        CertInstallationWizardViewModel(
            application = androidApplication()
        )
    }
    viewModel {
        com.lanjie.app.ui.browser.BrowserViewModel(
            application = androidApplication(),
            ruleRepository = get(),
            suggestionRepository = get(),
            elementRuleDao = get()
        )
    }
    viewModel {
        com.lanjie.app.ui.browser.elementrules.ElementRulesViewModel(
            application = androidApplication(),
            elementRuleDao = get()
        )
    }
}

