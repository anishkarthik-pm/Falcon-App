package com.kpn.falcon.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpn.falcon.data.api.PropertyFilter
import com.kpn.falcon.data.models.PropertyLead
import com.kpn.falcon.data.models.PropertyStatus
import com.kpn.falcon.data.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PropertiesUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val properties: List<PropertyLead> = emptyList(),
    val searchQuery: String = "",
    val activeFilter: PropertyStatus? = null
)

class PropertiesViewModel(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PropertiesUiState())
    val uiState: StateFlow<PropertiesUiState> = _uiState.asStateFlow()

    init {
        loadProperties()
    }

    fun loadProperties() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                propertyRepository.getProperties(
                    PropertyFilter(
                        status = _uiState.value.activeFilter,
                        searchQuery = _uiState.value.searchQuery.ifBlank { null }
                    )
                ).collect { properties ->
                    _uiState.value = _uiState.value.copy(isLoading = false, properties = properties)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load properties"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadProperties()
    }

    fun onFilterChanged(status: PropertyStatus?) {
        _uiState.value = _uiState.value.copy(activeFilter = status)
        loadProperties()
    }
}
