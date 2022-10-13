import SwiftUI
import kmmbeacons

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
            print("init")

            let obs = scanner.observeResults().watch(block: {scanResult in
                print("\(scanResult?.description ?? "empty")")
            })
            
            // obs.close()
        

            /*Greeting().helloFromStateFlow().watch(block: { text in
                self.text = (text ?? "") as String
            })*/
        }
         
    }
}
