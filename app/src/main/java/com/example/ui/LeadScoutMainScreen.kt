package com.example.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LeadEntityType
import com.example.ui.components.AIChatbotPanel
import com.example.ui.components.ApkInstallAndShareDialog
import com.example.ui.components.AuthDialog
import com.example.ui.components.DashboardStatsBar
import com.example.ui.components.LeadCard
import com.example.ui.components.LeadDetailDialog
import com.example.ui.components.ReportExportDialog
import com.example.ui.components.ScrapingFilterSheet
import com.example.ui.components.UsageLimitsDialog
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.SecondaryLight
import com.example.ui.theme.TertiaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadScoutMainScreen(
    viewModel: LeadScoutViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val leads by viewModel.filteredLeads.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val showOnlyBookmarked by viewModel.showOnlyBookmarked.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    val targetEntityType by viewModel.targetEntityType.collectAsStateWithLifecycle()
    val locationInput by viewModel.locationInput.collectAsStateWithLifecycle()
    val filterConfig by viewModel.filterConfig.collectAsStateWithLifecycle()

    val isExtracting by viewModel.isExtracting.collectAsStateWithLifecycle()
    val extractionProgressText by viewModel.extractionProgressText.collectAsStateWithLifecycle()

    val isChatDrawerOpen by viewModel.isChatDrawerOpen.collectAsStateWithLifecycle()
    val isExportDialogOpen by viewModel.isExportDialogOpen.collectAsStateWithLifecycle()
    val isAuthDialogOpen by viewModel.isAuthDialogOpen.collectAsStateWithLifecycle()
    val isQuotaDialogOpen by viewModel.isQuotaDialogOpen.collectAsStateWithLifecycle()
    val isFilterSheetOpen by viewModel.isFilterSheetOpen.collectAsStateWithLifecycle()
    val isApkInstallDialogOpen by viewModel.isApkInstallDialogOpen.collectAsStateWithLifecycle()
    val selectedLeadForDetail by viewModel.selectedLeadForDetail.collectAsStateWithLifecycle()
    val userFeedback by viewModel.userFeedback.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userFeedback) {
        userFeedback?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 840.dp

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF1E3A8A), Color(0xFF2563EB))
                                        ),
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "LeadScout AI",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Insurance & School Lead Extractor",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    },
                    actions = {
                        // Refresh Button (fetches new leads excluding past ones)
                        IconButton(
                            onClick = { viewModel.refreshNewLeads() },
                            enabled = !isExtracting
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh New Leads",
                                tint = PrimaryLight
                            )
                        }

                        // Install & Share APK Hub Button
                        IconButton(onClick = { viewModel.setApkInstallDialogOpen(true) }) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = Color(0xFF10B981)
                                    ) {
                                        Text("APK")
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Android,
                                    contentDescription = "Install & Share APK Hub",
                                    tint = Color(0xFF059669)
                                )
                            }
                        }

                        // Export / Download Button
                        IconButton(onClick = { viewModel.setExportDialogOpen(true) }) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = "Export Report",
                                tint = PrimaryLight
                            )
                        }

                        // Usage Quota Button
                        IconButton(onClick = { viewModel.setQuotaDialogOpen(true) }) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = if (userProfile.apiCreditsUsed > 80) AmberAccent else TertiaryLight
                                    ) {
                                        Text("${userProfile.apiCreditsMax - userProfile.apiCreditsUsed}")
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Speed,
                                    contentDescription = "Usage Quota",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Mail Authentication / Account
                        IconButton(onClick = { viewModel.setAuthDialogOpen(true) }) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "Account Profile",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Mobile Chat Drawer Toggle
                        if (!isWideScreen) {
                            IconButton(onClick = { viewModel.setChatDrawerOpen(true) }) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = PrimaryLight) {
                                            Text("AI")
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.QuestionAnswer,
                                        contentDescription = "AI Copilot",
                                        tint = PrimaryLight
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.setFilterSheetOpen(true) },
                    containerColor = PrimaryLight,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Tune, contentDescription = null) },
                    text = { Text("Extract Leads") }
                )
            }
        ) { innerPadding ->
            if (isWideScreen) {
                // Wide Screen Split View: 60% Dashboard & List, 40% AI Chatbot on the Right Side!
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Left 60%: Leads Feed & Controls
                    Column(
                        modifier = Modifier
                            .weight(1.4f)
                            .fillMaxHeight()
                    ) {
                        MainFeedContent(
                            leads = leads,
                            searchQuery = searchQuery,
                            onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                            selectedTab = selectedTab,
                            onTabSelected = { viewModel.onTabSelected(it) },
                            showOnlyBookmarked = showOnlyBookmarked,
                            onToggleBookmarkFilter = { viewModel.toggleBookmarkFilter() },
                            userProfile = userProfile,
                            isExtracting = isExtracting,
                            extractionProgressText = extractionProgressText,
                            onRefreshNewLeads = { viewModel.refreshNewLeads() },
                            onHarvestDailyBatch = { viewModel.harvestDailyBatchLeads() },
                            onOpenQuota = { viewModel.setQuotaDialogOpen(true) },
                            onOpenFilterSheet = { viewModel.setFilterSheetOpen(true) },
                            onOpenApkHub = { viewModel.setApkInstallDialogOpen(true) },
                            onToggleLeadBookmark = { viewModel.toggleLeadBookmark(it) },
                            onSelectLead = { viewModel.selectLeadForDetail(it) }
                        )
                    }

                    // Right 40%: Persistent AI Chatbot Panel
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        AIChatbotPanel(
                            messages = chatMessages,
                            isLoading = isChatLoading,
                            onSendMessage = { viewModel.askChatbot(it) },
                            onClearChat = { viewModel.clearChat() }
                        )
                    }
                }
            } else {
                // Mobile Handheld Single Pane
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    MainFeedContent(
                        leads = leads,
                        searchQuery = searchQuery,
                        onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        selectedTab = selectedTab,
                        onTabSelected = { viewModel.onTabSelected(it) },
                        showOnlyBookmarked = showOnlyBookmarked,
                        onToggleBookmarkFilter = { viewModel.toggleBookmarkFilter() },
                        userProfile = userProfile,
                        isExtracting = isExtracting,
                        extractionProgressText = extractionProgressText,
                        onRefreshNewLeads = { viewModel.refreshNewLeads() },
                        onHarvestDailyBatch = { viewModel.harvestDailyBatchLeads() },
                        onOpenQuota = { viewModel.setQuotaDialogOpen(true) },
                        onOpenFilterSheet = { viewModel.setFilterSheetOpen(true) },
                        onOpenApkHub = { viewModel.setApkInstallDialogOpen(true) },
                        onToggleLeadBookmark = { viewModel.toggleLeadBookmark(it) },
                        onSelectLead = { viewModel.selectLeadForDetail(it) }
                    )
                }
            }
        }

        // Mobile Bottom Sheet for AI Chatbot
        if (!isWideScreen && isChatDrawerOpen) {
            val chatSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
            ModalBottomSheet(
                onDismissRequest = { viewModel.setChatDrawerOpen(false) },
                sheetState = chatSheetState,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Box(modifier = Modifier.fillMaxHeight(0.85f)) {
                    AIChatbotPanel(
                        messages = chatMessages,
                        isLoading = isChatLoading,
                        onSendMessage = { viewModel.askChatbot(it) },
                        onClearChat = { viewModel.clearChat() },
                        onClose = { viewModel.setChatDrawerOpen(false) }
                    )
                }
            }
        }

        // APK Installer & Sharing Hub Dialog
        if (isApkInstallDialogOpen) {
            ApkInstallAndShareDialog(
                userProfile = userProfile,
                onDismiss = { viewModel.setApkInstallDialogOpen(false) },
                onTestApiKey = { viewModel.testApiKey(it) },
                onSaveApiKey = { viewModel.updateCustomApiKey(it) }
            )
        }

        // Extraction Filter Configuration Bottom Sheet
        if (isFilterSheetOpen) {
            ScrapingFilterSheet(
                targetEntityType = targetEntityType,
                onEntityTypeSelected = { viewModel.setTargetEntityType(it) },
                locationInput = locationInput,
                onLocationInputChanged = { viewModel.onLocationInputChanged(it) },
                filterConfig = filterConfig,
                onFilterConfigChanged = { viewModel.updateFilterConfig(it) },
                onDismiss = { viewModel.setFilterSheetOpen(false) },
                onStartScraping = { viewModel.startExtraction() }
            )
        }

        // Export Report Dialog
        if (isExportDialogOpen) {
            ReportExportDialog(
                totalLeadsCount = leads.size,
                filterConfig = filterConfig,
                onDismiss = { viewModel.setExportDialogOpen(false) },
                onConfirmExport = { format ->
                    viewModel.exportAndShareReport(format) { shareIntent ->
                        try {
                            context.startActivity(shareIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Ready to share report", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        // Usage Limits & Quota Dialog
        if (isQuotaDialogOpen) {
            UsageLimitsDialog(
                userProfile = userProfile,
                onDismiss = { viewModel.setQuotaDialogOpen(false) },
                onResetQuota = { viewModel.resetApiQuota() },
                onSaveCustomKey = { viewModel.updateCustomApiKey(it) }
            )
        }

        // Mail Auth & Account Profile Dialog
        if (isAuthDialogOpen) {
            AuthDialog(
                userProfile = userProfile,
                onDismiss = { viewModel.setAuthDialogOpen(false) },
                onLogin = { email, name -> viewModel.updateLogin(email, name) }
            )
        }

        // Lead Detail Dialog
        selectedLeadForDetail?.let { lead ->
            LeadDetailDialog(
                lead = lead,
                onDismiss = { viewModel.selectLeadForDetail(null) },
                onToggleBookmark = { viewModel.toggleLeadBookmark(lead) },
                onDelete = {
                    viewModel.deleteLead(lead)
                    viewModel.selectLeadForDetail(null)
                }
            )
        }
    }
}

@Composable
private fun MainFeedContent(
    leads: List<com.example.data.model.LeadEntity>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    selectedTab: LeadEntityType,
    onTabSelected: (LeadEntityType) -> Unit,
    showOnlyBookmarked: Boolean,
    onToggleBookmarkFilter: () -> Unit,
    userProfile: com.example.data.model.UserProfile,
    isExtracting: Boolean,
    extractionProgressText: String,
    onRefreshNewLeads: () -> Unit,
    onHarvestDailyBatch: () -> Unit,
    onOpenQuota: () -> Unit,
    onOpenFilterSheet: () -> Unit,
    onOpenApkHub: () -> Unit,
    onToggleLeadBookmark: (com.example.data.model.LeadEntity) -> Unit,
    onSelectLead: (com.example.data.model.LeadEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search and Bookmark Filter Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = { Text("Search by name, city, zip, category...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryLight,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onToggleBookmarkFilter,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (showOnlyBookmarked) AmberAccent.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = if (showOnlyBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmarks",
                    tint = if (showOnlyBookmarked) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Category Tabs
        val tabs = listOf(LeadEntityType.ALL, LeadEntityType.INSURANCE, LeadEntityType.SCHOOL)
        val selectedIndex = tabs.indexOf(selectedTab)

        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = PrimaryLight
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (tab) {
                                LeadEntityType.ALL -> Icons.Default.FilterList
                                LeadEntityType.INSURANCE -> Icons.Default.Business
                                LeadEntityType.SCHOOL -> Icons.Default.School
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.displayName,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    },
                    selectedContentColor = PrimaryLight,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Live Dashboard KPIs
        DashboardStatsBar(
            leads = leads,
            userProfile = userProfile,
            onOpenQuota = onOpenQuota
        )

        // Refresh & Daily Quota Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Refresh Button
            OutlinedButton(
                onClick = onRefreshNewLeads,
                enabled = !isExtracting,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryLight
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryLight.copy(alpha = 0.5f)),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Refresh Leads", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            // Daily 70-100 Harvest Button
            Button(
                onClick = onHarvestDailyBatch,
                enabled = !isExtracting,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E3A8A)
                ),
                modifier = Modifier
                    .weight(1.3f)
                    .height(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Daily 70-100 Leads", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Install Hub & Share Banner
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF064E3B).copy(alpha = 0.08f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFF10B981), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Streamlit Web & Mobile Launch Hub",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46)
                        )
                        Text(
                            text = "Run on Windows, Mac, iPhone & Android browsers",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }

                Button(
                    onClick = onOpenApkHub,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Launch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Extraction Progress Banner
        AnimatedVisibility(
            visible = isExtracting,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryLight.copy(alpha = 0.12f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color = PrimaryLight
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI Web Research in Progress",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryLight
                        )
                        Text(
                            text = extractionProgressText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Leads List
        if (leads.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (showOnlyBookmarked) "No Bookmarked Leads Found" else "No Leads Extracted Yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (showOnlyBookmarked) "Bookmark leads by tapping the star/bookmark icon on any lead card." else "Tap 'Extract Leads' below to target insurance companies or schools by city, state, or zip code.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 80.dp)
            ) {
                items(leads, key = { it.id }) { lead ->
                    LeadCard(
                        lead = lead,
                        onToggleBookmark = { onToggleLeadBookmark(lead) },
                        onSelect = { onSelectLead(lead) }
                    )
                }
            }
        }
    }
}
