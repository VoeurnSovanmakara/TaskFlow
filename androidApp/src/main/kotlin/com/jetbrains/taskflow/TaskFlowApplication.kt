package com.jetbrains.taskflow

import android.app.Application
import com.jetbrains.taskflow.data.database.getDatabaseBuilder
import com.jetbrains.taskflow.di.initKoin

class TaskFlowApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin(
            databaseBuilder = getDatabaseBuilder(this)
        )
    }
}