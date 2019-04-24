package com.simplemobiletools.camera.debug


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simplemobiletools.camera.R
import com.simplemobiletools.camera.activities.MainActivity
import com.simplemobiletools.camera.interfaces.AddTextFragmentListener

class TextFragment : BottomSheetDialogFragment() {

    internal var listener: AddTextFragmentListener?=null

    var edit_add_text:EditText?=null
    var btn_done: Button?=null

    fun setListener(listener: AddTextFragmentListener){
        this.listener=listener
    }

    companion object {
        internal var instance:TextFragment?=null
        fun getInstance():TextFragment{
            if (instance == null) {
                instance = TextFragment()
            }
            return instance!!
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return  inflater.inflate(R.layout.fragment_add_text, container, false)


    }


}
