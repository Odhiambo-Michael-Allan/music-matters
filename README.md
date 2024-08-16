<h1 align="center">Music Matters</h1>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=24"><img alt="API" src="https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat"/></a>
</p>

Music Matters is a modern and intuitive music player app built using Jetpack Compose. It offers a seamless and engaging listening experience with adaptive navigation and a clean, user-friendly interface. Whether you're shuffling through your favorite playlists or exploring new tracks, Music Matters is designed to make your music experience enjoyable and efficient.

![2](https://github.com/user-attachments/assets/aac4ae8f-4d0d-4a04-8104-15180c35162e)
![3](https://github.com/user-attachments/assets/4056a5e8-e36f-472a-92b9-d1f335d22b3a)

# **Motivation**

The development of Music Matters was driven by a desire to explore and understand the intricacies of media handling on Android. This project served as a learning platform to deepen my knowledge of Jetpack Compose, enabling me to create a fluid and modern user interface. Ultimately, the goal is to leverage this experience to build a robust podcasting app, and Music Matters is a foundational step toward that vision. 

## Tech stack & Open-source libraries
- Minimum SDK level 24.
- [Kotlin](https://kotlinlang.org/) based, utilizing [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) for asynchronous operations.
- Jetpack Libraries:
  - Jetpack Compose: Android’s modern toolkit for declarative UI development.
  - Lifecycle: Observes Android lifecycles and manages UI states upon lifecycle changes.
  - ViewModel: Manages UI-related data and is lifecycle-aware, ensuring data survival through configuration changes.
  - Navigation: Facilitates screen navigation, complemented by [Hilt Navigation Compose](https://developer.android.com/jetpack/compose/libraries#hilt) for dependency injection.
  - Room: Constructs a database with an SQLite abstraction layer for seamless database access.
- Architecture:
  - MVVM Architecture (View - ViewModel - Model): Facilitates separation of concerns and promotes maintainability.
  - Repository Pattern: Acts as a mediator between different data sources and the application's business logic.

- [ksp](https://github.com/google/ksp): Kotlin Symbol Processing API for code generation and analysis.


## **Contributing**

If you'd like to contribute to Project, here are some guidelines:

1. Fork the repository.
2. Create a new branch for your changes.
3. Make your changes.
4. Write tests to cover your changes.
5. Run the tests to ensure they pass.
6. Commit your changes.
7. Push your changes to your forked repository.
8. Submit a pull request.

## **Modularization**

Music Matters adopted modularization strategies below: 
* *Reusability*: Modularizing reusable codes properly enable opportunities for code sharing and limits code accessibility in other modules at the same time.
* *Parallel Building*: Each module can be run in parallel and it reduces the build time.
* *Strict visibility control*: Modules restrict exposing their dedicated from access outside the module.
* *Decentralized focusing*: Each developer team can be assigned their dedicated module.
For more information, check out the [Guide to Android app modularization](https://developer.android.com/topic/modularization) 

## **Find this repository useful?**

Support by joining the [stargazers](https://github.com/Odhiambo-Michael-Allan/music-matters/stargazers) for this repository.
Also, [follow me](https://github.com/Odhiambo-Michael-Allan) on Github for my next creations!

# License
```xml

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

