MovieDB with Architecture Components
================================
## About
This app is to browse [the Movie DB catalog](https://www.themoviedb.org) and lets you browse the top 50 movies in popularity descending order, showing the movie poster image where available and inspecting details of the movie.

## Architecture

The App uses the Architecture components, MVVM, LiveData, Rom and a Repository, fetching data from the backend when there is no local data available. Due to the nature of LiveData, the UI will be updated when the corresponding data will also change.

## Software

Android Studio Project.

## Highlights

- Works offline. The App uses a repository to synchronize backend data with a local Room database.

## Next steps

- Save items as favourites and these retained across app restarts, kill or catalog refresh
- Share items. The user will be able to select one or more items and share the selection via other Apps.
- Infinite scroll. The App shall lazy load more items
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