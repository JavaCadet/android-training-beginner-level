package com.example.rickandmorty.ui.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.data.model.Character
import com.example.rickandmorty.data.repository.RickAndMortyRepository
import com.example.rickandmorty.utils.ApiResult
import com.example.rickandmorty.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A [ViewModel] for the Character Detail Screen.
 *
 * This [ViewModel] is responsible for fetching and managing the state of a single character. It
 * uses the Hilt dependency injection to provide the [RickAndMortyRepository] dependency.
 *
 * @property repository The repository used to fetch character data.
 */
@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val repository: RickAndMortyRepository
) : ViewModel() {

    /**
     * A [MutableStateFlow] that holds the current [UiState] of the character.
     *
     * The UI layer will collect from this StateFlow to react to changes in the data. It is exposed
     * as a private `set` to ensure the state can only be modified from within the ViewModel.
     */
    var uiState = MutableStateFlow<UiState<Character>>(UiState.Loading)
        private set

    /**
     * Fetches a character from the repository by their ID.
     *
     * The data fetch is performed asynchronously in the [viewModelScope]. It updates the [uiState]
     * to reflect the current state of the operation.
     *
     * @param id The unique identifier of the character.
     */
    fun getCharacterById(id: Int) {
        viewModelScope.launch {
            uiState.value = UiState.Loading
            uiState.value = when (val result = repository.getCharacterById(id)) {
                is ApiResult.Success -> UiState.Success(result.data)
                is ApiResult.Failure -> UiState.Failure(result.message)
            }
        }
    }
}