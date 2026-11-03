import SwiftUI
import SharedLogic

struct TasksView: View {
    @State private var viewModel = TasksViewModel()
    @State private var activeTaskSheet: TaskSheet?

    var body: some View {
        NavigationStack {

            Group {
                if let errorMessage = viewModel.errorMessage {
                    ContentUnavailableView(
                        "Couldn't Load Tasks",
                        systemImage: "exclamationmark.triangle",
                        description: Text(errorMessage)
                    )
                } else if viewModel.tasks.isEmpty {
                    ContentUnavailableView(
                        "No Tasks Yet",
                        systemImage: "checklist",
                        description: Text("Your tasks will appear here.")
                    )
                } else {
                    List {
                        Section {
                            TaskFilterBar(
                                selectedFilter: $viewModel.selectedFilter,
                                tasks: viewModel.tasks
                            )
                                .listRowInsets(
                                    EdgeInsets(top: 4, leading: 0, bottom: 8, trailing: 0)
                                )
                                .listRowBackground(Color.clear)
                                .listRowSeparator(.hidden)
                        }

                        Section {
                            let displayedTasks = viewModel.sortedTasks

                            if displayedTasks.isEmpty {
                                ContentUnavailableView(
                                    "No Matching Tasks",
                                    systemImage: "magnifyingglass",
                                    description: Text(
                                        viewModel.searchText.trimmingCharacters(
                                            in: .whitespacesAndNewlines
                                        ).isEmpty
                                            ? "There are no tasks in the \(viewModel.selectedFilter.rawValue) category."
                                            : "Try another search term or change your filter."
                                    )
                                )
                                    .listRowBackground(Color.clear)
                                    .listRowSeparator(.hidden)
                            } else {
                                ForEach(displayedTasks, id: \.id) { task in
                                    NavigationLink {
                                        TaskDetailView(
                                            task: task,
                                            onEdit: {
                                                activeTaskSheet = .edit(task)
                                            },
                                            onDelete: {
                                                // Existing delete behavior.
                                            }
                                        )
                                    } label: {
                                        TaskRow(task: task) {
                                            viewModel.toggleCompletion(for: task)
                                        }
                                    }
                                    .buttonStyle(.plain)
                                    .listRowInsets(
                                        EdgeInsets(
                                            top: 0,
                                            leading: 20,
                                            bottom: 0,
                                            trailing: 20
                                        )
                                    )
                                    .listRowSeparatorTint(
                                        Color(.separator).opacity(0.5)
                                    )
                                    .listRowBackground(Color.clear)
                                }
                            }
                        } header: {
                            Text(viewModel.summary)
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
            .navigationTitle("TaskFlow")
            .toolbar {
                // Sorting lives in the toolbar instead of taking a row in the list
                ToolbarItem(placement: .topBarTrailing) {
                    Menu {
                        Picker("Sort tasks", selection: $viewModel.selectedSort) {
                            ForEach(TaskSort.allCases) { sort in
                                Text(sort.rawValue)
                                    .tag(sort)
                            }
                        }
                    } label: {
                        Image(systemName: "arrow.up.arrow.down")
                    }
                    .accessibilityLabel("Sort tasks")
                }
            }
            // Native search + "create" button, in the same row (see SearchAndCreate below)
            .modifier(
                SearchAndCreate(searchText: $viewModel.searchText) {
                    activeTaskSheet = .create
                }
            )
            .sheet(item: $activeTaskSheet) { destination in
                switch destination {
                case .create:
                    CreateTaskView(task: nil, onCreated: {})

                case .edit(let task):
                    CreateTaskView(task: task, onCreated: {})
                }
            }
        }
        .alert(
            "Couldn't Update Task",
            isPresented: Binding(
                get: { viewModel.updateError != nil },
                set: { if !$0 { viewModel.clearUpdateError() } }
            )
        ) {
            Button("OK", role: .cancel) {
                viewModel.clearUpdateError()
            }
        } message: {
            Text(viewModel.updateError ?? "Please try again.")
        }
        .tint(Palette.accent)
        // Start once and keep observing while the Tasks tab is alive.
        // Stopping in onDisappear fires on every push to TaskDetailView, and the
        // restart on pop re-renders the list during the back animation.
        .onAppear {
            viewModel.startObserving()
        }
    }
}