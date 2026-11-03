//
// Created by sovanmakara on 10/26/26.
//

import Foundation

enum TaskSort: String, CaseIterable, Identifiable {
    case newest = "Newest"
    case priority = "Priority"
    case dueDate = "Due Date"

    var id: String { rawValue }
}