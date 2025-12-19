package com.safemail.safemailapp.newsApi

import com.safemail.safemailapp.components.Constants
import com.safemail.safemailapp.newsLocalDb.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {
    @GET("v2/top-headlines")
    suspend fun getHeadLines(
        @Query("country") countryCode: String = "us",
        @Query("page") pageNumber: Int = 1,
        @Query("category") category: String? = null,
        @Query("apiKey") apiKey: String = Constants.Companion.NEWS_API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = Constants.Companion.NEWS_API_KEY
    ): Response<NewsResponse>
}