package com.kpn.falcon.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpn.falcon.data.models.ActivityItem
import com.kpn.falcon.data.models.DashboardStats
import com.kpn.falcon.data.models.PropertyLead
import com.kpn.falcon.data.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val stats: DashboardStats = DashboardStats(),
    val recentProperties: List<PropertyLead> = emptyList(),
    val activities: List<ActivityItem> = emptyList()
)

class HomeViewModel(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val stats = propertyRepository.getDashboardStats()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    stats = stats,
                    activities = buildSampleActivities()  // replaced by real endpoint in Task 11
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load dashboard"
                )
            }
        }
    }

    // Placeholder until /dashboard/activities endpoint is added
    private fun buildSampleActivities(): List<ActivityItem> = emptyList()
}
