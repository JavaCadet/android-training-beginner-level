package com.example.rickandmorty.data.model

import kotlinx.serialization.Serializable

/**
 * Represents the response object from the Rick and Marty API.
 *
 * @property info The information about pagination.
 * @property results A list of characters returned by the Rick and Morty API.
 */
@Serializable
data class CharactersResponse(
    val info: PageInfo,
    val results: List<Character>
)
