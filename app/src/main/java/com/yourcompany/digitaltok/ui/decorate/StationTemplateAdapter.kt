package com.yourcompany.digitaltok.ui.decorate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yourcompany.digitaltok.R

class TemplateAdapter(
    private val items: List<TemplateItem>,
    private val onClick: (TemplateItem) -> Unit
) : RecyclerView.Adapter<TemplateAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_station_template, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.thumb.setImageResource(item.thumbRes)
        holder.title.text = item.title
        holder.desc.text = item.desc

        holder.itemView.setOnClickListener { onClick(item) }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val thumb: ImageView = view.findViewById(R.id.ivThumb)
        val title: TextView = view.findViewById(R.id.tvName)
        val desc: TextView = view.findViewById(R.id.tvDesc)
        val divider: View = view.findViewById(R.id.divider)
    }
}
