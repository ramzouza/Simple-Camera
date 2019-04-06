package com.simplemobiletools.camera.Adapter

import android.content.Context
import android.graphics.Typeface

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.simplemobiletools.camera.R
import java.lang.StringBuilder

class FontAdapter(internal var context: Context,
                  internal var listener:FontAdapter.FontAdapterClickListener) :
                    RecyclerView.Adapter<FontAdapter.FontViewViewHolder>(){

    var row_selected = -1;

    override fun onCreateViewHolder(parent:ViewGroup, viewType : Int):FontViewViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.font_item, parent, false)
        return FontViewViewHolder(itemView)
    }

    override fun getItemCount() : Int {
        return fontList.size
    }

    override fun onBindViewHolder(holder: FontViewViewHolder, position : Int) {
        if(row_selected == position)
            holder.img_check.visibility = View.VISIBLE
        else
            holder.img_check.visibility = View.INVISIBLE

        val typeFace = Typeface.createFromAsset(context.assets, StringBuilder("fonts/")
                .append(fontList.get(position)).toString())

        holder.txt_font_name.text = fontList.get(position)
        holder.txt_font_demo.typeface = typeFace
    }

    internal var fontList:List<String>

    init {
        this.fontList = loadFontList()!!
    }

    private fun loadFontList(): List<String>? {
        var result = ArrayList<String>()

        result.add("impact.ttf")
        result.add("Impacted.ttf")
        result.add("unicode.impact.ttf")

        return result
    }

    interface FontAdapterClickListener {
        fun onFontSelected(fontName : String)
    }

    inner class FontViewViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        internal var txt_font_demo:TextView
        internal var txt_font_name:TextView
        internal var img_check:ImageView

        init {
            txt_font_demo = itemView.findViewById(R.id.txt_font_demo) as TextView
            txt_font_name = itemView.findViewById(R.id.txt_font_name) as TextView

            img_check = itemView.findViewById(R.id.img_check) as ImageView

            itemView.setOnClickListener {
                listener.onFontSelected(fontList[adapterPosition])
                row_selected = adapterPosition
                notifyDataSetChanged()
            }
        }
    }
}
