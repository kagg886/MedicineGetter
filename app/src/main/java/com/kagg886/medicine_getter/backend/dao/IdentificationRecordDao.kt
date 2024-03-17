package com.kagg886.medicine_getter.backend.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kagg886.medicine_getter.backend.entity.IdentificationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface IdentificationRecordDao {
    @Query("select * from record")
    fun getAllRecord(): Flow<List<IdentificationRecord>>

    @Insert
    suspend fun insertNewRecord(i: IdentificationRecord)
}

//@Dao
//interface TaskDao {
//    @Query("SELECT * FROM task_table")
//    fun getAllTasks(): Flow<List<Task>>
//
//    @Insert
//    suspend fun insertTask(task: Task)
//}