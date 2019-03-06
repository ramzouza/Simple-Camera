package com.simplemobiletools.camera.Utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.provider.MediaStore
import android.icu.text.MeasureFormat
import android.net.Uri
import android.util.Log
import com.simplemobiletools.camera.activities.MainActivity
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.security.CodeSource

object BitmapTools {


    fun getPicture(context: Context,path:Uri,width: Int,height: Int):Bitmap{

        val column = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(path,column,null,null,null)
        cursor!!.moveToFirst()
        val columnIndex = cursor.getColumnIndex(column[0])
        val pathPic = cursor.getString(columnIndex)
        cursor.close()

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathPic,options)
        options.inSampleSize = calculateSize(options,width,height)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(pathPic,options)
    }
}
