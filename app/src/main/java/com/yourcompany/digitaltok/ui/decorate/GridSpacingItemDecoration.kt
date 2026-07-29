package com.yourcompany.digitaltok.ui.decorate

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val horizontalSpace: Int,
    private val verticalSpace: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        val column = position % spanCount

        // 좌우 간격 (12dp)
        outRect.left = horizontalSpace * column / spanCount
        outRect.right = horizontalSpace * (spanCount - column - 1) / spanCount

        // 위아래 간격 (13dp)
        if (position >= spanCount) {
            outRect.top = verticalSpace
        }
    }
}