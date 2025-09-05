# Android App Task – Beginner Level

## Subject

Create an Android app that displays a list of Rick and Morty characters from the public API. Selecting a character opens a detail screen with more information.

## Requirements

1. **List Screen**
   - Fetch characters from the Rick & Morty API.
   - Endpoint for list: <https://rickandmortyapi.com/api/character>
   - Display them in a vertical scrollable list.
   - Each item should show at least:
     - Character name
     - Character image (small thumbnail)
   - Show a loading indicator while data is fetched.
   - If the request fails, display an error message and allow retry.
2. **Detail Screen**
   - When a character is tapped, navigate to a new screen.
   - Endpoint for details: <https://rickandmortyapi.com/api/character/{id}>
   - Display:
     - Large character image
     - Name
     - Status (Alive/Dead/Unknown)
     - Species
     - Gender
   - Include a back button to return to the list.
3. **Architecture**
   - Use **MVVM** pattern.
   - Repository layer handles API calls.
   - `ViewModel` manages state (*loading*, *error*, *data*).
   - UI in **Jetpack Compose** subscribes to the ViewModel.
4. **Technology**
   - Jetpack Compose (UI)
   - Retrofit (networking)
   - Coil (image loading)
   - Material 3 components
5. **Constraints**
   - Minimum SDK: 24
   - Do not hardcode character data — it must come from the API!
