package com.example.focusbuddy.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.focusbuddy.R

class AppChoiceAdapter(
    private val appList: List<AppInfo>,
    private val onSelectionChanged: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppChoiceAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.ivIcon)
        val name: TextView = view.findViewById(R.id.tvAppName)
        val checkbox: CheckBox = view.findViewById(R.id.cbAppSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_choice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = appList[position]
        holder.icon.setImageDrawable(app.icon)
        holder.name.text = app.name
        holder.checkbox.isChecked = app.isSelected

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            app.isSelected = isChecked
            onSelectionChanged(app)
        }
    }

    override fun getItemCount() = appList.size
}