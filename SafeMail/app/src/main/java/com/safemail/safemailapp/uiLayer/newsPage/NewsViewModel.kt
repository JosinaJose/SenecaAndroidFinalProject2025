package com.safemail.safemailapp.uiLayer.newsPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safemail.safemailapp.newsApi.RetrofitInstance
import com.safemail.safemailapp.dataModels.NewsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel : ViewModel() {

    private val _newsResponse = MutableStateFlow<NewsResponse?>(null)
    val newsResponse: StateFlow<NewsResponse?> = _newsResponse

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun getTopHeadlines(countryCode: String = "us", page: Int = 1) {
        viewModelScope.launch {
            try {
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
}
