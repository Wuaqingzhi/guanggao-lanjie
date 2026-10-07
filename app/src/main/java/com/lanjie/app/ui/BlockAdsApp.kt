package com.lanjie.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.lanjie.app.ui.data.HomeAppKey
import com.lanjie.app.ui.data.OnboardingKey
import com.lanjie.app.ui.data.SplashKey
import com.lanjie.app.ui.dialog.VPNConflictDialog
import com.lanjie.app.ui.onboarding.OnboardingScreen
import com.lanjie.app.ui.splash.SplashScreen


@Composable
fun BlockAdsApp(
    modifier: Modifier = Modifier,
    onRequestVpnPermission: () -> Unit,
    showVpnConflictDialog: Boolean = false,
    onDismissVpnConflictDialog: () -> Unit = {},
    onShowVpnConflictDialog: () -> Unit = {},
) {

    if (showVpnConflictDialog) {
        VPNConflictDialog(
            onDismissVpnConflictDialog = onDismissVpnConflictDialog,
        )
    }

    val backStack = rememberNavBackStack(SplashKey)
    NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
        modifier = modifier,
        entryProvider = entryProvider {
            entry<SplashKey> {
                SplashScreen(
                    onNavigateToHome = {
                        backStack.add(HomeAppKey)
                        backStack.remove(SplashKey)
                    },
                    onNavigateToOnboarding = {
                        backStack.add(OnboardingKey)
                        backStack.remove(SplashKey)
                    }
                )
            }
            entry<OnboardingKey> {
                OnboardingScreen(
                    onNavigateToHome = {
                        backStack.add(HomeAppKey)
                        backStack.remove(OnboardingKey)
                    }
                )
            }
            entry<HomeAppKey> {
                HomeApp(
                    onRequestVpnPermission = onRequestVpnPermission,
                    onShowVpnConflictDialog = onShowVpnConflictDialog
                )
            }

        }
    )
}
