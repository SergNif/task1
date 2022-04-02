package com.sergnf88.task1



import kotlinx.serialization.Serializable

@Serializable
data class Kiparo(
    val location: String?,
    val name: String?,
    val news: List<New>
)