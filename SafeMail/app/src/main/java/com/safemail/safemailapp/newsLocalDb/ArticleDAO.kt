package com.safemail.safemailapp.newsLocalDb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDAO {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(article: Article): Long

    @Update
    suspend fun update(article: Article)

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("SELECT * FROM articles WHERE adminEmail = :adminEmail")
    fun getAllArticles(adminEmail: String): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE isFavorite = 1 AND adminEmail = :adminEmail")
    fun getFavoriteArticles(adminEmail: String): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE isReadLater = 1 AND adminEmail = :adminEmail")
    fun getReadLaterArticles(adminEmail: String): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE url = :url AND adminEmail = :adminEmail")
    suspend fun getArticleByUrl(url: String, adminEmail: String): Article?

    @Query("SELECT EXISTS(SELECT 1 FROM articles WHERE url = :url AND adminEmail = :adminEmail)")
    suspend fun isArticleSaved(url: String, adminEmail: String): Boolean

    @Query("DELETE FROM articles WHERE url = :url AND adminEmail = :adminEmail")
    suspend fun deleteByUrl(url: String, adminEmail: String)
}