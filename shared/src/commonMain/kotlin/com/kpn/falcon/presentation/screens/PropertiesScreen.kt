package com.kpn.falcon.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kpn.falcon.data.models.PropertyLead
import com.kpn.falcon.data.models.PropertyStatus
import com.kpn.falcon.di.SessionManager
import com.kpn.falcon.domain.entities.UserRole
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNRadius
import com.kpn.falcon.presentation.theme.KPNTheme
import com.kpn.falcon.presentation.viewmodels.PropertiesUiState
import com.kpn.falcon.presentation.viewmodels.PropertiesViewModel
import com.kpn.falcon.util.Strings
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PropertiesScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel = koinViewModel<PropertiesViewModel>()
    val sessionManager = koinInject<SessionManager>()
    val state by viewModel.uiState.collectAsState()
    val currentUser by sessionManager.currentUser.collectAsState()
    val listState = rememberLazyListState()

    KPNTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = KPNColors.Background) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Top Bar ───────────────────────────────────
                PropertiesTopBar(
                    canAdd = currentUser?.role == UserRole.BD_EXECUTIVE,
                    onAdd = { navigator.push(AddPropertyScreen()) }
                )

                // ── Search bar ────────────────────────────────
                PropertiesSearchBar(
                    query = state.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // ── Filter chips ──────────────────────────────
                PropertiesFilterRow(
                    activeFilter = state.activeFilter,
                    onFilterSelected = viewModel::onFilterChanged
                )

                Spacer(Modifier.height(4.dp))

                // ── Content ───────────────────────────────────
                when {
                    state.isLoading && state.properties.isEmpty() -> KPNLoadingScreen()
                    state.error != null && state.properties.isEmpty() -> KPNErrorScreen(
                        message = state.error!!,
                        onRetry = viewModel::refresh
                    )
                    else -> PropertiesContent(
                        state = state,
                        listState = listState,
                        onRefresh = viewModel::refresh,
                        onCardClick = { property ->
                            navigator.push(PropertyDetailScreen(property.propertyId))
                        }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Top bar
// ─────────────────────────────────────────────────

@Composable
private fun PropertiesTopBar(
    canAdd: Boolean,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(KPNColors.Surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = Strings.PROPERTIES_TITLE,
            style = MaterialTheme.typography.headlineLarge,
            color = KPNColors.TextPrimary
        )
        if (canAdd) {
            KPNOutlinedActionButton(
                text = "+ Add Property",
                onClick = onAdd,
                icon = null
            )
        }
    }
}

// ─────────────────────────────────────────────────
// Search bar
// ─────────────────────────────────────────────────

@Composable
private fun PropertiesSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(KPNColors.Surface, KPNRadius.input)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = KPNColors.TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        androidx.compose.foundation.text.BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = KPNColors.TextPrimary),
            singleLine = true,
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(
                        text = Strings.PROPERTIES_SEARCH_HINT,
                        style = MaterialTheme.typography.bodyLarge,
                        color = KPNColors.TextSecondary.copy(alpha = 0.6f)
                    )
                }
                inner()
            }
        )
        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(18.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Clear search",
                    tint = KPNColors.TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Filter chip row
// ─────────────────────────────────────────────────

@Composable
private fun PropertiesFilterRow(
    activeFilter: PropertyStatus?,
    onFilterSelected: (PropertyStatus?) -> Unit
) {
    val filters = listOf(
        Strings.PROPERTIES_FILTER_ALL to null,
        Strings.PROPERTIES_FILTER_OPEN to PropertyStatus.SUBMITTED,
        Strings.PROPERTIES_FILTER_PENDING to PropertyStatus.IN_REVIEW,
        Strings.PROPERTIES_FILTER_IN_PROGRESS to PropertyStatus.FORWARDED,
        Strings.PROPERTIES_FILTER_CLOSED to PropertyStatus.APPROVED
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Filter icon
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Filter",
            tint = KPNColors.TextSecondary,
            modifier = Modifier
                .padding(start = 16.dp, end = 4.dp)
                .size(18.dp)
        )

        androidx.compose.foundation.lazy.LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(count = filters.size) { index ->
                val (label, status) = filters[index]
                val isActive = activeFilter == status

                FilterChip(
                    selected = isActive,
                    onClick = { onFilterSelected(status) },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KPNColors.Primary,
                        selectedLabelColor = KPNColors.PrimaryText,
                        containerColor = KPNColors.Surface,
                        labelColor = KPNColors.TextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isActive,
                        selectedBorderColor = KPNColors.Primary,
                        borderColor = KPNColors.Border
                    )
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Main content — list or empty state
// ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PropertiesContent(
    state: PropertiesUiState,
    listState: LazyListState,
    onRefresh: () -> Unit,
    onCardClick: (PropertyLead) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.properties.isEmpty()) {
            PropertiesEmptyState(
                hasSearchQuery = state.searchQuery.isNotBlank() || state.activeFilter != null
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 88.dp  // room for FAB
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Result count header
                item {
                    ResultCountHeader(
                        count = state.properties.size,
                        filter = state.activeFilter,
                        query = state.searchQuery
                    )
                }

                items(
                    items = state.properties,
                    key = { it.propertyId }
                ) { property ->
                    PropertyCard(
                        property = property,
                        onClick = { onCardClick(property) }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Result count header
// ─────────────────────────────────────────────────

@Composable
private fun ResultCountHeader(
    count: Int,
    filter: PropertyStatus?,
    query: String
) {
    val label = buildString {
        append("$count propert${if (count == 1) "y" else "ies"}")
        if (filter != null) append(" · ${filter.displayLabel()}")
        if (query.isNotBlank()) append(" · \"$query\"")
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = KPNColors.TextSecondary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

private fun PropertyStatus.displayLabel(): String = when (this) {
    PropertyStatus.DRAFT -> "Draft"
    PropertyStatus.SUBMITTED -> "Open"
    PropertyStatus.IN_REVIEW -> "Pending"
    PropertyStatus.FORWARDED -> "In Progress"
    PropertyStatus.APPROVED -> "Closed"
    PropertyStatus.REJECTED -> "Rejected"
}

// ─────────────────────────────────────────────────
// Empty state
// ─────────────────────────────────────────────────

@Composable
private fun PropertiesEmptyState(hasSearchQuery: Boolean) {
    if (hasSearchQuery) {
        KPNEmptyState(
            title = "No results found",
            subtitle = "Try adjusting your search or filter",
            modifier = Modifier.fillMaxSize()
        )
    } else {
        KPNEmptyState(
            title = Strings.PROPERTIES_EMPTY_TITLE,
            subtitle = Strings.PROPERTIES_EMPTY_SUBTITLE,
            modifier = Modifier.fillMaxSize()
        )
    }
}
