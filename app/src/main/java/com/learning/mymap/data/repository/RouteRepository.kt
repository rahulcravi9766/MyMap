package com.learning.mymap.data.repository

import com.google.android.gms.maps.model.LatLng
import com.learning.mymap.data.model.LocationSuggestion
import com.learning.mymap.data.model.Route
import kotlinx.coroutines.delay

class RouteRepository {

    private val mockLocations = listOf(
        LocationSuggestion(
            "1", "Downtown Plaza", "123 Main St, Downtown",
            LatLng(40.7128, -74.0060)
        ),
        LocationSuggestion(
            "2", "Central Park", "Central Park, Midtown",
            LatLng(40.7829, -73.9654)
        ),
        LocationSuggestion(
            "3", "Times Square", "Times Square, Midtown",
            LatLng(40.7580, -73.9855)
        ),
        LocationSuggestion(
            "4", "Grand Central", "42 E 42nd St, Midtown",
            LatLng(40.7527, -73.9772)
        ),
        LocationSuggestion(
            "5", "Brooklyn Bridge", "Brooklyn Bridge, Brooklyn",
            LatLng(40.7061, -73.9969)
        ),
        LocationSuggestion(
            "6", "Statue of Liberty", "Liberty Island, NY",
            LatLng(40.6892, -74.0445)
        ),
        LocationSuggestion(
            "7", "Empire State Building", "350 5th Ave, Midtown",
            LatLng(40.7484, -73.9857)
        ),
        LocationSuggestion(
            "8", "One World Trade", "285 Fulton St, Lower Manhattan",
            LatLng(40.7126, -74.0129)
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
        delay(500) // Simulate API call
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
            // Add slight curvature for realism
            val curve = Math.sin(fraction * Math.PI) * 0.0002
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
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadiusKm * c
    }

    private fun calculateDuration(start: LatLng, end: LatLng): Int {
        val distance = calculateDistance(start, end)
        return (distance / 40 * 60).toInt()
    }
}