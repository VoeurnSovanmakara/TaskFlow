//
// Created by sovanmakara on 10/22/26.
//

import SwiftUI
import SharedLogic

struct CreateTaskView: View {
    @Environment(\.dismiss) private var dismiss

    let task: SharedLogic.Task?
    let onCreated: () -> Void

    @State private var title = ""
    @State private var description = ""
    @State private var priority: TaskPriorityOption = .medium
    @State private var status: TaskStatusOption = .todo
    @State private var hasDueDate = false
    @State private var dueDate = Date()
    @State private var hasReminder = false
    @State private var reminderDate = Calendar.current.date(
        byAdding: .hour,
        value: 1,
        to: Date()
    ) ?? Date()
    @State private var errorMessage: String?
    @State private var isSaving = false

    @State private var reminderSaveError: String?
    @State private var showReminderSaveAlert = false

    @State private var showDateSheet = false
    @FocusState private var titleFocused: Bool

    private let creator = KoinIosKt.createTaskCreator()
    private let updater = KoinIosKt.createTaskUpdater()

    private var isEditing: Bool {
        task != nil
    }

    private var formattedReminderDate: String? {
        guard hasReminder else { return nil }

        return ISO8601DateFormatter().string(from: reminderDate)
    }

    // MARK: - Logic (unchanged)

    private func saveTask() {
        let trimmedTitle = title.trimmingCharacters(
            in: .whitespacesAndNewlines
        )

        guard !trimmedTitle.isEmpty else {
            errorMessage = "Please enter a task title."
            return
        }

        let trimmedDescription = description.trimmingCharacters(
            in: .whitespacesAndNewlines
        )

        let formattedDueDate: String? = hasDueDate
            ? {
            let formatter = DateFormatter()
            formatter.calendar = Calendar(identifier: .iso8601)
            formatter.timeZone = TimeZone(secondsFromGMT: 0)
            formatter.dateFormat = "yyyy-MM-dd"
            return formatter.string(from: dueDate)
        }()
            : nil

        isSaving = true
        errorMessage = nil

        let onSuccess: (String) -> Void = { taskID in
            _Concurrency.Task { @MainActor in
                isSaving = false

                do {
                    if hasReminder {
                        try await TaskReminderNotificationManager.shared.scheduleReminder(
                            taskID: taskID,
                            title: trimmedTitle,
                            reminderDate: reminderDate
                        )
                    } else {
                        TaskReminderNotificationManager.shared.cancelReminder(
                            taskID: taskID
                        )
                    }

                    // Refresh the task list and close the form.
                    onCreated()
                    dismiss()

                } catch {
                    reminderSaveError = error.localizedDescription
                    showReminderSaveAlert = true
                }
            }
        }

        let onError: (String) -> Void = { message in
            _Concurrency.Task { @MainActor in
                isSaving = false
                errorMessage = message
            }
        }

        if let task {
            updater.update(
                task: task,
                title: trimmedTitle,
                description: trimmedDescription.isEmpty
                    ? nil
                    : trimmedDescription,
                priority: priority.rawValue,
                status: status.rawValue,
                dueDate: formattedDueDate,
                reminderAt: formattedReminderDate,
                onSuccess: onSuccess,
                onError: onError
            )
        } else {
            creator.create(
                title: trimmedTitle,
                description: trimmedDescription,
                priority: priority.rawValue,
                status: status.rawValue,
                dueDate: formattedDueDate,
                reminderAt: formattedReminderDate,
                onSuccess: onSuccess,
                onError: onError
            )
        }
    }

