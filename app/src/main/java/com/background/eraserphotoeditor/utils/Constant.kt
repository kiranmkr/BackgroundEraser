package com.background.eraserphotoeditor.utils

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import com.example.backgrounderaser.R

object Constant {

    const val CAMERA_IMAGE = 10101
    const val PICK_IMAGE = 20202

    val readPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    } else {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    val cameraPermission = arrayOf(Manifest.permission.CAMERA)

    var screenWidth: Float = 720f

    @JvmStatic
    var mainBitmap: Bitmap? = null

    var cameraStatus: Boolean = false

    @JvmStatic
    val fileProvider: String = "com.example.backgrounderaser.fileprovider"

    var oldWidth: Int = 0

    var oldHeight: Int = 0

    @JvmStatic
    var labelNumber: Int = 1

    @JvmStatic
    var labelCategory: String = "babyshower"

    @JvmStatic
    var firstLaunch: Boolean = true

    @JvmStatic
    var colorArray = arrayOf(
        R.color.black,
        R.color.white,
        R.color.red_100,
        R.color.red_300, R.color.red_500, R.color.red_700, R.color.blue_100,
        R.color.blue_300, R.color.blue_500, R.color.blue_700, R.color.green_100, R.color.green_300,
        R.color.green_500, R.color.green_700, R.color.Gold
    )

    @JvmStatic
    var colorPro = arrayOf(
        R.color.colorPrimaryDark,
        R.color.gray
    )
}