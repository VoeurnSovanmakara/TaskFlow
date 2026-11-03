//
// Created by sovanmakara on 10/22/26.
//


import SwiftUI
import SharedLogic

struct TaskDetailView: View {
    let task: SharedLogic.Task
    let onEdit: () -> Void
    let onDelete: () -> Void

    @Environment(\.dismiss) private var dismiss
    @State private var showDeleteConfirmation = false

    @State private var isDeleting = false
    @State private var deleteError: String?
    private let deleter = KoinIosKt.createTaskDeleter()

    private func deleteTask() {
        guard !isDeleting else { return }

        isDeleting = true
        deleteError = nil

        deleter.delete(
            id: task.id,
            onSuccess: {
                isDeleting = false
                onDelete()
                dismiss()
            },
            onError: { message in
                isDeleting = false
                deleteError = message
            }
        )
    }
    // MARK: - Display helpers (same status/priority look as the list and create screens)

    private var priorityColor: Color {
        switch task.priority.name {
        case "HIGH":
            return .red
        case "MEDIUM":
            return .orange
        default:
            return .blue
        }
    }

    private var prioritySymbol: String {
        switch task.priority.name {
        case "HIGH": return "arrow.up"
        case "LOW": return "arrow.down"
        default: return "minus"
        }
    }

    // "IN_PROGRESS" -> "In Progress", "HIGH" -> "High" (display text only)
    private var statusTitle: String {
        task.status.name
            .replacingOccurrences(of: "_", with: " ")
            .capitalized
    }

    private var priorityTitle: String {
        task.priority.name.capitalized
    }

    private let steps: [(name: String, title: String, symbol: String)] = [
        ("TODO", "To Do", "largecircle.fill.circle"),
        ("IN_PROGRESS", "In Progress", "circle.lefthalf.filled"),
        ("COMPLETED", "Completed", "checkmark.circle.fill")
    ]

    private var selectedStep: Int {
        steps.firstIndex(where: { $0.name == task.status.name }) ?? 0
    }

    private var dueDateText: String? {
        guard let dueDate = task.dueDate else { return nil }

        let formatter = DateFormatter()
        formatter.locale = .current
        formatter.dateStyle = .medium

        // Kotlin LocalDate string format: yyyy-MM-dd
        guard let date = formatterDate(
            String(describing: dueDate)
        ) else {
            return String(describing: dueDate)
        }

        return formatter.string(from: date)
    }

    private func formatterDate(_ value: String) -> Date? {
        let formatter = DateFormatter()
        formatter.calendar = Calendar(identifier: .iso8601)
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.date(from: value)
    }

    private func formattedTimestamp(_ value: String) -> String {
        let formatter = ISO8601DateFormatter()
        formatter.formatOptions = [
            .withInternetDateTime,
            .withFractionalSeconds
        ]

        let date = formatter.date(from: value)
            ?? ISO8601DateFormatter().date(from: value)

        guard let date else { return value }

        return date.formatted(
            .dateTime.month(.abbreviated)
                .day()
                .year()
                .hour()
                .minute()
        )
    }

    private func relativeTimestamp(_ value: String) -> String {
        let formatter = ISO8601DateFormatter()
        formatter.formatOptions = [
            .withInternetDateTime,
            .withFractionalSeconds
        ]

        let date = formatter.date(from: value)
            ?? ISO8601DateFormatter().date(from: value)

        guard let date else { return "Recently updated" }

        let relativeFormatter = RelativeDateTimeFormatter()
        relativeFormatter.unitsStyle = .short

        return "Updated " + relativeFormatter.localizedString(
            for: date,
            relativeTo: Date()
        )
    }

