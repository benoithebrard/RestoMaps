package com.bempaaa.restomaps.viewmodels

import com.bempaaa.restomaps.data.RestoVenue
import com.bempaaa.restomaps.data.toName

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
    category = categories.toName(),
    phone = contact?.phone,
    address = location.address,
    postalCode = location.postalCode,
    city = location.city,
    websiteUrl = url
)