package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeadEntity
import com.example.data.model.UserProfile
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.SecondaryLight
import com.example.ui.theme.TertiaryLight

@Composable
fun DashboardStatsBar(
    leads: List<LeadEntity>,
    userProfile: UserProfile,
    onOpenQuota: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalLeads = leads.size
    val insuranceCount = leads.count { it.entityType == "INSURANCE" }
    val schoolCount = leads.count { it.entityType == "SCHOOL" }
    val emailCount = leads.count { it.email.isNotBlank() && !it.email.contains("Excluded") }
    val emailRate = if (totalLeads > 0) (emailCount * 100 / totalLeads) else 0
    val phoneCount = leads.count { it.phone.isNotBlank() && !it.phone.contains("Excluded") }
    val phoneRate = if (totalLeads > 0) (phoneCount * 100 / totalLeads) else 0
    val avgScore = if (totalLeads > 0) leads.map { it.leadQualityScore }.average().toInt() else 92

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Live Intelligence & Quota Dashboard",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "API: ${userProfile.apiCreditsUsed}/${userProfile.apiCreditsMax} Used",
                style = MaterialTheme.typography.labelSmall,
                color = if (userProfile.apiCreditsUsed > 80) AmberAccent else TertiaryLight,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Total Extracted",
                value = "$totalLeads",
                subtitle = "$insuranceCount Ins · $schoolCount Edu",
                icon = Icons.Default.Business,
                accentColor = PrimaryLight,
                gradient = listOf(Color(0xFF1E3A8A), Color(0xFF2563EB))
            )

            StatCard(
                title = "Verified Emails",
                value = "$emailRate%",
                subtitle = "$emailCount direct inboxes",
                icon = Icons.Default.Email,
                accentColor = TertiaryLight,
                gradient = listOf(Color(0xFF065F46), Color(0xFF059669))
            )

            StatCard(
                title = "Phone Reach",
                value = "$phoneRate%",
                subtitle = "$phoneCount active lines",
                icon = Icons.Default.Phone,
                accentColor = SecondaryLight,
                gradient = listOf(Color(0xFF0369A1), Color(0xFF0284C7))
            )

            StatCard(
                title = "Lead Quality",
                value = "$avgScore/100",
                subtitle = "Decision Tree index",
                icon = Icons.Default.AutoAwesome,
                accentColor = AmberAccent,
                gradient = listOf(Color(0xFF92400E), Color(0xFFD97706))
            )

            StatCard(
                title = "API Limit Balance",
                value = "${userProfile.apiCreditsMax - userProfile.apiCreditsUsed}",
                subtitle = "Free requests left",
                icon = Icons.Default.Speed,
                accentColor = Color(0xFF8B5CF6),
                gradient = listOf(Color(0xFF5B21B6), Color(0xFF7C3AED)),
                onClick = onOpenQuota
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    gradient: List<Color>,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.width(155.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}