    // MARK: - Body
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 32) {
                header

                section("Progress") {
                    progressTrack
                }

                if let description = task.description_,
                   !description.isEmpty {
                    section("Notes") {
                        Text(description)
                            .font(.body)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                }

                section("Details") {
                    VStack(spacing: 16) {
                        metadataRow(
                            title: "Created",
                            value: formattedTimestamp(
                                String(describing: task.createdAt)
                            )
                        )

                        Divider()

                        VStack(alignment: .leading, spacing: 6) {
                            metadataRow(
                                title: "Last updated",
                                value: formattedTimestamp(
                                    String(describing: task.updatedAt)
                                )
                            )

                            Text(
                                relativeTimestamp(
                                    String(describing: task.updatedAt)
                                )
                            )
                                .font(.caption)
                                .foregroundStyle(.secondary)
                                .frame(maxWidth: .infinity, alignment: .leading)
                        }
                    }
                    .padding(20)
                    .background(.regularMaterial)
                    .clipShape(RoundedRectangle(cornerRadius: 20))
                }

                Button(role: .destructive) {
                    showDeleteConfirmation = true
                } label: {
                    Label("Delete Task", systemImage: "trash")
                        .font(.system(.subheadline, design: .rounded).weight(.semibold))
                        .frame(maxWidth: .infinity)
                }
                .padding(.top, 8)
                .accessibilityLabel("Delete task")
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 16)
        }
        .background(Color(.systemBackground).ignoresSafeArea())
        .navigationTitle("Task Details")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .safeAreaInset(edge: .bottom, spacing: 0) {
            Button {
                onEdit()
            } label: {
                Label("Edit Task", systemImage: "pencil")
                    .foregroundStyle(Palette.onAccent)
                    .padding(.horizontal, 28)
                    .padding(.vertical, 14)
                    .background(Palette.accent, in: Capsule())
                    .shadow(color: Palette.accent.opacity(0.35), radius: 12, y: 6)
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
            }
            .buttonStyle(.plain)
            .padding(.vertical, 12)
            .frame(maxWidth: .infinity)
        }
        .confirmationDialog(
            "Delete this task?",
            isPresented: $showDeleteConfirmation,
            titleVisibility: .visible
        ) {
            Button("Delete Task", role: .destructive) {
                deleteTask()
            }
            .disabled(isDeleting)

            Button("Cancel", role: .cancel) {}
        } message: {
            Text("This action cannot be undone.")
        }
        .tint(Palette.accent)
        .overlay {
            if isDeleting {
                ZStack {
                    Color.black.opacity(0.15)
                        .ignoresSafeArea()

                    ProgressView("Deleting task...")
                        .padding(24)
                        .background(.regularMaterial)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                }
            }
        }
        .alert(
            "Couldn't Delete Task",
            isPresented: Binding(
                get: { deleteError != nil },
                set: { if !$0 { deleteError = nil } }
            )
        ) {
            Button("OK", role: .cancel) {
                deleteError = nil
            }
        } message: {
            Text(deleteError ?? "Something went wrong.")
        }
    }

    // MARK: - Header: tags, big title, due date (no card)

    private var header: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack(spacing: 8) {
                tag(
                    symbol: steps[selectedStep].symbol,
                    text: statusTitle,
                    color: selectedStep == 2 ? .green : Palette.accent
                )

                tag(
                    symbol: prioritySymbol,
                    text: priorityTitle,
                    color: priorityColor
                )
            }

            Text(task.title)
                .font(.system(.largeTitle, design: .rounded).weight(.bold))
                .frame(maxWidth: .infinity, alignment: .leading)

            if let dueDateText {
                Label(dueDateText, systemImage: "calendar")
                    .font(.subheadline.weight(.medium))
                    .foregroundStyle(Palette.accent)
            }
        }
    }

    private func tag(symbol: String, text: String, color: Color) -> some View {
        HStack(spacing: 5) {
            Image(systemName: symbol)
                .font(.system(size: 11, weight: .bold))
            Text(text)
                .font(.caption.weight(.semibold))
        }
        .foregroundStyle(color)
        .padding(.horizontal, 10)
        .padding(.vertical, 5)
        .background(color.opacity(0.14), in: Capsule())
    }

    // MARK: - Progress track (read-only version of the one on the create screen)

    private var progressTrack: some View {
        HStack(spacing: 0) {
            ForEach(Array(steps.enumerated()), id: \.offset) { index, step in
                VStack(spacing: 8) {
                    ZStack {
                        HStack(spacing: 0) {
                            Rectangle()
                                .fill(index == 0 ? Color.clear : stepColor(index <= selectedStep))
                                .frame(height: 3)
                            Rectangle()
                                .fill(index == steps.count - 1 ? Color.clear : stepColor(index < selectedStep))
                                .frame(height: 3)
                        }

                        Image(systemName: stepSymbol(index: index, step: step.symbol))
                            .font(.system(size: 26))
                            .foregroundStyle(index <= selectedStep ? Palette.accent : Color(.systemGray3))
                            .background(Circle().fill(Color(.systemBackground)).padding(2))
                    }

                    Text(step.title)
                        .font(.footnote.weight(index == selectedStep ? .semibold : .regular))
                        .foregroundStyle(index == selectedStep ? Color.primary : Color.secondary)
                }
                .frame(maxWidth: .infinity)
            }
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel("Status: \(statusTitle)")
    }

    private func stepColor(_ reached: Bool) -> Color {
        reached ? Palette.accent : Color(.systemGray4)
    }

    private func stepSymbol(index: Int, step: String) -> String {
        if index < selectedStep {
            return "checkmark.circle.fill"
        } else if index == selectedStep {
            return step
        } else {
            return "circle"
        }
    }

    // MARK: - Sections + rows

    private func section<Content: View>(
        _ title: String,
        @ViewBuilder content: () -> Content
    ) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title.uppercased())
                .font(.footnote.weight(.semibold))
                .tracking(0.6)
                .foregroundStyle(.secondary)

            content()
        }
    }

    private func metadataRow(
        title: String,
        value: String
    ) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.subheadline)
                .foregroundStyle(.secondary)

            Text(value)
                .font(.body)
                .textSelection(.enabled)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
