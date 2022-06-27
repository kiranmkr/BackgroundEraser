package com.background.eraserphotoeditor.ui


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.backgrounderaser.R
import com.example.backgrounderaser.databinding.ActivityMainBinding
import com.background.eraserphotoeditor.utils.Constant
import com.background.eraserphotoeditor.utils.Constants
import com.background.eraserphotoeditor.utils.StoreManager
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {


    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val viewTreeObserver = mainBinding.rootLayout.viewTreeObserver

        if (viewTreeObserver!!.isAlive) {

            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mainBinding.rootLayout.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                    Log.d(
                        "myWidth",
                        "${mainBinding.rootLayout.width} -- ${mainBinding.rootLayout.height}"
                    )

                    Constant.screenWidth = mainBinding.rootLayout.width.toFloat()

                }
            })
        }

        mainBinding.imagCap.setOnClickListener {
            Constant.cameraStatus = false
            StoreManager.setCurrentCropedBitmap(this, null)
            StoreManager.setCurrentCroppedMaskBitmap(this, null)
            openGallery()
        }

        mainBinding.cameraCap.setOnClickListener {
            Constant.cameraStatus = true
            StoreManager.setCurrentCropedBitmap(this, null)
            StoreManager.setCurrentCroppedMaskBitmap(this, null)
            openCamera()
        }
    }


    private fun openCamera() {

        if (EasyPermissions.hasPermissions(this, *Constant.cameraPermission)) {
            openCameraNewWay()
            //registerTakePictureLauncher(initTempUri())
        } else {
            EasyPermissions.requestPermissions(
                this, "We need permissions because this and that",
                Constant.CAMERA_IMAGE, *Constant.cameraPermission
            )
        }
    }

    private var cam_uri: Uri? = null

    private fun openCameraNewWay() {
        cam_uri = initTempUri()
        cam_uri?.let {
            takePicture.launch(it)
        }
    }

    private fun initTempUri(): Uri? {
        //gets the temp_images dir
        val tempImagesDir = File(
            applicationContext.filesDir, //this function gets the external cache dir
            getString(R.string.temp_images_dir)
        ) //gets the directory for the temporary images dir

        tempImagesDir.mkdir() //Create the temp_images dir

        //Creates the temp_image.jpg file
        val tempImage = File(
            tempImagesDir, //prefix the new abstract path with the temporary images dir path
            getString(R.string.temp_image)
        ) //gets the abstract temp_image file name

        return if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                applicationContext,
                Constant.fileProvider,
                tempImage
            )
        } else {
            Uri.fromFile(tempImage)
        }

    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->

            // The image was saved into the given Uri -> do something with it
            if (success) {

                if (cam_uri != null) {

                    val bitmap = Constants.getBitmapFromUri(
                        this,
                        cam_uri, Constant.screenWidth, Constant.screenWidth
                    )

                    if (bitmap != null) {
                        Constant.mainBitmap = bitmap
                        StoreManager.setCurrentOriginalBitmap(this, bitmap)
                        startActivity(Intent(this, SecondScreen::class.java))
                    }

                } else {
                    Log.d("myCameraImage", "image is not save")
                }

            }
        }

    private fun openGallery() {
        if (EasyPermissions.hasPermissions(this, *Constant.readPermission)) {
            openGalleryNewWay()
        } else {
            EasyPermissions.requestPermissions(
                this, "We need permissions because this and that",
                Constant.PICK_IMAGE, *Constant.readPermission
            )
        }
    }

    private fun openGalleryNewWay() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        getResult.launch(intent)
    }

    // Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val value = it.data?.data

                if (value != null) {

                    //val bitmap = scaleDown(getBitmap(value), screenWidth)
                    val bitmap = Constants.getBitmapFromUri(
                        this,
                        value, Constant.screenWidth, Constant.screenWidth
                    )

                    if (bitmap != null) {
                        Constant.mainBitmap = bitmap
                        StoreManager.setCurrentOriginalBitmap(this, bitmap)
                        startActivity(Intent(this, SecondScreen::class.java))
                    }
                }

            }
        }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

        Log.d("myPermission", "${Constant.cameraStatus}")

        if (Constant.cameraStatus) {
            Log.d("onPermissionsGranted", "open Camera")
            openCameraNewWay()
        } else {
            Log.d("onPermissionsGranted", "open Gallery")
            openGalleryNewWay()
        }
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("myPermission", "not allow")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}