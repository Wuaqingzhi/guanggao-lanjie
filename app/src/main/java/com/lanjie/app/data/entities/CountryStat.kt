package com.lanjie.app.data.entities

/**
 * Aggregated DNS query count by destination country.
 */
data class CountryStat(
    val countryCode: String,
    val count: Int
)
