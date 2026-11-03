//
// Created by sovanmakara on 10/30/26.
//

import SwiftUI

struct DashboardView: View {
    @State private var viewModel = DashboardViewModel()

    private let columns = [
        GridItem(.flexible(), spacing: 12),
        GridItem(.flexible(), spacing: 12)
    ]

    private var progress: Double {
        min(max(Double(viewModel.completionPercentage) / 100, 0), 1)
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 32) {

                    // MARK: - Welcome
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Welcome back!")
                            .font(.system(.largeTitle, design: .rounded).weight(.bold))

                        Text("Here's an overview of your productivity.")
                            .foregroundStyle(.secondary)
                    }

                    // MARK: - Task Progress (ring replaces the old progress box)
                    HStack(spacing: 20) {
                        ZStack {
                            Circle()
                                .stroke(Palette.accent.opacity(0.15), lineWidth: 12)

                            Circle()
                                .trim(from: 0, to: progress)
                                .stroke(
                                    Palette.accent,
                                    style: StrokeStyle(lineWidth: 12, lineCap: .round)
                                )
                                .rotationEffect(.degrees(-90))

                            Text("\(viewModel.completionPercentage)%")
                                .font(.system(.title2, design: .rounded).weight(.bold))
                        }
                        .frame(width: 104, height: 104)
                        .animation(.easeInOut(duration: 0.6), value: progress)
                        .accessibilityElement(children: .ignore)
                        .accessibilityLabel(
                            "Completion rate \(viewModel.completionPercentage) percent"
                        )

                        VStack(alignment: .leading, spacing: 4) {
                            Text("Completion Rate")
                                .font(.system(.headline, design: .rounded))

                            Text(
                                "\(viewModel.completedTasks) of \(viewModel.totalTasks) tasks completed"
                            )
                                .font(.subheadline)
                                .foregroundStyle(.secondary)
                        }

                        Spacer(minLength: 0)
                    }

                    // MARK: - Overview
                    VStack(alignment: .leading, spacing: 12) {
                        sectionHeader("Overview")

                        LazyVGrid(columns: columns, spacing: 12) {

                            DashboardCard(
                                title: "Total Tasks",
                                value: viewModel.totalTasks,
                                icon: "checklist",
                                color: Palette.accent
                            )

                            DashboardCard(
                                title: "Completed",
                                value: viewModel.completedTasks,
                                icon: "checkmark.circle",
                                color: .green
                            )

                            DashboardCard(
                                title: "Projects",
                                value: viewModel.totalProjects,
                                icon: "folder",
                                color: .orange
                            )

                            DashboardCard(
                                title: "In Progress",
                                value: viewModel.inProgressTasks,
                                icon: "clock",
                                color: .blue
                            )
                        }
                    }

                    // MARK: - Recent Tasks
                    VStack(alignment: .leading, spacing: 4) {
                        sectionHeader("Recent tasks", trailing: "Latest 5")

                        if viewModel.recentTasks.isEmpty {
                            ContentUnavailableView(
                                "No Tasks Yet",
                                systemImage: "checklist",
                                description: Text("Your recent tasks will appear here.")
                            )
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 20)
                        } else {
                            VStack(spacing: 0) {
                                ForEach(viewModel.recentTasks, id: \.id) { task in
                                    DashboardRecentTaskRow(task: task)

                                    if task.id != viewModel.recentTasks.last?.id {
                                        Divider()
                                            .padding(.leading, 38)
                                    }
                                }
                            }
                        }
                    }

                    // MARK: - Project Overview
                    VStack(alignment: .leading, spacing: 4) {
                        sectionHeader(
                            "Your Projects",
                            trailing: "\(viewModel.totalProjects) projects"
                        )

                        if viewModel.projectTaskCounts.isEmpty {
                            ContentUnavailableView(
                                "No Projects Yet",
                                systemImage: "folder",
                                description: Text("Create a project to organize your tasks.")
                            )
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 20)
                        } else {
                            VStack(spacing: 0) {
                                ForEach(
                                    viewModel.projectTaskCounts,
                                    id: \.project.id
                                ) { item in
                                    DashboardProjectRow(
                                        project: item.project,
                                        taskCount: item.taskCount
                                    )

                                    if item.project.id != viewModel.projectTaskCounts.last?.project.id {
                                        Divider()
                                            .padding(.leading, 58)
                                    }
                                }
                            }
                        }
                    }

                    // MARK: - Error
                    if let error = viewModel.errorMessage {
                        HStack(spacing: 8) {
                            Image(systemName: "exclamationmark.triangle.fill")
                            Text(error)
                        }
                        .font(.footnote.weight(.medium))
                        .foregroundStyle(.red)
                    }
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 16)
            }
            .background(Color(.systemBackground).ignoresSafeArea())
        }
        .task {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.stopObserving()
        }
    }

    private func sectionHeader(_ title: String, trailing: String? = nil) -> some View {
        HStack {
            Text(title.uppercased())
                .font(.footnote.weight(.semibold))
                .tracking(0.6)
                .foregroundStyle(.secondary)

            Spacer()

            if let trailing {
                Text(trailing)
                    .font(.footnote)
                    .foregroundStyle(.secondary)
            }
        }
    }
}

private struct DashboardCard: View {
    let title: String
    let value: Int
    let icon: String
    let color: Color

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Image(systemName: icon)
                .font(.system(size: 18, weight: .semibold))
                .foregroundStyle(color)

            Text("\(value)")
                .font(.system(size: 32, weight: .bold, design: .rounded))
                .contentTransition(.numericText())

            Text(title)
                .font(.subheadline.weight(.medium))
                .foregroundStyle(.secondary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .background(
            color.opacity(0.10),
            in: RoundedRectangle(cornerRadius: 18, style: .continuous)
        )
        .accessibilityElement(children: .combine)
    }
}