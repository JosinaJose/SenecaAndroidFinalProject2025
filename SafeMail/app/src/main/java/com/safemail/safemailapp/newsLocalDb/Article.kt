package com.safemail.safemailapp.newsLocalDb

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
// In Article.kt
@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val adminEmail: String, // Key field for data isolation
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String,
    val urlToImage: String?,
    val isReadLater: Boolean = false,
    val isFavorite: Boolean = false,
)
@Serializable
data class Source(
    val id: String? = null,
    val name: String? = null
)