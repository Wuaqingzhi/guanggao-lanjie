package com.lanjie.app.ui.splash

sealed interface SplashEvent {
    data object Home : SplashEvent
    data object Onboarding : SplashEvent
}