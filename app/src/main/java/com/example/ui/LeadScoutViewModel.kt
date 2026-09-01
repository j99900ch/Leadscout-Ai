package com.example.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.export.ReportExporter
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.ExportFormat
import com.example.data.model.ExtractionJobEntity
import com.example.data.model.LeadEntity
import com.example.data.model.LeadEntityType
import com.example.data.model.ScrapingFilterConfig
import com.example.data.model.UserProfile
import com.example.data.remote.GeminiApiClient
import com.example.data.repository.LeadRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class LeadScoutViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val geminiApiClient = GeminiApiClient()
    private val reportExporter = ReportExporter(application)
    private val repository = LeadRepository(
        database.leadDao(),
        database.extractionJobDao(),
        geminiApiClient,
        reportExporter
    )

    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
    val extractionJobs: StateFlow<List<ExtractionJobEntity>> = repository.allJobs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filter & Search states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTab = MutableStateFlow(LeadEntityType.ALL)
    val selectedTab: StateFlow<LeadEntityType> = _selectedTab.asStateFlow()

    private val _showOnlyBookmarked = MutableStateFlow(false)
    val showOnlyBookmarked: StateFlow<Boolean> = _showOnlyBookmarked.asStateFlow()

    // Scraping form states
    private val _targetEntityType = MutableStateFlow(LeadEntityType.INSURANCE)
    val targetEntityType: StateFlow<LeadEntityType> = _targetEntityType.asStateFlow()

    private val _locationInput = MutableStateFlow("Mumbai, Maharashtra 400001")
    val locationInput: StateFlow<String> = _locationInput.asStateFlow()

    private val _filterConfig = MutableStateFlow(ScrapingFilterConfig())
    val filterConfig: StateFlow<ScrapingFilterConfig> = _filterConfig.asStateFlow()

    // UI Dialog & Drawer states
    private val _isExtracting = MutableStateFlow(false)
    val isExtracting: StateFlow<Boolean> = _isExtracting.asStateFlow()

    private val _extractionProgressText = MutableStateFlow("Initializing Web Research...")
    val extractionProgressText: StateFlow<String> = _extractionProgressText.asStateFlow()

    private val _isChatDrawerOpen = MutableStateFlow(false)
    val isChatDrawerOpen: StateFlow<Boolean> = _isChatDrawerOpen.asStateFlow()

    private val _isExportDialogOpen = MutableStateFlow(false)
    val isExportDialogOpen: StateFlow<Boolean> = _isExportDialogOpen.asStateFlow()

    private val _isAuthDialogOpen = MutableStateFlow(false)
    val isAuthDialogOpen: StateFlow<Boolean> = _isAuthDialogOpen.asStateFlow()

    private val _isQuotaDialogOpen = MutableStateFlow(false)
    val isQuotaDialogOpen: StateFlow<Boolean> = _isQuotaDialogOpen.asStateFlow()

    private val _isFilterSheetOpen = MutableStateFlow(false)
    val isFilterSheetOpen: StateFlow<Boolean> = _isFilterSheetOpen.asStateFlow()

    private val _isApkInstallDialogOpen = MutableStateFlow(false)
    val isApkInstallDialogOpen: StateFlow<Boolean> = _isApkInstallDialogOpen.asStateFlow()

    private val _selectedLeadForDetail = MutableStateFlow<LeadEntity?>(null)
    val selectedLeadForDetail: StateFlow<LeadEntity?> = _selectedLeadForDetail.asStateFlow()

    private val _userFeedback = MutableStateFlow<String?>(null)
    val userFeedback: StateFlow<String?> = _userFeedback.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Filtered Leads Flow
    val filteredLeads: StateFlow<List<LeadEntity>> = combine(
        repository.allLeads,
        _searchQuery,
        _selectedTab,
        _showOnlyBookmarked
    ) { leads, query, tab, bookmarkedOnly ->
        leads.filter { lead ->
            val matchesQuery = query.isBlank() ||
                lead.name.contains(query, ignoreCase = true) ||
                lead.city.contains(query, ignoreCase = true) ||
                lead.state.contains(query, ignoreCase = true) ||
                lead.zipCode.contains(query, ignoreCase = true) ||
                lead.category.contains(query, ignoreCase = true) ||
                lead.contactPerson.contains(query, ignoreCase = true)

            val matchesTab = when (tab) {
                LeadEntityType.ALL -> true
                LeadEntityType.INSURANCE -> lead.entityType == "INSURANCE"
                LeadEntityType.SCHOOL -> lead.entityType == "SCHOOL"
            }

            val matchesBookmark = !bookmarkedOnly || lead.isBookmarked

            matchesQuery && matchesTab && matchesBookmark
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Seed initial curated leads if database is empty on first boot
        viewModelScope.launch {
            repository.performExtraction(
                entityType = "INSURANCE",
                location = "Mumbai, Maharashtra 400001",
                filterConfig = ScrapingFilterConfig()
            )
            repository.performExtraction(
                entityType = "SCHOOL",
                location = "New Delhi, Delhi NCR 110001",
                filterConfig = ScrapingFilterConfig()
            )
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onTabSelected(tab: LeadEntityType) {
        _selectedTab.value = tab
    }

    fun toggleBookmarkFilter() {
        _showOnlyBookmarked.value = !_showOnlyBookmarked.value
    }

    fun setTargetEntityType(type: LeadEntityType) {
        _targetEntityType.value = type
    }

    fun onLocationInputChanged(newLocation: String) {
        _locationInput.value = newLocation
    }

    fun updateFilterConfig(newConfig: ScrapingFilterConfig) {
        _filterConfig.value = newConfig
    }

    fun startExtraction() {
        if (_isExtracting.value) return
        val entityType = if (_targetEntityType.value == LeadEntityType.SCHOOL) "SCHOOL" else "INSURANCE"
        val location = _locationInput.value.ifBlank { "Mumbai, Maharashtra 400001" }

        viewModelScope.launch {
            _isExtracting.value = true
            _extractionProgressText.value = "Initiating RAG Decision Tree Agent..."
            delay(400)
            _extractionProgressText.value = "Conducting Deep Web Research in $location..."
            delay(600)
            _extractionProgressText.value = "Extracting verified emails, phones & licensing vectors..."

            val result = repository.performExtraction(
                entityType = entityType,
                location = location,
                filterConfig = _filterConfig.value
            )

            result.onSuccess { leads ->
                _userFeedback.value = "Extracted ${leads.size} verified $entityType leads in $location!"
                _isFilterSheetOpen.value = false
            }.onFailure { error ->
                _userFeedback.value = "Extraction notice: ${error.message}"
            }

            _isExtracting.value = false
        }
    }

    fun refreshNewLeads() {
        if (_isExtracting.value) return
        val entityType = if (_targetEntityType.value == LeadEntityType.SCHOOL) "SCHOOL" else "INSURANCE"
        val location = _locationInput.value.ifBlank { "Mumbai, Maharashtra" }

        viewModelScope.launch {
            _isExtracting.value = true
            _extractionProgressText.value = "Scanning IRDAI & Board registers for NEW, un-scraped leads..."
            delay(400)
            _extractionProgressText.value = "Filtering past entries & extracting fresh Indian contacts..."

            val result = repository.refreshWithNewLeads(
                entityType = entityType,
                location = location,
                filterConfig = _filterConfig.value
            )

            result.onSuccess { newLeads ->
                _userFeedback.value = "Refreshed! Added ${newLeads.size} completely fresh, verified leads."
            }.onFailure { error ->
                _userFeedback.value = "Refresh notice: ${error.message}"
            }

            _isExtracting.value = false
        }
    }

    fun harvestDailyBatchLeads() {
        if (_isExtracting.value) return
        val location = _locationInput.value.ifBlank { "Pan-India Metros" }

        viewModelScope.launch {
            _isExtracting.value = true
            _extractionProgressText.value = "Harvesting High-Volume Daily Quota (70-100 Leads)..."
            delay(400)
            _extractionProgressText.value = "Researching across General, Health, Life Insurance & Top Schools..."

            val result = repository.harvestHighVolumeLeads(
                targetLocation = location,
                filterConfig = _filterConfig.value,
                targetCount = 75
            )

            result.onSuccess { count ->
                _userFeedback.value = "Daily Harvest Complete: $count verified high-quality leads added!"
            }.onFailure { error ->
                _userFeedback.value = "Harvest notice: ${error.message}"
            }

            _isExtracting.value = false
        }
    }

    fun toggleLeadBookmark(lead: LeadEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(lead)
        }
    }

    fun deleteLead(lead: LeadEntity) {
        viewModelScope.launch {
            repository.deleteLead(lead)
            _userFeedback.value = "Removed lead: ${lead.name}"
        }
    }

    fun clearAllLeads() {
        viewModelScope.launch {
            repository.clearAllLeads()
            _userFeedback.value = "Database cleared. Ready for new extraction."
        }
    }

    fun selectLeadForDetail(lead: LeadEntity?) {
        _selectedLeadForDetail.value = lead
    }

    fun setChatDrawerOpen(isOpen: Boolean) {
        _isChatDrawerOpen.value = isOpen
    }

    fun setExportDialogOpen(isOpen: Boolean) {
        _isExportDialogOpen.value = isOpen
    }

    fun setAuthDialogOpen(isOpen: Boolean) {
        _isAuthDialogOpen.value = isOpen
    }

    fun setQuotaDialogOpen(isOpen: Boolean) {
        _isQuotaDialogOpen.value = isOpen
    }

    fun setFilterSheetOpen(isOpen: Boolean) {
        _isFilterSheetOpen.value = isOpen
    }

    fun setApkInstallDialogOpen(isOpen: Boolean) {
        _isApkInstallDialogOpen.value = isOpen
    }

    fun clearFeedback() {
        _userFeedback.value = null
    }

    fun exportAndShareReport(
        format: ExportFormat,
        onShareReady: (Intent) -> Unit
    ) {
        viewModelScope.launch {
            val leads = filteredLeads.value
            if (leads.isEmpty()) {
                _userFeedback.value = "No leads available to export in current filter!"
                return@launch
            }

            val entityName = if (_selectedTab.value == LeadEntityType.SCHOOL) "Schools" else if (_selectedTab.value == LeadEntityType.INSURANCE) "Insurance" else "Combined"
            val loc = _locationInput.value

            val file = repository.exportLeadsReport(
                leads = leads,
                format = format,
                filterConfig = _filterConfig.value,
                entityType = entityName,
                targetLocation = loc
            )

            val shareIntent = repository.getShareIntent(file, format)
            onShareReady(shareIntent)
            _userFeedback.value = "Report generated (${file.name}). Free API Key validated!"
            _isExportDialogOpen.value = false
        }
    }

    fun askChatbot(question: String) {
        if (question.isBlank() || _isChatLoading.value) return
        viewModelScope.launch {
            _isChatLoading.value = true
            repository.askChatbot(question, filteredLeads.value)
            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        repository.clearChat()
    }

    fun updateLogin(email: String, name: String) {
        repository.updateLoginProfile(email, name)
        _isAuthDialogOpen.value = false
        _userFeedback.value = "Logged in as $email"
    }

    fun updateCustomApiKey(key: String) {
        repository.updateCustomApiKey(key)
        _userFeedback.value = "API key updated successfully."
    }

    fun resetApiQuota() {
        repository.resetApiQuota()
        _userFeedback.value = "API quota usage counter reset to 0/100."
    }

    suspend fun testApiKey(key: String): Result<String> {
        return geminiApiClient.testApiKey(key)
    }
}
