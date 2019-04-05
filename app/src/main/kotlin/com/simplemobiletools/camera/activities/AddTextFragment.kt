package com.simplemobiletools.camera.activities


import android.os.Bundle
import android.app.Fragment
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simplemobiletools.camera.R
import com.simplemobiletools.camera.interfaces.AddTextFragmentListener


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AddTextFragment : BottomSheetDialogFragment() {

    var colorSelected:Int = Color.parseColor("#000000")

    internal var listener:AddTextFragmentListener?=null

    fun setListener(listener:AddTextFragmentListener){
        this.listener =listener
    }

    var edt_add_text:EditText?=null
    var btn_done:Button?=null


    companion object {
        internal var instance: AddTextFragment?=null

        fun getInstance():AddTextFragment{
            if(instance ==null){
                instance = AddTextFragment()
            }
            return instance!!
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var itemView = inflater.inflate(R.layout.fragment_add_text, container, false)


        edt_add_text = itemView.findViewById<EditText>(R.id.edt_add_text)
        btn_done = itemView.findViewById<Button>(R.id.btn_done)


        btn_done!!.setOnClickListener{
            listener!!.onAddTextListener(edt_add_text!!.text.toString(),Color.parseColor("#FFFFFF"))
        }

        return itemView
    }


}
