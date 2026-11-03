//
// Created by sovanmakara on 10/30/26.
//

import SwiftUI


struct SearchAndCreate: ViewModifier {
    @Binding var searchText: String
    let onCreate: () -> Void

    func body(content: Content) -> some View {
        content
            .searchable(
                text: $searchText,
                prompt: "Search tasks"
            )
            .textInputAutocapitalization(.never)
            .autocorrectionDisabled()
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button(action: onCreate) {
                        Image(systemName: "plus")
                            .font(.system(size: 18, weight: .semibold))
                    }
                    .accessibilityLabel("Create Task")
                }
            }
    }
}