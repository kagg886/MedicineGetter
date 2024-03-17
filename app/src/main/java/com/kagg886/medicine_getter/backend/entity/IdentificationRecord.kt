package com.kagg886.medicine_getter.backend.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kagg886.medicine_getter.network.AIResult
import java.time.LocalDateTime

@Entity(tableName = "record")
data class IdentificationRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val createTime: LocalDateTime = LocalDateTime.now(),
    val result: AIResult
)

//@Entity(tableName = "task_table")
//data class Task(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val title: String,
//    val description: String,
//)