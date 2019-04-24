package com.simplemobiletools.camera.Utils
import android.util.Log
import java.lang.Exception
import java.util.regex.Pattern

fun isHyperlink(word : String) : Boolean {

    val regexStr = "[^\\s]*(\\.com|\\.ca|\\.net|\\.io|\\.fr|\\.uk|\\.pdf|\\.png|\\.jpg|\\.dev)[^\\s]*";
    //Log.i("INFO", word);
    //Log.i("INFO", Pattern.matches(regexStr,word).toString());
    return Pattern.matches(regexStr,word);
}
