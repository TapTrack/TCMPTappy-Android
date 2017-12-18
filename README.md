# Taptrack TCMP Tappy SDK
This project provides an SDK for interfacing with a TapTrack Tappy NFC readers. The 'app' module contains the Tappy NFC Reader demo app found at
https://play.google.com/store/apps/details?id=com.taptrack.bletappyexample

This version replaces the previous TappyBLE SDK and provides a simplified API
as well as the ability to use TappyUSB readers with Android devices that
can act as a USB Host.

## Common Gradle Dependencies
```groovy
// TappyBLE readers
compile 'com.taptrack.tcmptappy2:tappyble:2.0.0-beta3'
// TappyUSB readers
compile 'com.taptrack.tcmptappy2:tappyusb:2.0.0-beta3'


// For working with raw TCMP messages
compile 'com.taptrack.tcmptappy2:tcmp:2.0.0-beta3'
// For converting between TCMP messages based on the previous SDK
compile 'com.taptrack.tcmptappy2:tcmpconverter:2.0.0-beta3'
```
