import { Text, TouchableOpacity, View } from "react-native";

const App = () => {
  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <TouchableOpacity style={{ padding: 10, backgroundColor: 'black', borderRadius: 10 }}>
        <Text style={{ color: 'white' }}>Click</Text>
      </TouchableOpacity>
    </View>
  )
}

export default App;