package com.safemail.safemailapp.roomdatabase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.safemail.safemailapp.uiLayer.newsPage.NewsViewModel

class NewsViewModelFactory(
    private val repository: ArticleRepository,
    private val adminEmail: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(repository, adminEmail) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
