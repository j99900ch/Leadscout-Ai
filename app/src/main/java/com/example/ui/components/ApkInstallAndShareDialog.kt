package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.export.ApkDistributionHelper
import com.example.data.model.UserProfile
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.SecondaryLight
import com.example.ui.theme.TertiaryLight
import kotlinx.coroutines.launch
import java.io.File

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun ApkInstallAndShareDialog(
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onTestApiKey: suspend (String) -> Result<String>,
    onSaveApiKey: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Streamlit Web", "Install APK", "Mac Laptop", "Free Gemini")

    var isPreparingApk by remember { mutableStateOf(false) }
    var apkFilePrepared by remember { mutableStateOf<File?>(null) }
    var installStatusMessage by remember { mutableStateOf<String?>(null) }

    // Free Gemini Key test state
    var keyToTest by remember { mutableStateOf(userProfile.customApiKey) }
    var isTestingKey by remember { mutableStateOf(false) }
    var keyTestResult by remember { mutableStateOf<String?>(null) }
    var isKeyTestSuccess by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF059669), Color(0xFF10B981))
                            ),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Cross-Platform Launch Hub",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Streamlit Web (PC/Mac/iOS/Android) & APK",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
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
                // Tab Row
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (selectedTabIndex) {
                    0 -> {
                        // TAB 0: Streamlit Web for Windows, Mac, iPhone & Android Browsers
                        StreamlitWebGuideContent(context = context)
                    }
                    1 -> {
                        // TAB 1: Android Mobile Install & ZIP / WhatsApp Sharing
                        InstallTabContent(
                            context = context,
                            isPreparingApk = isPreparingApk,
                            installStatusMessage = installStatusMessage,
                            onStartInstall = {
                                scope.launch {
                                    isPreparingApk = true
                                    installStatusMessage = "Preparing package installer..."
                                    try {
                                        val apk = ApkDistributionHelper.getOrExtractApkFile(context)
                                        apkFilePrepared = apk
                                        val installIntent = ApkDistributionHelper.createInstallIntent(context, apk)
                                        context.startActivity(installIntent)
                                        installStatusMessage = "✅ Step 1 Complete! Now tap 'Install' on Android prompt (Click 2)."
                                    } catch (e: Exception) {
                                        installStatusMessage = "Installer launched: ${e.message}. Tap 'Install' on screen."
                                        Toast.makeText(context, "APK ready in cache. Select package installer.", Toast.LENGTH_LONG).show()
                                    }
                                    isPreparingApk = false
                                }
                            },
                            onShareZipWhatsApp = {
                                scope.launch {
                                    isPreparingApk = true
                                    try {
                                        val zip = ApkDistributionHelper.getOrGenerateZipPackage(context)
                                        val waIntent = ApkDistributionHelper.createZipWhatsAppShareIntent(context, zip)
                                        try {
                                            context.startActivity(waIntent)
                                        } catch (e: Exception) {
                                            val genericIntent = ApkDistributionHelper.createGenericZipShareIntent(context, zip)
                                            context.startActivity(genericIntent)
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error sharing ZIP: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                    isPreparingApk = false
                                }
                            },
                            onShareZipGeneric = {
                                scope.launch {
                                    isPreparingApk = true
                                    try {
                                        val zip = ApkDistributionHelper.getOrGenerateZipPackage(context)
                                        val genericIntent = ApkDistributionHelper.createGenericZipShareIntent(context, zip)
                                        context.startActivity(genericIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error preparing ZIP: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                    isPreparingApk = false
                                }
                            },
                            onShareWhatsApp = {
                                scope.launch {
                                    isPreparingApk = true
                                    try {
                                        val apk = ApkDistributionHelper.getOrExtractApkFile(context)
                                        val waIntent = ApkDistributionHelper.createWhatsAppShareIntent(context, apk)
                                        try {
                                            context.startActivity(waIntent)
                                        } catch (e: Exception) {
                                            val genericIntent = ApkDistributionHelper.createGenericShareIntent(context, apk)
                                            context.startActivity(genericIntent)
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error sharing APK: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                    isPreparingApk = false
                                }
                            },
                            onDownloadApk = {
                                scope.launch {
                                    isPreparingApk = true
                                    val apk = ApkDistributionHelper.getOrExtractApkFile(context)
                                    Toast.makeText(
                                        context,
                                        "APK exported: ${apk.name} (${apk.length() / 1024} KB). Ready to install in 2 clicks!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    isPreparingApk = false
                                }
                            }
                        )
                    }
                    2 -> {
                        // TAB 2: Mac Laptop Installation Guide
                        MacLaptopGuideContent(context = context)
                    }
                    3 -> {
                        // TAB 3: Free Gemini API Key Setup & Testing
                        FreeGeminiSetupContent(
                            context = context,
                            keyToTest = keyToTest,
                            onKeyChanged = { keyToTest = it },
                            isTestingKey = isTestingKey,
                            keyTestResult = keyTestResult,
                            isKeyTestSuccess = isKeyTestSuccess,
                            onRunTest = {
                                scope.launch {
                                    isTestingKey = true
                                    keyTestResult = "Validating key with Google Gemini servers..."
                                    val res = onTestApiKey(keyToTest)
                                    res.onSuccess { msg ->
                                        keyTestResult = msg
                                        isKeyTestSuccess = true
                                        onSaveApiKey(keyToTest)
                                    }.onFailure { err ->
                                        keyTestResult = "Validation failed: ${err.message}"
                                        isKeyTestSuccess = false
                                    }
                                    isTestingKey = false
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun InstallTabContent(
    context: Context,
    isPreparingApk: Boolean,
    installStatusMessage: String?,
    onStartInstall: () -> Unit,
    onShareZipWhatsApp: () -> Unit,
    onShareZipGeneric: () -> Unit,
    onShareWhatsApp: () -> Unit,
    onDownloadApk: () -> Unit
) {
    var clickStep by remember { mutableIntStateOf(1) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Hero 2-Click Start / Install Action Button
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF064E3B)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ 2-CLICK ANDROID INSTALLER",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399)
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF10B981).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (clickStep == 1) "Click 1 of 2" else "Click 2 of 2",
                            color = Color(0xFF6EE7B7),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (clickStep == 1) "Tap START to launch Android package installer" else "Now tap 'Install' on the Android screen prompt!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (clickStep == 1) {
                            clickStep = 2
                        }
                        onStartInstall()
                    },
                    enabled = !isPreparingApk,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (clickStep == 1) Color(0xFF10B981) else Color(0xFF059669),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    if (isPreparingApk) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Launching Installer...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (clickStep == 1) "CLICK 1: START INSTALLATION" else "CLICK 2: CONFIRM / RE-OPEN",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }

                if (installStatusMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = installStatusMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA7F3D0),
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ZIP Package Sharing Section (Android + Mac + PC)
        Text(
            text = "SHARE INSTALLATION PACKAGES",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Share ZIP via WhatsApp
        Button(
            onClick = onShareZipWhatsApp,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25D366),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Share ZIP (2-Click Mobile + Mac) on WhatsApp",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Share ZIP Generic
            OutlinedButton(
                onClick = onShareZipGeneric,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share ZIP", fontSize = 12.sp)
            }

            // Share Direct APK
            OutlinedButton(
                onClick = onShareWhatsApp,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Android, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share APK", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Download APK Locally Button
        OutlinedButton(
            onClick = onDownloadApk,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Download / Export APK File", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3-Step Process Guide
        Text(
            text = "2-CLICK ANDROID MOBILE WORKFLOW",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(6.dp))

        StepRow(stepNumber = "1", title = "Click 1: Tap START", desc = "Triggers the package installation service on your phone.")
        StepRow(stepNumber = "2", title = "Click 2: Tap INSTALL on Screen", desc = "Android confirms the prompt: Tap 'Install' then 'Open'.")
        StepRow(stepNumber = "3", title = "When Sharing ZIP Package", desc = "Receiver extracts ZIP, opens START_ANDROID.html, and taps START twice.")
    }
}

@Composable
private fun MacLaptopGuideContent(context: Context) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val startMacCommand = "./START_MAC.command"
    val manualCommand = "pip3 install -r requirements.txt && streamlit run streamlit_app.py"

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F172A)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF38BDF8).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LaptopMac,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "1-Click Mac App Installation",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Extract ZIP ➔ Click START ➔ Installs App Icon & Launches",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3 Easy Steps to 1-Click Mac Installation
        Text(
            text = "HOW TO INSTALL & RUN ON MAC",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = PrimaryLight
        )

        Spacer(modifier = Modifier.height(8.dp))

        StepRow(
            stepNumber = "1",
            title = "Extract Downloaded ZIP on Mac",
            desc = "Unzip the exported folder anywhere on your Mac (e.g. Downloads or Desktop)."
        )
        StepRow(
            stepNumber = "2",
            title = "Double-Click 'START_MAC.command'",
            desc = "Click the start file in the folder. It installs the native 'LeadScout AI.app' icon in /Applications and on Desktop."
        )
        StepRow(
            stepNumber = "3",
            title = "Works Simultaneously",
            desc = "The app opens immediately in Safari/Chrome on Mac, and mobile devices on your Wi-Fi can connect simultaneously!"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 1-Click Command Box
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "START BUTTON FILE (OR TERMINAL QUICK-RUN)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TertiaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Double-click START_MAC.command or paste this in Terminal:",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = startMacCommand,
                            color = Color(0xFF34D399),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        IconButton(
                            onClick = {
                                clipboardManager?.setPrimaryClip(
                                    ClipData.newPlainText("Mac Start Command", startMacCommand)
                                )
                                Toast.makeText(context, "Copied start command to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // BlueStacks / Android Studio alternative note
        MacMethodCard(
            badge = "ALTERNATIVE: ANDROID APK",
            title = "Android Studio / BlueStacks / PlayCover",
            description = "You can also drag & drop the LeadScout AI APK into Android Studio or BlueStacks for Mac to run the Android build."
        )
    }
}

@Composable
private fun MacMethodCard(badge: String, title: String, description: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = badge,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TertiaryLight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun FreeGeminiSetupContent(
    context: Context,
    keyToTest: String,
    onKeyChanged: (String) -> Unit,
    isTestingKey: Boolean,
    keyTestResult: String?,
    isKeyTestSuccess: Boolean,
    onRunTest: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = TertiaryLight.copy(alpha = 0.1f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = TertiaryLight,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "100% Free Gemini API Tier",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TertiaryLight
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Google AI Studio provides a free tier with 15 Requests Per Minute (RPM) and 1,500 Requests Per Day (RPD). Zero credit card required!",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Direct Link Button to AI Studio
        Button(
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Visit https://aistudio.google.com to get your free key", Toast.LENGTH_LONG).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryLight
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Get Free API Key on Google AI Studio")
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "TEST & VERIFY FREE KEY",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = keyToTest,
            onValueChange = onKeyChanged,
            placeholder = { Text("Paste Gemini Key (e.g. AQ.Ab8RN...)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onRunTest,
            enabled = !isTestingKey && keyToTest.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = SecondaryLight
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isTestingKey) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Testing Connection...")
            } else {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Validate & Save Key")
            }
        }

        if (keyTestResult != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isKeyTestSuccess) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isKeyTestSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isKeyTestSuccess) Color(0xFF059669) else Color(0xFFDC2626),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = keyTestResult,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isKeyTestSuccess) Color(0xFF065F46) else Color(0xFF991B1B),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StepRow(stepNumber: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(PrimaryLight.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = PrimaryLight,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun StreamlitWebGuideContent(context: Context) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val runCommand = "streamlit run streamlit_app.py --server.address=0.0.0.0 --server.port=8501"

    Column(modifier = Modifier.fillMaxWidth()) {
        // Hero Web Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F766E)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = Color(0xFF5EEAD4),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "STREAMLIT LOCAL & WEB HUB",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5EEAD4)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Runs instantly in browser on Windows, Mac laptops, iPhone Safari & Android Chrome with zero APK installation required!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 1-Click Terminal Command Box
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "1. LAUNCH ON LAPTOP (WINDOWS & MAC)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Run this in your terminal or double-click run_windows.bat:",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = runCommand,
                            color = Color(0xFF38BDF8),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                clipboardManager?.setPrimaryClip(
                                    ClipData.newPlainText("Streamlit Command", runCommand)
                                )
                                Toast.makeText(context, "Copied Streamlit launch command!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Mobile Phone Browser Guide
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "2. ACCESS ON IPHONE & ANDROID BROWSER",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF059669)
                )
                Spacer(modifier = Modifier.height(6.dp))
                StepRow(stepNumber = "A", title = "Same Wi-Fi Connection", desc = "Connect iPhone or Android phone to the same Wi-Fi as your laptop.")
                StepRow(stepNumber = "B", title = "Open Safari / Chrome", desc = "Visit http://<laptop-ip>:8501 (e.g. http://192.168.1.50:8501).")
                StepRow(stepNumber = "C", title = "Add to Home Screen", desc = "Tap Share ➔ 'Add to Home Screen' on iPhone or 'Install' on Chrome.")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Streamlit Free Cloud Hosting
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = TertiaryLight.copy(alpha = 0.08f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "3. 24/7 FREE STREAMLIT COMMUNITY CLOUD",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TertiaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Deploy streamlit_app.py to share.streamlit.io for a permanent global HTTPS web link accessible on any phone or laptop anywhere!",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

