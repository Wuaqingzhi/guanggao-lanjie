package com.lanjie.app.data.entities

data class AppStat(
    val appName: String,
    val packageName: String,
    val totalQueries: Int,
    val blockedQueries: Int
)