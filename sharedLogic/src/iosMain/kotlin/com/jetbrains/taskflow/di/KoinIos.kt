package com.jetbrains.taskflow.di

import com.jetbrains.taskflow.data.database.getDatabaseBuilder

fun initKoinIos() {
    initKoin(
        databaseBuilder = getDatabaseBuilder()
    )
}