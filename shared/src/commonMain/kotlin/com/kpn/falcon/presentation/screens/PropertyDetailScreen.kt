package com.kpn.falcon.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.kpn.falcon.data.models.*
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.screens.detail.GeoIQTab
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNElevation
import com.kpn.falcon.presentation.theme.KPNRadius
import com.kpn.falcon.presentation.theme.KPNTheme
import com.kpn.falcon.presentation.viewmodels.PropertyDetailViewModel
import com.kpn.falcon.util.Strings
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

data class PropertyDetailScreen(val propertyId: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<PropertyDetailViewModel> { parametersOf(propertyId) }
        val state by viewModel.uiState.collectAsState()

        KPNTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = KPNColors.Background) {
                when {
                    state.isLoading -> KPNLoadingScreen()
                    state.error != null -> KPNErrorScreen(
                        message = state.error!!,
                        onRetry = viewModel::loadProperty
                    )
                    state.property != null -> PropertyDetailContent(
                        state = state,
                        viewModel = viewModel,
                        onBack = { navigator.pop() }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Main scaffold
// ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PropertyDetailContent(
    state: com.kpn.falcon.presentation.viewmodels.PropertyDetailUiState,
    viewModel: PropertyDetailViewModel,
    onBack: () -> Unit
) {
    val property = state.property!!
    val tabs = listOf(
        Strings.DETAIL_TAB_OVERVIEW,
        Strings.DETAIL_TAB_GEO_IQ,
        Strings.DETAIL_TAB_SCORING,
        Strings.DETAIL_TAB_COMMENTS,
        Strings.DETAIL_TAB_APPROVALS
    )

    Scaffold(
        topBar = {
            DetailTopBar(
                propertyId = property.propertyId,
                status = property.status,
                phone = property.contact.landlordPhone,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Sticky tab row
            DetailTabRow(tabs = tabs, activeTab = state.activeTab, onTabSelected = viewModel::onTabSelected)

            // Tab content
            when (state.activeTab) {
                0 -> OverviewTab(property = property)
                1 -> GeoIQTab(state = state, viewModel = viewModel)
                2 -> DetailTabStub(title = "Scoring", subtitle = "Auto-suggest scores — available in Task 8")
                3 -> DetailTabStub(title = "Comments", subtitle = "Comments & revision log — coming soon")
                4 -> ApprovalsTab(approvalChain = property.approvalChain, status = property.status)
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Top bar
// ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopBar(
    propertyId: String,
    status: PropertyStatus,
    phone: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = propertyId,
                    style = MaterialTheme.typography.titleMedium,
                    color = KPNColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                StatusPill(status = status)
            }
        },
        navigationIcon = {
            KPNBackButton(onClick = onBack)
        },
        actions = {
            if (phone.length == 10) {
                WhatsAppButton(
                    phone = phone,
                    onClick = {},
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = KPNColors.Surface,
            titleContentColor = KPNColors.TextPrimary,
            actionIconContentColor = KPNColors.AccentOrange
        )
    )
}

// ─────────────────────────────────────────────────
// Tab row
// ─────────────────────────────────────────────────

@Composable
private fun DetailTabRow(
    tabs: List<String>,
    activeTab: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = activeTab,
        containerColor = KPNColors.Surface,
        contentColor = KPNColors.TextPrimary,
        edgePadding = 8.dp,
        indicator = { tabPositions ->
            if (activeTab < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = KPNColors.AccentGreen
                )
            }
        },
        divider = { HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp) }
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = index == activeTab,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (index == activeTab) KPNColors.TextPrimary else KPNColors.TextSecondary,
                        fontWeight = if (index == activeTab) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────
// Overview tab
// ─────────────────────────────────────────────────

@Composable
private fun OverviewTab(property: PropertyLead) {
    // Collapse state for each section
    var specsExpanded by remember { mutableStateOf(true) }
    var roadExpanded by remember { mutableStateOf(false) }
    var commercialsExpanded by remember { mutableStateOf(false) }
    var competitorsExpanded by remember { mutableStateOf(false) }
    var contactExpanded by remember { mutableStateOf(false) }

    val allPhotos = property.media.exteriorPhotos + property.media.internalPhotos

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Phase tracker ─────────────────────────────
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = KPNRadius.card,
                colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
                elevation = CardDefaults.cardElevation(KPNElevation.card)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Approval Phase",
                        style = MaterialTheme.typography.labelMedium,
                        color = KPNColors.TextSecondary
                    )
                    PhaseTracker(
                        currentPhase = property.phase.coerceIn(1, 8),
                        daysInStage = daysSince(property.updatedAt)
                    )
                }
            }
        }

        // ── Property ID block ─────────────────────────
        item {
            PropertyLeadDetailsBlock(
                propertyId = property.propertyId,
                bdExecutiveName = property.bdExecutiveId,
                dateAdded = epochMsToDateString(property.createdAt),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))
        }

        // ── Media gallery ─────────────────────────────
        if (allPhotos.isNotEmpty()) {
            item {
                MediaGalleryRow(
                    photos = allPhotos,
                    videoUrls = property.media.videos
                )
                Spacer(Modifier.height(4.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = KPNColors.Border,
                    thickness = 0.5.dp
                )
                Spacer(Modifier.height(4.dp))
            }
        }

        // ── Store Specs ───────────────────────────────
        item {
            SectionCard {
                KPNCollapsibleSection(
                    title = "Store Specifications",
                    isExpanded = specsExpanded,
                    onToggle = { specsExpanded = !specsExpanded }
                ) {
                    StoreSpecsSection(property.storeSpecs)
                }
            }
        }

        // ── Road & Access ─────────────────────────────
        item {
            SectionCard {
                KPNCollapsibleSection(
                    title = "Road & Accessibility",
                    isExpanded = roadExpanded,
                    onToggle = { roadExpanded = !roadExpanded }
                ) {
                    RoadAccessSection(property.roadAccess)
                }
            }
        }

        // ── Commercials ───────────────────────────────
        item {
            SectionCard {
                KPNCollapsibleSection(
                    title = "Commercial Details",
                    isExpanded = commercialsExpanded,
                    onToggle = { commercialsExpanded = !commercialsExpanded }
                ) {
                    CommercialsSection(property.commercials)
                }
            }
        }

        // ── Competitors ───────────────────────────────
        if (property.competitors.isNotEmpty()) {
            item {
                SectionCard {
                    KPNCollapsibleSection(
                        title = "Competitors (${property.competitors.size})",
                        isExpanded = competitorsExpanded,
                        onToggle = { competitorsExpanded = !competitorsExpanded }
                    ) {
                        CompetitorsSection(property.competitors)
                    }
                }
            }
        }

        // ── Contact ───────────────────────────────────
        item {
            SectionCard {
                KPNCollapsibleSection(
                    title = "Contact Information",
                    isExpanded = contactExpanded,
                    onToggle = { contactExpanded = !contactExpanded }
                ) {
                    ContactSection(property.contact)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Media gallery row
// ─────────────────────────────────────────────────

@Composable
private fun MediaGalleryRow(photos: List<String>, videoUrls: List<String>) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Media",
                style = MaterialTheme.typography.titleSmall,
                color = KPNColors.TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "${photos.size} photos · ${videoUrls.size} video(s)",
                style = MaterialTheme.typography.labelSmall,
                color = KPNColors.TextSecondary
            )
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(photos) { url ->
                MediaThumbnail(url = url)
            }
            // Video placeholders
            items(count = videoUrls.size) {
                VideoThumbnail()
            }
        }
    }
}

