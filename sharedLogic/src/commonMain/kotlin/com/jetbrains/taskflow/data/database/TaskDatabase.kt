package com.jetbrains.taskflow.data.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.jetbrains.taskflow.data.database.dao.ProjectDao
import com.jetbrains.taskflow.data.database.dao.TaskDao
import com.jetbrains.taskflow.data.database.entity.ProjectEntity
import com.jetbrains.taskflow.data.database.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        ProjectEntity::class,
    ],
    version = 2
)
@ConstructedBy(TaskDatabaseConstructor::class)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun projectDao(): ProjectDao
}

@Suppress("KotlinNoActualForExpect")
expect object TaskDatabaseConstructor : RoomDatabaseConstructor<TaskDatabase> {
    override fun initialize(): TaskDatabase
}