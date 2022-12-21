package com.net.firstmodule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.net.routee.database.LocationDataClass

class LocationListViewAdapter(private val list : ArrayList<LocationDataClass>) : RecyclerView.Adapter<LocationListViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.location_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item:LocationDataClass = list[position]

        holder.latitude.text = item.latitude.toString()
        holder.longitude.text = item.longitude.toString()
        holder.time.text = item.dateTime.toString()
    }

    override fun getItemCount(): Int = list.size
    fun addLocations(locations: List<LocationDataClass>?) {
        if (locations != null) {
            for (i in list.size until locations.size) {
                list.add(locations[i])
                notifyItemChanged(i)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val latitude: TextView = itemView.findViewById(R.id.txtLatitude)
        val longitude: TextView = itemView.findViewById(R.id.txtLongitude)
        val time: TextView = itemView.findViewById(R.id.txtTime)
    }
}