package com.finalproject.load_monitoring.ui.trainslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.finalproject.load_monitoring.R
import com.finalproject.load_monitoring.models.TrainModel
import com.finalproject.load_monitoring.utils.DateFormatUtils
import com.google.android.material.textview.MaterialTextView

// The Adapter is the class that receives a list of data from the ViewModel
// and translates each object in the list into a row/card on the screen (RecyclerView).
class TrainCardAdapter(
    private var items: List<TrainModel>,
    private val onTrainClick: (TrainModel) -> Unit
) : RecyclerView.Adapter<TrainCardAdapter.TrainViewHolder>() {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    class TrainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTrainNumber: MaterialTextView = itemView.findViewById(R.id.tvTrainNumber)
        val tvDepartureTime: MaterialTextView = itemView.findViewById(R.id.tvDepartureTime)
        val tvArrivalTime: MaterialTextView = itemView.findViewById(R.id.tvArrivalTime)
        val tvPlatformDeparture: MaterialTextView = itemView.findViewById(R.id.tvPlatformOrigin)
        val tvPlatformDestination: MaterialTextView =
            itemView.findViewById(R.id.tvPlatformDestination)
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_train_card, parent, false)
        return TrainViewHolder(view)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onBindViewHolder(holder: TrainViewHolder, position: Int) {
        val train = items[position]

        holder.tvTrainNumber.text = train.trainID
//        holder.tvDepartureTime.text = train.departureTime
//        holder.tvArrivalTime.text = train.arrivalTime
        holder.tvDepartureTime.text = DateFormatUtils.formatStringTime(train.departureTime)
        holder.tvArrivalTime.text = DateFormatUtils.formatStringTime(train.arrivalTime)
        holder.tvPlatformDeparture.text = holder.itemView.context.getString(
            R.string.platform_number,
            train.originPlatform
        )
        holder.tvPlatformDestination.text =
            holder.itemView.context.getString(
                R.string.platform_number,
                train.destinationPlatform
            )

        holder.itemView.setOnClickListener { // When the user clicks on the card
            onTrainClick(train)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<TrainModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}