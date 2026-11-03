//
// Created by sovanmakara on 11/2/26.
//

import SwiftUI
import SharedLogic


struct DashboardRecentTaskRow: View {
    let task: SharedLogic.Task

    private var isDone: Bool { task.status.name == "COMPLETED" }

    private var priorityTitle: String {
        task.priority.name.capitalized
    }

    private var statusSymbol: String {
        switch task.status.name {
        case "COMPLETED": "checkmark.circle.fill"
        case "IN_PROGRESS": "circle.lefthalf.filled"
        default: "circle"
        }
    }

    private var statusColor: Color {
        switch task.status.name {
        case "COMPLETED": .green
        case "IN_PROGRESS": Palette.accent
        default: Color(.systemGray3)
        }
    }

    private var prioritySymbol: String {
        switch task.priority.name {
        case "HIGH": "arrow.up"
        case "LOW": "arrow.down"
        default: "minus"
        }
    }

    private var priorityColor: Color {
        switch task.priority.name {
        case "HIGH": .red
        case "LOW": .blue
        default: .orange
        }
    }

    private var dueDate: TaskDueDateInfo? {
        guard let dueDate = task.dueDate else {
            return nil
        }

        return TaskDueDateInfo.make(
            dueDate: dueDate,
            isCompleted: isDone
        )
    }

    var body: some View {
        HStack(spacing: 14) {
            Image(systemName: statusSymbol)
                .font(.system(size: 24))
                .foregroundStyle(statusColor)

            VStack(alignment: .leading, spacing: 6) {
                Text(task.title)
                    .font(.system(.subheadline, design: .rounded).weight(.semibold))
                    .strikethrough(isDone)
                    .foregroundStyle(isDone ? Color.secondary : Color.primary)
                    .lineLimit(1)

                HStack(spacing: 8) {
                    HStack(spacing: 4) {
                        Image(systemName: prioritySymbol)
                            .font(.system(size: 9, weight: .bold))
                        Text(priorityTitle)
                            .font(.caption2.weight(.semibold))
                    }
                    .foregroundStyle(priorityColor)
                    .padding(.horizontal, 7)
                    .padding(.vertical, 2)
                    .background(priorityColor.opacity(0.14), in: Capsule())

                    if let dueDate {
                        Label(
                            dueDate.title,
                            systemImage: "calendar"
                        )
                            .font(.caption.weight(.medium))
                            .foregroundStyle(dueDate.color)
                    }
                }
            }

            Spacer(minLength: 0)
        }
        .padding(.vertical, 12)
        .accessibilityElement(children: .combine)
    }
}