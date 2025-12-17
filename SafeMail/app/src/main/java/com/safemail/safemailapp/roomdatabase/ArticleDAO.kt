package com.safemail.safemailapp.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.safemail.safemailapp.dataModels.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article): Long

    @Update
    suspend fun update(article: Article)

    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE isFavorite = 1")
    fun getFavoriteArticles(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE isReadLater = 1")
    fun getReadLaterArticles(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE url = :url")
    suspend fun getArticleByUrl(url: String): Article?

    @Query("SELECT EXISTS(SELECT 1 FROM articles WHERE url = :url)")
    suspend fun isArticleSaved(url: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM articles WHERE url = :url AND isReadLater = 1)")
    suspend fun isReadLater(url: String): Boolean

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("DELETE FROM articles WHERE url = :url")
    suspend fun deleteArticleByUrl(url: String)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()
}