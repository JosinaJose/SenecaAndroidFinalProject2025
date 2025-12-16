package com.safemail.safemailapp.uiLayer.newsPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safemail.safemailapp.dataModels.Article
import com.safemail.safemailapp.newsApi.RetrofitInstance
import com.safemail.safemailapp.dataModels.NewsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel : ViewModel() {

    private val _newsResponse = MutableStateFlow<NewsResponse?>(null)
    val newsResponse: StateFlow<NewsResponse?> = _newsResponse.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Track favorite articles
    private val _favoriteArticles = MutableStateFlow<List<Article>>(emptyList())
    val favoriteArticles: StateFlow<List<Article>> = _favoriteArticles.asStateFlow()

    // Track read later articles (using Set of URLs for efficient lookup)
    private val _readLaterArticles = MutableStateFlow<Set<String>>(emptySet())
    val readLaterArticles: StateFlow<Set<String>> = _readLaterArticles.asStateFlow()

    fun toggleFavorite(article: Article) {
        val current = _favoriteArticles.value.toMutableList()
        if (current.contains(article)) {
            current.remove(article)
        } else {
            current.add(article)
        }
        _favoriteArticles.value = current
    }

    fun toggleReadLater(article: Article) {
        article.url?.let { url ->
            _readLaterArticles.value = if (_readLaterArticles.value.contains(url)) {
                _readLaterArticles.value - url
            } else {
                _readLaterArticles.value + url
            }
        }
    }

    fun getReadLaterArticles(): List<Article> {
        return _newsResponse.value?.articles?.filter {
            _readLaterArticles.value.contains(it.url)
        } ?: emptyList()
    }

    fun isReadLater(article: Article): Boolean {
        return article.url?.let { _readLaterArticles.value.contains(it) } ?: false
    }

    fun getTopHeadlines(countryCode: String = "us", page: Int = 1) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null // Clear previous errors

                val response: Response<NewsResponse> =
                    RetrofitInstance.newsApi.getHeadLines(
                        countryCode = countryCode,
                        pageNumber = page
                        // no need to pass apiKey, default from Constants is used
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
                _errorMessage.value = null // Clear previous errors

                val response: Response<NewsResponse> =
                    RetrofitInstance.newsApi.searchForNews(
                        searchQuery = query,
                        pageNumber = page
                        // apiKey automatically comes from Constants
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