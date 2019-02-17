package com.simplemobiletools.camera.activities

import android.os.Bundle
import android.view.LayoutInflater
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.view.*
import com.simplemobiletools.camera.R
import kotlinx.android.synthetic.main.activity_main.*

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri


class ScanActivity : SimpleActivity(), ZXingScannerView.ResultHandler{
    private var mScannerView: ZXingScannerView? = null

    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        mScannerView = ZXingScannerView(this)   // Programmatically initialize the scanner view
        setContentView(mScannerView)                // Set the scanner view as the content view
    }

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera()          // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()           // Stop camera on pause
    }

    fun navigate(url: String){
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        startActivity(openURL)
    }

    fun cancel(dialog : DialogInterface){
        dialog.cancel()
        mScannerView!!.resumeCameraPreview(this)
    }

    override fun handleResult(rawResult: Result) {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)

        handleResultController(rawResult.text, dialogBuilder);
        // If you would like to resume scanning, call this method below:
//         mScannerView!!.resumeCameraPreview(this);
    }

    fun handleResultController(result : String, dialogBuilder : AlertDialog.Builder){

        // set message of alert dialog
        dialogBuilder
                .setTitle("Link Found")
                .setMessage("Do you want to visit this link: " + result +" ?")
                // if the dialog is cancelable
                .setCancelable(true)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                    dialog, id -> navigate(result)
                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> cancel(dialog)
                })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        // show alert dialog
        alert.show()
    }
}


