

[![Kotlin-Version][kotlin-image]][kotlin-url]
[![License][license-image]][license-url]
[![Android Studio][android-studio-image]][android-studio-image]
[![Platform][android-image]][android-url]

# Screp
A step-counter with add-ins weather forecast and picture sharing function.

Sensor-based Android Application Development group project at Metropolia UAS - Autumn 2022.

<br  />

### Walking is now getting better and more fun with Screp App

<p  align="row">

Placeholder for demo screen recording
<img  src= ""  width="300"  >

</p>

<p>

### Demo recording with full features

<p> place holder link 

 [![demo recording](http://img.youtube.com/vi/fS2B3cMipnM/0.jpg)](https://youtu.be/fS2B3cMipnM)
  
</p>

## Features

📍 Track user location on map and navigate map to the current location

👣 Record walking session with timer and step count sensor

📈 View graph and summary of walking records

📸 Take photos during your walk

🖼️ View your photos in app gallery grouped by location and sorted by date

📲 Connect to bluetooth and share your photos via different options


**Known bugs**: The file sharing feature via bluetooth is now not working on all devices yet,

### Functional requirements implemented:
 
- [x] Follow material design guidelines recommendation
- [x] Use all of 4 basic components: activity, broadcast receiver, service, content provider
- [x] Use Compose
- [x] Implement Persistence (Room and File and SharedPreferences)
- [x] Implement Connection to [Open weather API](https://api.openweathermap.org/data/2.5/weather?) web service
- [x] Implement Step count sensor
- [x] Implement Bluetooth communication
- [x] Implement Extra "hardware" on addition to sensors (gps, camera,...)
- [x] Implement ViewModel, LiveData
- [x] Implement WorkManager/Worker 
- [x] Implement Other APIs: Google Map, Navigation


## Requirements

- Android with minimum SDK 26

- Android Studio Chipmunk | 2021.2.1 

## Installation

- Make sure you have Android Studio installed with the lowest version of Android Studio Chipmunk | 2021.2.1 

- Clone the project: 

```zsh
git clone https://github.com/GiangNguyen1207/screp.git Screp
cd Screp

```

- [Open the project on Android studio] (https://developer.android.com/studio/projects/create-project#ImportAProject)


## Run project
- on Emulator: [run from Android Studio menu](https://developer.android.com/studio/run/emulator): Not recommended as the app has step count sensor and bluetooth feature, which are not optimized or available on emulator
- on Hardware device: [run from Android Studio](https://developer.android.com/studio/run/device): Android phones with API >= 26 are recommended.

In case of running issue: Navigate to top menu in Xcode: `File > Invalidate Caches`, or clean and rebuild the project.

## Resources

- [Stack Overflow](https://stackoverflow.com/)
- [Medium](https://medium.com/)
- [Android Developer guide](https://developer.android.com/guide)

## Dependencies
- [Google Map API](https://developers.google.com/maps/documentation/android-sdk/overview)
- [Coil](https://coil-kt.github.io/coil/)
- [Retrofit](https://github.com/square/retrofit)
- [Compose Compiler](https://github.com/androidx/androidx/blob/androidx-main/compose/compiler)
- [Compose Runtime](https://github.com/androidx/androidx/blob/androidx-main/compose/runtime)
- [Core](https://github.com/androidx/androidx/blob/androidx-main/core)
- [DataStore](https://github.com/androidx/androidx/blob/androidx-main/datastore)
- [Lifecycle](https://github.com/androidx/androidx/blob/androidx-main/lifecycle)
- [Navigation](https://github.com/androidx/androidx/blob/androidx-main/navigation)
- [Room](https://github.com/androidx/androidx/blob/androidx-main/room)
- [WorkManager](https://github.com/androidx/androidx/blob/androidx-main/work)


## Contributors

[Dieu Vu](https://github.com/dieu-vu)

[Giang Nguyen](https://github.com/GiangNguyen1207)

[Xiaoming Ma](https://github.com/myxmxm)


With the guidance and support from teachers at Metropolia UAS: Jarkko Vuori, Patrick Ausderau and Ulla Sederlöf.

Questions and comments are welcomed.

[kotlin-image]: https://img.shields.io/badge/kotlin-1.7.0-blue
[kotlin-url]: https://kotlinlang.org/
[android-studio-image]: https://img.shields.io/badge/Android%20Studio-Dolphin%20%7C%202021.3.1-orange
[android-image]: https://img.shields.io/badge/-Android-green
[android-url]: https://developer.android.com/
[license-image]: https://img.shields.io/badge/License-MIT-blue.svg
[license-url]: LICENSE
