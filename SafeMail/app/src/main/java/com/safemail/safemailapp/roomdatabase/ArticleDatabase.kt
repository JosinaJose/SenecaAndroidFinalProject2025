package com.safemail.safemailapp.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.safemail.safemailapp.dataModels.Article

@Database(
    entities = [Article::class],
    version = 2,  // Increment version
    exportSchema = false
)
@TypeConverters(Convertors::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDAO

    companion object {
        @Volatile
        private var INSTANCE: ArticleDatabase? = null

        // Migration from version 1 to 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns with default values
                database.execSQL("ALTER TABLE articles ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE articles ADD COLUMN isReadLater INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): ArticleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDatabase::class.java,
                    "article_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    // .fallbackToDestructiveMigration()  // Use only in development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}