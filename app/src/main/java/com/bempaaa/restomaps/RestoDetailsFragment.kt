package com.bempaaa.restomaps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bempaaa.restomaps.data.RestoVenue
import com.bempaaa.restomaps.databinding.ViewRestoDetailsBinding
import com.bempaaa.restomaps.viewmodels.toRestoDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.serialization.json.Json

private const val VENUE_KEY = "venue_key"

class RestoDetailsFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(venue: RestoVenue) = RestoDetailsFragment().apply {
            arguments = Bundle().apply {
                val venueJson = Json.stringify(RestoVenue.serializer(), venue)
                putString(VENUE_KEY, venueJson)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return arguments?.getString(VENUE_KEY)?.let { jsonString ->
            Json.parse(RestoVenue.serializer(), jsonString)
        }?.let { venue ->
            DataBindingUtil.inflate<ViewRestoDetailsBinding>(
                inflater,
                R.layout.view_resto_details,
                container,
                false
            ).apply {
                viewModel = venue.toRestoDetailsViewModel()
            }.root
        }
    }
}