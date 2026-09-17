package com.jetbrains.taskflow.data.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun getDatabaseBuilder(): RoomDatabase.Builder<TaskDatabase> {

    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )

    val dbFilePath =
        requireNotNull(documentDirectory?.path) + "/taskflow.db"

    return Room.databaseBuilder<TaskDatabase>(
        name = dbFilePath
    )
}