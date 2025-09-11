package com.example.rickandmorty.utils

/**
 * Represents the various UI states.
 *
 * This class ensures that a UI component can handle all possible states (loading, success, and
 * failure).
 *
 * @param T The type of the data returned on success.
 */
sealed class UiState<out T> {

    /**
     * Represents the loading state, typically used when fetching data is in progress.
     */
    object Loading : UiState<Nothing>()

    /**
     * Represents a successful state, holding the data.
     *
     * @param T The type of the data.
     * @property data The data returned upon a successful operation.
     */
    data class Success<out T>(val data: T) : UiState<T>()

    /**
     * Represents a failed state, indicating that an error has occurred.
     *
     * @property message An optional error message explaining the reason for the failure.
     */
    data class Failure(val message: String?) : UiState<Nothing>()
}
