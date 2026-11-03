//
// Created by sovanmakara on 10/28/26.
//

import SwiftUI

struct CreateProjectView: View {
    @Environment(\.dismiss) private var dismiss

    @State private var name = ""
    @State private var projectDescription = ""
    @FocusState private var nameFocused: Bool

    let isSaving: Bool
    let errorMessage: String?
    let onSave: (String, String?) -> Void

    private var trimmedName: String {
        name.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private func save() {
        let description = projectDescription
            .trimmingCharacters(
                in: .whitespacesAndNewlines
            )

        onSave(
            trimmedName,
            description.isEmpty ? nil : description
        )
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 24) {
                    Image(systemName: "folder.fill")
                        .font(.system(size: 30))
                        .foregroundStyle(Palette.accent)
                        .frame(width: 64, height: 64)
                        .background(
                            Palette.accent.opacity(0.14),
                            in: RoundedRectangle(cornerRadius: 18, style: .continuous)
                        )
                        .accessibilityHidden(true)

                    TextField(
                        "",
                        text: $name,
                        prompt: Text("Name your project")
                            .foregroundColor(Color(.tertiaryLabel)),
                        axis: .vertical
                    )
                        .focused($nameFocused)
                        .textInputAutocapitalization(.words)
                        .font(.system(.largeTitle, design: .rounded).weight(.bold))
                        .lineLimit(1...3)

                    HStack(alignment: .top, spacing: 10) {
                        Image(systemName: "text.alignleft")
                            .foregroundStyle(.secondary)
                            .padding(.top, 3)

                        TextField(
                            "",
                            text: $projectDescription,
                            prompt: Text("Add a description (optional)")
                                .foregroundColor(Color(.tertiaryLabel)),
                            axis: .vertical
                        )
                            .font(.body)
                            .lineLimit(3...8)
                    }

                    if let errorMessage {
                        HStack(spacing: 8) {
                            Image(systemName: "exclamationmark.triangle.fill")
                            Text(errorMessage)
                        }
                        .font(.footnote.weight(.medium))
                        .foregroundStyle(.red)
                    }
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 24)
                .disabled(isSaving)
            }
            .scrollDismissesKeyboard(.interactively)
            .background(Color(.systemBackground).ignoresSafeArea())
            .navigationTitle("New Project")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        dismiss()
                    }
                    .disabled(isSaving)
                }

                ToolbarItem(placement: .confirmationAction) {
                    Button {
                        save()
                    } label: {
                        if isSaving {
                            ProgressView()
                        } else {
                            Text("Add")
                                .fontWeight(.semibold)
                        }
                    }
                    .disabled(trimmedName.isEmpty || isSaving)
                }
            }
            .onAppear {
                DispatchQueue.main.async {
                    nameFocused = true
                }
            }
        }
        .tint(Palette.accent)
    }
}