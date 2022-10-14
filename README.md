

[![Kotlin-Version][kotlin-image]][kotlin-url]
[![License][license-image]][license-url]
[![Android Studio][android-studio-image]][android-studio-image]
[![Platform][android-image]][android-url]

# Screp
A step-counter with add-ins weather forecast and picture sharing function.

Sensor-based Android Application Development group project at Metropolia UAS - Autumn 2022.

<br  />
<p  align="center">

<img src="https://user-images.githubusercontent.com/51530194/195667250-34f6943e-e8d4-480e-ab26-615a713907ab.png" width=200 />
</p>

### Have a walk, enjoy the weather and capture memorable moments with Screp App
Screenshots from the app
<p  align="row">
<img  src= "https://user-images.githubusercontent.com/51530194/195669141-7874f98c-74b0-49cf-acb3-6cc9f15e6a4d.png"  width="200" />
<img width="200" alt="ảnh" src="https://user-images.githubusercontent.com/51530194/195669421-4e1b9adf-1686-4f89-b258-514d23cfa343.png">
<img width="200" alt="ảnh" src="https://user-images.githubusercontent.com/51530194/195672388-d297f060-4cd4-4c81-a992-4aac45562d57.png">
<img width="200" alt="ảnh" src="https://user-images.githubusercontent.com/51530194/195669529-730a72ed-703f-412e-8ed3-78bb6235879e.png">
<img width="200" alt="ảnh" src="https://user-images.githubusercontent.com/51530194/195669716-56d81932-8ef7-4bfe-9a8d-5815f23806c5.png">
<img width="200" alt="ảnh" src="https://user-images.githubusercontent.com/51530194/195669815-3157f66d-05ea-46eb-99eb-fd73eb7a9e26.png">
<img width="200" alt="ảnh" src="https://user-images.githubusercontent.com/51530194/195805224-93deb477-e832-4132-9e67-3477a1eb23c8.png">
<img width="200" src="https://user-images.githubusercontent.com/51530194/195670873-e19f7794-7ad7-4d4b-b35f-b4f3c9517945.jpg">


</p>

<p>

### Demo recording with full features

Click to play video

<p>

 [![demo recording](http://img.youtube.com/vi/MdR7DuJ_PTc/0.jpg)](https://youtu.be/MdR7DuJ_PTc)
 

## Features

📍 Track user location on map and navigate map to the current location

🚶 Record walking session with timer and step count sensor
 
👣 See your route track on the map 

📈 View graph and summary of walking records

⛅ Have a weather forecast service ready for you

🔔 Subscribe to weather notification service at a chosen time

📸 Take photos during your walk and have them pinned on the map

🖼️ View your photos in app gallery grouped by location

📲 Connect to bluetooth and share your photos via different options


**Known bugs**: The file sharing feature via bluetooth is now not working on all devices yet,

### Functional requirements implemented:
 
- [x] Follow material design guidelines recommendation
- [x] Use all of the 4 required basic components: activity, broadcast receiver, service, content provider
- [x] Use Compose
- [x] Implement Persistence (Room and File and Data Store)
- [x] Implement Connection to [Open weather API](https://api.openweathermap.org/data/2.5/weather?) web service
- [x] Implement Step count sensor
- [x] Implement Bluetooth communication: using classic Bluetooth to find and pair smart phone devices, establish connection to transfer file
- [x] Implement Extra "hardware" on addition to sensors (gps, camera,...)
- [x] Implement ViewModel, LiveData
- [x] Implement WorkManager/Worker 
- [x] Implement Other APIs: Google Map, Navigation
- [x] Use coroutines and threads


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

- [Open the project on Android studio](https://developer.android.com/studio/projects/create-project#ImportAProject)


## Run project
- on Emulator: [run from Android Studio menu](https://developer.android.com/studio/run/emulator): Not recommended as the app has step count sensor and bluetooth feature, which are not optimized or available on emulator
- on Hardware device: [run from Android Studio](https://developer.android.com/studio/run/device): Android phones with API >= 26 are recommended.

In case of running issue: Navigate to top menu in Android Studio: `File > Invalidate Caches`, or clean and rebuild the project.

## Resources

- [Stack Overflow](https://stackoverflow.com/)
- [Medium](https://medium.com/)
- [Android Developer guide](https://developer.android.com/guide)
- [Youtube](https://www.youtube.com/)

## Dependencies
- [Google Map API](https://developers.google.com/maps/documentation/android-sdk/overview)
- [Coil](https://coil-kt.github.io/coil/)
- [Retrofit](https://github.com/square/retrofit)
- [Moshi](https://github.com/square/moshi)
- [Compose Compiler](https://github.com/androidx/androidx/blob/androidx-main/compose/compiler)
- [Compose Runtime](https://github.com/androidx/androidx/blob/androidx-main/compose/runtime)
- [Core](https://github.com/androidx/androidx/blob/androidx-main/core)
- [DataStore](https://github.com/androidx/androidx/blob/androidx-main/datastore)
- [Lifecycle](https://github.com/androidx/androidx/blob/androidx-main/lifecycle)
- [Navigation](https://github.com/androidx/androidx/blob/androidx-main/navigation)
- [Room](https://github.com/androidx/androidx/blob/androidx-main/room)
- [WorkManager](https://github.com/androidx/androidx/blob/androidx-main/work)
- [Material Icons](https://fonts.google.com/icons?icon.set=Material+Icons)


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
