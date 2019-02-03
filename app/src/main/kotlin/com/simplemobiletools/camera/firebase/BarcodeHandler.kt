package com.simplemobiletools.camera.firebase

import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import android.graphics.Bitmap



 class BarcodeHandler() {

     companion object {


         fun initBarcodeHandler(bitmap: Bitmap) {
             val image = FirebaseVisionImage.fromBitmap(bitmap)
             scanBarcode(image)
         }

         fun  scanBarcode(image: FirebaseVisionImage) {
             // created object that can only detect traditional QC codes

             println("Got inside the function")
             val view = FirebaseVisionBarcodeDetectorOptions.Builder()
                     .setBarcodeFormats(
                             FirebaseVisionBarcode.FORMAT_QR_CODE
                     )
                     .build()


             val detector = FirebaseVision.getInstance()
                     .getVisionBarcodeDetector(view)

             val result = detector.detectInImage(image)
                     .addOnSuccessListener { barcodes ->
                         println("successfull")
                         println(barcodes[0].getRawValue())
                         // Task completed successfully
                         // [START_EXCLUDE]
                         // [START get_barcodes]
                         for (barcode in barcodes) {

                             val bounds = barcode.boundingBox
                             val corners = barcode.cornerPoints
                             val rawValue = barcode.rawValue
                             val valueType = barcode.valueType


                             // list of supported types can be found :
                             // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/barcode/FirebaseVisionBarcode.html#TYPE_URL
                             when (valueType) {
                                 FirebaseVisionBarcode.TYPE_WIFI -> {
                                     val ssid = barcode.wifi!!.ssid
                                     val password = barcode.wifi!!.password
                                     val type = barcode.wifi!!.encryptionType
                                 }
                                 FirebaseVisionBarcode.TYPE_URL -> {
                                     val title = barcode.url!!.title
                                     val url = barcode.url!!.url
                                     println("url test")
                                     println(url)
                                 }

                             }
                         }
                         // [END get_barcodes]
                         // [END_EXCLUDE]
                     }
                     .addOnFailureListener {
                        println("error")
                         // Task failed with an exception
                         // ...
                     }
             // [END run_detector]
             println("this is the test for result " + result)


         }


     }
}
























