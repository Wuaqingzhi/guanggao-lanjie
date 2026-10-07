package com.lanjie.app.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.lanjie.app.data.datastore.AppPreferences
import com.lanjie.app.ui.about.AboutScreen
import com.lanjie.app.ui.appearance.AppearanceScreen
import com.lanjie.app.ui.appmanagement.AppManagementScreen
import com.lanjie.app.ui.customrules.CustomRulesScreen
import com.lanjie.app.ui.data.AboutKey
import com.lanjie.app.ui.data.AppManagementKey
import com.lanjie.app.ui.data.AppearanceKey
import com.lanjie.app.ui.browser.BrowserActivity
import com.lanjie.app.ui.browser.BrowserScreen
import com.lanjie.app.ui.browser.elementrules.ElementRulesScreen
import com.lanjie.app.ui.data.BottomBarScreen
import com.lanjie.app.ui.data.BrowserKey
import com.lanjie.app.ui.data.ElementRulesKey
import com.lanjie.app.ui.data.CustomRuleKey
import com.lanjie.app.ui.data.DnsProviderKey
import com.lanjie.app.ui.data.DomainRulesKey
import com.lanjie.app.ui.data.FilterDetailKey
import com.lanjie.app.ui.data.FilterKey
import com.lanjie.app.ui.data.FireWallKey
import com.lanjie.app.ui.data.HomeKey
import com.lanjie.app.ui.data.HttpsFilteringKey
import com.lanjie.app.ui.data.CertInstallationWizardKey
import com.lanjie.app.ui.httpsfiltering.wizard.CertInstallationWizardScreen
import com.lanjie.app.ui.data.LogsKey
import com.lanjie.app.ui.data.ProfileKey
import com.lanjie.app.ui.data.SettingsKey
import com.lanjie.app.ui.data.StatisticsKey
import com.lanjie.app.ui.data.WhiteListAppKey
import com.lanjie.app.ui.data.TrustedNetworksKey
import com.lanjie.app.ui.data.WireGuardEditKey
import com.lanjie.app.ui.data.WireGuardImportKey
import com.lanjie.app.ui.dnsprovider.DnsProviderScreen
import com.lanjie.app.ui.domainrules.DomainRulesScreen
import com.lanjie.app.ui.filter.FilterSetupScreen
import com.lanjie.app.ui.filter.detail.FilterDetailScreen
import com.lanjie.app.ui.firewall.FirewallScreen
import com.lanjie.app.ui.home.HomeScreen
import com.lanjie.app.ui.httpsfiltering.HttpsFilteringScreen
import com.lanjie.app.ui.logs.LogsScreen
import com.lanjie.app.ui.profile.ProfileScreen
import com.lanjie.app.ui.settings.SettingsScreen
import com.lanjie.app.ui.statistics.StatisticsScreen
import com.lanjie.app.ui.whitelist.AppWhitelistScreen
import com.lanjie.app.ui.wireguard.WireGuardEditScreen
import com.lanjie.app.ui.wireguard.WireGuardImportScreen
import org.koin.compose.koinInject

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeApp(
    onRequestVpnPermission: () -> Unit = {},
    onShowVpnConflictDialog: () -> Unit = {}
) {
    val context = LocalContext.current
    val appPrefs: AppPreferences = koinInject()
    val showBottomNavLabels by appPrefs.showBottomNavLabels.collectAsStateWithLifecycle(
        initialValue = true,
    )
    val firewallEnabled by appPrefs.firewallEnabled.collectAsStateWithLifecycle(
        initialValue = false,
    )
    val homeStack = rememberNavBackStack(HomeKey)
    val filterStack = rememberNavBackStack(FilterKey)
    val firewallStack = rememberNavBackStack(FireWallKey)
    val domainRuleStack = rememberNavBackStack(DomainRulesKey)
    val settingsStack = rememberNavBackStack(SettingsKey)
    var currentTab by rememberSaveable { mutableStateOf(BottomBarScreen.Home) }

    val currentBackStack = when (currentTab) {
        BottomBarScreen.Home -> homeStack
        BottomBarScreen.FilterSetup -> filterStack
        BottomBarScreen.Firewall -> firewallStack
        BottomBarScreen.DomainRule -> domainRuleStack
        BottomBarScreen.Settings -> settingsStack
    }

    val bottomBarScreens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.FilterSetup,
        BottomBarScreen.Firewall,
        BottomBarScreen.DomainRule,
        BottomBarScreen.Settings
    )
    var showBottomBar by rememberSaveable { mutableStateOf(true) }
    fun safePop(stack: MutableList<*>) {
        if (stack.size > 1) {
            stack.removeLastOrNull()
        }
        showBottomBar = stack.size <= 1
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (!showBottomBar) return@Scaffold
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = LocalDensity.current.density,
                    fontScale = 1f
                )
            ) {
                NavigationBar(
                    windowInsets = WindowInsets.navigationBars,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    bottomBarScreens.forEach { screen ->
                        NavigationBarItem(
                            selected = currentBackStack == when (screen) {
                                BottomBarScreen.Home -> homeStack
                                BottomBarScreen.FilterSetup -> filterStack
                                BottomBarScreen.Firewall -> firewallStack
                                BottomBarScreen.DomainRule -> domainRuleStack
                                BottomBarScreen.Settings -> settingsStack
                            },
                            onClick = {
                                currentTab = screen
                            },
                            icon = {
                                if (screen == BottomBarScreen.Firewall && firewallEnabled) {
                                    com.lanjie.app.ui.component.BurningFireIcon(
                                        contentDescription = stringResource(screen.labelRes)
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(screen.icon),
                                        contentDescription = stringResource(screen.labelRes),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            label = if (showBottomNavLabels) {
                                {
                                    Text(
                                        text = stringResource(screen.labelRes),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = LocalTextStyle.current.copy(
                                            fontSize = 10.5.sp,
                                            letterSpacing = (-0.2).sp
                                        )
                                    )
                                }
                            } else null,
                            alwaysShowLabel = showBottomNavLabels,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // When on a non-Home tab root, back should switch to Home tab instead of exiting
        BackHandler(enabled = currentTab != BottomBarScreen.Home && currentBackStack.size <= 1) {
            currentTab = BottomBarScreen.Home
            showBottomBar = true
        }

        NavDisplay(
            backStack = currentBackStack,
            onBack = {
                safePop(currentBackStack)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .consumeWindowInsets(PaddingValues(bottom = innerPadding.calculateBottomPadding())),
            entryProvider = entryProvider {
                entry<HomeKey> {
                    HomeScreen(
                        onShowVpnConflictDialog = onShowVpnConflictDialog,
                        onRequestVpnPermission = onRequestVpnPermission,
                        onNavigateToLogScreen = { filterStatus ->
                            showBottomBar = false
                            homeStack.add(LogsKey(filterStatus))
                        },
                        onNavigateToLogsWithQuery = { domain ->
                            showBottomBar = false
                            homeStack.add(LogsKey(searchQuery = domain))
                        },
                        onNavigateToStatisticsScreen = {
                            showBottomBar = false
                            homeStack.add(StatisticsKey)
                        },
                        onNavigateToProfileScreen = {
                            showBottomBar = false
                            homeStack.add(ProfileKey)
                        },
                        onNavigateToBrowser = { url ->
                            context.startActivity(BrowserActivity.createIntent(context, url))
                        }
                    )
                }
                entry<FilterKey> {
                    FilterSetupScreen(
                        onNavigateToFilterDetail = { filterId ->
                            showBottomBar = false
                            filterStack.add(FilterDetailKey(filterId))
                        },
                        onNavigateToCustomRules = {
                            showBottomBar = false
                            filterStack.add(CustomRuleKey)
                        }
                    )
                }
                entry<FireWallKey> {
                    FirewallScreen()
                }
                entry<DomainRulesKey> {
                    DomainRulesScreen()
                }
                entry<SettingsKey> {
                    SettingsScreen(
                        onNavigateToAbout = {
                            showBottomBar = false
                            settingsStack.add(AboutKey)
                        },
                        onNavigateToAppearance = {
                            showBottomBar = false
                            settingsStack.add(AppearanceKey)
                        },
                        onNavigateToAppManagement = {
                            showBottomBar = false
                            settingsStack.add(AppManagementKey)
                        },
                        onNavigateToFilterSetup = {
                            currentTab = BottomBarScreen.FilterSetup
                        },
                        onNavigateToWhitelistApps = {
                            showBottomBar = false
                            settingsStack.add(WhiteListAppKey)
                        },
                        onNavigateToTrustedNetworks = {
                            showBottomBar = false
                            settingsStack.add(TrustedNetworksKey)
                        },
                        onNavigateToWireGuardImport = {
                            showBottomBar = false
                            settingsStack.add(WireGuardImportKey)
                        },
                        onNavigateToHttpsFiltering = {
                            showBottomBar = false
                            settingsStack.add(HttpsFilteringKey)
                        },
                        onNavigateToDNSProvider = {
                            showBottomBar = false
                            settingsStack.add(DnsProviderKey)
                        }
                    )
                }
                entry<StatisticsKey> {
                    StatisticsScreen(
                        onNavigateBack = {
                            safePop(homeStack)
                        },
                        onNavigateToFilterDetail = { filterId ->
                            showBottomBar = false
                            homeStack.add(FilterDetailKey(filterId))
                        }
                    )
                }
                entry<LogsKey> {
                    LogsScreen(
                        initialFilterStatus = it.filterStatus,
                        initialSearchQuery = it.searchQuery,
                        onNavigateBack = {
                            safePop(homeStack)
                        }
                    )
                }
                entry<ProfileKey> {
                    ProfileScreen(
                        onNavigateBack = {
                            safePop(homeStack)
                        }
                    )
                }
                entry<FilterDetailKey> {
                    FilterDetailScreen(
                        filterId = it.filterId,
                        onNavigateBack = {
                            safePop(filterStack)
                        }
                    )
                }
                entry<CustomRuleKey> {
                    CustomRulesScreen(
                        onNavigateBack = {
                            safePop(filterStack)
                        }
                    )
                }
                entry<AboutKey> {
                    AboutScreen(
                        onNavigateBack = {
                            safePop(settingsStack)
                        }
                    )
                }
                entry<AppearanceKey> {
                    AppearanceScreen(
                        onNavigateBack = {
                            safePop(settingsStack)
                        }
                    )
                }
                entry<AppManagementKey> {
                    AppManagementScreen(
                        onNavigateBack = {
                            safePop(settingsStack)
                        }
                    )
                }
                entry<DnsProviderKey> {
                    DnsProviderScreen(
                        onNavigateBack = {
                            safePop(settingsStack)
                        }
                    )
                }
                entry<WhiteListAppKey> {
                    AppWhitelistScreen(
                        onNavigateBack = {
                            safePop(settingsStack)
                        }
                    )
                }
                entry<TrustedNetworksKey> {
                    com.lanjie.app.ui.trustednetworks.TrustedNetworksScreen(
                        onNavigateBack = {
                            safePop(settingsStack)
                        }
                    )
                }
                entry<WireGuardImportKey> {
                    WireGuardImportScreen(
                        onNavigateBack = {
                            safePop(settingsStack)
                        },
                        onEditProfile = { profileId ->
                            settingsStack.add(WireGuardEditKey(profileId))
                        },
                    )
                }
                entry<WireGuardEditKey> { key ->
                    WireGuardEditScreen(
                        profileId = key.profileId,
                        onNavigateBack = {
                            safePop(settingsStack)
                        },
                    )
                }
                entry<HttpsFilteringKey> {
                    HttpsFilteringScreen(
                        onNavigateBack = {
                            safePop(settingsStack)
                        },
                        onNavigateToWizard = {
                            settingsStack.add(CertInstallationWizardKey)
                        }
                    )
                }
                entry<CertInstallationWizardKey> {
                    CertInstallationWizardScreen(
                        onNavigateBack = {
                            safePop(settingsStack)
                        }
                    )
                }
                entry<BrowserKey> { key ->
                    BrowserScreen(
                        initialUrl = key.initialUrl,
                        onCloseBrowser = {
                            safePop(currentBackStack)
                        },
                        onNavigateToElementRules = {
                            currentBackStack.add(ElementRulesKey)
                        }
                    )
                }
                entry<ElementRulesKey> {
                    ElementRulesScreen(
                        onNavigateBack = {
                            safePop(currentBackStack)
                        }
                    )
                }
            }
        )
    }
}