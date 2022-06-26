package com.example.backgrounderaser.recyclerAdapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.backgrounderaser.R
import com.example.backgrounderaser.interfaceCallBack.StickerClick
import com.example.backgrounderaser.utils.loadThumbnail


class ShapeAdapter(callBack: StickerClick) : RecyclerView.Adapter<ShapeAdapter.ViewHolder>() {

    private val itemCallBAck: StickerClick = callBack
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShapeAdapter.ViewHolder {
        context = parent.context

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layer_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShapeAdapter.ViewHolder, position: Int) {

        val path = "file:///android_asset/bgScr/${position + 1}.webp"

        holder.thumbNail.loadThumbnail(path, null)
    }

    override fun getItemCount(): Int {
        return 28
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var thumbNail: ImageView

        init {

            thumbNail = itemView.findViewById(R.id.imageView52)

            itemView.setOnClickListener {
                itemCallBAck.setOnStickerClickListener(adapterPosition + 1, true)
            }

        }

    }

}