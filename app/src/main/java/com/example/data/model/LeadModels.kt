package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jobId: Long = 0,
    val entityType: String = "INSURANCE", // "INSURANCE", "SCHOOL", "CUSTOM"
    val name: String = "",
    val location: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val website: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val category: String = "", // e.g. "Commercial & Property", "Public High School", "Life & Health"
    val licenseOrRegistrationNo: String = "",
    val contactPerson: String = "", // Principal, Superintendent, Agency Principal, Broker
    val ratingOrAccreditation: String = "", // "A+ (AM Best)", "State Board Verified", "4.8★"
    val leadQualityScore: Int = 85, // 0-100 score
    val scrapedFieldsSummary: String = "",
    val isBookmarked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "extraction_jobs")
data class ExtractionJobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val entityType: String,
    val targetLocation: String,
    val filtersApplied: String,
    val totalLeadsFound: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "COMPLETED" // "COMPLETED", "IN_PROGRESS", "FAILED"
)

data class ScrapingFilterConfig(
    val extractName: Boolean = true,
    val extractLocation: Boolean = true,
    val extractWebsite: Boolean = true,
    val extractAddress: Boolean = true,
    val extractEmail: Boolean = true,
    val extractPhone: Boolean = true,
    val extractLicenseRating: Boolean = true,
    val extractExecutive: Boolean = true,
    val leadsRange: Int = 15 // Range 0 to 30 leads selection
) {
    fun activeFieldCount(): Int {
        var count = 0
        if (extractName) count++
        if (extractLocation) count++
        if (extractWebsite) count++
        if (extractAddress) count++
        if (extractEmail) count++
        if (extractPhone) count++
        if (extractLicenseRating) count++
        if (extractExecutive) count++
        return count
    }

    fun toSummary(): String {
        val list = mutableListOf<String>()
        if (extractName) list.add("Name")
        if (extractLocation) list.add("Location")
        if (extractWebsite) list.add("Website")
        if (extractAddress) list.add("Address")
        if (extractEmail) list.add("Email")
        if (extractPhone) list.add("Phone")
        if (extractLicenseRating) list.add("License/Rating")
        if (extractExecutive) list.add("Executive")
        return list.joinToString(", ")
    }
}

enum class LeadEntityType(val displayName: String, val tag: String) {
    INSURANCE("Insurance Companies", "INSURANCE"),
    SCHOOL("Registered Schools", "SCHOOL"),
    ALL("All Categories", "ALL")
}

enum class ExportFormat(val extension: String, val displayName: String, val mimeType: String) {
    CSV("csv", "CSV Spreadsheet (.csv)", "text/csv"),
    EXCEL("xls", "Excel Workbook (.xls)", "application/vnd.ms-excel"),
    PDF("pdf", "Formatted PDF Report (.pdf)", "application/pdf")
}

data class UserProfile(
    val email: String = "chaudharyjyoti0754@gmail.com",
    val displayName: String = "Jyoti Chaudhary",
    val planName: String = "Enterprise Pro & Research Tier",
    val apiCreditsUsed: Int = 14,
    val apiCreditsMax: Int = 100,
    val isFreeKeyProcessed: Boolean = true,
    val customApiKey: String = "",
    val totalExports: Int = 6,
    val joinDate: String = "August 2026",
    val isLoggedIn: Boolean = true
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedPrompts: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ScrapedLeadDto(
    val name: String? = null,
    val location: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val website: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val category: String? = null,
    val licenseOrRegistrationNo: String? = null,
    val contactPerson: String? = null,
    val ratingOrAccreditation: String? = null,
    val leadQualityScore: Int? = null
)
