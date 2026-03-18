package com.kpn.falcon.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.kpn.falcon.data.models.ActivityItem
import com.kpn.falcon.data.models.DashboardStats
import com.kpn.falcon.data.models.PropertyLead
import com.kpn.falcon.di.SessionManager
import com.kpn.falcon.domain.entities.User
import com.kpn.falcon.domain.entities.UserRole
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNRadius
import com.kpn.falcon.presentation.theme.KPNTheme
import com.kpn.falcon.presentation.viewmodels.HomeUiState
import com.kpn.falcon.presentation.viewmodels.HomeViewModel
import com.kpn.falcon.util.Strings
import com.kpn.falcon.util.KPNConstants
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen() {
    val viewModel = koinViewModel<HomeViewModel>()
    val sessionManager = koinInject<SessionManager>()
    val state by viewModel.uiState.collectAsState()
    val user by sessionManager.currentUser.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    KPNTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = KPNColors.Background) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Offline banner
                if (!isOnline) OfflineBanner()

                when {
                    state.isLoading && state.stats == DashboardStats() -> KPNLoadingScreen()
                    state.error != null && state.stats == DashboardStats() -> KPNErrorScreen(
                        message = state.error!!,
                        onRetry = viewModel::loadDashboard
                    )
                    else -> HomeContent(
                        state = state,
                        user = user,
                        onRefresh = viewModel::loadDashboard
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    user: User?,
    onRefresh: () -> Unit
) {
    val tabNavigator = LocalTabNavigator.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Header ─────────────────────────────────────
        item {
            HomeHeader(user = user)
        }

        // ── Dashboard section ──────────────────────────
        item {
            KPNSectionHeader(
                title = Strings.HOME_DASHBOARD,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        item {
            DashboardGrid(
                stats = state.stats,
                onCardClick = { tabNavigator.current = PropertiesTab },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // ── Recent Properties section ───────────────────
        item {
            KPNSectionHeader(
                title = Strings.HOME_RECENT_PROPERTIES,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        item {
            RecentPropertiesCard(
                stats = state.stats,
                onAddProperty = { tabNavigator.current = PropertiesTab },
                onViewKanban = { tabNavigator.current = PipelineTab },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // ── Recent Activities section ───────────────────
        item {
            KPNSectionHeader(
                title = Strings.HOME_RECENT_ACTIVITIES,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        if (state.activities.isEmpty() && !state.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recent activities",
                        style = MaterialTheme.typography.bodyLarge,
                        color = KPNColors.TextSecondary
                    )
                }
            }
        } else {
            items(state.activities) { activity ->
                ActivityFeedItem(
                    activity = activity,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Header row
// ─────────────────────────────────────────────────

@Composable
private fun HomeHeader(user: User?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(KPNColors.Surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar chip with initials + name + role
        if (user != null) {
            UserAvatarChip(user = user)
        } else {
            Spacer(Modifier.width(48.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            // + Add Property shortcut (BD_EXECUTIVE only)
            if (user?.role == UserRole.BD_EXECUTIVE) {
                IconButton(
                    onClick = { /* Navigate to AddProperty handled by PropertiesScreen */ },
                    modifier = Modifier
                        .size(36.dp)
                        .background(KPNColors.Primary, KPNRadius.chip)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add property", tint = KPNColors.PrimaryText, modifier = Modifier.size(20.dp))
                }
            }
            // Logout
            IconButton(
                onClick = { /* handled via LoginViewModel in parent scope */ },
                modifier = Modifier
                    .size(36.dp)
                    .background(KPNColors.Background, KPNRadius.chip)
            ) {
                Icon(Icons.Default.Logout, contentDescription = "Logout", tint = KPNColors.TextSecondary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun UserAvatarChip(user: User) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Avatar circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(KPNColors.Primary, KPNRadius.chip),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.initials(),
                style = MaterialTheme.typography.labelMedium,
                color = KPNColors.PrimaryText
            )
        }
        Column {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleSmall,
                color = KPNColors.TextPrimary
            )
            Text(
                text = user.role.displayName(),
                style = MaterialTheme.typography.labelSmall,
                color = KPNColors.TextSecondary
            )
        }
    }
}

private fun String.initials(): String {
    val parts = trim().split(" ")
    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.size == 1 && parts[0].isNotBlank() -> parts[0].take(2).uppercase()
        else -> "??"
    }
}

private fun UserRole.displayName(): String = when (this) {
    UserRole.BD_EXECUTIVE -> "BD Executive"
    UserRole.BD_MANAGER -> "BD Manager"
    UserRole.STATE_HEAD -> "State Head"
    UserRole.BD_HEAD -> "BD Head"
    UserRole.CEO -> "CEO"
    UserRole.CFO -> "CFO"
}

// ─────────────────────────────────────────────────
// Dashboard 2×2 grid
// ─────────────────────────────────────────────────

@Composable
private fun DashboardGrid(
    stats: DashboardStats,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    data class CardDef(val label: String, val count: Int, val icon: ImageVector, val tint: Color)

    val cards = listOf(
        CardDef(Strings.HOME_OPEN_PROPERTIES, stats.openProperties, Icons.Default.FolderOpen, KPNColors.AccentGreen),
        CardDef(Strings.HOME_PENDING_PROPERTIES, stats.pendingProperties, Icons.Default.HourglassEmpty, KPNColors.AccentOrange),
        CardDef(Strings.HOME_CLOSED_PROPERTIES, stats.closedProperties, Icons.Default.CheckCircle, KPNColors.StatusSubmitted),
        CardDef(Strings.HOME_IN_PROGRESS, stats.inProgressProperties, Icons.Default.Sync, KPNColors.Primary)
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label = cards[0].label,
                count = cards[0].count,
                icon = cards[0].icon,
                iconTint = cards[0].tint,
                onClick = onCardClick,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = cards[1].label,
                count = cards[1].count,
                icon = cards[1].icon,
                iconTint = cards[1].tint,
                onClick = onCardClick,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label = cards[2].label,
                count = cards[2].count,
                icon = cards[2].icon,
                iconTint = cards[2].tint,
                onClick = onCardClick,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = cards[3].label,
                count = cards[3].count,
                icon = cards[3].icon,
                iconTint = cards[3].tint,
                onClick = onCardClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ─────────────────────────────────────────────────
// Recent Properties card
// ─────────────────────────────────────────────────

@Composable
private fun RecentPropertiesCard(
    stats: DashboardStats,
    onAddProperty: () -> Unit,
    onViewKanban: () -> Unit,
    modifier: Modifier = Modifier
) {
    val total = stats.openProperties + stats.pendingProperties +
            stats.closedProperties + stats.inProgressProperties
    val finalized = stats.closedProperties
    val totalTarget = KPNConstants.MIN_COMPETITORS.coerceAtLeast(total) // placeholder

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = KPNElevation.card)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Store count headline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$finalized/$total ${Strings.HOME_STORES_FINALIZED}",
                    style = MaterialTheme.typography.titleMedium,
                    color = KPNColors.TextPrimary
                )
                StatusPill(
                    status = if (stats.pendingProperties > 0)
                        com.kpn.falcon.data.models.PropertyStatus.IN_REVIEW
                    else com.kpn.falcon.data.models.PropertyStatus.DRAFT
                )
            }

            // Segmented progress bar
            StoreProgressBar(
                finalizedCount = stats.closedProperties,
                pendingCount = stats.pendingProperties,
                revisionCount = stats.inProgressProperties,
                sourcedCount = stats.openProperties,
                totalTarget = total.coerceAtLeast(1),
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)

            // CTA row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                KPNTextActionButton(
                    text = Strings.HOME_ADD_PROPERTY,
                    onClick = onAddProperty,
                    icon = Icons.Default.Add,
                    modifier = Modifier.weight(1f)
                )
                KPNTextActionButton(
                    text = Strings.HOME_VIEW_KANBAN,
                    onClick = onViewKanban,
                    icon = Icons.Default.GridView,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Activity feed item
// ─────────────────────────────────────────────────

@Composable
private fun ActivityFeedItem(
    activity: ActivityItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = activity.propertyId,
                    style = MaterialTheme.typography.labelMedium,
                    color = KPNColors.AccentGreen
                )
                Text(
                    text = activity.action,
                    style = MaterialTheme.typography.bodyMedium,
                    color = KPNColors.TextSecondary
                )
            }
            Text(
                text = epochMsToDateString(activity.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = KPNColors.TextSecondary
            )
        }
    }
}
