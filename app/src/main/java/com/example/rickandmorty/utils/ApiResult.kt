package com.example.rickandmorty.utils

/**
 * Represents the result of an API call.
 *
 * This class ensures that a function's return value is always either a successful result containing
 * data or a failure result containing an error message.
 *
 * @param T The type of data returned on success.
 */
sealed class ApiResult<out T> {

    /**
     * Represents a successful API call.
     *
     * @param T The type of the data.
     * @property data The data returned from the API call.
     */
    data class Success<T>(val data: T) : ApiResult<T>()

    /**
     * Represents a failed API call.
     *
     * @property message An optional error message explaining the reason for the failure.
     */
    data class Failure(val message: String?) : ApiResult<Nothing>()
}
