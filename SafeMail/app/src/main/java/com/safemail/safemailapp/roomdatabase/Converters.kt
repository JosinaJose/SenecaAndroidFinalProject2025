package com.safemail.safemailapp.roomdatabase

import androidx.room.TypeConverter
import com.safemail.safemailapp.dataModels.Source
import com.google.gson.Gson

class Convertors {

    private val gson = Gson()

    @TypeConverter
    fun fromSource(source: Source?): String? {
        return source?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toSource(sourceString: String?): Source? {
        return sourceString?.let {
            gson.fromJson(it, Source::class.java)
        }
    }
}
