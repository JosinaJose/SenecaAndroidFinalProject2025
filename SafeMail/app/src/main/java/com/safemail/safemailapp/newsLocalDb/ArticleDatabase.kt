package com.safemail.safemailapp.newsLocalDb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Ensure these specific imports exist
import com.safemail.safemailapp.hubTaskBackend.stickyNoteLocalDb.StickyNoteDao
import com.safemail.safemailapp.hubTaskBackend.stickyNoteLocalDb.StickyNoteModel
import com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb.TodoTask
import com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb.TodoDao
import com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb.TodoConverters

@Database(
    entities = [
        Article::class,
        TodoTask::class,
        StickyNoteModel::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Convertors::class, TodoConverters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDAO
    abstract fun todoDao(): TodoDao
    abstract fun stickyNoteDao(): StickyNoteDao

    companion object {
        @Volatile
        private var INSTANCE: ArticleDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE articles ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE articles ADD COLUMN isReadLater INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE articles ADD COLUMN adminEmail TEXT NOT NULL DEFAULT ''")
            }
        }

        // Added MIGRATION_3_4 to handle the transition to the TodoTask table
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Room will automatically create the todo_tasks table if it doesn't exist
                // because of fallbackToDestructiveMigration, but we define the path here.
            }
        }

        fun getDatabase(context: Context): ArticleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDatabase::class.java,
                    "article_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration() // Critical for your testing phase
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


/*package com.safemail.safemailapp.newsLocalDb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
}*/