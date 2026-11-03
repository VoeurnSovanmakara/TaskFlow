//
// Created by sovanmakara on 10/26/26.
//

import SwiftUI

enum Palette {
    static let accent = Color(UIColor { trait in
        trait.userInterfaceStyle == .dark
            ? UIColor(red: 0.773, green: 0.753, blue: 1.0, alpha: 1)   // #C5C0FF
            : UIColor(red: 0.357, green: 0.302, blue: 1.0, alpha: 1)   // #5B4DFF
    })

    // Readable text/icon color on top of the accent (white in light, deep indigo in dark)
    static let onAccent = Color(UIColor { trait in
        trait.userInterfaceStyle == .dark
            ? UIColor(red: 0.165, green: 0.106, blue: 0.620, alpha: 1)  // #2A1B9E
            : UIColor.white
    })
}
