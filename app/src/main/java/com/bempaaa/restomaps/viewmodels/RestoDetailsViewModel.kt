package com.bempaaa.restomaps.viewmodels

import com.bempaaa.restomaps.data.RestoVenue

data class RestoDetailsViewModel(
    val title: String,
    val category: String?,
    val phone: String?,
    val address: String?,
    val postalCode: String?,
    val city: String?,
    val websiteUrl: String?
)

internal fun RestoVenue.toRestoDetailsViewModel() = RestoDetailsViewModel(
    title = name,
    category = categories.getOrNull(0)?.name,
    phone = contact?.phone,
    address = location.address,
    postalCode = location.postalCode,
    city = location.city,
    websiteUrl = url
)