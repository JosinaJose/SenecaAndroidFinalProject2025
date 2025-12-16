package com.safemail.safemailapp.dataModels

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)