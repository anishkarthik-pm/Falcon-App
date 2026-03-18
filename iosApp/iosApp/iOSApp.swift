import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        KoinModulesKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
