package com.example.todoplanner.sql

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo("dateOfCreation")
    val dateOfCreation: String,
    @ColumnInfo("dateOfUpdater")
    val dateOfUpdater : String,
    @ColumnInfo("description")
    val description : String,
    @ColumnInfo("expirationDate")
    val expirationDate : String,
    @ColumnInfo("header")
    val header: String,
    @ColumnInfo("priority")
    val priority: String,
)