@Composable
private fun MediaThumbnail(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "Property photo",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(KPNColors.Border)
    )
}

@Composable
private fun VideoThumbnail() {
    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(KPNColors.AccentGreen.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.PlayCircle,
            contentDescription = "Video",
            tint = KPNColors.AccentGreen,
            modifier = Modifier.size(32.dp)
        )
    }
}

// ─────────────────────────────────────────────────
// Section content composables
// ─────────────────────────────────────────────────

@Composable
private fun StoreSpecsSection(s: StoreSpecs) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        KPNKeyValueRow("Site Status", s.siteStatus.displayLabel())
        KPNKeyValueRow("Road Facing", s.roadFacing.name.replace("_", " ").titleCase())
        KPNKeyValueRow("Total Area", "${s.totalArea} sqft")
        KPNKeyValueRow("Carpet Area", "${s.carpetArea} sqft")
        if (s.storeFrontage > 0f) KPNKeyValueRow("Store Frontage", "${s.storeFrontage} ft")
        if (s.facadeFrontage > 0f) KPNKeyValueRow("Facade Frontage", "${s.facadeFrontage} ft")
        if (s.storeDimensions.isNotBlank()) KPNKeyValueRow("Dimensions", s.storeDimensions)
        if (s.stepsToEntry > 0) KPNKeyValueRow("Steps to Entry", "${s.stepsToEntry}")
        if (s.ceilingHeight > 0f) KPNKeyValueRow("Ceiling Height", "${s.ceilingHeight} ft")
        if (s.floors.isNotEmpty()) KPNKeyValueRow("Floors", s.floors.joinToString(", ") { it.name.titleCase() })
        if (s.infrastructure.isNotEmpty()) KPNKeyValueRow("Infrastructure", s.infrastructure.joinToString(", ") { it.displayLabel() })
        KPNKeyValueRow("Signage", s.signageAvailability.name.replace("_", " ").titleCase())
        if (s.juiceCounterAvailable) {
            KPNKeyValueRow("Juice Counter", if (s.juiceCounterArea != null) "${s.juiceCounterArea} sqft" else "Yes")
        }
        if (s.powerLoad != null) KPNKeyValueRow("Power Load", "${s.powerLoad} KVA")
        if (s.carParking > 0 || s.bikeParking > 0) {
            KPNKeyValueRow("Parking", "Car: ${s.carParking}  Bike: ${s.bikeParking}")
        }
    }
}

