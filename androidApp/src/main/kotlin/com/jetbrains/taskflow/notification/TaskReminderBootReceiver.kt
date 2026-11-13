package com.jetbrains.taskflow.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jetbrains.taskflow.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.time.Clock

class TaskReminderBootReceiver: BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                restoreReminders(context.applicationContext)
            }

            else -> return
        }
    }

    private fun restoreReminders(context: Context) {
        val pendingResult = goAsync()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val taskRepository = get<TaskRepository>()
                val tasks = taskRepository.observeTasks().first()

                val now = Clock.System.now()

                tasks.forEach { task ->
                    val reminderAt = task.reminderAt ?: return@forEach

                    // Do not restore reminders that have already passed.
                    if (reminderAt <= now) return@forEach
                    try {
                        TaskReminderScheduler.sync(context, task)
                    } catch (exception: Exception) {
                        Log.e(
                            "TaskReminderBoot",
                            "Failed to restore reminder for task ${task.id}",
                            exception
                        )
                    }
                }

            } catch (exception: Exception) {
                Log.e(
                    "TaskReminderBoot",
                    "Failed to restore task reminders",
                    exception
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}