//
// Created by sovanmakara on 10/26/26.
//

import Foundation
import SharedLogic

enum TaskSheet: Identifiable {
    case create
    case edit(SharedLogic.Task)

    var id: String {
        switch self {
        case .create:
            return "create"
        case .edit(let task):
            return "edit-\(task.id)"
        }
    }
}