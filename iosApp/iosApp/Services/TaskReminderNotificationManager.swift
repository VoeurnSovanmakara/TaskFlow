//
// Created by sovanmakara on 11/3/26.
//

import Foundation
import UserNotifications

@MainActor
final class TaskReminderNotificationManager {
    static let shared = TaskReminderNotificationManager()

    let center = UNUserNotificationCenter.current()

    private init() {}

    func requestPermission() async -> Bool {
        do {
            return try await center.requestAuthorization(
                options: [.alert, .sound, .badge]
            )
        } catch {
            print("Notification permission error: \(error)")
            return false
        }
    }

    func scheduleReminder(
        taskID: String,
        title: String,
        reminderDate: Date
    ) async throws {
        // Request permission before scheduling.
        let isAuthorized = await requestPermission()

        guard isAuthorized else {
            throw ReminderNotificationError.permissionDenied
        }

        // Remove any previous reminder for this task.
        center.removePendingNotificationRequests(withIdentifiers: [taskID])

        let content = UNMutableNotificationContent()
        content.title = "Task Reminder"
        content.body = title
        content.sound = .default

        let componets = Calendar.current.dateComponents(
            [.year, .month, .day, .hour, .minute, .second],
            from: reminderDate
        )

        let trigger = UNCalendarNotificationTrigger(
            dateMatching: componets, repeats: false
        )

        let request = UNNotificationRequest(
            identifier: taskID,
            content: content,
            trigger: trigger
        )

        try await center.add(request)
    }

    func cancelReminder(taskID: String) {
        center.removePendingNotificationRequests(withIdentifiers: [taskID])
        center.removeDeliveredNotifications(withIdentifiers: [taskID])
    }
}

enum ReminderNotificationError: LocalizedError {
    case permissionDenied

    var errorDescription: String? {
        switch self {
        case .permissionDenied:
            return "Notification permission is required to schedule reminders."
        }
    }
}