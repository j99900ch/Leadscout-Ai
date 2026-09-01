package com.example.data.repository

import com.example.data.export.ReportExporter
import com.example.data.local.ExtractionJobDao
import com.example.data.local.LeadDao
import com.example.data.model.ChatMessage
import com.example.data.model.ExportFormat
import com.example.data.model.ExtractionJobEntity
import com.example.data.model.LeadEntity
import com.example.data.model.ScrapingFilterConfig
import com.example.data.model.UserProfile
import com.example.data.remote.GeminiApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class LeadRepository(
    private val leadDao: LeadDao,
    private val extractionJobDao: ExtractionJobDao,
    private val geminiApiClient: GeminiApiClient,
    private val reportExporter: ReportExporter
) {
    val allLeads: Flow<List<LeadEntity>> = leadDao.getAllLeads()
    val allJobs: Flow<List<ExtractionJobEntity>> = extractionJobDao.getAllJobs()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Hello! I am your LeadScout AI Analyst. I can query your scraped leads, calculate contact reachability, draft personalized outreach emails, or find top-rated insurance and school targets. How can I assist with your dataset today?",
                isUser = false,
                suggestedPrompts = listOf(
                    "Top 3 leads with highest quality score?",
                    "Draft cold outreach email for principals",
                    "How many verified phone numbers found?",
                    "Summarize commercial liability providers"
                )
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    suspend fun performExtraction(
        entityType: String,
        location: String,
        filterConfig: ScrapingFilterConfig
    ): Result<List<LeadEntity>> {
        val currentProfile = _userProfile.value
        if (currentProfile.apiCreditsUsed >= currentProfile.apiCreditsMax) {
            return Result.failure(Exception("API Credit limit reached (${currentProfile.apiCreditsMax}/${currentProfile.apiCreditsMax}). Please reset or enter custom API key in Dashboard."))
        }

        try {
            val existingNames = leadDao.getAllLeadNames()
            val filterSummary = filterConfig.toSummary()
            val rawDtos = geminiApiClient.researchLeads(
                entityType = entityType,
                location = location,
                filterSummary = filterSummary,
                customApiKey = currentProfile.customApiKey.ifBlank { null },
                batchCount = filterConfig.leadsRange.coerceIn(1, 30),
                excludedEntities = existingNames
            )

            // Convert to Room LeadEntity
            val entities = rawDtos.map { dto ->
                val websiteClean = if (dto.website.isNullOrBlank()) "" else dto.website.trim()
                val formattedWebsite = if (websiteClean.startsWith("http://", ignoreCase = true) || websiteClean.startsWith("https://", ignoreCase = true)) {
                    websiteClean
                } else if (websiteClean.isNotBlank() && !websiteClean.contains(" ")) {
                    "https://$websiteClean"
                } else websiteClean

                LeadEntity(
                    entityType = entityType,
                    name = if (filterConfig.extractName) dto.name ?: "Unknown Organization" else "Entity #${(100..999).random()}",
                    location = if (filterConfig.extractLocation) dto.location ?: location else location,
                    city = if (filterConfig.extractLocation) dto.city ?: "" else "",
                    state = if (filterConfig.extractLocation) dto.state ?: "" else "",
                    zipCode = if (filterConfig.extractLocation) dto.zipCode ?: "" else "",
                    website = if (filterConfig.extractWebsite) formattedWebsite else "[Filter Excluded]",
                    email = if (filterConfig.extractEmail) dto.email ?: "" else "[Filter Excluded]",
                    phone = if (filterConfig.extractPhone) dto.phone ?: "" else "[Filter Excluded]",
                    address = if (filterConfig.extractAddress) dto.address ?: "" else "[Filter Excluded]",
                    category = dto.category ?: if (entityType == "INSURANCE") "Commercial / Personal Lines" else "Registered Educational Institution",
                    licenseOrRegistrationNo = if (filterConfig.extractLicenseRating) dto.licenseOrRegistrationNo ?: "" else "",
                    contactPerson = if (filterConfig.extractExecutive) dto.contactPerson ?: "" else "",
                    ratingOrAccreditation = if (filterConfig.extractLicenseRating) dto.ratingOrAccreditation ?: "" else "",
                    leadQualityScore = dto.leadQualityScore ?: (85..98).random(),
                    scrapedFieldsSummary = filterSummary,
                    timestamp = System.currentTimeMillis()
                )
            }

            // Save to DB
            leadDao.insertLeads(entities)

            // Record Extraction Job
            val job = ExtractionJobEntity(
                title = "$entityType in $location",
                entityType = entityType,
                targetLocation = location,
                filtersApplied = filterSummary,
                totalLeadsFound = entities.size,
                status = "COMPLETED"
            )
            extractionJobDao.insertJob(job)

            // Update credit usage
            _userProfile.value = currentProfile.copy(
                apiCreditsUsed = currentProfile.apiCreditsUsed + 1
            )

            return Result.success(entities)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun refreshWithNewLeads(
        entityType: String,
        location: String,
        filterConfig: ScrapingFilterConfig
    ): Result<List<LeadEntity>> {
        val existingNames = leadDao.getAllLeadNames()
        val filterSummary = filterConfig.toSummary()

        try {
            val rawDtos = geminiApiClient.researchLeads(
                entityType = entityType,
                location = location,
                filterSummary = filterSummary,
                customApiKey = _userProfile.value.customApiKey.ifBlank { null },
                batchCount = filterConfig.leadsRange.coerceIn(1, 30),
                excludedEntities = existingNames
            )

            val newEntities = rawDtos.map { dto ->
                val websiteClean = if (dto.website.isNullOrBlank()) "" else dto.website.trim()
                val formattedWebsite = if (websiteClean.startsWith("http://", ignoreCase = true) || websiteClean.startsWith("https://", ignoreCase = true)) {
                    websiteClean
                } else if (websiteClean.isNotBlank() && !websiteClean.contains(" ")) {
                    "https://$websiteClean"
                } else websiteClean

                LeadEntity(
                    entityType = entityType,
                    name = if (filterConfig.extractName) dto.name ?: "Unknown Organization" else "Entity #${(100..999).random()}",
                    location = if (filterConfig.extractLocation) dto.location ?: location else location,
                    city = if (filterConfig.extractLocation) dto.city ?: "" else "",
                    state = if (filterConfig.extractLocation) dto.state ?: "" else "",
                    zipCode = if (filterConfig.extractLocation) dto.zipCode ?: "" else "",
                    website = if (filterConfig.extractWebsite) formattedWebsite else "[Filter Excluded]",
                    email = if (filterConfig.extractEmail) dto.email ?: "" else "[Filter Excluded]",
                    phone = if (filterConfig.extractPhone) dto.phone ?: "" else "[Filter Excluded]",
                    address = if (filterConfig.extractAddress) dto.address ?: "" else "[Filter Excluded]",
                    category = dto.category ?: if (entityType == "INSURANCE") "Commercial / Personal Lines" else "Registered Educational Institution",
                    licenseOrRegistrationNo = if (filterConfig.extractLicenseRating) dto.licenseOrRegistrationNo ?: "" else "",
                    contactPerson = if (filterConfig.extractExecutive) dto.contactPerson ?: "" else "",
                    ratingOrAccreditation = if (filterConfig.extractLicenseRating) dto.ratingOrAccreditation ?: "" else "",
                    leadQualityScore = dto.leadQualityScore ?: (88..99).random(),
                    scrapedFieldsSummary = filterSummary,
                    timestamp = System.currentTimeMillis()
                )
            }

            // Save new leads
            leadDao.insertLeads(newEntities)

            // Update credit usage
            _userProfile.value = _userProfile.value.copy(
                apiCreditsUsed = _userProfile.value.apiCreditsUsed + 1
            )

            return Result.success(newEntities)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun harvestHighVolumeLeads(
        targetLocation: String,
        filterConfig: ScrapingFilterConfig,
        targetCount: Int = 75
    ): Result<Int> {
        val currentProfile = _userProfile.value
        val existingNames = leadDao.getAllLeadNames().toMutableList()
        val filterSummary = filterConfig.toSummary()
        var totalHarvested = 0

        val sectors = listOf(
            "INSURANCE" to "Corporate Group Health & Life Insurance",
            "INSURANCE" to "IRDAI General Insurance & Marine Broker",
            "INSURANCE" to "Commercial Liability & Motor Fleet",
            "SCHOOL" to "CBSE Senior Secondary Schools",
            "SCHOOL" to "ICSE / ISC High Academies",
            "SCHOOL" to "IB World Schools & International Colleges"
        )

        for ((entityType, subSector) in sectors) {
            if (totalHarvested >= targetCount) break
            try {
                val segmentDtos = geminiApiClient.researchLeads(
                    entityType = entityType,
                    location = "$targetLocation ($subSector)",
                    filterSummary = filterSummary,
                    customApiKey = currentProfile.customApiKey.ifBlank { null },
                    batchCount = 12,
                    excludedEntities = existingNames
                )

                val entities = segmentDtos.map { dto ->
                    val websiteClean = if (dto.website.isNullOrBlank()) "" else dto.website.trim()
                    val formattedWebsite = if (websiteClean.startsWith("http://", ignoreCase = true) || websiteClean.startsWith("https://", ignoreCase = true)) {
                        websiteClean
                    } else if (websiteClean.isNotBlank() && !websiteClean.contains(" ")) {
                        "https://$websiteClean"
                    } else websiteClean

                    val name = dto.name ?: "Organization #${(100..999).random()}"
                    existingNames.add(name)

                    LeadEntity(
                        entityType = entityType,
                        name = name,
                        location = dto.location ?: targetLocation,
                        city = dto.city ?: "",
                        state = dto.state ?: "",
                        zipCode = dto.zipCode ?: "",
                        website = formattedWebsite,
                        email = dto.email ?: "",
                        phone = dto.phone ?: "",
                        address = dto.address ?: "",
                        category = dto.category ?: subSector,
                        licenseOrRegistrationNo = dto.licenseOrRegistrationNo ?: "",
                        contactPerson = dto.contactPerson ?: "",
                        ratingOrAccreditation = dto.ratingOrAccreditation ?: "",
                        leadQualityScore = dto.leadQualityScore ?: (88..99).random(),
                        scrapedFieldsSummary = "$filterSummary | $subSector",
                        timestamp = System.currentTimeMillis()
                    )
                }

                leadDao.insertLeads(entities)
                totalHarvested += entities.size
            } catch (e: Exception) {
                // Continue to next sector
            }
        }

        _userProfile.value = currentProfile.copy(
            apiCreditsUsed = currentProfile.apiCreditsUsed + 2
        )

        return Result.success(totalHarvested)
    }

    suspend fun exportLeadsReport(
        leads: List<LeadEntity>,
        format: ExportFormat,
        filterConfig: ScrapingFilterConfig,
        entityType: String,
        targetLocation: String
    ): File {
        val file = reportExporter.generateExportFile(
            leads = leads,
            format = format,
            filterConfig = filterConfig,
            entityType = entityType,
            targetLocation = targetLocation
        )

        // Increment export count
        val currentProfile = _userProfile.value
        _userProfile.value = currentProfile.copy(
            totalExports = currentProfile.totalExports + 1,
            isFreeKeyProcessed = true
        )

        return file
    }

    fun getShareIntent(file: File, format: ExportFormat) =
        reportExporter.createShareIntent(file, format)

    suspend fun toggleBookmark(lead: LeadEntity) {
        leadDao.updateLead(lead.copy(isBookmarked = !lead.isBookmarked))
    }

    suspend fun deleteLead(lead: LeadEntity) {
        leadDao.deleteLead(lead)
    }

    suspend fun clearAllLeads() {
        leadDao.clearAllLeads()
    }

    suspend fun askChatbot(question: String, activeLeads: List<LeadEntity>) {
        val userMsg = ChatMessage(
            text = question,
            isUser = true
        )
        _chatMessages.value = _chatMessages.value + userMsg

        val leadsSummary = if (activeLeads.isEmpty()) {
            "No active leads currently in view. Total DB records available."
        } else {
            activeLeads.take(8).joinToString("\n") { l ->
                "• [${l.entityType}] ${l.name} | City: ${l.city}, ${l.state} ${l.zipCode} | Email: ${l.email} | Phone: ${l.phone} | Category: ${l.category} | Contact: ${l.contactPerson} | Score: ${l.leadQualityScore}/100"
            }
        }

        val answer = geminiApiClient.askLeadAnalyst(
            question = question,
            contextSummary = leadsSummary,
            customApiKey = _userProfile.value.customApiKey.ifBlank { null }
        )

        val botMsg = ChatMessage(
            text = answer,
            isUser = false,
            suggestedPrompts = listOf(
                "Export this segment to Excel",
                "Draft outreach follow-up pitch",
                "Show contact completeness ranking"
            )
        )
        _chatMessages.value = _chatMessages.value + botMsg
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                text = "Chat history cleared. How can I help you analyze your extracted leads?",
                isUser = false,
                suggestedPrompts = listOf(
                    "Top 3 leads with highest quality score?",
                    "Draft cold outreach email for principals",
                    "Summarize contact details"
                )
            )
        )
    }

    fun updateLoginProfile(email: String, displayName: String) {
        _userProfile.value = _userProfile.value.copy(
            email = email,
            displayName = displayName,
            isLoggedIn = true
        )
    }

    fun updateCustomApiKey(key: String) {
        _userProfile.value = _userProfile.value.copy(
            customApiKey = key.trim(),
            isFreeKeyProcessed = true
        )
    }

    fun resetApiQuota() {
        _userProfile.value = _userProfile.value.copy(
            apiCreditsUsed = 0
        )
    }
}
