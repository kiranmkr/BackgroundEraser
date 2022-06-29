package com.background.eraserphotoeditor.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.example.backgrounderaser.R
import java.util.*


object FeedbackUtils {

    private fun getDeviceInfo(context: Context): String {
        val sdk = Build.VERSION.SDK_INT      // API Level
        val model = Build.MODEL            // Model
        val brand = Build.BRAND          // Product
        var infoString = ""
        val locale: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0).country
        } else {
            Locale.getDefault().country.lowercase(Locale.ROOT)
        }
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val version = pInfo.versionName
            infoString += "Application Version: $version\n"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        infoString += "Brand: " + brand + " (" + model + ")\n" +
                "Android API: " + getSDKName(sdk)

        if (locale.isNotEmpty()) {
            infoString += "\nCountry: $locale"
        }
        return infoString
    }

    private fun getSDKName(sdk: Int): String {
        val sdkName: String
        when (sdk) {
            21, 22 -> {
                sdkName = "Android 5"
            }
            23 -> {
                sdkName = "Android 6"
            }
            24, 25 -> {
                sdkName = "Android 7"
            }
            26, 27 -> {
                sdkName = "Android 8"
            }
            28 -> {
                sdkName = "Android 9"
            }
            29 -> {
                sdkName = "Android 10"
            }
            30 -> {
                sdkName = "Android 11"
            }
            31->{
                sdkName = "Android 12"
            }
            else -> {
                sdkName = "Android 13"
            }
        }

        return sdkName
    }

    private fun emailSubject(context: Context): String {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val version = pInfo.versionName
            val name = "Background Eraser"
            return "$name - $version"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return context.getString(R.string.app_name)
    }

    @JvmStatic
    fun startFeedbackEmail(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("coreelgofeedback@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject(context))
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "\n\n--Please write your question above this--\n${getDeviceInfo(context)}"
        )
        context.startActivity(Intent.createChooser(intent, "Email via..."))
    }

}