package com.example.rickandmorty.data.model

import kotlinx.serialization.Serializable

/**
 * Represents the character's origin or last-known location.
 *
 * @property name The name of the location.
 * @property url Link to the location's endpoint.
 */
@Serializable
data class Location(
    val name: String,
    val url: String
)
