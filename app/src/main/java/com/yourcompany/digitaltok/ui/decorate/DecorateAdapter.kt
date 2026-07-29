package com.yourcompany.digitaltok.ui.decorate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yourcompany.digitaltok.R

class DecorateAdapter(
    private var items: List<DecorateItem>,
    private val onItemClick: (DecorateItem) -> Unit,
    private val onFavoriteClick: (String, Boolean) -> Unit // (imageId, isFavorite)
) : RecyclerView.Adapter<DecorateAdapter.DecorateViewHolder>() {

    private var selectedId: String? = null

    /**
     * ViewModel로부터 새로운 목록을 받아 UI를 갱신합니다.
     * 이제 모든 데이터 관리는 ViewModel이 담당합니다.
     */
    fun submitList(newItems: List<DecorateItem>) {
        items = newItems
        // 선택 상태는 유지하거나 초기화할 수 있습니다. 여기서는 초기화합니다.
        selectedId = null
        notifyDataSetChanged()
    }

    /** 선택된 아이템 반환 */
    fun getSelectedItem(): DecorateItem? {
        return items.firstOrNull { it.id == selectedId }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DecorateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_decorate_grid, parent, false)
        return DecorateViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DecorateViewHolder, position: Int) {
        val item = items[position]

        // --- 썸네일 이미지 로딩 (Glide 사용) ---
        if (!item.previewUrl.isNullOrEmpty()) {
            holder.ivThumb.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(item.previewUrl)
                .placeholder(R.drawable.ic_launcher_background) // 로딩 중 이미지
                .into(holder.ivThumb)
        } else if (item.imageUri != null) {
            holder.ivThumb.visibility = View.VISIBLE
            holder.ivThumb.setImageURI(item.imageUri)
        } else {
            holder.ivThumb.setImageDrawable(null)
            holder.ivThumb.visibility = View.GONE
        }

        // --- 즐겨찾기(별) 상태 표시 ---
        holder.ivStar.visibility = View.VISIBLE
        holder.ivStar.isSelected = item.isFavorite
        holder.ivStar.bringToFront()

        // --- 선택 테두리 표시 ---
        holder.viewSelectedBorder.visibility =
            if (item.id == selectedId) View.VISIBLE else View.GONE

        // --- 클릭 리스너 ---
        holder.itemView.setOnClickListener {
            selectedId = if (selectedId == item.id) null else item.id
            notifyDataSetChanged() // 선택 테두리 UI 갱신
            onItemClick(item)
        }

        // 별 클릭 시, ViewModel에만 알림
        holder.ivStar.setOnClickListener {
            onFavoriteClick(item.id, !item.isFavorite)
        }
    }

    class DecorateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivThumb: ImageView = itemView.findViewById(R.id.ivThumb)
        val ivStar: ImageView = itemView.findViewById(R.id.ivStar)
        val viewSelectedBorder: View = itemView.findViewById(R.id.viewSelectedBorder)
    }
}
