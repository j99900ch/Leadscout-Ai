package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeadEntity
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.SecondaryLight
import com.example.ui.theme.TertiaryLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LeadDetailDialog(
    lead: LeadEntity,
    onDismiss: () -> Unit,
    onToggleBookmark: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val isInsurance = lead.entityType == "INSURANCE"
    val dateStr = SimpleDateFormat("MMMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(lead.timestamp))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (isInsurance) PrimaryLight.copy(alpha = 0.12f) else SecondaryLight.copy(alpha = 0.12f),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isInsurance) Icons.Default.Business else Icons.Default.School,
                            contentDescription = null,
                            tint = if (isInsurance) PrimaryLight else SecondaryLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isInsurance) "Insurance Entity" else "Registered School",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onToggleBookmark) {
                    Icon(
                        imageVector = if (lead.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (lead.isBookmarked) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = lead.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${lead.category} • ${lead.ratingOrAccreditation}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Score Badge Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = TertiaryLight.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lead Quality Score: ${lead.leadQualityScore}/100",
                                fontWeight = FontWeight.Bold,
                                color = TertiaryLight,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "RAG Verified",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TertiaryLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Detailed Vector Info Rows
                DetailInfoItem(
                    icon = Icons.Default.LocationOn,
                    label = "Physical Address",
                    value = lead.address.ifEmpty { "${lead.city}, ${lead.state} ${lead.zipCode}" },
                    onAction = {
                        val geoUri = Uri.parse("geo:0,0?q=${Uri.encode(lead.address.ifEmpty { lead.name })}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            copyText(context, "Address", lead.address)
                        }
                    },
                    actionLabel = "Open Map"
                )

                DetailInfoItem(
                    icon = Icons.Default.Email,
                    label = "Email Address",
                    value = lead.email,
                    onAction = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${lead.email}")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            copyText(context, "Email", lead.email)
                        }
                    },
                    actionLabel = "Compose Email"
                )

                DetailInfoItem(
                    icon = Icons.Default.Phone,
                    label = "Contact Phone",
                    value = lead.phone,
                    onAction = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${lead.phone}")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            copyText(context, "Phone", lead.phone)
                        }
                    },
                    actionLabel = "Dial"
                )

                DetailInfoItem(
                    icon = Icons.Default.Language,
                    label = "Official Website",
                    value = lead.website,
                    onAction = {
                        val url = if (lead.website.startsWith("http")) lead.website else "https://${lead.website}"
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        } catch (e: Exception) {
                            copyText(context, "Website", url)
                        }
                    },
                    actionLabel = "Visit Link"
                )

                DetailInfoItem(
                    icon = Icons.Default.Person,
                    label = "Key Executive / Principal",
                    value = lead.contactPerson,
                    onAction = { copyText(context, "Contact Person", lead.contactPerson) },
                    actionLabel = "Copy"
                )

                DetailInfoItem(
                    icon = Icons.Default.Verified,
                    label = "Registry ID / License No.",
                    value = lead.licenseOrRegistrationNo,
                    onAction = { copyText(context, "License", lead.licenseOrRegistrationNo) },
                    actionLabel = "Copy"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Scraped: $dateStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val full = """
                        ${lead.name}
                        Type: ${lead.entityType}
                        Category: ${lead.category}
                        Location: ${lead.address.ifEmpty { "${lead.city}, ${lead.state} ${lead.zipCode}" }}
                        Email: ${lead.email}
                        Phone: ${lead.phone}
                        Website: ${lead.website}
                        Contact Person: ${lead.contactPerson}
                        License ID: ${lead.licenseOrRegistrationNo}
                        Rating: ${lead.ratingOrAccreditation}
                        Lead Score: ${lead.leadQualityScore}/100
                    """.trimIndent()
                    copyText(context, "Full Lead Dossier", full)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Full Dossier")
            }
        },
        dismissButton = {
            Row {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Lead",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}

@Composable
private fun DetailInfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    onAction: () -> Unit,
    actionLabel: String
) {
    if (value.isBlank() || value.contains("Excluded")) return

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryLight,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            TextButton(onClick = onAction) {
                Text(actionLabel, fontSize = 11.sp)
            }
        }
    }
}

private fun copyText(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copied $label to clipboard", Toast.LENGTH_SHORT).show()
}
