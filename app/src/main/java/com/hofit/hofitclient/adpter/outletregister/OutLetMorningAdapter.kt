package com.hofit.hofitclient.adpter.outletregister

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hofit.hofitclient.R
import com.hofit.hofitclient.model.TimeSlot

class OutLetMorningAdapter(private var timeSlot: ArrayList<TimeSlot>) : RecyclerView.Adapter<OutLetMorningAdapter.OutletViewModel>(){

    @SuppressLint("NotifyDataSetChanged")
    fun setEmployees(timeSlot: ArrayList<TimeSlot>) {
        this.timeSlot = ArrayList()
        this.timeSlot = timeSlot
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutletViewModel {
        return OutletViewModel(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_time_slot, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OutletViewModel, position: Int) {
        holder.bind(timeSlot[position])
    }

    override fun getItemCount(): Int {
        return timeSlot.size
    }

    class OutletViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeCheck: ImageView = itemView.findViewById(R.id.imageView6)
        private val timeName: TextView = itemView.findViewById(R.id.outlet_timing)

        fun bind(timeSlot: TimeSlot) {
            timeCheck.visibility = if (timeSlot.isChecked) View.VISIBLE else View.GONE
            timeName.text = timeSlot.name
            itemView.setOnClickListener {
                timeSlot.isChecked =  (!timeSlot.isChecked)
                timeCheck.visibility = if (timeSlot.isChecked) View.VISIBLE else View.GONE
            }
        }

    }

    fun getAll(): ArrayList<TimeSlot> {
        return timeSlot
    }

    fun getSelected(): ArrayList<TimeSlot> {
        val selected: ArrayList<TimeSlot> = ArrayList()
        for (i in 0 until timeSlot.size) {
            if (timeSlot[i].isChecked) {
                selected.add(timeSlot[i])
            }
        }
        return selected
    }

}