import SwiftUI
import KBeaconScanner

struct ContentView: View {
    @ObservedObject private(set) var viewModel: ViewModel

    var body: some View {
        Text(viewModel.text)
    }
}

extension ContentView {
    class ViewModel: ObservableObject {
        @Published var text = "Loading..."
        var scanner = KmmBeacons()
        
        init() {
            scanner.start()
            scanner.observeResults().watch(block: {scanResult in
                print("ScanResult \(scanResult?.description ?? "empty")")
            })

            /*Greeting().helloFromStateFlow().watch(block: { text in
                self.text = (text ?? "") as String
            })*/
        }
         
    }
}
