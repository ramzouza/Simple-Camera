package com.simplemobiletools.camera.Adapter

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import com.simplemobiletools.commons.extensions.getLatestMediaId
import com.simplemobiletools.commons.extensions.toast
import java.io.IOException

class FirebaseVisionAdapter {

    private var context : Context
    constructor(context : Context){
        this.context = context
    }

    fun vision(file_uri : Uri, handler : (best : String) -> Unit){
        val image: FirebaseVisionImage

        try {
            image = FirebaseVisionImage.fromFilePath(context, file_uri)
            val labeler = FirebaseVision.getInstance().getCloudImageLabeler();
            labeler.processImage(image).addOnSuccessListener { labels ->

                Log.i("INFO", labels.size.toString())
                var highestConfidence: Float = 0f;
                var bestLabel = -1
                var best: FirebaseVisionImageLabel? = null;
                var i = 0;
                for (label in labels) {
                    val text = label.text
                    val entityId = label.entityId
                    val confidence = label.confidence
                    if (confidence > highestConfidence) {
                        highestConfidence = confidence
                        bestLabel = i;
                        best = label;
                    }
                    Log.i("INFO", text.toString() + " | " + i.toString() + " | " + confidence.toString())
                    i++
                }
                if (best != null) {
                    Log.i("INFO", best?.entityId);
                    context.toast(best.text)
                    handler(best.text);
                };
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}