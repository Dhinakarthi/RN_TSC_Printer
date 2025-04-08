import { useEffect, useState } from "react";
import { Text, TouchableOpacity, View, NativeModules, FlatList } from "react-native";

const App = () => {
  const { BlutoothModule } = NativeModules;

  const [pairedDevice, setPairedDevice] = useState([]);

  useEffect(() => {
    BlutoothModule.checkBluetoothEnabled();
    BlutoothModule.getPairedDevices()
      .then(device => setPairedDevice(device))
      .catch(error => console.error(error));
  }, []);

  return (
    <View style={{ flex: 1, padding: 25 }}>
      {pairedDevice && <View>
        <Text style={{ fontSize: 20 }}>Paired Devices</Text>
        <FlatList
          data={pairedDevice}
          keyExtractor={(item) => item?.address}
          renderItem={({ item }) => {
            return (
              <TouchableOpacity style={{ marginVertical: 10, padding: 6, borderColor: 'black', borderWidth: 1, borderRadius: 10 }}>
                <Text style={{ fontSize: 14, color: 'black', }}>{item?.name}</Text>
                <Text style={{ fontSize: 12, color: 'black', marginTop: 5 }}>{item?.address}</Text>
              </TouchableOpacity>
            )
          }}
        />
      </View>}
    </View>
  )
}

export default App;