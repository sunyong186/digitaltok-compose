package com.yourcompany.digitaltok.ui.decorate

import android.net.Uri

data class DecorateItem(
    val id: String, // 서버 이미지 ID 또는 로컬 임시 ID
    val title: String = "",
    val previewUrl: String? = null, // Glide로 로드할 URL (서버)
    val imageUri: Uri? = null, // Glide로 로드할 Uri (로컬)
    var isSlot: Boolean = false,
    var isFavorite: Boolean = false,
    var isSelected: Boolean = false, // 선택 상태
    val onAddClick: (() -> Unit)? = null // 추가 버튼 클릭 리스너
)
