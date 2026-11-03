//
// Created by sovanmakara on 10/30/26.
//

import Foundation
import SharedLogic


@MainActor
@Observable
final class DashboardViewModel {
    private(set) var tasks: [SharedLogic.Task] = []
    private(set) var projects: [SharedLogic.Project] = []
    private(set) var errorMessage: String?

    private let taskObserver: TaskListObserver
    private let projectObserver: ProjectListObserver
    private var isObserving = false

    init() {
        taskObserver = KoinIosKt.createTaskListObserver()
        projectObserver = KoinIosKt.createProjectListObserver()
    }

    func startObserving() {
        guard !isObserving else { return }
        isObserving = true

        taskObserver.start(
            onTasks: { [weak self] tasks in
                _Concurrency.Task { @MainActor in
                    self?.tasks = tasks
                }
            },
            onError: { [weak self] message in
                _Concurrency.Task { @MainActor in
                    self?.errorMessage = message
                }
            }
        )

        projectObserver.start(
            onProjects: { [weak self] projects in
                _Concurrency.Task { @MainActor in
                    self?.projects = projects
                }
            },
            onError: { [weak self] error in
                _Concurrency.Task { @MainActor in
                    self?.errorMessage =
                        error.message ?? "Couldn't load projects."
                }
            }
        )
    }

    func stopObserving() {
        guard isObserving else { return }
        isObserving = false

        taskObserver.stop()
        projectObserver.stop()
    }

    var totalTasks: Int {
        tasks.count
    }

    var completedTasks: Int {
        tasks.filter {
            $0.status.name == "COMPLETED"
        }.count
    }

    var inProgressTasks: Int {
        tasks.filter {
            $0.status.name == "IN_PROGRESS"
        }.count
    }

    var totalProjects: Int {
        projects.count
    }

    var openTasks: Int {
        totalTasks - completedTasks
    }

    var completionPercentage: Int {
        guard totalTasks > 0 else { return 0 }

        return Int(
            (Double(completedTasks) / Double(totalTasks) * 100).rounded()
        )
    }

    // MARK: - Statistics
    var recentTasks: [SharedLogic.Task] {
        tasks
        .sorted {
            String(describing: $0.createdAt)
                > String(describing: $1.createdAt)
        }
        .prefix(5)
        .map { $0 }
    }

    var projectTaskCounts: [(project: SharedLogic.Project, taskCount: Int)] {
        projects.map { project in
            let count = tasks.filter {
                $0.projectId == project.id
            }.count

            return (project: project, taskCount: count)
        }
    }
}