package com.sergnf88.task1

import kotlinx.serialization.Serializable

@Serializable
data class New(
    val date: String?,
    val description: String = "",
    val id: Int,
    val keywords: List<String?>,
    val title: String = "",
    val visible: Boolean?
)