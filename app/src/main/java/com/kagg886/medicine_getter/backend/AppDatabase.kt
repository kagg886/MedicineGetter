package com.kagg886.medicine_getter.backend

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.kagg886.medicine_getter.backend.dao.IdentificationRecordDao
import com.kagg886.medicine_getter.backend.entity.IdentificationRecord
import com.kagg886.medicine_getter.network.AIResult
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Database(entities = [IdentificationRecord::class], version = 1)
@TypeConverters(Convert::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun identificationRecordDao(): IdentificationRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Convert {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(value ?: 0), ZoneId.systemDefault())
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long {
        return date?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: 0
    }

    @TypeConverter
    fun jsonToAIResult(json: String?): AIResult {
        return Json.decodeFromString<AIResult>(json!!)
    }

    @TypeConverter
    fun aiResultToJson(ai: AIResult): String {
        return Json.encodeToString(AIResult.serializer(),ai)
    }
}