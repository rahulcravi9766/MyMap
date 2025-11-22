package com.learning.mymap.data.model

import com.google.android.gms.maps.model.LatLng

data class LocationSuggestion(
    val id: String,
    val name: String,
    val address: String,
    val latLng: LatLng
)
