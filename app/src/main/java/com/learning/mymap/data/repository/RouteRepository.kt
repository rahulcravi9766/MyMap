package com.learning.mymap.data.repository

import com.google.android.gms.maps.model.LatLng
import com.learning.mymap.data.model.LocationSuggestion
import com.learning.mymap.data.model.Route
import kotlinx.coroutines.delay
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class RouteRepository {

    private val mockLocations = listOf(
        LocationSuggestion(
            "1", "Vidhana Soudha", "Dr Rajendra Prasad Rd, Cubbon Park",
            LatLng(13.1919, 77.5937)
        ),
        LocationSuggestion(
            "2", "Cubbon Park", "Cubbon Park, Vasanth Nagar",
            LatLng(13.1890, 77.5941)
        ),
        LocationSuggestion(
            "3", "Bangalore Palace", "Palace Road, High Grounds",
            LatLng(13.2046, 77.6110)
        ),
        LocationSuggestion(
            "4", "Lalbagh Botanical Garden", "Lalbagh Botanical Garden, South Bangalore",
            LatLng(13.1759, 77.5846)
        ),
        LocationSuggestion(
            "5", "Indiranagar Lake", "100 Feet Road, Indiranagar",
            LatLng(13.1964, 77.6409)
        ),
        LocationSuggestion(
            "6", "Ulsoor Lake", "Ulsoor Lake, East Bangalore",
            LatLng(13.1815, 77.6205)
        ),
        LocationSuggestion(
            "7", "Koramangala", "Koramangala, South Bangalore",
            LatLng(13.0352, 77.6245)
        ),
        LocationSuggestion(
            "8", "MG Road", "MG Road, Downtown Bangalore",
            LatLng(13.1939, 77.6009)
        ),
        LocationSuggestion(
            "9", "Whitefield", "Whitefield, East Bangalore",
            LatLng(13.1624, 77.7422)
        ),
        LocationSuggestion(
            "10", "Jayanagar", "4th Block, Jayanagar, South Bangalore",
            LatLng(13.0288, 77.5940)
        ),
        LocationSuggestion(
            "11", "Rajajinagar", "Rajajinagar, West Bangalore",
            LatLng(13.1848, 77.5553)
        ),
        LocationSuggestion(
            "12", "Mathikere Market", "Mathikere, North Bangalore",
            LatLng(13.2198, 77.5710)
        )
    )

    suspend fun searchLocations(query: String): List<LocationSuggestion> {
        delay(300)
        return if (query.isEmpty()) {
            emptyList()
        } else {
            mockLocations.filter { location ->
                location.name.contains(query, ignoreCase = true) ||
                        location.address.contains(query, ignoreCase = true)
            }
        }
    }

    suspend fun calculateRoute(start: LatLng, end: LatLng): Route {
        delay(500)
        val polylinePoints = generatePolylinePoints(start, end)
        return Route(
            startPoint = start,
            endPoint = end,
            polylinePoints = polylinePoints,
            distance = String.format("%.2f km", calculateDistance(start, end)),
            duration = "${calculateDuration(start, end)} mins"
        )
    }

    private fun generatePolylinePoints(start: LatLng, end: LatLng): List<LatLng> {
        val points = mutableListOf<LatLng>()
        val steps = 50

        for (i in 0..steps) {
            val fraction = i.toDouble() / steps
            val lat = start.latitude + (end.latitude - start.latitude) * fraction
            val lng = start.longitude + (end.longitude - start.longitude) * fraction
            val curve = sin(fraction * Math.PI) * 0.0002
            points.add(LatLng(lat + curve, lng))
        }
        return points
    }

    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        val lat1 = start.latitude
        val lon1 = start.longitude
        val lat2 = end.latitude
        val lon2 = end.longitude

        val earthRadiusKm = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }

    private fun calculateDuration(start: LatLng, end: LatLng): Int {
        val distance = calculateDistance(start, end)
        return (distance / 40 * 60).toInt()
    }
}