package com.learning.mymap.presentation.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.learning.mymap.data.model.LocationSuggestion
import com.learning.mymap.data.model.Route
import com.learning.mymap.presentation.viewmodel.RouteAnimationViewModel

@Composable
fun HomeScreen(padding: PaddingValues, viewModel: RouteAnimationViewModel = hiltViewModel()) {

    val startQuery by viewModel.startSearchQuery.collectAsState()
    val destinationQuery by viewModel.destinationSearchQuery.collectAsState()
    val startSuggestions by viewModel.startSuggestions.collectAsState()
    val destinationSuggestions by viewModel.destinationSuggestions.collectAsState()
    val selectedStart by viewModel.selectedStart.collectAsState()
    val selectedDestination by viewModel.selectedDestination.collectAsState()
    val route by viewModel.route.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val markerPosition by viewModel.markerPosition.collectAsState()
    val isAnimating by viewModel.isAnimating.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(padding)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                SearchField(
                    value = startQuery,
                    label = "Start Location",
                    onValueChange = { viewModel.updateStartSearchQuery(it) },
                    onClear = { viewModel.updateStartSearchQuery("") }
                )

                if (startSuggestions.isNotEmpty()) {
                    SuggestionsList(
                        suggestions = startSuggestions,
                        onSelect = { viewModel.selectStartLocation(it) }
                    )
                }

                SearchField(
                    value = destinationQuery,
                    label = "Destination",
                    onValueChange = { viewModel.updateDestinationSearchQuery(it) },
                    onClear = { viewModel.updateDestinationSearchQuery("") }
                )

                if (destinationSuggestions.isNotEmpty()) {
                    SuggestionsList(
                        suggestions = destinationSuggestions,
                        onSelect = { viewModel.selectDestinationLocation(it) }
                    )
                }

                selectedStart?.let { SelectedLocationChip("Start: ${it.name}") }
                selectedDestination?.let { SelectedLocationChip("Destination: ${it.name}") }
            }
        }

        if (route != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                MapViewContainer(
                    route = route!!,
                    markerPosition = markerPosition,
                    context = context
                )
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        route?.let { r ->
                            Text(
                                "Distance: ${r.distance} | Duration: ${r.duration}",
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.startMarkerAnimation() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                enabled = !isAnimating
                            ) {
                                Icon(
                                    Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text("Animate")
                            }

                            Button(
                                onClick = { viewModel.resetAnimation() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text("Reset")
                            }

                            Button(
                                onClick = { viewModel.clearSelections() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text("Clear")
                            }
                        }
                    }
                }
            }
        } else if (!isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Select start and destination to view route", color = Color.Gray)
            }
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Filled.Close, contentDescription = "Clear")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
private fun SuggestionsList(
    suggestions: List<LocationSuggestion>,
    onSelect: (LocationSuggestion) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
    ) {
        items(suggestions) { suggestion ->
            SuggestionItem(suggestion = suggestion, onSelect = onSelect)
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: LocationSuggestion,
    onSelect: (LocationSuggestion) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(suggestion) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Filled.LocationOn,
            contentDescription = null,
            tint = Color(0xFF1976D2),
            modifier = Modifier
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(suggestion.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(suggestion.address, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun SelectedLocationChip(text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFE3F2FD)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Filled.LocationOn,
                contentDescription = null,
                tint = Color(0xFF1976D2)
            )
            Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun MapViewContainer(
    route: Route,
    markerPosition: LatLng?,
    context: Context
) {
    AndroidView(
        factory = { MapView(context).apply { onCreate(null) } },
        modifier = Modifier
            .fillMaxSize()
    ) { mapView ->
        mapView.getMapAsync { googleMap ->
            googleMap.clear()

            // Add start marker
            googleMap.addMarker(
                MarkerOptions()
                    .position(route.startPoint)
                    .title("Start")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )

            // Add end marker
            googleMap.addMarker(
                MarkerOptions()
                    .position(route.endPoint)
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )

            // Add animated marker
            if (markerPosition != null) {
                googleMap.addMarker(
                    MarkerOptions()
                        .position(markerPosition)
                        .title("Vehicle")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )
            }

            // Draw polyline
            googleMap.addPolyline(
                PolylineOptions()
                    .addAll(route.polylinePoints)
                    .color(android.graphics.Color.BLUE)
                    .width(8f)
            )

            // Animate camera
            val bounds = com.google.android.gms.maps.model.LatLngBounds.builder()
                .include(route.startPoint)
                .include(route.endPoint)
                .build()
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(bounds, 200)
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // Cleanup if needed
        }
    }
}