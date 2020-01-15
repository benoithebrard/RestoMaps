package com.bempaaa.restomaps.viewmodels

import com.bempaaa.restomaps.data.RestoVenue

data class RestoDetailsViewModel(val title: String)

internal fun RestoVenue.toRestoDetailsViewModel() = RestoDetailsViewModel(
    title = name
)