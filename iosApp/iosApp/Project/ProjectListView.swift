//
// Created by sovanmakara on 10/27/26.
//

import SwiftUI
import SharedLogic

struct ProjectListView: View {
    @State private var viewModel = ProjectListViewModel()
    @State private var isShowingCreateProject = false

    private var summary: String {
        let count = viewModel.projects.count
        return "\(count) \(count == 1 ? "project" : "projects")"
    }

    var body: some View {
        NavigationStack {
            Group {
                if let errorMessage = viewModel.errorMessage {
                    ContentUnavailableView(
                        "Couldn't Load Projects",
                        systemImage: "exclamationmark.triangle",
                        description: Text(errorMessage)
                    )
                } else if viewModel.projects.isEmpty {
                    ContentUnavailableView {
                        Label("No Projects Yet", systemImage: "folder")
                    } description: {
                        Text("Create a project to organize your tasks.")
                    } actions: {
                        // Same action as the "+" in the toolbar
                        Button("New Project") {
                            isShowingCreateProject = true
                        }
                        .buttonStyle(.borderedProminent)
                    }
                } else {
                    List {
                        Section {
                            ForEach(viewModel.projects, id: \.id) { project in
                                ProjectRow(project: project)
                                    .listRowInsets(
                                        EdgeInsets(top: 0, leading: 20, bottom: 0, trailing: 20)
                                    )
                                    .listRowSeparatorTint(Color(.separator).opacity(0.5))
                                    .listRowBackground(Color.clear)
                            }
                        } header: {
                            Text(summary)
                                .font(.footnote.weight(.semibold))
                                .foregroundStyle(.secondary)
                                .textCase(nil)
                                .padding(.leading, 4)
                        }
                    }
                    .listStyle(.plain)
                    .scrollContentBackground(.hidden)
                }
            }
            .background(Color(.systemBackground).ignoresSafeArea())
            .navigationTitle("Projects")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        isShowingCreateProject = true
                    } label: {
                        Image(systemName: "plus")
                    }
                    .accessibilityLabel("Create project")
                }
            }
        }
        .tint(Palette.accent)
        .sheet(isPresented: $isShowingCreateProject) {
            CreateProjectView(
                isSaving: viewModel.isSaving,
                errorMessage: viewModel.creationErrorMessage,
                onSave: { name, description in
                    viewModel.createProject(
                        name: name,
                        description: description
                    ) {
                        isShowingCreateProject = false
                    }
                }
            )
        }
        .onAppear {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.stopObserving()
        }
    }
}

private struct ProjectRow: View {
    let project: SharedLogic.Project

    // Each project gets a stable color from its name, so the list isn't one flat tone.
    // (Not hashValue: Swift randomizes that on every launch.)
    private var tint: Color {
        let colors: [Color] = [Palette.accent, .orange, .green, .pink, .teal, .purple]
        let seed = project.name.unicodeScalars.reduce(0) { $0 + Int($1.value) }
        return colors[seed % colors.count]
    }

    var body: some View {
        HStack(spacing: 14) {
            Image(systemName: "folder.fill")
                .font(.system(size: 20))
                .foregroundStyle(tint)
                .frame(width: 44, height: 44)
                .background(
                    tint.opacity(0.14),
                    in: RoundedRectangle(cornerRadius: 12, style: .continuous)
                )

            VStack(alignment: .leading, spacing: 4) {
                Text(project.name)
                    .font(.system(.body, design: .rounded).weight(.semibold))
                    .lineLimit(1)

                if let description = project.description_,
                   !description.isEmpty {
                    Text(description)
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                        .lineLimit(2)
                }
            }

            Spacer(minLength: 0)
        }
        .padding(.vertical, 12)
        .accessibilityElement(children: .combine)
    }
}