@Composable
private fun RoadAccessSection(r: RoadAccess) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        KPNKeyValueRow("Front Road Width", "${r.frontRoadWidth} ft")
        KPNKeyValueRow("Road Type", r.roadType.name.replace("_", " ").titleCase())
        if (r.roadConnectivity.isNotEmpty()) {
            KPNKeyValueRow("Connectivity", r.roadConnectivity.joinToString(", ") { it.name.replace("_", " ").titleCase() })
        }
        KPNKeyValueRow("Parking Available", if (r.parkingAvailable) "Yes" else "No")
        KPNKeyValueRow("Delivery Access", r.deliveryVehicleAccess.name.titleCase())
        KPNKeyValueRow("Car Parking", "${r.carParkingCount} spots")
        KPNKeyValueRow("Bike Parking", "${r.bikeParkingCount} spots")
    }
}

@Composable
private fun CommercialsSection(c: Commercials) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        KPNKeyValueRow("Landlord Rent", "₹${c.landlordRentPerSqft}/sqft")
        if (c.bdOfferedRentPerSqft > 0f) KPNKeyValueRow("BD Offered Rent", "₹${c.bdOfferedRentPerSqft}/sqft")
        if (c.totalMonthlyRentAsk > 0L) KPNKeyValueRow("Total Ask (monthly)", "₹${c.totalMonthlyRentAsk}")
        if (c.totalMonthlyRentOffered > 0L) KPNKeyValueRow("Total Offered (monthly)", "₹${c.totalMonthlyRentOffered}")
        if (c.rrr > 0f) KPNKeyValueRow("RRR", "${formatPercent(c.rrr)}%")
        if (c.revenueEstimate > 0L) KPNKeyValueRow("Revenue Estimate", "₹${c.revenueEstimate}/month")
        KPNKeyValueRow("Lease Term", "${c.leaseTermYears} years")
        KPNKeyValueRow("Escalation", "${c.escalationPercent}% / ${c.escalationFrequencyYears}yr")
        if (c.securityDepositMonths > 0) KPNKeyValueRow("Security Deposit", "${c.securityDepositMonths} months")
        if (c.rentFreePeriodDays > 0) KPNKeyValueRow("Rent Free Period", "${c.rentFreePeriodDays} days")
        if (c.lesseeLockInYears > 0) KPNKeyValueRow("Lessee Lock-in", "${c.lesseeLockInYears} years")
        KPNKeyValueRow("Lessor Lock-in", c.lessorLockIn)
        KPNKeyValueRow("Registration Fees", c.registrationFees.name.replace("_", " ").titleCase())
        if (c.possessionDate != null) KPNKeyValueRow("Possession Date", epochMsToDateString(c.possessionDate))
        if (c.openingMonthAuto != null) KPNKeyValueRow("Opening Month", epochMsToDateString(c.openingMonthAuto))
        if (c.openingQuarter != null) KPNKeyValueRow("Opening Quarter", c.openingQuarter)
        if (!c.deviationFromStdTerms.isNullOrBlank()) {
            KPNKeyValueRow(
                label = "Deviation Notes",
                value = c.deviationFromStdTerms,
                valueColor = KPNColors.AccentOrange
            )
        }
    }
}

@Composable
private fun CompetitorsSection(competitors: List<Competitor>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        competitors.forEachIndexed { i, comp ->
            Column {
                if (i > 0) HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))
                Text(
                    text = "${i + 1}. ${comp.brandName.ifBlank { "Competitor ${i + 1}" }}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = KPNColors.TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                KPNKeyValueRow("Distance", "${comp.distanceMeters}m")
                if (comp.storeAreaSqft > 0) KPNKeyValueRow("Store Area", "${comp.storeAreaSqft} sqft")
                if (comp.rentPerSqft != null) KPNKeyValueRow("Rent/sqft", "₹${comp.rentPerSqft}")
                if (comp.salesPerMonth != null) KPNKeyValueRow("Sales/month", "₹${comp.salesPerMonth}")
            }
        }
    }
}

@Composable
private fun ContactSection(c: ContactInfo) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        KPNKeyValueRow("Source", c.source.name.replace("_", " ").titleCase())
        KPNKeyValueRow("Landlord", c.landlordName.ifBlank { "—" })
        KPNKeyValueRow("Phone", c.landlordPhone.ifBlank { "—" })
        if (!c.landlordEmail.isNullOrBlank()) KPNKeyValueRow("Email", c.landlordEmail)
        if (!c.brokerName.isNullOrBlank()) KPNKeyValueRow("Broker", c.brokerName)
        if (!c.brokerPhone.isNullOrBlank()) KPNKeyValueRow("Broker Phone", c.brokerPhone)

        if (c.landlordPhone.length == 10) {
            Spacer(Modifier.height(8.dp))
            WhatsAppButton(
                phone = c.landlordPhone,
                onClick = {},
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────
// Approvals tab
// ─────────────────────────────────────────────────

@Composable
private fun ApprovalsTab(approvalChain: List<ApprovalEvent>, status: PropertyStatus) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Approval Trail",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = KPNColors.TextPrimary
                )
                StatusPill(status = status)
            }
        }

        if (approvalChain.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = KPNColors.TextSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            "No approval events yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = KPNColors.TextSecondary
                        )
                    }
                }
            }
        }

        itemsIndexed(approvalChain) { index, event ->
            ApprovalEventRow(event = event, isLast = index == approvalChain.lastIndex)
        }
    }
}

@Composable
private fun ApprovalEventRow(event: ApprovalEvent, isLast: Boolean) {
    val actionColor = when (event.action) {
        ApprovalAction.APPROVE -> KPNColors.AccentGreen
        ApprovalAction.REJECT -> KPNColors.AccentRed
        ApprovalAction.FORWARD -> KPNColors.AccentOrange
        ApprovalAction.REQUEST_REVISION -> KPNColors.AccentOrange
        ApprovalAction.SUBMIT -> KPNColors.TextSecondary
    }
    val actionIcon = when (event.action) {
        ApprovalAction.APPROVE -> Icons.Default.CheckCircle
        ApprovalAction.REJECT -> Icons.Default.Cancel
        ApprovalAction.FORWARD -> Icons.Default.Forward
        ApprovalAction.REQUEST_REVISION -> Icons.Default.Edit
        ApprovalAction.SUBMIT -> Icons.Default.Send
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Timeline dot + line
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(actionColor.copy(alpha = 0.12f), KPNRadius.chip)
                    .border(1.dp, actionColor, KPNRadius.chip),
                contentAlignment = Alignment.Center
            ) {
                Icon(actionIcon, contentDescription = null, tint = actionColor, modifier = Modifier.size(16.dp))
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .background(KPNColors.Border)
                )
            }
        }

        // Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.action.name.replace("_", " ").titleCase(),
                    style = MaterialTheme.typography.titleSmall,
                    color = actionColor,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = epochMsToDateString(event.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = KPNColors.TextSecondary
                )
            }
            Text(
                text = "By: ${event.actorId} (${event.actorRole})",
                style = MaterialTheme.typography.bodyMedium,
                color = KPNColors.TextSecondary
            )
            if (!event.remarks.isNullOrBlank()) {
                Text(
                    text = "\"${event.remarks}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KPNColors.TextPrimary
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Stub for unimplemented tabs
// ─────────────────────────────────────────────────

@Composable
private fun DetailTabStub(title: String, subtitle: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                Icons.Default.Construction,
                contentDescription = null,
                tint = KPNColors.Primary,
                modifier = Modifier.size(48.dp)
            )
            Text(title, style = MaterialTheme.typography.titleMedium, color = KPNColors.TextPrimary, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = KPNColors.TextSecondary)
        }
    }
}

// ─────────────────────────────────────────────────
// Layout helper
// ─────────────────────────────────────────────────

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
        elevation = CardDefaults.cardElevation(KPNElevation.card)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
            content()
        }
    }
}

// ─────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────

private fun String.titleCase(): String =
    split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }

private fun formatPercent(value: Float): String {
    val int = (value * 10).toInt()
    val whole = int / 10
    val frac = int % 10
    return if (frac == 0) "$whole" else "$whole.$frac"
}

private fun daysSince(epochMs: Long): Int {
    if (epochMs == 0L) return 0
    val nowEstimateMs = 1_700_000_000_000L // approximate, actual impl via currentTimeMillis in Task 12
    val diff = nowEstimateMs - epochMs
    return (diff / (24L * 60 * 60 * 1000)).toInt().coerceAtLeast(0)
}

private fun SiteStatus.displayLabel() = name.replace("_", " ")
    .split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }

private fun InfraType.displayLabel() = name.replace("_", " ")
    .split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }
