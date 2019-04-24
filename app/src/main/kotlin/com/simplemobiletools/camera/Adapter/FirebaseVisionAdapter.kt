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
import java.lang.Exception

open class FirebaseVisionAdapter {

    private var context : Context
    constructor(context : Context){
        this.context = context
    }

    fun visionLabel(file_uri : Uri, handler : (best : String) -> Unit){
        val image: FirebaseVisionImage

        try {
            image = FirebaseVisionImage.fromFilePath(context, file_uri)
            val labeler = FirebaseVision.getInstance().getCloudImageLabeler();
            labeler.processImage(image).addOnSuccessListener { labels ->

                val best = findBest(labels);

                if (best != null) {
                    context.toast(best.text)
                    handler(best.text);
                };
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun visionText(file_uri: Uri, handler: (result : String) -> Unit){
        val image: FirebaseVisionImage

        try {
            image = FirebaseVisionImage.fromFilePath(context, file_uri)
            val detector = FirebaseVision.getInstance().getCloudDocumentTextRecognizer();
            detector.processImage(image).addOnSuccessListener { result ->
                try{
                    val resultText = result.text
                    Log.i("INFO1", resultText);
                    handler(resultText);
                } catch (e : Exception) {
                    handler("");
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    public fun findBest(labels : Collection<FirebaseVisionImageLabel>) : FirebaseVisionImageLabel? {
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
            i++
        }
        return best;
    }


}
