package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeadEntityType
import com.example.data.model.ScrapingFilterConfig
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.SecondaryLight
import com.example.ui.theme.TertiaryLight

enum class LocationCategoryTab(val title: String, val icon: String) {
    NCR_AND_UP("NCR & UP Towns/Villages", "🏙️"),
    MINI_STATES("Small & Mini States", "🏔️"),
    MAJOR_METROS("Major Metros", "🏛️"),
    ALL_STATES("All 28 States & UTs", "🇮🇳")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScrapingFilterSheet(
    targetEntityType: LeadEntityType,
    onEntityTypeSelected: (LeadEntityType) -> Unit,
    locationInput: String,
    onLocationInputChanged: (String) -> Unit,
    filterConfig: ScrapingFilterConfig,
    onFilterConfigChanged: (ScrapingFilterConfig) -> Unit,
    onDismiss: () -> Unit,
    onStartScraping: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedLocationTab by remember { mutableStateOf(LocationCategoryTab.NCR_AND_UP) }

    val upAndNcrLocations = listOf(
        "Jewar, Greater Noida, UP 203135",
        "Noida, Gautam Buddha Nagar, UP 201301",
        "Greater Noida, UP 201310",
        "Ghaziabad, UP 201001",
        "Gurugram, Delhi NCR 122001",
        "Faridabad, Delhi NCR 121001",
        "Meerut, UP 250001",
        "Muzaffarnagar, UP 251001",
        "Khatauli, Muzaffarnagar, UP 251201",
        "Sardhana, Meerut, UP 250342",
        "Mathura, UP 281001",
        "Aligarh, UP 202001",
        "Agra, UP 282001",
        "Firozabad, UP 283203",
        "Hapur, UP 245101",
        "Bulandshahr, UP 203001",
        "Khurja, Bulandshahr, UP 203131",
        "Shamli, UP 247776",
        "Baghpat, UP 250609",
        "Bareilly, UP 243001",
        "Moradabad, UP 244001",
        "Sambhal, UP 244302",
        "Rampur, UP 244901",
        "Varanasi, UP 221001",
        "Prayagraj (Allahabad), UP 211001",
        "Lucknow, UP 226001",
        "Kanpur, UP 208001",
        "Ayodhya, UP 224001",
        "Gorakhpur, UP 273001",
        "Jhansi, UP 284001",
        "Saharanpur, UP 247001",
        "Bijnor, UP 246701",
        "Sitapur, UP 261001",
        "Unnao, UP 209801",
        "Shahjahanpur, UP 242001",
        "Azamgarh, UP 276001",
        "Mirzapur, UP 231001",
        "Deoria, UP 274001",
        "Gonda, UP 271001",
        "Banda, UP 210001",
        "Chitrakoot, UP 210205",
        "Mainpuri, UP 205001",
        "Etah, UP 207001",
        "Hathras, UP 204101",
        "Etawah, UP 206001",
        "Kannauj, UP 209725",
        "Amroha, UP 244221",
        "Badaun, UP 243601",
        "Pilibhit, UP 262001"
    )

    val miniStatesLocations = listOf(
        "Panaji, Goa 403001",
        "Margao, Goa 403601",
        "Mapusa, Goa 403507",
        "Vasco da Gama, Goa 403802",
        "Gangtok, Sikkim 737101",
        "Namchi, Sikkim 737126",
        "Geyzing, Sikkim 737111",
        "Agartala, Tripura 799001",
        "Udaipur, Tripura 799120",
        "Shillong, Meghalaya 793001",
        "Tura, Meghalaya 794001",
        "Jowai, Meghalaya 793150",
        "Imphal, Manipur 795001",
        "Churachandpur, Manipur 795128",
        "Kohima, Nagaland 797001",
        "Dimapur, Nagaland 797112",
        "Mokokchung, Nagaland 798601",
        "Aizawl, Mizoram 796001",
        "Lunglei, Mizoram 796701",
        "Itanagar, Arunachal Pradesh 791111",
        "Naharlagun, Arunachal Pradesh 791110",
        "Pasighat, Arunachal Pradesh 791102",
        "Shimla, Himachal Pradesh 171001",
        "Dharamshala, Himachal Pradesh 176215",
        "Solan, Himachal Pradesh 173212",
        "Mandi, Himachal Pradesh 175001",
        "Kullu, Himachal Pradesh 175101",
        "Dehradun, Uttarakhand 248001",
        "Haridwar, Uttarakhand 249401",
        "Rishikesh, Uttarakhand 249201",
        "Roorkee, Uttarakhand 247667",
        "Haldwani, Uttarakhand 263139",
        "Nainital, Uttarakhand 263002",
        "Leh, Ladakh 194101",
        "Kargil, Ladakh 194103",
        "Puducherry, Pondicherry 605001",
        "Karaikal, Puducherry 609602",
        "Chandigarh UT 160017",
        "Port Blair, Andaman & Nicobar 744101",
        "Daman, DNH & DD 396210",
        "Silvassa, DNH & DD 396230",
        "Srinagar, Jammu & Kashmir 190001",
        "Jammu, Jammu & Kashmir 180001"
    )

    val metroLocations = listOf(
        "Mumbai, Maharashtra 400001",
        "New Delhi, Delhi NCR 110001",
        "Bengaluru, Karnataka 560001",
        "Hyderabad, Telangana 500001",
        "Pune, Maharashtra 411001",
        "Chennai, Tamil Nadu 600001",
        "Kolkata, West Bengal 700001",
        "Ahmedabad, Gujarat 380001",
        "Jaipur, Rajasthan 302001",
        "Lucknow, Uttar Pradesh 226001",
        "Chandigarh 160017",
        "Kochi, Kerala 682001"
    )

    val allStatesLocations = listOf(
        "Maharashtra (Mumbai, Pune, Nagpur)",
        "Uttar Pradesh (Lucknow, Noida, Varanasi)",
        "Delhi NCR (New Delhi, Gurugram, Faridabad)",
        "Karnataka (Bengaluru, Mysuru, Mangaluru)",
        "Telangana (Hyderabad, Warangal)",
        "Tamil Nadu (Chennai, Coimbatore, Madurai)",
        "Gujarat (Ahmedabad, Surat, Vadodara)",
        "Rajasthan (Jaipur, Jodhpur, Udaipur)",
        "West Bengal (Kolkata, Siliguri, Durgapur)",
        "Kerala (Kochi, Thiruvananthapuram)",
        "Bihar (Patna, Gaya, Muzaffarpur)",
        "Madhya Pradesh (Indore, Bhopal, Gwalior)",
        "Punjab (Ludhiana, Amritsar, Jalandhar)",
        "Haryana (Gurugram, Faridabad, Panipat)",
        "Odisha (Bhubaneswar, Cuttack, Rourkela)",
        "Assam (Guwahati, Dibrugarh, Silchar)",
        "Jharkhand (Ranchi, Jamshedpur, Dhanbad)",
        "Chhattisgarh (Raipur, Bilaspur)",
        "Andhra Pradesh (Visakhapatnam, Vijayawada)",
        "Uttarakhand (Dehradun, Haridwar)",
        "Himachal Pradesh (Shimla, Dharamshala)",
        "Goa (Panaji, Margao, Mapusa)",
        "Sikkim (Gangtok, Namchi)",
        "Tripura (Agartala, Udaipur)",
        "Meghalaya (Shillong, Tura)",
        "Manipur (Imphal)",
        "Nagaland (Kohima, Dimapur)",
        "Mizoram (Aizawl)",
        "Arunachal Pradesh (Itanagar)",
        "Jammu & Kashmir (Srinagar, Jammu)",
        "Ladakh (Leh, Kargil)",
        "Puducherry (Pondicherry)",
        "Chandigarh UT"
    )

    val activeLocationList = when (selectedLocationTab) {
        LocationCategoryTab.NCR_AND_UP -> upAndNcrLocations
        LocationCategoryTab.MINI_STATES -> miniStatesLocations
        LocationCategoryTab.MAJOR_METROS -> metroLocations
        LocationCategoryTab.ALL_STATES -> allStatesLocations
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Targeted Lead Intelligence",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "All India Towns, Villages, Mini States & 0-30 Range",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Target Entity Type Selection
            Text(
                text = "1. TARGET INDUSTRY / SECTOR",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val isInsurance = targetEntityType == LeadEntityType.INSURANCE
                Card(
                    onClick = { onEntityTypeSelected(LeadEntityType.INSURANCE) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isInsurance) PrimaryLight.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = if (isInsurance) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PrimaryLight, Color(0xFF3B82F6)))) else null
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = if (isInsurance) PrimaryLight else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Insurance Companies",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isInsurance) PrimaryLight else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Agencies, Brokers & POS",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val isSchool = targetEntityType == LeadEntityType.SCHOOL
                Card(
                    onClick = { onEntityTypeSelected(LeadEntityType.SCHOOL) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSchool) SecondaryLight.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = if (isSchool) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SecondaryLight, Color(0xFF38BDF8)))) else null
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = if (isSchool) SecondaryLight else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Registered Schools",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isSchool) SecondaryLight else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "CBSE, ICSE, UP Board & Col",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Location Section (Freeform & Categorized Indian Explorer)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2. LOCATION (TOWNS, VILLAGES, STATES)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Zero Failure Scraping",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = locationInput,
                onValueChange = onLocationInputChanged,
                placeholder = { Text("Type ANY town, tehsil, village or state (e.g. Jewar, Khatauli, Mapusa)") },
                leadingIcon = {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (locationInput.isNotBlank()) {
                        IconButton(onClick = { onLocationInputChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Location Tabs (NCR & UP, Mini States, Metros, All India)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LocationCategoryTab.values().forEach { tab ->
                    val isTabActive = selectedLocationTab == tab
                    FilterChip(
                        selected = isTabActive,
                        onClick = { selectedLocationTab = tab },
                        label = { Text("${tab.icon} ${tab.title}", fontSize = 11.sp, fontWeight = if (isTabActive) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick select chip grid for towns/villages
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "1-Tap Quick Select (${selectedLocationTab.title}):",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activeLocationList.take(18).forEach { locItem ->
                            val isSelected = locationInput.equals(locItem, ignoreCase = true) || locationInput.startsWith(locItem.split(",")[0])
                            SuggestionChip(
                                onClick = { onLocationInputChanged(locItem) },
                                label = {
                                    Text(
                                        text = locItem.split(",")[0],
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                                    labelColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                ),
                                border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. LEADS RANGE SELECTION (0 TO 30 LEADS)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FormatListNumbered,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "3. LEADS RANGE SELECTION (0-30)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Badge showing current count
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${filterConfig.leadsRange} Leads",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Configure how many verified contacts to scrape per batch (Range: 1 to 30):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Range Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val next = (filterConfig.leadsRange - 1).coerceIn(1, 30)
                                onFilterConfigChanged(filterConfig.copy(leadsRange = next))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                        }

                        Slider(
                            value = filterConfig.leadsRange.toFloat(),
                            onValueChange = { onFilterConfigChanged(filterConfig.copy(leadsRange = it.toInt().coerceIn(1, 30))) },
                            valueRange = 1f..30f,
                            steps = 28,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        IconButton(
                            onClick = {
                                val next = (filterConfig.leadsRange + 1).coerceIn(1, 30)
                                onFilterConfigChanged(filterConfig.copy(leadsRange = next))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                        }
                    }

                    // Quick range preset pills (5, 10, 15, 20, 25, 30)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(5, 10, 15, 20, 25, 30).forEach { rangePreset ->
                            val isChosen = filterConfig.leadsRange == rangePreset
                            FilterChip(
                                selected = isChosen,
                                onClick = { onFilterConfigChanged(filterConfig.copy(leadsRange = rangePreset)) },
                                label = { Text("$rangePreset", fontSize = 11.sp, fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal) },
                                modifier = Modifier.height(30.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Scraping Field Filters (What to scrape)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "4. SCRAPING FIELD FILTERS (${filterConfig.activeFieldCount()}/8)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(
                    onClick = {
                        val allSelected = filterConfig.activeFieldCount() == 8
                        onFilterConfigChanged(
                            filterConfig.copy(
                                extractName = !allSelected,
                                extractLocation = !allSelected,
                                extractWebsite = !allSelected,
                                extractAddress = !allSelected,
                                extractEmail = !allSelected,
                                extractPhone = !allSelected,
                                extractLicenseRating = !allSelected,
                                extractExecutive = !allSelected
                            )
                        )
                    }
                ) {
                    Text(
                        if (filterConfig.activeFieldCount() == 8) "Deselect All" else "Select All",
                        fontSize = 11.sp
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    FieldFilterCheckbox(
                        label = "Organization / School Name",
                        description = "Official registered brand & branch identity",
                        checked = filterConfig.extractName,
                        onCheckedChange = { onFilterConfigChanged(filterConfig.copy(extractName = it)) }
                    )
                    FieldFilterCheckbox(
                        label = "Targeted Location (City, State, Zip, Tehsil)",
                        description = "Village, town, block, district and PIN code",
                        checked = filterConfig.extractLocation,
                        onCheckedChange = { onFilterConfigChanged(filterConfig.copy(extractLocation = it)) }
                    )
                    FieldFilterCheckbox(
                        label = "Website Link (URL with Excel Hyperlinks)",
                        description = "Verified web portal & official domain link",
                        checked = filterConfig.extractWebsite,
                        onCheckedChange = { onFilterConfigChanged(filterConfig.copy(extractWebsite = it)) }
                    )
                    FieldFilterCheckbox(
                        label = "Full Physical Address",
                        description = "Street address, tehsil road & campus landmark",
                        checked = filterConfig.extractAddress,
                        onCheckedChange = { onFilterConfigChanged(filterConfig.copy(extractAddress = it)) }
                    )
                    FieldFilterCheckbox(
                        label = "Contact Email ID",
                        description = "Direct department or executive business email",
                        checked = filterConfig.extractEmail,
                        onCheckedChange = { onFilterConfigChanged(filterConfig.copy(extractEmail = it)) }
                    )
                    FieldFilterCheckbox(
                        label = "Phone Contact Number (+91)",
                        description = "Main office hotline or verified direct contact",
                        checked = filterConfig.extractPhone,
                        onCheckedChange = { onFilterConfigChanged(filterConfig.copy(extractPhone = it)) }
                    )
                    FieldFilterCheckbox(
                        label = "License / Registration / Affiliation ID",
                        description = "IRDAI Reg No, CBSE/CISCE/UP Board Affiliation",
                        checked = filterConfig.extractLicenseRating,
                        onCheckedChange = { onFilterConfigChanged(filterConfig.copy(extractLicenseRating = it)) }
                    )
                    FieldFilterCheckbox(
                        label = "Key Executive / Principal Contact",
                        description = "Branch manager, principal, director, superintendent",
                        checked = filterConfig.extractExecutive,
                        onCheckedChange = { onFilterConfigChanged(filterConfig.copy(extractExecutive = it)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Button
            Button(
                onClick = onStartScraping,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryLight
                )
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                val targetCityName = if (locationInput.isNotBlank()) locationInput.split(",")[0].take(18) else "Target"
                Text(
                    text = "Scrape ${filterConfig.leadsRange} Leads in $targetCityName",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun FieldFilterCheckbox(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = PrimaryLight
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}
