package com.simplemobiletools.camera.dialogs

import android.app.AlertDialog
import android.app.AlertDialog.*
import android.net.Uri
import android.content.*

import com.simplemobiletools.commons.extensions.toast
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context.CLIPBOARD_SERVICE


class SmartHubDialog {

    private var context : Context
    private var dialogBuilder : AlertDialog.Builder;

    constructor(context : Context){
        this.context = context
        this.dialogBuilder = AlertDialog.Builder(context , THEME_HOLO_DARK);

    }

    fun build(title : String, message: String, urlLabel: String, url : String, clipboardLabel : String, clipboardToast: String, clipboardContent: String){

        dialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(urlLabel, DialogInterface.OnClickListener { dialog, id ->
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse(url)
                    context.startActivity(openURL)
                })
                .setNeutralButton(clipboardLabel, DialogInterface.OnClickListener { dialog, id ->
                    val clip = ClipData.newPlainText(clipboardLabel, clipboardContent);
                    val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.primaryClip = clip
                    context.toast("Copied ${clipboardToast} to clipboard.")
                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> dialog.dismiss()
                })

        val alert = dialogBuilder.create()

        alert.show()
    }



}
