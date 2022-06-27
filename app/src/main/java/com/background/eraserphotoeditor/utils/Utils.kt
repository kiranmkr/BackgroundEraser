package com.background.eraserphotoeditor.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.res.AssetManager
import android.graphics.*
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import java.io.*
import java.util.*


object Utils {

    @Suppress("DEPRECATION")
    @JvmField
    val BASE_LOCAL_PATH =
        "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Background Eraser/"

    @Suppress("DEPRECATION")
    val Base_External_Save =
        "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Background Eraser/"

    @JvmStatic
    fun getRootPath(context: Context, internalDrir: Boolean): String {

        val root = if (internalDrir) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.getExternalFilesDir("Background Eraser")?.absolutePath + "/"
            } else {
                BASE_LOCAL_PATH
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Base_External_Save
            } else {
                BASE_LOCAL_PATH
            }
        }

        val dirDest = File(root)

        if (!dirDest.exists()) {
            dirDest.mkdirs()
        }

        return root
    }

    fun getShareDirectory(context: Context): String {
        return context.externalCacheDir?.absolutePath + "/"
    }

    @Suppress("DEPRECATION")
    @JvmStatic
    fun saveMediaToStorage(bitmap: Bitmap, activity: AppCompatActivity): String? {

        var filePath: String? = null

        //Generating a file name
        val filename = "JPEG_${System.currentTimeMillis()}.jpg"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            activity.contentResolver?.also { resolver ->

                val dirDest = File(Environment.DIRECTORY_DCIM, "Background Eraser")

                //Content resolver will process the contentValues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "$dirDest")
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                filePath = "/storage/emulated/0/DCIM/Background Eraser"

                //Opening an outputStream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }

            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val dirDest = File(imagesDir, "Background Eraser")
            if (!dirDest.exists()) {
                dirDest.mkdirs()
            }
            val image = File(dirDest, filename)

            Log.e("myFilePath", "$image")

            filePath = image.toString()

            fos = FileOutputStream(image)

        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.flush()
            it.close()

            Log.e("myFileFos", "Saved to Photos")

        }

        return filePath
    }


    @JvmStatic
    fun showToast(c: Context, message: String?) {
        try {
            if (!(c as Activity).isFinishing) {
                c.runOnUiThread { //show your Toast here..
                    Toast.makeText(c.applicationContext, "${message}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getLayoutBitmap(view: View): Bitmap {
        return view.drawToBitmap(Bitmap.Config.ARGB_8888)
    }

    var d = 0f
    var newRot = 0f
    private var isZoomAndRotate = false
    private var isOutSide = false
    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2
    private var mode = NONE
    private val start = PointF()
    private val mid = PointF()
    var oldDist = 1f
    private var xCoOrdinate = 0f
    private var yCoOrdinate = 0f
    var lastEvent: FloatArray? = null

    @JvmStatic
    fun viewTransformation(view: View, event: MotionEvent) {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {


                xCoOrdinate = view.x - event.rawX
                yCoOrdinate = view.y - event.rawY
                start.set(event.x, event.y)
                isOutSide = false
                mode = DRAG
                lastEvent = null

            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    midPoint(mid, event)
                    mode = ZOOM
                }
                lastEvent = FloatArray(4)
                lastEvent!![0] = event.getX(0)
                lastEvent!![1] = event.getX(1)
                lastEvent!![2] = event.getY(0)
                lastEvent!![3] = event.getY(1)
                d = rotation(event)
            }
            MotionEvent.ACTION_UP -> {

                isZoomAndRotate = false
                if (mode == DRAG) {
                    event.x
                    event.y
                }
                isOutSide = true
                mode = NONE
                lastEvent = null
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_OUTSIDE -> {
                isOutSide = true
                mode = NONE
                lastEvent = null
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_MOVE -> if (!isOutSide) {
                if (mode == DRAG) {
                    isZoomAndRotate = false
                    view.animate().x(event.rawX + xCoOrdinate).y(event.rawY + yCoOrdinate)
                        .setDuration(0).start()
                }
                if (mode == ZOOM && event.pointerCount == 2) {
                    val newDist1 = spacing(event)
                    if (newDist1 > 10f) {
                        val scale: Float = newDist1 / oldDist * view.scaleX
                        if (scale in 0.5..2.5) {
                            view.scaleX = scale
                            view.scaleY = scale
                            Log.d("mySticker", "${scale}")
                        }
                    }
                    if (lastEvent != null) {
                        newRot = rotation(event)
                        view.rotation = (view.rotation + (newRot - d))
                    }
                }
            }

        }
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    private fun rotation(event: MotionEvent): Float {
        val delta_x = (event.getX(0) - event.getX(1)).toDouble()
        val delta_y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = Math.atan2(delta_y, delta_x)
        return Math.toDegrees(radians).toFloat()
    }


    /**
     * Decode a bitmap from a file and apply the transformations described in its EXIF data
     *
     * @param file - The image file to be read using [BitmapFactory.decodeFile]
     */
    @Suppress("UNUSED_VARIABLE")
    @JvmStatic
    fun decodeBitmap(file: File): Bitmap? {
        // First, decode EXIF data and retrieve transformation matrix
        if (!fileChecker(file.absolutePath)) {
            return null
        }

        val exif = ExifInterface(file.absolutePath)
        val transformation =
            decodeExifOrientation(
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90
                )
            )

        // Read bitmap using factory methods, and transform it using EXIF data
        val options = BitmapFactory.Options()
        return try {

            val bitmap =
                scaleDown(BitmapFactory.decodeFile(file.absolutePath, options), 1920f, true)
            if (bitmap != null) {
                /*Bitmap.createBitmap(
                  BitmapFactory.decodeFile(file.absolutePath),
                  0, 0, bitmap.width, bitmap.height, transformation, true
                )*/
                bitmap
            } else {
                null
            }
            // return BitmapFactory.decodeFile(file.absolutePath, options)

        } catch (ex: OutOfMemoryError) {
            ex.printStackTrace()
            null
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
            null
        }

    }

    /**
     * Helper function used to convert an EXIF orientation enum into a transformation matrix
     * that can be applied to a bitmap.
     *
     * @param orientation - One of the constants from [ExifInterface]
     */
    private fun decodeExifOrientation(orientation: Int): Matrix {
        val matrix = Matrix()

        // Apply transformation corresponding to declared EXIF orientation
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL, ExifInterface.ORIENTATION_UNDEFINED -> Unit
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1F, 1F)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1F, -1F)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postScale(-1F, 1F)
                matrix.postRotate(270F)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postScale(-1F, 1F)
                matrix.postRotate(90F)
            }

            // Error out if the EXIF orientation is invalid
            else -> throw IllegalArgumentException("Invalid orientation: $orientation")
        }

        // Return the resulting matrix
        return matrix
    }

    fun scaleDown(
        realImage: Bitmap?, maxImageSize: Float,
        filter: Boolean
    ): Bitmap? {

        if (realImage != null) {

            Log.e("myBitmapVal", "${realImage.width} -- ${realImage.height}")

            return if (realImage.width > maxImageSize || realImage.height > maxImageSize) {

                val ratio =
                    Math.min(maxImageSize / realImage.width, maxImageSize / realImage.height)
                val width = Math.round(ratio * realImage.width)
                val height = Math.round(ratio * realImage.height)

                return Bitmap.createScaledBitmap(
                    realImage, width,
                    height, filter
                )
            } else {
                realImage
            }
        } else {
            return null
        }

    }

    @JvmStatic
    fun getBitmapOrg(path: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val f = File(path)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return bitmap
    }

    /**
     * Retrieve a bitmap from assets.
     *
     * @param mgr
     * The [AssetManager] obtained via [Context.getAssets]
     * @param path
     * The path to the asset.
     * @return The [Bitmap] or `null` if we failed to decode the file.
     */
    fun getBitmapFromAsset(mgr: AssetManager, path: String?): Bitmap? {
        var `is`: InputStream? = null
        var bitmap: Bitmap?
        try {
            `is` = mgr.open(path!!)
            bitmap = BitmapFactory.decodeStream(`is`)
        } catch (e: IOException) {
            bitmap = null
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (ignored: IOException) {
                }
            }
        }
        return bitmap
    }

    fun getBitmapFromAsset(context: Context, path: String): Bitmap? = try {
        context.assets.open(path).use { BitmapFactory.decodeStream(it) }
    } catch (ex: IOException) {
        ex.printStackTrace()
        null
    }

    /* fun getBitmapFromAsset(context: Context, filePath: String): Bitmap? {
         val assetManager = context.assets
         val istr: InputStream
         var bitmap: Bitmap? = null
         try {
             istr = assetManager.open(filePath)
             bitmap = BitmapFactory.decodeStream(istr)
         } catch (e: IOException) {
             e.printStackTrace()
             bitmap = null

         }
         return bitmap
     }*/

    fun fileChecker(path: String?): Boolean {
        return if (path != null) {
            val file = File(path)
            file.exists()
        } else {
            false
        }
    }

    //**********************************************************************************************//
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            context.resources.displayMetrics
        ).toInt()
    }

    @Suppress("DEPRECATION")
    @JvmStatic
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    @JvmStatic
    fun clearGarbageCollection() {
        try {
            System.gc()
            Runtime.getRuntime().gc()
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    fun randomColor(): String {
        val r = Random()
        val red = r.nextInt(256)
        val green = r.nextInt(256)
        val blue = r.nextInt(256)
        return java.lang.String.format("#%06X", 0xFFFFFF and Color.rgb(red, green, blue))
    }

    @JvmStatic
    var listOfCategory = arrayOf(
        "esports",
        "3d",
        "abstract",
        "alphabet",
        "architecture",
        "ecommerce",
        "food",
        "law",
        "pets",
    )

    @JvmStatic
    val categoryMap: HashMap<String, Int> =
        hashMapOf(
            "3d" to 10,
            "abstract" to 10,
            "alphabet" to 10,
            "architecture" to 10,
            "ecommerce" to 10,
            "esports" to 10,
            "food" to 10,
            "law" to 10,
            "pets" to 10,
            "shipping" to 10,
            "shape" to 10
        )
}