package com.simplemobiletools.camera.activities


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simplemobiletools.camera.Adapter.ThumbnailAdapter
import com.simplemobiletools.camera.R
import com.simplemobiletools.camera.interfaces.FilterListInterface
import com.simplemobiletools.camera.Utils.BitmapTools
import com.simplemobiletools.camera.Utils.SpaceItemDecoration
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import kotlinx.android.synthetic.main.filter_list.*


/**
 * A simple [Fragment] subclass.
 *
 */
class FilterList : BottomSheetDialogFragment(),FilterListInterface {
    internal lateinit var recycler_view: RecyclerView
    internal var listener : FilterListInterface ?=null
    internal lateinit var adapter:ThumbnailAdapter
    internal lateinit var thumbnailItemList: MutableList<ThumbnailItem>

    companion object {
        internal var instance:FilterList?=null

        fun getInstance():FilterList{
            if(instance == null){
                instance = FilterList()
            }
            return instance!!
        }
    }

    fun setListener(listFragmentListener: FilterListInterface){
        this.listener = listFragmentListener
    }

    override fun onFilterSelected(filter: Filter) {
        if(listener != null)
            listener!!.onFilterSelected(filter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val itemView = inflater.inflate(R.layout.filter_list, container, false)

        thumbnailItemList = ArrayList()
        adapter = ThumbnailAdapter((activity as Context?)!!,thumbnailItemList,this)

        recycler_view = itemView.findViewById<RecyclerView>(R.id.recycler_view)
        recycler_view.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        recycler_view.itemAnimator = DefaultItemAnimator()
        val space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8f,resources.displayMetrics).toInt()
        recycler_view.addItemDecoration(SpaceItemDecoration(space))
        recycler_view.adapter = adapter

        displayImage(null,Uri.parse("flash.jpg"))

        return itemView
    }

    fun displayImage(bitmap: Bitmap?,path: Uri) {
        val r = Runnable {
            val thumbImage : Bitmap?
            Log.d("INSIDE DISPLAY, ",bitmap.toString())
            if(bitmap == null)
                thumbImage = BitmapTools.getPicture(activity!!, MainActivity.Main.IMAGE_FILTER,100,100)
            else{
                thumbImage = BitmapTools.getPicture(activity!!,path,100,100)}
            if(thumbImage == null)
                return@Runnable

            ThumbnailsManager.clearThumbs()
            thumbnailItemList.clear()

            //add normal bitmap first
            val thumbnailItem = ThumbnailItem()
            thumbnailItem.image = thumbImage
            thumbnailItem.filterName = "Normal"
            ThumbnailsManager.addThumb(thumbnailItem)

            //add Filter pack

            val filters = FilterPack.getFilterPack(activity!!)

            for(filter in filters){
                val item = ThumbnailItem()
                item.image = thumbImage
                item.filter = filter
                item.filterName = filter.name
                ThumbnailsManager.addThumb(item)
            }

            thumbnailItemList.addAll(ThumbnailsManager.processThumbs(activity))
            activity!!.runOnUiThread{
                adapter.notifyDataSetChanged()
            }


        }
        Thread(r).start()

    }


}
