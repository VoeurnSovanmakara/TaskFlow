package com.jetbrains.taskflow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform