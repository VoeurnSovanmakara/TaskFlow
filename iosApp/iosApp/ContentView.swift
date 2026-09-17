import SharedLogic
import SwiftUI


// MARK: - Models

struct HomeItem: Identifiable {
    let id = UUID()
    let title: String
    let subtitle: String
    let systemImage: String
    let tintColor: Color
}

// MARK: - Sample Data

extension HomeItem {
    static let sample: [HomeItem] = [
        HomeItem(title: "Design Sprint", subtitle: "Wraps up Friday", systemImage: "paintbrush.fill", tintColor: .pink),
        HomeItem(title: "Team Standup", subtitle: "Daily at 9:30 AM", systemImage: "person.3.fill", tintColor: .blue),
        HomeItem(title: "Release Notes", subtitle: "v2.4 shipped today", systemImage: "shippingbox.fill", tintColor: .orange),
        HomeItem(title: "Analytics", subtitle: "Weekly report ready", systemImage: "chart.bar.fill", tintColor: .green),
        HomeItem(title: "Feedback", subtitle: "3 new responses", systemImage: "bubble.left.and.bubble.right.fill", tintColor: .purple)
    ]
}

struct ContentView: View {

    @State private var searchText: String = ""
    @State private var selectedCategory: String = "All"

    private let categories = ["All", "Design", "Engineering", "Marketing", "Support"]
    private let items = HomeItem.sample

    private var filteredItems: [HomeItem] {
        guard !searchText.isEmpty else { return items }
        return items.filter { $0.title.localizedCaseInsensitiveContains(searchText) }
    }


    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    header

                    searchBar

                    categoryChips

                    VStack(alignment: .leading, spacing: 12) {
                        Text("Today")
                            .font(.title3.bold())
                            .padding(.horizontal)

                        ForEach(filteredItems) { item in
                            HomeItemRow(item: item)
                                .padding(.horizontal)
                        }
                    }
                }
                .padding(.top, 12)
                .padding(.bottom, 32)
            }
            .navigationBarHidden(true)
            .background(Color(.systemGroupedBackground))
        }
    }


    private var header: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text("Good morning")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                Text("Alex")
                    .font(.largeTitle.bold())
            }
            Spacer()
            Button {
                // Handle profile tap
            } label: {
                Image(systemName: "person.crop.circle.fill")
                    .font(.system(size: 36))
                    .foregroundStyle(.blue)
            }
        }
        .padding(.horizontal)
    }

    private var searchBar: some View {
        HStack(spacing: 10) {
            Image(systemName: "magnifyingglass")
                .foregroundStyle(.secondary)
            TextField("Search", text: $searchText)
                .textFieldStyle(.plain)
            if !searchText.isEmpty {
                Button {
                    searchText = ""
                } label: {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundStyle(.secondary)
                }
            }
        }
        .padding(12)
        .background(Color(.secondarySystemGroupedBackground))
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .padding(.horizontal)
    }

    private var categoryChips: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 10) {
                ForEach(categories, id: \.self) { category in
                    CategoryChip(
                        title: category,
                        isSelected: category == selectedCategory
                    ) {
                        selectedCategory = category
                    }
                }
            }
            .padding(.horizontal)
        }
    }
}

// MARK: - Category Chip

private struct CategoryChip: View {
    let title: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.subheadline.weight(.medium))
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(isSelected ? Color.blue : Color(.secondarySystemGroupedBackground))
                .foregroundStyle(isSelected ? .white : .primary)
                .clipShape(Capsule())
        }
        .buttonStyle(.plain)
    }
}

// MARK: - Home Item Row

private struct HomeItemRow: View {
    let item: HomeItem

    var body: some View {
        HStack(spacing: 14) {
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(item.tintColor.opacity(0.15))
                    .frame(width: 48, height: 48)
                Image(systemName: item.systemImage)
                    .foregroundStyle(item.tintColor)
                    .font(.system(size: 20, weight: .semibold))
            }

            VStack(alignment: .leading, spacing: 2) {
                Text(item.title)
                    .font(.headline)
                Text(item.subtitle)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }

            Spacer()

            Image(systemName: "chevron.right")
                .font(.footnote.weight(.semibold))
                .foregroundStyle(.tertiary)
        }
        .padding(14)
        .background(Color(.secondarySystemGroupedBackground))
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }
}


struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
