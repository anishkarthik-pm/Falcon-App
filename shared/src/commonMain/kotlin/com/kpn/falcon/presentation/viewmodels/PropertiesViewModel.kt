package com.kpn.falcon.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpn.falcon.data.api.PropertyFilter
import com.kpn.falcon.data.models.PropertyLead
import com.kpn.falcon.data.models.PropertyStatus
import com.kpn.falcon.data.repository.PropertyRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PropertiesUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val properties: List<PropertyLead> = emptyList(),
    val searchQuery: String = "",
    val activeFilter: PropertyStatus? = null
)

@OptIn(FlowPreview::class)
class PropertiesViewModel(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PropertiesUiState())
    val uiState: StateFlow<PropertiesUiState> = _uiState.asStateFlow()

    // Separate flows for search and filter so we can debounce search independently
    private val _searchQuery = MutableStateFlow("")
    private val _activeFilter = MutableStateFlow<PropertyStatus?>(null)

    init {
        // Combine search (debounced) + filter → reload
        viewModelScope.launch {
            combine(
                _searchQuery.debounce(300),
                _activeFilter
            ) { query, filter -> query to filter }
                .distinctUntilChanged()
                .collect { (query, filter) ->
                    fetchProperties(query = query, filter = filter)
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        _searchQuery.value = query
    }

    fun onFilterChanged(status: PropertyStatus?) {
        _uiState.value = _uiState.value.copy(activeFilter = status)
        _activeFilter.value = status
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
            fetchProperties(
                query = _searchQuery.value,
                filter = _activeFilter.value,
                isRefresh = true
            )
        }
    }

    private suspend fun fetchProperties(
        query: String,
        filter: PropertyStatus?,
        isRefresh: Boolean = false
    ) {
        if (!isRefresh) {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        }
        try {
            propertyRepository.getProperties(
                PropertyFilter(
                    status = filter,
                    searchQuery = query.ifBlank { null }
                )
            ).collect { properties ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    properties = properties,
                    error = null
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isRefreshing = false,
                error = e.message ?: "Failed to load properties"
            )
        }
    }
}
