package com.safemail.safemailapp.uiLayer.newsPage

import android.util.Log
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

class NewsViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    /* ---------------- ROOM DATA ---------------- */

    val articles = repository.getAllArticles()
    val readLaterArticles = repository.getReadLaterArticles()

    /* ---------------- API RESPONSE ---------------- */

    private val _newsResponse = MutableStateFlow<NewsResponse?>(null)
    val newsResponse: StateFlow<NewsResponse?> = _newsResponse.asStateFlow()

    /* ---------------- READ LATER (ROOM) ---------------- */

    fun toggleReadLater(article: Article) {
        viewModelScope.launch {
            val existingArticle = repository.getArticleByUrl(article.url)

            if (existingArticle != null) {
                val updatedArticle = existingArticle.copy(
                    isReadLater = !existingArticle.isReadLater
                )
                repository.updateArticle(updatedArticle)
                Log.d("RoomDB", "Updated read later: ${article.title}")
            } else {
                repository.insertArticle(article.copy(isReadLater = true))
                Log.d("RoomDB", "Inserted read later: ${article.title}")
            }
        }
    }

    suspend fun isReadLater(url: String): Boolean {
        return repository.isReadLater(url)
    }

    /* ---------------- FAVORITES (ROOM) ---------------- */

    fun toggleFavorite(article: Article) {
        viewModelScope.launch {
            val existingArticle = repository.getArticleByUrl(article.url)

            if (existingArticle != null) {
                val updatedArticle = existingArticle.copy(
                    isFavorite = !existingArticle.isFavorite
                )
                repository.updateArticle(updatedArticle)
                Log.d("RoomDB", "Updated favorite: ${article.title}")
            } else {
                repository.insertArticle(article.copy(isFavorite = true))
                Log.d("RoomDB", "Inserted favorite: ${article.title}")
            }
        }
    }

    /* ---------------- NEWS API ---------------- */

    fun getTopHeadlines(countryCode: String = "us", page: Int = 1) {
        viewModelScope.launch {
            try {
                val response: Response<NewsResponse> =
                    RetrofitInstance.newsApi.getHeadLines(
                        countryCode = countryCode,
                        pageNumber = page
                    )

                if (response.isSuccessful) {
                    _newsResponse.value = response.body()
                }
            } catch (e: Exception) {
                Log.e("NewsAPI", "Failed to load headlines", e)
            }
        }
    }

    fun searchNews(query: String, page: Int = 1) {
        viewModelScope.launch {
            try {
                val response =
                    RetrofitInstance.newsApi.searchForNews(
                        searchQuery = query,
                        pageNumber = page
                    )

                if (response.isSuccessful) {
                    _newsResponse.value = response.body()
                }
            } catch (e: Exception) {
                Log.e("NewsAPI", "Search failed", e)
            }
        }
    }
}
