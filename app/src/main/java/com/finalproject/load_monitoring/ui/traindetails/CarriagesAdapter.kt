package com.finalproject.load_monitoring.ui.traindetails

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.finalproject.load_monitoring.R
import com.finalproject.load_monitoring.models.CarriageModel
import com.finalproject.load_monitoring.models.OccupancyLevel
import com.google.android.material.textview.MaterialTextView

class CarriagesAdapter(
    private val items: List<CarriageModel>
) : RecyclerView.Adapter<CarriagesAdapter.CarriageViewHolder>() {

    inner class CarriageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCarriageNumber: MaterialTextView = itemView.findViewById(R.id.tvCarriageNumber)
        val imgOccupancy: AppCompatImageView = itemView.findViewById(R.id.imgCarriageOccupancy)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarriageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carriage, parent, false)
        return CarriageViewHolder(view)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    override fun onBindViewHolder(holder: CarriageViewHolder, position: Int) {
        val carriage = items[position]

        holder.tvCarriageNumber.text = carriage.carriageNumber.toString()

        val (drawableRes, colorRes) = when (carriage.occupancy) {
            OccupancyLevel.LOW -> R.drawable.ic_low_load to R.color.green
            OccupancyLevel.MEDIUM -> R.drawable.ic_medium_load to R.color.yellow
            OccupancyLevel.HIGH -> R.drawable.ic_high_load to R.color.red
        }

        holder.imgOccupancy.setImageResource(drawableRes)
        holder.imgOccupancy.setColorFilter(
            ContextCompat.getColor(holder.itemView.context, colorRes),
            PorterDuff.Mode.SRC_IN
        )
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    override fun getItemCount(): Int = items.size
}
