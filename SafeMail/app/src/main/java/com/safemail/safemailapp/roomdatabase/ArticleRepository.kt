package com.safemail.safemailapp.roomdatabase

import com.safemail.safemailapp.dataModels.Article
import kotlinx.coroutines.flow.Flow


class ArticleRepository(private val dao: ArticleDAO) {

    // Pass adminEmail to filter only relevant data
    fun getAllArticles(adminEmail: String): Flow<List<Article>> =
        dao.getAllArticles(adminEmail)

    fun getFavoriteArticles(adminEmail: String): Flow<List<Article>> =
        dao.getFavoriteArticles(adminEmail)

    fun getReadLaterArticles(adminEmail: String): Flow<List<Article>> =
        dao.getReadLaterArticles(adminEmail)

    suspend fun insertArticle(article: Article) = dao.insert(article)

    suspend fun updateArticle(article: Article) = dao.update(article)

    //suspend fun deleteArticle(article: Article) = dao.deleteArticle(article)

    // Check saved status specifically for this admin
    suspend fun isArticleSaved(url: String, adminEmail: String): Boolean =
        dao.isArticleSaved(url, adminEmail)

    suspend fun getArticleByUrl(url: String, adminEmail: String): Article? =
        dao.getArticleByUrl(url, adminEmail)

    suspend fun deleteArticle(url: String, adminEmail: String) {
        dao.deleteByUrl(url, adminEmail)
    }



}