package com.jetbrains.taskflow.data.database

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

fun getDatabaseBuilder(
    context: Context
): RoomDatabase.Builder<TaskDatabase> {

    val appContext = context.applicationContext

    val dbFile = appContext.getDatabasePath("taskflow.db")

    return Room.databaseBuilder<TaskDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}