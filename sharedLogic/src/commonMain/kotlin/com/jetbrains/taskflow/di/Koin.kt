package com.jetbrains.taskflow.di

import androidx.room3.RoomDatabase
import com.jetbrains.taskflow.data.database.TaskDatabase
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin(
    databaseBuilder: RoomDatabase.Builder<TaskDatabase>,
    additionalModules: List<Module> = emptyList()
) {
    startKoin {
        modules(
            appModule,
            module {
                single<RoomDatabase.Builder<TaskDatabase>> {
                    databaseBuilder
                }
            },
            *additionalModules.toTypedArray()
        )
    }
}