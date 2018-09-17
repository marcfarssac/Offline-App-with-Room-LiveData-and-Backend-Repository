Architecture Components (MVVM, Room and LiveData) App 
================================
## About
This app is to browse [the Movie DB catalog](https://www.themoviedb.org) and lets you check the top 50 movies in popularity descending order, showing the movie poster image where available and inspecting details of the movie in a DetailActivityViewModel using the Movie DB API.


### Youtube video: summary and example

[![Watch the video](https://user-images.githubusercontent.com/18221570/45615307-6c3d6f80-ba6c-11e8-883b-30e738a0df28.png)](https://www.youtube.com/watch?v=CNqnQtc2vDA&feature=youtu.be)

### Screenshoots

| Most popular movie and list       | Movie detail                    |
|-----------|-----------|
|<img src="https://user-images.githubusercontent.com/18221570/45496847-a3630680-b776-11e8-9726-d843acf45b2c.png" width=500></img> | <img src="https://user-images.githubusercontent.com/18221570/45496848-a3fb9d00-b776-11e8-8ea1-9c181afcb2bc.png" width=500></img>|


## Architecture

The App uses the Architecture components, MVVM, LiveData, Room and a Repository, fetching data from the backend when there is no local data available. Due to the nature of LiveData, the UI will be updated when the corresponding data will also change. 
Currently implementing a Firebase Realtime Database in the backend that when updated triggers an event that thanks to the LiveData objects refreshes the UI for all clients in Real time.  

## Software

Android Studio.
Firebase Realtime Database.

## Libraries used

- Patterns and frameworks
    - MVVM (Model-View-ViewModel) using Google's new Architecture Components viewModel, viewModel, LiveData, LifecycleObserver
    - Clean Architecture with viewModel interacting with UseCases and the latter interacting with local database.
    
- Database
    - Room Persistance Library, part of Google Architecture Components    

- Currently adding latest JetPack software components.

## Highlights

- Works offline. The App uses a repository to synchronize backend data with a local Room database.

## Next steps

- Save items as favourites and these retained across app restarts, kill or catalog refresh
- Share items. The user will be able to select one or more items and share the selection via other Apps.
- Infinite scroll. The App shall lazy load more items (Paging)
- Add more filtering options, not only popularity, such as Year, Genre, Keywords.
- Transition. Ability to provide smooth transitions between screens and states.

## License

Copyright 2017 Google Inc. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
