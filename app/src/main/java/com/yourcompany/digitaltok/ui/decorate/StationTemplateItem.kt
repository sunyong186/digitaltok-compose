package com.yourcompany.digitaltok.ui.decorate

data class TemplateItem(
    val id: String,
    val title: String,
    val desc: String = "",
    val thumbRes: Int = 0,
    val thumbUrl: String? = null
)
