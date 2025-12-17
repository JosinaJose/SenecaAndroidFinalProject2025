package com.safemail.safemailapp.roomdatabase

import com.safemail.safemailapp.dataModels.Article
import kotlinx.coroutines.flow.Flow

class ArticleRepository(private val dao: ArticleDAO) {

    fun getAllArticles(): Flow<List<Article>> = dao.getAllArticles()

    fun getFavoriteArticles(): Flow<List<Article>> = dao.getFavoriteArticles()

    fun getReadLaterArticles(): Flow<List<Article>> = dao.getReadLaterArticles()

    suspend fun insertArticle(article: Article) = dao.insert(article)

    suspend fun updateArticle(article: Article) = dao.update(article)

    suspend fun deleteArticle(article: Article) = dao.deleteArticle(article)

    suspend fun isArticleSaved(url: String): Boolean = dao.isArticleSaved(url)

    suspend fun isReadLater(url: String): Boolean = dao.isReadLater(url)

    suspend fun getArticleByUrl(url: String): Article? = dao.getArticleByUrl(url)
}