# Taptrack TCMP Tappy SDK
This project provides an SDK for interfacing with a TapTrack Tappy NFC readers. The 'app' module contains the Tappy NFC Reader demo app found at
https://play.google.com/store/apps/details?id=com.taptrack.bletappyexample

This version replaces the previous TappyBLE SDK and provides a simplified API
as well as the ability to use TappyUSB readers with Android devices that
can act as a USB Host.

## Common Gradle Dependencies
```groovy
// Universal Dependencies
implementation 'com.taptrack.tcmptappy2:tcmp:2.0.0'
implementation 'com.taptrack.tcmptappy2:tappy:2.0.0'

// TappyBLE readers
implementation 'com.taptrack.tcmptappy2:tappyble:2.0.0'
implementation 'com.taptrack.tcmptappy:blescannercompat:0.9.3'

// TappyUSB readers
implementation 'com.taptrack.tcmptappy2:tappyusb:2.0.0'
implementation 'com.github.felHR85:UsbSerial:4.5'

// For working with raw TCMP messages
// For converting between TCMP messages based on the previous SDK
implementation 'com.taptrack.tcmptappy2:tcmpconverter:2.0.0'
```

Note: You will also need to add the Jitpack Maven repository to your 
project's Gradle file:

```groovy
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
``` 


# Usage
The Tappy operates on an asynchronous communication model. In order to tell
the Tappy to perform an operation, you send it a command with the details
of the operation to perform. The Tappy similarly sends asynchronous responses back to the
client to inform it of any pertinent events that occurred. Depending on the
command sent to the Tappy assuming no error occurs, it may reply
immediately (PingCommand->PingResponse), it may not reply at all (StopCommand),
or it may provide a variable number of responses (StreamTags-> TagFound and
TimeoutReached).

## Universal Tappy Interface
Regardless of what type of Tappy you are using, they will conform to the same
interface specified in [Tappy.java](tappy/src/main/java/com/taptrack/tcmptappy2/Tappy.java).
In a nutshell, at a minimum, you will have to register a ResponseListener
 using registerResponseListener and a statusListener using registerStatusListener.

 When the statusListener receives a status of `Tappy.STATUS_READY`, the connection
 is active and ready to transmit TCMP commands to the Tappy via `sendMessage()`. In
 order to trigger the connection procedure, you should call `connect()` and make sure
 to `disconnect()` as well as `close()` the Tappy when you are done.

```java
    tappy.registerStatusListener((status) -> {
        if (status == Tappy.STATUS_READY) {
            tappy.sendMessage(new PingCommand());
        }
    });

    tappy.registerResponseListener((response) -> {
        Log.v("TAPPY_EXAMPLE","Received a response");
    });

    tappy.connect();

    // some time later
    tappy.disconnect();
    tappy.close();
```

## TappyBLE Specifics

### Device Discovery
In order to connect to a TappyBLE, you must first search for it in order
to get the relevant `BluetoothDevice` that Android's BLE stack can then
use to connect to it. While you can manually invoke Android's BLE
searching functionality, it is generally recommended to use the `TappyBleScanner`
provided in the SDK. The `TappyBleScanner` will invoke Android's BLE scanning
functionality and call a listener with a `TappyBleDeviceDefinition`
 whenever it finds a Tappy. These device definitions encapsulate all
 the information needed to connect to a Tappy and can be persisted to an
 Android Parcelable using `ParcelableTappyBleDeviceDefinition`. Note that
 the listener will be called each time Android's BLE scanning functionality
 detects the Tappy, so take care to avoid trying to connect to the same Tappy
 twice.

```java
TappyBleScanner scanner = TappyBleScanner.get();

scanner.registerTappyBleFoundListener((tappyBleDefinition)-> {
    Log.v("TAPPY_EXAMPLE","Found a TappyBLE");
});

scanner.startScan();

// some time later
scanner.stopScan();
```

### Instantiating
Once you have a `BluetoothDevice` or `TappyBleDeviceDefinition` that corresponds
to a Tappy, you can instantiate a TappyBle using the static `getTappyBle`
methods:

```java
Tappy tappyOne = TappyBle.get(context, bluetoothDevice);

Tappy tappyTwo = TappyBle.get(context, tappyBluetoothDeviceDefinition);
```

### Permissions
On all versions of Android, in order to connect to a Bluetooth device,
your app will need to hold the `BLUETOOTH` permission as well as
`BLUETOOTH_ADMIN` to search for devices. On versions later than SDK level
23 (Marshmallow), you will additionally need to hold either the
`ACCESS_COARSE_LOCATION` or `ACCESS_FINE_LOCATION` runtime permissions in
order to search for Bluetooth devices. Additionally, while relatively
rare in modern times, it is possible for a device to support Bluetooth
without supporting BLE/Bluetooth Smart, so it may be a good idea to check
for BLE support explicitly.

## TappyUSB Specifics
### Device Discovery
Detecting TappyUSB devices is a somewhat different procedure than detecting
TappyBLE devices. There are two different approaches you can use to get
one of the UsbDevice instances Android uses to represent connected USB
devices.

