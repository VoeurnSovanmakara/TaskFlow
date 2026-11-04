//
// Created by sovanmakara on 10/30/26.
//

import SwiftUI
import Observation
import SharedLogic

@MainActor
@Observable
final class TasksViewModel {

    // MARK: - Task data

    private(set) var tasks: [SharedLogic.Task] = []
    private(set) var errorMessage: String?

    // MARK: - Screen state

    var searchText = ""
    var selectedFilter: TaskFilter = .all
    var selectedSort: TaskSort = .newest

    // MARK: - Update state

    private(set) var isUpdatingTask = false
    private(set) var updateError: String?

    // MARK: - Dependencies

    private let observer: TaskListObserver
    private let updater = KoinIosKt.createTaskUpdater()

    init() {
        observer = KoinIosKt.createTaskListObserver()
    }

    // MARK: - Observation

    // Observation is idempotent: pushing a screen and coming back must NOT restart the
    // flow, or `tasks` is re-assigned in the middle of the pop / tab bar animation.
    private var isObserving = false

    func startObserving() {
        guard !isObserving else { return }
        isObserving = true

        observer.start(
            onTasks: { [weak self] tasks in
                _Concurrency.Task { @MainActor in
                    self?.tasks = tasks
                    self?.errorMessage = nil
                }
            },
            onError: { [weak self] message in
                _Concurrency.Task { @MainActor in
                    self?.errorMessage = message
                }
            }
        )
    }

    func stopObserving() {
        guard isObserving else { return }
        isObserving = false

        observer.stop()
    }

    // MARK: - Filtering

    var filteredTasks: [SharedLogic.Task] {
        let query = searchText.trimmingCharacters(
            in: .whitespacesAndNewlines
        )

        return tasks.filter { task in
            let matchesFilter = selectedFilter.matches(task)

            let matchesSearch = query.isEmpty
                || task.title.localizedCaseInsensitiveContains(query)
                || (task.description_ ?? "")
                .localizedCaseInsensitiveContains(query)

            return matchesFilter && matchesSearch
        }
    }

    func count(for filter: TaskFilter) -> Int {
        tasks.filter { filter.matches($0) }.count
    }

    // MARK: - Sorting

    var sortedTasks: [SharedLogic.Task] {
        switch selectedSort {
        case .newest:
            // Build each key once (String(describing:) on a Kotlin object is not free)
            return filteredTasks
            .map { (task: $0, key: String(describing: $0.createdAt)) }
            .sorted { $0.key > $1.key }
            .map(\.task)

        case .priority:
            let priorityOrder = [
                "HIGH": 0,
                "MEDIUM": 1,
                "LOW": 2
            ]

            return filteredTasks.sorted {
                (priorityOrder[$0.priority.name] ?? 3)
                    < (priorityOrder[$1.priority.name] ?? 3)
            }

        case .dueDate:
            return filteredTasks
            .map { (task: $0, key: $0.dueDate.map { String(describing: $0) }) }
            .sorted { first, second in
                guard let firstKey = first.key else {
                    return false
                }

                guard let secondKey = second.key else {
                    return true
                }

                return firstKey < secondKey
            }
            .map(\.task)
        }
    }

    // MARK: - Summary

    var completedCount: Int {
        tasks.filter { $0.status.name == "COMPLETED" }.count
    }

    var summary: String {
        "\(tasks.count - completedCount) open · \(completedCount) done"
    }

    // MARK: - Task actions

    func toggleCompletion(for task: SharedLogic.Task) {
        guard !isUpdatingTask else { return }

        isUpdatingTask = true
        updateError = nil

        let newStatus = task.status.name == "COMPLETED"
            ? "TODO"
            : "COMPLETED"

        updater.update(
            task: task,
            title: task.title,
            description: task.description_,
            priority: task.priority.name,
            status: newStatus,
            dueDate: task.dueDate.map {
                String(describing: $0)
            },
            reminderAt: task.reminderAt.map {
                String(describing: $0)
            },
            onSuccess: { [weak self] _ in
                _Concurrency.Task { @MainActor in
                    self?.isUpdatingTask = false
                }
            },
            onError: { [weak self] message in
                _Concurrency.Task { @MainActor in
                    self?.isUpdatingTask = false
                    self?.updateError = message
                }
            }
        )
    }

    func clearUpdateError() {
        updateError = nil
    }
}