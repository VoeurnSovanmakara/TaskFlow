package com.jetbrains.taskflow.presentation.task.components

import android.R.attr.icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jetbrains.taskflow.R
import com.jetbrains.taskflow.notification.rememberTaskReminderPermissionRequester
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toInstant
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskReminderSection(
    reminderAt: Instant?,
    onReminderChanged: (Instant?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val scheme = MaterialTheme.colorScheme
    val timeZone = TimeZone.currentSystemDefault()

    val reminderDateTime = remember(reminderAt) {
        (reminderAt ?: (Clock.System.now() + 1.hours))
            .toLocalDateTime(timeZone)
    }

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("EEE, MMM d")
    }

    val timeFormatter = remember {
        DateTimeFormatter.ofPattern("h:mm a")
    }

    // Date and time are shown separately, each in its own tappable field
    val displayZoned = remember(reminderAt) {
        reminderAt?.let {
            java.time.Instant
                .ofEpochMilli(it.toEpochMilliseconds())
                .atZone(ZoneId.systemDefault())
        }
    }

    val displayDate = displayZoned?.format(dateFormatter)
    val displayTime = displayZoned?.format(timeFormatter)

    val requestReminderPermission = rememberTaskReminderPermissionRequester(onReminderChanged)

    // Quick presets: "tomorrow" and "next week" both land at 9:00 AM
    fun morning(daysAhead: Int): Instant {
        val date = Clock.System.todayIn(timeZone).plus(DatePeriod(days = daysAhead))
        return LocalDateTime(date, LocalTime(9, 0)).toInstant(timeZone)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = scheme.surfaceContainerLow
        ),
        border = BorderStroke(1.dp, scheme.outlineVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.schedule),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "REMINDER",
                            style = MaterialTheme.typography.labelSmall,
                            color = scheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = if (reminderAt == null) {
                            "Get notified about this task"
                        } else {
                            "Reminder enabled"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Switch(
                    checked = reminderAt != null,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            requestReminderPermission(
                                Clock.System.now() + 1.hours
                            )
                        } else {
                            onReminderChanged(null)
                        }
                    }
                )
            }

            // Replaces the "Change date" / "Change time" buttons: tap the value itself
            if (reminderAt != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReminderField(
                        label = "DATE",
                        value = displayDate.orEmpty(),
                        modifier = Modifier.weight(1f),
                        onClick = { showDatePicker = true }
                    )

                    ReminderField(
                        label = "TIME",
                        value = displayTime.orEmpty(),
                        modifier = Modifier.weight(1f),
                        onClick = { showTimePicker = true }
                    )
                }
            }

            // Quick picks: always visible, and tapping one also turns the reminder on
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickReminderChip("In 1 hour") {
                    requestReminderPermission(Clock.System.now() + 1.hours)
                }

                QuickReminderChip("Tomorrow 9 AM") {
                    requestReminderPermission(morning(daysAhead = 1))
                }

                QuickReminderChip("Next week") {
                    requestReminderPermission(morning(daysAhead = 7))
                }
            }
        }
    }

    if (showDatePicker) {
        val initialDateMillis = reminderDateTime.date
            .atStartOfDayIn(TimeZone.UTC)
            .toEpochMilliseconds()

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDateMillis,
            initialDisplayedMonthMillis = initialDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            shape = RoundedCornerShape(28.dp),
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant
                                .fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.UTC)
                                .date

                            val updatedDateTime = LocalDateTime(
                                selectedDate,
                                LocalTime(
                                    reminderDateTime.hour,
                                    reminderDateTime.minute
                                )
                            )

                            onReminderChanged(
                                updatedDateTime.toInstant(timeZone)
                            )
                        }

                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = reminderDateTime.hour,
            initialMinute = reminderDateTime.minute,
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            shape = RoundedCornerShape(28.dp),
            title = { Text("Select reminder time") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updatedDateTime = LocalDateTime(
                            reminderDateTime.date,
                            LocalTime(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                        )

                        onReminderChanged(
                            updatedDateTime.toInstant(timeZone)
                        )

                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// A tappable value box: small label on top, the current value in the accent color
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = scheme.surface,
        border = BorderStroke(1.dp, scheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = scheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = scheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickReminderChip(label: String, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        shape = RoundedCornerShape(12.dp)
    )
}