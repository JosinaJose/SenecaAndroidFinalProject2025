package com.safemail.safemailapp.uiLayer.newsPage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safemail.safemailapp.dataModels.Article
import com.safemail.safemailapp.dataModels.NewsResponse
import com.safemail.safemailapp.newsApi.RetrofitInstance
import com.safemail.safemailapp.roomdatabase.ArticleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    private val repository: ArticleRepository,
    private val adminEmail: String
) : ViewModel() {

    /* ---------------- ROOM DATA ---------------- */

    val articles: StateFlow<List<Article>> = repository.getAllArticles(adminEmail)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Public Read Later articles
    val readLaterArticles: StateFlow<List<Article>> = repository.getReadLaterArticles(adminEmail)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Helper map for quick URL lookup
    val readLaterMap: StateFlow<Map<String, Boolean>> = readLaterArticles
        .map { list -> list.associate { it.url to it.isReadLater } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())


    /* ---------------- API RESPONSE ---------------- */

    private val _newsResponse = MutableStateFlow<NewsResponse?>(null)
    val newsResponse: StateFlow<NewsResponse?> = _newsResponse.asStateFlow()


    /* ---------------- READ LATER ---------------- */

    fun toggleReadLater(article: Article) {
        viewModelScope.launch {
            val existingArticle = repository.getArticleByUrl(article.url, adminEmail)
            if (existingArticle != null) {
                repository.updateArticle(existingArticle.copy(isReadLater = !existingArticle.isReadLater))
            } else {
                repository.insertArticle(article.copy(adminEmail = adminEmail, isReadLater = true))
            }
        }
    }

    fun isReadLater(url: String): Boolean = readLaterMap.value[url] ?: false


    /* ---------------- FAVORITES ---------------- */

    fun toggleFavorite(article: Article) {
        viewModelScope.launch {
            val existingArticle = repository.getArticleByUrl(article.url, adminEmail)
            if (existingArticle != null) {
                repository.updateArticle(existingArticle.copy(isFavorite = !existingArticle.isFavorite))
            } else {
                repository.insertArticle(article.copy(adminEmail = adminEmail, isFavorite = true))
            }
        }
    }


    /* ---------------- NEWS API ---------------- */

    fun getTopHeadlines(countryCode: String = "us", page: Int = 1) {
        viewModelScope.launch {
            try {
                val response: Response<NewsResponse> =
                    RetrofitInstance.newsApi.getHeadLines(countryCode, page)
                if (response.isSuccessful) _newsResponse.value = response.body()
            } catch (e: Exception) {
                Log.e("NewsAPI", "Failed to load headlines", e)
            }
        }
    }

    fun searchNews(query: String, page: Int = 1) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.newsApi.searchForNews(query, page)
                if (response.isSuccessful) _newsResponse.value = response.body()
            } catch (e: Exception) {
                Log.e("NewsAPI", "Search failed", e)
            }
        }
    }
}
