package com.safemail.safemailapp.dataModels

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = false)
    val url: String = "",  // Provide default empty string
    val author: String? = null,
    val content: String? = null,
    val description: String? = null,
    val publishedAt: String? = null,
    val source: Source? = null,
    val title: String? = null,
    val urlToImage: String? = null,
    val isFavorite: Boolean = false,
    val isReadLater: Boolean = false
)
@Serializable
data class Source(
    val id: String? = null,
    val name: String? = null
)