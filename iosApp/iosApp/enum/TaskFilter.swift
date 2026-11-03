//
// Created by sovanmakara on 10/26/26.
//

import Foundation
import SharedLogic

enum TaskFilter: String, CaseIterable, Identifiable {
    case all = "All"
    case todo = "To Do"
    case inProgress = "In Progress"
    case completed = "Completed"
    case overdue = "Overdue"
    case dueToday = "Due Today"

    var id: String { rawValue }

    func matches(_ task: SharedLogic.Task) -> Bool {
        switch self {
        case .all:
            return true

        case .todo:
            return task.status.name == "TODO"

        case .inProgress:
            return task.status.name == "IN_PROGRESS"

        case .completed:
            return task.status.name == "COMPLETED"

        case .overdue:
            guard task.status.name != "COMPLETED",
                  let dueDate = task.dueDate else {
                return false
            }

            let components = DateComponents(
                year: Int(dueDate.year),
                month: Int(dueDate.monthNumber),
                day: Int(dueDate.dayOfMonth)
            )

            guard let date = Calendar.current.date(from: components) else {
                return false
            }

            return Calendar.current.startOfDay(for: date)
                < Calendar.current.startOfDay(for: Date())

        case .dueToday:
            guard task.status.name != "COMPLETED",
                  let dueDate = task.dueDate else {
                return false
            }

            let components = DateComponents(
                year: Int(dueDate.year),
                month: Int(dueDate.monthNumber),
                day: Int(dueDate.dayOfMonth)
            )

            guard let date = Calendar.current.date(from: components) else {
                return false
            }

            return Calendar.current.isDateInToday(date)
        }
    }
}