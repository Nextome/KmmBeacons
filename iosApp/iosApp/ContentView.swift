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
        var regions = Array<KScanRegion>()

        @Published var text = "Loading..."
        var scanner = KmmBeacons()
        
        
        init() {
            print("init")
            
            regions.append(KScanRegion(uuid: "777E6B3A-4E6A-40B4-9E02-975E61DF3C27"))
            regions.append(KScanRegion(uuid: "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"))
            regions.append(KScanRegion(uuid: "3C8836E0-8FE6-11E8-9EB6-529269FB1459"))
            regions.append(KScanRegion(uuid: "ACFD065E-C3C0-11E3-9BBE-1A514932AC01"))
            regions.append(KScanRegion(uuid: "23A01AF0-232A-4518-9C0E-323FB773F5EF"))
            regions.append(KScanRegion(uuid: "4F0358E0-2EE7-11E4-8C21-0800200C9A66"))
            regions.append(KScanRegion(uuid: "F7826DA6-4FA2-4E98-8024-BC5B71E0893E"))
            
            scanner.setIosRegions(regions: regions)
            scanner.startScan()

            let obs = scanner.observeResults().watch(block: {scanResult in
                print("\(scanResult?.description ?? "empty")")
            })
                   

            /*Greeting().helloFromStateFlow().watch(block: { text in
                self.text = (text ?? "") as String
            })*/
        }
         
    }
}
