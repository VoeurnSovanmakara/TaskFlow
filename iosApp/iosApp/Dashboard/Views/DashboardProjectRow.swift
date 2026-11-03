//
// Created by sovanmakara on 11/2/26.
//

import SwiftUI
import SharedLogic

struct DashboardProjectRow: View {
    let project: SharedLogic.Project
    let taskCount: Int

    var body: some View {
        HStack(spacing: 14) {
            Image(systemName: "folder.fill")
                .font(.system(size: 20))
                .foregroundStyle(Palette.accent)
                .frame(width: 44, height: 44)
                .background(
                    Palette.accent.opacity(0.12),
                    in: RoundedRectangle(cornerRadius: 12, style: .continuous)
                )

            VStack(alignment: .leading, spacing: 4) {
                Text(project.name)
                    .font(.system(.subheadline, design: .rounded).weight(.semibold))
                    .lineLimit(1)

                Text("\(taskCount) \(taskCount == 1 ? "task" : "tasks")")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }

            Spacer()

            Image(systemName: "chevron.right")
                .font(.caption.weight(.semibold))
                .foregroundStyle(.tertiary)
        }
        .padding(.vertical, 12)
        .accessibilityElement(children: .combine)
    }
}