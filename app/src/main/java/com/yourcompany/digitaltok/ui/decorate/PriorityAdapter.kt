package com.yourcompany.digitaltok.ui.decorate

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yourcompany.digitaltok.R

class PriorityAdapter(
    private val items: List<TemplateItem>,
    private val onItemClick: (TemplateItem) -> Unit
) : RecyclerView.Adapter<PriorityAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_station_template, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivThumb: ImageView = itemView.findViewById(R.id.ivThumb)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvName) // R.id.tvTitle -> R.id.tvName
        private val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position])
                }
            }
        }

        fun bind(item: TemplateItem) {
            tvTitle.text = item.title
            tvDesc.text = item.desc

            Log.d("PriorityAdapter", "Binding item: title='${item.title}', url='${item.thumbUrl}'")

            if (!item.thumbUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(item.thumbUrl)
                    .placeholder(R.drawable.blank_img)
                    .error(R.drawable.blank_img)
                    .into(ivThumb)
            } else if (item.thumbRes != 0) {
                ivThumb.setImageResource(item.thumbRes)
            } else {
                ivThumb.setImageResource(R.drawable.blank_img)
            }
        }
    }
}
