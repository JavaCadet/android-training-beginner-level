package com.example.rickandmorty.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the life status of a character in the Rick and Morty universe.
 *
 * - `ALIVE` - The character is confirmed to be alive.
 * - `DEAD` - The character is confirmed to be dead.
 * - `UNKNOWN` - The character's status has not been specified or is unknown.
 *
 * @property value The value that will be shown to the app's user.
 */
@Serializable
enum class CharacterStatus(val value: String) {
    @SerialName("Alive") ALIVE("Alive"),
    @SerialName("Dead") DEAD("Dead"),
    @SerialName("unknown") UNKNOWN("Unknown")
}

/**
 * Represents the gender of a character in the Rick and Morty universe.
 *
 * - `FEMALE`
 * - `MALE`
 * - `GENDERLESS`
 * - `UNKNOWN`
 *
 */
@Serializable
enum class CharacterGender(val value: String) {
    @SerialName("Female") FEMALE("Female"),
    @SerialName("Male") MALE("Male"),
    @SerialName("Genderless") GENDERLESS("Genderless"),
    @SerialName("unknown") UNKNOWN("Unknown")
}

/**
 * Represents a character in the Rick and Morty universe.
 *
 * @property id The unique identifier of the character.
 * @property name The name of the character.
 * @property status The life status of the character. See [CharacterStatus].
 * @property species The species of the character.
 * @property type The type or subspecies of the character.
 * @property gender The gender of the character.
 * @property origin The origin location of the character.
 * @property location The last known location of the character.
 * @property image Link to the character's image.
 */
@Serializable
data class Character(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val type: String,
    val gender: CharacterGender,
    val origin: Location,
    val location: Location,
    val image: String
)
