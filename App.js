import { useEffect } from "react";
import { Text, TouchableOpacity, View, NativeModules } from "react-native";

const App = () => {
  const { BlutoothModule } = NativeModules;
  useEffect(() => {
    console.log('bluetoothAvailable', BlutoothModule.bluetoothAvailable());
    console.log('bluetoothLEAvailable', BlutoothModule.bluetoothLEAvailable());
    console.log('checkBluetoothAdapter', BlutoothModule.checkBluetoothAdapter());
    BlutoothModule.checkBluetoothEnabled()
    BlutoothModule.getPairedDevices().then(
      response => console.log('PairedDevice', response)
    ).catch(error => console.log('Error', error));
  }, []);

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <TouchableOpacity
        style={{ padding: 10, backgroundColor: 'black', borderRadius: 10 }}
        onPress={() => BlutoothModule.sampleToast('Success!!!')}
      >
        <Text style={{ color: 'white' }}>Click</Text>
      </TouchableOpacity>
    </View>
  )
}

export default App;