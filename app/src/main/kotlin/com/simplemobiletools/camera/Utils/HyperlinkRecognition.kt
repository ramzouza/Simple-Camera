package com.simplemobiletools.camera.Utils
import java.lang.Exception
import java.net.URL

fun isHyperlink(word : String) : Boolean {
    try{
        URL(word).toURI()
        return true;
    } catch (e : Exception){
        return false;
    }
}
