package com.simplemobiletools.camera.firebase
//
//import com.google.firebase.ml.vision.FirebaseVision
//import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
//import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
//import com.google.firebase.ml.vision.common.FirebaseVisionImage
//import java.io.IOException
//import android.net.Uri
//import com.simplemobiletools.camera.extensions.config
//import android.content.Context
//import java.io.File
//import android.app.Activity
//import android.content.Context.CAMERA_SERVICE
//import android.hardware.camera2.CameraAccessException
//import android.hardware.camera2.CameraCharacteristics
//import android.hardware.camera2.CameraManager
//import android.media.Image
//import android.os.Build
////import android.support.annotation.RequiresApi
//import android.util.Log
//import android.util.SparseIntArray
//import android.view.Surface
//import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
////import android.media.Image
//
 class BarcodeHandler() {
//
//     companion object {
//
//         private val ORIENTATIONS = SparseIntArray()
//
//         init {
//             ORIENTATIONS.append(Surface.ROTATION_0, 90)
//             ORIENTATIONS.append(Surface.ROTATION_90, 0)
//             ORIENTATIONS.append(Surface.ROTATION_180, 270)
//             ORIENTATIONS.append(Surface.ROTATION_270, 180)
//         }
//         /**
//          * Get the angle by which an image must be rotated given the device's current
//          * orientation.
//          */
////         @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//         @Throws(CameraAccessException::class)
//         private fun getRotationCompensation(cameraId: String, activity: Activity, context: Context): Int {
//             // Get the device's current rotation relative to its "native" orientation.
//             // Then, from the ORIENTATIONS table, look up the angle the image must be
//             // rotated to compensate for the device's rotation.
//             val deviceRotation = activity.windowManager.defaultDisplay.rotation
//             var rotationCompensation = ORIENTATIONS.get(deviceRotation)
//
//             // On most devices, the sensor orientation is 90 degrees, but for some
//             // devices it is 270 degrees. For devices with a sensor orientation of
//             // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
//             val cameraManager = context.getSystemService(CAMERA_SERVICE) as CameraManager
//             val sensorOrientation = cameraManager
//                     .getCameraCharacteristics(cameraId)
//                     .get(CameraCharacteristics.SENSOR_ORIENTATION)!!
//             rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360
//
//             // Return the corresponding FirebaseVisionImageMetadata rotation value.
//             val result: Int
//             when (rotationCompensation) {
//                 0 -> result = FirebaseVisionImageMetadata.ROTATION_0
//                 90 -> result = FirebaseVisionImageMetadata.ROTATION_90
//                 180 -> result = FirebaseVisionImageMetadata.ROTATION_180
//                 270 -> result = FirebaseVisionImageMetadata.ROTATION_270
//                 else -> {
//                     result = FirebaseVisionImageMetadata.ROTATION_0
//                 }
//             }
//             return result
//         }
//
//
//         fun initBarcodeHandler(context: Context,imagePath: String) {
//             val imageUri = Uri.fromFile(File(imagePath))
//
//             val image: FirebaseVisionImage
//
//             try {
//
//
//
//
//
////                 System.out.println("*******************************************")
////                 System.out.println("Printing the image object returned from firebase:")
////                 System.out.println("*******************************************")
//
//                 val options = FirebaseVisionBarcodeDetectorOptions.Builder()
//                         .setBarcodeFormats(
//                                 FirebaseVisionBarcode.FORMAT_QR_CODE)
//                         .build()
//
//                 val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
//                 var link : String
////                 val rawBarcodeValue
//                 val result = detector.detectInImage(image)
//                         .addOnSuccessListener { barcodes ->
//                             for (barcode in barcodes) {
//                                 val bounds = barcode.boundingBox
//                                 val corners = barcode.cornerPoints
//
//                                 val rawValue = barcode.rawValue
//
//                                 val valueType = barcode.valueType
//                                 // See API reference for complete list of supported types
//                                 when (valueType) {
//                                     FirebaseVisionBarcode.TYPE_WIFI -> {
//                                         val ssid = barcode.wifi!!.ssid
//                                         val password = barcode.wifi!!.password
//                                         val type = barcode.wifi!!.encryptionType
//                                     }
//                                     FirebaseVisionBarcode.TYPE_URL -> {
//                                         val title = barcode.url!!.title
//                                         val url = barcode.url!!.url
//                                     }
//                                 }
//                                 println("----------------------------------------------------")
//                                 println("This is the link "+ rawValue)
//                                 println("----------------------------------------------------")
//
//                             }
//
//
//                         }
//                         .addOnFailureListener {
//                             // Task failed with an exception
//                             // ...
//
//                             println("******************************________________________________***********")
//                             println("FAILED TO SCAN")
//                         }
//
//                 println("----------------------------------------------------")
//                 println("This is the link ")
//                 println("----------------------------------------------------")
//
//             } catch (e: IOException) {
//                 e.printStackTrace()
//             }
//
//
//
//
//         }
//
//
//
//     }
}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
