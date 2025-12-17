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
    version = 3,  // 1. Updated from 2 to 3
    exportSchema = false
)
@TypeConverters(Convertors::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDAO

    companion object {
        @Volatile
        private var INSTANCE: ArticleDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE articles ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE articles ADD COLUMN isReadLater INTEGER NOT NULL DEFAULT 0")
            }
        }

        // 2. Add Migration from version 2 to 3
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the new adminEmail column.
                // We use a default empty string so existing rows don't cause errors.
                database.execSQL("ALTER TABLE articles ADD COLUMN adminEmail TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): ArticleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDatabase::class.java,
                    "article_database"
                )
                    // 3. Add the new migration to the builder
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}