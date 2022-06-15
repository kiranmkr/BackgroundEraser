package com.example.backgrounderaser.utils

import com.example.backgrounderaser.R

object Constant {

    const val REQUEST_CAPTURE_IMAGE = 10101
    const val REQUEST_GELLERY_IMAGE = 20202

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