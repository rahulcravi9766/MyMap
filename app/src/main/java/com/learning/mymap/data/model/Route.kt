package com.learning.mymap.data.model

import com.google.android.gms.maps.model.LatLng

data class Route(
    val startPoint: LatLng,
    val endPoint: LatLng,
    val polylinePoints: List<LatLng>,
    val distance: String
)