#### Programmatic Discovery
In order to perform programmatic detection you can use the
`getPotentialTappies(Context)` static method on the TappyUsb class.

```java
List<UsbDevice> devices = TappyUsb.getPotentialTappies(context)
```

Note that, due to limitations of the detection procedure, this may also
return other devices that use the same USB chip that the Tappy does, so
take care if you have multiple USB serial devices connected to your
Android device.

#### Manifest-based Discovery
In addition to programmatically discovering potential TappyUSB devices,
you can register your application as an option for Android to open
when the user plugs in a Tappy. In order to do this, you will need to register
an intent-filter in your application manifest so Android knows to call your
Activity.

Manifest.xml
```xml
<activity
    android:name=".MyActivity"
    android:label="@string/title_activity"
    android:theme="@style/AppTheme">
    <intent-filter>
        <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"/>
    </intent-filter>

    <meta-data
        android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"
        android:resource="@xml/tappy_device_filter"/>
</activity>
```

The `tappy_device_filter` file is included in the `tappyusb` module's resources
so you should not need to create it. When registered in this way, your
Activity can be started with an intent that contains a `UsbDevice` in its
extras bundle referenced by the `UsbManager.EXTRA_DEVICE` key

```java
UsbDevice device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
```

### Permissions
Instead of requiring that the developer register a permission in the manifest
to access USB devices, Android requires that the user grant the app
explicit permission to access specific devices at runtime. If you used
the manifest-based discovery, the user electing to use the device with
your app when prompted by the system automatically grants this permission.

However, if programmatic discovery is used, you must request this
permission before you can interact with a device. You can perform this
procedure manually if you so choose, or you can use the [UsbPermissionDelegate](tappyusb/src/main/java/com/taptrack/tcmptappy2/usb/UsbPermissionDelegate.java)
provided in the `tappyusb` module.

```java
PermissionListener listener = new UsbPermissionDelegate.PermissionListener(){
    public void permissionDenied(UsbDevice device) {
        Log.v("TAPPY_EXAMPLE","Permission was denied");
    }

    public void permissionGranted(UsbDevice device) {
        Log.v("TAPPY_EXAMPLE","Got permission!");
    }
};

UsbPermissionDelegate delegate = UsbPermissionDelegate(context,listener);
delegate.register();

delegate.requestPermission(someUsbDevice);

// don't forget to unregister when done
delegate.unregister();
```

### Instantiating
Once you have your `UsbDevice` and have received permission to interact with
it, simply pass it to the static `getTappyUsb` method along with a context
to get a `Tappy` instance.

```java
Tappy usbTappy = TappyUsb.getTappyUsb(context,device);
```


## TCMP
The messaging protocol used by Tappy devices for commands and responses
is called the Tappy Command Messaging Protocol (TCMP). TCMP defines a
set of independent CommandFamilies that each contain a set of Commands
and Responses.

```groovy
    // The System Family includes commands like getting the battery
    // level for battery-operated Tappies or setting configuration options
    // as well as responses that the Tappy returns if it receives
    // invalid TCMP commands
    implementation "com.taptrack.tcmptappy2:commandfamily-system:$LATEST_VERSION"

    // The BasicNFC family includes operations like scanning for and writing
    // to NFC tags
    implementation "com.taptrack.tcmptappy2:commandfamily-basicnfc:$LATEST_VERSION"
```

Note: The command families are versioned independently from each other
as well as the core SDK, please visit their specific repositories for 
version numbers.

### Resolving Responses
The SDK does not automatically resolve received responses into specific
TCMP responses, instead it simply verifies the that the packet is not
corrupted and passes it to the client for resolution and payload verification.

While it is possible to this manually, it is recommended to use a command
family's [`MessageResolver`](tcmp/src/main/java/com/taptrack/tcmptappy2/MessageResolver.java). If you expect to receive responses from multiple
CommandFamilies, you should use the [`MessageResolverMux`](tcmp/src/main/java/com/taptrack/tcmptappy2/MessageResolverMux.java) from the `tcmp` module to
combine multiple `MessageResolver`s. Since it is always possible to
receive responses from the System command family, you should almost always
be using a `ResolverMux`.

```java
MessageResolver resolver = new MessageResolverMux(
    new BasicNfcCommandResolver(),
    new SystemCommandResolver()
)

try {
    TCMPMessage resolvedResponse = resolver.resolveResponse(someResponse)
    if (resolvedResponse == null) {
        Log.v("TAPPY_EXAMPLE", "Message not supported by this resolver");
    } else if (resolvedResponse instanceof PingResponse) {
        Log.v("TAPPY_EXAMPLE", "Ping response received");
    } else {
        Log.v("TAPPY_EXAMPLE", "Non-ping response received");
    }
} catch (MalformedPayloadException e) {
    Log.e("TAPPY_EXAMPLE", "Payload format was incorrect for response",e);
}
```
