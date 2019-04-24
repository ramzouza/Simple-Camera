package com.simplemobiletools.camera.extensions
import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

open class DoubleTapListener(ctx: Context) : View.OnTouchListener {

    private val gestureDetector: GestureDetector

    companion object {

        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100
    }

    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {


        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            var result = false
            super.onDoubleTap(e)
            try {
                onDoubleTap()
                result = true
            }
            catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }



    }

    open fun onDoubleTap() {}
}
