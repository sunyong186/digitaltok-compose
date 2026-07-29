package com.yourcompany.digitaltok.ui.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.digitaltok.ui.theme.FaqItem
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.databinding.ItemFaqBinding

class FaqAdapter(
    private val items: List<FaqItem>
) : RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {


    private val expandedPositions = mutableSetOf<Int>()

    // FAQ ViewHolder
    inner class FaqViewHolder(
        private val binding: ItemFaqBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FaqItem, position: Int) {
            binding.tvQuestion.text = item.question
            binding.tvAnswer.text = item.answer

            val isExpanded = expandedPositions.contains(position)


            binding.answerContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE


            binding.ivChevron.setImageResource(
                if (isExpanded) R.drawable.vector_3 else R.drawable.vector_2
            )


            binding.dividerTop.visibility = if (isExpanded) View.VISIBLE else View.GONE


            binding.dividerBottom.visibility = if (isExpanded) View.GONE else View.VISIBLE

            // 클릭 시 해당 position만 토글
            binding.root.setOnClickListener {
                if (expandedPositions.contains(position)) {
                    expandedPositions.remove(position)
                } else {
                    expandedPositions.add(position)
                }
                notifyItemChanged(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFaqBinding.inflate(inflater, parent, false)
        return FaqViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}
