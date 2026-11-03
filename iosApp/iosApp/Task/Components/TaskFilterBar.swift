//
// Created by sovanmakara on 10/30/26.
//


import SwiftUI
import SharedLogic

struct TaskFilterBar: View {
    @Binding var selectedFilter: TaskFilter
    let tasks: [SharedLogic.Task]

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(TaskFilter.allCases) { filter in
                    filterChip(filter)
                }
            }
            .padding(.horizontal, 20)
            .animation(.easeInOut(duration: 0.15), value: selectedFilter)
        }
    }

    private func filterChip(_ filter: TaskFilter) -> some View {
        let selected = selectedFilter == filter
        let count = tasks.filter { filter.matches($0) }.count

        return Button {
            selectedFilter = filter
        } label: {
            HStack(spacing: 6) {
                Text(filter.rawValue)
                    .font(.subheadline.weight(.semibold))

                if count > 0 {
                    Text("\(count)")
                        .font(.caption.weight(.bold))
                        .opacity(0.7)
                }
            }
            .foregroundStyle(selected ? Palette.onAccent : Color.primary)
            .padding(.horizontal, 14)
            .padding(.vertical, 8)
            .background(
                selected ? Palette.accent : Color(.secondarySystemBackground),
                in: Capsule()
            )
        }
        .buttonStyle(.plain)
        .accessibilityAddTraits(selected ? .isSelected : [])
    }
}

