import { useEffect, useState } from "react";
import { Text, TouchableOpacity, View, NativeModules, FlatList } from "react-native";

const App = () => {
  const { BlutoothModule } = NativeModules;

  const [pairedDevice, setPairedDevice] = useState([]);
  const [isDiscovering, setIsDiscovering] = useState(false);

  useEffect(() => {
    BlutoothModule.checkBluetoothEnabled();
    BlutoothModule.getPairedDevices()
      .then(device => setPairedDevice(device))
      .catch(error => console.error(error));
  }, []);

  const startDiscovery = () => {
    setIsDiscovering(true);
    BlutoothModule.startDiscovery();

    const timer = setTimeout(() => {
      setIsDiscovering(false);
      BlutoothModule.stopDiscovery();
      clearTimeout(timer);
    }, 15000);
  }

  return (
    <View style={{ flex: 1, padding: 20 }}>
      {pairedDevice && <View style={{ marginTop: 10 }}>
        <Text style={{ fontSize: 20 }}>Paired Devices</Text>
        <FlatList
          data={pairedDevice}
          keyExtractor={(item) => item?.address}
          renderItem={({ item }) => {
            return (
              <TouchableOpacity style={{ marginVertical: 10, padding: 6, borderColor: 'black', borderWidth: 1, borderRadius: 10 }}>
                <Text style={{ fontSize: 14, color: 'black' }}>{item?.name}</Text>
                <Text style={{ fontSize: 12, color: 'black', marginTop: 5 }}>{item?.address}</Text>
              </TouchableOpacity>
            )
          }}
        />
      </View>}
      <View style={{ marginTop: 10 }}>
        <View style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' }}>
          <Text style={{ fontSize: 20 }}>Available Devices</Text>
          <TouchableOpacity
            style={{ padding: 5, backgroundColor: 'black', alignItems: 'center', borderRadius: 10 }}
            onPress={() => startDiscovery()}
          >
            <Text style={{ color: 'white', fontSize: 12 }}>Scan</Text>
          </TouchableOpacity>
        </View>
      </View>
      {isDiscovering && <Text style={{ flex: 1, marginTop: 20, fontSize: 16, textAlign: 'center' }}>Loading...</Text>}
    </View>
  )
}

export default App;