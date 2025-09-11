package com.example.rickandmorty.data.network

import com.example.rickandmorty.data.model.CharactersResponse
import com.example.rickandmorty.data.model.Character
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for accessing the Rick and Morty API endpoints.
 *
 * This service provides methods to retrieve paginated lists of characters and detailed information
 * about a single character.
 */
interface RickAndMortyApiService {

    /**
     * Retrieves a paginated list of characters.
     *
     * @param page The page number to load (default = 1).
     * @return A [CharactersResponse] containing pagination info and a list of characters.
     */
    @GET("character")
    suspend fun getCharactersResponse(@Query("page") page: Int = 1): CharactersResponse

    /**
     * Retrieves detailed information about a single character by ID.
     *
     * @param id The unique identifier of the character.
     * @return A [Character] containing the details about the character.
     */
    @GET("character/{id}")
    suspend fun getCharacterResponse(@Path("id") id: Int): Character
}
