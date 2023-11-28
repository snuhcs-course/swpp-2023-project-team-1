package com.project.spire.ui.create

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.spire.R

class MaskFetchAdapter(
    private var masks: List<Bitmap>,
    private var labels: List<String>,
    private val canvasViewModel: CanvasViewModel,
    private val textColorDefault: Int,
    private val textColorClicked: Int
): RecyclerView.Adapter<MaskFetchAdapter.MaskFetchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaskFetchViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.mask_fetch_item, parent, false)

        return MaskFetchViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return labels.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: MaskFetchViewHolder, position: Int) {
        if (labels.isEmpty() || masks.isEmpty()) return
        val label = labels[position]
        val mask = masks[position]
        holder.button.text = label
        holder.button.setOnClickListener {
            if (!holder.isClicked) {
                Log.d("MaskFetchAdapter", "Button clicked")
                canvasViewModel.applyFetchedMask(mask)
                holder.button.setBackgroundResource(R.drawable.btn_mask_fetch_clicked_bg)
                holder.button.setTextColor(textColorClicked)
                holder.isClicked = true
            }
            else {
                Log.d("MaskFetchAdapter", "Button unclicked")
                canvasViewModel.applyFetchedMask(mask, true)
                holder.button.setBackgroundResource(R.drawable.btn_mask_fetch_bg)
                holder.button.setTextColor(textColorDefault)
                holder.isClicked = false
            }
        }
    }

    inner class MaskFetchViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.mask_fetch_button)
        var isClicked: Boolean = false
    }

    fun updateList(newMasks: List<Bitmap>, newLabels: List<String>) {
        masks = newMasks
        labels = newLabels
        //notifyItemInserted(labels.size - 1)
        notifyDataSetChanged()
    }
}