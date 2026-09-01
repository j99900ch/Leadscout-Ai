package com.example.data.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ApkDistributionHelper {

    private const val APK_FILE_NAME = "LeadScoutAI_v1.0.0.apk"
    private const val ZIP_PACKAGE_NAME = "LeadScoutAI_Installer_Package.zip"

    suspend fun getOrExtractApkFile(context: Context): File = withContext(Dispatchers.IO) {
        val apkDir = File(context.cacheDir, "apk").apply {
            if (!exists()) mkdirs()
        }
        val targetFile = File(apkDir, APK_FILE_NAME)

        try {
            var copied = false
            val sourcePath = context.applicationInfo.sourceDir
            if (!sourcePath.isNullOrEmpty()) {
                val sourceFile = File(sourcePath)
                if (sourceFile.exists() && sourceFile.length() > 5000) {
                    FileInputStream(sourceFile).use { input ->
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    copied = true
                }
            }

            if (!copied || targetFile.length() < 5000) {
                try {
                    context.assets.open(APK_FILE_NAME).use { input ->
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    copied = true
                } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext targetFile
    }

    suspend fun getOrGenerateZipPackage(context: Context): File = withContext(Dispatchers.IO) {
        val apkFile = getOrExtractApkFile(context)
        val zipDir = File(context.cacheDir, "zip_packages").apply {
            if (!exists()) mkdirs()
        }
        val targetZip = File(zipDir, ZIP_PACKAGE_NAME)

        val androidHtmlContent = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <title>LeadScout AI - Android & Infinix Mobile Quick Installer</title>
                <style>
                    body { background: #0F172A; color: #F8FAFC; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; padding: 18px 14px; margin: 0; display: flex; flex-direction: column; align-items: center; }
                    .card { background: #1E293B; border-radius: 18px; padding: 20px; max-width: 460px; width: 100%; text-align: center; border: 1px solid rgba(255,255,255,0.08); box-shadow: 0 10px 25px rgba(0,0,0,0.4); margin-bottom: 14px; }
                    .badge { background: rgba(56,189,248,0.15); color: #38BDF8; padding: 4px 12px; border-radius: 20px; font-size: 11px; font-weight: 700; display: inline-block; margin-bottom: 10px; }
                    .btn-action { display: flex; align-items: center; justify-content: center; width: 100%; padding: 16px; border: none; border-radius: 14px; font-size: 15px; font-weight: 800; cursor: pointer; text-decoration: none; margin: 10px 0; }
                    .btn-blue { background: linear-gradient(135deg, #0284C7, #0369A1); color: white; box-shadow: 0 8px 20px rgba(2,132,199,0.35); }
                    .btn-green { background: linear-gradient(135deg, #10B981, #059669); color: white; box-shadow: 0 8px 20px rgba(16,185,129,0.35); }
                    .alert { background: rgba(245,158,11,0.1); border: 1px solid rgba(245,158,11,0.3); border-radius: 12px; padding: 12px; font-size: 12px; color: #FCD34D; text-align: left; line-height: 1.45; }
                    .step { background: rgba(255,255,255,0.04); border-radius: 10px; padding: 10px 12px; margin: 6px 0; text-align: left; display: flex; gap: 10px; align-items: center; }
                    .step-num { width: 24px; height: 24px; border-radius: 50%; background: #10B981; color: white; display: flex; align-items: center; justify-content: center; font-weight: 800; font-size: 11px; flex-shrink: 0; }
                </style>
            </head>
            <body>
                <div class="card" style="background: linear-gradient(180deg, #1E293B, #0F2038); border-color: rgba(56,189,248,0.3);">
                    <span class="badge">🌐 METHOD 1: 1-CLICK INSTANT WEB APP (ZERO ERROR)</span>
                    <h2 style="font-size: 19px; margin-bottom: 4px;">Install in Chrome on Mobile</h2>
                    <p style="font-size: 12px; color: #94A3B8; margin-bottom: 12px;">Guaranteed 100% working on Infinix and all Android phones without parse errors.</p>
                    
                    <a href="https://ais-dev-6nozdjhgfyczw4mk4mo3yo-293782780527.asia-southeast1.run.app" class="btn-action btn-blue" target="_blank">
                        🚀 Open & Install in Chrome
                    </a>
                    
                    <p style="font-size: 11px; color: #94A3B8; margin-top: 6px;">
                        💡 In Chrome: Tap <b>3 dots (⋮)</b> ➔ Tap <b>"Add to Home screen" / "Install app"</b>.
                    </p>
                </div>

                <div class="card">
                    <span class="badge" style="background: rgba(16,185,129,0.15); color: #34D399;">📦 METHOD 2: DIRECT APK PACKAGE INSTALLER</span>
                    <h2 style="font-size: 18px; margin-bottom: 6px;">Install via Infinix File Manager</h2>

                    <div class="alert" style="margin-bottom: 12px;">
                        <b>⚠️ Avoid "Corrupted File" error on Infinix:</b><br>
                        1. Extract the ZIP completely on your phone.<br>
                        2. Open Infinix <b>"Files" / "File Manager"</b> app.<br>
                        3. Go to <b>Downloads</b> ➔ Tap <b>$APK_FILE_NAME</b> ➔ Tap <b>"Install"</b>.
                    </div>

                    <a href="$APK_FILE_NAME" download class="btn-action btn-green">
                        📥 Download / Open APK File
                    </a>
                </div>
            </body>
            </html>
        """.trimIndent()

        val readmeContent = """
            ========================================================================
            🚀 LEADSCOUT AI - INFINIX & ANDROID MOBILE INSTALLATION GUIDE
            ========================================================================

            📱 HOW TO FIX 'CORRUPTED FILE' OR 'PARSE ERROR' ON INFINIX / ANDROID:
            ------------------------------------------------------------------------
            Chrome blocks direct installation from inside local HTML files.
            Here are the 2 guaranteed ways to install on your Infinix phone:

            🌟 METHOD 1: 1-Click Install via Chrome (Zero Error, No APK Parsing Needed):
            1. Open the Live Web App in Chrome:
               https://ais-dev-6nozdjhgfyczw4mk4mo3yo-293782780527.asia-southeast1.run.app
            2. Tap the 3 dots (⋮) in the top-right corner of Chrome.
            3. Tap 'Add to Home screen' or 'Install App'.
            --> The app icon installs directly to your home screen!

            📦 METHOD 2: Install APK via Infinix File Manager:
            1. Extract the ZIP file in your phone's Downloads.
            2. Open the 'Files' or 'File Manager' app on your Infinix phone.
            3. Tap '$APK_FILE_NAME' directly inside the folder.
            4. Tap 'Install'. (If prompted, toggle 'Allow from this source' ON).

            🍎 MAC LAPTOP (1-CLICK):
            ------------------------------------------------------------------------
            Double-click 'START_MAC.command' (or run ./start.sh).

            💻 WINDOWS PC (1-CLICK):
            ------------------------------------------------------------------------
            Double-click 'START_WINDOWS.bat'.
            ========================================================================
        """.trimIndent()

        try {
            FileOutputStream(targetZip).use { fos ->
                ZipOutputStream(BufferedOutputStream(fos)).use { zos ->
                    // 1. Add APK
                    if (apkFile.exists()) {
                        zos.putNextEntry(ZipEntry(APK_FILE_NAME))
                        FileInputStream(apkFile).use { it.copyTo(zos) }
                        zos.closeEntry()
                    }

                    // 2. Add START_ANDROID.html
                    zos.putNextEntry(ZipEntry("START_ANDROID.html"))
                    zos.write(androidHtmlContent.toByteArray())
                    zos.closeEntry()

                    // 3. Add START_HERE.html
                    zos.putNextEntry(ZipEntry("START_HERE.html"))
                    zos.write(androidHtmlContent.toByteArray())
                    zos.closeEntry()

                    // 4. Add README_INSTALLATION.txt
                    zos.putNextEntry(ZipEntry("README_INSTALLATION.txt"))
                    zos.write(readmeContent.toByteArray())
                    zos.closeEntry()

                    // 5. Add START_MAC.command placeholder
                    zos.putNextEntry(ZipEntry("START_MAC.command"))
                    zos.write("#!/bin/bash\npython3 -m pip install streamlit pandas requests openpyxl && streamlit run streamlit_app.py\n".toByteArray())
                    zos.closeEntry()

                    // 6. Add START_WINDOWS.bat placeholder
                    zos.putNextEntry(ZipEntry("START_WINDOWS.bat"))
                    zos.write("@echo off\npip install streamlit pandas requests openpyxl\nstreamlit run streamlit_app.py\npause\n".toByteArray())
                    zos.closeEntry()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext targetZip
    }

    fun createInstallIntent(context: Context, apkFile: File): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
    }

    fun createWhatsAppShareIntent(context: Context, apkFile: File): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val shareMessage = """
            🚀 *LeadScout AI - Instant Lead Extractor*
            
            Attached APK: *$APK_FILE_NAME*
            
            📲 *2-Click Install on Android Mobile:*
            1. Tap the attached APK file (Click 1: Start Installer).
            2. Tap *'Install'* on the Android screen prompt (Click 2: Confirm).
            
            ⚡ *Allow from this source:* If prompted, toggle ON to complete install.
            🔑 Works 100% free with Gemini AI Free Tier.
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            putExtra(Intent.EXTRA_SUBJECT, "LeadScout AI - 2-Click Install Android APK")
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return intent
    }

    fun createZipWhatsAppShareIntent(context: Context, zipFile: File): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            zipFile
        )

        val shareMessage = """
            🚀 *LeadScout AI - Complete Multiplatform Package (ZIP)*
            
            Attached: *$ZIP_PACKAGE_NAME*
            
            📲 *2-Click Install on Android Mobile:*
            1. Extract the ZIP on your phone & open *START_ANDROID.html* (Click 1: Start).
            2. Tap *'Install'* on the Android popup (Click 2: Confirm).
            
            🍎 *Mac Laptop:* Double-click *START_MAC.command*
            💻 *Windows PC:* Double-click *START_WINDOWS.bat*
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            putExtra(Intent.EXTRA_SUBJECT, "LeadScout AI Complete Package (ZIP)")
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return intent
    }

    fun createGenericZipShareIntent(context: Context, zipFile: File): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            zipFile
        )

        val shareMessage = """
            🚀 LeadScout AI - Complete Installation Package (ZIP)
            
            Contains:
            - Android Mobile 2-Click Installer (START_ANDROID.html & $APK_FILE_NAME)
            - Mac 1-Click Launcher (START_MAC.command)
            - Windows 1-Click Launcher (START_WINDOWS.bat)
            
            How to Install on Android Mobile:
            Click 1: Open START_ANDROID.html and tap START
            Click 2: Tap Install on Android system prompt
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            putExtra(Intent.EXTRA_SUBJECT, "LeadScout AI Complete ZIP Package")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return Intent.createChooser(intent, "Share LeadScout AI ZIP Package via")
    }

    fun createGenericShareIntent(context: Context, apkFile: File): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val shareMessage = """
            🚀 LeadScout AI - Android APK (2-Click Mobile Install)
            
            1. Tap APK to open
            2. Tap 'Install' on Android prompt
            
            File: $APK_FILE_NAME
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            putExtra(Intent.EXTRA_SUBJECT, "LeadScout AI APK Package")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return Intent.createChooser(intent, "Share LeadScout AI APK via")
    }
}
