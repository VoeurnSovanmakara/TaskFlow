//
// Created by sovanmakara on 10/27/26.
//

import SwiftUI
import Observation
import SharedLogic

@MainActor
@Observable
final class ProjectListViewModel {
    private(set) var projects: [SharedLogic.Project] = []
    private(set) var errorMessage: String?
    private(set) var creationErrorMessage: String?
    private(set) var isSaving = false

    private let observer: ProjectListObserver
    private let creator: ProjectCreator

    init() {
        observer = KoinIosKt.createProjectListObserver()
        creator = KoinIosKt.createProjectCreator()
    }

    func startObserving() {
        observer.start(
            onProjects: { [weak self] projects in
                _Concurrency.Task { @MainActor in
                    self?.projects = projects
                    self?.errorMessage = nil
                }
            },
            onError: { [weak self] error in
                _Concurrency.Task { @MainActor in
                    self?.errorMessage = error.message ?? "Something went wrong."
                }
            }
        )
    }

    func stopObserving() {
        observer.stop()
    }

    func createProject(
        name: String,
        description: String?,
        onSuccess: @escaping () -> Void
    ) {
        guard !isSaving else { return }

        isSaving = true
        creationErrorMessage = nil

        creator.create(
            name: name,
            description: description,
            onSuccess: { [weak self] in
                _Concurrency.Task { @MainActor in
                    self?.isSaving = false
                    onSuccess()
                }
            },
            onError: { [weak self] error in
                _Concurrency.Task { @MainActor in
                    self?.isSaving = false
                    self?.creationErrorMessage =
                        error.message ?? "Couldn't create project."
                }
            }
        )
    }
}