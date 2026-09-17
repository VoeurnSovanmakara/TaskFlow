package com.jetbrains.taskflow.data.database

import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

fun createDatabase(
    builder: RoomDatabase.Builder<TaskDatabase>
): TaskDatabase {

    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}