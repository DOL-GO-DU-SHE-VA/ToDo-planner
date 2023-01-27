package com.example.todoplanner.sql

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addUser(task: Task)

    @Query("SELECT * FROM task_table ORDER BY id ASC")
    fun readAllData() : LiveData<List<Task>>

    @Delete
    fun delete(task: Task)

    @Query("UPDATE task_table SET priority = :value WHERE id = :id")
    fun update( value: String, id: Int)
}