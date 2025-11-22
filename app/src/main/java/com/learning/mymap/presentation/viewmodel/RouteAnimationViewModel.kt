package com.learning.mymap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.mymap.data.model.LocationSuggestion
import com.learning.mymap.data.repository.RouteRepository
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteAnimationViewModel @Inject constructor(val repository: RouteRepository) : ViewModel() {

    private val _startSearchQuery = MutableStateFlow("")
    val startSearchQuery: StateFlow<String> = _startSearchQuery.asStateFlow()

    private val _destinationSearchQuery = MutableStateFlow("")
    val destinationSearchQuery: StateFlow<String> = _destinationSearchQuery.asStateFlow()

    private val _startSuggestions = MutableStateFlow<List<LocationSuggestion>>(emptyList())
    val startSuggestions: StateFlow<List<LocationSuggestion>> = _startSuggestions.asStateFlow()

    private val _destinationSuggestions = MutableStateFlow<List<LocationSuggestion>>(emptyList())
    val destinationSuggestions: StateFlow<List<LocationSuggestion>> = _destinationSuggestions.asStateFlow()

    private val _selectedStart = MutableStateFlow<LocationSuggestion?>(null)
    val selectedStart: StateFlow<LocationSuggestion?> = _selectedStart.asStateFlow()

    private val _selectedDestination = MutableStateFlow<LocationSuggestion?>(null)
    val selectedDestination: StateFlow<LocationSuggestion?> = _selectedDestination.asStateFlow()


    private fun searchStartLocations(query: String) {
        viewModelScope.launch {
            _startSuggestions.value = repository.searchLocations(query)
        }
    }

    private fun searchDestinationLocations(query: String) {
        viewModelScope.launch {
            _destinationSuggestions.value = repository.searchLocations(query)
        }
    }

    fun updateStartSearchQuery(query: String) {
        _startSearchQuery.value = query
        if (query.isEmpty()) {
            _startSuggestions.value = emptyList()
        } else {
            searchStartLocations(query)
        }
    }

    fun updateDestinationSearchQuery(query: String) {
        _destinationSearchQuery.value = query
        if (query.isEmpty()) {
            _destinationSuggestions.value = emptyList()
        } else {
            searchDestinationLocations(query)
        }
    }

    fun clearSelections() {
        _startSearchQuery.value = ""
        _destinationSearchQuery.value = ""
        _startSuggestions.value = emptyList()
        _destinationSuggestions.value = emptyList()
    }

    fun selectStartLocation(location: LocationSuggestion) {
        _selectedStart.value = location
        _startSearchQuery.value = location.name
        _startSuggestions.value = emptyList()
        //calculateRouteIfBothSelected
    }

    fun selectDestinationLocation(location: LocationSuggestion) {
        _selectedDestination.value = location
        _destinationSearchQuery.value = location.name
        _destinationSuggestions.value = emptyList()
        //calculateRouteIfBothSelected
    }

}