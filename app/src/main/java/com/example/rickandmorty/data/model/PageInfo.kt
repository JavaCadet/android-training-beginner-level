package com.example.rickandmorty.data.model

import kotlinx.serialization.Serializable

/**
 * Represents the information about pagination.
 *
 * @property count The total count of characters.
 * @property pages The total number of pages.
 * @property next Link to the next page (or null if the current page is the last).
 * @property prev Link to the next page (or null if the current page is the first).
 */
@Serializable
data class PageInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)
