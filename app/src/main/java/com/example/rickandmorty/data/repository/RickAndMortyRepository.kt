package com.example.rickandmorty.data.repository

import android.util.Log
import com.example.rickandmorty.utils.ApiResult
import com.example.rickandmorty.data.model.Character
import com.example.rickandmorty.data.network.RickAndMortyApiService
import retrofit2.HttpException
import java.io.IOException

/**
 * Repository for accessing Rick and Morty character data.
 */
interface RickAndMortyRepository {

    /**
     * Returns a list of characters.
     *
     * @param loadMore If true, fetch the next page.
     * @return [ApiResult] holding a list of characters, or an error message.
     */
    suspend fun getCharacters(loadMore: Boolean = false): ApiResult<List<Character>>

    /**
     * Returns a single character by ID.
     *
     * @param id The unique identifier of the character.
     * @return [ApiResult] holding a single character, or an error message.
     */
    suspend fun getCharacterById(id: Int): ApiResult<Character>
}

/**
 * Default implementation of [RickAndMortyRepository] that uses [RickAndMortyApiService]
 * to fetch Rick and Morty character data, and caches results in memory.
 *
 * @property apiService Retrofit service used to call the Rick and Morty API.
 */
class DefaultRickAndMortyRepository(
    private val apiService: RickAndMortyApiService
) : RickAndMortyRepository {
    private val tag = "RickAndMortyCharacterRepository"
    private var currentPage: Int = 1
    private var totalPages: Int? = null

    // Map for faster lookup
    private val charactersCache = mutableMapOf<Int, Character>()

    /**
     * Fetches a list of characters from the API or memory cache.
     *
     * It handles pagination, caching, and errors.
     *
     * @param loadMore If true, fetch the next page.
     * @return [ApiResult] holding a list of characters, or error message.
     */
    override suspend fun getCharacters(loadMore: Boolean): ApiResult<List<Character>> {
        // Fetch from network only if needed!
        if (charactersCache.isEmpty() || loadMore) {
            if (currentPage < (totalPages ?: 0)) {
                currentPage++
            }

            try {
                val response = apiService.getCharactersResponse(currentPage)
                totalPages = totalPages ?: response.info.pages
                charactersCache.putAll(
                    response.results.associateBy { character -> character.id }
                )
            } catch (e: IOException) {
                Log.e(tag, e.message, e)
                return ApiResult.Failure("No internet connection")
            } catch (e: HttpException) {
                Log.e(tag, e.message, e)
                return ApiResult.Failure("HTTP error: ${e.code()}")
            } catch (e: Exception) {
                Log.e(tag, e.message, e)
                return ApiResult.Failure("Unexpected error occurred")
            }
        }

        return ApiResult.Success(charactersCache.values.toList())
    }

    /**
     * Fetches a single character by ID from the API or memory cache.
     *
     * It handles caching and errors.
     *
     * @param id The unique identifier of the character.
     * @return [ApiResult] holding a single character, or an error message.
     */
    override suspend fun getCharacterById(id: Int): ApiResult<Character> {
        // Fetch from network only if needed!
        // (In our case, if we can click on a character, their data is already fetched and cached,
        // so we will never fetch from network. But, maybe it can be used for something else...)
        val character = charactersCache.getOrElse(id) {
            try {
                val newCharacter = apiService.getCharacterResponse(id)
                charactersCache.put(id, newCharacter)
                newCharacter
            } catch (e: IOException) {
                Log.e(tag, e.message, e)
                return ApiResult.Failure("No internet connection")
            } catch (e: HttpException) {
                Log.e(tag, e.message, e)
                return ApiResult.Failure("Server error: ${e.code()}")
            } catch (e: Exception) {
                Log.e(tag, e.message, e)
                return ApiResult.Failure("Unexpected error occurred")
            }
        }

        return ApiResult.Success(character)
    }
}
