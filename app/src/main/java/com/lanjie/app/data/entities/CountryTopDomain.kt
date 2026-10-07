package com.lanjie.app.data.entities

/**
 * Aggregated domain query counts within a specific destination country.
 */
data class CountryTopDomain(
    val domain: String,
    val count: Int,
    val blockedCount: Int = 0
)
