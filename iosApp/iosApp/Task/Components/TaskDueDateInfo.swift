//
// Created by sovanmakara on 10/30/26.
//

import SwiftUI
import SharedLogic

struct TaskDueDateInfo {
    let title: String
    let color: Color

    static func make(
        dueDate: Kotlinx_datetimeLocalDate,
        isCompleted: Bool
    ) -> TaskDueDateInfo {
        let components = DateComponents(
            year: Int(dueDate.year),
            month: Int(dueDate.monthNumber),
            day: Int(dueDate.dayOfMonth)
        )

        let calendar = Calendar.current
        let today = calendar.startOfDay(for: Date())

        guard let date = calendar.date(from: components) else {
            return TaskDueDateInfo(
                title: "Due \(String(describing: dueDate))",
                color: .secondary
            )
        }

        let dueDay = calendar.startOfDay(for: date)
        let days = calendar.dateComponents(
            [.day],
            from: today,
            to: dueDay
        ).day ?? 0

        if isCompleted {
            return TaskDueDateInfo(
                title: "Due \(date.formatted(date: .abbreviated, time: .omitted))",
                color: .secondary
            )
        }

        if days < 0 {
            let count = abs(days)
            return TaskDueDateInfo(
                title: count == 1
                    ? "1 day overdue"
                    : "\(count) days overdue",
                color: .red
            )
        }

        if days == 0 {
            return TaskDueDateInfo(
                title: "Due today",
                color: .orange
            )
        }

        if days == 1 {
            return TaskDueDateInfo(
                title: "Due tomorrow",
                color: .orange
            )
        }

        return TaskDueDateInfo(
            title: "Due \(date.formatted(date: .abbreviated, time: .omitted))",
            color: .secondary
        )
    }
}