    private func populateForm() {
        guard let task else { return }

        title = task.title
        description = task.description_ ?? ""

        priority = TaskPriorityOption(
            rawValue: task.priority.name
        ) ?? .medium

        status = TaskStatusOption(
            rawValue: task.status.name
        ) ?? .todo

        if let taskDueDate = task.dueDate {
            let formatter = DateFormatter()
            formatter.calendar = Calendar(identifier: .iso8601)
            formatter.timeZone = TimeZone(secondsFromGMT: 0)
            formatter.dateFormat = "yyyy-MM-dd"

            if let parsedDate = formatter.date(
                from: String(describing: taskDueDate)
            ) {
                dueDate = parsedDate
                hasDueDate = true
            }
        } else {
            hasDueDate = false
        }

        if let reminder = task.reminderAt {
            let formatter = ISO8601DateFormatter()
            formatter.formatOptions = [
                .withInternetDateTime,
                .withFractionalSeconds
            ]

            let parsedDate = formatter.date(from: String(describing: reminder))
                ?? ISO8601DateFormatter().date(
                from: String(describing: reminder)
            )

            if let parsedDate {
                reminderDate = parsedDate
                hasReminder = true
            }
        } else {
            hasReminder = false
        }
    }

    private var titleIsValid: Bool {
        !title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    // Choosing a date anywhere also switches "has due date" on
    private var dueDateBinding: Binding<Date> {
        Binding(
            get: { dueDate },
            set: { newValue in
                dueDate = newValue
                hasDueDate = true
            }
        )
    }

    // MARK: - Body

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 32) {
                    header
                        .padding(.horizontal, 20)

                    section("Priority") {
                        priorityTiles
                    }
                    .padding(.horizontal, 20)

                    section("Progress") {
                        statusTrack
                    }
                    .padding(.horizontal, 20)

                    dueSection

                    section("Reminder") {
                        VStack(alignment: .leading, spacing: 12) {
                            Toggle("Set a reminder", isOn: $hasReminder)
                                .font(.subheadline.weight(.medium))
                                .tint(Palette.accent)

                            if hasReminder {
                                DatePicker(
                                    "Remind me",
                                    selection: $reminderDate,
                                    in: Date()...,
                                    displayedComponents: [.date, .hourAndMinute]
                                )
                                    .datePickerStyle(.compact)

                                Text("TaskFlow will notify you at this date and time.")
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                        }
                    }
                    .padding(.horizontal, 20)

                    if let errorMessage {
                        HStack(spacing: 8) {
                            Image(systemName: "exclamationmark.triangle.fill")
                            Text(errorMessage)
                        }
                        .font(.footnote.weight(.medium))
                        .foregroundStyle(.red)
                        .padding(.horizontal, 20)
                    }
                }
                .padding(.vertical, 12)
            }
            .scrollDismissesKeyboard(.interactively)
            .background(Color(.systemBackground).ignoresSafeArea())
            .navigationTitle(isEditing ? "Edit Task" : "New Task")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        dismiss()
                    }
                    .disabled(isSaving)
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button {
                        saveTask()
                    } label: {
                        if isSaving {
                            ProgressView()
                        } else {
                            Text(isEditing ? "Save Changes" : "Add")
                                .fontWeight(.semibold)
                        }
                    }
                    .disabled(!titleIsValid || isSaving)
                }
            }
            .sheet(isPresented: $showDateSheet) {
                dateSheet
            }
            .onAppear {
                populateForm()

                if !isEditing {
                    DispatchQueue.main.async {
                        titleFocused = true
                    }
                }
            }
        }
        .tint(Palette.accent)
        .alert(
            "Task Saved",
            isPresented: $showReminderSaveAlert
        ) {
            Button("OK") {
                onCreated()
                dismiss()
            }
        } message: {
            Text(
                "Your task was saved, but the reminder could not be scheduled. "
                    + (reminderSaveError ?? "Please check your notification permissions.")
            )
        }
    }

    // MARK: - Header: big title + notes, no card, no chrome

    private var header: some View {
        VStack(alignment: .leading, spacing: 14) {
            TextField(
                "",
                text: $title,
                prompt: Text("What needs doing?")
                    .foregroundColor(Color(.tertiaryLabel)),
                axis: .vertical
            )
                .focused($titleFocused)
                .textInputAutocapitalization(.sentences)
                .font(.system(.largeTitle, design: .rounded).weight(.bold))
                .lineLimit(1...4)

            HStack(alignment: .top, spacing: 10) {
                Image(systemName: "text.alignleft")
                    .foregroundStyle(.secondary)
                    .padding(.top, 3)

                TextField(
                    "",
                    text: $description,
                    prompt: Text("Add notes")
                        .foregroundColor(Color(.tertiaryLabel)),
                    axis: .vertical
                )
                    .font(.body)
                    .lineLimit(2...8)
            }
        }
    }

    // MARK: - Priority: three big tiles

    private var priorityTiles: some View {
        HStack(spacing: 10) {
            ForEach(TaskPriorityOption.allCases) { option in
                let selected = option == priority

                Button {
                    priority = option
                } label: {
                    VStack(spacing: 6) {
                        Image(systemName: option.symbol)
                            .font(.system(size: 18, weight: .bold))
                        Text(option.title)
                            .font(.footnote.weight(.semibold))
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 68)
                    .foregroundStyle(selected ? Color.white : option.color)
                    .background(
                        selected ? option.color : Color(.secondarySystemBackground),
                        in: RoundedRectangle(cornerRadius: 16, style: .continuous)
                    )
                }
                .buttonStyle(.plain)
            }
        }
        .animation(.easeInOut(duration: 0.15), value: priority)
    }

    // MARK: - Status: a progress track (To Do ─ In Progress ─ Completed)

    private var statusTrack: some View {
        let steps = TaskStatusOption.allCases
        let selectedIndex = steps.firstIndex(of: status) ?? 0

        return HStack(spacing: 0) {
            ForEach(Array(steps.enumerated()), id: \.element) { index, option in
                Button {
                    status = option
                } label: {
                    VStack(spacing: 8) {
                        ZStack {
                            HStack(spacing: 0) {
                                Rectangle()
                                    .fill(index == 0 ? Color.clear : stepColor(index <= selectedIndex))
                                    .frame(height: 3)
                                Rectangle()
                                    .fill(index == steps.count - 1 ? Color.clear : stepColor(index < selectedIndex))
                                    .frame(height: 3)
                            }

                            Image(systemName: stepSymbol(index: index, selectedIndex: selectedIndex, option: option))
                                .font(.system(size: 26))
                                .foregroundStyle(index <= selectedIndex ? Palette.accent : Color(.systemGray3))
                                .background(Circle().fill(Color(.systemBackground)).padding(2))
                        }

                        Text(option.title)
                            .font(.footnote.weight(index == selectedIndex ? .semibold : .regular))
                            .foregroundStyle(index == selectedIndex ? Color.primary : Color.secondary)
                    }
                    .frame(maxWidth: .infinity)
                    .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
            }
        }
        .animation(.easeInOut(duration: 0.15), value: status)
    }

    private func stepColor(_ reached: Bool) -> Color {
        reached ? Palette.accent : Color(.systemGray4)
    }

    private func stepSymbol(index: Int, selectedIndex: Int, option: TaskStatusOption) -> String {
        if index < selectedIndex {
            return "checkmark.circle.fill"
        } else if index == selectedIndex {
            return option.symbol
        } else {
            return "circle"
        }
    }

    // MARK: - Due date: a horizontal day strip

    private var dueSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                sectionTitle("Due")
                Spacer()
                Text(hasDueDate
                         ? dueDate.formatted(date: .complete, time: .omitted)
                         : "No due date")
                    .font(.footnote.weight(.medium))
                    .foregroundStyle(hasDueDate ? Palette.accent : Color.secondary)
            }
            .padding(.horizontal, 20)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 10) {
                    stripTile(symbol: "xmark", label: "None", selected: !hasDueDate) {
                        hasDueDate = false
                    }

                    ForEach(0..<14, id: \.self) { offset in
                        let date = Calendar.current.date(byAdding: .day, value: offset, to: Date()) ?? Date()
                        dayTile(date: date, isToday: offset == 0)
                    }

                    stripTile(symbol: "calendar", label: "More", selected: false) {
                        showDateSheet = true
                    }
                }
                .padding(.horizontal, 20)
            }
            .animation(.easeInOut(duration: 0.15), value: hasDueDate)
        }
    }

    private func dayTile(date: Date, isToday: Bool) -> some View {
        let selected = hasDueDate && Calendar.current.isDate(dueDate, inSameDayAs: date)

        return Button {
            dueDate = date
            hasDueDate = true
        } label: {
            VStack(spacing: 4) {
                Text(isToday ? "Today" : date.formatted(.dateTime.weekday(.abbreviated)))
                    .font(.caption2.weight(.semibold))
                    .textCase(.uppercase)
                Text(date.formatted(.dateTime.day()))
                    .font(.system(.title3, design: .rounded).weight(.bold))
            }
            .frame(width: 60, height: 72)
            .foregroundStyle(selected ? Color.white : Color.primary)
            .background(
                selected ? Palette.accent : Color(.secondarySystemBackground),
                in: RoundedRectangle(cornerRadius: 16, style: .continuous)
            )
        }
        .buttonStyle(.plain)
    }

    private func stripTile(
        symbol: String,
        label: String,
        selected: Bool,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            VStack(spacing: 6) {
                Image(systemName: symbol)
                    .font(.system(size: 17, weight: .semibold))
                Text(label)
                    .font(.caption2.weight(.semibold))
                    .textCase(.uppercase)
            }
            .frame(width: 60, height: 72)
            .foregroundStyle(selected ? Color.white : Color.secondary)
            .background(
                selected ? Color.gray : Color(.secondarySystemBackground),
                in: RoundedRectangle(cornerRadius: 16, style: .continuous)
            )
        }
        .buttonStyle(.plain)
    }

    private var dateSheet: some View {
        NavigationStack {
            DatePicker(
                "Due date",
                selection: dueDateBinding,
                displayedComponents: .date
            )
                .datePickerStyle(.graphical)
                .labelsHidden()
                .padding()
                .navigationTitle("Pick a date")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .confirmationAction) {
                        Button("Done") {
                            showDateSheet = false
                        }
                        .fontWeight(.semibold)
                    }
                }
        }
        .presentationDetents([.medium])
    }

    // MARK: - Small helpers

    private func section<Content: View>(
        _ title: String,
        @ViewBuilder content: () -> Content
    ) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            sectionTitle(title)
            content()
        }
    }

    private func sectionTitle(_ text: String) -> some View {
        Text(text.uppercased())
            .font(.footnote.weight(.semibold))
            .tracking(0.6)
            .foregroundStyle(.secondary)
    }
}

// MARK: - Options (same cases and raw values; symbols and colors added for the UI)

private enum TaskPriorityOption: String, CaseIterable, Identifiable {
    case low = "LOW"
    case medium = "MEDIUM"
    case high = "HIGH"

    var id: String { rawValue }

    var title: String {
        switch self {
        case .low: "Low"
        case .medium: "Medium"
        case .high: "High"
        }
    }

    var symbol: String {
        switch self {
        case .low: "arrow.down"
        case .medium: "minus"
        case .high: "arrow.up"
        }
    }

    var color: Color {
        switch self {
        case .low: .blue
        case .medium: .orange
        case .high: .red
        }
    }
}

private enum TaskStatusOption: String, CaseIterable, Identifiable {
    case todo = "TODO"
    case inProgress = "IN_PROGRESS"
    case completed = "COMPLETED"

    var id: String { rawValue }

    var title: String {
        switch self {
        case .todo: "To Do"
        case .inProgress: "In Progress"
        case .completed: "Completed"
        }
    }

    var symbol: String {
        switch self {
        case .todo: "largecircle.fill.circle"
        case .inProgress: "circle.lefthalf.filled"
        case .completed: "checkmark.circle.fill"
        }
    }
}
