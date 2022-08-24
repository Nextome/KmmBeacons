import SwiftUI
import shared

struct ContentView: View {
    @StateObject var text = ViewModel()
    
	var body: some View {
        Text(text.text)
    
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}

extension ContentView {
    class ViewModel: ObservableObject {
        @Published var text = "Loading..."
        
        init() {
            
            Greeting().greetingSuspend { text, error in
                DispatchQueue.main.async {
                    self.text = text ?? "No Text"
                }
            }
            
            
            // Greeting().greetingObservable { cflow, error in
            //    cflow?.watch(block: { text in
            //        self.text = (text ?? "Loading...") as String
            //    })
            
        }
    }
}

