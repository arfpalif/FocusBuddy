package com.example.focusbuddy.ui.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.focusbuddy.R
import com.example.focusbuddy.data.AppUsageInfo

class AppsAdapter(private val usageInfo: List<AppUsageInfo>, private val maxUsageInfo: Long) :
    RecyclerView.Adapter<AppsAdapter.ViewHolder>() {

    init {
        Log.d("AppsAdapter", "Created adapter with ${usageInfo.size} items")
        Log.d("AppsAdapter", "Max usage: $maxUsageInfo")
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        val tvAppName: TextView = itemView.findViewById(R.id.tvAppName)
        val progressUsage: ProgressBar = itemView.findViewById(R.id.progressUsage)
        val waktu: TextView = itemView.findViewById(R.id.tvAppName3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("AppsAdapter", "Creating ViewHolder")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_use, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("AppsAdapter", "getItemCount: ${usageInfo.size}")
        return usageInfo.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("AppsAdapter", "Binding view at position $position")

        val app = usageInfo[position]

        // Set data ke view
        holder.ivIcon.setImageDrawable(app.icon)
        holder.tvAppName.text = app.appName

        // Hitung persentase untuk progress bar
        val usagePercentage = if (maxUsageInfo > 0) {
            (app.usageTime.toDouble() / maxUsageInfo.toDouble() * 100).toInt()
        } else {
            0
        }

        // Set progress
        holder.progressUsage.max = 100
        holder.progressUsage.progress = usagePercentage

        Log.d("AppsAdapter", "Bound data for ${app.appName} with usage $usagePercentage%")
    }
}