import SwiftUI
import SharedLogic

struct ContentView: View {
    @State private var selectedTab: AppTab = .dashboard

    var body: some View {
        TabView(selection: $selectedTab) {

            DashboardView()
            .tabItem {
                Label("Dashboard", systemImage: "square.grid.2x2")
            }
            .tag(AppTab.dashboard)

            TasksView()
            .tabItem {
                Label("Tasks", systemImage: "checklist")
            }
            .tag(AppTab.tasks)

            ProjectListView()
            .tabItem {
                Label("Projects", systemImage: "folder")
            }
            .tag(AppTab.projects)
        }
    }
}