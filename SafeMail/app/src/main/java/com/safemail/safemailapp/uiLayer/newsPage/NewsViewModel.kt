package com.safemail.safemailapp.uiLayer.newsPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safemail.safemailapp.dataModels.Article
import com.safemail.safemailapp.dataModels.NewsResponse
import com.safemail.safemailapp.newsApi.RetrofitInstance
import com.safemail.safemailapp.roomdatabase.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import android.util.Log

class NewsViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    // Get all saved articles (both favorites and read later)
    val articles = repository.getAllArticles()

    // Get only read later articles
    val readLaterArticles = repository.getReadLaterArticles()

    private val _newsResponse = MutableStateFlow<NewsResponse?>(null)
    val newsResponse: StateFlow<NewsResponse?> = _newsResponse.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /* ---------------- READ LATER (ROOM DATABASE) ---------------- */

    suspend fun toggleReadLater(article: Article) {
        viewModelScope.launch {
            // Check if article already exists in database
            val existingArticle = repository.getArticleByUrl(article.url)

            if (existingArticle != null) {
                // Article exists - toggle the readLater flag
                val updatedArticle = existingArticle.copy(
                    isReadLater = !existingArticle.isReadLater
                )
                repository.updateArticle(updatedArticle)
                Log.d("RoomDB", "Updated article read later status: ${article.title}")
            } else {
                // Article doesn't exist - insert with readLater = true
                val newArticle = article.copy(isReadLater = true)
                repository.insertArticle(newArticle)
                Log.d("RoomDB", "Inserted article as read later: ${article.title}")
            }
        }
    }

    suspend fun isReadLater(url: String): Boolean {
        return repository.isReadLater(url)
    }

    /* ---------------- FAVORITES (ROOM DATABASE) ---------------- */

    fun toggleFavorite(article: Article) {
        viewModelScope.launch {
            val existingArticle = repository.getArticleByUrl(article.url)

            if (existingArticle != null) {
                // Article exists - toggle the favorite flag
                val updatedArticle = existingArticle.copy(
                    isFavorite = !existingArticle.isFavorite
                )
                repository.updateArticle(updatedArticle)
                Log.d("RoomDB", "Updated article favorite status: ${article.title}")
            } else {
                // Article doesn't exist - insert with favorite = true
                val newArticle = article.copy(isFavorite = true)
                repository.insertArticle(newArticle)
                Log.d("RoomDB", "Inserted article as favorite: ${article.title}")
            }
        }
    }

    /* ---------------- NEWS API ---------------- */

    fun getTopHeadlines(countryCode: String = "us", page: Int = 1) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null

                val response: Response<NewsResponse> =
                    RetrofitInstance.newsApi.getHeadLines(
                        countryCode = countryCode,
                        pageNumber = page
                    )

                if (response.isSuccessful) {
                    _newsResponse.value = response.body()
                } else {
                    _errorMessage.value = "Error: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown error"
            }
        }
    }

    fun searchNews(query: String, page: Int = 1) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null

                val response = RetrofitInstance.newsApi.searchForNews(
                    searchQuery = query,
                    pageNumber = page
                )

                if (response.isSuccessful) {
                    _newsResponse.value = response.body()
                } else {
                    _errorMessage.value = "Error: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown error"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}