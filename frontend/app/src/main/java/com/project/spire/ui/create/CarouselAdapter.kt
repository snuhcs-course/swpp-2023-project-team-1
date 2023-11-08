package com.project.spire.ui.create

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.spire.R

class CarouselAdapter(
    private val carouselList: ArrayList<Bitmap> // CarouselData
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.carousel_item, parent, false)

        return CarouselViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return carouselList.size
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val item = carouselList[position]
        holder.image.load(item)
    }

    inner class CarouselViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView
        // val text: TextView

        init {
            image = view.findViewById(R.id.list_item_image)
        }
    }


}