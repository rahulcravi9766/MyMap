package com.learning.mymap.data.repository

import com.google.android.gms.maps.model.LatLng
import com.learning.mymap.data.model.LocationSuggestion
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
}