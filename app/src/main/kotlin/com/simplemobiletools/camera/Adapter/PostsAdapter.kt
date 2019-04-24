package com.simplemobiletools.camera.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.simplemobiletools.camera.R

import com.simplemobiletools.camera.activities.MainActivity
import com.simplemobiletools.camera.interfaces.CustomItemClickListener
import com.simplemobiletools.camera.models.ModelRecyclerView


class PostsAdapter (dataModel:ArrayList<ModelRecyclerView>, context : Context): RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {



    var dataModel : ArrayList<ModelRecyclerView> = dataModel

    private val mContext=  context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val view : View= LayoutInflater.from(parent.context).inflate(R.layout.row_post, parent , false)

        return PostsViewHolder(view)
    }


    override fun getItemCount() = dataModel.size


    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        var item = dataModel[position]
        var name = item.mName
        var image = item.mImage


        holder!!.NameFeature.text = name
        holder.ImageFeature.setImageResource(image)

        holder.setOnCustomItemClickListener(object:CustomItemClickListener{
            override fun onCustomItemClickListener(view: View, pos: Int) {

                if(pos ==0){
                    (mContext as MainActivity).qr_code()
                }else if(pos==1){
                    (mContext as MainActivity).startFilter("Filter")
                }else if(pos==2){
                    (mContext as MainActivity).detect_object()
                }else if(pos==3){
                    (mContext as MainActivity).startFilter("memeGen")
                } else if (pos == 4){
                    (mContext as MainActivity).scan_hyperlink();
                }
            }

        })


      }


    class PostsViewHolder(itemView : View) :RecyclerView.ViewHolder(itemView), View.OnClickListener {


        var NameFeature : TextView
        var ImageFeature : ImageView
        var CustomItemClickListener :CustomItemClickListener?=null

        init {
            NameFeature = itemView.findViewById(R.id.NameFeature)
            ImageFeature = itemView.findViewById(R.id.ImageFeature)
            itemView.setOnClickListener(this)
        }

        fun setOnCustomItemClickListener(itemClickListener: CustomItemClickListener){
            this.CustomItemClickListener = itemClickListener
        }

        override fun onClick(p0: View?) {
         this.CustomItemClickListener!!.onCustomItemClickListener(p0!!,adapterPosition)

    }


}
}
