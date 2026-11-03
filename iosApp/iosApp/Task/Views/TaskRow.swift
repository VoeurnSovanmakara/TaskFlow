//
// Created by sovanmakara on 10/30/26.
//

import SwiftUI
import SharedLogic


struct TaskRow: View {
    let task: SharedLogic.Task
    let onToggleCompletion: () -> Void

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

    private var dueDateInfo: TaskDueDateInfo? {
        guard let dueDate = task.dueDate else {
            return nil
        }

        return TaskDueDateInfo.make(
            dueDate: dueDate,
            isCompleted: isDone
        )
    }

    var body: some View {
        HStack(alignment: .top, spacing: 14) {
            Button(action: onToggleCompletion) {
                Image(systemName: statusSymbol)
                    .font(.system(size: 24))
                    .foregroundStyle(statusColor)
                    .padding(.top, 1)
                    .frame(width: 32, height: 32)
                    .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
            .accessibilityLabel(
                isDone ? "Reopen task" : "Mark task as completed"
            )
            .accessibilityHint("Changes the task status")

            VStack(alignment: .leading, spacing: 4) {
                Text(task.title)
                    .font(.system(.body, design: .rounded).weight(.semibold))
                    .strikethrough(isDone)
                    .foregroundStyle(isDone ? Color.secondary : Color.primary)

                if let description = task.description_ {
                    if !description.isEmpty {
                        Text(description)
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                            .lineLimit(1)
                    }
                }

                HStack(spacing: 10) {
                    HStack(spacing: 4) {
                        Image(systemName: prioritySymbol)
                            .font(.system(size: 10, weight: .bold))
                        Text(priorityTitle)
                            .font(.caption.weight(.semibold))
                    }
                    .foregroundStyle(priorityColor)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 3)
                    .background(priorityColor.opacity(0.14), in: Capsule())

                    if let dueDateInfo {
                        Label(
                            dueDateInfo.title,
                            systemImage: "calendar"
                        )
                            .font(.caption.weight(.medium))
                            .foregroundStyle(dueDateInfo.color)
                    }
                }
                .padding(.top, 4)
            }

            Spacer(minLength: 0)
        }
        .padding(.vertical, 14)
        .accessibilityElement(children: .combine)
    }
}

